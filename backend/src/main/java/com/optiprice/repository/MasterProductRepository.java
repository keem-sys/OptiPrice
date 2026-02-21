package com.optiprice.repository;

import com.optiprice.model.MasterProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterProductRepository extends JpaRepository<MasterProduct, Long> {
    List<MasterProduct> findByGenericNameContainingIgnoreCase(String name);
    List<MasterProduct> findByCategoryIgnoreCase(String category);

    @Query("SELECT DISTINCT m.genericName FROM MasterProduct m")
    List<String> findAllTrackedProductNames();

    @Query(value = """
    SELECT m.*
    FROM master_product m
    JOIN store_item si ON m.id = si.master_product_id
    JOIN store s ON si.store_id = s.id
    WHERE
        to_tsvector('english',
            COALESCE(m.generic_name, '') || ' ' ||
            COALESCE(m.category, '') || ' ' ||
            COALESCE(si.store_specific_name, '') || ' ' ||
            COALESCE(si.brand, '') || ' ' ||
            COALESCE(s.name, '')
        )
        @@ websearch_to_tsquery('english', :query)
    GROUP BY m.id
    ORDER BY MAX(ts_rank(
        to_tsvector('english',
            COALESCE(m.generic_name, '') || ' ' ||
            COALESCE(si.brand, '') || ' ' ||
            COALESCE(s.name, '')
        ),
        websearch_to_tsquery('english', :query)
    )) DESC
    """,
            countQuery = """
    SELECT COUNT(DISTINCT m.id)
    FROM master_product m
    JOIN store_item si ON m.id = si.master_product_id -- CHANGED
    JOIN store s ON si.store_id = s.id                -- CHANGED
    WHERE
        to_tsvector('english',
            COALESCE(m.generic_name, '') || ' ' ||
            COALESCE(m.category, '') || ' ' ||
            COALESCE(si.store_specific_name, '') || ' ' ||
            COALESCE(si.brand, '') || ' ' ||
            COALESCE(s.name, '')
        )
        @@ websearch_to_tsquery('english', :query)
    """,
            nativeQuery = true)
    Page<MasterProduct> searchByKeyword(@Param("query") String query, Pageable pageable);
}