package com.optiprice.scraper;

import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.options.WaitUntilState;
import com.optiprice.dto.checkers.CheckersProduct;
import com.optiprice.dto.pnp.PnpResponse;
import com.optiprice.dto.pnp.PnpProduct;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PnpScraper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<PnpProduct> scrapeProducts(String searchTerm) {
        String encoded = URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);
        String url = "https://www.pnp.co.za/search/" + encoded;
        System.out.println("PNP: Scraping Search Term: " + url);
        return scrapeInternal(url);
    }

    public List<PnpProduct> scrapeCategory(String categoryUrl) {
        System.out.println("PNP: Scraping Category URL: " + categoryUrl);
        return scrapeInternal(categoryUrl);
    }

    public List<PnpProduct> scrapeInternal(String targetUrl) {
        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(List.of("--disable-blink-features=AutomationControlled", "--no-sandbox")));

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                    .setViewportSize(1920, 1080));

            Page page = context.newPage();
            AtomicReference<String> jsonCapture = new AtomicReference<>();

            page.onResponse(response -> {
                String url = response.url();
                if (url.contains("/products/search") && response.status() == 200) {
                    try {
                        String body = response.text();

                        JsonNode root = objectMapper.readTree(body);
                        if (root.has("products") && root.get("products").isArray() && root.get("products").size() > 0) {
                            jsonCapture.set(body);
                        }
                    } catch (Exception e) {
                    }
                }
            });

            page.navigate(targetUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

            page.waitForTimeout(2000);
            page.mouse().wheel(0, 400);

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

        System.out.println("PnP: No data captured after waiting.");
        return new ArrayList<>();
    }
}