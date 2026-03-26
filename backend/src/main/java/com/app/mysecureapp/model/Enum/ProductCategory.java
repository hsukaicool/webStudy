package com.app.mysecureapp.model.Enum;

/**
 * 商品分類枚舉 (Type-Safe Category)
 * 🚀 PPT 亮點：確保資料一致性，避免手動輸入拼錯字或惡意竄改。
 */
public enum ProductCategory {
    ELECTRONICS("電子產品"),
    PHOTOGRAPHY("攝影器材"),
    FURNITURE("家具"),
    CLOTHING("服飾配件"),
    BOOKS("書籍文具"),
    OTHER("其他");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}


