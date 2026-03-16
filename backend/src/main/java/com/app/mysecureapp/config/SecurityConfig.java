package com.app.mysecureapp.config;

// ==========================================
// 1. 我們自己專案的檔案
// ==========================================
// 用來跟資料庫溝通，根據帳號把使用者撈出來
import com.app.mysecureapp.repository.UserRepository;

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
// UserDetailsService：Spring 規定的標準介面，專門用來「定義如何透過帳號載入使用者」。
import org.springframework.security.core.userdetails.UserDetailsService;
// UsernameNotFoundException：當資料庫找不到該帳號時，專門用來丟出的例外錯誤。
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// PasswordEncoder & BCryptPasswordEncoder：密碼加密與解密的工具，確保我們是用 BCrypt 單向雜湊來處理密碼。
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration // 告訴 Spring 啟動時要先讀取這個設定檔
@EnableWebSecurity // 啟用 Spring Security 的網頁安全機制 (開啟安檢大門)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter; // 我們自己寫的 JWT 警衛

    @Autowired
    private UserRepository userRepository; // 用來去資料庫找使用者的工具

    // 【核心大腦】定義整個應用程式的 HTTP 安檢流程 (Filter Chain)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 關閉 CSRF (跨站請求偽造) 防護
                // 【原因】CSRF 攻擊主要是針對依賴 Cookie/Session 的網站。我們現在改用前後端分離，
                // Token 是放在 HTTP Header 裡面傳遞，天然免疫 CSRF 攻擊，所以直接關閉以節省效能。
                .csrf(csrf -> csrf.disable())

                // 2. 設定 API 路由的存取權限 (分流)
                .authorizeHttpRequests(auth -> auth
                        // 針對登入 (/api/auth/login) 和註冊 (/api/auth/register) 的 API，允許所有人存取 (不須 Token)
                        .requestMatchers("/api/auth/**").permitAll()
                        // 除了上面提到的之外，"任何其他請求" 都必須經過身分驗證才能放行
                        .anyRequest().authenticated()
                )

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
        // 告訴它：去哪裡找使用者資料？(交給下面的 userDetailsService)
        authProvider.setUserDetailsService(userDetailsService());
        // 告訴它：密碼是用什麼演算法加密的？(交給下面的 passwordEncoder)
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 【認證管理員】負責處理登入時的認證請求 (我們稍後在 AuthController 會用到它來觸發登入驗證)
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

    // 【使用者資料服務】定義如何從資料庫撈取使用者細節
    // 當使用者登入，或是解析 JWT 需要核對身分時，Spring Security 會呼叫這個方法。
    @Bean
    public UserDetailsService userDetailsService() {
        // 使用 Lambda 表達式實作：傳入 username，用 UserRepository 去資料庫撈資料
        return username -> (org.springframework.security.core.userdetails.UserDetails) userRepository.findByUsername(username)
                // 如果找不到，就丟出例外錯誤
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者: " + username));
    }
}