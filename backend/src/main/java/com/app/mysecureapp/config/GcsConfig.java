package com.app.mysecureapp.config;

// === 1. Google Cloud 驗證與儲存 SDK ===
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

// === 2. Spring Framework 核心標註 ===
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// === 3. Spring 資源加載與 Java IO ===
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;

/**
 * 【GCP 雲端儲存配置類 (GCS Configuration)】
 * 🚀 職責：作為外部 SDK 的「工廠」，手動將 Google Storage 實例註冊為 Spring Bean。
 * * 技術亮點：
 * 1. 控制反轉 (IoC)：將第三方 Storage 對象納入 Spring 容器管理，實現全局單例。
 * 2. 外部化配置：透過 @Value 動態讀取環境變數，達成代碼與憑證路徑的解耦。
 * 3. 資源映射技術：利用 ClassPathResource 解決專案打包後金鑰檔案定位失敗的常見痛點。
 */
@Configuration // 🚀 標註為配置類：Spring Boot 啟動時會優先掃描此類別以建立必要的連線物件
public class GcsConfig {

    /** 🚀 金鑰檔案路徑：對應 application.properties 中的 gcp.storage.credentials-path */
    @Value("${gcp.storage.credentials-path}")
    private String credentialsPath;

    /** 🚀 GCP 專案識別碼：對應 application.properties 中的 gcp.storage.project-id */
    @Value("${gcp.storage.project-id}")
    private String projectId;

    /**
     * 【建立 Storage 實例 (Bean Production)】
     * 🚀 邏輯：讀取 JSON 金鑰 -> 執行雲端握手驗證 -> 回傳受管理的儲存工具物件。
     * @return 具備操作權限的 Google Cloud Storage 物件
     * @throws IOException 當金鑰檔案讀取失敗或格式錯誤時拋出
     */
    @Bean // 🚀 宣告為 Bean：讓 Spring 容器接管此對象，使其可被 @Autowired 到 Service 中
    public Storage storage() throws IOException {

        // 1. 🚀 驗證憑證載入 (Authentication)
        // 使用 ClassPathResource 從 resources 目錄中動態獲取金鑰檔案的輸入流。
        // replace("classpath:", "") 確保能正確解析 Spring 的路徑字串。
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ClassPathResource(credentialsPath.replace("classpath:", "")).getInputStream()
        );

        // 2. 🚀 客戶端初始化 (Client Initialization)
        // 透過 StorageOptions 的 Builder 模式，配置專案 ID 與驗證憑證。
        // build().getService() 是正式與 Google API 建立連線並取得服務實例的關鍵步驟。
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .getService();
    }
}