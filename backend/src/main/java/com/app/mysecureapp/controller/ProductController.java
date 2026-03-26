package com.app.mysecureapp.controller;

// === 1. 資料傳輸物件 (DTO) 引入 ===
import com.app.mysecureapp.dto.ProductRequest;
import com.app.mysecureapp.dto.Response.ProductResponse;

// === 2. 業務邏輯與工具類 引入 ===
import com.app.mysecureapp.service.ProductService;
import com.app.mysecureapp.util.SecurityUtil;

// === 3. Spring Framework 核心標註 ===
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;

// === 4. Java 標準庫 ===
import java.io.IOException;

import java.util.List;
import java.util.UUID;

/**
 * 【商品控制器 (Product Controller)】
 * 🚀 職責：作為商品模組的對外接口，遵循 RESTful API 設計規範。
 * * 技術亮點：
 * 1. 端點隔離：區分「公共瀏覽」與「賣家操作」權限。
 * 2. 身分自動解碼：透過 SecurityUtil 直接從 JWT 提取操作者身分，無需前端傳入 UserID。
 * 3. 數據一致性：確保所有操作均返回最新生成的 ProductResponse，便於前端 UI 動態更新。
 */
@RestController
@RequestMapping("/api/products") // 🚀 統一路由前綴：所有商品相關 API 皆以此開頭
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 【POST：新增/上架商品】
     * 🚀 邏輯：解析 Token -> 提取 UUID -> 呼叫 Service 執行上架 -> 回傳 200 OK + 商品資料
     *
     * @param request 接收前端 AddProduct.jsx 傳來的 JSON 表單資料
     * @return 封裝後的商品回應物件 (包含 UUID 與 上架時間)
     */
    @PostMapping(value = "/add",
            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> addProduct(
            @RequestPart ProductRequest request,
            @RequestPart(value = "image", required = false)
            MultipartFile image) throws java.io.IOException {

        // 1. 🚀 安全守衛：從 SecurityContext (JWT) 中解析出當前登入者的 UUID
        // 理由：這能防止惡意使用者竄改請求中的 UserID，確保資料歸屬於正確的賣家。
        UUID userUuid = SecurityUtil.getCurrentUserUuid();

        // 2. 執行業務邏輯：交由 Service 處理資料庫儲存與賣家權限檢查
        ProductResponse response = productService.createProduct(userUuid, request, image);

        // 3. 回傳標準 HTTP 200 響應與處理結果
        return ResponseEntity.ok(response);
    }

    /**
     * 【GET：獲取當前登入賣家的商品清單】
     * 🚀 適用場景：賣家後台的「商品管理」頁面渲染
     * 🚀 技術亮點：身分透傳 (Identity Pass-through)
     *
     * @return 該賣家名下的所有商品 DTO 清單
     */
    @GetMapping("/my-products")
    public ResponseEntity<List<ProductResponse>> getMyProducts() {

        // 1. 🚀 自動身分識別：前端無需在 URL 傳入 sellerId，直接從 JWT 獲取
        // 專業理由：這能有效防止「越權查詢 (ID Traversal)」，提升 API 安全性。
        UUID userUuid = SecurityUtil.getCurrentUserUuid();

        // 2. 查詢該賣家的所有商品（已在 Service 層完成 Entity 轉 DTO 的 Stream 處理）
        List<ProductResponse> products = productService.getSellerProductsByUuid(userUuid);

        // 3. 回傳清單與 200 OK 狀態碼
        return ResponseEntity.ok(products);
    }

    /**
     * 【PUT：更新商品】
     */
    @PutMapping(value = "/{externalId}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID externalId,
            @RequestPart("product") ProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        UUID userUuid = SecurityUtil.getCurrentUserUuid();
        ProductResponse updated = productService.updateProduct(userUuid, externalId, request, image);
        return ResponseEntity.ok(updated);
    }

    /**
     * 【PATCH：更新商品狀態】
     * 🚀 邏輯：支援賣家在列表中一鍵切換商品狀態
     */
    @PatchMapping("/{externalId}/status")
    public ResponseEntity<ProductResponse> updateProductStatus(
            @PathVariable UUID externalId,
            @RequestParam com.app.mysecureapp.model.Enum.ProductStatus status) {

        UUID userUuid = SecurityUtil.getCurrentUserUuid();
        ProductResponse updated = productService.updateProductStatus(userUuid, externalId, status);
        return ResponseEntity.ok(updated);
    }

    /**
     * 【DELETE：刪除商品】
     */
    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID externalId) {
        UUID userUuid = SecurityUtil.getCurrentUserUuid();
        productService.deleteProduct(userUuid, externalId);
        return ResponseEntity.noContent().build(); // 204 No Content 代表成功且無回傳內容
    }
}