package com.app.mysecureapp.model;

// === 1. Jakarta Persistence (JPA) 規範：定義資料庫映射 ===
import com.app.mysecureapp.model.Enum.ProductCategory;
import com.app.mysecureapp.model.Enum.ProductCondition;
import com.app.mysecureapp.model.Enum.ProductStatus;
import jakarta.persistence.*;

// === 2. Lombok 工具：自動化樣板代碼生成 ===
import lombok.*;

// === 3. Hibernate 擴充註解：自動化時間戳記 ===
import org.hibernate.annotations.CreationTimestamp;

// === 4. Java 標準函式庫 ===
import java.math.BigDecimal; // 🚀 處理金流必備：高精度十進位運算
import java.time.LocalDateTime;
import java.util.UUID; // 🚀 安全識別碼：避免暴露遞增 ID

/**
 * 商品實體類別 (Product Entity)
 * 🚀 職責：描述商品的完整屬性，並透過 JPA 映射至資料庫中的 "products" 資料表。
 * * 技術亮點：
 * 1. 關聯性設計：使用 @ManyToOne 與 SellerProfile 連結，落實賣家與商品的層級關係。
 * 2. 金流精度控制：採用 BigDecimal 避免浮點數運算造成的金額誤差。
 * 3. 性能優化：關聯欄位採用 Lazy Loading (懶加載) 避免不必要的資料查詢。
 */
@Entity
@Table(name = "products")
@Getter // 透過 Lombok 自動產生所有欄位的取值方法
@Setter // 透過 Lombok 自動產生所有欄位的賦值方法
@NoArgsConstructor // 提供 JPA 所需的無參數建構子
@AllArgsConstructor // 提供全參數建構子，便於測試與開發
public class Product {

    /**
     * 資料庫內部主鍵 (Primary Key)
     * 使用 IDENTITY 策略，由資料庫 (PostgreSQL) 自動遞增生成。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // 🔒 安全保護：主鍵不可透過外部 Setter 修改
    private Long id;

    /**
     * 對外顯示的安全識別碼 (UUID)
     * 🚀 PPT 亮點：實作「隱性存取」策略。
     * 理由：前端 API 溝通一律使用 UUID，防止駭客透過規律的 ID (如 1, 2, 3) 進行遍歷攻擊 (ID Enumeration)。
     */
    @Column(unique = true, nullable = false)
    @Setter(AccessLevel.NONE) // 🔒 UUID 應為唯讀，確保唯一性與一致性
    private UUID externalId = UUID.randomUUID();

    /**
     * 核心關聯：所屬賣家 (Seller Profile)
     * 🚀 FetchType.LAZY：延遲加載。只有在真正需要用到賣家資料時才去資料庫抓取，提升效能。
     * @ManyToOne：多個商品可以屬於同一個賣家。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private SellerProfile seller;

    /** 商品名稱 */
    @Column(nullable = false)
    private String name;

    /** 商品分類 (例如：electronics, photography 等) */
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    /** 商品狀況 (例如：new, like_new, good, fair) */
    @Enumerated(EnumType.STRING)
    private ProductCondition productCondition;

    /**
     * 售價 (BigDecimal)
     * 🚀 專業點：precision=10 代表總共 10 位數，scale=2 代表小數點後兩位。
     * 理由：在電商系統中，使用 double 會導致運算誤差，BigDecimal 是金流開發的業界標配。
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** 庫存數量 */
    @Column(nullable = false)
    private Integer stock;

    /** 商品詳細描述 (使用 TEXT 儲存較長字串) */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * * 商品目前狀態
     * 預設值為 ON_SHELF (上架中)，可用於後續實作下架或售完功能。
     */
    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ON_SHELF;

    /** 封面圖片網址 (暫存單一網址) */
    private String imageUrl;

    /**
     * 審計欄位：商品上架時間
     * 由 Hibernate 自動於插入資料時生成，不可由前端修改 (updatable = false)。
     */
    @CreationTimestamp
    @Column(updatable = false)
    @Setter(AccessLevel.NONE) // 🔒 上架時間應由系統自動產生，不可手動篡改
    private LocalDateTime createdAt;
}