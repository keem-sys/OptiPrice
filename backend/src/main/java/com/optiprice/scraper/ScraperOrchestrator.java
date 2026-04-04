package com.optiprice.scraper;

import com.optiprice.dto.checkers.CheckersProduct;
import com.optiprice.dto.pnp.PnpImage;
import com.optiprice.dto.pnp.PnpProduct;
import com.optiprice.dto.pnp.PnpPromotion;
import com.optiprice.dto.shoprite.ShopriteProduct;
import com.optiprice.model.MasterProduct;
import com.optiprice.model.Store;
import com.optiprice.model.StoreItem;
import com.optiprice.repository.MasterProductRepository;
import com.optiprice.service.BrandExtractor;
import com.optiprice.service.MasterProductService;
import com.optiprice.service.StoreItemService;
import com.optiprice.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScraperOrchestrator {

    private final CheckersScraper checkersScraper;
    private final PnpScraper pnpScraper;
    private final ShopriteScraper shopriteScraper;

    private final StoreService storeService;
    private final StoreItemService storeItemService;
    private final BrandExtractor brandExtractor;
    private final MasterProductRepository masterProductRepository;

    public void scrapeAllStores(String searchTerm) {
        System.out.println("--- Orchestrating SEARCH for: " + searchTerm + " ---");

        try {
            List<CheckersProduct> products = checkersScraper.scrapeProducts(searchTerm);
            Store store = getCheckersStore();
            processCheckersProducts(products, store, null);
        } catch (Exception e) { System.err.println("Checkers Search Failed: " + e.getMessage()); }

        try {
            List<ShopriteProduct> products = shopriteScraper.scrapeProducts(searchTerm);
            Store store = getShopriteStore();
            processShopriteProducts(products, store, null);
        } catch (Exception e) { System.err.println("Shoprite Search Failed: " + e.getMessage()); }

        try {
            List<PnpProduct> products = pnpScraper.scrapeProducts(searchTerm);
            Store store = getPnpStore();
            processPnpProducts(products, store, null);
        } catch (Exception e) { System.err.println("PnP Search Failed: " + e.getMessage()); }
    }

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
                BigDecimal regularPrice = parseShopritePrice(p.price());
                BigDecimal promoPrice = parseShopritePrice(p.unitSalePrice());

                BigDecimal currentPrice;
                BigDecimal oldPrice = null;
                boolean isOnPromotion = false;
                String promoText = null;

                if (promoPrice != null && promoPrice.compareTo(BigDecimal.ZERO) > 0 && promoPrice.compareTo(regularPrice) < 0) {
                    currentPrice = promoPrice;
                    oldPrice = regularPrice;
                    isOnPromotion = true;
                    BigDecimal savings = regularPrice.subtract(promoPrice);
                    promoText = "Now R" + promoPrice.setScale(2, RoundingMode.HALF_UP);
                } else {
                    currentPrice = regularPrice;
                }

                String brand = brandExtractor.extractBrand(p.name(), p.brand());
                String articleSku = p.id();

                storeItemService.saveOrUpdateItem(
                        store,
                        p.id(),
                        p.name(),
                        brand,
                        currentPrice,
                        oldPrice,
                        p.productImageUrl(),
                        p.productUrl(),
                        knownCategory, p.barcode(),
                        isOnPromotion,
                        promoText,
                        articleSku
                );
            } catch (Exception e) { /* skip bad item */ }
        }
    }

    private void processPnpProducts(List<PnpProduct> products, Store store, String knownCategory) {
        if (products == null || products.isEmpty()) return;
        List<PnpProduct> safeList = products.stream().limit(40).toList();

        for (PnpProduct p : safeList) {
            try {
                String name = p.name();
                BigDecimal price = (p.price() != null && p.price().value() != null)
                        ? BigDecimal.valueOf(p.price().value())
                        : BigDecimal.ZERO;

                boolean isOnPromotion = false;
                String promoText = null;
                BigDecimal oldPrice = null;

                if (p.potentialPromotions() != null && !p.potentialPromotions().isEmpty()) {
                    isOnPromotion = true;
                    promoText = p.potentialPromotions().getFirst().promotionTextMessage();
                }

                if (p.price() != null && p.price().oldPrice() != null && p.price().oldPrice() > p.price().value()) {
                    oldPrice = BigDecimal.valueOf(p.price().oldPrice());
                    isOnPromotion = true;
                }

                String img = findBestPnpImage(p.images());
                String barcode = extractCleanBarcode(p.code());

                if (barcode == null && img != null) {
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("-(\\d{12,14})-");
                    java.util.regex.Matcher matcher = pattern.matcher(img);
                    if (matcher.find()) {
                        barcode = matcher.group(1);
                    }
                }

                if (barcode != null && (name.contains(" x ") || name.toLowerCase().contains("pack"))) {
                    barcode = null;
                    System.out.println("Discarded suspicious image barcode for bulk item: " + name);
                }


                String brand = brandExtractor.extractBrand(p.name(), p.brand());

                String productUrl = "https://www.pnp.co.za/p/" + p.code();

                storeItemService.saveOrUpdateItem(
                        store, p.code(), p.name(), brand, price, oldPrice, img, productUrl,
                        knownCategory, barcode, isOnPromotion, promoText, null
                );
            } catch (Exception e) { /* skip */ }
        }
    }

    private void processCheckersProducts(List<CheckersProduct> products, Store store, String knownCategory) {
        if (products == null || products.isEmpty()) return;
        List<CheckersProduct> safeList = products.stream().limit(40).toList();

        for (CheckersProduct p : safeList) {
            try {
                double basePrice = p.getPriceValue();
                BigDecimal currentPrice = BigDecimal.valueOf(basePrice);
                BigDecimal oldPrice = null;
                boolean isOnPromotion = false;
                String promoText = null;

                if (p.bonusBuy() != null) {
                    isOnPromotion = true;
                    promoText = p.bonusBuy().name();

                    Double val = p.bonusBuy().discountValue();
                    String typeCode = (p.bonusBuy().discountType() != null)
                            ? p.bonusBuy().discountType().code() : "price";

                    if (val != null && val > 0) {
                        if ("amount".equalsIgnoreCase(typeCode)) {
                            currentPrice = BigDecimal.valueOf(basePrice - val);
                            oldPrice = BigDecimal.valueOf(basePrice);
                        } else {
                            currentPrice = BigDecimal.valueOf(val);
                            oldPrice = BigDecimal.valueOf(basePrice);
                        }
                    }
                }

                else if (p.isOnPromotion() != null && p.isOnPromotion()) {
                    isOnPromotion = true;
                    promoText = "Special Offer";
                    if (p.oldPrice() != null && p.priceFactor() != null) {
                        oldPrice = BigDecimal.valueOf((double) p.oldPrice() / p.priceFactor());
                    }
                }

//                // kip items with 0 price (data errors)
//                if (currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
//                    continue;
//                }

                // Metadata Extraction
                String name = (p.displayName() != null) ? p.displayName() : p.name();
                String productUrl = "https://www.checkers.co.za/p/" + p.id();

                String barcode = null;
                if (p.barcodes() != null && p.barcodes().length > 0) {
                    barcode = extractCleanBarcode(p.barcodes()[0]);
                }

                String brand = brandExtractor.extractBrand(p.name(), p.brand());
                String articleSku = p.articleNumber() + p.unitOfMeasure();

                // Save
                storeItemService.saveOrUpdateItem(
                        store, p.id(), name, brand,
                        currentPrice, oldPrice,
                        p.getImageUrl(), productUrl,
                        knownCategory, barcode, isOnPromotion, promoText, articleSku
                );
            } catch (Exception e) {
                System.err.println("Error processing Checkers item: " + e.getMessage());
            }
        }
    }

    // Store Helpers

    public void scrapeSingleTarget(String storeName, String url, String category) {
        if (storeName.equalsIgnoreCase("Checkers")) {
            var results = checkersScraper.scrapeCategory(url);
            processCheckersProducts(results, getCheckersStore(), category);
        }
        else if (storeName.equalsIgnoreCase("Pick n Pay")) {
            var results = pnpScraper.scrapeCategory(url);
            processPnpProducts(results, getPnpStore(), category);
        }
        else if (storeName.equalsIgnoreCase("Shoprite")) {
            var results = shopriteScraper.scrapeCategory(url);
            processShopriteProducts(results, getShopriteStore(), category);
        }
    }

    private BigDecimal parseShopritePrice(String priceStr) {
        if (priceStr == null || priceStr.isBlank()) return null;
        try {
            String clean = priceStr.replace("R", "").replace(",", ".").trim();
            return new BigDecimal(clean);
        } catch (Exception e) {
            return null;
        }
    }

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

    private String findBestPnpImage(List<PnpImage> images) {
        if (images == null || images.isEmpty()) return null;

        for (PnpImage img : images) {
            if (img.url() != null) {
                if (img.url().contains("400Wx400H")) return img.url();
                if (img.url().contains("300Wx300H")) return img.url();
                if (img.url().contains("515Wx515H")) return img.url();
            }
        }

        for (PnpImage img : images) {
            if ("product".equalsIgnoreCase(img.format())) return img.url();
        }

        for (PnpImage img : images) {
            if ("zoom".equalsIgnoreCase(img.format())) return img.url();
        }

        return images.getFirst().url();
    }

    private String extractCleanBarcode(String rawCode) {
        if (rawCode == null) return null;
        String numbersOnly = rawCode.replaceAll("[^0-9]", "");
        String clean = numbersOnly.replaceFirst("^0+(?!$)", "");
        return (clean.length() >= 10) ? clean : null;
    }
}