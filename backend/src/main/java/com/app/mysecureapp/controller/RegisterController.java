package com.app.mysecureapp.controller;

import com.app.mysecureapp.dto.RegisterRequest;
import com.app.mysecureapp.service.UserService; //
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping; //
import org.springframework.web.bind.annotation.RestController; //

@RestController // 宣告這是一個 RESTful API 控制器，回傳資料會自動轉為 JSON
@RequestMapping("/api/auth") // 定義此控制器所有路徑的開頭為 /api/auth
public class RegisterController {

    private final UserService userService;

    // 建構子
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

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
}
