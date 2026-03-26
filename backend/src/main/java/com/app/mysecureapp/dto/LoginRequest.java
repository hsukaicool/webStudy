package com.app.mysecureapp.dto;

/**
 * 登入請求 DTO (Data Transfer Object)
 * 用來接收前端傳來的登入表單資料
 */

public record LoginRequest(
        String username,
        String password
) {
}