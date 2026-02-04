package com.optiprice.repository;

import com.optiprice.model.MasterProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterProductRepository extends JpaRepository<MasterProduct, Long> {
    List<MasterProduct> findByGenericNameContainingIgnoreCase(String name);
}
