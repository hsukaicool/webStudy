import { MapPin, ShieldCheck, Share2 } from 'lucide-react';
import { Link } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

export default function Store() {
    return (
        <div className="store-page">
            <Navbar />

            <main className="store-page__main">
                {/* 橫幅區域 - Banner Section */}
                <section className="store-banner">
                    <img
                        src="https://images.unsplash.com/photo-1441986300917-64674bd600d8?q=80&w=1920&auto=format&fit=crop"
                        alt="Store Banner"
                        className="store-banner__img"
                    />
                    <div className="store-banner__overlay"></div>
                </section>

                <div className="store-content-container">
                    {/* 商店資訊卡片 - Store Info Card */}
                    <section className="store-info">
                        <div className="store-info__avatar-wrapper">
                            <img
                                src="https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=200&auto=format&fit=crop"
                                alt="Store Avatar"
                                className="store-info__avatar-img"
                            />
                        </div>

                        <div className="store-info__body">
                            <div className="store-info__header">
                                <div className="store-info__title-group">
                                    <h1 className="store-info__name">
                                        小明的精選好物
                                        <ShieldCheck className="store-info__verified-icon" />
                                    </h1>
                                    <div className="store-info__meta">
                                        <span className="store-info__meta-item">
                                            <MapPin className="store-info__meta-icon" />
                                            台北市, 台灣
                                        </span>
                                        <span className="store-info__meta-item">加入時間：2023年5月</span>
                                    </div>
                                </div>

                                <div className="store-info__actions">
                                    <button className="store-info__action-btn" aria-label="Share">
                                        <Share2 className="store-info__action-icon" />
                                    </button>
                                </div>
                            </div>

                            <p className="store-info__description">
                                歡迎來到小明的精選好物！這裡專門販售保存良好的二手電子產品、攝影器材與質感生活選物。
                                所有商品出貨前皆會經過嚴格測試與清潔，提供 7 天個人保固，讓您買得安心、用得開心。
                                有任何問題歡迎隨時聊聊詢問！
                            </p>
                        </div>
                    </section>

                    {/* 商品網格區域 - Products Section */}
                    <section className="store-products">
                        <header className="store-products__header">
                            <h2 className="store-products__title">所有商品 (24)</h2>
                            <div className="store-products__filters">
                                <select className="store-products__select">
                                    <option>最新上架</option>
                                    <option>價格由低到高</option>
                                    <option>價格由高到低</option>
                                    <option>最熱銷</option>
                                </select>
                            </div>
                        </header>

                        <div className="product-grid">
                            {/* 商品 1 - 使用重複結構或 Map */}
                            <ProductCard
                                to="/product"
                                img="https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?q=80&w=600&auto=format&fit=crop"
                                title="iPad Pro 12.9\"
                                price={850}
                                desc="9成新 • 附原廠配件"
                            />
                            <ProductCard
                                to="/product"
                                img="https://images.unsplash.com/photo-1516961642265-531546e84af2?q=80&w=600&auto=format&fit=crop"
                                title="Sony A7III 相機"
                                price={1200}
                                desc="快門數 5000 • 盒裝完整"
                            />
                            {/* ...其餘商品以此類推 */}
                        </div>
                    </section>
                </div>
            </main>

            <Footer />
        </div>
    );
}

// 輔助組件：商品卡片 (可拆分至獨立檔案)
function ProductCard({ to, img, title, price, desc }) {
    return (
        <Link to={to} className="product-card">
            <div className="product-card__image-container">
                <div
                    className="product-card__img"
                    style={{ backgroundImage: `url('${img}')` }}
                />
            </div>
            <div className="product-card__content">
                <div className="product-card__header">
                    <h3 className="product-card__title">{title}</h3>
                    <span className="product-card__price">${price}</span>
                </div>
                <p className="product-card__desc">{desc}</p>
            </div>
        </Link>
    );
}
