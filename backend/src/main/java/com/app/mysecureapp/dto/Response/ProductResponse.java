package com.app.mysecureapp.dto.Response;

// === 1. Java 標準函式庫 ===
import com.app.mysecureapp.model.Enum.ProductCategory;
import com.app.mysecureapp.model.Enum.ProductCondition;
import com.app.mysecureapp.model.Enum.ProductStatus;

import java.math.BigDecimal; // 🚀 關鍵技術：高精度金額表示，確保電商系統金流精確性
import java.time.LocalDateTime;
import java.util.UUID;        // 🚀 安全規範：用於替代資料庫遞增 ID 的唯一識別碼

/**
 * 商品回應物件 (Product Response DTO)
 * 🚀 技術選型：使用 Java Record (Java 14+) 實作。
 * * 職責說明：
 * 1. 數據過濾：僅從資料庫 Product Entity 提取前端「需要顯示」的欄位。
 * 2. 資安防護：透過 externalId (UUID) 隱藏資料庫內部的 Long ID，防止 ID 遍歷攻擊。
 * 3. 唯讀傳輸：Record 的特性確保了資料在從 Service 傳送到 Controller 的過程中不會被篡改。
 */

//record 專門用於「只攜帶資料」的物件
public record ProductResponse(

        /** * 🚀 安全識別碼 (External ID)
         * 對應資料庫中的 UUID。前端在執行「查看詳情」或「編輯」時，皆以此 ID 作為 API 路徑參數。
         */
        UUID externalId,

        /** 商品名稱：例如 "Sony WH-1000XM4" */
        String name,

        /** 商品分類：electronics, photography, furniture 等 */
        ProductCategory category,

        /** 商品狀況：new (全新), like_new (二手極新) 等 */
        ProductCondition condition,

        /** * 商品售價
         * 🚀 專業點：採用 BigDecimal 格式回傳。
         * 理由：保證前端拿到的金額不會因為 JSON 解析或浮點數運算出現「.9999」的誤差。
         */
        BigDecimal price,

        /** 目前庫存數量 */
        Integer stock,

        /** 商品詳細文字描述 */
        String description,

        /** * 商品狀態 (Status)
         * 例如：ON_SHELF (上架中), OFF_SHELF (已下架), SOLD_OUT (售完)。
         */
        ProductStatus status,

        /** * 商品封面圖網址
         * 🚀 實作細節：Service 層會從圖片清單中自動選取第一張作為主圖。
         */
        String imageUrl,

        /** * 商品上架時間
         * 🚀 審計亮點：由 @CreationTimestamp 自動生成，供前端顯示「上架於 X 分鐘前」。
         */
        LocalDateTime createdAt
) {}