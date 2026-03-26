package com.app.mysecureapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

// 賣家看是誰買的
@Entity
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // 關聯回主訂單

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // 買了什麼

    // 🚀 核心設計：冗餘存儲賣家資訊，方便賣家獨立查詢自己的銷售額
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private SellerProfile seller;

    private Integer quantity; // 購買數量

    // 🚀 關鍵技術：價格快照 (Price Snapshot)
    // 即使以後商品漲價，這張訂單紀錄的金額也不會變動，確保帳務準確
    private BigDecimal priceAtPurchase;
}
