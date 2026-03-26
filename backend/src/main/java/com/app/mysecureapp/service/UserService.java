package com.app.mysecureapp.service;

import com.app.mysecureapp.dto.RegisterRequest;
import com.app.mysecureapp.model.User;
import com.app.mysecureapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 使用者業務邏輯層 (User Service)
 * 負責處理與「使用者帳號生命週期」相關的核心業務，例如：註冊、登入驗證等。
 * 遵循單一職責原則 (SRP)，不涉及個人檔案 (Profile) 的具體操作。
 */
@Service
public class UserService {

    // 使用 final 確保依賴物件在初始化後不可被修改 (Thread-safe)
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 引入 ProfileService 進行「服務委派 (Service Delegation)」
    // 將建立個人檔案的職責交給專業的 Service 處理，降低模組間的耦合度
    private final ProfileService ProfileService;

    /**
     * 建構子注入 (Constructor Injection)
     * Spring 官方推薦的依賴注入方式，有利於撰寫單元測試並確保依賴完整性。
     */
    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ProfileService ProfileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.ProfileService = ProfileService;
    }

    /**
     * 處理使用者註冊請求
     * * @Transactional: 宣告事務管理。確保「存入 User」與「建立 Profile」這兩個動作同生共死 (Atomic)。
     * 如果建立 Profile 時發生例外錯誤，已存入的 User 會自動 Rollback (退回)，避免資料庫產生不一致的髒資料。
     * * @param request 前端傳來的註冊資料傳輸物件 (DTO)
     * @return 註冊成功並持久化後的 User 實體
     * 主要是管理登入
     */
    @Transactional
    public User registerUser(RegisterRequest request) {

        // --- 1. 業務規則驗證 (Validation) ---
        // 為了防止資料庫報錯，在寫入前先進行資料庫層級的唯一性檢查
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("錯誤: 使用者名稱 " + request.username() + " 已被使用!");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("錯誤: Email " + request.email() + " 已被註冊!");
        }

        // --- 2. 資料轉換與封裝 (Mapping) ---
        // 將前端傳來的 DTO 資料封裝進準備存入資料庫的 Entity
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setDisplayName(request.displayName());

        // 資安防護：密碼必須經過 BCrypt 單向雜湊加密後才能放入記憶體與資料庫
        user.setPassword(passwordEncoder.encode(request.password()));

        // 權限控管：賦予新註冊者最基礎的系統權限
        user.setRole("ROLE_USER");

        // --- 3. 持久化核心資料 (Persistence) ---
        // 執行 SQL INSERT，並取得包含資料庫自動生成 ID 的 savedUser 物件
        User savedUser = userRepository.save(user);

        // --- 4. 觸發連帶業務 (Service Delegation) ---
        // 🚀 將剛剛建好的 savedUser 傳遞給 ProfileService。
        // UserService 不過問 Profile 裡面到底有 bio 還是 location，完美實現解耦。
        ProfileService.createDefaultProfile(savedUser);

        // --- 5. 回傳結果 ---
        return savedUser;
    }
}