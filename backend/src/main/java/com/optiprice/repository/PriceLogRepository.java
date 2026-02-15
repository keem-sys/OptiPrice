package com.optiprice.repository;

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
}