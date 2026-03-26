package com.app.mysecureapp.service;

import com.app.mysecureapp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * 授權與認證服務層 (Authentication Service)
 * 專門處理「登入驗證」與「核發通行證 (JWT)」的業務邏輯。
 * 將登入職責與一般的 UserService (帳號管理) 分離，符合單一職責原則。
 */
@Service
public class AuthService {

    // AuthenticationManager 是 Spring Security 的「認證總司令」
    // 它會自動去調用 UserDetailsService 查資料庫，並用 PasswordEncoder 比對密碼
    private final AuthenticationManager authenticationManager;

    // JWT 工具類別，負責把使用者資訊打包成加密字串
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 驗證使用者身分並產生 JWT Token (登入流程)
     * * @param username 前端傳來的使用者名稱
     * @param password 前端傳來的明文密碼
     * @return 簽發成功的 JWT 字串 (包含使用者身分與過期時間)
     * @throws AuthenticationException 若帳號不存在或密碼錯誤，會由 Spring 自動拋出例外
     */
    public String login(String username, String password) throws AuthenticationException {

        // --- 1. 執行核心身分驗證 (Authentication) ---
        // 將前端傳來的帳號密碼，封裝成一個「未認證的 Token」交給總司令審查。
        // 💡 魔法發生在這裡：
        // authenticationManager 會在底層自動觸發 loadUserByUsername() 抓出資料庫裡的暗碼，
        // 然後自動使用 BCrypt 演算法進行比對。我們完全不需要手動寫密碼比對邏輯！
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );

        // --- 2. 認證成功，取得核心身分資料 (Principal) ---
        // 程式能走到這一行，代表密碼絕對正確 (否則上面就 throw Exception 擋掉了)。
        // 這裡的 Principal 就是該名使用者的詳細資訊 (包含他的 Role 權限)。
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // --- 3. 核發 JWT Token (Stateless Session) ---
        // 驗證過關後，我們不使用傳統的 Session 存入伺服器記憶體，
        // 而是將使用者資訊交給 JwtUtil 簽發成一張「數位護照 (JWT)」。
        // 之後該使用者只要帶著這串字串，伺服器就能認得他。
        return jwtUtil.generateToken(userDetails);
    }
}