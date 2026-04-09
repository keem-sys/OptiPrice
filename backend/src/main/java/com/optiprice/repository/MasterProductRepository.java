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
    List<MasterProduct> findByGenericNameIgnoreCase(String name);
    List<MasterProduct> findByCategoryIgnoreCase(String category);

    @Query("SELECT DISTINCT m.genericName FROM MasterProduct m")
    List<String> findAllTrackedProductNames();

    @Query("SELECT m FROM MasterProduct m LEFT JOIN FETCH m.storeItems si LEFT JOIN FETCH si.store WHERE m.id = :id")
    Optional<MasterProduct> findByIdWithStores(@Param("id") Long id);

    // Find a master product using the sorted word fingerprint
    Optional<MasterProduct> findFirstByFingerprint(String fingerprint);

    /**
     * Official Promotion (DealType) Query
     * Finds products where at least one store has flagged it as an official promotion/sale.
     */
    @Query("SELECT DISTINCT m FROM MasterProduct m JOIN m.storeItems si WHERE si.isOnPromotion = true")
    Page<MasterProduct> findOfficialPromotions(Pageable pageable);

    /**
     * Price Drop (DealType) Query
     * Finds products where the current price is at least X% lower than its all-time high price.
     */
    @Query(value = """
            SELECT DISTINCT m.*
            FROM master_product m
            JOIN store_item si ON m.id = si.master_product_id
            WHERE si.current_price <= (
                SELECT MAX(pl.price) * :multiplier
                FROM price_log pl
                WHERE pl.store_item_id = si.id
            )
            """,
            countQuery = """
            SELECT COUNT(DISTINCT m.id)
            FROM master_product m
            JOIN store_item si ON m.id = si.master_product_id
            WHERE si.current_price <= (
                SELECT MAX(pl.price) * :multiplier
                FROM price_log pl
                WHERE pl.store_item_id = si.id
            )
            """,
            nativeQuery = true)
    Page<MasterProduct> findProductsWithPriceDrop(@Param("multiplier") double multiplier, Pageable pageable);


    /**
     * Huge Gap (DealType) Query
     * Finds products sold in at least 2 stores where the price gap is larger than a minimum amount.
     * Sorts them so the biggest savings appear at the top of the page.
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
        SELECT DISTINCT generic_name
        FROM master_product
        WHERE generic_name IN :basketItems
        AND id IN (
            SELECT master_product_id
            FROM store_item
            GROUP BY master_product_id
            HAVING COUNT(DISTINCT store_id) >= 2
        )
        """, nativeQuery = true)
    List<String> findProductsInTrendBasket(@Param("basketItems") List<String> basketItems);

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