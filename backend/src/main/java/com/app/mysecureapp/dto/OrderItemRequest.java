package com.app.mysecureapp.dto;

import java.util.UUID;

public record OrderItemRequest(
        UUID productExternalId, // 商品的對外 ID
        Integer quantity        // 購買數量
) {}