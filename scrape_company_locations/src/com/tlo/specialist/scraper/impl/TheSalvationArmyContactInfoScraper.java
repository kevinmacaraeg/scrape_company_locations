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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class TheSalvationArmyContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(TheSalvationArmyContactInfoScraper.class.getName());
	
	private static final String SALVATION_ARMY_CONTACT_URL = "http://www.salvationarmyusa.org/usn/contact";
	
	private static final String SALVATION_ARMY_SEARCH_TEXTBOX_CSS_SELECTOR = "div.form-search.form-zip.form-inline>input#postcode1";
	
	private static final String SALVATION_ARMY_SEARCH_BUTTON_CSS_SELECTOR = "div.form-search.form-zip.form-inline>input#postcode1-submit";
	
	private static final String SALVATION_ARMY_LOCATION_LINK_CSS_SELECTOR = "ul#gdos_results>li:nth-of-type(1)>a";
	
	private static final String SALVATION_ARMY_OFFICE_LOCATIONS_CSS_SELECTOR = "div.centerdetails";
	
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String state : Constants.US_STATES_MAP.keySet()) {
				
				logger.info("Connecting to " + SALVATION_ARMY_CONTACT_URL + "......");
				WebDriver driver = new ChromeDriver();
				driver.get(SALVATION_ARMY_CONTACT_URL);
				Thread.sleep(3000);
				
				logger.info("Searching for " + Constants.US_STATES_MAP.get(state) + " locations......");
				WebElement searchTextboxElement = driver.findElement(By.cssSelector(SALVATION_ARMY_SEARCH_TEXTBOX_CSS_SELECTOR));
				
				searchTextboxElement.clear();
				searchTextboxElement.sendKeys(state);
				
				WebElement searchButtonElement = driver.findElement(By.cssSelector(SALVATION_ARMY_SEARCH_BUTTON_CSS_SELECTOR));
				
				searchButtonElement.click();
				Thread.sleep(3000);
				
				try {
					WebElement locationLinkElement = driver.findElement(By.cssSelector(SALVATION_ARMY_LOCATION_LINK_CSS_SELECTOR));
					locationLinkElement.click();
					Thread.sleep(3000);
				} catch (NoSuchElementException e) {
					logger.info("No locations found for " + Constants.US_STATES_MAP.get(state));
					driver.quit();
					continue;
				}
				
				logger.info("Scraping available contact information......");
				String html_content = driver.getPageSource();
				String currentURL = driver.getCurrentUrl();
				
				driver.quit();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(SALVATION_ARMY_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
				
				logger.info("Parsing scraped contact information......");
				List<CompanyContactInformation> companyContactInformationListPerState = service.parseContactInformation(masterCompanyId, masterCompanyName, currentURL, fullCompanyContactInfoSet);
				
				for (CompanyContactInformation companyContactInformation : companyContactInformationListPerState) {
					String address = companyContactInformation.getAddress();
					companyContactInformation.setAddress(address + ", " + state + " United States");
				}
				
				companyContactInformationList.addAll(companyContactInformationListPerState);
				
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
