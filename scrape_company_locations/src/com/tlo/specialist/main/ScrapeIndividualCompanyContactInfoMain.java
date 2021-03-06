package com.tlo.specialist.main;

import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.util.CompanyContactInfoScraperFactory;

public class ScrapeIndividualCompanyContactInfoMain {

	private static Logger logger;
	
	private static String outputFilePath;
	
	private static String masterCompanyId;
	
	private static String masterCompanyName;
	
	private static CompanyContactInfoScraper scraper;

	public static void main(String[] args) {
		if (args.length == 3) {
			
			masterCompanyId = args[0];
			masterCompanyName = args[1];
			outputFilePath = args[2];
			
			try {
				
				init();
				
				logger = Logger.getLogger(ScrapeIndividualCompanyContactInfoMain.class.getName());
				
				logger.info("Scrape " + masterCompanyName + " Contact Information batch process started at " + new Date(System.currentTimeMillis()));
				
				scraper.scrapeCompanyContactInformation(masterCompanyId, masterCompanyName, outputFilePath);
				
				logger.info("Scrape " + masterCompanyName + " Contact Information batch process ended at " + new Date(System.currentTimeMillis()));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Invalid number of parameters!");
		}
	}
	
	public static void init() {
		try {
			URL log4jUrl = Thread.currentThread().getContextClassLoader().getResource("properties/tlo-log4j.properties");
			PropertyConfigurator.configure(log4jUrl.getFile());
			
			System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");
			
			scraper = CompanyContactInfoScraperFactory.getScraper(masterCompanyId);
			if (scraper == null) throw new Exception(masterCompanyName + " does not have an existing scraper yet!");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
