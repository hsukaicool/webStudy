package com.app.mysecureapp.config;

// ==========================================
// 1. 我們自己專案的檔案
// ==========================================

// ==========================================
// 2. Java EE / Jakarta 網頁底層規範
// ==========================================
// 所有 Web 過濾器 (Filter) 的最源頭介面。
// 這裡匯入是因為我們要把自訂的 JWT 警衛強制轉型為標準 Filter 塞進安檢鏈裡。
import jakarta.servlet.Filter;

// ==========================================
// 3. Spring Core (核心容器與依賴注入 DI)
// ==========================================
// @Autowired：請 Spring 幫我把已經建立好的物件 (例如 UserRepository) 自動注射進來，不用我自己 new。
import org.springframework.beans.factory.annotation.Autowired;
// @Bean：標記在方法上，告訴 Spring「這個方法回傳的物件，請幫我收進容器裡集中管理」。
import org.springframework.context.annotation.Bean;
// @Configuration：告訴 Spring 啟動時要先讀這個檔案，因為裡面有很多重要的 @Bean 設定。
import org.springframework.context.annotation.Configuration;

// ==========================================
// 4. Spring Security 核心設定與網路安全 (Web Security)
// ==========================================
// HttpSecurity：最核心的設定工具！用來設定哪些 API 要攔截、CORS/CSRF 怎麼擋、Session 怎麼管。
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// @EnableWebSecurity：加在類別上，代表正式啟用 Web 安全安檢大門。
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// SessionCreationPolicy：定義 Session 的生成策略 (我們要用到裡面的 STATELESS 無狀態策略)。
import org.springframework.security.config.http.SessionCreationPolicy;
// SecurityFilterChain：最終組裝完成的「安全過濾鏈」，也就是整條安檢通道的成品。
import org.springframework.security.web.SecurityFilterChain;
// UsernamePasswordAuthenticationFilter：Spring 預設用來處理表單帳號密碼登入的過濾器。
// 匯入它是為了當作「定位點」，好讓我們的 JWT 過濾器可以插隊在它前面。
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// ==========================================
// 5. Spring Security 認證機制 (Authentication & Password)
// ==========================================
// AuthenticationManager：認證大總管，之後在登入 API 會呼叫它來執行帳號密碼的核對。
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// AuthenticationProvider & DaoAuthenticationProvider：實際去執行「比對密碼」與「驗證身分」的底層工人。
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

// PasswordEncoder & BCryptPasswordEncoder：密碼加密與解密的工具，確保我們是用 BCrypt 單向雜湊來處理密碼。
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.mysecureapp.service.CustomUserDetailsService; // 導入專換工具

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
// ==========================================

@Configuration // 告訴 Spring 啟動時要先讀取這個設定檔
@EnableWebSecurity // 啟用 Spring Security 的網頁安全機制 (開啟安檢大門)
@EnableMethodSecurity // 🚀 關鍵：開啟「方法等級」的安全檢查功能
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter; // 我們自己寫的 JWT 警衛

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // 使用轉換工具

    // 【核心大腦】定義整個應用程式的 HTTP 安檢流程 (Filter Chain)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                // 啟用 CORS，它會去抓我們剛寫的 WebConfig 設定
                .cors(org.springframework.security.config.Customizer.withDefaults())

                // 1. 關閉 CSRF (跨站請求偽造) 防護
                // 【原因】CSRF 攻擊主要是針對依賴 Cookie/Session 的網站。我們現在改用前後端分離，
                // Token 是放在 HTTP Header 裡面傳遞，天然免疫 CSRF 攻擊，所以直接關閉以節省效能。
                .csrf(csrf -> csrf.disable())

                // 2. 設定 API 路由的存取權限 (分流)
                .authorizeHttpRequests(auth -> auth
                        // 針對登入 (/api/auth/login) 和註冊 (/api/auth/register) 的 API，允許所有人存取 (不須 Token)
                        .requestMatchers("/api/auth/**").permitAll()
                        // 這裡必須放行 /api/public/ 開頭的所有路徑，這樣「沒登入的使用者」才能看到首頁商品。
                        .requestMatchers("/api/public/**").permitAll()

                        .requestMatchers("/hello").permitAll()
                        
                        // 🚀 放行 Spring 預設的錯誤轉發路徑，避免發生 403 Forbidden 掩蓋真實錯誤內容
                        .requestMatchers("/error").permitAll()

                        // 你之前的 ProductController 路徑是 /api/products/
                        // 我們設定只有「已登入 (authenticated)」的人才能進
                        // 如果你想更嚴格，可以改為 .hasRole("SELLER")
                        .requestMatchers("/api/products/**").authenticated()

                        // 🚀 【新增：賣家專用區】
                        // 這裡我們明確指定 /api/seller/ 開頭的所有路徑都必須「通過驗證 (authenticated)」
                        .requestMatchers("/api/seller/**").authenticated()

                        // 🚀 訂單必須登入才能看
                        .requestMatchers("/api/orders/**").authenticated()

                        // 除了上面提到的之外，"任何其他請求" 都必須經過身分驗證才能放行
                        .anyRequest().authenticated())

                // 3. 設定 Session 策略為「無狀態 (STATELESS)」
                // 【原因】這是 JWT 架構的靈魂！我們告訴 Spring Security：「不要在伺服器記憶體裡建立 Session 記住使用者」。
                // 每個請求都必須自己帶上 JWT，伺服器驗證完就忘記，實現真正的無狀態擴展。
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. 掛載我們自訂的認證提供者 (告訴系統去哪裡查帳號、怎麼比對密碼)
                .authenticationProvider(authenticationProvider())

                // 5. 安插我們的 JWT 警衛
                // 【原因】Spring 預設有一個檢查帳號密碼的警衛 (UsernamePasswordAuthenticationFilter)。
                // 我們要把自訂的 JWT 警衛 (jwtAuthFilter) 安排在它「之前」。
                // 這樣只要請求一進來，我們先檢查有沒有合法的 Token，有的話就直接放行，不用再去比對帳號密碼了。
                .addFilterBefore((Filter) jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 【認證供應商】負責整合「找資料」與「比對密碼」的邏輯
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // 告訴它：去哪裡找使用者資料？(交給customUserDetailsService)
        authProvider.setUserDetailsService(customUserDetailsService);
        // 告訴它：密碼是用什麼演算法加密的？(交給下面的 passwordEncoder)
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 【認證管理員】負責處理登入時的認證請求 (我們稍後在 LoginController 會用到它來觸發登入驗證)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 【密碼加密器】定義密碼的雜湊演算法
    // BCrypt 是一種單向不可逆的加密演算法，就算資料庫被駭客偷走，也解不出原始密碼，是業界標準。
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 【CORS 配置中心】定義哪些外來網站可以存取我們的 API
    // 🚀 技術亮點：精確控制跨網域存取權限，防止非法網域調用 API。
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. 允許的來源：這裡填入你 React 前端的網址
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://127.0.0.1:5173"));

        // 2. 允許的 HTTP 方法：一定要包含 PATCH，因為頭像更新是用 PATCH
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 3. 允許的標頭：允許前端傳送 Authorization (JWT) 和 Content-Type
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));

        // 4. 允許攜帶憑證 (如 Cookie 或認證資訊)
        configuration.setAllowCredentials(true);

        // 5. 將此設定應用到所有的 API 路徑 (/**)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}