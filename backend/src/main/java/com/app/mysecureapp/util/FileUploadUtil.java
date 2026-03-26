package com.app.mysecureapp.util;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 【雲端檔案上傳工具】
 * 🚀 職責：統一全專案的檔案上傳邏輯，支援 GCS 儲存。
 */
@Component
public class FileUploadUtil {

    private final Storage storage;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    public FileUploadUtil(Storage storage) {
        this.storage = storage;
    }

    /**
     * 通用上傳方法
     * @param file 檔案
     * @param folder 雲端資料夾路徑 (例如 "avatars/" 或 "products/")
     * @return 公開網址
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) return null;

        // 1. 生成唯一檔名：folder + UUID + 原檔名
        String fileName = folder + UUID.randomUUID() + "-" + file.getOriginalFilename();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        // 2. 執行上傳
        storage.create(blobInfo, file.getBytes());

        // 3. 回傳網址
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }
    /**
     * 🚀 根據網址刪除 GCS 上的檔案
     * @param fileUrl 資料庫存的完整網址
     */
    public void deleteFileByUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.contains(bucketName)) return;

        try {
            // 1. 從網址中提取檔名 (例如 products/uuid-name.jpg)
            // 網址格式通常是: https://storage.googleapis.com/bucket-name/folder/filename
            String fileName = fileUrl.substring(fileUrl.lastIndexOf(bucketName + "/") + bucketName.length() + 1);

            // 2. 執行刪除
            BlobId blobId = BlobId.of(bucketName, fileName);
            boolean deleted = storage.delete(blobId);

            if (deleted) {
                System.out.println("GCS 檔案刪除成功: " + fileName);
            }
        } catch (Exception e) {
            System.err.println("GCS 檔案刪除失敗: " + e.getMessage());
        }
    }
}