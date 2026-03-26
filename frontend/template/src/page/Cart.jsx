import React, { useState, useEffect, useMemo } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Trash2, Minus, Plus, ShieldCheck, ArrowRight, ShoppingBag, Store } from 'lucide-react';
import cartApi from '../api/services/cartApi'; // 🌟 引入我們剛剛寫好的 cartApi
import '../scss/pagesScss/_cart.scss';
export default function Cart() {
  const navigate = useNavigate();
  const [cartItems, setCartItems] = useState([]); // 🌟 預設為空陣列
  const [selectedItemIds, setSelectedItemIds] = useState([]);
  const [isLoading, setIsLoading] = useState(true); // 🌟 新增讀取狀態
  // 🌟 1. 取得購物車資料 (API 串接)
  const fetchCartItems = async () => {
    try {
      setIsLoading(true);
      const res = await cartApi.getMyCart();
      // 👈 這裡拿到的 res 已經是陣列了 (被 interceptor 攔截處理過)
      const flattenedItems = [];

      console.log("🔍 後端購物車回傳的原始資料：", JSON.stringify(res, null, 2));

      // 將後端回傳的 List<CartGroupResponse> 攤平為你原本的格式
      res.forEach(group => {
        group.items.forEach(item => {
          flattenedItems.push({
            id: item.cartItemId,               // 後端的 cartItemId
            productExternalId: item.productExternalId, // 把商品的 externalId 保留下來
            seller: group.sellerName,          // 賣家名稱
            name: item.productName,            // 商品名稱
            price: item.price,          // 這裡請確認後端對應的報價欄位
            quantity: item.quantity,           // 數量
            image: item.imageUrl || 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8',
            condition: item.condition || '標準狀態'
          });
        });
      });

      setCartItems(flattenedItems);
      setSelectedItemIds(flattenedItems.map(i => i.id)); // 預設全選
    } catch (error) {
      console.error('取得購物車失敗:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleGoToCheckout = () => {
    // 🚀 1. 確保有選中商品（雖然按鈕有 disabled，但多一層判斷更安全）
    if (selectedItems.length === 0) return;

    // 🚀 2. 格式化資料：確保欄位名稱與結帳頁、詳情頁完全對齊
    const checkoutPayload = selectedItems.map(item => ({
      productExternalId: item.productExternalId || item.externalId, // 支撐不同來源的 ID 命名
      name: item.productName || item.name,
      price: item.price,
      quantity: item.quantity,
      imageUrl: item.image
    }));

    // 🚀 3. 帶上資料包裹跳轉
    navigate("/checkout", {
      state: { items: checkoutPayload }
    });
  };


  // 🌟 畫面初始載入時打 API
  useEffect(() => {
    fetchCartItems();
  }, []);
  // 你原本的分組邏輯保持不變（完美契合！）
  const groupedItems = useMemo(() => {
    return cartItems.reduce((acc, item) => {
      if (!acc[item.seller]) {
        acc[item.seller] = [];
      }
      acc[item.seller].push(item);
      return acc;
    }, {});
  }, [cartItems]);
  // 🌟 2. 更新購物車數量：樂觀更新 (Optimistic Update)
  const updateQuantity = async (id, delta) => {
    const targetItem = cartItems.find(i => i.id === id);
    if (!targetItem) return;

    const newQuantity = Math.max(1, targetItem.quantity + delta);

    // 【前端先反應】，使用者不會感覺卡頓
    setCartItems(items =>
      items.map(item => item.id === id ? { ...item, quantity: newQuantity } : item)
    );
    // 【背景呼叫 API】
    try {
      await cartApi.updateQuantity(id, newQuantity);
    } catch (error) {
      console.error('更新數量失敗:', error);
      fetchCartItems(); // 若 API 失敗，重新拉取資料把畫面復原
    }
  };
  // 🌟 3. 移除購物車項目：樂觀更新
  const removeItem = async (id) => {
    // 【前端先刪除】
    setCartItems(items => items.filter(item => item.id !== id));
    setSelectedItemIds(ids => ids.filter(itemId => itemId !== id));
    // 【背景呼叫 API】
    try {
      await cartApi.removeFromCart(id);
    } catch (error) {
      console.error('移除失敗:', error);
      fetchCartItems(); // 若失敗，還原畫面
    }
  };


  const toggleItemSelection = (id) => {
    setSelectedItemIds(prev =>
      prev.includes(id) ? prev.filter(itemId => itemId !== id) : [...prev, id]
    );
  };

  const toggleSellerSelection = (seller) => {
    const sellerItemIds = groupedItems[seller].map(item => item.id);
    const allSelected = sellerItemIds.every(id => selectedItemIds.includes(id));

    if (allSelected) {
      setSelectedItemIds(prev => prev.filter(id => !sellerItemIds.includes(id)));
    } else {
      setSelectedItemIds(prev => {
        const newIds = new Set([...prev, ...sellerItemIds]);
        return Array.from(newIds);
      });
    }
  };

  const toggleAll = () => {
    if (selectedItemIds.length === cartItems.length && cartItems.length > 0) {
      setSelectedItemIds([]);
    } else {
      setSelectedItemIds(cartItems.map(item => item.id));
    }
  };

  const selectedItems = cartItems.filter(item => selectedItemIds.includes(item.id));
  const subtotal = selectedItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
  const selectedSellers = new Set(selectedItems.map(item => item.seller));
  const shipping = selectedSellers.size * 15;
  const total = subtotal + shipping;

  return (
    <div className="cart-page">


      <main className="cart-page__container">
        <h1 className="cart-page__title">購物車</h1>

        {cartItems.length === 0 ? (
          <div className="cart-empty">
            <div className="cart-empty__icon-box">
              <ShoppingBag className="cart-empty__icon" />
            </div>
            <h2 className="cart-empty__title">您的購物車是空的</h2>
            <p className="cart-empty__desc">看起來您還沒有將任何好物加入購物車。</p>
            <Link to="/" className="cart-empty__btn">
              去逛逛
            </Link>
          </div>
        ) : (
          <div className="cart-page__content">
            {/* Select All Bar */}
            <div className="cart-selection-bar">
              <input
                type="checkbox"
                checked={selectedItemIds.length === cartItems.length && cartItems.length > 0}
                ref={input => {
                  if (input) {
                    input.indeterminate = selectedItemIds.length > 0 && selectedItemIds.length < cartItems.length;
                  }
                }}
                onChange={toggleAll}
                className="cart-selection-bar__checkbox"
                id="selectAll"
              />
              <label htmlFor="selectAll" className="cart-selection-bar__label">
                全選 ({selectedItemIds.length}/{cartItems.length})
              </label>
            </div>

            {/* Grouped Cart Items */}
            <div className="cart-page__list">
              {Object.entries(groupedItems).map(([seller, items]) => {
                const sellerItemIds = items.map(i => i.id);
                const isAllSelected = sellerItemIds.every(id => selectedItemIds.includes(id));
                const isSomeSelected = sellerItemIds.some(id => selectedItemIds.includes(id)) && !isAllSelected;
                if (isLoading) {
                  return <div className="cart-page"><main className="cart-page__container"><h2>載入購物車中...</h2></main></div>;
                }
                return (
                  <div key={seller} className="cart-group">
                    {/* Seller Header */}
                    <div className="cart-group__header">
                      <input
                        type="checkbox"
                        checked={isAllSelected}
                        ref={input => { if (input) input.indeterminate = isSomeSelected; }}
                        onChange={() => toggleSellerSelection(seller)}
                        className="cart-group__checkbox"
                      />
                      <Store className="cart-group__seller-icon" />
                      <Link to="/store" className="cart-group__seller-link">
                        {seller}
                      </Link>
                    </div>

                    {/* Items */}
                    <div className="cart-group__items">
                      {items.map((item, index) => (
                        <div key={item.id} className="cart-item">
                          {/* Checkbox */}
                          <div className="cart-item__checkbox-box">
                            <input
                              type="checkbox"
                              checked={selectedItemIds.includes(item.id)}
                              onChange={() => toggleItemSelection(item.id)}
                              className="cart-item__checkbox"
                            />
                          </div>

                          {/* Product Image */}
                          <Link to="/product" className="cart-item__img-link">
                            <img src={item.image} alt={item.name} className="cart-item__img" />
                          </Link>

                          {/* Product Details */}
                          <div className="cart-item__details">
                            <div className="cart-item__header">
                              <div>
                                <Link to="/product" className="cart-item__name">
                                  {item.name}
                                </Link>
                                <div className="cart-item__condition">
                                  {item.condition}
                                </div>
                              </div>
                              <button
                                onClick={() => removeItem(item.id)}
                                className="cart-item__remove-btn"
                                title="移除商品"
                              >
                                <Trash2 size={20} />
                              </button>
                            </div>

                            <div className="cart-item__actions">
                              <div className="cart-item__price">
                                ${item.price.toLocaleString()}
                              </div>
                              <div className="quantity-ctrl">
                                <button
                                  onClick={() => updateQuantity(item.id, -1)}
                                  className="quantity-ctrl__btn"
                                  disabled={item.quantity <= 1}
                                >
                                  <Minus size={16} />
                                </button>
                                <span className="quantity-ctrl__value">
                                  {item.quantity}
                                </span>
                                <button
                                  onClick={() => updateQuantity(item.id, 1)}
                                  className="quantity-ctrl__btn"
                                >
                                  <Plus size={16} />
                                </button>
                              </div>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                );
              })}
            </div>

            {/* Order Summary */}
            <div className="cart-summary">
              <h2 className="cart-summary__title">訂單摘要</h2>

              <div className="cart-summary__body">
                <div className="cart-summary__row">
                  <span>已選商品小計 ({selectedItems.length} 件)</span>
                  <span className="cart-summary__row-value">${subtotal.toLocaleString()}</span>
                </div>
                <div className="cart-summary__row">
                  <span>運費 (依賣家計算)</span>
                  <span className="cart-summary__row-value">${shipping.toLocaleString()}</span>
                </div>
              </div>

              <div className="cart-summary__total-section">
                <div className="cart-summary__total-row">
                  <span className="cart-summary__total-label">總計</span>
                  <span className="cart-summary__total-amount">${total.toLocaleString()}</span>
                </div>
                <p className="cart-summary__tax-hint">包含所有稅費</p>
              </div>

              <div className="cart-summary__footer">
                <div className="cart-summary__protection">
                  <ShieldCheck className="cart-summary__protection-icon" />
                  <div>
                    <h4 className="cart-summary__protection-title">買家保障</h4>
                    <p className="cart-summary__protection-desc">
                      若商品未送達或與描述不符，您將獲得全額退款。
                    </p>
                  </div>
                </div>

                <div className="cart-summary__checkout-btn-wrapper">
                  <button
                    className="cart-summary__checkout-btn"
                    disabled={selectedItems.length === 0}
                    onClick={handleGoToCheckout} // 🚀 綁定新的跳轉邏輯
                  >
                    前往結帳 ({selectedItems.length}) <ArrowRight size={20} />
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
      </main>


    </div>
  );
}
