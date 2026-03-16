package com.app.mysecureapp.dto;

/**
 * 一個用於 API 資料傳輸的物件 (Data Transfer Object).
 * 使用 Java Record 來簡化程式碼，它會自動產生建構子、getter、equals、hashCode 和 toString 方法。
 * @param message 要傳遞的訊息
 */
public record MessageDto(String message) {
}