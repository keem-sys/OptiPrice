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

        @JsonProperty("price") Double rawPrice,

        @JsonProperty("oldPrice")
        Integer oldPrice,

        @JsonProperty("isOnPromotion")
        Boolean isOnPromotion,

        @JsonProperty("isStockAvailable")
        Boolean isStockAvailable,

        @JsonProperty("imageId")
        String imageId,

        @JsonProperty("unitOfMeasure")
        String unitOfMeasure,

        @JsonProperty("barcodes")
        String[] barcodes,

        @JsonProperty("bonusBuy")
        CheckersBonusBuy bonusBuy
) {
        public CheckersPrice price() {
                return new CheckersPrice(priceWithoutDecimal, priceFactor, currencySymbol);
        }

        public String getImageUrl() {
                if (imageId != null && !imageId.isEmpty()) {
                        return "https://catalog.sixty60.co.za/v2/files/" + imageId + "?width=512&height=512";
                }
                return null;
        }

        public double getPriceValue() {
                if (priceWithoutDecimal != null && priceFactor != null && priceFactor > 0) {
                        return (double) priceWithoutDecimal / priceFactor;
                }

                if (rawPrice != null && rawPrice > 0) {
                        return rawPrice;
                }

                if (oldPrice != null && priceFactor != null && priceFactor > 0) {
                        return (double) oldPrice / priceFactor;
                }

                return 0.0;
        }
}