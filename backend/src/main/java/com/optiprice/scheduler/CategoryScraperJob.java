package com.optiprice.scheduler;

import com.optiprice.model.StoreCategory;
import com.optiprice.repository.StoreCategoryRepository;
import com.optiprice.scraper.ScraperOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryScraperJob {

    private final StoreCategoryRepository storeCategoryRepository;
    private final ScraperOrchestrator orchestrator;

    /**
     * Runs every Wednesday at 2:00 AM to refresh "Dairy" category.
     * Cron: 0 0 2 * * WED
     */
    @Scheduled(cron = "0 0 2 * * WED")
    public void crawlDairyCategory() {
        crawlCategory("Dairy");
    }

    /**
     * Runs every Sunday at 2:00 AM to refresh "Pantry" category.
     * Cron: 0 0 2 * * SUN
     */
    @Scheduled(cron = "0 0 2 * * SUN")
    public void crawlPantryCategory() {
        crawlCategory("Pantry");
    }

    private void crawlCategory(String categoryName) {
        log.info("STARTING CATEGORY CRAWL for at {}", categoryName, LocalDateTime.now());

        List<StoreCategory> storeCategories = storeCategoryRepository.findByCategoryNameIgnoreCase(categoryName);

        if (storeCategories.isEmpty()) {
            log.warn("No URLs found in the database for category: {}", categoryName);
            return;
        }

        for (StoreCategory sc : storeCategories) {
            String storeName = sc.getStore().getName();
            String targetUrl = sc.getUrl();

            log.info("Crawling {} via {}", categoryName, storeName);

            try {
                orchestrator.scrapeCategoryFromDb(categoryName, storeName, targetUrl);

            } catch (Exception e) {
                log.error("❌ Failed to crawl {} on {}", categoryName, storeName, e);
            }
        }

        log.info("CATEGORY CRAWL COMPLETED for", categoryName);
    }
}