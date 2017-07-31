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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.JsoupHelper;
import com.tlo.specialist.util.SeleniumHelper;
import com.tlo.specialist.util.StringHelper;

public class MultiPagedOneDropdownContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(MultiPagedOneDropdownContactInfoScraper.class.getName());
	
	private static String centralLocationsURL;
	
	private static String dropdownCssSelector;
	
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
			
			List<String> dropdownOptions = SeleniumHelper.getDropdownTextOptions(dropdownElement);
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String option : dropdownOptions) {
				logger.info("Scraping company contact information for " + option + "......");
				
				logger.info("Connecting to " + centralLocationsURL + "......");
				driver = new ChromeDriver();
				driver.get(centralLocationsURL);
				Thread.sleep(3000);
				
				dropdownElement = driver.findElement(By.cssSelector(dropdownCssSelector));
				
				Select countriesDropdown = new Select(dropdownElement);
				
				countriesDropdown.selectByVisibleText(option);
				
				Thread.sleep(5000);
				
				String html_content = driver.getPageSource();
				String currentURL = driver.getCurrentUrl();
				
				driver.quit();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(officeLocationsCssSelector);
				
				logger.info("Parsing each company contact information retrieved......");
				Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
				List<CompanyContactInformation> companyContactInformationListPerDropdownOption = service.parseContactInformation(masterCompanyId, masterCompanyName, currentURL, fullCompanyContactInfoSet);
				companyContactInformationList.addAll(companyContactInformationListPerDropdownOption);
				
			}
			
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
			URL url = Thread.currentThread().getContextClassLoader().getResource("properties/multiPagedOneDropdownProperties.properties");
			systemProperties.load(url.openStream());
			
			centralLocationsURL = systemProperties.getProperty(masterCompanyId + ".central.locations.url").trim();
			if (StringHelper.isEmpty(centralLocationsURL)) {
				throw new Exception(masterCompanyId + ".central.locations.url" + " property is missing. Please update properties file!");
			}
			
			dropdownCssSelector = systemProperties.getProperty(masterCompanyId + ".dropdown.css.selector").trim();
			if (StringHelper.isEmpty(dropdownCssSelector)) {
				throw new Exception(masterCompanyId + ".dropdown.css.selector" + " property is missing. Please update properties file!");
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
