package com.optiprice;

import com.optiprice.scraper.ScraperOrchestrator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class OptiPriceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OptiPriceApplication.class, args);
	}

	@Bean
	CommandLineRunner testFullShops(ScraperOrchestrator orchestrator) {
		return args -> {
			System.out.println("PHASE 1: STARTING FULL SHOPS SCRAPE");
				orchestrator.scrapeAllStores("milk");
			System.out.println("PHASE 1 COMPLETE: DATA SHOULD BE PERSISTED");
		};
	}
}