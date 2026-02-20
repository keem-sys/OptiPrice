package com.optiprice.scheduler;

import com.optiprice.repository.StoreItemRepository;
import com.optiprice.scraper.ScraperOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyScraperJob {

    private final ScraperOrchestrator orchestrator;
    private final StoreItemRepository storeItemRepository;

    private final List<String> ESSENTIALS = List.of(
            "Full Cream Milk 2L",
            "White Bread 700g",
            "Sunflower Oil 2L",
            "Coca-Cola 2L",
            "Tastic Rice 2kg"
    );

    @Scheduled(cron = "0 0 4 * * *")
    public void runDailyScrape() {
        log.info("STARTING DAILY DATA REFRESH at {}", LocalDateTime.now());

        OffsetDateTime threshold = OffsetDateTime.now().minusHours(24);

        List<String> staleProducts = storeItemRepository.findStaleProductNames(threshold);
        log.info("Found {} stale products (older than {}).", staleProducts.size(), threshold);

        Set<String> batchSet = new HashSet<>(ESSENTIALS);
        batchSet.addAll(staleProducts);

        List<String> batchToScrape = new ArrayList<>(batchSet);
        Collections.shuffle(batchToScrape);

        log.info("Total Target Search Terms for today: {}", batchToScrape.size());

        int count = 0;
        int maxItemsPerRun = 200;

        for (String term : batchToScrape) {
            if (count >= maxItemsPerRun) {
                log.warn("Reached safety limit of {} items. Stopping job for today.", maxItemsPerRun);
                break;
            }

            if (term == null || term.trim().length() < 3) continue;

            log.info("Refreshing Stale Item ({}/{}): {}", count + 1, batchToScrape.size(), term);

            try {
                orchestrator.scrapeAllStores(term);
                long sleepTime = 5000 + (long)(Math.random() * 5000);
                Thread.sleep(sleepTime);

            } catch (Exception e) {
                log.error("Failed to refresh: {}", term, e);
            }
            count++;
        }

        log.info("DAILY SCRAPE COMPLETED. Refreshed {} products.", count);
    }
}