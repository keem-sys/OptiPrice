package com.optiprice.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class MasterProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String genericName;
    private String category;

    @OneToMany(mappedBy = "masterProduct")
    private List<StoreItem> storeItems;
}