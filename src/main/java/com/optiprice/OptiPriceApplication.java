package com.optiprice;

import com.optiprice.scraper.PnpScraper;
import com.optiprice.dto.pnp.PnpProduct;
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
	CommandLineRunner testScraper(PnpScraper scraper) {
		return args -> {
			System.out.println("--- STARTING PLAYWRIGHT PNP SCRAPE ---");

			List<PnpProduct> products = scraper.scrapeProducts("milk");

			products.forEach(p -> {
				System.out.println("Found: " + p.name() + " | Price: " + p.price().formattedValue());
			});

			System.out.println("--- FINISHED ---");
		};
	}
}