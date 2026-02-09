package com.optiprice.utils;

import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Debug utility to discover Checkers API endpoints and JSON structure
 * This will save all JSON responses to files for inspection
 */
public class CheckersApiDiscovery {

    public static void main(String[] args) {
        String searchTerm = args.length > 0 ? args[0] : "milk";
        ObjectMapper objectMapper = new ObjectMapper();
        AtomicInteger responseCounter = new AtomicInteger(0);

        File outputDir = new File("checkers-api-responses");
        outputDir.mkdirs();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
                            .setSlowMo(500)
            );

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            );

            Page page = context.newPage();

            System.out.println("=== CHECKERS API DISCOVERY ===");
            System.out.println("Searching for: " + searchTerm);
            System.out.println("Saving responses to: " + outputDir.getAbsolutePath());
            System.out.println();

            // Log all responses
            page.onResponse(response -> {
                String url = response.url();

                System.out.println("→ " + response.request().method() + " " + response.status() + " " + url);

                try {
                    String contentType = response.headerValue("content-type");

                    if (contentType != null && contentType.contains("application/json")) {
                        String responseBody = response.text();
                        int count = responseCounter.incrementAndGet();

                        boolean mightBeProducts = url.contains("search") ||
                                url.contains("product") ||
                                url.contains("catalog") ||
                                url.contains("api") ||
                                url.contains("graphql");

                        String prefix = mightBeProducts ? "PRODUCTS_" : "other_";
                        String filename = prefix + count + ".json";

                        File outputFile = new File(outputDir, filename);
                        try (FileWriter writer = new FileWriter(outputFile)) {
                            JsonNode jsonNode = objectMapper.readTree(responseBody);
                            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
                                    .writeValueAsString(jsonNode);
                            writer.write(prettyJson);
                        }

                        System.out.println("  ✓ JSON Response saved to: " + filename);
                        System.out.println("  URL: " + url);
                        System.out.println("  Size: " + responseBody.length() + " chars");

                        if (responseBody.contains("\"name\"") &&
                                (responseBody.contains("\"price\"") || responseBody.contains("\"value\""))) {
                            System.out.println("  ⭐ LIKELY CONTAINS PRODUCT DATA!");
                        }
                        System.out.println();
                    }

                } catch (Exception e) {
                    System.err.println("  Error processing response: " + e.getMessage());
                }
            });

            String searchUrl = "https://www.checkers.co.za/search?Search=" + searchTerm;
            System.out.println("Navigating to: " + searchUrl);
            System.out.println();

            page.navigate(searchUrl);

            System.out.println("Waiting for page to load...");
            page.waitForTimeout(10000);

            System.out.println();
            System.out.println("=== SUMMARY ===");
            System.out.println("Total JSON responses captured: " + responseCounter.get());
            System.out.println("Check the files in: " + outputDir.getAbsolutePath());
            System.out.println("Look for files starting with 'PRODUCTS_' - these are most likely to contain product data");
            System.out.println();
            System.out.println("Browser will stay open for 20 seconds for manual inspection...");

            page.waitForTimeout(20000);

            browser.close();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}