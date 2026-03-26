package com.app.mysecureapp.repository;

// === 1. Spring Data JPA 核心架構 ===
import com.app.mysecureapp.model.Product;
import com.app.mysecureapp.model.SellerProfile; // 記得要引進這個模型
import com.app.mysecureapp.model.Enum.ProductStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// === 2. Java 標準函式庫 ===
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 商品資料存取層 (Product Repository)
 * 🚀 職責：繼承 JpaRepository 以獲得標準的 CRUD (增刪改查) 功能。
 * * 技術亮點：
 * 1. 衍生查詢 (Derived Queries)：利用 Spring Data 命名規範，免寫 SQL 即可實作複雜查詢。
 * 2. 安全查詢：支援透過 UUID (External ID) 檢索，落實前後端分離的安全規範。
 * 3. 關聯檢索：支援根據賣家實體過濾商品清單，對接賣家中心後台。
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 🚀 這裡就是解決方案！
    // 你在 SQL 門口就叫它「一起帶回來」，所以 Service 裡的循環就不會再額外打 SQL 了
    // JOIN FETCH 技術
    @Query("SELECT p FROM Product p JOIN FETCH p.seller WHERE p.status = :status")
    // 加入 Pageable 參數，Spring 就知道要幫你寫 LIMIT 和 OFFSET 的 SQL 了
    // 🚀 關鍵修改：將 List 改成 Page
    // 這樣 Spring 才會幫你跑兩次 SQL (一次抓 20 筆資料，一次算總共有幾頁)
    Page<Product> findByStatus(@Param("status") ProductStatus status, Pageable pageable);

    /**
     * 🚀 安全檢索：透過對外公開的 UUID 尋找商品
     * 適用場景：商品詳情頁面 (Product Detail)
     * @param externalId 商品的安全識別碼
     * @return 封裝於 Optional 中的商品實體，避免 NullPointerException
     */
    Optional<Product> findByExternalId(UUID externalId);

    /**
     * 🚀 賣家商品管理：獲取特定賣家的所有商品清單
     * 適用場景：賣家後台的「商品管理」列表
     * @param seller 賣家實體物件
     * @return 該賣家名下的所有商品列表
     */
    List<Product> findBySeller(SellerProfile seller);

    /**
     * 🚀 分類瀏覽：根據商品分類獲取清單
     * 適用場景：首頁或分類搜尋頁面
     * @param category 分類名稱 (如: electronics)
     * @return 該分類下的所有商品
     */
    List<Product> findByCategory(String category);

    /**
     * 🚀 狀態過濾：獲取特定賣家且處於「上架中」的商品
     * 適用場景：消費者瀏覽特定賣家的賣場首頁
     */
    List<Product> findBySellerAndStatus(SellerProfile seller, String status);


}