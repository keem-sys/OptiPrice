package com.optiprice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
public class StoreItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2)
    private BigDecimal currentPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal oldPrice;

    private String storeSpecificName;
    private String brand;
    private String externalId;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String productUrl;

    @Column(name ="barcode", length = 50)
    private String barcode;

    private Boolean isOnPromotion;
    private String promotionText;

    private OffsetDateTime lastUpdated;

    @Column(name = "article_sku", length = 50)
    private String articleSku;


    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "master_product_id")
    @JsonBackReference
    private MasterProduct masterProduct;
}