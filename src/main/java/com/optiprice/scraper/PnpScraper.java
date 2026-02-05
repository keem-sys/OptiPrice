package com.optiprice.scraper;

import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.options.WaitUntilState;
import com.optiprice.dto.checkers.CheckersProduct;
import com.optiprice.dto.pnp.PnpResponse;
import com.optiprice.dto.pnp.PnpProduct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PnpScraper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<PnpProduct> scrapeProducts(String searchTerm) {
        try (Playwright playwright = Playwright.create()) {

            List<String> args = new ArrayList<>();
            args.add("--disable-blink-features=AutomationControlled");
            args.add("--no-sandbox");

            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(args));

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36")
                    .setViewportSize(1920, 1080)
                    .setLocale("en-ZA"));

            context.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

            Page page = context.newPage();

            AtomicReference<String> jsonCapture = new AtomicReference<>();

            page.onResponse(response -> {
                String url = response.url();
                if (url.contains("/products/search") &&
                        url.toLowerCase().contains("query=" + searchTerm.toLowerCase()) &&
                        response.status() == 200) {
                    try {
                        jsonCapture.set(response.text());
                    } catch (Exception e) {
                    }
                }
            });

            String searchUrl = "https://www.pnp.co.za/search/" + searchTerm;
            System.out.println("PnP: Navigating to " + searchUrl);
            page.navigate(searchUrl);

            for (int i = 0; i < 30; i++) {
                if (jsonCapture.get() != null) break;
                page.waitForTimeout(1000);
            }

            browser.close();

            if (jsonCapture.get() != null) {
                PnpResponse response = objectMapper.readValue(jsonCapture.get(), PnpResponse.class);
                List<PnpProduct> products = response.products() != null ? response.products() : new ArrayList<>();
                System.out.println("Parsed " + products.size() + " products from PnP API response");
                return response.products();
            }

        } catch (Exception e) {
            System.err.println("Playwright Error: " + e.getMessage());
        }

        return new ArrayList<>();
    }


    // JUST FOR TESTING
    public static void main(String[] args) {
        String searchTerm = args.length > 0 ? args[0] : "milk";

        System.out.println("=== PNP SCRAPER TEST ===");
        System.out.println("Search term: " + searchTerm);
        System.out.println();

        PnpScraper scraper = new PnpScraper();
        List<PnpProduct> products = scraper.scrapeProducts(searchTerm);

        System.out.println();
        if (products.isEmpty()) {
            System.out.println("❌ No products found!");
        } else {
            System.out.println("=== RESULTS: " + products.size() + " PRODUCTS ===");
            System.out.println();

            for (int i = 0; i < Math.min(10, products.size()); i++) {
                PnpProduct product = products.get(i);
                String priceStr = product.price() != null ? product.price().formattedValue() : "N/A";
                System.out.printf("%d. %s%n", (i + 1), product.name());
                System.out.printf("   Price: %s | Stock: %s | Article: %s%n",
                        priceStr,
                        product.inStockIndicator() ? "In Stock" : "Out of Stock",
                        product.code()
                );
                System.out.println();
            }

            if (products.size() > 10) {
                System.out.println("... and " + (products.size() - 10) + " more products");
            }
        }
    }
}