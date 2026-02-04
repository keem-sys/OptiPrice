package com.optiprice.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PriceLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private OffsetDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "store_item_id")
    private StoreItem storeItem;
}