package com.optiprice.dto.shoprite;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShopriteProduct(
        String id,
        String name,
        String price,
        @JsonProperty("brand")
        String brand,
        String stock,
        @JsonProperty("product_image_url")
        String productImageUrl,
        String productUrl,
        String barcode
) {
        public ShopriteProduct withUrl(String url, String foundBarcode) {
                return new ShopriteProduct(id, name, price, brand, stock, productImageUrl, url, foundBarcode);
        }

        public boolean isAvailable() {
                return stock == null || !stock.toLowerCase().contains("out");
        }
}