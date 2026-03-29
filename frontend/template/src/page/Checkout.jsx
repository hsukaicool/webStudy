import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { ArrowLeft, CheckCircle, MapPin, User, Loader2 } from 'lucide-react';

import orderApi from '../api/services/orderApi';
import axiosClient, { BASE_URL } from '../api/axiosClient';

export default function Checkout() {
  const navigate = useNavigate();
  const location = useLocation();
  const [isSubmitting, setIsSubmitting] = useState(false);

  // 🚀 關鍵：從 location.state 拿資料
  const checkoutItems = location.state?.items || [];

  const subtotal = checkoutItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
  const shipping = checkoutItems.length > 0 ? 30 : 0;
  const total = subtotal + shipping;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (checkoutItems.length === 0) return;

    setIsSubmitting(true);
    try {
      // 1. 先整理出明細清單
      const orderItems = checkoutItems.map(item => ({
        productExternalId: item.productExternalId,
        quantity: item.quantity
      }));

      // 🚀 關鍵修正：包裝成後端 OrderRequest 期待的格式 { items: [...] }
      const payload = {
        items: orderItems
      };

      // 2. 傳送包裝好的 payload
      await orderApi.createOrder(payload);
      
      navigate('/orders');
    } catch (error) {
      console.error("下單失敗:", error);
      if (error.response?.status === 401) {
        alert("登入逾時，請重新登入");
        navigate("/login");
      } else {
        alert('🎉 訂單建立成功！');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  if (checkoutItems.length === 0) {
    return (
      <div className="checkout">
        <main className="checkout__main" style={{ textAlign: 'center' }}>
          <p>結帳清單是空的，去逛逛吧！</p>
          <Link to="/" style={{ color: '#ff8a00' }}>回首頁</Link>
        </main>
      </div>
    );
  }

  return (
    <div className="checkout">

      <main className="checkout__main">
        <div className="checkout__header">
          <Link to="/cart" className="checkout__back-link">
            <ArrowLeft className="w-6 h-6" />
          </Link>
          <h1 className="checkout__title">結帳</h1>
        </div>

        <form onSubmit={handleSubmit} className="checkout__grid">
          <div className="checkout__form-column">
            {/* 收件人資訊 */}
            <section className="checkout__section">
              <h2 className="checkout__section-title">
                <User /> 收件人資訊
              </h2>
              <div className="checkout__form-grid checkout__form-grid--2-col">
                <div className="checkout__form-group">
                  <label className="checkout__label">姓名 <span>*</span></label>
                  <input type="text" required placeholder="請輸入真實姓名" className="checkout__input" />
                </div>
                <div className="checkout__form-group">
                  <label className="checkout__label">手機號碼 <span>*</span></label>
                  <input type="tel" required placeholder="0912345678" className="checkout__input" />
                </div>
              </div>
            </section>

            {/* 運送地址 */}
            <section className="checkout__section">
              <h2 className="checkout__section-title">
                <MapPin /> 運送地址
              </h2>
              <div className="checkout__form-grid checkout__form-grid--2-col">
                <div className="checkout__form-group">
                  <label className="checkout__label">縣市 <span>*</span></label>
                  <select required className="checkout__select" defaultValue="">
                    <option value="" disabled>請選擇縣市</option>
                    <option value="taipei">台北市</option>
                    <option value="new_taipei">新北市</option>
                    <option value="kaohsiung">高雄市</option>
                  </select>
                </div>
                <div className="checkout__form-group">
                  <label className="checkout__label">鄉鎮市區 <span>*</span></label>
                  <input type="text" required placeholder="例如：信義區" className="checkout__input" />
                </div>
                <div className="checkout__form-group checkout__form-group--full">
                  <label className="checkout__label">詳細地址 <span>*</span></label>
                  <input type="text" required placeholder="街道門牌資訊" className="checkout__input" />
                </div>
              </div>
            </section>
          </div>

          {/* 訂單摘要右側欄 */}
          <div className="checkout__summary-column">
            <div className="checkout__summary-card">
              <h2 className="checkout__summary-title">訂單明細</h2>
              <div className="checkout__item-list">
                {checkoutItems.map((item, idx) => (
                  <div key={idx} className="checkout__item">
                    <div className="checkout__item-img-wrapper">
                      <img
                        src={item.imageUrl?.startsWith('http') ? item.imageUrl : `${BASE_URL}${item.imageUrl}`}
                        alt={item.name}
                        className="checkout__item-img"
                      />
                    </div>
                    <div className="checkout__item-content">
                      <h3 className="checkout__item-name">{item.name}</h3>
                      <div className="checkout__item-meta">
                        <span className="checkout__item-quantity">x{item.quantity}</span>
                        <span className="checkout__item-price">${item.price?.toLocaleString()}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              <div className="checkout__calc-box">
                <div className="checkout__calc-row">
                  <span>商品小計</span>
                  <span>${subtotal.toLocaleString()}</span>
                </div>
                <div className="checkout__calc-row">
                  <span>運費</span>
                  <span>${shipping.toLocaleString()}</span>
                </div>
                <div className="checkout__calc-row checkout__calc-row--total">
                  <span>總計</span>
                  <span className="checkout__total-amount">${total.toLocaleString()}</span>
                </div>
              </div>

              <button type="submit" disabled={isSubmitting} className="checkout__submit-btn">
                {isSubmitting ? (
                  <Loader2 className="w-5 h-5 animate-spin" />
                ) : (
                  <CheckCircle className="w-5 h-5" />
                )}
                {isSubmitting ? '正在建立訂單...' : '確認下單'}
              </button>
              <p className="checkout__terms-hint">下單即表示同意服務條款</p>
            </div>
          </div>
        </form>
      </main>
    </div>
  );
}
