import axiosClient from '../axiosClient';

const bannerApi = {
    /** 🚀 獲取所有啟用的橫幅 (所有人) */
    getPublicBanners: () => axiosClient.get('/api/banners/public'),

    /** 🚀 管理員上傳橫幅 (僅限 TEXT2) */
    uploadBanner: (image, title, link) => {
        const formData = new FormData();
        formData.append('image', image);
        formData.append('title', title);
        formData.append('link', link);
        return axiosClient.post('/api/banners/admin/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    }
};

export default bannerApi;
