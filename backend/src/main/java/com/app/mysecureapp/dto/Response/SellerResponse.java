package com.app.mysecureapp.dto.Response;

import java.time.LocalDateTime;

public record SellerResponse(
        String shopName,
        String shopDescription,
        String avatarUrl,
        String bannerUrl,
        String taxId,
        String status,
        String servicePhone,
        LocalDateTime createdAt
) {}