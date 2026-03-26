import { userApi } from '../api/services/userApi';
import { useState, useEffect } from 'react';
import { User, Mail, Phone, Calendar, Camera } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

import Cropper from 'react-easy-crop';
import { getCroppedImg } from '../api/utils/canvasUtils';
// import '../scss/pagesScss/Profile.scss'; // 👈 引入樣式檔案

export default function Profile() {

    const [isEditing, setIsEditing] = useState(false);
    const navigate = useNavigate();

    // 裁切相關狀態
    const [imageToCrop, setImageToCrop] = useState(null); // 選取的原始圖片網址
    const [crop, setCrop] = useState({ x: 0, y: 0 });
    const [zoom, setZoom] = useState(1);
    const [croppedAreaPixels, setCroppedAreaPixels] = useState(null);

    // 狀態
    const [profileData, setProfileData] = useState({
        displayName: '', // 來自 user 實體
        email: '',
        phoneNumber: '',
        bio: '',
        location: '',
        birthday: '',
        gender: 'SECRET',
        avatarUrl: ''
    });

    // 獲取使用者個人資料
    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const data = await userApi.getProfile();
                // 直接解構後端傳來的整合資料
                setProfileData({
                    displayName: data.displayName || '',
                    email: data.email || '',
                    phoneNumber: data.phoneNumber || '',
                    bio: data.bio || '',
                    location: data.location || '',
                    birthday: data.birthday || '',
                    gender: data.gender || 'SECRET',
                    avatarUrl: data.avatarUrl || ''
                });
            } catch (error) {
                console.error("獲取資料失敗", error);
            }
        };
        fetchProfile();
    }, []);


    // 處理表單輸入
    const handleChange = (e) => {
        const { name, value } = e.target;
        setProfileData(prev => ({ ...prev, [name]: value }));
    };

    // 儲存個人資料
    const handleSave = async () => {
        try {
            const requestBody = {
                displayName: profileData.displayName,
                phoneNumber: profileData.phoneNumber,
                bio: profileData.bio,
                location: profileData.location,
                birthday: profileData.birthday,
                gender: profileData.gender
            };
            await userApi.updateProfile(requestBody);
            alert("儲存成功！");
            setIsEditing(false); //儲存成功後自動切換回唯讀模式
        } catch (error) {
            alert("儲存失敗：" + error.response?.data || error.message);
        }
    };


    // 🚀 修改大頭貼選取邏輯
    const handleAvatarChange = (e) => {
        const file = e.target.files[0];
        if (!file) return;

        // 讀取檔案並轉為暫時網址以供 Cropper 顯示
        const reader = new FileReader();
        reader.addEventListener('load', () => setImageToCrop(reader.result));
        reader.readAsDataURL(file);
    };
    // 🚀 當裁切移動停止時儲存座標
    const onCropComplete = (croppedArea, croppedAreaPixels) => {
        setCroppedAreaPixels(croppedAreaPixels);
    };
    // 🚀 執行裁切並上傳
    const handleCropConfirm = async () => {
        try {
            // 1. 利用 Canvas 產出裁切後的圖片 Blob
            const croppedImageBlob = await getCroppedImg(imageToCrop, croppedAreaPixels);

            // 2. 將 Blob 轉為 File 物件 (為了配合後端接收的 MultipartFile)
            const croppedFile = new File([croppedImageBlob], 'avatar.jpg', { type: 'image/jpeg' });
            // 3. 呼叫上傳 API
            const response = await userApi.uploadAvatar(croppedFile);

            // 4. 更新 UI
            setProfileData(prev => ({ ...prev, avatarUrl: response.avatarUrl }));
            setImageToCrop(null); // 關閉裁切視窗
            alert("裁切並上傳成功！");
        } catch (error) {
            console.error("裁切處理失敗", error);
            alert("圖片處理失敗，請重試。");
        }
    };




    return (
        <div className="profile-page">
            <main className="profile-page__main">
                {/* 右側內容區 - Main Content */}
                <section className="profile-content">
                    <div className="profile-content__card">
                        <h2 className="profile-content__title">
                            {isEditing ? "會員資料修改" : "會員資料"}
                        </h2>


                        {/* 🚀 圖片上傳區：優化後不再需要按鈕 */}
                        <div className="avatar-upload">
                            <div
                                className={`avatar-upload__preview ${isEditing ? 'avatar-upload__preview--active' : 'avatar-upload__preview--disabled'}`}
                                // 💡 關鍵點：只有在編輯模式時，點擊圖片區域才會觸發檔案選取
                                onClick={() => isEditing && document.getElementById('avatar-input').click()}
                                style={{ cursor: isEditing ? 'pointer' : 'default' }}
                            >
                                <img
                                    className="avatar-upload__img"
                                    src={profileData.avatarUrl || "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=200"} alt="Avatar" />

                                {isEditing && (
                                    <div className="avatar-upload__overlay">
                                        <Camera className="avatar-upload__icon" />
                                        <span className="avatar-upload__hint">更換大頭貼</span>
                                    </div>
                                )}
                            </div>

                            {/* 💡 隱藏的 Input，它在背後默默工作 */}
                            <input
                                type="file"
                                id="avatar-input"
                                hidden
                                onChange={handleAvatarChange}
                                accept="image/*"
                            />



                            <div className="form-field">
                                <label className="form-field__label">個人簡介</label>
                                <div className="form-field__input-group">
                                    <textarea
                                        name="bio"
                                        value={profileData.bio}
                                        onChange={handleChange}
                                        disabled={!isEditing}
                                        className="form-field__input"
                                        placeholder="寫點什麼介紹自己..."
                                    />
                                </div>
                            </div>

                        </div>

                        {/* 表單內容 */}
                        <div className="profile-form">
                            <div className="profile-form__grid">

                                <div className="form-field">
                                    <label className="form-field__label">姓名</label>
                                    <div className="form-field__input-group">
                                        <User className="form-field__icon" />
                                        <input type="text" name="displayName"
                                            onChange={handleChange}
                                            value={profileData.displayName}
                                            disabled={!isEditing} className="form-field__input" />
                                    </div>
                                </div>


                                <div className="form-field">
                                    <label className="form-field__label">電子郵件</label>
                                    <div className="form-field__input-group">
                                        <Mail className="form-field__icon" />
                                        {/* Email (唯讀) */}
                                        <input type="email" name="email"
                                            value={profileData.email || ''}
                                            readOnly disabled className="form-field__input" />
                                    </div>
                                </div>

                                <div className="form-field">
                                    <label className="form-field__label">手機號碼</label>
                                    <div className="form-field__input-group">
                                        <Phone className="form-field__icon" />
                                        {/* 手機 */}
                                        <input type="tel" name="phoneNumber" value={profileData.phoneNumber || ''} onChange={handleChange} disabled={!isEditing} className="form-field__input" />
                                    </div>
                                </div>

                                <div className="form-field">
                                    <label className="form-field__label">生日</label>
                                    <div className="form-field__input-group">
                                        <Calendar className="form-field__icon" />
                                        <input
                                            type="date" name="birthday"
                                            value={profileData.birthday || ''}
                                            onChange={handleChange}
                                            disabled={!isEditing}
                                            className="form-field__input"
                                        />
                                    </div>
                                </div>

                                {/* 性別  */}
                                <div className="form-field">
                                    <label className="form-field__label">性別</label>
                                    <div className="form-field__input-group">
                                        <User className="form-field__icon" /> {/* 或使用 Users */}
                                        <select
                                            name="gender"
                                            value={profileData.gender || 'SECRET'}
                                            onChange={handleChange}
                                            disabled={!isEditing}
                                            className="form-field__input"
                                        >
                                            <option value="MALE">男</option>
                                            <option value="FEMALE">女</option>
                                            <option value="OTHER">其他</option>
                                            <option value="SECRET">保密</option>
                                        </select>
                                    </div>
                                </div>

                            </div>
                            <div className="profile-form__actions">
                                {!isEditing ? (
                                    // 瀏覽模式：顯示「修改資料」
                                    <button className="btn-save" onClick={() => setIsEditing(true)}>修改資料</button>
                                ) : (
                                    // 編輯模式：顯示「取消」與「儲存」
                                    <>
                                        <button className="btn-cancel" onClick={() => setIsEditing(false)}>取消</button>
                                        <button className="btn-save" onClick={handleSave}>儲存修改</button>
                                    </>
                                )}
                            </div>

                        </div>
                    </div>
                </section>
            </main>
            {/* 🚀 彈出的裁切視窗 (Crop Modal) */}
            {imageToCrop && (
                <div className="crop-modal">
                    <div className="crop-modal__content">
                        <div className="crop-modal__container">
                            <Cropper
                                image={imageToCrop}
                                crop={crop}
                                zoom={zoom}
                                aspect={1 / 1} // 強制 1:1 圓形/方形
                                cropShape="round" // 顯示為圓形裁切框
                                showGrid={false}
                                onCropChange={setCrop}
                                onCropComplete={onCropComplete}
                                onZoomChange={setZoom}
                            />
                        </div>
                        <div className="crop-modal__controls">
                            <input
                                type="range"
                                value={zoom}
                                min={1}
                                max={3}
                                step={0.1}
                                onChange={(e) => setZoom(e.target.value)}
                                className="crop-modal__slider"
                            />
                            <div className="crop-modal__actions">
                                <button className="btn-cancel" onClick={() => setImageToCrop(null)}>取消</button>
                                <button className="btn-save" onClick={handleCropConfirm}>確認裁切並上傳</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

