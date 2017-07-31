package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.ArrayList;
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

public class UbisoftContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(UbisoftContactInfoScraper.class.getName());
	
	private static final String UBISOFT_COMPANY_OVERVIEW_URL = "https://www.ubisoft.com/en-US/company/overview.aspx";
	
	private static final String UBISOFT_WEBSITE_URL = "https://www.ubisoft.com";
	
	private static final String UBISOFT_MAP_PINS_CSS_SELECTOR = "img.leaflet-marker-icon.leaflet-zoom-animated.leaflet-clickable";
	
	private static final String UBISOFT_LOCATION_LINKS_CSS_SELECTOR = "div.leaflet-popup-content>a";
	
	private static final String UBISOFT_OFFICE_LOCATIONS_CSS_SELECTOR = "div.game-info-content-wrap>aside.body-text.game-info-content>div.copy>p";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + UBISOFT_COMPANY_OVERVIEW_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(UBISOFT_COMPANY_OVERVIEW_URL);
			Thread.sleep(3000);
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			
			for (int second = 0;; second++) {
		    	if (second >= 5) {
		    		break;
		    	}
		    	js.executeScript("window.scrollBy(0, Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));", "");
		    	Thread.sleep(500);
		    }
			
			List<WebElement> mapPinsElement = driver.findElements(By.cssSelector(UBISOFT_MAP_PINS_CSS_SELECTOR));
			
			for (WebElement mapPinElement : mapPinsElement) {
				
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", mapPinElement);
				Thread.sleep(1000);
				
			}
			
			logger.info("Getting location URLs......");
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements locationLinksElements = websiteDocument.select(UBISOFT_LOCATION_LINKS_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> locationURLs = JsoupHelper.getElementsHrefAttributes(locationLinksElements);
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(UBISOFT_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String locationURL : locationURLs) {
				
				logger.info("Connecting to " + locationURL + "......");
				driver = new ChromeDriver();
				driver.get(locationURL);
				Thread.sleep(3000);
				
				js = (JavascriptExecutor) driver;
				
				for (int second = 0;; second++) {
			    	if (second >= 30) {
			    		break;
			    	}
			    	js.executeScript("window.scrollBy(0, Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));", "");
			    	Thread.sleep(500);
			    }
				
				logger.info("Scraping available company contact information......");
				html_content = driver.getPageSource();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(UBISOFT_OFFICE_LOCATIONS_CSS_SELECTOR);
				Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
				
				logger.info("Parsing scraped company contact information.......");
				List<CompanyContactInformation> companyContactInformationListPerLocation = service.parseContactInformation(masterCompanyId, masterCompanyName, locationURL, fullCompanyContactInfoSet);
				companyContactInformationList.addAll(companyContactInformationListPerLocation);
				
			}
			
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
