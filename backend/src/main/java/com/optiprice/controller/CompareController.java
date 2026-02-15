package com.optiprice.controller;

import com.optiprice.dto.response.MasterProductResponse;
import com.optiprice.dto.response.PagedResponse;
import com.optiprice.dto.response.PriceHistoryPoint;
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
    public PagedResponse<MasterProductResponse> compareProducts(
            @RequestParam("item") String item,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return productService.searchProducts(item, page, size);
    }

    @GetMapping("/product/{id}")
    public MasterProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/product/{id}/history")
    public List<PriceHistoryPoint> getProductHistory(@PathVariable Long id) {
        return productService.getPriceHistory(id);
    }

    @PostMapping("/scrape")
    public String triggerScrape(@RequestParam("item") String item) {
        new Thread(() -> scraperOrchestrator.scrapeAllStores(item)).start();
        return "Scraped started for: " + item + " . Check logs or refresh /compare in a minute";
    }
}
