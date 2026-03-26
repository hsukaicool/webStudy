package com.app.mysecureapp.model.Enum;

/**
 * 商品狀態枚舉 (Product Status)
 * 🚀 PPT 亮點：狀態機管理，確保商品在「上架、下架、售罄」之間邏輯嚴謹。
 */
public enum ProductStatus {
    ON_SHELF("上架中"),
    OFF_SHELF("已下架"),
    SOLD_OUT("已售完");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}