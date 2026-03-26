import axiosClient from '../axiosClient';

/**
 * 🛒 購物車模組 API 服務 (Cart API Service)
 * 🚀 職責：處理所有與 /api/cart 相關的請求（查詢、新增、更新數量、刪除項目）。
 */
const cartApi = {
    /**
     * 【獲取我的購物車】
     * 🚀 回傳按賣家分組的購物車資料 (List<CartGroupResponse>)
     */
    getMyCart: () => {
        return axiosClient.get('/api/cart');
    },

    /**
     * 【加入購物車】
     * @param {String} productExternalId - 商品的 UUID
     * @param {Number} quantity - 欲加入的數量 (預設為 1)
     */
    addToCart: (productExternalId, quantity = 1) => {
        // 因為後端預期使用 @RequestParam，所以將參數放在 params 裡傳遞
        return axiosClient.post('/api/cart/add', null, {
            params: {
                // 後端是用 @RequestParam 接收
                productExternalId,
                quantity
            }
        });
    },

    /**
     * 【更新購物車數量】 (+ / - 按鈕)
     * @param {Number} cartItemId - 購物車項目的資料庫 ID
     * @param {Number} quantity - 更新後的數量
     */
    updateQuantity: (cartItemId, quantity) => {
        return axiosClient.patch(`/api/cart/items/${cartItemId}`, null, {
            params: { quantity }
        });
    },

    /**
     * 【移除購物車項目】 (垃圾桶圖示)
     * @param {Number} cartItemId - 購物車項目的資料庫 ID
     */
    removeFromCart: (cartItemId) => {
        return axiosClient.delete(`/api/cart/items/${cartItemId}`);
    }
};

export default cartApi;
