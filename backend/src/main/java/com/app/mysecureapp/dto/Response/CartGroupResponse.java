package com.app.mysecureapp.dto.Response;

import java.util.List;

// 一個賣家的包裹
public record CartGroupResponse(
        String sellerName,
        List<CartItemResponse> items
) {}