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
import org.openqa.selenium.support.ui.Select;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;
import com.tlo.specialist.util.SeleniumHelper;

public class QatarAirwaysContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(QatarAirwaysContactInfoScraper.class.getName());
	
	private static final String QATAR_AIRWAYS_WORLDWIDE_OFFICES_URL = "http://www.qatarairways.com/us/en/contact-us.page";
	
	private static final String QATAR_AIRWAYS_COUNTRIES_DROPDOWN_CSS_SELECTOR = "select#category";
	
	private static final String QATAR_AIRWAYS_OFFICES_DROPDOWN_CSS_SELECTOR = "select#office";
	
	private static final String QATAR_AIRWAYS_SHOW_DETAILS_BUTTON_CSS_SELECTOR = "a#showContactUsDetails";
	
	private static final String QATAR_AIRWAYS_OFFICE_LOCATIONS_CSS_SELECTOR = "div#destinations>div.desBorder:nth-of-type(1)";

	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + QATAR_AIRWAYS_WORLDWIDE_OFFICES_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(QATAR_AIRWAYS_WORLDWIDE_OFFICES_URL);
			Thread.sleep(3000);
			
			WebElement countriesDropdownElement = driver.findElement(By.cssSelector(QATAR_AIRWAYS_COUNTRIES_DROPDOWN_CSS_SELECTOR));
			
			List<String> countriesDropdownOptions = SeleniumHelper.getDropdownTextOptions(countriesDropdownElement);
			
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (String country : countriesDropdownOptions) {
				
				if (!"Select".equals(country)) {
					
					logger.info("Scraping company contact information in " + country + "......");
					
					countriesDropdownElement = driver.findElement(By.cssSelector(QATAR_AIRWAYS_COUNTRIES_DROPDOWN_CSS_SELECTOR));
					
					Select countriesDropdown = new Select(countriesDropdownElement);
					
					countriesDropdown.selectByVisibleText(country);
					Thread.sleep(1000);
					
					WebElement officesDropdownElement = driver.findElement(By.cssSelector(QATAR_AIRWAYS_OFFICES_DROPDOWN_CSS_SELECTOR));
					
					List<String> officeDropdownOptions = SeleniumHelper.getDropdownTextOptions(officesDropdownElement);
					
					for (String office : officeDropdownOptions) {
						
						if (!"Select".equals(office)) {
							
							officesDropdownElement = driver.findElement(By.cssSelector(QATAR_AIRWAYS_OFFICES_DROPDOWN_CSS_SELECTOR));
							
							Select officesDropdown = new Select(officesDropdownElement);
							
							officesDropdown.selectByVisibleText(office);
							Thread.sleep(1000);
							
							WebElement showDetailsButtonElement = driver.findElement(By.cssSelector(QATAR_AIRWAYS_SHOW_DETAILS_BUTTON_CSS_SELECTOR));
							
							JavascriptExecutor js = (JavascriptExecutor) driver;
							js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", showDetailsButtonElement);
							Thread.sleep(1000);
							
							String html_content = driver.getPageSource();
							
							Document websiteDocument = Jsoup.parse(html_content);
							
							Elements officeLocationsElements = websiteDocument.select(QATAR_AIRWAYS_OFFICE_LOCATIONS_CSS_SELECTOR);
							Set<String> fullCompanyContactInfoSetPerOffice = JsoupHelper.getElementsTextToSet(officeLocationsElements);
							fullCompanyContactInfoSet.addAll(fullCompanyContactInfoSetPerOffice);
							
						}
						
					}
					
				}
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, QATAR_AIRWAYS_WORLDWIDE_OFFICES_URL, fullCompanyContactInfoSet);
				
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
