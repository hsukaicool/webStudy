// src/components/Navbar.jsx
import { Link } from 'react-router-dom';
import { Search, Heart, User, ShoppingBag, Menu, Gem } from 'lucide-react';

// Component-Based Icons (組件化圖標)
import { Search, Heart, User, ShoppingBag, Menu, Gem, Store } from 'lucide-react';

export default function Navbar({ variant }) {
    // 根據傳入的 variant 決定 Navbar 樣式
    // variant="home" 時通常為固定透明或毛玻璃背景
    const navbarClass = variant === "home" ? "navbar-home" : "navbar-default";

    return (
        <header className={`navbar ${navbarClass}`}>
            <div className="navbar-container">
                {/* 左側：漢堡選單與 Logo */}
                <div className="navbar-left">
                    <button className="mobile-menu-btn">

                    </button>
                    <Link to="/" className="navbar-logo font-display">
                        <Gem className="logo-icon" size={28} strokeWidth={1.5} />
                        <span className="logo-text">二手好物交易網</span>
                    </Link>

                    {/* 新增「成為賣家」按鈕 */}
                    {/* Link to="/become-seller"
                     Client-Side Routing (客戶端路由) 頁面不會跳轉（閃爍） */}
                    <Link to="/become-seller" className="become-seller-btn desktop-only">
                        <Store size={20} strokeWidth={2} />
                        <span>成為賣家</span>
                    </Link>
                </div>



                {/* 右側：搜尋與個人功能 */}
                <div className="navbar-actions">
                    <div className="search-wrapper desktop-only">
                        <Search size={18} className="search-icon" />
                        <input type="text" placeholder="搜尋商品..." className="search-input" />
                    </div>

                    {/* 小螢幕顯示的搜尋按鈕 */}
                    <button className="action-btn mobile-only">
                        <Search size={24} />
                    </button>

                    <Link to="/wishlist" className="action-btn">
                        <Heart size={24} />
                    </Link>

                    <Link to="/profile" className="action-btn">
                        <User size={24} />
                    </Link>

                    <Link to="/cart" className="action-btn cart-btn">
                        <ShoppingBag size={24} />
                        <span className="cart-badge">0</span>
                    </Link>
                </div>
            </div>
        </header>
    );
}
