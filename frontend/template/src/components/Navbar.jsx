import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Search, ClipboardList, User, ShoppingBag, Menu, Gem, Store, LogOut, LogIn } from 'lucide-react';
import { useAuth } from '../api/context/AuthContext';
import sellerApi from '../api/seller/sellerApi';

export default function Navbar({ variant }) {
    const navigate = useNavigate();

    // 依據 variant 決定外觀變體 (如：透明底或預設)
    const navbarModifier = variant === "home" ? "navbar--home" : "navbar--default";

    // 整合：使用 useState 管理登入狀態，確保 UI 即時聯動
    const { isLoggedIn, logout, sellerInfo, fetchSellerInfo } = useAuth();

    const handleLogout = () => {
        logout();
        alert('已成功登出');
        navigate('/login');
    };

    // 處理進入賣家中心前的開通邏輯
    const handleSellerClick = async (e) => {
        // 如果沒有賣家資訊，代表尚未開通
        if (isLoggedIn && !sellerInfo) {
            e.preventDefault();
            const confirmActive = window.confirm('恭喜成為資本家');
            if (confirmActive) {
                try {
                    await sellerApi.activateSeller(); // 調用後端 Get or Create 接口
                    await fetchSellerInfo(); // 重新獲取全域賣家狀態
                    navigate('/seller/products'); // 開通成功後導向
                } catch (error) {
                    console.error('開通賣家失敗:', error);
                    alert('開通失敗，請確認登入狀態或稍後再試');
                }
            }
        }
    };

    return (
        <header className={`navbar ${navbarModifier}`}>
            {/* 左側：漢堡選單、Logo、成為賣家 */}
            <div className="navbar__brand">
                <Link to="/" className="navbar__brand-link">
                    <Gem className="navbar__logo-icon" size={28} strokeWidth={1.5} />
                    <span className="navbar__title">二手好物交易網</span>
                </Link>
            </div>

            {/* 右側：搜尋與個人功能 */}
            <div className="navbar__content">
                {/* 電腦版搜尋框 */}
                <label className="navbar__search navbar__desktop-flex">
                    <div className="navbar__search-wrapper">
                        <div className="navbar__search-icon-box">
                            <Search className="navbar__icon" />
                        </div>
                        <input className="navbar__search-input" placeholder="搜尋商品..." />
                    </div>
                </label>

                <div className="navbar__actions">
                    {/* 手機版搜尋按鈕 */}
                    <button className="navbar__btn navbar__btn--icon navbar__mobile-only">
                        <Search className="navbar__icon" />
                    </button>
                    {isLoggedIn && (
                        <>
                            <Link
                                to="/seller/products"
                                className="navbar__btn navbar__btn--secondary navbar__desktop-only"
                                onClick={handleSellerClick}
                            >
                                <Store className="navbar__icon navbar__icon--sm" strokeWidth={2} />
                                <span>{sellerInfo ? '賣家中心' : '成為賣家'}</span>
                            </Link>

                            {/* 我的訂單 */}
                            <Link to="/orders" className="navbar__btn navbar__btn--icon" title="我的訂單">
                                <ClipboardList className="navbar__icon" />
                            </Link>

                            {/* 購物車 (整合 Badge) */}
                            <Link to="/cart" className="navbar__btn navbar__btn--icon navbar__btn--relative" title="購物車">
                                <ShoppingBag className="navbar__icon" />
                                <span className="navbar__badge">0</span>
                            </Link>

                        </>
                    )}


                    {/* 會員與登出入狀態判斷 */}
                    {isLoggedIn ? (
                        <>
                            <Link to="/profile" className="navbar__btn navbar__btn--icon" title="會員中心">
                                <User className="navbar__icon" />
                            </Link>
                            <button onClick={handleLogout} className="navbar__btn navbar__btn--icon navbar__btn--danger" title="登出">
                                <LogOut className="navbar__icon" />
                            </button>
                        </>
                    ) : (
                        // 未登入時顯示登入與註冊按鈕
                        <>
                            <Link to="/login" className="navbar__btn navbar__btn--secondary">
                                <LogIn className="navbar__icon navbar__icon--sm" />
                                登入
                            </Link>
                            <Link to="/register" className="navbar__btn navbar__btn--primary">
                                註冊
                            </Link>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
}
