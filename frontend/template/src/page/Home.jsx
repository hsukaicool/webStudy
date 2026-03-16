import { useState, useEffect } from 'react';
import { ArrowUpDown, Filter, Heart, Armchair, Shirt, Laptop, Book, Palette, MoreHorizontal } from 'lucide-react';
import { Link } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import { homePageApi } from '../api/services/homePage';

// 確保有引入你的 scss
// import '../scss/pagesScss/_home.scss'; 

export default function Home() {
    // 1. 輪播圖當前索引狀態
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    // 2. [重點新增]：宣告 Banner 的專屬狀態 (資料、載入中、錯誤)
    const [bannerImages, setBannerImages] = useState([]); // 預設空陣列
    const [isBannerLoading, setIsBannerLoading] = useState(true);
    const [bannerError, setBannerError] = useState(null);


    // [重點 2]：使用 useEffect 控制計時器自動輪播
    useEffect(() => {
        const timer = setInterval(() => {
            // 使用 callback 格式確保拿到最新的 prev 狀態，並且用 % 取餘數讓陣列可以循環
            setCurrentImageIndex((prev) => (prev + 1) % HERO_IMAGES.length);
        }, 5000); // 5 秒切換一次

        // [關鍵安全機制]：元件卸載時清除計時器，避免發生 Memory Leak (記憶體洩漏)
        return () => clearInterval(timer);
    }, []);

    // ... 原本的 fetchHomeData 等邏輯保留 ...


    // 原本的 API 串接邏輯

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
                    <div className="hero-content-wrapper">
                        <div className="hero-image-wrapper">
                            {/* 輪播圖片群 */}
                            {HERO_IMAGES.map((img, index) => (
                                <div
                                    key={img}
                                    // 利用 Template Literals (樣板字面值) 動態判斷是否要加上 'active'
                                    className={`hero-image ${index === currentImageIndex ? 'active' : ''}`}
                                    // 背景圖片還是得留在 inline style，因為它是動態資料
                                    style={{ backgroundImage: `url('${img}')` }}
                                />
                            ))}

                            {/* 輪播指示器（小圓點按鈕） */}
                            <div className="carousel-indicators">
                                {HERO_IMAGES.map((_, index) => (
                                    <button
                                        key={index}
                                        onClick={() => setCurrentImageIndex(index)}
                                        // 同樣動態判斷是否要加上 'active'
                                        className={`indicator-btn ${index === currentImageIndex ? 'active' : ''}`}
                                        aria-label={`切換至第 ${index + 1} 張圖片`}
                                    />
                                ))}
                            </div>
                        </div>
                    </div>
                </section>

                {/* 精選分類區塊 */}
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
                </section>

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
                        {/* 商品卡片 1 */}
                        <Link to="/marketplace" className="listing-card group">
                            <div className="image-wrapper">
                                <div
                                    className="listing-image"
                                    style={{ backgroundImage: "url('https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?q=80&w=600&auto=format&fit=crop')" }}
                                />
                                <button className="like-btn"><Heart className="icon" /></button>
                                <span className="badge">已驗證賣家</span>
                            </div>
                            <div className="listing-info">
                                <h3>iPad Pro 12.9"</h3>
                                <span className="price">$850</span>
                            </div>
                            <p className="location">紐約威廉斯堡 • 距離 2 英里</p>
                        </Link>

                        {/* 商品卡片 2-4 省略重複，你可以把剩餘的寫法依樣畫葫蘆 */}
                    </div>
                </section>

            </main>

        </div>
    );
}

