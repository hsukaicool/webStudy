package com.app.mysecureapp.model;

import jakarta.persistence.*;
        import java.time.LocalDateTime;

/**
 * 圖片檔案的實體類別 (Entity)
 * 對應資料庫中的 "image_files" 資料表
 * 用來記錄上傳圖片的資訊，例如檔名、儲存路徑和上傳時間。
 */
@Entity
@Table(name = "image_files") // 指定資料表名稱
public class ImageFile {

    // 主鍵 ID，由資料庫自動產生 (Auto Increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 原始檔案名稱 (例如: "my-photo.jpg")
    @Column(nullable = false) // 不允許為空
    private String fileName;

    // 檔案儲存路徑 (例如: "uploads/my-photo.jpg" 或完整 URL)
    @Column(nullable = false) // 不允許為空
    private String filePath;

    // 檔案類型 (ContentType, 例如: "image/jpeg", "image/png")
    private String contentType;

    // 檔案大小 (單位: bytes)
    private Long size;

    // 上傳時間，預設為當前時間
    private LocalDateTime uploadTime;

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    // 必須要有一個無參數建構子 (JPA 規範)
    public ImageFile() {
    }

    // 建構子：方便我們在 Service 層建立物件時使用
    public ImageFile(String fileName, String filePath, String contentType, Long size) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.contentType = contentType;
        this.size = size;
        this.uploadTime = LocalDateTime.now(); // 自動設定為當前時間
    }

    // --- Getters (取得資料) ---

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getSize() {
        return size;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    // --- Setters (修改資料) ---
    // ID 通常不需要 Setter，因為是由資料庫產生的

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    // uploadTime 通常在建立時就決定了，也不太需要 Setter，除非有特殊需求
}