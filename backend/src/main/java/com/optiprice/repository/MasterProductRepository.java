package com.optiprice.repository;

import com.optiprice.model.MasterProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterProductRepository extends JpaRepository<MasterProduct, Long> {
    List<MasterProduct> findByGenericNameContainingIgnoreCase(String name);
    List<MasterProduct> findByCategoryIgnoreCase(String category);
    @Query("SELECT DISTINCT m FROM MasterProduct m " +
            "LEFT JOIN FETCH m.storeItems si " +
            "LEFT JOIN FETCH si.store s " +
            "WHERE LOWER(m.genericName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(si.storeSpecificName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<MasterProduct> searchByKeyword(@Param("query") String query);
}
