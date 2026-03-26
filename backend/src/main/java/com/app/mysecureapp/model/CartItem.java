package com.app.mysecureapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 購物車項目實體 (CartItem Entity)
 * 負責將 Java 物件對應到資料庫中的 cart_item 資料表 (ORM 映射)
 */
@Entity // 標記為 JPA 實體，讓 Spring Data JPA 知道這要對應資料庫表
@Getter // 自動生成 getter 方法 (由 Lombok 提供，保持程式碼簡潔)
@Setter // 自動生成 setter 方法
@NoArgsConstructor // 自動生成無參數建構子 (JPA 規範要求必須有空建構子)
public class CartItem {

    @Id // 標記此欄位為資料庫的主鍵 (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 設定主鍵為自動遞增 (如 MySQL 的 AUTO_INCREMENT)
    private Long id;

    // 定義「多對一」關聯：多個購物車項目可以屬於同一個使用者
    @ManyToOne(fetch = FetchType.LAZY) // 採用延遲加載 (Lazy Loading)，效能優化點：只有用到時才去資料庫查詢 User
    @JoinColumn(name = "user_id") // 在資料庫中生成的外部鍵 (FK) 名稱為 user_id
    private User user;

    // 定義「多對一」關聯：多個購物車項目可以指向同一件商品
    @ManyToOne(fetch = FetchType.LAZY) // 同樣使用 LAZY 以減少不必要的資料庫 IO 開銷
    @JoinColumn(name = "product_id") // 資料庫對應的外部鍵名稱為 product_id
    private Product product;

    @Column(nullable = false) // 限制購買數量不可為空 (實務建議增加)
    private Integer quantity; // 購買數量

    private LocalDateTime addedAt = LocalDateTime.now(); // 預設紀錄加入時間，方便後續排序或計算效能
}