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
        // scrapeShoprite(searchTerm);
        // scrapeCheckers(searchTerm);
        scrapePnp(searchTerm);
    }

    private void scrapeShoprite(String searchTerm) {
        try {
            Store shoprite = storeService.getOrCreateStore("Shoprite",
                    "https://upload.wikimedia.org/wikipedia/commons/f/fc/Logo_-_Shoprite_-_SUPERMARCE.jpg",
                    "https://shoprite.co.za/");
            List<ShopriteProduct> products = shopriteScraper.scrapeProducts(searchTerm);

            for (ShopriteProduct p : products) {
                String cleanPrice = p.price().replace("R", "").replace(",", ".").trim();
                BigDecimal price = new BigDecimal(cleanPrice);

                String productUrl = p.productUrl();

                productService.processScrapedProduct(
                        shoprite,
                        p.id(),
                        p.name(),
                        p.getDisplayBrand(),
                        price,
                        p.productImageUrl(),
                        productUrl
                );
            }
        } catch (Exception e) {
            System.err.println("Shoprite failed: " + e.getMessage());
        }
    }

    private void scrapeCheckers(String searchTerm) {
        try {
            Store checkers = storeService.getOrCreateStore("Checkers",
                    "https://upload.wikimedia.org/wikipedia/en/thumb/b/b4/" +
                            "Checkers_%28supermarket_chain%29_Logo.svg/2560px-Checkers_" +
                            "%28supermarket_chain%29_Logo.svg.png", "https://checkers.co.za/");
            List<CheckersProduct> products = checkersScraper.scrapeProducts(searchTerm);

            for (CheckersProduct p : products) {
                String name = (p.displayName() != null) ? p.displayName() : p.name();

                String productUrl = "https://www.checkers.co.za/p/" + p.id();

                String brand = p.brand();
                if (brand == null || brand.trim().isEmpty()) {
                    brand = (name.split("\\s+").length > 0) ? name.split("\\s+")[0] : "Unknown";
                }
                productService.processScrapedProduct(
                        checkers, p.id(), name, brand,
                        BigDecimal.valueOf(p.getPriceValue()),
                        p.getImageUrl(), productUrl
                );
            }
        } catch (Exception e) {
            System.err.println("Checkers failed: " + e.getMessage());
        }
    }

    private void scrapePnp(String searchTerm) {
        try {
            Store pnp = storeService.getOrCreateStore("Pick n Pay",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/f/" +
                            "f8/Pick_n_Pay_logo.svg/2560px-Pick_n_Pay_logo.svg.png", "https://pnp.co.za/");
            List<PnpProduct> products = pnpScraper.scrapeProducts(searchTerm);

            if (products.isEmpty()) {
                System.out.println("PnP Scraper returned 0 products.");
                return;
            }

            for (PnpProduct p : products) {
                BigDecimal price = (p.price() != null && p.price().value() != null)
                        ? BigDecimal.valueOf(p.price().value())
                        : BigDecimal.ZERO;

                String img = (p.images() != null && !p.images().isEmpty()) ? p.images().get(0).url() : null;
                String brand = (p.name().split("\\s+").length > 0) ? p.name().split("\\s+")[0] : "";

                String productUrl = "https://www.pnp.co.za/p/" + p.code();

                productService.processScrapedProduct(
                        pnp, p.code(), p.name(), brand, price, img,
                        productUrl
                );
            }
        } catch (Exception e) {
            System.err.println("PnP failed: " + e.getMessage());
        }
    }
}