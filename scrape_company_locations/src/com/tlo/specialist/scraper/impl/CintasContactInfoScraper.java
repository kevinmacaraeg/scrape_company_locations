package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class CintasContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(CintasContactInfoScraper.class.getName());
	
	private static final String CINTAS_LOCATION_DIRECTORY_URL = "http://www.cintas.com/local/";
	
	private static final String CINTAS_WEBSITE_URL = "http://www.cintas.com";
	
	private static final String CINTAS_LOCATION_LINKS_CSS_SELECTOR = "ul.locations>li>a";
	
	private static final String CINTAS_OFFICE_LOCATIONS_CSS_SELECTOR = "div.contact-info";

	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + CINTAS_LOCATION_DIRECTORY_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(CINTAS_LOCATION_DIRECTORY_URL);
			Thread.sleep(3000);
			
			logger.info("Getting location URLs......");
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements countryLinksElements = websiteDocument.select(CINTAS_LOCATION_LINKS_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> countryURLs = JsoupHelper.getElementsHrefAttributes(countryLinksElements);
			countryURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(CINTAS_WEBSITE_URL, countryURLs);
			
			Set<String> locationURLs = new HashSet<String>();
			for (String countryURL : countryURLs) {
				
				logger.info("Connecting to " + countryURL + "......");
				driver = new ChromeDriver();
				driver.get(countryURL);
				Thread.sleep(3000);

				html_content = driver.getPageSource();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				Elements stateLinksElements = websiteDocument.select(CINTAS_LOCATION_LINKS_CSS_SELECTOR);
				
				Set<String> stateURLs = JsoupHelper.getElementsHrefAttributes(stateLinksElements);
				stateURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(CINTAS_WEBSITE_URL, stateURLs);
				
				for (String stateURL : stateURLs) {
					
					logger.info("Connecting to " + stateURL + "......");
					driver = new ChromeDriver();
					driver.get(stateURL);
					Thread.sleep(3000);

					html_content = driver.getPageSource();
					
					driver.quit();
					
					websiteDocument = Jsoup.parse(html_content);
					
					Elements locationLinksElements = websiteDocument.select(CINTAS_LOCATION_LINKS_CSS_SELECTOR);
					
					Set<String> locationURLsPerState = JsoupHelper.getElementsHrefAttributes(locationLinksElements);
					locationURLs.addAll(locationURLsPerState);
					
				}
				
			}
			
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(CINTAS_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationURLs, CINTAS_OFFICE_LOCATIONS_CSS_SELECTOR);
				
			logger.info("Writing company contacts to output file...");
			String outputFileName = service.getOutputFileName(masterCompanyName);
			File excelOutputFile = FileHelper.constructFile(outputFilePath, outputFileName);
			ExcelHelper.writeCompanyContactInfoToExcelFile(excelOutputFile, companyContactInformationList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
}
