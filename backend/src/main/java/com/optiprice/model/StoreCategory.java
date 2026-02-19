package com.optiprice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class StoreCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Store store;

    @Column(columnDefinition = "TEXT")
    private String url;
}
