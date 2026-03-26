package com.app.mysecureapp.dto;

// === 1. Java 標準函式庫 ===
import jakarta.validation.constraints.*; // 🚀 引入驗證工具
import java.math.BigDecimal; // 🚀 關鍵技術：處理金流資料的標準型態，避免浮點數誤差
import java.util.List;       // 🚀 容器：用於存取多張商品圖片的網址列表
import com.app.mysecureapp.model.Enum.ProductCategory; // 🚀 引入 Enum
import com.app.mysecureapp.model.Enum.ProductCondition;

/**
 * 商品新增請求物件 (Product Request DTO)
 * 🚀 技術選型：使用 Java Record (Java 14+ 新特性)
 * * 為什麼用 Record 而不是 Class？ (PPT 亮點)：
 * 1. 不可變性 (Immutability)：DTO 在傳輸過程中不應被修改，Record 天生支持此特性。
 * 2. 簡潔性：自動生成建構子、Getter、equals、hashCode 與 toString，減少樣板代碼。
 * 3. 語意清晰：明確告訴其他開發者，這只是一個「純粹的資料載體」。
 * * 對接說明：此結構精確對應 React 前端 AddProduct.jsx 中的 formData 狀態。
 * // 🚀 關鍵修改：將 String 改為 ProductCategory (Enum)
 */
public record ProductRequest(

        /** 商品名稱：對應前端 input[name='name'] */
        @NotBlank(message = "商品名稱不能為空")
        @Size(max = 100, message = "商品名稱太長了")
        String name,

        /** 商品分類：對應前端 select 中的 electronics, photography, furniture 等值 */
        @NotBlank(message = "必須選擇分類")
        ProductCategory category,

        /** 商品狀況：對應前端 condition (new, like_new, good, fair) */
        @NotBlank(message = "必須選擇商品狀況")
        ProductCondition condition,

        /** * 商品售價
         * 🚀 專業點：前端傳入數字字串，後端自動轉為 BigDecimal。
         * 理由：確保從接收資料的那一刻起，所有金額運算都處於高精度保護下。
         */
        @NotNull(message = "售價不能為空")
        @DecimalMin(value = "0.0", inclusive = false, message = "售價必須大於 0")
        BigDecimal price,

        /** 庫存數量：對應前端 stock 欄位 */
        @NotNull(message = "庫存不能為空")
        @Min(value = 1, message = "庫存至少要 1 件")
        Integer stock,

        /** 商品詳細描述：對應前端 textarea 內容 */
        @Size(max = 2000, message = "描述字數過多")
        String description,

        /** * 商品圖片網址列表
         * 🚀 彈性設計：支援多圖上傳。
         * 實作邏輯：後端 Service 會抓取列表中的第一個元素作為商品主圖 (Cover Image)。
         */
        List<String> imageUrls
) {}