// 集中管理所有的 API URL 路徑

// 【建議做法】 api/endpoints.js
export const ENDPOINTS = {
    AUTH: {
        LOGIN: '/api/auth/login',
        REGISTER: '/api/auth/register',
    },
    PRODUCTS: {
        LIST: '/products',
        DETAIL: (id) => `/products/${id}`, // 帶有變數的路徑可以寫成函數
    },
    HELLO_TEST: '/hello',  // 👈 新增這行 (因為你的 baseURL 已經包含 /my-project/api 了)

    IMGUPLOAD: '/img/upload',

    USER: {
        PROFILE: '/api/profile', // 修正路徑以符合後端 Controller
        UPDATE: '/api/profile',
        AVATAR: '/api/profile/avatar', // 圖片上傳的地方
    },
    IMAGES: {
        UPLOAD: '/api/images/upload',
    }
};



