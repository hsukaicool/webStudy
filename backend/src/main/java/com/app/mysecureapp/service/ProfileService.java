package com.app.mysecureapp.service;

// === 1. 專案內部的資料傳輸物件 (DTO) ===
import com.app.mysecureapp.dto.UserProfileRequest;

// === 2. 專案內部的實體類別 (Entity) ===
import com.app.mysecureapp.model.User;
import com.app.mysecureapp.model.UserProfile;

// === 3. 專案內部的資料庫操作層 (Repository) ===
import com.app.mysecureapp.repository.UserProfileRepository;
import com.app.mysecureapp.util.FileUploadUtil; // 🚀 引入你的通用搬運工

// === 4. Spring 核心與依賴注入 ===
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// === 5. Spring 事務管理 ===
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; // 🚀 用於接收前端傳來的圖片檔案

// === 6. Java 原生標準函式庫 ===
import java.io.IOException;
import java.util.UUID;



/**
 * 個人檔案業務邏輯層 (Profile Service)
 * 🚀 技術亮點：
 * 1. 配合 Lombok 自動化技術：利用 Model 層生成的存取方法，保持業務邏輯代碼精簡。
 * 2. 雙 ID 安全架構：所有查詢皆透過 UUID (External ID) 執行，物理隔絕資料庫主鍵。
 * 3. 冪等性局部更新：確保 API 僅針對變更欄位進行覆寫，保障資料一致性。
 */
@Service
public class ProfileService {

    // final 關鍵字確保依賴物件在初始化後不可變，提升系統穩定性
    private final UserProfileRepository userProfileRepository;
    private final UserQueryService userQueryService;
    private final FileUploadUtil fileUploadUtil;


    /**
     * 建構子注入 (Constructor Injection)
     * 這是 Spring 官方推薦的模式，比起 @Autowired 欄位注入，
     * 此方式能確保組件在測試時能被輕鬆模擬 (Mock)，並保證 Bean 的完整性。
     */
    @Autowired
    public ProfileService(UserProfileRepository userProfileRepository,
                          UserQueryService userQueryService,
                          FileUploadUtil fileUploadUtil) {
        this.userProfileRepository = userProfileRepository;
        this.userQueryService = userQueryService;
        this.fileUploadUtil = fileUploadUtil;
    }

    /**
     * 初始化個人檔案 (系統自動化流程)
     * 🚀 使用場景：當新使用者註冊成功後，由 UserService 觸發此內部邏輯。
     * @param user 已持久化的 User 實體
     */
    @Transactional
    public void createDefaultProfile(User user) {
        // 這裡調用的 setter 方法 (setUser, setBio...) 皆由 Lombok 在編譯時自動產生
        UserProfile emptyProfile = new UserProfile();

        // 建立與 User 的一對一關聯 (Foreign Key 綁定)
        emptyProfile.setUser(user);

        // 初始化預設值，優化前端 UI 顯示邏輯，避免 null 導致的頁面崩潰
        emptyProfile.setBio("");
        emptyProfile.setLocation("未設定");
        emptyProfile.setPhoneNumber("");
        emptyProfile.setAvatarUrl("");

        userProfileRepository.save(emptyProfile);
    }

    /**
     * 【頭像上傳更新】
     * 🚀 邏輯：識別用戶 -> 上傳 GCS -> 更新資料庫 URL -> 回傳網址
     * @param userUuid 來自 JWT 的使用者識別碼
     * @param file 前端傳來的二進位圖片
     * @return 儲存在雲端的公開網址
     */
    @Transactional // 🚀 確保資料庫更新與雲端路徑寫入的原子性
    public String updateAvatar(UUID userUuid, MultipartFile file) throws IOException {

        // 1. 取得現有的個人檔案實體
        UserProfile profile = userProfileRepository.findByUserExternalId(userUuid)
                .orElseThrow(() -> new RuntimeException("系統錯誤：找不到該使用者的個人檔案"));

        // 2. 🚀 呼叫通用工具類
        // 指定資料夾路徑為 "avatars/"，工具會自動幫你加上 UUID 檔名
        String newAvatarUrl = fileUploadUtil.uploadFile(file, "avatars/");

        // 3. 更新資料庫中的頭像網址欄位
        profile.setAvatarUrl(newAvatarUrl);

        // 4. 持久化異動 (利用 Dirty Checking 自動更新)
        userProfileRepository.save(profile);

        return newAvatarUrl;
    }

    /**
     * 取得個人檔案 (API 安全存取)
     * @param userUuid 來自 JWT Token 的安全識別碼
     */
    public UserProfile getUserProfileByUuid(UUID userUuid) {
        // 複用 UserQueryService 的統一查詢邏輯，減少重複代碼 (DRY Principle)
        User user = userQueryService.findUserByUuid(userUuid);

        // 透過外部 UUID 進行個人檔案檢索
        return userProfileRepository.findByUserExternalId(userUuid)
                .orElseThrow(() -> new RuntimeException("系統錯誤：找不到該使用者的個人檔案"));
    }

    /**
     * 更新個人檔案 (防禦性局部更新模式)
     * 🚀 專業點：實作了嚴謹的 Null-Check 驗證。
     * @param userUuid 使用者唯一識別碼
     * @param request 包含變更內容的 DTO
     */
    @Transactional
    public UserProfile updateProfileByUuid(UUID userUuid, UserProfileRequest request) {

        // 1. 取得受管理狀態的實體物件
        User user = userQueryService.findUserByUuid(userUuid);
        UserProfile profile = userProfileRepository.findByUserExternalId(userUuid)
                .orElseThrow(() -> new RuntimeException("系統錯誤：找不到該使用者的個人檔案"));

        // 2. 執行「非破壞性」更新
        // 只有前端明確傳送的欄位會被修改，未傳送的欄位將維持資料庫現狀。
        // 這展現了對「數據真實性」的極致追求，避免了傳統全覆蓋更新導致的資料丟失。

        if (request.displayName() != null) {
            user.setDisplayName(request.displayName());
        }

        if (request.phoneNumber() != null) {
            profile.setPhoneNumber(request.phoneNumber());
        }

        if (request.bio() != null) {
            profile.setBio(request.bio());
        }

        if (request.location() != null) {
            profile.setLocation(request.location());
        }

        if (request.birthday() != null) {
            profile.setBirthday(request.birthday());
        }

        if (request.gender() != null) {
            profile.setGender(request.gender());
        }

        // 3. 持久化異動
        // 由於處於 @Transactional 事務中，JPA 的 Dirty Checking 機制會自動同步狀態，
        // 呼叫 save() 則是為了讓邏輯流向更顯性化，方便團隊協作閱讀。
        return userProfileRepository.save(profile);
    }
}