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
    private String websiteUrl;

    public Store(String name, String logoUrl,  String websiteUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.websiteUrl = websiteUrl;
    }
}