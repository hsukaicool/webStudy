// src/page/ProductDetail.jsx
import { ChevronRight, ShieldCheck, MessageSquare, Handshake, MapPin, ExternalLink } from 'lucide-react';
import { Link } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';


export default function ProductDetail() {
    return (
        <div className="product-detail">
            <Navbar variant="marketplace" />

            <main className="product-detail__main">
                {/* Breadcrumbs */}
                <nav className="product-detail__breadcrumbs">
                    <Link to="/">首頁</Link>
                    <ChevronRight className="w-4 h-4" />
                    <a href="#">電子產品</a>
                    <ChevronRight className="w-4 h-4" />
                    <span className="product-detail__breadcrumbs--current">MacBook Pro 14" M2</span>
                </nav>

                <div className="product-detail__layout">
                    {/* Left Column: Gallery */}
                    <section className="product-detail__gallery">
                        <div className="product-detail__gallery-main">
                            <div
                                className="product-detail__gallery-main-img"
                                style={{ backgroundImage: 'url("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?q=80&w=1200&auto=format&fit=crop")' }}
                            />
                            {/* Pagination Dots */}
                            <div className="dots-container">...</div>
                        </div>

                        <div className="product-detail__gallery-thumbs">
                            <div
                                className="product-detail__gallery-thumbs-item product-detail__gallery-thumbs-item--active"
                                style={{ backgroundImage: 'url("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?q=80&w=400&auto=format&fit=crop")' }}
                            />
                            {/* Other Thumbs */}
                            <div className="product-detail__gallery-thumbs-item product-detail__gallery-thumbs-item--more">
                                <span>+5</span>
                            </div>
                        </div>

                        {/* Safe Meeting Tips */}
                        <div className="product-detail__tips">
                            <div className="product-detail__tips-icon">
                                <ShieldCheck className="w-6 h-6" />
                            </div>
                            <div className="product-detail__tips-content">
                                <h4>安全面交提示</h4>
                                <p>您的安全是我們的首要考量。請遵循以下指南以獲得最佳體驗：</p>
                                <ul className="product-detail__tips-list">
                                    <li><ShieldCheck className="w-4 h-4" /> 在光線充足的公共場所見面</li>
                                    <li><ShieldCheck className="w-4 h-4" /> 付款前請完整測試設備</li>
                                    {/* ... */}
                                </ul>
                            </div>
                        </div>
                    </section>

                    {/* Right Column: Details */}
                    <section className="product-detail__info">
                        <div>
                            <div className="product-detail__info-header">
                                <span className="product-detail__info-header-badge">極佳狀態</span>
                                <span className="product-detail__info-header-meta">• 2 天前</span>
                            </div>
                            <h1 className="product-detail__info-title">
                                MacBook Pro 14" (M2 Pro, 2023)
                            </h1>
                            <p className="product-detail__info-desc">
                                16GB RAM, 512GB SSD 太空灰...
                            </p>
                        </div>

                        <div className="product-detail__info-price">
                            <span className="product-detail__info-price-current">$1,850</span>
                            <span className="product-detail__info-price-original">$2,199</span>
                        </div>

                        <div className="product-detail__info-actions">
                            <button className="btn btn--primary">直接購買</button>
                            <div className="grid grid-cols-2 gap-3">
                                <button className="btn btn--dark">加入購物車</button>
                                <button className="btn btn--outline">
                                    <MessageSquare className="w-5 h-5" />
                                    聯絡賣家
                                </button>
                            </div>
                        </div>

                        {/* ... 其他部分如賣家資訊與交易區塊 ... */}
                    </section>
                </div>
            </main>

            <Footer variant="marketplace" />
        </div>
    );
}
