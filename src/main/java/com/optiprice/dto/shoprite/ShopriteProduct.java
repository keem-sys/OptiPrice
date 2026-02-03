package com.optiprice.dto.shoprite;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShopriteProduct(
        String id,
        String name,
        String price,
        String brand,
        String stock
) {

        public String getDisplayBrand() {
                if (brand != null && !brand.trim().isEmpty()) {
                        return brand;
                }

                if (name != null && !name.isEmpty()) {
                        String[] parts = name.trim().split("\\s+");
                        if (parts.length > 0) {
                                return parts[0];
                        }
                }

                return "Unknown";
        }

        public boolean isAvailable() {
                return stock == null || !stock.toLowerCase().contains("out");
        }
}