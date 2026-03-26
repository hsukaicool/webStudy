package com.app.mysecureapp.controller;

// === 1. 資料傳輸物件 (DTO) 引入 ===
import com.app.mysecureapp.dto.UserProfileRequest;  // 接收修改請求
import com.app.mysecureapp.dto.Response.UserProfileResponse; // 🚀 新增：專業的回應物件

// === 2. 模型層 (Model) 引入 ===
import com.app.mysecureapp.model.User;
import com.app.mysecureapp.model.UserProfile;

// === 3. 服務層與工具類 引入 ===
import com.app.mysecureapp.service.ProfileService; // 🚀 修正名稱對齊你的 Service
import com.app.mysecureapp.util.SecurityUtil;

// === 4. Spring Framework 核心標註 ===
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType; // 🚀 用於指定媒體類型
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // 🚀 用於處理二進位圖片檔案

// === 5. Java 標準庫 ===
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * 【個人檔案控制器】
 * 🚀 職責：負責用戶資料的查詢與變更。
 * 🚀 技術亮點：實作「雙 DTO 隔離模式」，確保 Request 與 Response 職責分離，符合業界安全規範。
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * 【GET：讀取個人檔案】
     * 🚀 邏輯：由 Token 識別身分 -> 查詢多表數據 -> 聚合為 Response DTO
     * @return 封裝後的用戶完整公開資訊
     */
    @GetMapping
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        // 1. 從安全上下文中提取當前登入使用者的 UUID
        UUID userUuid = SecurityUtil.getCurrentUserUuid();

        // 2. 獲取受管理狀態的 Profile 實體
        UserProfile profile = profileService.getUserProfileByUuid(userUuid);

        // 3. 轉換並回傳專業的回應物件 (含唯讀欄位)
        return ResponseEntity.ok(convertToResponse(profile));
    }

    /**
     * 【PUT：更新個人檔案】
     * 🚀 邏輯：接收修改 DTO -> 執行局部更新 -> 回傳「最新」的完整資料
     * @param request 包含使用者欲修改的欄位內容
     */
    @PutMapping
    public ResponseEntity<UserProfileResponse> updateMyProfile(@RequestBody UserProfileRequest request) {
        UUID userUuid = SecurityUtil.getCurrentUserUuid();

        // 執行跨表更新邏輯 (User 表與 Profile 表)
        UserProfile updatedProfile = profileService.updateProfileByUuid(userUuid, request);

        // 回傳更新後的最終狀態，確保前端 UI 與後端資料同步
        return ResponseEntity.ok(convertToResponse(updatedProfile));
    }

    /**
     * 【PATCH：上傳/更換頭像】
     * 🚀 技術亮點：處理 Multipart/form-data 請求。
     * 🚀 邏輯：接收檔案 -> 調用 GCS 服務 -> 更新資料庫 -> 回傳新網址。
     * @param file 來自前端 UI 的圖片檔案。
     */
    @PatchMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {

        // 1. 安全守衛：從 JWT 解析出當前操作者 UUID
        UUID userUuid = SecurityUtil.getCurrentUserUuid();

        // 2. 執行業務邏輯：GCP 上傳與資料庫同步 (回傳新的雲端網址)
        String newAvatarUrl = profileService.updateAvatar(userUuid, file);

        // 3. 回傳標準 JSON 響應，通知前端更新顯示內容
        return ResponseEntity.ok(Map.of("avatarUrl", newAvatarUrl));
    }

    /**
     * 【私有輔助方法：Entity 轉 Response DTO】
     * 🚀 關鍵技術：數據聚合 (Data Aggregation)
     * 在此處將來自 User 與 UserProfile 兩張表的資料「打平」後放入單一 Record。
     */
    private UserProfileResponse convertToResponse(UserProfile profile) {
        User user = profile.getUser();

        // 🚀 建立 Response 物件，包含前端需要的所有欄位 (甚至包含 createdAt)
        return new UserProfileResponse(
                user.getUsername(),      // 1. 帳號 (唯讀)
                user.getRole(),           // 2. 角色 (唯讀)
                user.getDisplayName(),   // 2. 顯示名稱 (可改)
                user.getEmail(),         // 3. 信箱
                profile.getPhoneNumber(),// 4. 電話
                profile.getBio(),        // 5. 簡介
                profile.getLocation(),   // 6. 地點
                profile.getBirthday(),   // 7. 生日
                profile.getGender(),     // 8. 性別
                profile.getAvatarUrl(),  // 9. 頭像網址
                profile.getCreatedAt()   // 10. 創立時間 (系統自動記錄)
        );
    }
}