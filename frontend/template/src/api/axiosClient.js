// # 用來封裝 Axios 實例，處理攔截器 (Token, Error handling)
// 【建議做法】 api/axiosClient.js
import axios from 'axios';

// 1. 建立獨立的 axios 實例
const axiosClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8080', // 從環境變數抓網址
    timeout: 10000, // 設定超時時間，避免 API 卡死
    headers: {
        'Content-Type': 'application/json',
    },
});

// 2. 請求攔截器 (Request Interceptor) - 在送出 request 前攔截
axiosClient.interceptors.request.use(
    (config) => {
        // 每次發送請求前，從 localStorage 抓取 Token 並放進 header
        const token = localStorage.getItem('access_token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 3. 響應攔截器 (Response Interceptor) - 在收到 response 後攔截
axiosClient.interceptors.response.use(
    (response) => {
        // 如果 API 成功，我們可以直接把裡面的 data 吐出來
        // 這樣元件就不用再 p.data.data
        return response.data;
    },
    (error) => {
        // 這裡可以做全局錯誤處理！例如 401 未授權就強制登出跳轉
        if (error.response?.status === 401) {
            console.error('登入過期，請重新登入');
            // 可以在這裡清除 localstorage 或導向登入頁
        } else if (error.response?.status >= 500) {
            console.error('伺服器發生錯誤');
        }
        return Promise.reject(error);
    }
);

export default axiosClient;
