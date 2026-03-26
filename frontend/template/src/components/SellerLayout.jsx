import React from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import { Package, ShoppingCart, Settings, LogOut } from 'lucide-react';
import { useAuth } from '../api/context/AuthContext';
import '../scss/layoutScss/_sellerLayout.scss';

export default function SellerLayout() {
  const location = useLocation();
  const { sellerInfo } = useAuth();
  
  // 導覽清單 (已移除「賣場總覽」)
  const navItems = [
    { icon: Package, label: '商品管理', path: '/seller/products' },
    { icon: ShoppingCart, label: '訂單管理', path: '/seller/orders' },
    { icon: Settings, label: '賣場設定', path: '/seller/settings' },
  ];

  // 取得目前頁面標題
  const currentTitle = navItems.find((item) => item.path === location.pathname)?.label || '賣家中心';

  return (
    <div className="seller-layout">
      {/* Sidebar */}
      <aside className="seller-layout__sidebar">
        
        {/* 賣家資訊區 (置頂) */}
        <div className="seller-profile">
          <div className="seller-profile__avatar-wrapper">
            <img 
              src={sellerInfo?.avatarUrl || "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?q=80&w=100&auto=format&fit=crop"} 
              alt="Seller Avatar" 
              className="seller-profile__avatar" 
            />
            <div className="seller-profile__status-dot"></div>
          </div>
          <div className="seller-profile__info">
            <h2 className="seller-profile__name">{sellerInfo?.shopName || '未命名賣場'}</h2>
            <span className="seller-profile__badge">專業賣家</span>
          </div>
        </div>
        
        <nav className="seller-nav">
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            const Icon = item.icon;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`seller-nav__item ${isActive ? 'seller-nav__item--active' : ''}`}
              >
                <Icon className="seller-nav__icon" size={20} />
                <span className="seller-nav__label">{item.label}</span>
              </Link>
            );
          })}
        </nav>

        {/* 返回買家版 */}
        <div className="seller-nav__footer">
          <Link to="/" className="seller-nav__exit">
            <LogOut className="seller-nav__icon" size={20} />
            <span className="seller-nav__label">返回買家版</span>
          </Link>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="seller-layout__main">
        <header className="seller-layout__header">
          <h1 className="seller-layout__title">{currentTitle}</h1>
        </header>

        <div className="seller-layout__content-scroll">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
