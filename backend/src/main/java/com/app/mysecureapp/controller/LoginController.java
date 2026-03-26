package com.app.mysecureapp.controller;

// === 1. 專案內部的依賴 (Local Project Imports) ===
import com.app.mysecureapp.dto.LoginRequest;
import com.app.mysecureapp.service.AuthService;

// === 2. Spring 核心與依賴注入 (Spring Core & DI) ===
import org.springframework.beans.factory.annotation.Autowired;

// === 3. Spring Web 模組 (Spring MVC) ===
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// === 4. Spring Security 安全模組 ===
import org.springframework.security.core.AuthenticationException;

// === 5. Java 原生標準函式庫 (Java Core) ===
import java.util.HashMap;
import java.util.Map;

/**
 * 登入控制器 (Login Controller)
 * 系統的「大門守衛」，只負責接收前端的 HTTP 請求、呼叫 Service，並回傳相對應的 HTTP 狀態碼與 JSON。
 */
@RestController // 告訴 Spring 這是個 REST API 控制器，所有 return 的物件都會自動被轉換成 JSON 格式
@RequestMapping("/api/auth") // 定義路由前綴，這支 Controller 負責的所有網址都會以 /api/auth 開頭
public class LoginController {

    private final AuthService authService;

    // 透過建構子注入 AuthService。這樣寫能確保 authService 不會是 null，是目前業界最推薦的寫法。
    @Autowired
    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 處理登入請求
     * @PostMapping("/login"): 綁定 HTTP POST 方法，完整網址會是 POST /api/auth/login
     * @RequestBody: 攔截前端傳來的 JSON 字串，並自動對應轉換成 LoginRequest 這個 Java 紀錄 (Record/DTO)
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            // 🚀 委派 (Delegation)：Controller 不自己比對密碼，把從 DTO 拆出來的帳號密碼交給 AuthService 處理
            String jwt = authService.login(loginRequest.username(), loginRequest.password());

            // 封裝成功回應：準備一個 Map (字典)，用來裝要回傳給前端的資料
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt); // 放進 JWT 通行證
            response.put("username", loginRequest.username()); // 放進使用者名稱，讓前端可以顯示「歡迎回來」

            // ResponseEntity.ok() 會產生 HTTP 200 (成功) 的狀態碼，並把 response 轉換成 JSON 回傳
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            // 捕捉例外：如果 AuthService 發現密碼錯誤，會丟出這個 Exception，我們在這裡攔截它
            // ResponseEntity.status(401) 會產生 HTTP 401 (未經授權) 的狀態碼，精準告訴前端登入失敗
            return ResponseEntity.status(401).body("登入失敗：帳號或密碼錯誤");
        }
    }
}