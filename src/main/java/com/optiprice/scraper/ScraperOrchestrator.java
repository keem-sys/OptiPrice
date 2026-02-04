package com.optiprice.scraper;

import com.optiprice.dto.checkers.CheckersProduct;
import com.optiprice.dto.pnp.PnpProduct;
import com.optiprice.dto.shoprite.ShopriteProduct;
import com.optiprice.model.Store;
import com.optiprice.service.ProductService;
import com.optiprice.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScraperOrchestrator {

    private final CheckersScraper checkersScraper;
    private final PnpScraper pnpScraper;
    private final ShopriteScraper shopriteScraper;

    private final StoreService storeService;
    private final ProductService productService;

    public void scrapeAllStores(String searchTerm) {
        System.out.println("--- Orchestrating Scrape for: " + searchTerm + " ---");
        scrapeShoprite(searchTerm);
        scrapeCheckers(searchTerm);
        scrapePnp(searchTerm);
    }

    private void scrapeShoprite(String searchTerm) {
        try {
            Store shoprite = storeService.getOrCreateStore("Shoprite",
                    "https://shoprite.co.za/logo.png");
            List<ShopriteProduct> products = shopriteScraper.scrapeProducts(searchTerm);

            for (ShopriteProduct p : products) {
                BigDecimal price = new BigDecimal(p.price().replace("R", "").replace(",", ".").trim());

                productService.processScrapedProduct(
                        shoprite, p.id(), p.name(), p.getDisplayBrand(), price, null,
                        "https://shoprite.co.za/search/all?q=" + searchTerm
                );
            }
        } catch (Exception e) {
            System.err.println("Shoprite failed: " + e.getMessage());
        }
    }

    private void scrapeCheckers(String searchTerm) {
        try {
            Store checkers = storeService.getOrCreateStore("Checkers",
                    "https://checkers.co.za/logo.png");
            List<CheckersProduct> products = checkersScraper.scrapeProducts(searchTerm);

            for (CheckersProduct p : products) {
                String name = (p.displayName() != null) ? p.displayName() : p.name();
                String url = "https://www.checkers.co.za/search?Search=" + p.articleNumber();

                productService.processScrapedProduct(
                        checkers, p.id(), name, p.brand(),
                        BigDecimal.valueOf(p.getPriceValue()),
                        p.getImageUrl(), url
                );
            }
        } catch (Exception e) {
            System.err.println("Checkers failed: " + e.getMessage());
        }
    }

    private void scrapePnp(String searchTerm) {
        try {
            Store pnp = storeService.getOrCreateStore("Pick n Pay",
                    "https://pnp.co.za/logo.png");
            List<PnpProduct> products = pnpScraper.scrapeProducts(searchTerm);

            for (PnpProduct p : products) {
                BigDecimal price = (p.price() != null && p.price().value() != null)
                        ? BigDecimal.valueOf(p.price().value())
                        : BigDecimal.ZERO;

                String img = (p.images() != null && !p.images().isEmpty()) ? p.images().get(0).url() : null;
                String brand = (p.name().split("\\s+").length > 0) ? p.name().split("\\s+")[0] : "";

                productService.processScrapedProduct(
                        pnp, p.code(), p.name(), brand, price, img,
                        "https://www.pnp.co.za/search/" + p.name()
                );
            }
        } catch (Exception e) {
            System.err.println("PnP failed: " + e.getMessage());
        }
    }
}