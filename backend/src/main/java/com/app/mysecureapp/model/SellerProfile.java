package com.app.mysecureapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;


@Entity
@Table(name = "seller_profiles")
@Getter
@Setter
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🚀 核心關聯：與 User 綁定
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String shopName;      // 店鋪名稱

    @Column(columnDefinition = "TEXT")
    private String shopDescription; // 店鋪簡介

    private String taxId;         // 統一編號 (選填)

    // 賣家狀態 (例如：審核中、已啟動、停權)
    private String status = "PENDING";

    // 退換貨政策
    @Column(columnDefinition = "TEXT")
    private String ReturnExchange;

    // --- 🚀 新增資產與識別欄位 ---

    private String avatarUrl;      // 賣家頭像 (與個人頭像分開，建立品牌感)

    private String bannerUrl;      // 賣場頂部橫幅廣告

    // --- 🚀 商業營運需求 ---

    private Double rating = 5.0;   // 賣場評分 (預設最高分，隨評價變動)

    @Column(columnDefinition = "TEXT")
    private String returnExchangePolicy; // 修正命名規範：退換貨政策內容

    private boolean holidayMode = false; // 🚀 休假模式：開啟時買家無法下單，保護賣家出貨率

    // --- 🚀 聯絡資訊 (可能與個人帳號不同) ---

    private String serviceEmail;   // 客服信箱
    private String servicePhone;   // 客服電話

    // --- 🚀 系統審計欄位 (面試加分項) ---

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt; // 成為賣家的時間

    @UpdateTimestamp
    private LocalDateTime updatedAt; // 資料最後更新時間
}