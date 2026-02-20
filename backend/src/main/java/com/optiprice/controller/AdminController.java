package com.optiprice.controller;

import com.optiprice.model.StoreCategory;
import com.optiprice.repository.StoreCategoryRepository;
import com.optiprice.scheduler.DailyScraperJob;
import com.optiprice.scheduler.CategoryScraperJob;
import com.optiprice.scraper.ScraperOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DailyScraperJob dailyScraperJob;
    private final StoreCategoryRepository storeCategoryRepository;
    private final ScraperOrchestrator orchestrator;


    @PostMapping("/trigger-daily-scrape")
    public ResponseEntity<String> triggerScrape() {
        new Thread(dailyScraperJob::runDailyScrape).start();
        return ResponseEntity.ok("Daily scrape triggered! Check logs.");
    }

    @PostMapping("/trigger-category-crawl")
    public ResponseEntity<String> triggerCategoryCrawl(@RequestParam String category) {
        new Thread(() -> {
            List<StoreCategory> links = storeCategoryRepository.findByCategoryNameIgnoreCase(category);
            for (StoreCategory link : links) {
                orchestrator.scrapeCategoryFromDb(
                        link.getCategory().getName(),
                        link.getStore().getName(),
                        link.getUrl()
                );
            }
        }).start();

        return ResponseEntity.ok("Started crawling category: " + category + ". Check logs!");
    }
}