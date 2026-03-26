package com.app.mysecureapp.dto;

import com.app.mysecureapp.model.UserProfile;
import java.time.LocalDate;

/**
 * 使用者個人資料更新請求
 * 建議只放「允許使用者自行修改」的欄位
 */
public record UserProfileRequest(
        String displayName,  // 更新顯示暱稱，而不是帳號(username)
        String email,
        String phoneNumber,
        String bio,
        String location,
        LocalDate birthday,
        UserProfile.Gender gender
) {}