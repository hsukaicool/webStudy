package com.app.mysecureapp.controller;

import com.app.mysecureapp.dto.MessageDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// 測試用

// 這是一個 RESTful API 的控制器
@RestController
@RequestMapping() // 所有在這個 Controller 裡的路徑都會以 /api 開頭
public class AllController {

    // 當前端呼叫 GET /api/hello 時，這個方法會被執行
    @GetMapping("/hello")
    public MessageDto sayHello() {
        // 回傳一個 DTO 物件，Spring Boot 會自動將其轉換為 JSON
        return new MessageDto("Hello from the backend!");
    }
}