package com.app.mysecureapp.controller;

// 註冊系統所需
import com.app.mysecureapp.dto.RegisterRequest;
import com.app.mysecureapp.model.User;
import com.app.mysecureapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 登入系統
import com.app.mysecureapp.dto.LoginRequest;
import com.app.mysecureapp.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 身分驗證控制器 (Controller Layer)
 * 負責處理與「註冊」、「登入」相關的 HTTP 請求。
 */
@RestController // 宣告這是一個 RESTful API 控制器，回傳資料會自動轉為 JSON
@RequestMapping("/api/auth") // 定義此控制器所有路徑的開頭為 /api/auth
public class AuthController {

    private final UserService userService;
    // 1. 新增宣告這兩個屬性
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // 使用建構子注入 (Constructor Injection) UserService 業務邏輯層
    @Autowired
    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 處理使用者註冊請求
     * @param registerRequest 前端傳入的 JSON 資料，會自動映射成這個物件
     * @return 回傳 HTTP 狀態碼與訊息
     */
    @PostMapping("/register") // 定義接收 HTTP POST 請求，路徑為 /api/auth/register
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            // 1. 呼叫 Service 層執行註冊邏輯（包含密碼加密、帳號重複檢查）
            userService.registerUser(registerRequest);

            // 2. 註冊成功，回傳 HTTP 200 (OK)
            return ResponseEntity.ok("使用者註冊成功！");

        } catch (RuntimeException e) {
            // 3. 註冊失敗（例如帳號重複），Service 會丟出例外
            // 回傳 HTTP 400 (Bad Request) 並附上錯誤原因
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. 使用 AuthenticationManager 進行認證
            // 這一步會自動去查資料庫，並比對密碼 (BCrypt)
            // 如果失敗，會拋出 AuthenticationException
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            // 2. 認證成功，取得 UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 3. 產生 JWT Token
            String jwt = jwtUtil.generateToken(userDetails);

            // 4. 回傳 Token 給前端
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            // 您也可以在這裡回傳 username, role, userId 等資訊方便前端使用
            response.put("username", userDetails.getUsername());

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            // 認證失敗 (帳號或密碼錯誤)
            return ResponseEntity.status(401).body("登入失敗：帳號或密碼錯誤");
        }
    }
}
