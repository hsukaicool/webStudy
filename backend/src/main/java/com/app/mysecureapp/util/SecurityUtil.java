package com.app.mysecureapp.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;

// 負責解碼拿 UUID (邊界防禦)

/**
 * 系統安全工具類別 (Security Utility)
 * 提供全域共用的安全相關方法，避免在各個 Controller 重複撰寫相同的提取邏輯。
 */
public class SecurityUtil {

    /**
     * 從 Spring Security 上下文中取得當前登入使用者的 UUID
     * @return 當前使用者的 UUID
     * @throws RuntimeException 如果沒有使用者登入或 UUID 格式錯誤
     */
    public static UUID getCurrentUserUuid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("使用者尚未登入或授權已過期");
        }

        // 從保險箱拿出我們偷天換日的 UUID 字串
        String uuidString = authentication.getName();

        // 轉換並回傳真正的 UUID 物件
        return UUID.fromString(uuidString);
    }
}