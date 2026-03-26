package com.app.mysecureapp.repository;

import com.app.mysecureapp.model.OrderItem;
import com.app.mysecureapp.model.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


// 賣家的視角
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 🚀 技術亮點：跨表排序 (Nested Sorting)
    // 查詢屬於該賣家的所有商品項目，並根據「關聯訂單的時間」來排序
    List<OrderItem> findBySellerOrderByOrderCreatedAtDesc(SellerProfile seller);
}