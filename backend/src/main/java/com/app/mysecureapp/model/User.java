package com.app.mysecureapp.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore; // 讓程式認識 @JsonIgnore
import java.util.UUID; // 讓程式認識 UUID

/**
 * 使用者實體類別 (Entity)
 * 對應資料庫中的 "users" 資料表
 */
@Entity
@Table(name = "users")
public class User {

    @Id // 唯一
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore // 這是關鍵：永遠不要讓這個 ID 出現在 JSON 回應中
    private Long id;

    // 對外使用的唯一識別碼
    @Column(nullable = false, unique = true)
    private UUID externalId = UUID.randomUUID();

    // 帳號
    // unique = true 確保資料唯一，避免重複
    @Column(nullable = false, unique = true)
    private String username;

    // 密碼
    // nullable = false 不可為空值
    @Column(nullable = false)
    private String password;

    // 電子信箱，唯一
    @Column(nullable = false, unique = true)
    private String email;

    // 顯示名稱通常是必填的
    @Column(nullable = false)
    private String displayName;

    // 角色權限，例如 "ROLE_USER", "ROLE_ADMIN"
    // 這裡簡化處理，直接存字串。複雜系統可能會用 @ManyToMany 關聯 Roles 表
    private String role = "ROLE_USER";

    // 建構子
    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public UUID getExternalId() {return externalId;}
    // id 由資料庫產生，通常不需要 setId

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDisplayName() {
        return displayName; }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}