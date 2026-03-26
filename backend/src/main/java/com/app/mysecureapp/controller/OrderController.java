package com.app.mysecureapp.controller;

import com.app.mysecureapp.dto.OrderRequest;
import com.app.mysecureapp.dto.Response.OrderResponse;
import com.app.mysecureapp.model.Order;
import com.app.mysecureapp.model.OrderItem;
import com.app.mysecureapp.service.OrderService;
import com.app.mysecureapp.util.SecurityUtil; // 🚀 記得確認你的 SecurityUtil 位置
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.mysecureapp.dto.Response.SellerSaleResponse;
import java.util.List;
import java.util.UUID;

/**
 * 【交易核心控制器 (Transaction Controller)】
 * 🚀 職責：處理訂單建立、買家訂單追蹤、賣家業績查詢。
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // 🚀 正確的建構子，解決 'orderService' might not have been initialized 的問題
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 【POST：創建訂單】
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        UUID buyerUuid = SecurityUtil.getCurrentUserUuid();
        return ResponseEntity.ok(orderService.createOrder(buyerUuid, request));
    }

    /**
     * 【GET：買家查看我的訂單】
     */
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        UUID buyerUuid = SecurityUtil.getCurrentUserUuid();
        return ResponseEntity.ok(orderService.getBuyerOrders(buyerUuid));
    }

    /**
     * 【GET：賣家查看銷售明細】
     */
    @GetMapping("/my-sales")
    public ResponseEntity<List<SellerSaleResponse>> getMySales() {
        UUID sellerUuid = SecurityUtil.getCurrentUserUuid();
        return ResponseEntity.ok(orderService.getSellerSaleResponses(sellerUuid));
    }
}