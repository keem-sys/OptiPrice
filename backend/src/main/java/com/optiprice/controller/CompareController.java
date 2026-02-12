package com.optiprice.controller;

import com.optiprice.dto.response.MasterProductResponse;
import com.optiprice.dto.response.ProductSearchResponse;
import com.optiprice.scraper.ScraperOrchestrator;
import com.optiprice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CompareController {
    private final ProductService productService;
    private  final ScraperOrchestrator scraperOrchestrator;

    @GetMapping("/compare")
    public List<MasterProductResponse> compareProducts(@RequestParam("item") String item) {
        return productService.searchProducts(item);
    }

    @PostMapping("/scrape")
    public String triggerScrape(@RequestParam("item") String item) {
        new Thread(() -> scraperOrchestrator.scrapeAllStores(item)).start();
        return "Scraped started for: " + item + " . Check logs or refresh /compare in a minute";
    }
}
