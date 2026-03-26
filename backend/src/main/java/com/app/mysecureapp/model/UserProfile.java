package com.app.mysecureapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.*;


@Entity
@Getter // 自動為所有欄位產生 Getter
@Setter // 自動為所有欄位產生 Setter
@NoArgsConstructor // 自動產生無參數建構子 (JPA 必備)
@AllArgsConstructor // 自動產生全參數建構子
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 與 User 是一對一關聯
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private User user;

    private String phoneNumber; // 手機號碼

    @Column(columnDefinition = "TEXT")
    private String bio; // 個人簡介

    @Column(columnDefinition = "TEXT")
    private String location; // 地點

    private String avatarUrl; // 大頭貼網址

    private LocalDate birthday; // 生日

    @Enumerated(EnumType.STRING)

    @Column(length = 10) // 限制長度，節省資料庫空間
    private Gender gender;
    public enum Gender {
        MALE,   // 男
        FEMALE, // 女
        OTHER,  // 其他
        SECRET  // 保密 / 不願透露
    }

    // --- 系統自動維護的稽核欄位 ---

    @CreationTimestamp
    @Column(updatable = false) // 建立後就不允許修改
    private LocalDateTime createdAt; // 檔案建立時間

    @UpdateTimestamp
    private LocalDateTime updatedAt; // 最後修改時間
}