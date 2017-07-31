package com.tlo.specialist.scraper.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.JsoupHelper;
import com.tlo.specialist.util.StringHelper;

public class MultiPagedContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(MultiPagedContactInfoScraper.class.getName());
	
	private static String locationsFirstPageURL;
	
	private static String nextButtonCssSelector;
	
	private static String officeLocationsCssSelector;

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			init(masterCompanyId);
			
			logger.info("Connecting to " + locationsFirstPageURL +"......");
			WebDriver driver = new ChromeDriver();
			driver.get(locationsFirstPageURL);
			Thread.sleep(3000);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
				
			logger.info("Scraping available contact information......");
			boolean hasNextPage = true;
			int pageNumber = 1;
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			do {
				logger.info("Scraping contact information from Page " + pageNumber + "......");
				WebElement nextPageButtonElement = null;
				try {
					nextPageButtonElement = driver.findElement(By.cssSelector(nextButtonCssSelector));
				} catch (NoSuchElementException e) {
					hasNextPage = false;
				}
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(officeLocationsCssSelector);
				
				Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
				
				List<CompanyContactInformation> companyContactInformationListPerPage = service.parseContactInformation(masterCompanyId, masterCompanyName, driver.getCurrentUrl(), fullCompanyContactInfoSet);
				companyContactInformationList.addAll(companyContactInformationListPerPage);
				
				Elements nextPageButtonJsoupElement = websiteDocument.select(nextButtonCssSelector);
				
				if (hasNextPage) {
					if (nextPageButtonElement.isDisplayed() && nextPageButtonElement.isEnabled() && !nextPageButtonJsoupElement.hasClass("disabled")) {
						js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", nextPageButtonElement);
						Thread.sleep(5000);
						pageNumber++;
					} else {
						hasNextPage = false;
					}
				}
			} while (hasNextPage);
				
			driver.quit();
			
			logger.info("Writing contact information to file......");
			service.writeCompanyContactInformationToFile(masterCompanyName, outputFilePath, companyContactInformationList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	void init(String masterCompanyId) throws Exception {
		try {
			
			Properties systemProperties = new Properties();
			URL url = Thread.currentThread().getContextClassLoader().getResource("properties/multiPagedProperties.properties");
			systemProperties.load(url.openStream());
			
			locationsFirstPageURL = systemProperties.getProperty(masterCompanyId + ".locations.first.page.url").trim();
			if (StringHelper.isEmpty(locationsFirstPageURL)) {
				throw new Exception(masterCompanyId + ".locations.first.page.url" + " property is missing. Please update properties file!");
			}
			
			nextButtonCssSelector = systemProperties.getProperty(masterCompanyId + ".next.button.css.selector").trim();
			if (StringHelper.isEmpty(nextButtonCssSelector)) {
				throw new Exception(masterCompanyId + ".next.button.css.selector" + " property is missing. Please update properties file!");
			}
			
			officeLocationsCssSelector = systemProperties.getProperty(masterCompanyId + ".office.locations.css.selector").trim();
			if (StringHelper.isEmpty(officeLocationsCssSelector)) {
				throw new Exception(masterCompanyId + ".office.locations.css.selector" + " property is missing. Please update properties file!");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
}
