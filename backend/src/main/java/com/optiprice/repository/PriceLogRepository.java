package com.optiprice.repository;

import com.optiprice.model.PriceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceLogRepository extends JpaRepository<PriceLog, Long> {
    List<PriceLog> findByStoreItemIdOrderByTimestampAsc(Long storeItemId);
}