package com.app.mysecureapp.dto.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SellerSaleResponse(
        UUID orderExternalId,       // 訂單編號（讓賣家追蹤）
        String buyerName,           // 買家名稱
        UUID productExternalId,     // 商品 UUID
        String productName,         // 商品名稱
        String imageUrl,            // 商品圖片
        Integer quantity,           // 購買數量
        BigDecimal priceAtPurchase, // 成交價格
        String orderStatus,         // 訂單狀態
        LocalDateTime createdAt     // 下單時間
) {}
