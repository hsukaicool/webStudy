package com.app.mysecureapp.model.Enum;



public enum ProductCondition {
    NEW("全新"),
    LIKE_NEW("九成新"),
    GOOD("八成新"),
    FAIR("七成新");

    private final String displayValue;

    // 建構子
    ProductCondition(String displayValue) {
        this.displayValue = displayValue;
    }

    // 取得中文描述的方法（可用於前端顯示或報表）
    public String getDisplayValue() {
        return displayValue;
    }
}