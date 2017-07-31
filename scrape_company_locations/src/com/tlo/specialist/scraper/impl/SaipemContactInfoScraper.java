package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.ArrayList;
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

public class SaipemContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(SaipemContactInfoScraper.class.getName());
	
	private static final String SAIPEM_IN_THE_WORLD_URL = "http://www.saipem.com/sites/SAIPEM_en_IT/siti-esteri/saipem-mondo-homepage.page";
	
	private static final String SAIPEM_WEBSITE_URL = "http://www.saipem.com";
	
	private static final String SAIPEM_MORE_BUTTON_CSS_SELECTOR = "a.cta.cta-2.js-hide-map-overlay";
	
	private static final String SAIPEM_COUNTRY_LIST_CSS_SELECTOR = "ul#country-list>li>a";
	
	private static final String SAIPEM_COUNTRY_LINK_CSS_SELECTOR = "div.gm-style-iw>div>div>div#content>div.map-info-window>div.content>a";
	
	private static final String SAIPEM_LOCATIONS_BUTTON_CSS_SELECTOR = "ul.section-menu-list>li:last-of-type>a";
	
	private static final String SAIPEM_OFFICE_LOCATIONS_CSS_SELECTOR = "div.locations-content>div.row>div.columns>ul>li>div.wrap";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + SAIPEM_IN_THE_WORLD_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(SAIPEM_IN_THE_WORLD_URL);
			Thread.sleep(3000);
			
			driver.get(SAIPEM_IN_THE_WORLD_URL);
			Thread.sleep(3000);
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			
			WebElement moreButtonElement = driver.findElement(By.cssSelector(SAIPEM_MORE_BUTTON_CSS_SELECTOR));
			js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", moreButtonElement);
			Thread.sleep(5000);
			
			logger.info("Getting location URLs......");
			List<WebElement> countryListElements = driver.findElements(By.cssSelector(SAIPEM_COUNTRY_LIST_CSS_SELECTOR));
			
			Set<String> locationURLs = new HashSet<String>();
			for (WebElement countryElement : countryListElements) {
				
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", countryElement);
				Thread.sleep(1000);
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements countryLinksElements = websiteDocument.select(SAIPEM_COUNTRY_LINK_CSS_SELECTOR);
				
				Set<String> locationURLPerCountry = JsoupHelper.getElementsHrefAttributes(countryLinksElements);
				locationURLs.addAll(locationURLPerCountry);
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(SAIPEM_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String locationURL : locationURLs) {
				
				logger.info("Connecting to " + locationURL + "......");
				WebDriver driver2 = new ChromeDriver();
				driver2.get(locationURL);
				Thread.sleep(3000);
				
				WebElement locationLinkElement = driver2.findElement(By.cssSelector(SAIPEM_LOCATIONS_BUTTON_CSS_SELECTOR));
				
				js = (JavascriptExecutor) driver2;
						
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", locationLinkElement);
				Thread.sleep(1000);
				
				logger.info("Scraping available company contact information......");
				
				String html_content = driver2.getPageSource();
				String currentURL = driver2.getCurrentUrl();
				
				driver2.quit();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(SAIPEM_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
				
				logger.info("Parsing scraped contact information......");
				List<CompanyContactInformation> companyContactInformationListPerCountry = service.parseContactInformation(masterCompanyId, masterCompanyName, currentURL, fullCompanyContactInfoSet);
				companyContactInformationList.addAll(companyContactInformationListPerCountry);
				
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
