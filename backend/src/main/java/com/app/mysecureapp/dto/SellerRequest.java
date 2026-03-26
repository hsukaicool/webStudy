package com.app.mysecureapp.dto;

import java.time.LocalDateTime;

/**
 * 賣家資料回應物件
 * 🚀 策略：在此過濾掉尚未開放的前端功能或敏感欄位
 */
public record SellerRequest(
        String shopName, //店名
        String shopDescription, //簡介
        String avatarUrl, // 頭像
        String bannerUrl, //Banner
        String taxId, // 統一編號 (選填)
        String status, // 狀態
        String servicePhone // 電話
) {}