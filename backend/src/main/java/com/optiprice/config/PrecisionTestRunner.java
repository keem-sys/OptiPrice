package com.optiprice.config;

import com.optiprice.scraper.ScraperOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrecisionTestRunner implements CommandLineRunner {

    private final ScraperOrchestrator orchestrator;

    private static final boolean RUN_TEST = false;

    @Override
    public void run(String... args) throws Exception {
        if (!RUN_TEST) return;

        System.out.println("🧪 --- STARTING PRECISION MATCHING TEST ---");

        String checkersUrl = "https://www.checkers.co.za/product/clover-fresh-full-cream-milk-2l-10413121EA";
        String pnpUrl = "https://www.pnp.co.za/clover-ultra-pasteurised-fresh-full-cream-milk-2l/p/000000000000555462_EA";
        String shopriteUrl = null;

        try {
            // Test Store 1 (Checkers)
            System.out.println("Step 1: Scrapping Checkers...");
            orchestrator.scrapeSingleTarget("Checkers", checkersUrl, "Dairy");

            // Test Store 2 (PnP)
            System.out.println("Step 2: Scrapping PnP...");
            orchestrator.scrapeSingleTarget("Pick n Pay", pnpUrl, "Dairy");

            // Test Store 3 (Shoprite)
            System.out.println("Step 3: Scrapping Shoprite...");
            orchestrator.scrapeSingleTarget("Shoprite", shopriteUrl, "Dairy");

            System.out.println("PRECISION TEST COMPLETE. Check DB for MasterProduct linking.");
        } catch (Exception e) {
            System.err.println("Test Failed: " + e.getMessage());
        }
    }
}