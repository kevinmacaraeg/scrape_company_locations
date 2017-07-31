package com.tlo.specialist.scraper.impl;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
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
import com.tlo.specialist.util.JsoupHelper;
import com.tlo.specialist.util.StringHelper;

public class OnePagedClickDropdownOptionsContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(OnePagedClickDropdownOptionsContactInfoScraper.class.getName());
	
	private static String centralLocationsURL;
	
	private static String dropdownCssSelector;
	
	private static String dropdownOptionsCssSelector;
	
	private static String officeLocationsCssSelector;

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			init(masterCompanyId);
			
			logger.info("Connecting to " + centralLocationsURL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(centralLocationsURL);
			Thread.sleep(3000);
			
			WebElement dropdownElement = driver.findElement(By.cssSelector(dropdownCssSelector));
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			
			js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", dropdownElement);
			Thread.sleep(3000);
			
			List<WebElement> dropdownOptionsElements = driver.findElements(By.cssSelector(dropdownOptionsCssSelector));
			
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (WebElement dropdownOptionElement : dropdownOptionsElements) {
				
				logger.info("Scraping company contact information for " + dropdownOptionElement.getText() + "......");
				
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", dropdownOptionElement);
				Thread.sleep(3000);
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(officeLocationsCssSelector);
				
				Set<String> fullCompanyContactInfoSetPerOption = JsoupHelper.getElementsTextToSet(officeLocationsElements);
				fullCompanyContactInfoSet.addAll(fullCompanyContactInfoSetPerOption);
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing each company contact information retrieved......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, centralLocationsURL, fullCompanyContactInfoSet);
			
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
			URL url = Thread.currentThread().getContextClassLoader().getResource("properties/onePagedClickDropdownOptionsProperties.properties");
			systemProperties.load(url.openStream());
			
			centralLocationsURL = systemProperties.getProperty(masterCompanyId + ".central.locations.url").trim();
			if (StringHelper.isEmpty(centralLocationsURL)) {
				throw new Exception(masterCompanyId + ".central.locations.url" + " property is missing. Please update properties file!");
			}
			
			dropdownCssSelector = systemProperties.getProperty(masterCompanyId + ".dropdown.css.selector").trim();
			if (StringHelper.isEmpty(dropdownCssSelector)) {
				throw new Exception(masterCompanyId + ".dropdown.css.selector" + " property is missing. Please update properties file!");
			}
			
			dropdownOptionsCssSelector = systemProperties.getProperty(masterCompanyId + ".dropdown.options.css.selector").trim();
			if (StringHelper.isEmpty(dropdownOptionsCssSelector)) {
				throw new Exception(masterCompanyId + ".dropdown.options.css.selector" + " property is missing. Please update properties file!");
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
