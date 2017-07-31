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

public class OnePagedTwoDropdownsContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(OnePagedTwoDropdownsContactInfoScraper.class.getName());
	
	private static String centralLocationsURL;

	private static String dropdown1CssSelector;
	
	private static String dropdown2CssSelector;
	
	private static String officeLocationsCssSelector;

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			init(masterCompanyId);
			
			WebDriver driver = new ChromeDriver();
			driver.get(centralLocationsURL);
			Thread.sleep(3000);
			
			WebElement dropdown1Element = driver.findElement(By.cssSelector(dropdown1CssSelector));
			
			List<String> dropdown1Options = SeleniumHelper.getDropdownTextOptions(dropdown1Element);
			
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (String option1 : dropdown1Options) {
					
				logger.info("Scraping office locations from " + option1 + "......");
			
				dropdown1Element = driver.findElement(By.cssSelector(dropdown1CssSelector));
			
				Select dropdown1 = new Select(dropdown1Element);
				
				dropdown1.selectByVisibleText(option1);
				Thread.sleep(3000);
				
				WebElement dropdown2Element = driver.findElement(By.cssSelector(dropdown2CssSelector));
				
				List<String> dropdown2Options = SeleniumHelper.getDropdownTextOptions(dropdown2Element);
				
				for (String option2 : dropdown2Options) {
					
					logger.info("Scraping office locations from " + option2 + "......");
					
					dropdown2Element = driver.findElement(By.cssSelector(dropdown2CssSelector));
				
					Select dropdown2 = new Select(dropdown2Element);
					
					dropdown2.selectByVisibleText(option2);
					Thread.sleep(3000);
					
					String html_content = driver.getPageSource();
					
					Document websiteDocument = Jsoup.parse(html_content);
					
					Elements officeLocationsElements = websiteDocument.select(officeLocationsCssSelector);
					
					Set<String> fullCompanyContactInfoSetPerOption = JsoupHelper.getElementsTextToSet(officeLocationsElements);
					fullCompanyContactInfoSet.addAll(fullCompanyContactInfoSetPerOption);
						
				}
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing scraped company contact information......");
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
			URL url = Thread.currentThread().getContextClassLoader().getResource("properties/onePagedTwoDropdownsProperties.properties");
			systemProperties.load(url.openStream());
			
			centralLocationsURL = systemProperties.getProperty(masterCompanyId + ".central.locations.url").trim();
			if (StringHelper.isEmpty(centralLocationsURL)) {
				throw new Exception(masterCompanyId + ".central.locations.url" + " property is missing. Please update properties file!");
			}
			
			dropdown1CssSelector = systemProperties.getProperty(masterCompanyId + ".dropdown1.css.selector").trim();
			if (StringHelper.isEmpty(dropdown1CssSelector)) {
				throw new Exception(masterCompanyId + ".dropdown1.css.selector" + " property is missing. Please update properties file!");
			}
			
			dropdown2CssSelector = systemProperties.getProperty(masterCompanyId + ".dropdown2.css.selector").trim();
			if (StringHelper.isEmpty(dropdown2CssSelector)) {
				throw new Exception(masterCompanyId + ".dropdown2.css.selector" + " property is missing. Please update properties file!");
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
