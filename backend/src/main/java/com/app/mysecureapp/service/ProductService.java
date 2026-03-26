package com.app.mysecureapp.service;

// === 1. 資料傳輸物件 (DTO) 引入 ===
import com.app.mysecureapp.dto.ProductRequest;
import com.app.mysecureapp.dto.Response.ProductResponse;

// === 2. 模型層 (Model) 引入 ===
import com.app.mysecureapp.model.Product;
import com.app.mysecureapp.model.SellerProfile;
import com.app.mysecureapp.model.User;

// === 3. 倉儲層 (Repository) 引入 ===
import com.app.mysecureapp.repository.ProductRepository;
import com.app.mysecureapp.repository.SellerProfileRepository;


// === 4. Spring 核心與事務管理 ===
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 🚀 確保資料一致性的關鍵
import org.springframework.web.multipart.MultipartFile;
import com.app.mysecureapp.util.FileUploadUtil; // 🚀 注入你的通用工具

// === 5. Java 標準庫 ===
import java.io.IOException;
import java.util.List;
import java.util.UUID;


/**
 * 【商品業務邏輯層】
 * 職責：處理商品上架、查詢、庫存管理等核心邏輯。
 * 🚀 技術亮點：
 * 1. 嚴格身分校驗：強制要求具備賣家身分方可執行寫入操作。
 * 2. 事務管理 (Transactional)：確保商品建立與關聯綁定在同一個原子操作內完成。
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerRepository;
    private final UserQueryService userQueryService; // 用於查詢 User 實體
    private final FileUploadUtil fileUploadUtil;

    public ProductService(ProductRepository productRepository,
                          SellerProfileRepository sellerRepository,
                          UserQueryService userQueryService,
                          FileUploadUtil fileUploadUtil) {
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
        this.userQueryService = userQueryService;
        this.fileUploadUtil = fileUploadUtil;
    }

    /**
     * 【上架新商品】
     * 🚀 邏輯：識別用戶 -> 驗證賣家資格 -> DTO 轉 Entity -> 持久化
     * @param userUuid 來自 JWT 的使用者識別碼
     * @param request 前端傳入的商品資訊
     */
    @Transactional // 🚀 標註為事務：若過程中發生異常，資料庫會自動回滾，避免產生髒數據
    public ProductResponse createProduct(UUID userUuid, ProductRequest request, MultipartFile image)
            throws IOException{

        // 1. 根據 UUID 獲取使用者實體
        User user = userQueryService.findUserByUuid(userUuid);

        // 2. 核心檢查：確認該用戶是否已開通賣家功能
        // 🚀 亮點：這是一道安全防線，防止普通用戶透過 API 漏洞惡意上架
        SellerProfile seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("權限不足：請先開通賣家中心功能"));


        // 將檔案存放在 GCP 的 "products/" 路徑下，實現資源分類
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = fileUploadUtil.uploadFile(image, "products/");
        }

        // 3. 數據映射 (DTO -> Entity)
        Product product = new Product();
        product.setSeller(seller); // 建立商品與賣家的強關聯
        product.setName(request.name());
        product.setCategory(request.category());
        product.setProductCondition(request.condition());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setDescription(request.description());
        product.setImageUrl(imageUrl);


        // 5. 儲存至資料庫
        Product savedProduct = productRepository.save(product);

        // 6. 回傳 Response DTO (封裝結果)
        return convertToResponse(savedProduct);
    }

    /**
     * 【更新商品】
     * 🚀 邏輯：確認身分 -> 檢查歸屬 -> 處理圖片替換 -> 更新欄位
     */
    @Transactional
    public ProductResponse updateProduct(UUID userUuid, UUID externalId, ProductRequest request, MultipartFile image)
            throws IOException {

        // 1. 獲取商品並驗證權限
        Product product = productRepository.findByExternalId(externalId)
                .orElseThrow(() -> new RuntimeException("找不到該商品"));

        // 🚀 安全核心：確認發起請求的人，就是該商品的擁有者
        if (!product.getSeller().getUser().getExternalId().equals(userUuid)) {
            throw new RuntimeException("權限不足：你不是此商品的賣家");
        }

        // 2. 處理圖片更新
        if (image != null && !image.isEmpty()) {
            // 刪除舊圖片 (若有)
            if (product.getImageUrl() != null) {
                fileUploadUtil.deleteFileByUrl(product.getImageUrl());
            }
            // 上傳新圖片
            String newUrl = fileUploadUtil.uploadFile(image, "products/");
            product.setImageUrl(newUrl);
        }

        // 3. 更新其餘欄位
        product.setName(request.name());
        product.setCategory(request.category());
        product.setProductCondition(request.condition());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setDescription(request.description());

        return convertToResponse(productRepository.save(product));
    }

    /**
     * 【更新商品狀態】
     * 🚀 邏輯：確認身分 -> 檢查歸屬 -> 更新狀態
     */
    @Transactional
    public ProductResponse updateProductStatus(UUID userUuid, UUID externalId, com.app.mysecureapp.model.Enum.ProductStatus newStatus) {
        Product product = productRepository.findByExternalId(externalId)
                .orElseThrow(() -> new RuntimeException("找不到該商品"));

        // 權限檢查
        if (!product.getSeller().getUser().getExternalId().equals(userUuid)) {
            throw new RuntimeException("權限不足：你不是此商品的賣家");
        }

        product.setStatus(newStatus);
        return convertToResponse(productRepository.save(product));
    }

    /**
     * 【刪除商品】
     */
    @Transactional
    public void deleteProduct(UUID userUuid, UUID externalId) {
        Product product = productRepository.findByExternalId(externalId)
                .orElseThrow(() -> new RuntimeException("找不到該商品"));

        // 權限檢查
        if (!product.getSeller().getUser().getExternalId().equals(userUuid)) {
            throw new RuntimeException("權限不足：無法刪除他人商品");
        }

        // 清理 GCP 圖片資源
        if (product.getImageUrl() != null) {
            fileUploadUtil.deleteFileByUrl(product.getImageUrl());
        }

        productRepository.delete(product);
    }

    /**
     * 【私有輔助：Entity 轉 Response DTO】
     */

    public List<ProductResponse> getSellerProductsByUuid(UUID userUuid) {
        User user = userQueryService.findUserByUuid(userUuid);

        // 確保只抓到該賣家的商品
        SellerProfile seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("賣家不存在"));

        // 1️⃣ 這裡是那「1」次查詢：抓出所有屬於該賣家的商品  (n+1 資料庫查詢結構)
        // SQL: SELECT * FROM products WHERE seller_id = ?
        List<Product> products = productRepository.findBySeller(seller);

        // 2. 使用 Stream 進行轉換 (這段寫在 PPT 會很專業)
        return products.stream()
                .map(this::convertToResponse) // 呼叫下方的轉換方法、這裡開始跑循環 (Loop)
                .toList();
    }
    private ProductResponse convertToResponse(Product product) {
        return new ProductResponse(
                product.getExternalId(), // 使用 UUID 對外，不暴露遞增 ID
                product.getName(),
                product.getCategory(),
                product.getProductCondition(),
                product.getPrice(),
                product.getStock(),
                product.getDescription(),
                product.getStatus(),
                product.getImageUrl(),
                product.getCreatedAt()
        );
    }
}