package com.app.mysecureapp.repository;

import com.app.mysecureapp.model.Order;
import com.app.mysecureapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


// 買家的視角
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 🚀 技術亮點：衍生查詢 (Derived Queries)
    // 根據買家 (User) 查詢所有訂單，並按照時間「由新到舊」排序
    List<Order> findByBuyerOrderByCreatedAtDesc(User buyer);
}
