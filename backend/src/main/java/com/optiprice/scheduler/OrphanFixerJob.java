package com.optiprice.scheduler;

import com.optiprice.model.StoreItem;
import com.optiprice.repository.StoreItemRepository;
import com.optiprice.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrphanFixerJob {

    private final StoreItemRepository storeItemRepository;
    private final MatchingService matchingService;

    @Scheduled(fixedDelay = 300000)
    public void retryOrphanedItems() {
        List<StoreItem> orphans = storeItemRepository.findByMasterProductIsNull();

        if (orphans.isEmpty()) return;

        log.info("ORPHAN SWEEPER: Found {} items with no Master Product. Retrying...", orphans.size());

        for (StoreItem item : orphans) {
            try {
                matchingService.findOrCreateMasterProduct(item, null);

                Thread.sleep(100);
            } catch (Exception e) {
                log.error("Failed to rescue item {}: {}", item.getId(), e.getMessage());
            }
        }

        log.info("ORPHAN SWEEP COMPLETE.");
    }
}