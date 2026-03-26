package com.app.mysecureapp.controller;

// === 1. 專案內部組件 (DTO, Model, Service, Utils) ===
import com.app.mysecureapp.dto.SellerRequest;
import com.app.mysecureapp.dto.Response.SellerResponse;
import com.app.mysecureapp.model.SellerProfile;
import com.app.mysecureapp.service.SellerService;
import com.app.mysecureapp.util.SecurityUtil;

// === 2. Spring 框架核心註解與回應處理 ===
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// === 3. Java 標準函式庫 ===
import java.util.UUID;
import org.springframework.http.MediaType;
import java.io.IOException;

/**
 * 賣家模組控制器 (Seller Management Controller)
 * 🚀 職責：處理所有與賣家身分相關的 HTTP 請求。
 * 核心架構：
 * 1. 採用 UUID 安全識別機制，不對外暴露資料庫實體 ID。
 * 2. 實作 DTO (Data Transfer Object) 模式，達成數據封裝與最小化暴露。
 * 3. 支援 API 冪等性設計 (Idempotent Design)，防止重複開通。
 */
@RestController
@RequestMapping("/api/seller")
public class SellerController {

    private final SellerService sellerService;

    /**
     * 建構子注入 (Constructor Injection)
     * 優點：確保組件不可變性 (Immutability)，並利於撰寫單元測試。
     */
    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    /**
     * 🚀 啟動賣家權限 (或獲取現有資料)
     * 路由：POST /api/seller/active
     * * 設計邏輯：
     * - 這是「一鍵開店」的核心接口。
     * - 採用 "Get or Create" 模式：若用戶尚未具備賣家身分則初始化，若已有資料則直接回傳。
     * - @RequestBody(required = false)：允許用戶在不傳送任何參數的情況下快速開通。
     */
    @PostMapping("/active")
    public ResponseEntity<SellerResponse> activateSeller(@RequestBody(required = false) SellerRequest request) {
        // 從 SecurityContext 中提取當前登入用戶的安全 UUID
        UUID userUuid = SecurityUtil.getCurrentUserUuid();

        // 若 request 為空，則傳入 null，讓 Service 層決定預設店名
        String initialShopName = (request != null) ? request.shopName() : null;

        SellerProfile seller = sellerService.getOrCreateSeller(userUuid, initialShopName);

        // 透過轉換方法將 Entity 投影至 Response DTO
        return ResponseEntity.ok(convertToResponse(seller));
    }

    /**
     * 🚀 更新賣家詳細資料
     * 路由：PUT /api/seller/profile
     * * 設計邏輯：
     * - 實作「防禦性更新」：僅更新 request 中有值的欄位。
     * - 透過 UUID 鎖定操作者身分，確保使用者只能修改「自己」的賣場資訊。
     */
    @PutMapping(value ="/profile" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SellerResponse> updateSettings(
            // 🚀 2. 使用 @RequestPart 接收 JSON 字串 (會自動轉為 DTO)
            @RequestPart("settings") SellerRequest request,
            // 🚀 3. 接收賣場頭像與看板 (設定為 required = false，使用者不一定要換圖)
            @RequestPart(value = "avatar", required = false) MultipartFile avatar,
            @RequestPart(value = "banner", required = false) MultipartFile banner
    ) throws IOException {
        UUID userUuid = SecurityUtil.getCurrentUserUuid();

        // 呼叫 Service 執行局部更新邏輯
        SellerProfile updated = sellerService.updateSellerSettings(
                userUuid, request, avatar, banner);

        return ResponseEntity.ok(convertToResponse(updated));
    }

    /**
     * 🚀 獲取賣家個人賣場資訊
     * 路由：GET /api/seller/my-shop
     * * 適用場景：賣家進入後台管理頁面時，初始化顯示目前的設定值。
     */
    @GetMapping("/my-shop")
    public ResponseEntity<SellerResponse> getMyShop() {
        UUID userUuid = SecurityUtil.getCurrentUserUuid();

        SellerProfile seller = sellerService.getSellerInfo(userUuid);

        return ResponseEntity.ok(convertToResponse(seller));
    }

    /**
     * 🛠️ 內部輔助方法：Entity 轉 Response DTO
     * * 為什麼需要這個？ (PPT 技術亮點)
     * 1. 隔離性：保護資料庫實體 (Entity)，避免隱私欄位 (如 user_id) 意外流出。
     * 2. 靈活性：後端資料庫可以有 20 個欄位，但 API 回應可以只精選出 8 個前端需要的欄位。
     * 3. 穩定性：即便修改資料庫欄位名稱，只需調整此轉換邏輯，不會導致前端壞掉。
     */
    private SellerResponse convertToResponse(SellerProfile s) {
        return new SellerResponse(
                s.getShopName(),
                s.getShopDescription(),
                s.getAvatarUrl(),
                s.getBannerUrl(),
                s.getTaxId(),
                s.getStatus(),
                s.getServicePhone(),
                s.getCreatedAt() // 雖然不可修改，但允許查詢展示
        );
    }
}