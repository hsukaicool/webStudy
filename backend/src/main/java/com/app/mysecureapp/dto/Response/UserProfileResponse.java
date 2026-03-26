package com.app.mysecureapp.dto.Response;

import com.app.mysecureapp.model.UserProfile;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 使用者個人資料回應物件 (User Profile Response DTO)
 * 🚀 技術亮點：數據封裝與資安過濾
 * 1. 隔離隱私：不回傳資料庫遞增 ID (Long id)，僅回傳必要的業務資訊。
 * 2. 唯讀展示：包含 createdAt 等系統欄位，供前端展示「會員加入日期」。
 */
public record UserProfileResponse(
        String username,      // 從 User 實體抓取，供前端顯示帳號
        String role,          // 回傳角色資訊 (如 "ROLE_ADMIN")
        String displayName,   // 顯示暱稱
        String email,         // 電子信箱
        String phoneNumber,   // 電話
        String bio,           // 個人簡介
        String location,      // 地點
        LocalDate birthday,   // 生日
        UserProfile.Gender gender, // 性別
        String avatarUrl,     // 頭像網址
        LocalDateTime createdAt // 帳號建立時間 (用於 PPT 展示系統審計能力)
) {}