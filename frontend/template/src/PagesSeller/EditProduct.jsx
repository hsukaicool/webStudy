import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Upload, X, ArrowLeft, Check, Loader2 } from 'lucide-react';
import productApi from '../api/services/productApi';
import '../scss/pagesScss/_addProduct.scss';

import Cropper from 'react-easy-crop';
import { getCroppedImg } from '../api/utils/canvasUtils';

export default function EditProduct() {
  const navigate = useNavigate();
  const { id } = useParams(); // 從網址取得商品 ID (externalId)
  
  const [loading, setLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [images, setImages] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null); // 🚀 存放實際檔案

  // 裁減圖片
  const [imageToCrop, setImageToCrop] = useState(null);
  const [crop, setCrop] = useState({ x: 0, y: 0 });
  const [zoom, setZoom] = useState(1);
  const [croppedAreaPixels, setCroppedAreaPixels] = useState(null);

  const [formData, setFormData] = useState({
    name: '',
    category: '',
    condition: 'NEW',
    price: '',
    stock: '1',
    description: ''
  });

  // 🚀 初始化時抓取現有商品資料
  useEffect(() => {
    const fetchProductData = async () => {
      try {
        setLoading(true);
        // 若後端有提供 /api/products/{id} 詳情端點，這裡可直接使用 getProductById
        // 目前暫時透過獲取賣家所有商品再來過濾 (Workaround)
        const products = await productApi.getMyProducts();
        const currentProduct = products.find(p => p.externalId === id);
        
        if (currentProduct) {
          setFormData({
            name: currentProduct.name,
            category: currentProduct.category,
            condition: currentProduct.condition,
            price: currentProduct.price.toString(),
            stock: currentProduct.stock.toString(),
            description: currentProduct.description || ''
          });
          // 設定既有圖片預覽
          if (currentProduct.imageUrl) {
            setImages([currentProduct.imageUrl]);
          }
        } else {
          alert("找不到該商品");
          navigate('/seller/products');
        }
      } catch (error) {
        console.error("無法取得商品資料: ", error);
        alert("資料載入失敗！");
        navigate('/seller/products');
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchProductData();
    }
  }, [id, navigate]);

  // 🚀 修改圖片選擇邏輯

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.addEventListener('load', () => setImageToCrop(reader.result));
      reader.readAsDataURL(file);
    }
  };

  const handleCropConfirm = async () => {
    try {
      const croppedImageBlob = await getCroppedImg(imageToCrop, croppedAreaPixels);
      const croppedFile = new File([croppedImageBlob], 'product_main.jpg', { type: 'image/jpeg' });
      const previewUrl = URL.createObjectURL(croppedImageBlob);
      setSelectedFile(croppedFile);
      setImages([previewUrl]); // 更新預覽圖
      setImageToCrop(null);    // 關閉裁切視窗
    } catch (error) {
      console.error("裁切失敗", error);
    }
  };


  const removeImage = (index) => {
    setImages(images.filter((_, i) => i !== index));
    setSelectedFile(null); // 🚀 同步清空檔案
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const payload = {
        name: formData.name,
        category: formData.category,
        condition: formData.condition,
        price: parseFloat(formData.price),
        stock: parseInt(formData.stock),
        description: formData.description,
        imageUrls: [] // 後端會處理圖片存儲後的 URL
      };
      // 💡 呼叫更新商品 API (注意傳入 externalId)
      await productApi.updateProduct(id, payload, selectedFile);
      alert('🎉 商品已成功更新！');
      navigate('/seller/products');
    } catch (error) {
      console.error('更新失敗:', error);
      alert('更新失敗，請檢查權限或資料格式');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '60vh', color: '#a8a29e' }}>
        <Loader2 className="spinner" size={40} style={{ animation: 'spin 1s linear infinite', marginBottom: '1rem', color: '#10b981' }} />
        <p>載入商品資料中...</p>
      </div>
    );
  }

  return (
    <div className="add-product">

      {/* Header */}
      <header className="add-product__header">
        <button
          onClick={() => navigate('/seller/products')}
          className="add-product__back-btn"
        >
          <ArrowLeft size={20} />
        </button>
        <div className="add-product__header-text">
          <h1 className="add-product__title">編輯商品</h1>
          <p className="add-product__subtitle">修改您的商品詳細資訊與圖片</p>
        </div>
      </header>

      <form onSubmit={handleSubmit} className="add-product__form">

        {/* 基本資訊 */}
        <section className="add-product__section">
          <h2 className="add-product__section-title">基本資訊</h2>

          <div className="add-product__grid">
            <div className="add-product__field add-product__field--full">
              <label className="add-product__label">
                商品名稱 <span className="required">*</span>
              </label>
              <input
                type="text"
                required
                placeholder="例如：Sony WH-1000XM4 無線降噪耳機"
                className="add-product__input"
                value={formData.name}
                onChange={e => setFormData({ ...formData, name: e.target.value })}
              />
            </div>

            <div className="add-product__field">
              <label className="add-product__label">
                商品分類 <span className="required">*</span>
              </label>
              <select
                required
                className="add-product__select"
                value={formData.category}
                onChange={e => setFormData({ ...formData, category: e.target.value })}
              >
                <option value="" disabled>選擇分類</option>
                <option value="ELECTRONICS">電子產品</option>
                <option value="PHOTOGRAPHY">攝影器材</option>
                <option value="FURNITURE">家具</option>
                <option value="CLOTHING">服飾配件</option>
                <option value="BOOKS">書籍文具</option>
                <option value="OTHER">其他</option>
              </select>
            </div>

            <div className="add-product__field">
              <label className="add-product__label">
                商品狀況 <span className="required">*</span>
              </label>
              <select
                required
                className="add-product__select"
                value={formData.condition}
                onChange={e => setFormData({ ...formData, condition: e.target.value })}
              >
                <option value="NEW">全新</option>
                <option value="LIKE_NEW">二手 - 極佳 (9成新以上)</option>
                <option value="GOOD">二手 - 良好 (有正常使用痕跡)</option>
                <option value="FAIR">二手 - 尚可 (有明顯使用痕跡)</option>
              </select>
            </div>
          </div>
        </section>

        {/* 價格與庫存 */}
        <section className="add-product__section">
          <h2 className="add-product__section-title">價格與庫存</h2>

          <div className="add-product__grid">
            <div className="add-product__field">
              <label className="add-product__label">
                售價 (NT$) <span className="required">*</span>
              </label>
              <div className="add-product__input-wrapper add-product__input-wrapper--has-prefix">
                <span className="prefix">$</span>
                <input
                  type="number"
                  required
                  min="0"
                  placeholder="0"
                  className="add-product__input"
                  value={formData.price}
                  onChange={e => setFormData({ ...formData, price: e.target.value })}
                />
              </div>
            </div>

            <div className="add-product__field">
              <label className="add-product__label">
                庫存數量 <span className="required">*</span>
              </label>
              <input
                type="number"
                required
                min="1"
                className="add-product__input"
                value={formData.stock}
                onChange={e => setFormData({ ...formData, stock: e.target.value })}
              />
            </div>
          </div>
        </section>

        {/* 商品圖片 */}
        <section className="add-product__section">
          <div className="add-product__image-header">
            <h2 className="add-product__section-title">商品圖片</h2>
            <span className="add-product__image-count">{images.length} / 5</span>
          </div>

          <div className="add-product__image-grid">
            {images.map((img, idx) => (
              <div key={idx} className="add-product__image-item">
                <img src={img} alt={`Preview ${idx}`} />
                <button
                  type="button"
                  onClick={() => removeImage(idx)}
                  className="add-product__remove-image"
                >
                  <X size={16} />
                </button>
                {idx === 0 && (
                  <div className="add-product__cover-badge">封面圖</div>
                )}
              </div>
            ))}

            {images.length < 5 && (
              <label className="add-product__upload-label">
                <Upload className="icon" />
                <span className="text">上傳圖片</span>
                <input
                  type="file"
                  accept="image/*"
                  className="hidden"
                  onChange={handleImageUpload}
                />
              </label>
            )}
          </div>
        </section>

        {/* 商品描述 */}
        <section className="add-product__section">
          <h2 className="add-product__section-title">商品描述</h2>

          <div className="add-product__field">
            <label className="add-product__label">
              詳細說明 <span className="required">*</span>
            </label>
            <textarea
              required
              placeholder="請詳細描述商品的狀況、規格、配件等資訊..."
              className="add-product__textarea"
              value={formData.description}
              onChange={e => setFormData({ ...formData, description: e.target.value })}
            />
          </div>
        </section>

        {/* Actions */}
        <div className="add-product__actions">
          <button
            type="button"
            onClick={() => navigate('/seller/products')}
            className="add-product__btn add-product__btn--ghost"
          >
            取消
          </button>
          <button
            type="submit"
            disabled={isSubmitting}
            className="add-product__btn add-product__btn--primary"
          >
            {isSubmitting ? (
              <div className="add-product__spinner" />
            ) : (
              <Check size={20} />
            )}
            {isSubmitting ? '更新中...' : '儲存變更'}
          </button>
        </div>
      </form>
      {/* 在 </div> 閉合標籤之前加入 */}
      {imageToCrop && (
        <div className="crop-modal">
          <div className="crop-modal__content">
            <div className="crop-modal__container">
              <Cropper
                image={imageToCrop}
                crop={crop}
                zoom={zoom}
                aspect={1 / 1} // 🚀 商品圖片固定 1:1 正方形
                onCropChange={setCrop}
                onCropComplete={(a, p) => setCroppedAreaPixels(p)}
                onZoomChange={setZoom}
              />
            </div>
            <div className="crop-modal__controls">
              <input type="range" value={zoom} min={1} max={3} step={0.1} onChange={(e) => setZoom(e.target.value)} className="crop-modal__slider" />
              <div className="crop-modal__actions">
                <button type="button" className="btn-cancel" onClick={() => setImageToCrop(null)}>取消</button>
                <button type="button" className="btn-save" onClick={handleCropConfirm}>確認裁切</button>
              </div>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
