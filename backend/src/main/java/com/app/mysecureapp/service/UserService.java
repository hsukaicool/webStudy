package com.app.mysecureapp.service;

import com.app.mysecureapp.dto.RegisterRequest;
import com.app.mysecureapp.model.User;
import com.app.mysecureapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    /**
     * 註冊新使用者
     * @param request 前端傳來的註冊請求 DTO
     * @return 註冊成功後的 User 物件
     */
    public User registerUser(RegisterRequest request) {
        // 1. 檢查帳號是否已存在
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("錯誤: 使用者名稱 " + request.username() + " 已被使用!");
        }

        // 2. 檢查 Email 是否已存在
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("錯誤: Email " + request.email() + " 已被註冊!");
        }

        // 3. 建立新的 User 物件
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setDisplayName(request.displayName()); // 設定顯示名稱


        // 4. 加密密碼 (這是關鍵!)
        user.setPassword(passwordEncoder.encode(request.password()));

        // 5. 設定預設角色 (通常註冊的一般使用者都是 ROLE_USER)
        user.setRole("ROLE_USER");

        // 6. 存入資料庫
        return userRepository.save(user);
    }
}