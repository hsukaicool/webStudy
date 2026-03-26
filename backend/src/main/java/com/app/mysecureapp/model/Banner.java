package com.app.mysecureapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;   // GCS 的圖片網址
    private String title;      // Banner 標題 (可選)
    private String linkUrl;    // 點擊後跳轉的網址 (例如某個活動頁)
    private Integer sortOrder; // 顯示順序 (數字越小越前面)
    private Boolean active;    // 是否啟用中

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}