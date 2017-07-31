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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;
import com.tlo.specialist.util.SeleniumHelper;

public class HitachiDataSystemsContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(HitachiDataSystemsContactInfoScraper.class.getName());
	
	private static final String HDS_WORLDWIDE_LOCATIONS_URL = "https://www.hds.com/en-us/contact/worldwide-locations.html";
	
	private static final String HDS_REGIONS_DROPDOWN_CSS_SELECTOR = "select#allRegion";
	
	private static final String HDS_COUNTRIES_DROPDOWN_CSS_SELECTOR = "select#allCountries";
	
	private static final String HDS_CITIES_DROPDOWN_CSS_SELECTOR = "select#allLocations";
	
	private static final String HDS_OFFICE_LOCATIONS_CSS_SELECTOR = "div#locationDetailsContent>div.side-block";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + HDS_WORLDWIDE_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(HDS_WORLDWIDE_LOCATIONS_URL);
			Thread.sleep(3000);
			
			WebElement regionsDropdownElement = driver.findElement(By.cssSelector(HDS_REGIONS_DROPDOWN_CSS_SELECTOR));
			
			List<String> regionsDropdownOptions = SeleniumHelper.getDropdownTextOptions(regionsDropdownElement);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String region : regionsDropdownOptions) {
				
				if (!"-Select Region-".equals(region)) {
					
					logger.info("Scraping contact information for " + region + "......");
					
					regionsDropdownElement = driver.findElement(By.cssSelector(HDS_REGIONS_DROPDOWN_CSS_SELECTOR));
					
					Select regionsDropdown = new Select(regionsDropdownElement);
					
					regionsDropdown.selectByVisibleText(region);
					Thread.sleep(1000);
			
					WebElement countriesDropdownElement = driver.findElement(By.cssSelector(HDS_COUNTRIES_DROPDOWN_CSS_SELECTOR));
					
					List<String> countriesDropdownOptions = SeleniumHelper.getDropdownTextOptions(countriesDropdownElement);
					
					for (String country : countriesDropdownOptions) {
						
						if (!"--Select Country--".equals(country)) {
							
							logger.info("Scraping contact information for " + country + "......");
							
							countriesDropdownElement = driver.findElement(By.cssSelector(HDS_COUNTRIES_DROPDOWN_CSS_SELECTOR));
							
							Select countriesDropdown = new Select(countriesDropdownElement);
							
							countriesDropdown.selectByVisibleText(country);
							Thread.sleep(1000);
							
							WebElement citiesDropdownElement = driver.findElement(By.cssSelector(HDS_CITIES_DROPDOWN_CSS_SELECTOR));
							
							if (citiesDropdownElement.isDisplayed() && citiesDropdownElement.isEnabled()) {
							
								List<String> citiesDropdownOptions = SeleniumHelper.getDropdownTextOptions(citiesDropdownElement);
								
								for (String city : citiesDropdownOptions) {
									
									if (!"--Select Location--".equalsIgnoreCase(city)) {
										
										logger.info("Scraping contact information for " + city + "......");
									
										citiesDropdownElement = driver.findElement(By.cssSelector(HDS_CITIES_DROPDOWN_CSS_SELECTOR));
										
										Select citiesDropdown = new Select(citiesDropdownElement);
										
										citiesDropdown.selectByVisibleText(city);
										Thread.sleep(1000);
										
										String html_content = driver.getPageSource();
										
										Document websiteDocument = Jsoup.parse(html_content);
										
										Elements officeLocationsElements = websiteDocument.select(HDS_OFFICE_LOCATIONS_CSS_SELECTOR);
										
										Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
										
										logger.info("Parsing scraped contact information......");
										List<CompanyContactInformation> companyContactInformationListPerCountryCity = service.parseContactInformation(masterCompanyId, masterCompanyName, driver.getCurrentUrl(), fullCompanyContactInfoSet);
										companyContactInformationList.addAll(companyContactInformationListPerCountryCity);
										
									}
									
								}
			
							} else {
								
								String html_content = driver.getPageSource();
								
								Document websiteDocument = Jsoup.parse(html_content);
								
								Elements officeLocationsElements = websiteDocument.select(HDS_OFFICE_LOCATIONS_CSS_SELECTOR);
								
								Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
								
								logger.info("Parsing scraped contact information......");
								List<CompanyContactInformation> companyContactInformationListPerCountryCity = service.parseContactInformation(masterCompanyId, masterCompanyName, driver.getCurrentUrl(), fullCompanyContactInfoSet);
								companyContactInformationList.addAll(companyContactInformationListPerCountryCity);
								
							}
						
						}
						
						
					}
				
				}
			
			}
			
			driver.quit();
			
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
