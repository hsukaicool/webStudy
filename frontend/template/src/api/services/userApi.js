// 處理跟使用者相關的 API (例如 login, getProfile)


// 【建議做法】 api/services/userApi.js
import axiosClient from '../axiosClient';
import { ENDPOINTS } from '../endpoints';

export const userApi = {
    // 登入
    login: async (credentials) => {
        return await axiosClient.post(ENDPOINTS.AUTH.LOGIN, credentials);
    },

    // 獲取使用者個人資料
    getProfile: async () => {
        return await axiosClient.get(ENDPOINTS.USER.PROFILE);
    },

    // 更新個人資料 (以 FormData 為例，例如有上傳頭像)
    updateProfile: async (data) => {
        return await axiosClient.put(ENDPOINTS.USER.UPDATE, data, {
            headers: { 'Content-Type': 'multipart/form-data' } // 需要特別覆寫 header 時也可以
        });
    },
    // 新增註冊功能
    register: async (userData) => {
        // userData 預期包含 { name, email, password }
        return await axiosClient.post(ENDPOINTS.AUTH.REGISTER, userData);
    },

};
