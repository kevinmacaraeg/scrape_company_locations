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
import org.openqa.selenium.JavascriptExecutor;
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

public class IHSContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(IHSContactInfoScraper.class.getName());
	
	private static final String IHS_LOCATIONS_URL = "https://www.ihs.com/about/locations.html";
	
	private static final String IHS_COUNTRIES_DROPDOWN_CSS_SELECTOR = "select#country";
	
	private static final String IHS_CANADA_STATES_DROPDOWN_CSS_SELECTOR = "select#ca-province";
	
	private static final String IHS_US_STATES_DROPDOWN_CSS_SELECTOR = "select#us-province";
	
	private static final String IHS_GO_BUTTON_CSS_SELECTOR = "button.primary-button.btn-location-search";
	
	private static final String IHS_OFFICE_LOCATIONS_CSS_SELECTOR = "div.location-result";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + IHS_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(IHS_LOCATIONS_URL);
			Thread.sleep(3000);
			
			WebElement countriesDropdownElement = driver.findElement(By.cssSelector(IHS_COUNTRIES_DROPDOWN_CSS_SELECTOR));
			
			List<String> countriesDropdownOptions = SeleniumHelper.getDropdownTextOptions(countriesDropdownElement);
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String country : countriesDropdownOptions) {
				
				if (!" - Select One - ".equals(country)) {
					
					logger.info("Scraping company contact information for " + country + "......");
					driver = new ChromeDriver();
					driver.get(IHS_LOCATIONS_URL);
					Thread.sleep(3000);
					
					JavascriptExecutor js = (JavascriptExecutor) driver;
					
					countriesDropdownElement = driver.findElement(By.cssSelector(IHS_COUNTRIES_DROPDOWN_CSS_SELECTOR));
					
					Select countriesDropdown = new Select(countriesDropdownElement);
					
					countriesDropdown.selectByVisibleText(country);
					
					WebElement statesDropdownElement = null;
					if ("Canada".equals(country)) {
						statesDropdownElement = driver.findElement(By.cssSelector(IHS_CANADA_STATES_DROPDOWN_CSS_SELECTOR));
					} else if ("United States of America (USA)".equals(country)) {
						statesDropdownElement = driver.findElement(By.cssSelector(IHS_US_STATES_DROPDOWN_CSS_SELECTOR));
					}
					
					if (statesDropdownElement != null && statesDropdownElement.isDisplayed() && statesDropdownElement.isEnabled()) {
						
						List<String> statesDropdownOptions = SeleniumHelper.getDropdownTextOptions(statesDropdownElement);
						
						for (String state : statesDropdownOptions) {
							
							if (!" - Select One - ".equals(state)) {
							
								logger.info("Scraping company contact information for " + state + "......");
								
								if ("Canada".equals(country)) {
									statesDropdownElement = driver.findElement(By.cssSelector(IHS_CANADA_STATES_DROPDOWN_CSS_SELECTOR));
								} else if ("United States of America (USA)".equals(country)) {
									statesDropdownElement = driver.findElement(By.cssSelector(IHS_US_STATES_DROPDOWN_CSS_SELECTOR));
								}
								
								Select statesDropdown = new Select(statesDropdownElement);
								
								statesDropdown.selectByVisibleText(state);
						
								WebElement goButtonElement = driver.findElement(By.cssSelector(IHS_GO_BUTTON_CSS_SELECTOR));
								
								js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", goButtonElement);
								Thread.sleep(1000);
								
								String html_content = driver.getPageSource();
								String currentURL = driver.getCurrentUrl();
								
								Document websiteDocument = Jsoup.parse(html_content);
								
								Elements officeLocationsElements = websiteDocument.select(IHS_OFFICE_LOCATIONS_CSS_SELECTOR);
								
								Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
								
								List<CompanyContactInformation> companyContactInformationListPerCountry = service.parseContactInformation(masterCompanyId, masterCompanyName, currentURL, fullCompanyContactInfoSet);
								companyContactInformationList.addAll(companyContactInformationListPerCountry);
							
							}
						
						}
						
						driver.quit();
					
					} else {
						
						WebElement goButtonElement = driver.findElement(By.cssSelector(IHS_GO_BUTTON_CSS_SELECTOR));
						
						js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", goButtonElement);
						Thread.sleep(1000);
						
						String html_content = driver.getPageSource();
						String currentURL = driver.getCurrentUrl();
						
						driver.quit();
						
						Document websiteDocument = Jsoup.parse(html_content);
						
						Elements officeLocationsElements = websiteDocument.select(IHS_OFFICE_LOCATIONS_CSS_SELECTOR);
						
						Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
						
						List<CompanyContactInformation> companyContactInformationListPerCountry = service.parseContactInformation(masterCompanyId, masterCompanyName, currentURL, fullCompanyContactInfoSet);
						companyContactInformationList.addAll(companyContactInformationListPerCountry);
						
					}
				
				}
				
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
