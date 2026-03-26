// src/page/ProductDetail.jsx
import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { ChevronRight, ShieldCheck, ShoppingCart, ShoppingBag, Loader2 } from 'lucide-react';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import productApi from '../api/services/productApi';
import cartApi from '../api/services/cartApi';

// 分類映射表
const CATEGORY_MAP = {
    'ELECTRONICS': '電子產品',
    'PHOTOGRAPHY': '攝影器材',
    'FURNITURE': '家具',
    'CLOTHING': '服飾配件',
    'BOOKS': '書籍文具',
    'OTHER': '其他'
};

// 狀態映射表
const CONDITION_MAP = {
    'NEW': '全新',
    'LIKE_NEW': '近全新',
    'GOOD': '良好',
    'FAIR': '可接受'
};

export default function ProductDetail() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isAdding, setIsAdding] = useState(false); // 🚀 新增：控制按鈕 Loading 狀態


    useEffect(() => {
        const fetchProduct = async () => {
            try {
                // 🚀 使用從 URL 拿到的 id 去後端撈取詳細資料
                const data = await productApi.getPublicProductDetail(id);
                setProduct(data);
            } catch (error) {
                console.error("載入商品失敗:", error);
                alert("找不到該商品或已下架");
                navigate("/"); // 如果商品被下架或是網址錯誤，將用戶導回首頁
            } finally {
                setLoading(false);
            }
        };
        fetchProduct();
    }, [id, navigate]);

    const handleAddToCart = async () => {
        try {
            setIsAdding(true);

            // 1. 呼叫 API
            await cartApi.addToCart(product.externalId, 1);

            // 2. 成功回饋 (之後可以改用漂亮的 Toast 組件)
            alert("✨ 已成功加入購物車！");

        } catch (error) {
            console.error("購物車加入失敗:", error);

            // 🛡️ 安全攔截：如果是 401/403 代表沒登入
            if (error.response?.status === 401 || error.response?.status === 403) {
                alert("請先登入後再加入購物車喔！");
                navigate("/login");
            } else {
                alert("伺服器鬧脾氣了，請稍後再試。");
            }
        } finally {
            setIsAdding(false);
        }
    };

    // ProductDetail.jsx 點擊邏輯
    const handleBuyNow = () => {
        // 🚀 將資料打包成結帳頁需要的格式
        const checkoutItem = {
            productExternalId: product.externalId, // 後端認這個 ID
            name: product.name,
            price: product.price,
            quantity: 1,
            imageUrl: product.imageUrl
        };

        // 🚀 帶上行李跳轉
        navigate("/checkout", {
            state: { items: [checkoutItem] }
        });
    };

    if (loading) {
        return (
            <div className="product-detail">
                <Navbar variant="marketplace" />
                <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
                    <Loader2 className="spinner" size={40} />
                </div>
                <Footer variant="marketplace" />
            </div>
        );
    }

    if (!product) return null;

    return (
        <div className="product-detail">
            <main className="product-detail__main">
                {/* 🚀 Breadcrumbs 動態呈現 */}
                <nav className="product-detail__breadcrumbs">
                    <Link to="/">所有商品</Link>
                    <ChevronRight className="w-4 h-4" />
                    <span className="product-detail__breadcrumbs--muted">
                        {CATEGORY_MAP[product.category] || product.category}
                    </span>
                    <ChevronRight className="w-4 h-4" />
                    <span className="product-detail__breadcrumbs--current">{product.name}</span>
                </nav>

                <div className="product-detail__layout">
                    {/* Left Column: Gallery */}
                    <section className="product-detail__gallery">
                        <div className="product-detail__gallery-main">
                            <div
                                className="product-detail__gallery-main-img"
                                style={{
                                    backgroundImage: `url("${product.imageUrl || 'https://images.unsplash.com/photo-default'}")`
                                }}
                            />
                        </div>

                        {/* Safe Meeting Tips */}
                        <div className="product-detail__tips">
                            <div className="product-detail__tips-icon">
                                <ShieldCheck className="w-6 h-6" />
                            </div>
                            <div className="product-detail__tips-content">
                                <h4>安全交易保障</h4>
                                <p>本平台提供安全的交易環境，所有賣家均有身分認證。請善用站內私訊功能，面交時建議於公共場所，驗貨無誤後再行付款。</p>
                            </div>
                        </div>
                    </section>

                    {/* Right Column: Details */}
                    <section className="product-detail__info">
                        <div>
                            <div className="product-detail__info-header">
                                <span className="product-detail__info-badge">
                                    {CONDITION_MAP[product.condition] || product.condition}
                                </span>
                                {/* 即時庫存展示 */}
                                <span className="product-detail__info-stock">
                                    庫存剩餘 {product.stock} 件
                                </span>
                            </div>
                            <h1 className="product-detail__info-title">
                                {product.name}
                            </h1>
                        </div>

                        <div className="product-detail__info-price">
                            <span className="product-detail__info-price-label"></span>
                            {/* 🚀 加入 toLocaleString 使價格擁有千分位符號，提升閱讀體驗 */}
                            <span className="product-detail__info-price-current">
                                NT$ {product.price.toLocaleString()}
                            </span>
                        </div>

                        <div className="product-detail__info-desc">
                            {product.description || '這個賣家很懶，沒有留下說明。'}
                        </div>

                        <div className="product-detail__info-actions">
                            <button className="product-detail__btn product-detail__btn--buy" onClick={handleBuyNow}>
                                <ShoppingBag className="w-5 h-5" />
                                直接購買
                            </button>

                            <button
                                className={`product-detail__btn product-detail__btn--cart ${isAdding ? 'opacity-50 cursor-not-allowed' : ''}`}
                                onClick={handleAddToCart} // 🚀 綁定事件
                                disabled={isAdding || product.stock <= 0} // 🚀 正在加入或沒庫存時禁用
                            >
                                {/* 🚀 根據狀態切換圖示：正在加入時顯示轉圈圈 (Loader2)，平常顯示購物車 */}
                                {isAdding ? (
                                    <Loader2 className="w-5 h-5 animate-spin" />
                                ) : (
                                    <ShoppingCart className="w-5 h-5" />
                                )}

                                {/* 🚀 動態切換文字 */}
                                {isAdding ? '處理中...' : '加入購物車'}
                            </button>
                        </div>
                    </section>
                </div>
            </main>
        </div>
    );
}
