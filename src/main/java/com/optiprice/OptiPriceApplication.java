package com.optiprice;

import com.optiprice.dto.shoprite.ShopriteProduct;
import com.optiprice.scraper.ShopriteScraper;
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
	CommandLineRunner testShoprite(ShopriteScraper scraper) {
		return args -> {
			System.out.println("--- STARTING PLAYWRIGHT SHOPRITE SCRAPE ---");

			List<ShopriteProduct> products = scraper.scrapeProducts("milk");

			if (products.isEmpty()) {
				System.out.println("No products found! Check if the selector '.product-frame' is correct.");
			} else {
				System.out.println("=== RESULTS: " + products.size() + " PRODUCTS ===");
				System.out.println();

				for (int i = 0; i < Math.min(10, products.size()); i++) {
					ShopriteProduct product = products.get(i);

					System.out.printf("%d. %s%n", (i + 1), product.name());
					System.out.printf("   Price: R%s | Brand: %s | ID: %s%n",
							product.price(),
							product.getDisplayBrand(),
							product.id()
					);

					System.out.println();
				}

				if (products.size() > 10) {
					System.out.println("... and " + (products.size() - 10) + " more products.");
				}

				System.out.println("--- FINISHED ---");
			}
		};
	}
}