// 【建議新增 src/api/services/appApi.js】
import axiosClient from '../axiosClient';
import { ENDPOINTS } from '../endpoints';

export const homePageApi = {
    // 獲取 JSP 後端的 Hello 測試資料
    getHelloMessage: async () => {
        // 這邊只要寫 GET，它會自動加上 baseURL 和 /hello
        return await axiosClient.get(ENDPOINTS.HELLO_TEST);
    },

    // [重點新增]：獲取首頁 Banner 圖片列表
    getBanners: async () => {
        // 請將 '/api/banners' 換成你後端實際的 API 路徑，或是加進 ENDPOINTS 裡
        return await axiosClient.get(ENDPOINTS.IMGUPLOAD);
    }
};



