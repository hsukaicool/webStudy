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



    // 更新個人資料 (傳送 UserProfileRequest JSON)
    updateProfile: async (data) => {
        return await axiosClient.put(ENDPOINTS.USER.UPDATE, data); // 預設會是 JSON 格式
    },


    // 新增註冊功能
    register: async (userData) => {
        // userData 預期包含 { name, email, password }
        return await axiosClient.post(ENDPOINTS.AUTH.REGISTER, userData);
    },

    // 上傳頭像 (傳送 FormData)
    uploadAvatar: async (file) => {
        const formData = new FormData();
        formData.append('file', file); // 🚀 注意：名稱必須與後端 @RequestParam("file") 一致

        return await axiosClient.patch(ENDPOINTS.USER.AVATAR, formData, {
            headers: {
                'Content-Type': 'multipart/form-data', // 🚀 指定為檔案上傳格式
            },
        });
    },

};
