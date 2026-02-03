package com.optiprice.scraper;

import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            AtomicReference<String> jsonCapture = new AtomicReference<>();

            page.onResponse(response -> {
                if (response.url().contains("/products/search") && response.url().contains("query=" + searchTerm)) {
                    jsonCapture.set(response.text());
                }
            });

            String searchUrl = "https://www.pnp.co.za/search/" + searchTerm;
            page.navigate(searchUrl);

            for (int i = 0; i < 10; i++) {
                if (jsonCapture.get() != null) break;
                page.waitForTimeout(1000);
            }

            browser.close();

            if (jsonCapture.get() != null) {
                PnpResponse response = objectMapper.readValue(jsonCapture.get(), PnpResponse.class);
                return response.products();
            }

        } catch (Exception e) {
            System.err.println("Playwright Error: " + e.getMessage());
        }

        return new ArrayList<>();
    }
}