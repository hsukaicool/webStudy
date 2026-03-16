// 處理跟商品相關的 API (例如 getProducts, createProduct)

// 【建議做法範例】 src/page/Home.jsx
import { useEffect, useState } from 'react';
import { userApi } from '../api/services/userApi';

const Home = () => {
    const [profile, setProfile] = useState(null);

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                // 從元件角度看：只需要知道呼叫 "userApi.getProfile()" 即可，不用管它背後多長
                const data = await userApi.getProfile();
                setProfile(data);
            } catch (error) {
                console.error("載入失敗", error);
            }
        };

        fetchUserData();
    }, []);

    return (
        <div>
            {/* 畫面渲染邏輯 */}
        </div>
    );
};

