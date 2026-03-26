package com.app.mysecureapp.config;

// === 1. 專案內部依賴 ===
import com.app.mysecureapp.util.JwtUtil;

// === 2. Java EE / Jakarta EE Servlet API (網頁底層通訊) ===
// 這是 Java 處理 HTTP 請求的最底層標準介面
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// === 3. Spring 核心與依賴注入 ===
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

// === 4. Spring Security 安全核心模組 ===
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

// === 5. Spring Web 模組 ===
import org.springframework.web.filter.OncePerRequestFilter;

// === 6. Java 原生例外處理 ===
import java.io.IOException;

/**
 * JWT 認證過濾器 (資安升級版：UUID 雙 ID 架構)
 * 攔截每一個進入系統的 HTTP 請求，驗證 JWT 並解析出使用者的 UUID。
 */
@Component // 讓 Spring 自動將這個 Filter 實例化並納入管理
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // 注入 UserDetailsService 來查詢資料庫
    // @Lazy: 延遲載入。這是為了解決 Spring Security 啟動時常見的「雞生蛋、蛋生雞」循環依賴問題
    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;

    /**
     * 核心過濾邏輯 (每次 Request 進來都會執行這裡)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 從 HTTP 標頭 (Header) 中攔截 Authorization 欄位
        final String authHeader = request.getHeader("Authorization");

        // 🚀 資安升級：這裡不再宣告 username，而是宣告 userUuid
        String userUuid = null;
        String jwt = null;

        // 2. 驗證 Header 格式是否符合 OAuth2 規範的 "Bearer " 開頭
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // 剔除前綴，只保留純 Token 字串
            try {
                // 🚀 關鍵偷天換日：
                // 雖然原本的方法名叫 extractUsername，但在雙 ID 架構下，
                // 我們在產生 Token 時已經把 UUID 塞進 Subject 裡了，所以這裡抽出來的其實是 UUID 字串！
                userUuid = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // 捕捉 Token 過期或遭竄改的例外
                logger.error("JWT Token 解析失敗或已過期: " + e.getMessage());
            }
        }

        // 3. 確保成功解析出 UUID，且當前請求尚未被授權 (SecurityContext 為空)
        if (userUuid != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 🚀 4. 將 UUID 交給 Service 去查資料庫
            // 注意：Spring Security 介面規定方法名叫 loadUserByUsername，
            // 但我們傳遞進去的是 userUuid (例如 "550e8400-e29b-41d4-a716-446655440000")。
            // 實作這個方法的 UserService 必須懂得把這個字串轉成 UUID 去查資料庫！
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userUuid);

            // 5. 二次驗證 Token 的有效性與使用者是否匹配
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // 6. 建立 Spring Security 專用的授權憑證 (Authentication Token)
                // 將 userDetails (內含 UUID) 放入憑證中，未來 Controller 就能直接提款
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // 附加上當前 HTTP 請求的詳細資訊 (例如 IP 位置、Session ID)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. 將憑證正式存入保險箱 (SecurityContextHolder)
                // 到這一步，Spring 才正式承認這個請求是「已登入」狀態
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. 放行！讓請求繼續往下走 (可能走向下一個 Filter，或是抵達 Controller)
        filterChain.doFilter(request, response);
    }
}