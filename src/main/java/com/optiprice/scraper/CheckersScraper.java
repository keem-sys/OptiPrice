package com.optiprice.scraper;

import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.optiprice.dto.checkers.CheckersProduct;
import com.optiprice.dto.checkers.CheckersResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Scraper for Checkers Sixty60 product data
 * Intercepts the POST request to /api/catalogue/get-products-filter
 */
@Service
public class CheckersScraper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<CheckersProduct> scrapeProducts(String searchTerm) {
        try (Playwright playwright = Playwright.create()) {

            List<String> args = new ArrayList<>();
            args.add("--disable-blink-features=AutomationControlled");
            args.add("--no-sandbox");

            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(args);

            Browser browser = playwright.chromium().launch(launchOptions);

            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36")
                    .setViewportSize(1920, 1080)
                    .setDeviceScaleFactor(1.0)
                    .setHasTouch(false)
                    .setJavaScriptEnabled(true)
                    .setLocale("en-ZA")
                    .setTimezoneId("Africa/Johannesburg");

            BrowserContext context = browser.newContext(contextOptions);

            context.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

            Page page = context.newPage();

            AtomicReference<String> jsonCapture = new AtomicReference<>();

            page.onResponse(response -> {
                String url = response.url();

                if (url.contains("/api/catalogue/get-products-filter") &&
                        response.status() == 200 &&
                        !url.contains("filter-options")) {
                    try {
                        String responseBody = response.text();
                        System.out.println("Captured product data from: " + url);
                        System.out.println("Response size: " + responseBody.length() + " characters");
                        jsonCapture.set(responseBody);
                    } catch (Exception e) {
                        System.err.println("Error reading response: " + e.getMessage());
                    }
                }
            });

            String searchUrl = "https://www.checkers.co.za/search?Search=" + searchTerm;
            System.out.println("Navigating to: " + searchUrl);
            page.navigate(searchUrl);

            for (int i = 0; i < 20; i++) {
                if (jsonCapture.get() != null) {
                    System.out.println("✓ API response captured after " + (i + 1) + " seconds");
                    break;
                }
                page.waitForTimeout(1000);
            }

            browser.close();

            if (jsonCapture.get() != null) {
                return parseProducts(jsonCapture.get());
            } else {
                System.err.println("No API response captured for search term: " + searchTerm);
            }

        } catch (Exception e) {
            System.err.println("Checkers Scraper Error: " + e.getMessage());
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private List<CheckersProduct> parseProducts(String json) {
        List<CheckersProduct> products = new ArrayList<>();

        try {
            CheckersResponse response = objectMapper.readTree(json)
                    .traverse(objectMapper)
                    .readValueAs(CheckersResponse.class);

            if (response.products() != null && !response.products().isEmpty()) {
                System.out.println("Parsed " + response.products().size() + " products from response");
                return response.products();
            } else {
                System.err.println("No products found in response");
            }

        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());

            try {
                JsonNode root = objectMapper.readTree(json);
                if (root.has("products") && root.get("products").isArray()) {
                    JsonNode productsArray = root.get("products");
                    System.out.println("⚠ Fallback parsing found " + productsArray.size() + " products");

                    for (JsonNode productNode : productsArray) {
                        try {
                            CheckersProduct product = objectMapper.treeToValue(productNode, CheckersProduct.class);
                            if (product != null && product.name() != null) {
                                products.add(product);
                            }
                        } catch (Exception pe) {
                        }
                    }
                }
            } catch (Exception fallbackError) {
                System.err.println("Fallback parsing also failed: " + fallbackError.getMessage());
            }
        }

        return products;
    }
}