package com.optiprice.dto.checkers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheckersProduct(
        @JsonProperty("id")
        String id,

        @JsonProperty("name")
        String name,

        @JsonProperty("brand")
        String brand,

        @JsonProperty("displayName")
        String displayName,

        @JsonProperty("description")
        String description,

        @JsonProperty("articleNumber")
        String articleNumber,

        @JsonProperty("priceWithoutDecimal")
        Integer priceWithoutDecimal,

        @JsonProperty("priceFactor")
        Integer priceFactor,

        @JsonProperty("currencySymbol")
        String currencySymbol,

        @JsonProperty("currency")
        String currency,

        @JsonProperty("discount")
        Integer discount,

        @JsonProperty("oldPrice")
        Integer oldPrice,

        @JsonProperty("isOnPromotion")
        Boolean isOnPromotion,

        // Stock fields
        @JsonProperty("isStockAvailable")
        Boolean isStockAvailable,

        @JsonProperty("stockOnHand")
        Integer stockOnHand,

        // Other useful fields
        @JsonProperty("imageId")
        String imageId,

        @JsonProperty("unitOfMeasure")
        String unitOfMeasure,

        @JsonProperty("packQuantity")
        Integer packQuantity,

        @JsonProperty("barcodes")
        String[] barcodes
) {
        public CheckersPrice price() {
                return new CheckersPrice(priceWithoutDecimal, priceFactor, currencySymbol);
        }

        public String getImageUrl() {
                if (imageId != null && !imageId.isEmpty()) {
                        return "https://catalog.sixty60.co.za/v2/files/" + imageId + "?width=256&height=256";
                }
                return null;
        }

        public double getPriceValue() {
                if (priceWithoutDecimal != null && priceFactor != null && priceFactor > 0) {
                        return (double) priceWithoutDecimal / priceFactor;
                }
                return 0.0;
        }
}