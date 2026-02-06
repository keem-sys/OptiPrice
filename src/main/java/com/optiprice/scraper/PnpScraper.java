package com.optiprice.scraper;

import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.options.WaitUntilState;
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

            // 1. Launch with standard stealth args
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(List.of("--disable-blink-features=AutomationControlled", "--no-sandbox")));

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                    .setViewportSize(1920, 1080));

            Page page = context.newPage();
            AtomicReference<String> jsonCapture = new AtomicReference<>();

            // 2. Flexible Interceptor
            page.onResponse(response -> {
                String url = response.url();
                // We only care about the search endpoint
                if (url.contains("/products/search") && response.status() == 200) {
                    try {
                        String body = response.text();

                        // Use Jackson to check if products exist (safer than string matching)
                        JsonNode root = objectMapper.readTree(body);
                        if (root.has("products") && root.get("products").isArray() && root.get("products").size() > 0) {
                            // Only capture if we actually found items
                            jsonCapture.set(body);
                        }
                    } catch (Exception e) {
                        // ignore parsing errors of intermediate requests
                    }
                }
            });

            // 3. Direct Navigation (Fastest)
            String searchUrl = "https://www.pnp.co.za/search/" + searchTerm;
            System.out.println("PnP: Navigating to " + searchUrl);

            // Wait for load, but don't hang on networkidle which can be slow
            page.navigate(searchUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

            // 4. Mimic human activity to trigger the second AJAX call
            page.waitForTimeout(2000);
            page.mouse().wheel(0, 400); // Small scroll

            // 5. Patient Wait loop
            for (int i = 0; i < 20; i++) {
                if (jsonCapture.get() != null) break;
                page.waitForTimeout(1000);
            }

            browser.close();

            if (jsonCapture.get() != null) {
                PnpResponse response = objectMapper.readValue(jsonCapture.get(), PnpResponse.class);
                List<PnpProduct> products = response.products() != null ? response.products() : new ArrayList<>();
                System.out.println("✓ Successfully captured " + products.size() + " products for PnP.");
                return products;
            }

        } catch (Exception e) {
            System.err.println("PnP Scraper Error: " + e.getMessage());
        }

        System.out.println("⚠ PnP: No data captured after waiting.");
        return new ArrayList<>();
    }
}