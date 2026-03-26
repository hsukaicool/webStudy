import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
// 匯入圖標庫：提升 UI 直覺感，lucide-react 是目前 React 社群主流的輕量化圖標庫
import { Plus, Search, Edit, Trash2, Loader2, PackageOpen, Eye, EyeOff } from 'lucide-react';
// 服務層抽象：將 API 請求邏輯抽離，符合 SoC (關注點分離) 原則，便於維護與測試
import productApi from '../api/services/productApi';
import '../scss/pagesScss/_sellerProducts.scss';

/**
 * 🚀 資料對照表 (Data Mapping)
 * 用途：將後端傳回的列舉值 (Enum) 轉換為前端顯示的友善中文。
 * 原因：後端資料庫通常存儲大寫英文以保持一致性，前端透過 Mapping Table 進行解碼與顯示。
 */
const CATEGORY_MAP = {
  'ELECTRONICS': '電子產品',
  'PHOTOGRAPHY': '攝影器材',
  'FURNITURE': '家具',
  'CLOTHING': '服飾配件',
  'BOOKS': '書籍文具',
  'OTHER': '其他'
};

export default function Products() {
  // --- 狀態管理 (State Management) ---
  // 存放原始商品資料清單
  const [productList, setProductList] = useState([]);
  // 控制載入狀態：確保在非同步資料回來前，使用者看到的是 Loading UI，提升 UX 體驗
  const [loading, setLoading] = useState(true);
  // 受控組件狀態：即時過濾功能的核心，記錄使用者輸入的搜尋詞與過濾條件
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');

  /**
   * 🚀 非同步資料獲取 (Side Effect Handling)
   * 用途：組件掛載 (Mount) 時自動觸發 API 請求。
   * 技術點：使用 async/await 處理 Promise，並配合 try-catch-finally 確保錯誤處理與載入狀態關閉。
   */
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setLoading(true);
        const data = await productApi.getMyProducts();
        setProductList(data);
      } catch (error) {
        // 錯誤邊界處理：實務中可結合 Toast 提示
        console.error('載入商品失敗:', error);
      } finally {
        // 無論成功或失敗，皆關閉載入動畫
        setLoading(false);
      }
    };
    fetchProducts();
  }, []); // 空依賴陣列：確保只在組件首次渲染時執行一次

  /**
   * 🚀 刪除商品處理邏輯
   * 包含二次確認與 API 呼叫，並在成功後本地更新狀態 (Optimistic UI 更新)。
   */
  const handleDelete = async (externalId, productName) => {
    if (window.confirm(`確定要刪除「${productName}」嗎？此操作無法復原。`)) {
      try {
        await productApi.deleteProduct(externalId);
        // 本地更新：直接從現有清單濾除已刪除的商品，不用重新打一次 API 撈全部資料
        setProductList(prev => prev.filter(p => p.externalId !== externalId));
        alert('刪除成功！');
      } catch (error) {
        console.error('刪除失敗:', error);
        alert('刪除失敗，請稍後再試。');
      }
    }
  };

  /**
   * 🚀 切換商品上下架狀態
   * 包含狀態切換判斷、API 呼叫與本地樂觀更新 (Optimistic UI Update)
   */
  const handleToggleStatus = async (externalId, currentStatus) => {
    // 若已售完不能切換，或只能切換上下架
    if (currentStatus === 'SOLD_OUT') {
      alert('已售完的商品無法直接切換狀態！請從編輯頁面處理。');
      return;
    }
    const newStatus = currentStatus === 'ON_SHELF' ? 'OFF_SHELF' : 'ON_SHELF';
    try {
      await productApi.updateProductStatus(externalId, newStatus);
      // 本地更新：直接改寫陣列內對應商品的狀態
      setProductList(prev => prev.map(p => 
        p.externalId === externalId ? { ...p, status: newStatus } : p
      ));
    } catch (error) {
      console.error('更新狀態失敗:', error);
      alert('狀態更新失敗，請檢查網路連線或稍後再試。');
    }
  };

  /**
   * 🚀 條件式渲染組件 (Conditional Styling)
   * 用途：根據商品狀態渲染不同的樣式標籤。
   * 原因：利用 BEM 命名規範 (--active/--soldout) 來動態切換 SCSS 樣式。
   */
  const getStatusBadge = (status) => {
    switch (status) {
      case 'ON_SHELF':
        return <span className="seller-products__badge seller-products__badge--active">上架中</span>;
      case 'OFF_SHELF':
        return <span className="seller-products__badge seller-products__badge--inactive">已下架</span>;
      case 'SOLD_OUT':
        return <span className="seller-products__badge seller-products__badge--soldout">已售完</span>;
      default:
        return <span className="seller-products__badge">{status}</span>;
    }
  };

  /**
   * 🚀 前端即時過濾邏輯 (Derived State)
   * 技術點：不額外發送 API，直接對現有資料進行篩選。
   * 優點：反應極快，減少後端負擔；利用 array.filter 進行多重條件判斷。
   */
  const filteredProducts = productList.filter(product => {
    // 忽略大小寫的名稱匹配
    const matchesSearch = product.name.toLowerCase().includes(searchTerm.toLowerCase());
    // 狀態過濾判斷：若為 'all' 則直接通過，否則比對狀態欄位
    const matchesStatus = statusFilter === 'all' || product.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  // 渲染 Loading 畫面：防止資料未到位導致頁面破圖或空白
  if (loading) {
    return (
      <div className="seller-products__loading">
        <Loader2 className="spinner" size={40} />
        <p>商品清單同步中...</p>
      </div>
    );
  }

  return (
    <div className="seller-products">
      {/* 🚀 頂部導航與過濾區：採用 Flexbox 佈局實現 RWD 自適應 */}
      <header className="seller-products__header">
        <div className="seller-products__search-group">
          <div className="seller-products__search-container">
            <Search className="seller-products__search-icon" size={18} />
            <input
              type="text"
              placeholder="搜尋商品名稱..."
              className="seller-products__search-input"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <select
            className="seller-products__filter"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="all">所有狀態</option>
            <option value="ON_SHELF">上架中</option>
            <option value="OFF_SHELF">已下架</option>
            <option value="SOLD_OUT">已售完</option>
          </select>
        </div>

        {/* 連結至新增頁面：使用 react-router-dom 的 Link 進行 SPA 無跳轉導航 */}
        <Link to="/seller/products/add" className="seller-products__add-btn">
          <Plus size={20} />
          新增商品
        </Link>
      </header>

      {/* 🚀 資料展示區：使用標準 Table 結構搭配 SCSS 自定義樣式 */}
      <div className="seller-products__table-container">
        <div className="seller-products__table-wrapper">
          <table className="seller-products__table">
            <thead>
              <tr>
                <th className="seller-products__th">商品詳細資訊</th>
                <th className="seller-products__th">售價</th>
                <th className="seller-products__th">庫存</th>
                <th className="seller-products__th">狀態</th>
                <th className="seller-products__th" style={{ textAlign: 'right' }}>管理操作</th>
              </tr>
            </thead>
            <tbody>
              {/* 🚀 優化後的空狀態 (Empty State UX) */}
              {filteredProducts.length === 0 ? (
                <tr>
                  <td colSpan="5">
                    <div className="seller-products__empty-state">
                      <div className="seller-products__empty-icon-box">
                        <PackageOpen size={48} />
                      </div>
                      <h3 className="seller-products__empty-title">
                        {searchTerm || statusFilter !== 'all' ? '找不到符合條件的商品' : '您的賣場還沒有商品'}
                      </h3>
                      <p className="seller-products__empty-text">
                        {searchTerm || statusFilter !== 'all' 
                          ? '請嘗試調整搜尋關鍵字或過濾條件' 
                          : '立即上架您的第一件商品，開啟您的創業之路！'}
                      </p>
                      {(!searchTerm && statusFilter === 'all') && (
                        <Link to="/seller/products/add" className="seller-products__empty-btn">
                          <Plus size={18} />
                          立即上架商品
                        </Link>
                      )}
                    </div>
                  </td>
                </tr>
              ) : (
                filteredProducts.map((product) => (
                  <tr key={product.externalId} className="seller-products__tr">
                    <td className="seller-products__td">
                      <div className="seller-products__info-cell">
                        {/* 圖片處理：若無提供 URL，顯示預設圖片 (Fallback Image) */}
                        <div className="seller-products__thumb">
                          <img
                            src={product.imageUrl || "https://images.unsplash.com/photo-default"}
                            alt={product.name}
                          />
                        </div>
                        <div className="seller-products__info-text">
                          <h3 className="seller-products__name">{product.name}</h3>
                          {/* 透過映射表顯示分類名稱 */}
                          <p className="seller-products__category">
                            {CATEGORY_MAP[product.category] || product.category}
                          </p>
                        </div>
                      </div>
                    </td>
                    <td className="seller-products__td">
                      {/* 國際化處理：toLocaleString() 自動加上千分位，提升數據閱讀性 */}
                      <span className="seller-products__price">
                        NT$ {product.price.toLocaleString()}
                      </span>
                    </td>
                    <td className="seller-products__td">
                      {/* 條件樣式：庫存為 0 時顯示紅色警告 */}
                      <span className={`seller-products__stock ${product.stock === 0 ? 'seller-products__stock--none' : 'seller-products__stock--normal'}`}>
                        {product.stock} 件
                      </span>
                    </td>
                    <td className="seller-products__td">
                      {getStatusBadge(product.status)}
                    </td>
                    <td className="seller-products__td">
                      <div className="seller-products__actions">
                        {/* 狀態切換按鈕 */}
                        <button 
                          onClick={() => handleToggleStatus(product.externalId, product.status)}
                          className="seller-products__action-btn seller-products__action-btn--toggle" 
                          title={product.status === 'ON_SHELF' ? '隱藏/下架' : '顯示/上架'}
                        >
                          {product.status === 'ON_SHELF' ? <EyeOff size={16} /> : <Eye size={16} />}
                        </button>
                        
                        <Link 
                          to={`/seller/products/edit/${product.externalId}`}
                          className="seller-products__action-btn seller-products__action-btn--edit" 
                          title="編輯"
                        >
                          <Edit size={16} />
                        </Link>
                        <button 
                          onClick={() => handleDelete(product.externalId, product.name)}
                          className="seller-products__action-btn seller-products__action-btn--delete" 
                          title="刪除"
                        >
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* 底部摘要：實時反饋資料總數 */}
        <div className="seller-products__pagination">
          <span>共找到 {filteredProducts.length} 筆商品</span>
        </div>
      </div>
    </div>
  );
}