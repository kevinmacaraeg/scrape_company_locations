package com.tlo.specialist.main;

import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.tlo.specialist.service.ScrapeCompanyContactInfoService;

public class ScrapeCompanyContactInfoByCompanyLocationsDetailMain {
	
	private static Logger logger;
	
	private static String companyLocationsDetailsFilePath;
	
	private static String sheetName;
	
	private static String outputFilePath;
	
	private static ScrapeCompanyContactInfoService service;
	
	public static void main(String[] args) {
		if (args.length == 3) {
			
			companyLocationsDetailsFilePath = args[0];
			sheetName = args[1];
			outputFilePath = args[2];
			
			try {
				
				init();
				
				logger = Logger.getLogger(ScrapeCompanyContactInfoOnePageMain.class.getName());
				
				logger.info("Scrape Company Contact Information by Company Locations Details from file batch process started at " + new Date(System.currentTimeMillis()));
				
				service.scrapeCompanyContactInfoByCompanyLocationsDetailsFromFile(companyLocationsDetailsFilePath, sheetName, outputFilePath);
				
				logger.info("Scrape Company Contact Information by Company Locations Details from file batch process ended at " + new Date(System.currentTimeMillis()));
				
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
			
			service = new ScrapeCompanyContactInfoService();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
