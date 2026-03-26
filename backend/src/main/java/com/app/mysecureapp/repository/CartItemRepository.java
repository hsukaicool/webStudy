package com.app.mysecureapp.repository;

import com.app.mysecureapp.model.CartItem;
import com.app.mysecureapp.model.Product;
import com.app.mysecureapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 購物車數據存取層 (Repository Layer)
 * 繼承 JpaRepository 以獲得基本的增刪查改 (CRUD) 功能
 */
@Repository // 標記為 Spring Bean，處理資料庫異常轉換
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * 功能：獲取特定使用者的所有購物車項目
     * 應用場景：進入「購物車頁面」時，顯示該使用者挑選的所有商品
     */
    List<CartItem> findByUser(User user);

    /**
     * 功能：根據使用者與商品尋找特定的購物車項目
     * 返回類型：使用 Optional 避免空指針異常 (NullPointerException)
     * 應用場景：使用者再次點擊「加入購物車」時，判斷該商品是否已存在。
     * - 若存在：則增加 quantity (數量)
     * - 若不存在：則新增一筆紀錄
     */
    Optional<CartItem> findByUserAndProduct(User user, Product product);

    /**
     * 功能：刪除該使用者的所有購物車紀錄
     * 註解說明：
     * 1. @Transactional: 刪除操作必須在事務中執行，確保資料一致性。
     * 2. 應用場景：使用者完成結帳 (Checkout) 後，清空購物車。
     */
    @Transactional
    void deleteByUser(User user);
}