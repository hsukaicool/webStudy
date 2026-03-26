package com.app.mysecureapp.service;

import com.app.mysecureapp.model.User;
import com.app.mysecureapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * 使用者查詢專用服務 (User Query Service)
 * 🚀 架構亮點：實踐單一職責原則 (SRP) 與輕量級讀寫分離 (CQRS)。
 * 專門提供全系統共用的「只讀 (Read-only)」查詢功能，避免污染負責核心生命週期的 UserService。
 */
@Service
public class UserQueryService {

    private final UserRepository userRepository;

    @Autowired
    public UserQueryService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 透過 UUID 尋找使用者核心實體 (提供給 ProfileService, CartService 等共用)
     * @param userUuid 外部安全識別碼
     * @return 查獲的 User 實體
     * @throws RuntimeException 若查無此人，統一在此拋出例外，避免各個 Service 重複寫防呆邏輯
     */
    public User findUserByUuid(UUID userUuid) {
        return userRepository.findByExternalId(userUuid)
                .orElseThrow(() -> new RuntimeException("系統錯誤：找不到此 UUID 的使用者 (" + userUuid + ")"));
    }

    // 未來如果你需要「透過 Email 找人」或「列出所有 Admin」，也全部寫在這個 QueryService 裡！
}