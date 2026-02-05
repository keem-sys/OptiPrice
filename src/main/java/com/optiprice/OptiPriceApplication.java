package com.optiprice;

import com.optiprice.scraper.ScraperOrchestrator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class OptiPriceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OptiPriceApplication.class, args);
	}

	@Bean
	CommandLineRunner testShoprite(ScraperOrchestrator orchestrator) {
		return args -> {
			System.out.println("--- STARTING PLAYWRIGHT SCRAPE ---");
				orchestrator.scrapeAllStores("milk");
			System.out.println("--- FINISHED ---");
		};
	}
}