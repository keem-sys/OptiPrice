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
    private final CategoryScraperJob categoryScraperJob;

    @PostMapping("/trigger-daily-scrape")
    public ResponseEntity<String> triggerScrape() {
        dailyScraperJob.runDailyScrape();
        return ResponseEntity.ok("Daily scrape triggered! Check logs.");
    }

    @PostMapping("/trigger-category-crawl")
    public ResponseEntity<String> triggerCategoryCrawl(@RequestParam String category) {
        categoryScraperJob.crawlCategory(category);
        return ResponseEntity.ok("Started crawling category: " + category + ". Check logs!");
    }
}