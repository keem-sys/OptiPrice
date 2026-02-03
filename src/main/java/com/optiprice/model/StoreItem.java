package com.optiprice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
public class StoreItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String storeSpecificName;
    private Double currentPrice;
    private String imageUrl;
    private String productUrl;
    private LocalDateTime lastUpdated;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "master_product_id")
    private MasterProduct masterProduct;
}
