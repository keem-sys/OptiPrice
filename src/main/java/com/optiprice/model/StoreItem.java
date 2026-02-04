package com.optiprice.model;

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

    private String storeSpecificName;
    private String brand;
    private String externalId;
    private String imageUrl;

    @Column(length = 1000)
    private String productUrl;

    private OffsetDateTime lastUpdated;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "master_product_id")
    private MasterProduct masterProduct;
}