import { useState, useEffect } from 'react';
import { ArrowUpDown, Filter, Heart, Armchair, Shirt, Laptop, Book, Palette, MoreHorizontal } from 'lucide-react';
import { Link } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import { homePageApi } from '../api/services/homePage';
import productApi from '../api/services/productApi';
import bannerApi from '../api/services/bannerApi';
import { useAuth } from '../api/context/AuthContext';
import Cropper from 'react-easy-crop';
import { getCroppedImg } from '../api/utils/canvasUtils';
import { BASE_URL } from '../api/axiosClient';


// 確保有引入你的 scss
// import '../scss/pagesScss/_home.scss'; 

export default function Home() {
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [imageToCrop, setImageToCrop] = useState(null);
    const [crop, setCrop] = useState({ x: 0, y: 0 });
    const [zoom, setZoom] = useState(1);
    const [croppedAreaPixels, setCroppedAreaPixels] = useState(null);
    const [isUploading, setIsUploading] = useState(false);


    const { userProfile } = useAuth(); // 🚀 從 AuthContext 拿取身分資料
    const isAdmin = userProfile?.role === 'ROLE_ADMIN'; // 🚀 正確的角色判定方式
    // 假設後端 username 對應到此處，或直接檢查 profile
    // 狀態管理
    const [banners, setBanners] = useState([]);
    const [isHeroLoading, setIsHeroLoading] = useState(true);
    useEffect(() => {
        const fetchBanners = async () => {
            try {
                const data = await bannerApi.getPublicBanners();
                setBanners(data);
            } catch (error) {
                console.error("載入 Banner 失敗:", error);
            } finally {
                setIsHeroLoading(false);
            }
        };
        fetchBanners();
    }, []);
    // 處理上傳 (觸發隱藏的 input)
    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.addEventListener('load', () => setImageToCrop(reader.result));
        reader.readAsDataURL(file);
    };

    const onCropComplete = (croppedArea, croppedAreaPixels) => {
        setCroppedAreaPixels(croppedAreaPixels);
    };

    const handleCropConfirm = async () => {
        try {
            setIsUploading(true);
            const croppedImageBlob = await getCroppedImg(imageToCrop, croppedAreaPixels);
            const croppedFile = new File([croppedImageBlob], 'banner.jpg', { type: 'image/jpeg' });

            await bannerApi.uploadBanner(croppedFile, "首頁 Banner", "/");
            alert("🎉 Banner 上傳成功！");
            setImageToCrop(null);
            window.location.reload();
        } catch (error) {
            console.error("上傳失敗", error);
            alert("上傳失敗，請重試");
        } finally {
            setIsUploading(false);
        }
    };

    const [products, setProducts] = useState([]);
    const [isLoading, setIsLoading] = useState(true);




    // [重點 2]：使用 useEffect 控制計時器自動輪播
    useEffect(() => {
        const timer = setInterval(() => {
            const length = banners.length > 0 ? banners.length : HERO_IMAGES.length;
            setCurrentImageIndex((prev) => (prev + 1) % length);
        }, 5000);

        return () => clearInterval(timer);
    }, [banners]);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                setIsLoading(true);
                // 呼叫 API，後端回傳的是 Page 物件
                const response = await productApi.getPublicProducts(0, 8); // 首頁顯示前 8 筆
                setProducts(response.content); // Page 物件的數據在 content 屬性中
            } catch (error) {
                console.error("載入商品失敗:", error);
            } finally {
                setIsLoading(false);
            }
        };
        fetchProducts();
    }, []);


    const [data, setData] = useState(null);

    useEffect(() => {
        const fetchHomeData = async () => {
            try {
                const result = await homePageApi.getHelloMessage();
                setData(result);
                console.log(result);
            } catch (error) {
                console.error('Home 畫面載入錯誤:', error);
            }
        };
        fetchHomeData();
    }, []);

    // 確保已引入

    const DEFAULT_BANNERS = [
        "https://images.unsplash.com/photo-1595515106969-1ce29566ff1c?q=80&w=1200&auto=format&fit=crop",
        // ... 其他預設圖
    ];

    // 在 Home 元件外部定義圖片來源
    const HERO_IMAGES = [
        "https://images.unsplash.com/photo-1595515106969-1ce29566ff1c?q=80&w=1200&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1616464916356-3a4002138120?q=80&w=1200&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1492707892479-7bc8d5a4ee93?q=80&w=1200&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=1200&auto=format&fit=crop"
    ];


    const categories = [
        { icon: Armchair, label: "家具" },
        { icon: Shirt, label: "復古服飾" },
        { icon: Laptop, label: "電子產品" },
        { icon: Book, label: "收藏品" },
        { icon: Palette, label: "藝術與裝飾" },
        { icon: MoreHorizontal, label: "更多" },
    ];


    return (
        <div className="page-home overflow-x-hidden">
            <main className="home-main">
                {/* Hero 區塊 */}
                <section className="hero-section">
                    {isAdmin && (
                        <div className="admin-controls">
                            <input
                                type="file"
                                id="banner-upload"
                                hidden
                                onChange={handleFileChange}
                                accept="image/*"
                            />
                            <button
                                className="edit-banner-btn"
                                onClick={() => document.getElementById('banner-upload').click()}
                            >
                                ✏️ 修改 Banner (Admin Only)
                            </button>
                        </div>
                    )}
                    <div className="hero-content-wrapper">
                        <div className="hero-image-wrapper">
                            {/* 🚀 從 API 拿到的 banners 資料 */}
                            {banners.length > 0 ? (
                                banners.map((banner, index) => (
                                    <div
                                        key={banner.id}
                                        className={`hero-image ${index === currentImageIndex ? 'active' : ''}`}
                                        style={{
                                            backgroundImage: `url('${banner.imageUrl.startsWith('http') ? banner.imageUrl : `${BASE_URL}${banner.imageUrl}`}')`
                                        }}
                                    />
                                ))
                            ) : (
                                // 🚀 如果 API 還沒回傳，先顯示預設圖
                                DEFAULT_BANNERS.map((img, index) => (
                                    <div
                                        key={index}
                                        className={`hero-image ${index === currentImageIndex ? 'active' : ''}`}
                                        style={{ backgroundImage: `url('${img}')` }}
                                    />
                                ))
                            )}

                            <div className="carousel-indicators">
                                {(banners.length > 0 ? banners : HERO_IMAGES).map((_, index) => (
                                    <button
                                        key={index}
                                        onClick={() => setCurrentImageIndex(index)}
                                        className={`indicator-btn ${index === currentImageIndex ? 'active' : ''}`}
                                        aria-label={`切換至第 ${index + 1} 張圖片`}
                                    />
                                ))}
                            </div>
                        </div>
                    </div>

                    {/* 🚀 彈出的裁切視窗 (Crop Modal) */}
                    {imageToCrop && (
                        <div className="crop-modal">
                            <div className="crop-modal__content">
                                <div className="crop-modal__container">
                                    <Cropper
                                        image={imageToCrop}
                                        crop={crop}
                                        zoom={zoom}
                                        aspect={21 / 9} // Banner 通常用寬螢幕比例
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
                                        <button
                                            className="btn-save"
                                            onClick={handleCropConfirm}
                                            disabled={isUploading}
                                        >
                                            {isUploading ? '處理中...' : '確認裁切並上傳'}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </section>

                {/* 精選分類區塊 
                <section className="categories-section">
                    <h2 className="section-title">精選分類</h2>
                    <div className="categories-grid">
                        {categories.map((cat, i) => (
                            <button key={i} className="category-btn group">
                                <div className="icon-circle">
                                    <cat.icon className="icon" />
                                </div>
                                <span className="category-label">{cat.label}</span>
                            </button>
                        ))}
                    </div>
                </section>*/}

                {/* 最新上架區塊 */}
                <section className="recent-listings-section">
                    <div className="listings-header">
                        <h2 className="section-title">最新上架</h2>
                        <div className="filter-actions">
                            <button className="icon-btn"><Filter className="icon" /></button>
                            <button className="icon-btn"><ArrowUpDown className="icon" /></button>
                        </div>
                    </div>

                    <div className="listings-grid">
                        {isLoading ? (
                            <p>商品載入中...</p>
                        ) : products.length > 0 ? (
                            products.map((product) => (
                                <Link
                                    to={`/product/${product.externalId}`}
                                    key={product.externalId}
                                    className="listing-card group"
                                >
                                    <div className="image-wrapper">
                                        <div
                                            className="listing-image"
                                            style={{ backgroundImage: `url('${product.imageUrl || '預設圖URL'}')` }}
                                        />
                                        <button className="like-btn"><Heart className="icon" /></button>
                                        <span className="badge">{product.condition}</span>
                                    </div>
                                    <div className="listing-info">
                                        <h3>{product.name}</h3>
                                        <span className="price">${product.price}</span>
                                    </div>
                                </Link>
                            ))
                        ) : (
                            <p>目前沒有商品</p>
                        )}
                    </div>
                </section>

            </main>

        </div>
    );
}

