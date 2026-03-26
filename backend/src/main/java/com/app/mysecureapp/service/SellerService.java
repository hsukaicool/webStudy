package com.app.mysecureapp.service;

// === 1. 專案內部模型與 DTO ===
import com.app.mysecureapp.dto.SellerRequest;
import com.app.mysecureapp.model.SellerProfile;
import com.app.mysecureapp.model.User;
import com.app.mysecureapp.repository.SellerProfileRepository;

// === 通用工具與 Web 檔案處理 ===
import com.app.mysecureapp.util.FileUploadUtil; // 🚀 注入你的搬運工
import org.springframework.web.multipart.MultipartFile;

// === 2. Spring 核心框架組件 ===
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// === 3. Java 標準函式庫 ===
import java.io.IOException;
import java.util.UUID;

/**
 * 賣家業務邏輯層 (Seller Service)
 * 🚀 職責：管理賣家身分的生命週期，包括自動化開通、資料查詢與防禦性更新。
 */
@Service
public class SellerService {

    private final SellerProfileRepository sellerRepository;
    private final UserQueryService userQueryService; // 複用現有的使用者查詢雷達
    private final FileUploadUtil fileUploadUtil;

    @Autowired
    public SellerService(SellerProfileRepository sellerRepository,
                         UserQueryService userQueryService,
                         FileUploadUtil fileUploadUtil) {
        this.sellerRepository = sellerRepository;
        this.userQueryService = userQueryService;
        this.fileUploadUtil = fileUploadUtil;
    }

    /**
     * 🚀 智慧開通賣家功能 (Get or Create 模式)
     * 此設計符合「冪等性」，確保前端重複觸發按鈕時，系統不會建立重複資料。
     * * @param userUuid 來自 SecurityUtil 的安全識別碼
     * @param shopName 使用者預設店名 (若為空則由系統生成)
     * @return 賣家實體 (不論是新建立的或既有的)
     */
    @Transactional
    public SellerProfile getOrCreateSeller(UUID userUuid, String shopName) {
        // 1. 透過 UUID 尋人雷達確認身分
        User user = userQueryService.findUserByUuid(userUuid);

        // 2. 執行「偵測並處理」邏輯
        return sellerRepository.findByUser(user)
                .map(existingSeller -> {
                    // --- 狀況 A：該用戶已經是賣家 ---
                    return existingSeller; // 直接回傳既有資料，不重複創立
                })
                .orElseGet(() -> {
                    // --- 狀況 B：該用戶第一次點擊成為賣家 ---
                    SellerProfile newSeller = new SellerProfile();
                    newSeller.setUser(user);

                    // 設定預設值：若沒傳店名，則用「使用者名稱 的賣場」
                    String finalShopName = (shopName != null && !shopName.isEmpty())
                            ? shopName : user.getDisplayName() + " 的賣場";
                    newSeller.setShopName(finalShopName);
                    newSeller.setShopDescription("歡迎光臨我的賣場！這是一個新成立的店舖。");
                    newSeller.setStatus("ACTIVE"); // 預設直接開通為活躍狀態

                    // 💡 PPT 亮點：在此處可以同步執行 RBAC 權限升級 (Role Upgrade)
                    // user.setRole("ROLE_SELLER");

                    return sellerRepository.save(newSeller);
                });
    }

    /**
     * 🚀 更新賣家資料 (全功能局部更新版)
     * 實作了防禦性檢查，確保只有前端有傳送的欄位才會被覆蓋，其餘欄位保持原樣。
     * * @param userUuid 賣家的 UUID
     * @param request 包含更新內容的 DTO
     * @return 更新後的實體
     */
    @Transactional
    public SellerProfile updateSellerSettings(
            UUID userUuid,
            SellerRequest request,
            MultipartFile avatarFile,
            MultipartFile bannerFile) throws IOException {

        // 1. 取得核心 User 物件
        User user = userQueryService.findUserByUuid(userUuid);

        // 2. 獲取賣家實體，若沒開通則攔截報錯
        SellerProfile seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("您尚未開通賣家身分，請先執行初始化動作"));

        // 3. 執行局部更新 (Null-Check 防禦邏輯)
        if (request.shopName() != null) seller.setShopName(request.shopName());
        if (request.shopDescription() != null) seller.setShopDescription(request.shopDescription());
        if (request.taxId() != null) seller.setTaxId(request.taxId());
        if (request.servicePhone() != null) seller.setServicePhone(request.servicePhone());
        // ⚠️ 商業考量：status 通常由系統後台或管理員更改，此處開放給賣家自行切換開關
        if (request.status() != null) seller.setStatus(request.status());
        // 處理賣場頭像 (Shop Avatar)
        // 存放路徑：seller/avatars/
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String avatarUrl = fileUploadUtil.uploadFile(avatarFile, "seller/avatars/");
            seller.setAvatarUrl(avatarUrl);
        }
        // 處理賣場看板 (Shop Banner)
        // 存放路徑：seller/banners/
        if (bannerFile != null && !bannerFile.isEmpty()) {
            String bannerUrl = fileUploadUtil.uploadFile(bannerFile, "seller/banners/");
            seller.setBannerUrl(bannerUrl);
        }


        // 🛡️ 資安保護點：
        // 這裡「刻意不處理」 request.createdAt()。
        // 因為「建立時間」是審計資料 (Audit Trail)，一經寫入不應被 API 再次修改。
        // 這確保了資料的真實性，展現了嚴謹的資料庫異動設計。

        return sellerRepository.save(seller);
    }

    /**
     * 取得賣家資料 (純查詢)
     */
    public SellerProfile getSellerInfo(UUID userUuid) {
        User user = userQueryService.findUserByUuid(userUuid);
        return sellerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("找不到您的賣家資料，請確認是否已成為賣家"));
    }
}