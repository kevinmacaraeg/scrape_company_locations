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

public class ESRIContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(ESRIContactInfoScraper.class.getName());
	
	private static final String ESRI_OFFICES_WORLDWIDE_URL = "http://www.esri.com/about-esri/contact";
	
	private static final String ESRI_CONTINENT_LINKS_CSS_SELECTOR = "div#continent-toggle-list>span.location-toggler";
	
	private static final String ESRI_COUNTRY_LINKS_CSS_SELECTOR = "div.location-content-container.visible>div#country-list>ul.grid-33>li.country-entry>a.country-name";
	
	private static final String ESRI_OFFICE_LOCATIONS_CSS_SELECTOR = "div.offices-wrapper";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			logger.info("Connecting to " + ESRI_OFFICES_WORLDWIDE_URL +"......");
			WebDriver driver = new ChromeDriver();
			driver.get(ESRI_OFFICES_WORLDWIDE_URL);
			Thread.sleep(3000);
			
			List<WebElement> continentLinksElements = driver.findElements(By.cssSelector(ESRI_CONTINENT_LINKS_CSS_SELECTOR));
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (WebElement continentLinkElement : continentLinksElements) {
				
				logger.info("Clicking continent link......");
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", continentLinkElement);
				Thread.sleep(1000);
				
				List<WebElement> countryLinksElements = driver.findElements(By.cssSelector(ESRI_COUNTRY_LINKS_CSS_SELECTOR));
				
				for (WebElement countryLinkElement : countryLinksElements) {
					
					logger.info("Clicking country link......");
					js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", countryLinkElement);
					Thread.sleep(1000);
					
					logger.info("Scraping available company contact information......");
					String html_content = driver.getPageSource();
					
					Document websiteDocument = Jsoup.parse(html_content);
					
					Elements officeLocationsElements = websiteDocument.select(ESRI_OFFICE_LOCATIONS_CSS_SELECTOR);
					
					Set<String> fullCompanyContactInfoSetPerCountry = JsoupHelper.getElementsTextToSet(officeLocationsElements);
					fullCompanyContactInfoSet.addAll(fullCompanyContactInfoSetPerCountry);
					
				}
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList= service.parseContactInformation(masterCompanyId, masterCompanyName, ESRI_OFFICES_WORLDWIDE_URL, fullCompanyContactInfoSet);
			
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
