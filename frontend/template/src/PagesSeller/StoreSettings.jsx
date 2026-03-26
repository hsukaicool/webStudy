import React, { useState, useEffect } from 'react';
import { Camera, Save, Upload, Edit, X } from 'lucide-react';
import { useAuth } from '../api/context/AuthContext';
import sellerApi from '../api/seller/sellerApi';
import '../scss/pagesScss/_storeSettings.scss';

import Cropper from 'react-easy-crop';
import { getCroppedImg } from '../api/utils/canvasUtils';

export default function StoreSettings() {

  const { sellerInfo, fetchSellerInfo } = useAuth();
  const [loading, setLoading] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [avatarFile, setAvatarFile] = useState(null);
  const [bannerFile, setBannerFile] = useState(null);

  // 裁剪功能
  const [imageToCrop, setImageToCrop] = useState(null);
  const [cropType, setCropType] = useState(null); // 'avatar' 或 'banner'
  const [crop, setCrop] = useState({ x: 0, y: 0 });
  const [zoom, setZoom] = useState(1);
  const [croppedAreaPixels, setCroppedAreaPixels] = useState(null);

  // 🚀 處理圖片選擇 (範例)
  const handleAvatarChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setCropType('avatar');
      const reader = new FileReader();
      reader.addEventListener('load', () => setImageToCrop(reader.result));
      reader.readAsDataURL(file);
    }
  };

  // 新增橫幅處理函式
  const handleBannerChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setCropType('banner');
      const reader = new FileReader();
      reader.addEventListener('load', () => setImageToCrop(reader.result));
      reader.readAsDataURL(file);
    }
  };

  const [formData, setFormData] = useState({
    shopName: '',
    shopDescription: '',
    avatarUrl: '',
    bannerUrl: '',
    taxId: '',
    servicePhone: '',
    status: 'ACTIVE' // 追蹤營業狀態
  });

  // 當 AuthContext 資料更新時，同步到本地表單
  useEffect(() => {
    if (sellerInfo) {
      setFormData({
        shopName: sellerInfo.shopName || '',
        shopDescription: sellerInfo.shopDescription || '',
        avatarUrl: sellerInfo.avatarUrl || '',
        bannerUrl: sellerInfo.bannerUrl || '',
        taxId: sellerInfo.taxId || '',
        servicePhone: sellerInfo.servicePhone || '',
        status: sellerInfo.status || 'ACTIVE'
      });
    }
  }, [sellerInfo]);

  const handleSave = async () => {
    setLoading(true);
    try {
      await sellerApi.updateProfile(formData, avatarFile, bannerFile);
      await fetchSellerInfo(); // 重新獲取最新資料以同步全域
      setIsEditing(false); // 儲存成功後切回檢視模式
      setAvatarFile(null); // 上傳完後清空
      setBannerFile(null);
      alert('賣場設定已儲存！');
    } catch (error) {
      console.error('儲存失敗:', error);
      alert('儲存失敗，請檢查網路或稍後再試');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    if (sellerInfo) {
      setFormData({
        shopName: sellerInfo.shopName || '',
        shopDescription: sellerInfo.shopDescription || '',
        avatarUrl: sellerInfo.avatarUrl || '',
        bannerUrl: sellerInfo.bannerUrl || '',
        taxId: sellerInfo.taxId || '',
        servicePhone: sellerInfo.servicePhone || '',
        status: sellerInfo.status || 'ACTIVE'
      });
    }
    setIsEditing(false);
  };

  /**
   * 🚀 營業狀態切換
   * 邏輯：在編輯模式下才允許點擊切換，並同步至本地狀態。
   */
  const handleStatusToggle = () => {
    if (!isEditing) return;
    const nextStatus = formData.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    setFormData({ ...formData, status: nextStatus });
  };


  const handleCropConfirm = async () => {
    try {
      const croppedImageBlob = await getCroppedImg(imageToCrop, croppedAreaPixels);
      const croppedFile = new File([croppedImageBlob], `${cropType}.jpg`, { type: 'image/jpeg' });
      const previewUrl = URL.createObjectURL(croppedImageBlob);
      if (cropType === 'avatar') {
        setAvatarFile(croppedFile);
        setFormData({ ...formData, avatarUrl: previewUrl });
      } else {
        setBannerFile(croppedFile);
        setFormData({ ...formData, bannerUrl: previewUrl });
      }
      setImageToCrop(null); // 關閉視窗
    } catch (error) {
      console.error("裁切失敗", error);
    }
  };

  return (
    <div className="store-settings">

      {/* 封面圖片區域 */}
      <div className={`store-settings__cover ${isEditing ? 'store-settings__cover--editing' : ''}`}>
        <img src={formData.bannerUrl || "https://images.unsplash.com/photo-1542273917363-3b1817f69a2d?q=80&w=1200&auto=format&fit=crop"} alt="Cover" className="store-settings__cover-img" />

        {isEditing && (
          <div
            className="store-settings__cover-overlay"
            onClick={() => document.getElementById('banner-input').click()} // 🚀 點擊觸發
          >
            <div className="store-settings__cover-icon-box">
              <Camera className="store-settings__icon" size={24} />
            </div>
            <span className="store-settings__cover-hint">點擊更換封面</span>
          </div>
        )}

        {/* 🚀 隱藏的橫幅輸入框 */}
        <input type="file" id="banner-input" hidden onChange={handleBannerChange} accept="image/*" />


        {/* 大頭貼容器 */}
        <div className="store-settings__avatar-container">
          <div className={`store-settings__avatar-wrapper ${isEditing ? 'store-settings__avatar-wrapper--editing' : ''}`}>
            <img src={formData.avatarUrl || "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=200&auto=format&fit=crop"} alt="Logo" className="store-settings__avatar-img" />

            {isEditing && (
              <div
                className="store-settings__avatar-overlay"
                onClick={() => document.getElementById('avatar-input').click()} // 🚀 點擊觸發
              >
                <Upload className="store-settings__icon" size={24} />
              </div>
            )}
          </div>
          {/* 🚀 隱藏的大頭貼輸入框 */}
          <input type="file" id="avatar-input" hidden onChange={handleAvatarChange} accept="image/*" />
        </div>
      </div>



      <div className="store-settings__grid">
        {/* 左側主要表單 */}
        <div className="store-settings__main-col">

          <section className="store-settings__section">
            <div className="store-settings__section-header">
              <h2 className="store-settings__section-title">基本資料</h2>
              {!isEditing && (
                <button
                  onClick={() => setIsEditing(true)}
                  className="store-settings__edit-btn"
                >
                  <Edit size={16} />
                  編輯賣場
                </button>
              )}
            </div>

            <div className="store-settings__field-group">
              <div className="store-settings__field">
                <label className="store-settings__label">賣場名稱</label>
                {isEditing ? (
                  <input
                    type="text"
                    value={formData.shopName}
                    onChange={(e) => setFormData({ ...formData, shopName: e.target.value })}
                    className="store-settings__input"
                    placeholder="請輸入店鋪名稱"
                  />
                ) : (
                  <p className="store-settings__value">{formData.shopName || '未設定名稱'}</p>
                )}
              </div>

              <div className="store-settings__field">
                <label className="store-settings__label">賣場介紹</label>
                {isEditing ? (
                  <textarea
                    rows={4}
                    value={formData.shopDescription}
                    onChange={(e) => setFormData({ ...formData, shopDescription: e.target.value })}
                    className="store-settings__textarea"
                    placeholder="向買家介紹您的店鋪..."
                  />
                ) : (
                  <p className="store-settings__value store-settings__value--long-text">
                    {formData.shopDescription || '尚未填寫自我介紹'}
                  </p>
                )}
              </div>
            </div>
          </section>

          <section className="store-settings__section">
            <h2 className="store-settings__section-title">商務資訊</h2>

            <div className="store-settings__field-group">
              <div className="store-settings__field">
                <label className="store-settings__label">客服電話</label>
                {isEditing ? (
                  <input
                    type="text"
                    value={formData.servicePhone}
                    onChange={(e) => setFormData({ ...formData, servicePhone: e.target.value })}
                    className="store-settings__input"
                    placeholder="例如：02-23456789"
                  />
                ) : (
                  <p className="store-settings__value">{formData.servicePhone || '未設定'}</p>
                )}
              </div>
              <div className="store-settings__field">
                <label className="store-settings__label">統一編號 (選填)</label>
                {isEditing ? (
                  <input
                    type="text"
                    value={formData.taxId}
                    onChange={(e) => setFormData({ ...formData, taxId: e.target.value })}
                    className="store-settings__input"
                    placeholder="8 位數字統編"
                  />
                ) : (
                  <p className="store-settings__value">{formData.taxId || '未設定'}</p>
                )}
              </div>
            </div>
          </section>

          {isEditing && (
            <div className="store-settings__actions">
              <button
                onClick={handleCancel}
                className="store-settings__btn store-settings__btn--ghost"
              >
                <X size={20} />
                取消變更
              </button>
              <button
                onClick={handleSave}
                disabled={loading}
                className="store-settings__btn store-settings__btn--primary"
              >
                {loading ? (
                  <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                ) : (
                  <Save size={20} />
                )}
                {loading ? '儲存中...' : '儲存設定'}
              </button>
            </div>
          )}
        </div>

        {/* 右側側邊欄資訊 */}
        <aside className="store-settings__sidebar">
          <div className="store-settings__status-card">
            <h3 className="store-settings__status-header">賣場狀態</h3>

            <div className="store-settings__status-list">
              <div className="store-settings__status-item">
                <span className="store-settings__status-label">
                  {formData.status === 'ACTIVE' ? '營業中' : '休息中'}
                </span>
                <div
                  onClick={handleStatusToggle}
                  className={`store-settings__switch ${formData.status === 'ACTIVE' ? 'active' : ''} ${isEditing ? 'store-settings__switch--editable' : ''}`}
                ></div>
              </div>

              <div className="store-settings__status-item store-settings__status-item--divider"></div>

              <div className="store-settings__status-item">
                <span className="store-settings__status-meta">加入時間</span>
                <span className="store-settings__status-value">
                  {sellerInfo?.createdAt ? new Date(sellerInfo.createdAt).toLocaleDateString() : 'N/A'}
                </span>
              </div>

              <div className="store-settings__status-item">
                <span className="store-settings__status-meta">店鋪評分</span>
                <span className="store-settings__status-value" style={{ color: '#10b981' }}>5.0 / 5.0</span>
              </div>
            </div>
          </div>
        </aside>
      </div>
      {imageToCrop && (
        <div className="crop-modal">
          <div className="crop-modal__content">
            <div className="crop-modal__container">
              <Cropper
                image={imageToCrop}
                crop={crop}
                zoom={zoom}
                aspect={cropType === 'avatar' ? 1 / 1 : 3 / 1} // 🚀 大頭貼 1:1, 橫幅 3:1
                cropShape={cropType === 'avatar' ? 'round' : 'rect'}
                onCropChange={setCrop}
                onCropComplete={(a, p) => setCroppedAreaPixels(p)}
                onZoomChange={setZoom}
              />
            </div>
            <div className="crop-modal__controls">
              <input type="range" value={zoom} min={1} max={3} step={0.1} onChange={(e) => setZoom(e.target.value)} className="crop-modal__slider" />
              <div className="crop-modal__actions">
                <button className="btn-cancel" onClick={() => setImageToCrop(null)}>取消</button>
                <button className="btn-save" onClick={handleCropConfirm}>確認裁切</button>
              </div>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
