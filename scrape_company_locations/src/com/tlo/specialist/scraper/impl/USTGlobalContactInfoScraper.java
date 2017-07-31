package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;

public class USTGlobalContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(USTGlobalContactInfoScraper.class.getName());
	
	private static final String UST_GLOBAL_LOCATIONS_URL = "http://www.ust-global.com/contact-us";
	
	private static final String UST_GLOBAL_OUR_LOCATIONS_TAB_LINK_CSS_SELECTOR = "div.ourlocationlink.field-type-text";
	
	private static final String UST_GLOBAL_MAP_PINS_CSS_SELECTOR = "div#map_wrapper>div#map_canvas>div.jvectormap-container>svg>g>circle.jvectormap-marker.jvectormap-element";
	
	private static final String UST_GLOBAL_OFFICE_LOCATIONS_CSS_SELECTOR = "div#customTip>span.address";

	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + UST_GLOBAL_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(UST_GLOBAL_LOCATIONS_URL);
			Thread.sleep(3000);
			
			WebElement ourLocationsTabLinkElement = driver.findElement(By.cssSelector(UST_GLOBAL_OUR_LOCATIONS_TAB_LINK_CSS_SELECTOR));
			
			ourLocationsTabLinkElement.click();
			Thread.sleep(1000);
			
			List<WebElement> mapPinsElements = driver.findElements(By.cssSelector(UST_GLOBAL_MAP_PINS_CSS_SELECTOR));
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Actions action = new Actions(driver);
			
			logger.info("Scraping available company contact information......");
			
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (WebElement mapPinElement : mapPinsElements) {
				
				action.moveToElement(mapPinElement).click().perform();
				Thread.sleep(3000);
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(UST_GLOBAL_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				for (Element officeLocationElement : officeLocationsElements) {
					fullCompanyContactInfoSet.add(officeLocationElement.text());
				}
				
			}
			
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, driver.getCurrentUrl(), fullCompanyContactInfoSet);
			
			driver.quit();
				
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
