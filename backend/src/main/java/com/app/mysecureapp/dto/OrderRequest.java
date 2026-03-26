package com.app.mysecureapp.dto;

import java.util.List;

// 🚀 建立這個 Record，Service 裡的 request.items() 才抓得到資料
public record OrderRequest(
        List<OrderItemRequest> items
) {}