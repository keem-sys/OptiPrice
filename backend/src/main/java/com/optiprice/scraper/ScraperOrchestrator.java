package com.optiprice.scraper;

import com.optiprice.dto.checkers.CheckersProduct;
import com.optiprice.dto.pnp.PnpImage;
import com.optiprice.dto.pnp.PnpProduct;
import com.optiprice.dto.shoprite.ShopriteProduct;
import com.optiprice.model.Store;
import com.optiprice.service.StoreItemService;
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
    private final StoreItemService storeItemService;

    // Category is Unknown -> AI predict it
    public void scrapeAllStores(String searchTerm) {
        System.out.println("--- Orchestrating SEARCH for: " + searchTerm + " ---");

        try {
            List<ShopriteProduct> products = shopriteScraper.scrapeProducts(searchTerm);
            Store store = getShopriteStore();
            processShopriteProducts(products, store, null); // null category
        } catch (Exception e) { System.err.println("Shoprite Search Failed: " + e.getMessage()); }

        try {
            List<PnpProduct> products = pnpScraper.scrapeProducts(searchTerm);
            Store store = getPnpStore();
            processPnpProducts(products, store, null);
        } catch (Exception e) { System.err.println("PnP Search Failed: " + e.getMessage()); }

        try {
            List<CheckersProduct> products = checkersScraper.scrapeProducts(searchTerm);
            Store store = getCheckersStore();
            processCheckersProducts(products, store, null);
        } catch (Exception e) { System.err.println("Checkers Search Failed: " + e.getMessage()); }
    }

    // Category is Known -> Skip AI
    public void scrapeCategoryFromDb(String categoryName, String storeName, String url) {
        System.out.println("--- Orchestrating CRAWL for: " + categoryName + " @ " + storeName + " ---");

        if (storeName.equalsIgnoreCase("Checkers")) {
            try {
                List<CheckersProduct> products = checkersScraper.scrapeCategory(url);
                processCheckersProducts(products, getCheckersStore(), categoryName);
            } catch (Exception e) { System.err.println("Checkers Crawl Failed"); }
        }
        else if (storeName.equalsIgnoreCase("Shoprite")) {
            try {
                List<ShopriteProduct> products = shopriteScraper.scrapeCategory(url);
                processShopriteProducts(products, getShopriteStore(), categoryName);
            } catch (Exception e) { System.err.println("Shoprite Crawl Failed"); }
        }
        else if (storeName.equalsIgnoreCase("Pick n Pay")) {
            try {
                List<PnpProduct> products = pnpScraper.scrapeCategory(url);
                processPnpProducts(products, getPnpStore(), categoryName);
            } catch (Exception e) { System.err.println("PnP Crawl Failed"); }
        }
    }


    // Processing Logic
    private void processShopriteProducts(List<ShopriteProduct> products, Store store, String knownCategory) {
        for (ShopriteProduct p : products) {
            try {
                String cleanPrice = p.price().replace("R", "").replace(",", ".").trim();
                BigDecimal price = new BigDecimal(cleanPrice);

                storeItemService.saveOrUpdateItem(
                        store,
                        p.id(),
                        p.name(),
                        p.getDisplayBrand(),
                        price,
                        p.productImageUrl(),
                        p.productUrl(),
                        knownCategory
                );
            } catch (Exception e) { /* skip bad item */ }
        }
    }

    private void processPnpProducts(List<PnpProduct> products, Store store, String knownCategory) {
        if (products == null || products.isEmpty()) return;

        List<PnpProduct> safeList = products.stream().limit(40).toList();

        for (PnpProduct p : safeList) {
            try {
                BigDecimal price = (p.price() != null && p.price().value() != null)
                        ? BigDecimal.valueOf(p.price().value())
                        : BigDecimal.ZERO;

                String img = null;
                if (p.images() != null && !p.images().isEmpty()) {
                    img = p.images().stream().filter(i -> "zoom".equalsIgnoreCase(i.format())).findFirst()
                            .map(PnpImage::url).orElse(null);
                    if (img == null) img = p.images().getFirst().url();
                }

                String brand = (p.name().split("\\s+").length > 0) ? p.name().split("\\s+")[0] : "";
                String productUrl = "https://www.pnp.co.za/p/" + p.code();

                storeItemService.saveOrUpdateItem(
                        store, p.code(), p.name(), brand, price, img, productUrl,
                        knownCategory
                );
            } catch (Exception e) { /* skip */ }
        }
    }

    private void processCheckersProducts(List<CheckersProduct> products, Store store, String knownCategory) {
        if (products == null || products.isEmpty()) return;
        List<CheckersProduct> safeList = products.stream().limit(40).toList();

        for (CheckersProduct p : safeList) {
            try {
                String name = (p.displayName() != null) ? p.displayName() : p.name();
                String productUrl = "https://www.checkers.co.za/p/" + p.id();

                String brand = p.brand();
                if (brand == null || brand.trim().isEmpty()) {
                    brand = (name.split("\\s+").length > 0) ? name.split("\\s+")[0] : "Unknown";
                }

                storeItemService.saveOrUpdateItem(
                        store, p.id(), name, brand,
                        BigDecimal.valueOf(p.getPriceValue()),
                        p.getImageUrl(), productUrl,
                        knownCategory
                );
            } catch (Exception e) { /* skip */ }
        }
    }

    // Store Helpers

    private Store getShopriteStore() {
        return storeService.getOrCreateStore("Shoprite",
                "https://upload.wikimedia.org/wikipedia/commons/f/fc/Logo_-_Shoprite_-_SUPERMARCE.jpg",
                "https://shoprite.co.za/");
    }

    private Store getCheckersStore() {
        return storeService.getOrCreateStore("Checkers",
                "https://upload.wikimedia.org/wikipedia/en/thumb/b/b4/Checkers_%28supermarket_chain%29_Logo.svg/2560px-Checkers_%28supermarket_chain%29_Logo.svg.png",
                "https://checkers.co.za/");
    }

    private Store getPnpStore() {
        return storeService.getOrCreateStore("Pick n Pay",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f8/Pick_n_Pay_logo.svg/2560px-Pick_n_Pay_logo.svg.png",
                "https://pnp.co.za/");
    }
}