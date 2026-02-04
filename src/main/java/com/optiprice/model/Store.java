package com.optiprice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;

    private String logoUrl;

    public Store(String name, String logoUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
    }
}