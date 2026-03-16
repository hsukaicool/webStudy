package com.app.mysecureapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 最一開始啟動的地方 main是初始運行點
@SpringBootApplication
public class MySecureAppApplication {
    public static void main(String[] args) {
        // 忽略 macOS 在外接硬碟上產生的 ._ 檔案，避免 ClassFormatException
        System.setProperty("spring.classformat.ignore", "true");
        SpringApplication.run(MySecureAppApplication.class, args);
    }
}