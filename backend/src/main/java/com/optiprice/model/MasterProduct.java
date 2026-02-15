package com.optiprice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class MasterProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String genericName;
    private String category;

    @BatchSize(size = 20)
    @OneToMany(mappedBy = "masterProduct", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<StoreItem> storeItems;
}