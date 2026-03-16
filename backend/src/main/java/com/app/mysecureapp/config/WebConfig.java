package com.app.mysecureapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 針對所有以 /api/ 開頭的網址
        registry.addMapping("/**") // 允許所有路徑
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173") // 允許的前端網址 (React)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允許的方法
                .allowedHeaders("*") // 允許所有 Header
                .allowCredentials(true); // 允許攜帶 Cookie (Session 驗證需要)
    }
}