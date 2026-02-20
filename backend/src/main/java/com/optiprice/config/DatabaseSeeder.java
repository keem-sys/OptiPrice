package com.optiprice.config;

import com.optiprice.repository.MasterProductRepository;
import com.optiprice.scraper.ScraperOrchestrator;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final MasterProductRepository masterProductRepository;
    private final ScraperOrchestrator orchestrator;

    @Override
    public void run(String @NonNull ... args) throws Exception {
        if (masterProductRepository.count() == 0) {
            System.out.println("Database is empty! Seeding initial data...");

            new Thread(() -> {
                orchestrator.scrapeAllStores("milk");
                orchestrator.scrapeAllStores("bread");
                System.out.println("Initial Database Seed Complete.");
            }).start();

        } else {
            System.out.println("Database already contains data. Skipping startup scrape.");
        }
    }
}