package com.app.mysecureapp.controller;

import com.app.mysecureapp.model.Banner;
import com.app.mysecureapp.service.BannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/banners")
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    // 1. 公開端點：所有人都能看 Banner
    @GetMapping("/public")
    public ResponseEntity<List<Banner>> getActiveBanners() {
        return ResponseEntity.ok(bannerService.getAllActiveBanners());
    }

    // 2. 管理員端點：只有 text 能上傳
    // 🚀 當請求進來時，Spring Security 會先檢查當前使用者的 Role 有沒有包含 "ADMIN" (對應資料庫的 ROLE_ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/upload")
    public ResponseEntity<Banner> upload(
            @AuthenticationPrincipal UserDetails userDetails, // 抓取目前登入者
            @RequestParam("image") MultipartFile image,
            @RequestParam String title,
            @RequestParam String link
    ) throws IOException {
        return ResponseEntity.ok(bannerService.uploadBanner(userDetails.getUsername(), image, title, link));
    }
}
