// src/components/Footer.jsx
import { Link } from 'react-router-dom';
import { Facebook, Instagram, Twitter, Mail, ArrowRight } from 'lucide-react';

export default function Footer({ variant }) {
    return (
        <footer className={`site-footer ${variant === 'home' ? 'footer-home' : ''}`}>
            <div className="footer-container">
                <div className="footer-main">
                    {/* 品牌與理念 */}
                    <div className="footer-brand">
                        <h2 className="footer-logo">二手好物交易網</h2>
                        <p className="footer-tagline">
                            信任的新標準。體驗如雜誌般精美的市集，每一次經過驗證的交流都能帶來安心與獨特的發現。
                        </p>
                        <div className="footer-socials">
                            <a href="#" aria-label="Facebook"><Facebook size={20} /></a>
                            <a href="#" aria-label="Instagram"><Instagram size={20} /></a>
                            <a href="#" aria-label="Twitter"><Twitter size={20} /></a>
                        </div>
                    </div>

                    {/* 連結區塊 */}
                    <div className="footer-nav-groups">
                        <div className="nav-group">
                            <h3>關於我們</h3>
                            <Link to="/story">品牌故事</Link>
                            <Link to="/team">團隊成員</Link>
                            <Link to="/careers">人才招募</Link>
                        </div>

                        <div className="nav-group">
                            <h3>客服中心</h3>
                            <Link to="/faq">常見問題</Link>
                            <Link to="/shipping">出貨與退換貨</Link>
                            <Link to="/contact">聯絡我們</Link>
                        </div>
                    </div>
                </div>

                {/* 底部資訊 */}
                <div className="footer-bottom">
                    <p className="copyright">
                        &copy; {new Date().getFullYear()} ROUST. All rights reserved.
                    </p>
                    <nav className="footer-legal">
                        <Link to="/privacy">隱私權政策</Link>
                        <Link to="/terms">服務條款</Link>
                    </nav>
                </div>
            </div>
        </footer>
    );
}
