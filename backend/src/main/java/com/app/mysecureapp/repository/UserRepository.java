package com.app.mysecureapp.repository;

// 導入資料庫的實體類別
import com.app.mysecureapp.model.User;
// 導入 Spring Data JPA 的 Repository
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 使用者資料存取層 (Repository)
 * 負責跟 "users" 資料表溝通。
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. 透過使用者名稱尋找使用者
    // 對應 SQL: SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    // 透過 UUID 尋找使用者 (用於對外的 API，比用 ID 安全)
    Optional<User> findByExternalId(java.util.UUID externalId);

    // 2. 檢查使用者名稱是否已存在 (註冊時檢查帳號重複)
    // 對應 SQL: SELECT count(*) > 0 FROM users WHERE username = ?
    Boolean existsByUsername(String username);

    // 3. 檢查 Email 是否已存在 (註冊時檢查 Email 重複)
    // 對應 SQL: SELECT count(*) > 0 FROM users WHERE email = ?
    Boolean existsByEmail(String email);




}