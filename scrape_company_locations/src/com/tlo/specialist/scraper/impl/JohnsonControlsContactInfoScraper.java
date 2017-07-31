package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.StringHelper;

public class JohnsonControlsContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(JohnsonControlsContactInfoScraper.class.getName());
	
	private static final String JOHNSON_CONTROLS_GLOBAL_LOCATIONS_URL1 = "http://www.johnsoncontrols.com/location-finder";
	
	private static final String JOHNSON_CONTROLS_GLOBAL_LOCATIONS_URL2 = "http://74.205.52.249/locator5/default.aspx";
	
	private static final String JOHNSON_CONTROLS_SEARCH_TEXTBOX_CSS_SELECTOR = "input#txtLocation";
	
	private static final String JOHNSON_CONTROLS_SEARCH_BUTTON_CSS_SELECTOR = "input#btnSearch";
	
	private static final String JOHNSON_CONTROLS_OFFICE_LOCATIONS_CSS_SELECTOR = "div.location";

	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + JOHNSON_CONTROLS_GLOBAL_LOCATIONS_URL2 + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(JOHNSON_CONTROLS_GLOBAL_LOCATIONS_URL2);
			Thread.sleep(3000);
			
			List<String> countryList = StringHelper.getISOCountriesList();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (String country : countryList) {
				
				WebElement searchTextboxElement = driver.findElement(By.cssSelector(JOHNSON_CONTROLS_SEARCH_TEXTBOX_CSS_SELECTOR));
				WebElement searchButtonElement = driver.findElement(By.cssSelector(JOHNSON_CONTROLS_SEARCH_BUTTON_CSS_SELECTOR));
				
				logger.info("Searching office locations for " + country + "......");
				searchTextboxElement.clear();
				searchTextboxElement.sendKeys(country);
				
				searchButtonElement.click();
				Thread.sleep(1000);
				
				logger.info("Scraping available contact information......");
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(JOHNSON_CONTROLS_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				for (Element officeLocationElement : officeLocationsElements) {
					fullCompanyContactInfoSet.add(officeLocationElement.text());
				}
				
			}
			
			driver.quit();
			
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, JOHNSON_CONTROLS_GLOBAL_LOCATIONS_URL1, fullCompanyContactInfoSet);
				
			logger.info("Writing company contacts to output file...");
			String outputFileName = service.getOutputFileName(masterCompanyName);
			File excelOutputFile = FileHelper.constructFile(outputFilePath, outputFileName);
			ExcelHelper.writeCompanyContactInfoToExcelFile(excelOutputFile, companyContactInformationList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
}
