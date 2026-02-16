package com.optiprice.repository;

import com.optiprice.model.Store;
import com.optiprice.model.StoreItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreItemRepository extends JpaRepository<StoreItem, Long> {
    Optional<StoreItem> findByExternalIdAndStore(String externalId, Store store);
    List<StoreItem> findByStoreSpecificNameContainingIgnoreCase(String storeSpecificName);
    @Query("SELECT DISTINCT si.masterProduct.genericName " +
            "FROM StoreItem si " +
            "WHERE si.lastUpdated < :threshold")
    List<String> findStaleProductNames(@Param("threshold") OffsetDateTime threshold);
}
