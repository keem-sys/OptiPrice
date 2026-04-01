package com.optiprice.repository;

import com.optiprice.dto.response.BasketTrendProjection;
import com.optiprice.dto.response.PriceHistoryPoint;
import com.optiprice.model.PriceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceLogRepository extends JpaRepository<PriceLog, Long> {
    @Query("""
        SELECT new com.optiprice.dto.response.PriceHistoryPoint(pl.timestamp, s.name, pl.price)
        FROM PriceLog pl
        JOIN pl.storeItem si
        JOIN si.store s
        WHERE si.masterProduct.id = :masterId
        ORDER BY pl.timestamp ASC
    """)
    List<PriceHistoryPoint> findHistoryByMasterId(@Param("masterId") Long masterId);

    /**
     * BASKET INDEX
     * Subquery: Finds up to 10 Master Products that are sold at ALL 3 stores (Fair comparison).
     * Main Query: Sums the daily price of those exact 10 items for each store.
     */
    @Query(value = """
        SELECT 
            CAST(pl.timestamp AS DATE) as logDate, 
            s.name as storeName, 
            SUM(pl.price) as basketPrice
        FROM price_log pl
        JOIN store_item si ON pl.store_item_id = si.id
        JOIN store s ON si.store_id = s.id
        WHERE si.master_product_id IN (
            SELECT master_product_id 
            FROM store_item 
            GROUP BY master_product_id 
            HAVING COUNT(DISTINCT store_id) >= 2
            LIMIT 10
        )
        GROUP BY CAST(pl.timestamp AS DATE), s.name
        ORDER BY logDate ASC
        """, nativeQuery = true)
    List<BasketTrendProjection> getDailyBasketTrend();
}