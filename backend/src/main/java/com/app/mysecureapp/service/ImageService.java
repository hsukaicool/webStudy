package com.app.mysecureapp.service;

import com.app.mysecureapp.model.ImageFile;
import com.app.mysecureapp.repository.ImageFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 圖片上傳服務 (Service Layer)
 * 負責處理圖片上傳的業務邏輯，包括儲存檔案到磁碟和記錄資訊到資料庫。
 */
@Service
public class ImageService {

    // 注入 Repository，用來操作資料庫
    private final ImageFileRepository imageFileRepository;

    // 從 application.properties 中讀取檔案儲存路徑
    // 預設值為 "uploads"，如果沒設定的話
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Autowired // 透過建構子注入 Repository
    public ImageService(ImageFileRepository imageFileRepository) {
        this.imageFileRepository = imageFileRepository;
    }

    /**
     * 儲存上傳的檔案
     *
     * @param file 前端上傳的檔案 (MultipartFile)
     * @return 儲存後的 ImageFile 實體 (包含資料庫 ID)
     * @throws IOException 如果檔案寫入失敗
     */
    public ImageFile storeFile(MultipartFile file) throws IOException {
        // 1. 取得原始檔名 (例如 "my-photo.jpg")
        // StringUtils.cleanPath 可以防止路徑遍歷攻擊 (Path Traversal)
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new IOException("檔名無效！");
        }
        originalFileName = StringUtils.cleanPath(originalFileName);

        // 2. 產生一個唯一的檔名，避免重複覆蓋 (例如 "uuid-my-photo.jpg")
        String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;

        // 3. 確保上傳目錄存在，如果不存在就建立
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 4. 將檔案寫入到磁碟
        // resolve() 用來組合路徑，例如 "uploads/uuid-my-photo.jpg"
        Path targetLocation = uploadPath.resolve(uniqueFileName);
        // copy() 將 InputStream 複製到目標路徑，如果有同名檔案則覆蓋
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // 5. 建立 ImageFile 實體物件，準備存入資料庫
        // 這裡我們存的是「相對路徑」或者是「檔名」，方便之後 Controller 組合 URL
        ImageFile imageFile = new ImageFile();
        imageFile.setFileName(originalFileName);
        imageFile.setFilePath(uniqueFileName); // 這裡存的是磁碟上的檔名
        imageFile.setContentType(file.getContentType());
        imageFile.setSize(file.getSize());
        imageFile.setUploadTime(java.time.LocalDateTime.now());

        // 6. 呼叫 Repository 將資訊存入資料庫
        return imageFileRepository.save(imageFile);
    }
}