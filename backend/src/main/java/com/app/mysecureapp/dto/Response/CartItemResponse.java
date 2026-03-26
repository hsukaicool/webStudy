package com.app.mysecureapp.dto.Response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 🚀 單個商品項的數據：提供 UI 渲染所需的所有細節
 */
public record CartItemResponse(
        Long cartItemId,         // 購物車項目的 ID (刪除或修改數量用)
        UUID productExternalId,  // 商品的 UUID (導向詳情頁用)
        String productName,      // 商品名稱
        String imageUrl,         // 商品圖片
        String condition,        // 商品狀況 (如：9成新)
        BigDecimal price,        // 目前售價
        Integer quantity         // 勾選數量
) {}