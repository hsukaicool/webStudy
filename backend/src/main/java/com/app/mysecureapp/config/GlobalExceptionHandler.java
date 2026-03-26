package com.app.mysecureapp.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

/**
 * 【全域異常處理器】 (Global Exception Handler)
 * 🚀 技術點：利用 AOP (面向切面編程) 思想，實現對所有 Controller 的統一錯誤管理。
 * 🚀 職責：當系統發生錯誤或驗證不通過時，由該類別統一「接手」並處理成標準格式。
 */
@RestControllerAdvice // 🚀 關鍵標註：代表這個類別是全專案的「報錯監聽器」，所有 RestController 噴出的錯誤都會經過它。
public class GlobalExceptionHandler {

    /**
     * 【處理驗證失敗異常】
     * 🚀 觸發條件：當 Controller 的參數前面有加上 @Valid，且前端傳入的數據違反了 DTO 上的限制（如 @NotBlank, @Min）時，
     * 系統會拋出 MethodArgumentNotValidException。
     * * @param ex 系統拋出的原始異常物件 (裡面包含所有錯誤細節)
     * @return 封裝後的 ResponseEntity (狀態碼為 400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class) // 🚀 指定「只抓」驗證失敗的錯誤
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        // 建立一個 Map (像是一本小字典)，準備存放「哪個欄位錯了」與「錯誤訊息是什麼」
        Map<String, String> errors = new HashMap<>();

        // 🚀 核心邏輯：
        // 1. ex.getBindingResult().getFieldErrors()：從異常中抓取所有出錯的欄位 (例如 price, name)
        // 2. .forEach(...)：對每個錯誤進行掃描
        ex.getBindingResult().getFieldErrors().forEach(error ->
                // 3. 把「欄位名稱」當 Key，「提示訊息」當 Value 存進 Map 裡
                // 例如：Key="price", Value="售價不能為負數"
                errors.put(error.getField(), error.getDefaultMessage())
        );

        // 🚀 回傳 400 Bad Request，並把這個 Map 轉成漂亮的 JSON 送回給 React
        // 前端 React 看到的會是：{ "price": "售價不能為負數", "name": "名稱不能為空" }
        return ResponseEntity.badRequest().body(errors);
    }
}