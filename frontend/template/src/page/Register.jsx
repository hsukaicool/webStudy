import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Gem, Mail, Lock, User, ArrowRight, Github } from 'lucide-react';
import { useState } from 'react';
import { userApi } from '../api/services/userApi';

export default function Register() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        displayName: '', // 對應前端「姓名」
        username: '',    // 對應前端「帳號」
        email: '',       // 對應前端「電子郵件」
        password: '',    // 對應前端「密碼」
    });

    const [loading, setLoading] = useState(false);

    // 更新輸入值的處理函數
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };
    // 5. 實作提交邏輯
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await userApi.register(formData);
            console.log('註冊成功:', response);
            alert('註冊成功！請重新登入');
            navigate('/login'); // 成功後跳轉到登入頁
        } catch (error) {
            console.error('註冊失敗:', error);
            alert(error.response?.data?.message || '註冊失敗，請稍後再試');
        } finally {
            setLoading(false);
        }
    };



    return (
        <div className="register-container">
            {/* 左側：品牌形象區塊 */}
            <div className="register-banner">
                <div className="register-banner__bg" />
                <div className="register-banner__overlay" />
                <div className="register-banner__content">
                    <Link to="/" className="register-banner__logo">
                        <Gem className="icon-gem" strokeWidth={1.5} />
                        <span className="logo-text">二手好物交易網</span>
                    </Link>
                    <h1 className="register-banner__title">
                        開啟您的<br />尋寶之旅。
                    </h1>
                    <p className="register-banner__description">
                        建立您的專屬帳號，開始收藏喜愛的商品、與賣家交流，並打造屬於您的獨特品味空間。
                    </p>
                </div>
            </div>

            {/* 右側：表單區塊 */}
            <div className="register-form-wrapper">
                <div className="register-form-container">
                    {/* 手機版 Logo */}
                    <Link to="/" className="mobile-logo">
                        <Gem className="icon-gem" strokeWidth={1.5} />
                        <span className="logo-text">二手好物交易網</span>
                    </Link>

                    <header className="register-header">
                        <h2 className="register-header__title">建立帳號</h2>
                        <p className="register-header__subtitle">加入我們，體驗最安心的二手交易社群。</p>
                    </header>

                    <form className="register-form" onSubmit={handleSubmit}>
                        <div className="form-group">
                            {/* 1. 姓名 -> displayName */}
                            <label className="form-label">姓名</label>
                            <div className="input-wrapper">
                                <span className="input-icon"><User /></span>
                                <input
                                    className="form-input"
                                    name="displayName"
                                    value={formData.displayName}
                                    onChange={handleChange}
                                    placeholder="王小明"
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="form-label">電子郵件</label>
                            <div className="input-wrapper">
                                <span className="input-icon"><Mail /></span>
                                <input
                                    name="email"
                                    value={formData.email}
                                    onChange={handleChange}
                                    type="email"
                                    className="form-input"
                                    placeholder="name@example.com" />
                            </div>
                        </div>

                        {/* 2. 帳號 -> username */}
                        <div className="form-group">
                            <label className="form-label">登入帳號</label>
                            <div className="input-wrapper">
                                <span className="input-icon"><User /></span>
                                <input
                                    name="username"
                                    value={formData.username}
                                    onChange={handleChange}
                                    type="text"
                                    className="form-input"
                                    placeholder="請設定登入帳號"
                                />
                            </div>
                        </div>



                        <div className="form-group">
                            <label className="form-label">密碼</label>
                            <div className="input-wrapper">
                                <span className="input-icon"><Lock /></span>
                                <input
                                    name="password"
                                    value={formData.password}
                                    onChange={handleChange}
                                    type="password"
                                    className="form-input"
                                    placeholder="最少 8 個字元" />
                            </div>
                        </div>

                        <button type="submit" className="btn-submit">
                            註冊帳號 <ArrowRight className="icon-arrow" />
                        </button>

                    </form>

                    <div className="divider">
                        <div className="divider__line"></div>
                        <span className="divider__text">或透過以下方式註冊</span>
                        <div className="divider__line"></div>
                    </div>

                    <div className="social-login">
                        <button className="btn-social">
                            <svg className="icon-google" viewBox="0 0 24 24">
                                <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4" />
                                <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853" />
                                <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05" />
                                <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335" />
                            </svg>
                            Google
                        </button>
                        <button className="btn-social">
                            <Github className="icon-github" />
                            GitHub
                        </button>
                    </div>

                    <footer className="login-prompt">
                        已經有帳號了嗎？ <Link to="/login" className="login-link">立即登入</Link>
                    </footer>
                </div>
            </div >
        </div >
    );
}
