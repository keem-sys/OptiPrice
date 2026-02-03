package com.optiprice;

import com.optiprice.dto.checkers.CheckersProduct;
import com.optiprice.scraper.CheckersScraper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class OptiPriceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OptiPriceApplication.class, args);
	}

	@Bean
	CommandLineRunner testScraper(CheckersScraper scraper) {
		return args -> {
			System.out.println("--- STARTING PLAYWRIGHT CHECKERS SCRAPE ---");

			List<CheckersProduct> products = scraper.scrapeProducts("milk");

			if (products.isEmpty()) {
				System.out.println("No products found!");
			} else {
				System.out.println("=== RESULTS: " + products.size() + " PRODUCTS ===");
				System.out.println();

				for (int i = 0; i < Math.min(10, products.size()); i++) {
					CheckersProduct product = products.get(i);
					String priceStr = product.price() != null ? product.price().formattedValue() : "N/A";
					System.out.printf("%d. %s%n", (i + 1), product.name());
					System.out.printf("   Price: %s | Stock: %s | Article: %s%n",
							priceStr,
							product.isStockAvailable() ? "In Stock" : "Out of Stock",
							product.articleNumber()
					);
					System.out.println();
				}

				if (products.size() > 10) {
					System.out.println("... and " + (products.size() - 10) + " more products");
				}

				System.out.println("--- FINISHED ---");
			}
		};
	}
}