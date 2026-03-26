import { createContext, useContext, useState, useEffect } from 'react';
import sellerApi from '../seller/sellerApi';
import { userApi } from '../services/userApi'; // 🚀 補上 userApi

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [userProfile, setUserProfile] = useState(null); // 儲存用戶資料 (含角色)
    const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('access_token'));
    const [sellerInfo, setSellerInfo] = useState(null);

    // 獲取賣家資料
    const fetchSellerInfo = async () => {
        try {
            const response = await sellerApi.getMyShop();
            setSellerInfo(response);
        } catch (error) {
            // 如果 404 或報錯，代表可能還沒開通賣家，設為 null 即可
            setSellerInfo(null);
        }
    };

    const fetchUserProfile = async () => {
        try {
            const data = await userApi.getProfile();
            setUserProfile(data);
        } catch (error) {
            setUserProfile(null);
        }
    };

    useEffect(() => {
        if (isLoggedIn) {
            fetchUserProfile(); // 🚀 補上這行，確保登入後抓取身分

        } else {
            setSellerInfo(null);
        }
    }, [isLoggedIn]);

    // 登入方法
    const login = (token) => {
        localStorage.setItem('access_token', token);
        setIsLoggedIn(true);
    };

    // 登出方法
    const logout = () => {
        localStorage.removeItem('access_token');
        setIsLoggedIn(false);
        setSellerInfo(null);
    };

    return (
        <AuthContext.Provider value={{ isLoggedIn, userProfile, sellerInfo, login, logout, fetchSellerInfo }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
