import axiosClient from '../axiosClient';

/**
 * 商品模組 API 服務 (Product API Service)
 * 🚀 職責：處理所有與 /api/products 相關的請求（上架、下架、查詢等）。
 */
const productApi = {
    /**
     * 🚀 新增/上架商品 (支援圖片與 JSON 混合)
     * @param {Object} productData - 商品資料
     * @param {File} imageFile - 商品主圖檔案 (選填)
     */
    addProduct: (productData, imageFile) => {
        const formData = new FormData();
        // 💡 關鍵點：後端使用 @RequestPart ProductRequest request
        // 注意：參數名為 'request'
        const jsonBlob = new Blob([JSON.stringify(productData)], { type: 'application/json' });
        formData.append('request', jsonBlob);
        // 加入商品圖片
        if (imageFile) formData.append('image', imageFile);
        return axiosClient.post('/api/products/add', formData);
    },
    /**
     * 🚀 獲取賣家自己的商品清單
     * 路由：GET /api/products/my-products
     */
    getMyProducts: () => {
        return axiosClient.get('/api/products/my-products');
    },
    /**
     * 🚀 更新/編輯商品
     * @param {String} externalId - 商品的 UUID
     * @param {Object} productData - 更新的商品資料
     * @param {File} imageFile - 新的商品圖片 (選填)
     */
    updateProduct: (externalId, productData, imageFile) => {
        const formData = new FormData();
        // 💡 關鍵點：後端更新使用的是 @RequestPart("product")
        const jsonBlob = new Blob([JSON.stringify(productData)], { type: 'application/json' });
        formData.append('product', jsonBlob);

        if (imageFile) formData.append('image', imageFile);

        return axiosClient.put(`/api/products/${externalId}`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    },

    /**
     * 🚀 刪除商品
     * @param {String} externalId - 商品的 UUID
     */
    deleteProduct: (externalId) => {
        return axiosClient.delete(`/api/products/${externalId}`);
    },

    /**
     * 🚀 獲取公開展示商品 (分頁)
     * @param {number} page - 頁碼 (0 開始)
     * @param {number} size - 每頁數量
     */
    getPublicProducts: (page = 0, size = 10) => {
        return axiosClient.get(`/api/public/products/list`, {
            params: { page, size }
        });
    },

    /**
     * 🚀 獲取單一商品公開資料
     * @param {String} externalId - 商品的 UUID
     */
    getPublicProductDetail: (externalId) => {
        return axiosClient.get(`/api/public/products/${externalId}`);
    },

    /**
     * 🚀 更新商品狀態
     * @param {String} externalId - 商品的 UUID
     * @param {String} status - 新狀態 (ON_SHELF, OFF_SHELF, SOLD_OUT)
     */
    updateProductStatus: (externalId, status) => {
        return axiosClient.patch(`/api/products/${externalId}/status`, null, {
            params: { status }
        });
    }
};

export default productApi;
