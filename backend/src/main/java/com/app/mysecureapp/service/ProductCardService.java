package com.app.mysecureapp.service;

import com.app.mysecureapp.dto.Response.ProductCardResponse;
import com.app.mysecureapp.dto.Response.ProductResponse;
import com.app.mysecureapp.model.Enum.ProductStatus;
import com.app.mysecureapp.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

import com.app.mysecureapp.model.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;



/**
 * 【展示用商品業務層】
 * 🚀 職責：處理首頁、搜尋、推薦等不需要高度權限校驗的「讀取型」業務。
 */
@Service
public class ProductCardService {

    private final ProductRepository productRepository;

    public ProductCardService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 【獲取首頁分頁商品】
     * 🚀 優化亮點：分頁加載 (Pagination) + 關聯預抓 (JOIN FETCH)
     * @return 回傳 Page 物件，包含商品數據與分頁元數據 (Metadata)
     */
    public Page<ProductCardResponse> getPublicProductCards(int page, int size) {
        // 1. 建立分頁請求：第 page 頁、每頁 size 筆、按時間由新到舊排序
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 2. 呼叫 Repository：只搬出「這一頁」需要的貨物
        // 🚀 注意：這裡用的是我們之前寫的有 JOIN FETCH 的那個方法
        Page<Product> productPage = productRepository.findByStatus(ProductStatus.ON_SHELF, pageable);

        // 3. 🚀 關鍵技術：直接在 Page 物件上使用 .map()
        // 這樣轉換出來的結果依然是 Page 格式，會自動保留總頁數、目前頁碼等資訊
        return productPage.map(p -> new ProductCardResponse(
                p.getExternalId(),
                p.getName(),
                p.getPrice(),
                p.getImageUrl(),
                p.getProductCondition()
        ));
    }

    /**
     * 【獲取單一商品公開資訊】
     * 🚀 邏輯：透過 UUID 查詢，並確保商品狀態為 ON_SHELF，否則視為找不到
     */
    public ProductResponse getPublicProductDetail(java.util.UUID externalId) {
        Product product = productRepository.findByExternalId(externalId)
                .orElseThrow(() -> new RuntimeException("找不到該商品"));

        if (product.getStatus() != ProductStatus.ON_SHELF) {
            throw new RuntimeException("該商品已下架或無法購買");
        }

        return new ProductResponse(
                product.getExternalId(),
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