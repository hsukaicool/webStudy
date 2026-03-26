package com.app.mysecureapp.service;

// --- 1. 模型層 (Model/Entity) 引入 ---
import com.app.mysecureapp.dto.UserProfileRequest;
// 引入 User，因為建立個人檔案時必須知道是「誰」的檔案
import com.app.mysecureapp.model.User;
// 引入 UserProfile，這是我們要操作的主體物件
import com.app.mysecureapp.model.UserProfile;

// --- 2. 持久層 (Repository) 引入 ---
// 引入 Repository，這是真正跟資料庫通訊的介面
import com.app.mysecureapp.repository.UserProfileRepository;
import com.app.mysecureapp.repository.UserRepository;

// --- 3. Spring 框架相關引入 ---
// 標記這是一個 Service 元件，交給 Spring 容器管理
import org.springframework.stereotype.Service;
// 確保資料操作的「原子性」，要麼全部成功，要麼全部失敗 (回滾)
import org.springframework.transaction.annotation.Transactional;
// 處理相依性注入 (Dependency Injection)
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 【個人檔案服務層】
 * 功能：串接 User 與 UserProfile 兩大模型，處理業務邏輯。
 * 主要是傳email不能改動的部分給前端的 會員資料
 * 亮點：在註冊時自動初始化檔案，並確保資料的一致性。
 */
@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final com.app.mysecureapp.repository.UserRepository userRepository; // 🚀 注入 User 的倉庫

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository, UserRepository userRepository) {
        // 1. 透過 UUID 找到對應的 Profile (這裡會用到你之前寫的查詢方法)
        this.userProfileRepository = userProfileRepository;

        // 2. 從 Profile 取得關聯的 User 實體 (因為有一對一關聯)
        this.userRepository = userRepository;
    }

    /**
     * 【核心功能：建立預設個人檔案】
     * 當一個新 User 被建立後，呼叫此方法來完成「一對一串接」。
     * 跟新增帳號的時候用的
     * * @param user 已經存入資料庫並擁有 ID 的 User 物件
     */
    @Transactional // 🚀 啟動事務，確保 UserProfile 儲存失敗時不會產生孤兒資料
    public void createDefaultProfile(User user) {
        // 1. 實例化 UserProfile 模型 (Entity)
        UserProfile emptyProfile = new UserProfile();

        // 2. 【關鍵串接點】建立一對一雙向關聯
        // 將 User 傳入 Profile 中，這會在資料庫的 user_profiles 表產生一個 user_id 的外鍵 (FK)
        emptyProfile.setUser(user);

        // 3. 初始化預設值 (防止前端讀取時出現 null 導致 UI 崩潰)
        // 根據你提供的 Model，我們填充對應欄位
        emptyProfile.setBio("這傢伙很懶，什麼都沒留下。"); // 預設自介
        emptyProfile.setLocation("地球");               // 預設地點
        emptyProfile.setPhoneNumber("");              // 預設電話留空，等使用者填寫
        emptyProfile.setAvatarUrl("/images/default-avatar.png"); // 預設頭像路徑

        // 4. 設定性別為保密 (使用你在 Model 裡面定義的 Enum)
        emptyProfile.setGender(UserProfile.Gender.SECRET);

        // 5. 透過 Repository 將整合好的資料存入資料庫
        userProfileRepository.save(emptyProfile);

        // 📝 PPT 筆記：此時資料庫會自動觸發 @CreationTimestamp 記錄建立時間
    }

    /**
     * 【查詢功能：獲取完整檔案】
     * 串接點：透過 User 的外部唯一 ID (UUID) 來尋找對應的個人檔案
     */
    public UserProfile getUserProfileByUuid(java.util.UUID userUuid) {
        // 這邊會呼叫 Repository 透過 externalId 來搜尋
        return userProfileRepository.findByUserExternalId(userUuid)
                .orElseThrow(() -> new RuntimeException("找不到該 UUID 對應的個人檔案"));
    }

    /**
     * 【更新功能：修改個人檔案與顯示名稱】
     * 亮點：實作「跨實體事務更新」，確保名字與資料同步成功。
     */
    @Transactional // 🚀 關鍵：確保 User 和 UserProfile 要麼同時存成功，要麼同時失敗
    public UserProfile updateProfileByUuid(java.util.UUID userUuid, UserProfileRequest request) {
        // 1. 透過 UUID 找到對應的 Profile (這裡會用到你之前寫的查詢方法)
        UserProfile profile = getUserProfileByUuid(userUuid);

        // 2. 從 Profile 取得關聯的 User 實體 (因為有一對一關聯)
        User user = profile.getUser();

        // 🚀 3. 更新 User 實體的內容 (來自 DTO 的 displayName)
        // 這樣使用者改暱稱時，資料庫的 users 表也會跟著動
        if (request.displayName() != null) {
            user.setDisplayName(request.displayName());
        }
        userRepository.save(user); // 存入 users 表

        // 4. 更新 UserProfile 實體的內容
        profile.setPhoneNumber(request.phoneNumber());
        profile.setBio(request.bio());
        profile.setLocation(request.location());
        profile.setBirthday(request.birthday());
        profile.setGender(request.gender());

        // 5. 儲存 Profile 並回傳
        // 📝 PPT 筆記：此時 Hibernate 會自動處理 SQL 的 UPDATE 語法
        return userProfileRepository.save(profile);
    }
}
