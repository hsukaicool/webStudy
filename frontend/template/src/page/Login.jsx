import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Gem, Mail, Lock, ArrowRight, Github, User } from 'lucide-react';
import '../scss/pagesScss/Login.scss'; // 記得引入你的 SCSS 檔案

import { useNavigate } from 'react-router-dom'; // 引入跳轉鉤子
import { userApi } from '../api/services/userApi'; // 引入 API



export default function Login() {

    const navigate = useNavigate();
    const [credentials, setCredentials] = useState({ username: '', password: '' });
    const [loading, setLoading] = useState(false);
    const handleChange = (e) => {
        const { name, value } = e.target;
        setCredentials(prev => ({ ...prev, [name]: value }));
    };
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            // 呼叫後端 API
            const response = await userApi.login(credentials);

            // 根據後端回傳：response 內包含 { token, username }
            localStorage.setItem('access_token', response.token);
            localStorage.setItem('username', response.username);

            alert('登入成功！');
            navigate('/'); // 跳轉至首頁
        } catch (error) {
            console.error('登入失敗:', error);
            // 處理 401 或其他錯誤訊息
            alert(error.response?.data || '登入失敗，請檢查帳號密碼');
        } finally {
            setLoading(false);
        }
    };


    return (
        <div className="login-container">
            {/* 左側 - 品牌形象與 Banner */}
            <div className="login-banner">
                <div
                    className="login-banner__bg"
                    style={{ backgroundImage: "url('https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?q=80&w=1200&auto=format&fit=crop')" }}
                />
                <div className="login-banner__overlay" />
                <div className="login-banner__content">
                    <Link to="/" className="login-banner__logo">
                        <Gem className="icon-gem" strokeWidth={1.5} />
                        <span className="logo-text">二手好物交易網</span>
                    </Link>
                    <h1 className="login-banner__title">
                        發現獨特，<br />傳承價值。
                    </h1>
                    <p className="login-banner__description">
                        加入我們經過驗證的社群，探索高品質的二手好物，體驗安全、安心的交易過程。
                    </p>
                </div>
            </div>

            {/* 右側 - 登入表單 */}
            <div className="login-form-wrapper">
                <div className="login-form-container">
                    {/* 手機版 Logo */}
                    <Link to="/" className="mobile-logo">
                        <Gem className="icon-gem" strokeWidth={1.5} />
                        <span className="logo-text">二手好物交易網</span>
                    </Link>

                    <div className="login-header">
                        <h2 className="login-header__title">歡迎回來</h2>
                        <p className="login-header__subtitle">請登入您的帳號以繼續探索精選好物。</p>
                    </div>

                    <form className="login-form" onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label className="form-label">帳號</label>
                            <div className="input-wrapper">
                                <div className="input-icon">
                                    <User />
                                </div>
                                <input
                                    name="username" // 👈 必須對應 credentials 的 key
                                    value={credentials.username}
                                    onChange={handleChange}
                                    type="text" // 建議改為 text 或依照需求
                                    className="form-input"

                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <div className="form-group__header">
                                <label className="form-label">密碼</label>
                                <a href="#" className="forgot-password">忘記密碼？</a>
                            </div>
                            <div className="input-wrapper">
                                <div className="input-icon">
                                    <Lock />
                                </div>
                                <input
                                    name="password"
                                    value={credentials.password}
                                    onChange={handleChange}
                                    type="password"
                                    className="form-input"
                                    placeholder="••••••••"
                                />
                            </div>
                        </div>

                        <button className="btn-submit">
                            登入帳號 <ArrowRight className="icon-arrow" />
                        </button>
                    </form>

                    {/* 分隔線 */}
                    <div className="divider">
                        <div className="divider__line"></div>
                        <span className="divider__text">或透過以下方式登入</span>
                        <div className="divider__line"></div>
                    </div>

                    {/* 社群登入按鈕 */}
                    <div className="social-login">
                        <button className="btn-social">
                            <svg viewBox="0 0 24 24">
                                <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4" />
                                <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853" />
                                <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05" />
                                <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335" />
                            </svg>
                            Google
                        </button>
                        <button className="btn-social">
                            <Github />
                            GitHub
                        </button>
                    </div>

                    <p className="register-prompt">
                        還沒有帳號嗎？{' '}
                        <Link to="/register" className="register-link">
                            立即註冊
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
}
