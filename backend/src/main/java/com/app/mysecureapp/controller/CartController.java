package com.app.mysecureapp.controller;

import com.app.mysecureapp.dto.Response.CartGroupResponse;
import com.app.mysecureapp.service.CartService;
import com.app.mysecureapp.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 【購物車控制器 (Cart Controller)】
 * 🚀 職責：處理購物車的增刪改查，支援按賣家分組的數據結構。
 * 技術亮點：RESTful API 設計、身分上下文自動識別、分組數據導航。
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * 【GET：獲取我的購物車】
     * 🚀 亮點：回傳的是已按賣家分組的 List<CartGroupResponse>
     */
    @GetMapping
    public ResponseEntity<List<CartGroupResponse>> getMyCart() {
        UUID userUuid = SecurityUtil.getCurrentUserUuid(); // 🛡️ 安全抓取目前使用者
        return ResponseEntity.ok(cartService.getGroupedCart(userUuid));
    }

    /**
     * 【POST：加入購物車】
     * @param productExternalId 商品的 UUID
     * @param quantity 欲加入的數量
     */
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @RequestParam UUID productExternalId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        UUID userUuid = SecurityUtil.getCurrentUserUuid();
        cartService.addToCart(userUuid, productExternalId, quantity);
        return ResponseEntity.ok("已成功加入購物車");
    }

    /**
     * 【PATCH：更新購物車數量】
     * 🚀 用途：處理 UI 上的「+」與「-」按鈕點擊
     * @param cartItemId 購物車項目的資料庫 ID
     */
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<Void> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        cartService.updateQuantity(cartItemId, quantity);
        return ResponseEntity.ok().build();
    }

    /**
     * 【DELETE：移除購物車項目】
     * 🚀 用途：點擊垃圾桶圖示
     */
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.ok().build();
    }
}