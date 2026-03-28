package com.optiprice.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BrandExtractor {

    private static final List<String> KNOWN_BRANDS = List.of(
            "Clover", "Parmalat", "Fair Cape", "Cadbury", "Nestle", "Nestlé",
            "Albany", "Blue Ribbon", "Sasko", "Omo", "Sunlight", "Sta-Soft",
            "Tastic", "Fatti's & Moni's", "Kellogg's", "Bokomo", "Jungle Oats",
            "Jacobs", "Nescafe", "Nescafé", "Ricoffy", "Five Roses", "Freshpak",
            "Coca-Cola", "Coke", "Pepsi", "Fanta", "Sprite", "Stoney",
            "Knorr", "Maggi", "Robertson's", "Simba", "Lays", "Doritos",
            "Crystal Valley", "Ritebrand", "Checkers", "PnP", "Pick n Pay", "Simple Truth"
    );

    public String extractBrand(String productName, String jsonBrand) {
        if (jsonBrand != null && !jsonBrand.trim().isEmpty() && !jsonBrand.equalsIgnoreCase("Unbranded")) {
            return jsonBrand.trim();
        }

        String name = productName.trim();
        for (String brand : KNOWN_BRANDS) {
            if (name.regionMatches(true, 0, brand, 0, brand.length())) {
                return brand;
            }
        }

        String[] words = name.split("\\s+");
        if (words.length > 0) {
            String firstWord = words[0];
            if (firstWord.length() > 2 && !isCommonAdjective(firstWord)) {
                return firstWord;
            }
        }

        return "Unknown";
    }

    private boolean isCommonAdjective(String word) {
        String w = word.toLowerCase();
        return List.of("fresh", "large", "small", "value", "bulk", "frozen", "sliced", "white", "brown")
                .contains(w);
    }
}