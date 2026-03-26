import React, { useState, useEffect } from 'react'; // 🚀 補上 useEffect
import { Search, Package, Truck, CheckCircle, Ban, Store } from 'lucide-react';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import orderApi from '../api/services/orderApi'; // 🚀 補上 orderApi
import '../scss/pagesScss/_buyerOrders.scss';

// 1. 定義分頁按鈕 (放在元件外部或是內部皆可)
const STATUS_TABS = [
  { id: 'all', label: '全部訂單' },
  { id: 'pending', label: '待出貨' },
  { id: 'shipping', label: '待收貨' },
  { id: 'completed', label: '已完成' },
  { id: 'cancelled', label: '已取消' },
];

export default function BuyerOrders() {
  // 2. 宣告狀態 (一定要在函式內部)
  const [orders, setOrders] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');

  // 3. 獲取真實資料
  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setIsLoading(true);
        const data = await orderApi.getBuyerOrders();
        setOrders(data);
      } catch (error) {
        console.error("載入訂單失敗", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchOrders();
  }, []);

  // 4. 定義狀態顯示邏輯
  const getStatusDisplay = (status) => {
    switch (status) {
      case 'PENDING': // 🚀 注意：需與後端 Enum 值 (通常是大寫) 對齊
        return <span className="status-badge status-badge--pending"><Package size={14} /> 待出貨</span>;
      case 'SHIPPING':
        return <span className="status-badge status-badge--shipping"><Truck size={14} /> 待收貨</span>;
      case 'COMPLETED':
        return <span className="status-badge status-badge--completed"><CheckCircle size={14} /> 已完成</span>;
      case 'CANCELLED':
        return <span className="status-badge status-badge--cancelled"><Ban size={14} /> 已取消</span>;
      default: return null;
    }
  };

  // 5. 資料過濾邏輯 (搜尋 + 分頁)
  const filteredOrders = orders.filter(order => {
    const matchesTab = activeTab === 'all' || order.status === activeTab;
    const matchesSearch = order.externalId.toLowerCase().includes(searchTerm.toLowerCase());
    return matchesTab && matchesSearch;
  });

  if (isLoading) return <div className="loading">讀取訂單中...</div>;

  return (
    <div className="buyer-orders">
      <main className="buyer-orders__container">
        <div className="buyer-orders__header">
          <h1 className="buyer-orders__header-title">購買訂單</h1>
          <p className="buyer-orders__header-subtitle">追蹤您的所有購買紀錄與物流狀態</p>
        </div>

        {/* 搜尋欄 */}
        <div className="buyer-orders__search-wrapper">
          <div className="buyer-orders__search">
            <Search className="buyer-orders__search-icon" />
            <input
              type="text"
              placeholder="搜尋訂單編號..."
              className="buyer-orders__search-input"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>

        {/* 分頁標籤 */}
        <div className="buyer-orders__tabs">
          {STATUS_TABS.map(tab => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`buyer-orders__tab ${activeTab === tab.id ? 'buyer-orders__tab--active' : ''}`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* 訂單列表 */}
        <div className="buyer-orders__list">
          {filteredOrders.length > 0 ? (
            filteredOrders.map(order => (
              <div key={order.externalId} className="order-card">
                <div className="order-card__header">
                  <div className="order-card__info">
                    <span className="order-card__info-id">訂單編號：{order.externalId}</span>
                    <span className="order-card__info-divider">|</span>
                    <span className="order-card__info-date">{new Date(order.createdAt).toLocaleString()}</span>
                  </div>
                  <div className="order-card__status">
                    {getStatusDisplay(order.status)}
                  </div>
                </div>

                <div className="order-card__body">
                  {/* 🚀 這裡我們只顯示該訂單的第一個商品作為代表，或是你可以 map 過 orders.items */}
                  {order.items && order.items.length > 0 && (
                    <div className="order-card__product">
                      <div className="order-card__product-info">
                        <h3 className="order-card__product-name">{order.items[0].productName}</h3>
                        <p className="order-card__product-price">
                          NT$ {order.items[0].priceAtPurchase.toLocaleString()} x {order.items[0].quantity}
                        </p>
                      </div>
                    </div>
                  )}

                  <div className="order-card__total">
                    <span className="order-card__total-label">訂單總金額</span>
                    <span className="order-card__total-amount">NT$ {order.totalAmount.toLocaleString()}</span>
                  </div>
                </div>
              </div>
            ))
          ) : (
            <div className="buyer-orders__empty">
              <Package className="buyer-orders__empty-icon" />
              <p>找不到符合條件的訂單</p>
            </div>
          )}
        </div>
      </main>

    </div>
  );
}
