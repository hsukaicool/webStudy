import React, { useState, useEffect } from 'react';
import { Search, MessageSquare, XCircle, Package, Truck, CheckCircle, Ban } from 'lucide-react';
import '../scss/pagesScss/_sellerOrders.scss';
import orderApi from '../api/services/orderApi';

// Mock data (維持原有資料)


const STATUS_TABS = [
  { id: 'all', label: '全部訂單' },
  { id: 'pending', label: '待出貨' },
  { id: 'shipping', label: '運送中' },
  { id: 'completed', label: '已完成' },
  { id: 'cancelled', label: '已取消' },
];

export default function Orders() {
  const [activeTab, setActiveTab] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [orders, setOrders] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  // 從後端撈真實資料
  useEffect(() => {
    const fetchSales = async () => {
      try {
        setIsLoading(true);
        const data = await orderApi.getMySales();
        setOrders(data);
      } catch (error) {
        console.error("載入銷售明細失敗", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchSales();
  }, []);


  const getStatusDisplay = (status) => {
    switch (status) {
      case 'PENDING':
        return <span className="seller-orders__status seller-orders__status--pending"><Package size={14} /> 待出貨</span>;
      case 'SHIPPING':
        return <span className="seller-orders__status seller-orders__status--shipping"><Truck size={14} /> 運送中</span>;
      case 'COMPLETED':
        return <span className="seller-orders__status seller-orders__status--completed"><CheckCircle size={14} /> 已完成</span>;
      case 'CANCELLED':
        return <span className="seller-orders__status seller-orders__status--cancelled"><Ban size={14} /> 已取消</span>;
      default:
        return null;
    }
  };

  const filteredOrders = orders.filter(order => {
    const matchesTab = activeTab === 'all' || order.orderStatus?.toLowerCase() === activeTab;
    const matchesSearch =
      order.orderExternalId?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      order.buyerName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      order.productName?.toLowerCase().includes(searchTerm.toLowerCase());
    return matchesTab && matchesSearch;
  });


  if (isLoading) return <div className="loading">讀取中...</div>;
  return (
    <div className="seller-orders">

      {/* 頂部搜尋 */}
      <div className="seller-orders__header">
        <div className="seller-orders__search-container">
          <Search className="seller-orders__search-icon" size={18} />
          <input
            type="text"
            placeholder="搜尋訂單編號、買家或商品名稱..."
            className="seller-orders__search-input"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {/* 狀態切換 */}
      <div className="seller-orders__tabs">
        {STATUS_TABS.map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`seller-orders__tab ${activeTab === tab.id ? 'seller-orders__tab--active' : ''}`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* 訂單列表 */}
      <div className="seller-orders__list">
        {filteredOrders.length > 0 ? (
          filteredOrders.map(order => (
            <div key={order.orderExternalId} className="seller-orders__item">

              {/* 訂單頭部 */}
              <div className="seller-orders__item-header">
                <div className="seller-orders__buyer">
                  <span className="seller-orders__buyer-name">{order.buyerName}</span>
                  <div className="seller-orders__item-meta">
                    <span className="divider">|</span>
                    <span>編號：{order.orderExternalId}</span>
                    {/* 日期 */}
                    <span className="divider">|</span>
                    <span>{new Date(order.createdAt).toLocaleString()}</span>
                  </div>
                </div>
                {/* 狀態 */}
                <div className="seller-orders__status-box">
                  {getStatusDisplay(order.orderStatus)}
                </div>
              </div>

              {/* 訂單內容 */}
              <div className="seller-orders__item-body">
                <div className="seller-orders__product-info">
                  <div className="seller-orders__product-thumb">
                    <img src={order.imageUrl} alt={order.productName} />
                  </div>
                  <div className="seller-orders__product-details">
                    <h3 className="seller-orders__product-name">{order.productName}</h3>
                    <p className="seller-orders__product-price">NT$ {order.priceAtPurchase?.toLocaleString()} x {order.quantity}</p>
                  </div>
                </div>

                <div className="seller-orders__total-box">
                  <span className="seller-orders__total-label">訂單總金額</span>
                  <span className="seller-orders__total-amount">NT$ {(order.priceAtPurchase * order.quantity)?.toLocaleString()}</span>
                </div>
              </div>

              {/* 操作按鈕 */}
              {/* <div className="seller-orders__item-footer">
                <button className="seller-orders__btn seller-orders__btn--secondary">
                  <MessageSquare size={16} />
                  聯絡買家
                </button>

                {(order.orderStatus === 'PENDING' || order.orderStatus === 'SHIPPING') && (
                  <button className="seller-orders__btn seller-orders__btn--outline-danger">
                    <XCircle size={16} />
                    取消訂單
                  </button>
                )}

                {order.status === 'pending' && (
                  <button className="seller-orders__btn seller-orders__btn--primary">
                    安排出貨
                  </button>
                )}
              </div>*/}
            </div>
          ))
        ) : (
          <div className="seller-orders__empty">
            <Package className="seller-orders__empty-icon" />
            <p>找不到符合條件的訂單</p>
          </div>
        )}
      </div>
    </div>
  );
}
