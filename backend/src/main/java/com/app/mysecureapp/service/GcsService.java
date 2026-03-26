package com.app.mysecureapp.service;

// === 1. Google Cloud Storage SDK 引入 ===
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

// === 2. Spring Framework 核心功能 ===
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// === 3. Web 檔案處理與 Java 標準庫 ===
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

/**
 * 【GCP 雲端儲存業務邏輯層】
 * 職責：封裝 Google Cloud Storage (GCS) 的底層操作，提供簡單的檔案上傳接口。
 * 🚀 技術亮點：
 * 1. 雲端 SDK 整合：直接使用 Google 官方 Storage 客戶端進行高效通訊。
 * 2. 外部化配置 (@Value)：動態讀取 application.properties 中的儲存桶名稱，提升環境遷移靈活性。
 * 3. 非破壞性命名策略：結合 UUID 與原始檔名，確保雲端檔案唯一性，杜絕覆蓋風險。
 */
@Service
public class GcsService {

    /** 核心儲存對象：由 GcsConfig 中定義的 Bean 自動注入 */
    private final Storage storage;

    /** 儲存桶名稱：從配置檔案中獲取，例如 "web_my_images" */
    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    /**
     * 🚀 技術細節：建構子注入 (Constructor Injection)
     * 優點：有利於單元測試，且確保 Storage 對象在 Service 初始化時即處於就緒狀態。
     */
    public GcsService(Storage storage) {
        this.storage = storage;
    }

    /**
     * 【執行圖片上傳】
     * 🚀 邏輯：接收位元組流 -> 生成唯一標識 -> 設定 Metadata -> 寫入雲端 -> 返回路徑
     * @param file 前端傳來的二進位圖片檔案 (MultipartFile)
     * @return 儲存在雲端後的公開存取網址 (URL)
     * @throws IOException 當檔案讀取或雲端寫入失敗時拋出異常
     */
    public String uploadImage(MultipartFile file) throws IOException {

        // 1. 🚀 命名策略：UUID + 原檔名 (Collision Avoidance)
        // 理由：避免多使用者同時上傳相同名稱的檔案（如 image.jpg）導致資料被覆蓋。
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        // 2. 定義在雲端儲存的位置 (物件識別碼)
        // BlobId 包含兩個關鍵資訊：目標儲存桶 (Bucket) 與 檔案名稱 (Name)
        BlobId blobId = BlobId.of(bucketName, fileName);

        // 3. 設定檔案屬性 (Metadata)
        // 🚀 亮點：手動設定 Content-Type，確保瀏覽器打開網址時能正確渲染圖片，而非直接下載。
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        // 4. 執行雲端寫入操作
        // 將 MultipartFile 的 Byte 陣列直接推送到 Google Cloud 伺服器
        storage.create(blobInfo, file.getBytes());

        // 5. 🚀 構造並回傳公開訪問 URL
        // 符合 Google Cloud Storage 標準網址格式，方便前端 React 直接作為 <img> 標籤的 src
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }
}