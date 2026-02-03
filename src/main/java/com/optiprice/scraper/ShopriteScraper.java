package com.optiprice.scraper;

import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optiprice.dto.shoprite.ShopriteProduct;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShopriteScraper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ShopriteProduct> scrapeProducts(String searchTerm) {
        List<ShopriteProduct> products = new ArrayList<>();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(List.of("--disable-blink-features=AutomationControlled")));

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"));

            Page page = context.newPage();

            String encodedSearch = URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);
            String url = "https://www.shoprite.co.za/search/all?q=" + encodedSearch;

            System.out.println("Navigating to Shoprite: " + url);
            page.navigate(url);


            try {
                page.waitForSelector(".product-frame", new Page.WaitForSelectorOptions().setTimeout(15000));
            } catch (Exception e) {
                System.out.println("Timed out waiting for products. Shoprite might be blocking or no results.");
                return products;
            }

            List<ElementHandle> productElements = page.querySelectorAll(".product-frame");
            System.out.println("Found " + productElements.size() + " product elements.");

            for (ElementHandle element : productElements) {
                String jsonAttribute = element.getAttribute("data-product-ga");

                if (jsonAttribute != null && !jsonAttribute.isEmpty()) {
                    try {
                        ShopriteProduct product = objectMapper.readValue(jsonAttribute, ShopriteProduct.class);
                        products.add(product);
                    } catch (Exception e) {
                        System.err.println("Failed to parse product JSON: " + e.getMessage());
                    }
                }
            }

            browser.close();

        } catch (Exception e) {
            System.err.println("Shoprite Scraper Error: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }
}