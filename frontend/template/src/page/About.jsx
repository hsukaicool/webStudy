import React from 'react';
import { ShieldCheck, Leaf, Gem, Users } from 'lucide-react';


export default function About() {
    return (
        <div className="about-page">

            <main className="about-main">
                {/* Hero Section */}
                <section className="about-hero">
                    <div className="about-hero__container">
                        <h1 className="about-hero__title">
                            重新定義<br /><span className="text-highlight">二手交易</span>的價值
                        </h1>
                        <p className="about-hero__description">
                            我們相信每一件物品都值得被珍視。透過建立一個安全、透明且充滿信任的社群，我們讓二手交易不再只是買賣，而是品味與故事的傳承。
                        </p>
                    </div>
                </section>

                {/* Mission Section */}
                <section className="about-mission">
                    <div className="about-mission__grid">
                        <div className="about-mission__image-wrapper">
                            <img
                                src="https://images.unsplash.com/photo-1528698827591-e19ccd7bc23d?q=80&w=1200&auto=format&fit=crop"
                                alt="我們的使命"
                            />
                        </div>
                        <div className="about-mission__content">
                            <span className="about-mission__tag">我們的使命</span>
                            <h2 className="about-mission__title">
                                打造最值得信賴的<br />品味交流平台
                            </h2>
                            <p className="about-mission__text">
                                在快時尚與過度消費的時代，我們致力於推廣「有意識的消費」。我們嚴格把關每一件上架的商品，確保買賣雙方都能在一個安全、有保障的環境中，找到屬於自己的心頭好。
                            </p>
                            <p className="about-mission__text">
                                從精緻的復古家具到經典的底片相機，我們讓這些承載著時間記憶的物品，能夠在下一個主人手中繼續閃耀。
                            </p>
                        </div>
                    </div>
                </section>

                {/* Values Section */}
                <section className="about-values">
                    <div className="about-values__container">
                        <header className="about-values__header">
                            <h2 className="about-values__title">核心價值</h2>
                            <p className="about-values__subtitle">這是我們對每一位使用者的承諾，也是我們不斷前進的動力。</p>
                        </header>

                        <div className="about-values__grid">
                            <div className="value-card">
                                <div className="value-card__icon-box">
                                    <ShieldCheck className="icon" />
                                </div>
                                <h3 className="value-card__title">極致安全</h3>
                                <p className="value-card__text">
                                    嚴格的實名驗證機制與安全的交易流程，讓您在每一次的買賣中都能感到絕對的安心。
                                </p>
                            </div>

                            <div className="value-card">
                                <div className="value-card__icon-box">
                                    <Gem className="icon" />
                                </div>
                                <h3 className="value-card__title">嚴選品質</h3>
                                <p className="value-card__text">
                                    我們堅持高標準的商品審核，確保平台上的每一件物品都具備優良的狀態與獨特的價值。
                                </p>
                            </div>

                            <div className="value-card">
                                <div className="value-card__icon-box">
                                    <Leaf className="icon" />
                                </div>
                                <h3 className="value-card__title">永續循環</h3>
                                <p className="value-card__text">
                                    透過延長物品的使用壽命，我們與您一起為地球盡一份心力，實踐綠色環保的生活方式。
                                </p>
                            </div>
                        </div>
                    </div>
                </section>

                {/* Stats Section */}
                <section className="about-stats">
                    <div className="about-stats__grid">
                        <div className="stat-item">
                            <div className="stat-item__number">50K+</div>
                            <div className="stat-item__label">活躍會員</div>
                        </div>
                        <div className="stat-item">
                            <div className="stat-item__number">120K+</div>
                            <div className="stat-item__label">成功交易</div>
                        </div>
                        <div className="stat-item">
                            <div className="stat-item__number">99%</div>
                            <div className="stat-item__label">五星好評</div>
                        </div>
                        <div className="stat-item">
                            <div className="stat-item__number">24/7</div>
                            <div className="stat-item__label">全天候支援</div>
                        </div>
                    </div>
                </section>
            </main>
        </div>
    );
}
