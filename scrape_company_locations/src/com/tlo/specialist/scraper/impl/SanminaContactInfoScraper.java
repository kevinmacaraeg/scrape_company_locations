package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class SanminaContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(SanminaContactInfoScraper.class.getName());
	
	private static final String SANMINA_LOCATIONS_URL = "http://www.sanmina.com/locations/";
	
	private static final String SANMINA_WEBSITE_URL = "http://www.sanmina.com";
	
	private static final String SANMINA_WORLD_MAP_AREAS_CSS_SELECTOR = "map#map>area";
	
	private static final String SANMINA_LOCATION_LINKS_CSS_SELECTOR = "a.locationslink";
	
	private static final String SANIMINA_OFFICE_LOCATIONS_CSS_SELECTOR = "div.entry-content";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + SANMINA_LOCATIONS_URL +"......");
			WebDriver driver = new ChromeDriver();
			driver.get(SANMINA_LOCATIONS_URL);
			Thread.sleep(3000);
			
			logger.info("Getting location URLs......");
			
			List<WebElement> worldMapAreasElements = driver.findElements(By.cssSelector(SANMINA_WORLD_MAP_AREAS_CSS_SELECTOR));
			
			for (WebElement worldMapAreaElement : worldMapAreasElements) {
				
				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", worldMapAreaElement);
				Thread.sleep(1000);
			}
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Elements locationLinksElements = websiteDocument.select(SANMINA_LOCATION_LINKS_CSS_SELECTOR);
			Set<String> locationURLs = JsoupHelper.getElementsHrefAttributes(locationLinksElements);
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(SANMINA_WEBSITE_URL, locationURLs);
				
			logger.info("Scraping all available company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationURLs, SANIMINA_OFFICE_LOCATIONS_CSS_SELECTOR);
			
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
