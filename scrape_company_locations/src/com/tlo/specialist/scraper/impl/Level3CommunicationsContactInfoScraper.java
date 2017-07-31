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

public class Level3CommunicationsContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(Level3CommunicationsContactInfoScraper.class.getName());
	
	private static final String LEVEL3_GLOBAL_REACH_URL = "http://www.level3.com/en/global-reach/";
	
	private static final String LEVEL3_WEBSITE_URL = "http://www.level3.com";
	
	private static final String LEVEL3_REGION_LINKS_CSS_SELECTOR = "div.map-nav-btns.module>ul>li>a.button.sky";
	
	private static final String LEVEL3_COUNTRY_LINKS_CSS_SELECTOR = "div.regional-links>a";
	
	private static final String LEVEL3_OFFICE_LOCATIONS_CSS_SELECTOR = "div.contact";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + LEVEL3_GLOBAL_REACH_URL +"......");
			WebDriver driver = new ChromeDriver();
			driver.get(LEVEL3_GLOBAL_REACH_URL);
			Thread.sleep(3000);
			
			logger.info("Getting location URLs......");
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Elements regionLinksElements = websiteDocument.select(LEVEL3_REGION_LINKS_CSS_SELECTOR);
			Set<String> regionURLs = JsoupHelper.getElementsHrefAttributes(regionLinksElements);
			regionURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(LEVEL3_WEBSITE_URL, regionURLs);
			
			Set<String> locationURLs = new HashSet<String>();
			for (String regionURL : regionURLs) {
				
				logger.info("Connecting to " + regionURL +"......");
				driver = new ChromeDriver();
				driver.get(regionURL);
				Thread.sleep(3000);
				
				html_content = driver.getPageSource();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				Elements countryLinksElements = websiteDocument.select(LEVEL3_COUNTRY_LINKS_CSS_SELECTOR);
				Set<String> countryURLs = JsoupHelper.getElementsHrefAttributes(countryLinksElements);
				countryURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(LEVEL3_WEBSITE_URL, countryURLs);
				
				for (String countryURL : countryURLs) {
					
					if (countryURL.contains("local.level3.com/en")) {
						
						logger.info("Connecting to " + countryURL +"......");
						driver = new ChromeDriver();
						driver.get(countryURL);
						Thread.sleep(3000);
						
						html_content = driver.getPageSource();
						
						driver.quit();
						
						websiteDocument = Jsoup.parse(html_content);
				
						Elements northAmericanLinksElements = websiteDocument.select(LEVEL3_COUNTRY_LINKS_CSS_SELECTOR);
						Set<String> northAmericanURLs = JsoupHelper.getElementsHrefAttributes(northAmericanLinksElements);
						locationURLs.addAll(northAmericanURLs);
						
					} else {
						
						locationURLs.add(countryURL);
					
					}
				
				}
				
			}
			
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(LEVEL3_WEBSITE_URL, locationURLs);
			
			logger.info("Scraping all available company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationURLs, LEVEL3_OFFICE_LOCATIONS_CSS_SELECTOR);
			
			logger.info("Writing contact information to file......");
			String outputFileName = service.getOutputFileName(masterCompanyName);
			File excelFile = FileHelper.constructFile(outputFilePath, outputFileName);
			ExcelHelper.writeCompanyContactInfoToExcelFile(excelFile, companyContactInformationList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
}
