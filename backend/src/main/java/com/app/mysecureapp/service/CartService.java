package com.app.mysecureapp.service;

import com.app.mysecureapp.dto.Response.CartGroupResponse;
import com.app.mysecureapp.dto.Response.CartItemResponse;
import com.app.mysecureapp.model.CartItem;
import com.app.mysecureapp.model.Product;
import com.app.mysecureapp.model.User;
import com.app.mysecureapp.repository.CartItemRepository;
import com.app.mysecureapp.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartItemRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserQueryService userQueryService;

    public CartService(CartItemRepository cartRepository,
                       ProductRepository productRepository,
                       UserQueryService userQueryService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userQueryService = userQueryService;
    }

    /**
     * 【獲取分組後的購物車】
     * 🚀 核心技術：Java Stream API GroupingBy
     * 作用：把 List<CartItem> 轉換成符合 UI 結構的 List<CartGroupResponse>
     */
    @Transactional(readOnly = true)
    public List<CartGroupResponse> getGroupedCart(UUID userUuid) {
        User user = userQueryService.findUserByUuid(userUuid);
        List<CartItem> allItems = cartRepository.findByUser(user);

        // 1. 按照「賣家店名」進行分組
        return allItems.stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getSeller().getShopName()))
                .entrySet().stream()
                .map(entry -> new CartGroupResponse(
                        entry.getKey(), // 這裡就是 sellerName
                        entry.getValue().stream()
                                .map(this::convertToItemResponse) // 2. 轉換內部商品為 DTO
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    /**
     * 【加入購物車】
     * 🚀 邏輯：如果已存在則增加數量，不存在則新建
     */
    @Transactional
    public void addToCart(UUID userUuid, UUID productExternalId, Integer quantity) {
        // 1. 找人
        User user = userQueryService.findUserByUuid(userUuid);

        // 2. 找貨
        Product product = productRepository.findByExternalId(productExternalId)
                .orElseThrow(() -> new RuntimeException("找不到商品：" + productExternalId));

        // 3. 判斷是要「更新數量」還是「新增項目」
        CartItem cartItem = cartRepository.findByUserAndProduct(user, product)
                .map(item -> {
                    item.setQuantity(item.getQuantity() + quantity);
                    return item;
                })
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setUser(user);
                    newItem.setProduct(product);
                    newItem.setQuantity(quantity);
                    return newItem;
                });

        cartRepository.save(cartItem);
    }

    /**
     * 【更新商品數量】
     * 🚀 用途：對應 UI 上的 + 與 - 按鈕
     */
    @Transactional
    public void updateQuantity(Long cartItemId, Integer newQuantity) {
        if (newQuantity <= 0) {
            cartRepository.deleteById(cartItemId);
            return;
        }
        CartItem item = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("找不到該購物車項目"));
        item.setQuantity(newQuantity);
        cartRepository.save(item);
    }

    /**
     * 【刪除購物車項目】
     * 🚀 用途：對應 UI 上的垃圾桶圖示
     */
    @Transactional
    public void removeFromCart(Long cartItemId) {
        cartRepository.deleteById(cartItemId);
    }

    // 輔助方法：將實體轉成 DTO
    private CartItemResponse convertToItemResponse(CartItem item) {
        Product p = item.getProduct();
        return new CartItemResponse(
                item.getId(),
                p.getExternalId(),
                p.getName(),
                p.getImageUrl(),
                p.getProductCondition().toString(), // 抓取商品狀況 Enum
                p.getPrice(),
                item.getQuantity()
        );
    }
}