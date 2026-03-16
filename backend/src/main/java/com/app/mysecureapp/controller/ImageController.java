package com.app.mysecureapp.controller;

import com.app.mysecureapp.model.ImageFile;
import com.app.mysecureapp.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
// 大型圖片資料庫
/**
 * 圖片上傳 API 接口 (Controller Layer)
 * 提供對外的 HTTP 端點，讓前端可以上傳圖片。*/
 @RestController
 @RequestMapping("/api/images")


public class ImageController {

    private final ImageService imageService;

   // 透過建構子注入 ImageService
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * 上傳圖片 API
     * POST /api/images/upload
     *
     * @param file 前端表單中 input name="file" 的檔案
     * @return 上傳成功後回傳圖片資訊 (JSON)
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 呼叫 Service 儲存檔案
            ImageFile savedFile = imageService.storeFile(file);
            // 回傳 200 OK 和儲存後的檔案資訊
            // 這裡可以選擇回傳完整的 Entity，或者只回傳 ID 和 URL
            return ResponseEntity.ok(savedFile);

         } catch (IOException e) {
            // 如果發生 IO 錯誤 (例如磁碟滿了、權限不足)，回傳 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("圖片上傳失敗: " + e.getMessage());
        }
    }
}

