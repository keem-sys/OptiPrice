package com.optiprice.dto.checkers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheckersPrice(
        @JsonProperty("priceWithoutDecimal")
        Integer priceWithoutDecimal,

        @JsonProperty("priceFactor")
        Integer priceFactor,

        @JsonProperty("currencySymbol")
        String currencySymbol
) {
        public Double value() {
                if (priceWithoutDecimal != null && priceFactor != null && priceFactor > 0) {
                        return priceWithoutDecimal.doubleValue() / priceFactor;
                }
                return null;
        }

        public String formattedValue() {
                Double val = value();
                if (val != null) {
                        String symbol = currencySymbol != null ? currencySymbol : "R";
                        return String.format("%s%.2f", symbol, val);
                }
                return null;
        }
}