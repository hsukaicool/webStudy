package com.app.mysecureapp.service;

// === 1. 專案內部模型與倉庫 ===
import com.app.mysecureapp.model.User;
import com.app.mysecureapp.repository.UserRepository;

// === 2. Spring Security 核心介面 ===
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// === 3. Spring 核心註解 ===
import org.springframework.stereotype.Service;

// === 4. Java 標準函式庫 ===
import java.util.List;
import java.util.UUID;

/**
 * 客製化使用者詳情服務 (雙 ID 智慧路由版)
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 🚀 智慧識別路由 (Smart Identifier Routing)
     * @param identifier 此參數可能是「帳號(username)」或是「安全識別碼(UUID)」
     */
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user;

        try {
            // 🚀 階段 A：嘗試判斷是否為 UUID 格式 (這通常來自 JWT 驗證請求)
            UUID userUuid = UUID.fromString(identifier);

            // 如果是 UUID 格式，改用 findByExternalId 查詢
            user = userRepository.findByExternalId(userUuid)
                    .orElseThrow(() -> new UsernameNotFoundException("找不到此 UUID 的使用者"));

        } catch (IllegalArgumentException e) {
            // 🚀 階段 B：如果不是 UUID 格式，則視為普通帳號 (這通常來自登入 Login 請求)
            user = userRepository.findByUsername(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("找不到帳號: " + identifier));
        }

        // 🚀 關鍵「掉包」動作：
        // 我們回傳給 Spring Security 的第一個參數 (Username) 統一設定為 UUID 字串。
        // 這確保了產生的 JWT Token 內容是安全的 UUID，而非真實帳號。
        return new org.springframework.security.core.userdetails.User(
                user.getExternalId().toString(),  // 回傳 uuid
                user.getPassword(),
                true, true, true, true, // 帳號狀態 (正常)
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}