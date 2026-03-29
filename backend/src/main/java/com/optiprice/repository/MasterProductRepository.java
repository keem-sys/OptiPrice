package com.optiprice.repository;

import com.optiprice.model.MasterProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterProductRepository extends JpaRepository<MasterProduct, Long> {
    List<MasterProduct> findByGenericNameContainingIgnoreCase(String name);
    List<MasterProduct> findByCategoryIgnoreCase(String category);

    @Query("SELECT DISTINCT m.genericName FROM MasterProduct m")
    List<String> findAllTrackedProductNames();

    @Query("SELECT m FROM MasterProduct m LEFT JOIN FETCH m.storeItems si LEFT JOIN FETCH si.store WHERE m.id = :id")
    Optional<MasterProduct> findByIdWithStores(@Param("id") Long id);

    /* Finds products sold in at least 2 stores where the price gap is larger than a minimum amount.
     Sorts them so the biggest savings appear at the top of the page.
     */
    @Query("""
        SELECT m FROM MasterProduct m
        JOIN m.storeItems si
        GROUP BY m
        HAVING COUNT(si) > 1
        AND (MAX(si.currentPrice) - MIN(si.currentPrice)) >= :minGap
        ORDER BY (MAX(si.currentPrice) - MIN(si.currentPrice)) DESC
    """)
    Page<MasterProduct> findProductsWithPriceGap(@Param("minGap") java.math.BigDecimal minGap, Pageable pageable);

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