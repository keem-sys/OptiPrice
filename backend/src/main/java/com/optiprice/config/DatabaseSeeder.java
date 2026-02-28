package com.optiprice.config;

import com.optiprice.model.Store;
import com.optiprice.model.StoreItem;
import com.optiprice.repository.MasterProductRepository;
import com.optiprice.repository.StoreItemRepository;
import com.optiprice.scraper.ScraperOrchestrator;
import com.optiprice.service.MatchingService;
import com.optiprice.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final MasterProductRepository masterProductRepository;
    private final ScraperOrchestrator orchestrator;
    private final StoreItemRepository storeItemRepository;
    private final StoreService storeService;
    private final MatchingService matchingService;

    @Override
    public void run(String @NonNull ... args) throws Exception {
        if (masterProductRepository.count() == 0) {
            System.out.println("Database is empty! Seeding initial data...");

            new Thread(() -> {
                orchestrator.scrapeAllStores("milk");
                // orchestrator.scrapeAllStores("bread");
                // orchestrator.scrapeAllStores("Nestlé Ideal Evaporated Milk 380ml");
                System.out.println("Initial Database Seed Complete.");
            }).start();

        } else {
            System.out.println("Database already contains data. Skipping startup scrape.");
        }
    }
}