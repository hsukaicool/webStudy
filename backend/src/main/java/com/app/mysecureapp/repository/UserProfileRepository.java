package com.app.mysecureapp.repository;

import com.app.mysecureapp.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    // 🚀 加入這行：Spring Data JPA 會自動解析這個名稱
    // 它會去 UserProfile 找 user 欄位，再到 User 裡面找 externalId 欄位
    Optional<UserProfile> findByUserExternalId(UUID externalId);
}