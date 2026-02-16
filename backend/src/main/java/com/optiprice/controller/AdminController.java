package com.optiprice.controller;

import com.optiprice.scheduler.DailyScraperJob;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DailyScraperJob dailyScraperJob;

    @PostMapping("/trigger-daily-scrape")
    public ResponseEntity<String> triggerScrape() {
        new Thread(dailyScraperJob::runDailyScrape).start();
        return ResponseEntity.ok("Daily scrape triggered! Check server logs.");
    }
}