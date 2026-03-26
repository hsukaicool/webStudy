package com.app.mysecureapp.dto.Response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID productExternalId,
        String productName,
        String imageUrl,
        String sellerName,
        Integer quantity,
        BigDecimal priceAtPurchase
) {}
