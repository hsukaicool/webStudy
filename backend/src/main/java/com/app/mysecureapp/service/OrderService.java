package com.app.mysecureapp.service;

// === 1. Spring 核心與事務管理 ===
import com.app.mysecureapp.dto.Response.OrderItemResponse;
import com.app.mysecureapp.dto.Response.OrderResponse;
import com.app.mysecureapp.dto.Response.SellerSaleResponse;
import com.app.mysecureapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// === 2. 模型與資料夾引入 ===
import com.app.mysecureapp.model.Order;
import com.app.mysecureapp.model.OrderItem;
import com.app.mysecureapp.model.Product;
import com.app.mysecureapp.model.User;
import com.app.mysecureapp.model.SellerProfile;
import com.app.mysecureapp.model.Enum.OrderStatus;

// === 3. 倉儲層 (Repository) ===

// === 4. DTO 與工具 ===
import com.app.mysecureapp.dto.OrderRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 【訂單業務邏輯層 (Order Business Logic)】
 * 🚀 職責：處理複雜的下單流程、庫存扣減、以及買賣雙方的訂單查詢。
 * 技術亮點：實作原子性事務、價格快照、以及非對稱數據檢索。
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserQueryService userQueryService;
    private final SellerProfileRepository sellerRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            UserQueryService userQueryService,
            SellerProfileRepository sellerRepository,
            CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userQueryService = userQueryService;
        this.sellerRepository = sellerRepository;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * 【創建新訂單 (下單流程)】
     * 🚀 核心機制：@Transactional 確保原子性。
     * 若過程中庫存不足或任何一步失敗，資料庫會自動回滾，不會產生「幽靈訂單」。
     */
    @Transactional
    public Order createOrder(UUID buyerUuid, OrderRequest request) {
        // 1. 獲取買家實體
        User buyer = userQueryService.findUserByUuid(buyerUuid);

        // 2. 初始化訂單主檔
        Order order = new Order();
        order.setBuyer(buyer);
        order.setStatus(OrderStatus.PENDING); // 預設為待處理

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 3. 處理每一個購買項目
        for (var itemReq : request.items()) {
            // A. 尋找商品 (使用 External ID 確保安全性)
            Product product = productRepository.findByExternalId(itemReq.productExternalId())
                    .orElseThrow(() -> new RuntimeException("找不到商品：" + itemReq.productExternalId()));

            // B. 簡單庫存檢查 (進階可在此實作扣庫存邏輯)
            if (product.getStock() < itemReq.quantity()) {
                throw new RuntimeException("商品 " + product.getName() + " 庫存不足");
            }

            // C. 建立訂單明細並實作「價格快照 (Price Snapshot)」
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setSeller(product.getSeller()); // 🚀 自動連結賣家，方便賣家後續查詢
            item.setQuantity(itemReq.quantity());
            item.setPriceAtPurchase(product.getPrice()); // 鎖定下單時的價格

            // D. 累加總金額
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(itemReq.quantity()));
            totalAmount = totalAmount.add(itemTotal);

            orderItems.add(item);
        }

        // 4. 完成訂單主檔封裝
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // 5. 持久化至資料庫
        Order savedOrder = orderRepository.save(order);
        // 6. 🚀 下單成功後，自動清除購物車中已結帳的商品
        for (var itemReq : request.items()) {
            Product product = productRepository.findByExternalId(itemReq.productExternalId())
                    .orElse(null);
            if (product != null) {
                cartItemRepository.findByUserAndProduct(buyer, product)
                        .ifPresent(cartItemRepository::delete);
            }
        }
        return savedOrder;
    }

    /**
     * 【買家查詢：我的訂單列表】
     * 🚀 亮點：將資料庫 Entity 轉換為前端專用的 OrderResponse DTO
     */
    @Transactional(readOnly = true) // 🚀 加這行！讓 Hibernate Session 保持開啟直到方法結束
    public List<OrderResponse> getBuyerOrders(UUID buyerUuid) {
        // 1. 先找到買家實體
        User buyer = userQueryService.findUserByUuid(buyerUuid);

        // 2. 從 Repository 撈出原始訂單清單 (List<Order>)
        List<Order> orders = orderRepository.findByBuyerOrderByCreatedAtDesc(buyer);

        // 3. 執行流式轉換 (Stream Mapping)
        return orders.stream().map(order -> new OrderResponse(
                order.getExternalId(),
                order.getBuyer() != null ? order.getBuyer().getDisplayName() : "未知用戶",
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                // 🚀 嵌套轉換明細項目
                order.getItems().stream().map(item -> new OrderItemResponse(
                        item.getProduct().getExternalId(),
                        item.getProduct().getName(),
                        item.getProduct().getImageUrl(),
                        item.getSeller() != null && item.getSeller().getUser() != null
                                ? item.getSeller().getUser().getDisplayName()
                                : "未知賣家",
                        item.getQuantity(),
                        item.getPriceAtPurchase())).toList()))
                .toList();
    }

    /**
     * 【賣家查詢：我的銷售明細 DTO 版】
     * 🚀 亮點：將資料庫明細轉換為賣家專用的 SellerSaleResponse，避免 LazyLoading 報錯。
     */
    @Transactional(readOnly = true) // 🚀 關鍵：確保在轉換過程中的 Lazy Loading 不會斷線
    public List<SellerSaleResponse> getSellerSaleResponses(UUID userUuid) {
        // A. 透過 UUID 找到賣家實體
        User user = userQueryService.findUserByUuid(userUuid);
        SellerProfile seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("您尚未開通賣家功能"));

        // B. 撈出該賣家的所有訂單明細
        List<OrderItem> items = orderItemRepository.findBySellerOrderByOrderCreatedAtDesc(seller);

        // C. 執行 DTO 轉換邏輯
        return items.stream()
                .filter(item -> item.getOrder() != null && item.getProduct() != null)
                // 🛡️ 過濾掉孤兒明細資料
                .map(item -> new SellerSaleResponse(
                        item.getOrder().getExternalId(), // 訂單編號
                        item.getOrder().getBuyer() != null ? item.getOrder().getBuyer().getDisplayName() : "未知買家", // 買家是誰
                        item.getProduct().getExternalId(), // 商品 ID
                        item.getProduct().getName(), // 商品名稱
                        item.getProduct().getImageUrl(), // 商品圖片
                        item.getQuantity(), // 賣出數量
                        item.getPriceAtPurchase(), // 成交單價
                        item.getOrder().getStatus().name(), // 訂單狀態
                        item.getOrder().getCreatedAt() // 下單時間
                )).toList();
    }
}