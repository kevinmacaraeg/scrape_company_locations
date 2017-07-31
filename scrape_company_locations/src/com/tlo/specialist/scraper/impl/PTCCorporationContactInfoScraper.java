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

public class PTCCorporationContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(PTCCorporationContactInfoScraper.class.getName());
	
	private static final String PTC_OFFICE_LOCATIONS_URL = "https://www.ptc.com/en/office-locations-map";
	
	private static final String PTC_VIEW_LOCATIONS_LINK_CSS_SELECTOR = "a#profile-tab";
	
	private static final String PTC_COUNTRY_LINKS_CSS_SELECTOR = "ul#regionTabs>li>a";
	
	private static final String PTC_OFFICE_LOCATIONS_CSS_SELECTOR = "div.location>div.eventDescription>p";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + PTC_OFFICE_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(PTC_OFFICE_LOCATIONS_URL);
			Thread.sleep(3000);
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			
			WebElement viewLocationsLinkElement = driver.findElement(By.cssSelector(PTC_VIEW_LOCATIONS_LINK_CSS_SELECTOR));
			
			js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", viewLocationsLinkElement);
			Thread.sleep(1000);
		
			List<WebElement> countryLinksElements = driver.findElements(By.cssSelector(PTC_COUNTRY_LINKS_CSS_SELECTOR));
			
			for (WebElement countryLinkElement : countryLinksElements) {
				
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", countryLinkElement);
				Thread.sleep(1000);
				
			}
			
			logger.info("Scraping available company contact information......");
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements officeLocationsElements = websiteDocument.select(PTC_OFFICE_LOCATIONS_CSS_SELECTOR);
			
			Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing scraped company contact information.......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, PTC_OFFICE_LOCATIONS_URL, fullCompanyContactInfoSet);
			
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
