package com.app.mysecureapp.repository;

import com.app.mysecureapp.model.SellerProfile;
import com.app.mysecureapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
    // 透過 User 物件來找賣家資料
    Optional<SellerProfile> findByUser(User user);
}