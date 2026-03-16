package com.app.mysecureapp.dto;

public record LoginRequest(
        String username,
        String password
) {
}