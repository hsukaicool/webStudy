package com.app.mysecureapp.controller;

import com.app.mysecureapp.dto.Response.ProductCardResponse;
import com.app.mysecureapp.dto.Response.ProductResponse;
import com.app.mysecureapp.service.ProductCardService;
import org.springframework.data.domain.Page; // 🚀 分頁核心類別：封裝了數據列表與分頁元數據
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 【公共商品展示控制器 (Public Product Display Controller)】
 * 🚀 職責說明：
 * 1. 專門處理「非授權相關」的商品查詢請求，主要供首頁、搜尋頁面使用。
 * 2. 實作「讀取分離」策略，將買家瀏覽與賣家管理的控制器解耦。
 */
@RestController
@RequestMapping("/api/public/products") // 🚀 路由規範：/public 路徑在 SecurityConfig 中已設定為 permitAll()
public class ProductCardController {

    // 注入專門處理「展示型邏輯」的 Service
    private final ProductCardService productCardService;

    /**
     * 建構子注入 (Constructor Injection)
     * 🚀 優點：保證 Bean 的不可變性，且利於進行單元測試 (Unit Testing)。
     */
    public ProductCardController(ProductCardService productCardService) {
        this.productCardService = productCardService;
    }

    /**
     * 【GET：分頁獲取首頁商品列表】
     * 🚀 技術亮點：伺服器端分頁 (Server-side Pagination)
     * * @param page 目前欲查看的頁碼。defaultValue = "0" 代表從第一頁開始。
     * @param size 每頁顯示的筆數。defaultValue = "10" 限制單次傳輸量，避免大數據造成網路阻塞。
     * @return ResponseEntity 封裝的 Page 物件。
     * * 🚀 前端連動：React 可透過路徑 /api/public/products/list?page=0&size=10 進行動態加載。
     */
    @GetMapping("/list")
    public ResponseEntity<Page<ProductCardResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 呼叫 Service 層執行「分頁 + JOIN FETCH」優化後的查詢邏輯
        Page<ProductCardResponse> productCards = productCardService.getPublicProductCards(page, size);

        // 回傳 200 OK 狀態碼及封裝好的分頁數據
        return ResponseEntity.ok(productCards);
    }

    /**
     * 【GET：獲取單一商品詳細資料】
     * @param externalId 商品的 UUID
     * @return 該商品的完整公開資訊
     */
    @GetMapping("/{externalId}")
    public ResponseEntity<ProductResponse> getProductDetail(@PathVariable java.util.UUID externalId) {
        ProductResponse response = productCardService.getPublicProductDetail(externalId);
        return ResponseEntity.ok(response);
    }
}