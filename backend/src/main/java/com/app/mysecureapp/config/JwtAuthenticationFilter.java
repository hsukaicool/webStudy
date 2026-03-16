package com.app.mysecureapp.config;

import com.app.mysecureapp.service.UserService;
import com.app.mysecureapp.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * JWT 認證過濾器
 * 每次請求進來時，檢查 Header 是否有有效的 JWT Token
 */
@Component
public class JwtAuthenticationFilter extends  OncePerRequestFilter{
    @Autowired
    private JwtUtil jwtUtil;
    // 這裡我們注入 UserDetailsService，用來從資料庫載入使用者
    // 注意：Spring Security 預設會尋找一個實作了 UserDetailsService 的 Bean
    // 會在 SecurityConfig 中定義它，或者讓 UserService 實作它

    @Autowired
    @Lazy   // 避免陷入循環依賴 (Circular Dependency)
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 1. 從 Header 中取得 Authorization 欄位
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // 2. 檢查 Header 是否以 "Bearer " 開頭
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // 去掉 "Bearer " (7 個字元)
            try {
                username = jwtUtil.extractUsername(jwt); // 取出使用者名稱
            } catch (Exception e) {
                // Token 解析失敗 (例如過期、簽名不符)
                logger.error("JWT Token 解析失敗: " + e.getMessage());
            }
        }

        // 3. 如果有使用者名稱，且目前沒有人登入 (SecurityContext 為空)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 4. 載入使用者詳細資訊 (查資料庫)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            //5. 驗證 Token 是否有效
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // 6. 建立 Authentication 物件 (通行證)
                // 這裡傳入 userDetails.getAuthorities()
                // 是為了讓 Spring Security 知道使用者的權限 (ROLE_USER, ROLE_ADMIN)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. 將通行證放入 SecurityContext，表示「已登入」
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 8. 繼續執行下一個過濾器
        filterChain.doFilter(request, response);
    }
}
