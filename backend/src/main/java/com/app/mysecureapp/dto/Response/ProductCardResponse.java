package com.app.mysecureapp.dto.Response;

import com.app.mysecureapp.model.Enum.ProductCondition;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * 商品卡片回應物件 (Product Card Response DTO)
 * 🚀 用途：專用於首頁、搜尋列表等「高頻率、大批量」顯示場景。
 */
public record ProductCardResponse(
        UUID externalId,      // 用於跳轉詳情頁的 Key
        String name,          // 標題
        BigDecimal price,     // 價格
        String imageUrl,      // 縮圖
        ProductCondition condition // 商品狀況 (如：全新/二手)
) {}