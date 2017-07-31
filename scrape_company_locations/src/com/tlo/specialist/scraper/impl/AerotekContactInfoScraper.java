package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.HashSet;
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

public class AerotekContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(AerotekContactInfoScraper.class.getName());
	
	private static final String[] AEROTEK_GLOBAL_LOCATIONS_URLS = {"https://www.aerotek.com/en-au/locations?loc=all", "https://www.aerotek.com/en-au/locations/united-states", "https://www.aerotek.com/en-au/locations/canada"};
	
	private static final String AEROTEK_WEBSITE_URL = "https://www.aerotek.com";
	
	private static final String AEROTEK_MAP_PINS_CSS_SELECTOR = "a.MapPushpinBase";
	
	private static final String AEROTEK_COUNTRY_LINK_CSS_SELECTOR = "a.pin-anchor";
	
	private static final String AEROTEK_OFFICE_LOCATIONS_CSS_SELECTOR = "div.office-location-details";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			logger.info("Getting location URLs......");
			Set<String> locationURLs = new HashSet<String>();
			for (String aerotekLocationsURL : AEROTEK_GLOBAL_LOCATIONS_URLS) {
				
				logger.info("Connecting to " + aerotekLocationsURL + "......");
				WebDriver driver = new ChromeDriver();
				driver.get(aerotekLocationsURL);
				Thread.sleep(3000);
				
				List<WebElement> mapPinsElements = driver.findElements(By.cssSelector(AEROTEK_MAP_PINS_CSS_SELECTOR));
				
				JavascriptExecutor js = (JavascriptExecutor) driver;
				
				for (WebElement mapPinElement : mapPinsElements) {
					
					js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", mapPinElement);
					Thread.sleep(3000);
					
					String html_content = driver.getPageSource();
					
					Document websiteDocument = Jsoup.parse(html_content);
					
					Elements countryLinkElements = websiteDocument.select(AEROTEK_COUNTRY_LINK_CSS_SELECTOR);
					
					Set<String> locationURLsPerCountry = JsoupHelper.getElementsHrefAttributes(countryLinkElements);
					locationURLs.addAll(locationURLsPerCountry);
				
				}
				
				driver.quit();
			
			}
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(AEROTEK_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationURLs, AEROTEK_OFFICE_LOCATIONS_CSS_SELECTOR);
			
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
