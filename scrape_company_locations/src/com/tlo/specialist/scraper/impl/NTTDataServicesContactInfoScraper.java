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
import org.openqa.selenium.support.ui.Select;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.SeleniumHelper;

public class NTTDataServicesContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(NTTDataServicesContactInfoScraper.class.getName());
	
	private static final String NTT_DATA_SERVICES_LOCATIONS_URL = "https://us.nttdata.com/en/location";
	
	private static final String NTT_DATA_SERVICES_LIST_VIEW_TAB_LINK_CSS_SELECTOR = "a#map-view";
	
	private static final String NTT_DATA_SERVICES_COUNTRIES_DROPDOWN_CSS_SELECTOR = "select#country-picker";
	
	private static final String NTT_DATA_SERVICES_REGIONS_DROPDOWN_CSS_SELECTOR = "select#region-picker";
	
	private static final String NTT_DATA_SERVICES_OFFICE_LOCATIONS_CSS_SELECTOR = "div.location-details.contact-details-address";

	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + NTT_DATA_SERVICES_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(NTT_DATA_SERVICES_LOCATIONS_URL);
			Thread.sleep(3000);
			
			WebElement listViewTabLinkElement = driver.findElement(By.cssSelector(NTT_DATA_SERVICES_LIST_VIEW_TAB_LINK_CSS_SELECTOR));
			
			listViewTabLinkElement.click();
			Thread.sleep(1000);
			
			WebElement countriesDropdownElement = driver.findElement(By.cssSelector(NTT_DATA_SERVICES_COUNTRIES_DROPDOWN_CSS_SELECTOR));
			
			List<String> countriesDropdownOptions = SeleniumHelper.getDropdownTextOptions(countriesDropdownElement);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (String country : countriesDropdownOptions) {
				
				logger.info("Scraping available company contact information for " + country + " ......");
				
				countriesDropdownElement = driver.findElement(By.cssSelector(NTT_DATA_SERVICES_COUNTRIES_DROPDOWN_CSS_SELECTOR));
				
				Select countriesDropdown = new Select(countriesDropdownElement);
				
				countriesDropdown.selectByVisibleText(country);
				Thread.sleep(1000);
				
				WebElement regionsDropdownElement = driver.findElement(By.cssSelector(NTT_DATA_SERVICES_REGIONS_DROPDOWN_CSS_SELECTOR));
				
				List<String> regionsDropdownOptions = SeleniumHelper.getDropdownTextOptions(regionsDropdownElement);
				
				for (String region : regionsDropdownOptions) {
					
					logger.info("Scraping available company contact information for " + region + " ......");
					
					regionsDropdownElement = driver.findElement(By.cssSelector(NTT_DATA_SERVICES_REGIONS_DROPDOWN_CSS_SELECTOR));
					
					Select regionsDropdown = new Select(regionsDropdownElement);
					
					regionsDropdown.selectByVisibleText(region);
					Thread.sleep(1000);
				
					String html_content = driver.getPageSource();
					
					Document websiteDocument = Jsoup.parse(html_content);
					
					Elements officeLocationsElements = websiteDocument.select(NTT_DATA_SERVICES_OFFICE_LOCATIONS_CSS_SELECTOR);
					
					for (Element officeLocationElement : officeLocationsElements) {
						fullCompanyContactInfoSet.add(officeLocationElement.text());
					}
				
				}
			}
			
			driver.quit();
			
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, driver.getCurrentUrl(), fullCompanyContactInfoSet);
				
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
