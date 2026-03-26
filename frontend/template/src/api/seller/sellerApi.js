import axiosClient from '../axiosClient';

/**
 * 賣家模組 API 服務 (Seller API Service)
 * 🚀 職責：封裝所有與 /api/seller 相關的請求邏輯。
 */
const sellerApi = {
    /**
     * 🚀 啟動賣家權限 / 一鍵開店
     * 路由：POST /api/seller/active
     * @param {string} shopName - 預期的店鋪名稱 (選填)
     */
    activateSeller: (shopName) => {
        return axiosClient.post('/api/seller/active', { shopName });
    },

    /**
     * 🚀 獲取當前登入用戶的賣場資訊
     * 路由：GET /api/seller/my-shop
     */
    getMyShop: () => {
        return axiosClient.get('/api/seller/my-shop');
    },

    /**
     * 🚀 更新賣家個人資料 (支援圖片與 JSON 混合上傳)
     * @param {Object} settings - 賣場配置 (JSON 物件)
     * @param {File} avatar - 頭像檔案 (選填)
     * @param {File} banner - 橫幅檔案 (選填)
     */
    updateProfile: (settings, avatar, banner) => {
        const formData = new FormData();
        // 💡 關鍵點：後端使用 @RequestPart("settings")，前端必須將 JSON 轉為 Blob 並指定 type
        const jsonBlob = new Blob([JSON.stringify(settings)], { type: 'application/json' });
        formData.append('settings', jsonBlob);
        // 加入圖片檔案 (如果有的話)
        if (avatar) formData.append('avatar', avatar);
        if (banner) formData.append('banner', banner);
        return axiosClient.put('/api/seller/profile', formData, {
            headers: {
                'Content-Type': 'multipart/form-data', // 🚀 手動指定為多部分表單格式
            },
        });
    }
};

export default sellerApi;
