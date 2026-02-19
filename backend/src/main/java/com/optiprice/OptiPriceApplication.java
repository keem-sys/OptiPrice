package com.optiprice;

import com.optiprice.scraper.ScraperOrchestrator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableCaching
public class OptiPriceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OptiPriceApplication.class, args);
	}

	@Bean
	CommandLineRunner testFullShops(ScraperOrchestrator orchestrator) {
		return args -> {
			System.out.println("STARTING FULL SHOPS SCRAPE");
				orchestrator.scrapeAllStores("milk");
			System.out.println("COMPLETE: DATA SHOULD BE PERSISTED");
		};
	}
}