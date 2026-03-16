package com.app.mysecureapp.dto;



/**
 * 註冊請求 DTO (Data Transfer Object)
 * 用來接收前端傳來的註冊表單資料
 */
public record RegisterRequest(
        String username,
        String password,
        String email,
        String displayName
) {
}
