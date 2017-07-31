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

public class NationalOilwellVarcoContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(NationalOilwellVarcoContactInfoScraper.class.getName());
	
	private static final String NOV_LOCATIONS_URL = "http://www.nov.com/contactus/Locations.aspx";
	
	private static final String NOV_SHOW_ADVANCED_SEARCH_LINK_CSS_SELECTOR = "div#advSearch>a";
	
	private static final String NOV_COUNTRIES_DROPDOWN_CSS_SELECTOR = "select#ctl00_ContentPlaceHolder1_ddlcountries";
	
	private static final String NOV_OFFICE_LOCATIONS_CSS_SELECTOR = "table.GridMargin.data-table.patents-list>tbody>tr";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + NOV_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(NOV_LOCATIONS_URL);
			Thread.sleep(3000);
			
			WebElement showAdvancedSearchLinkElement = driver.findElement(By.cssSelector(NOV_SHOW_ADVANCED_SEARCH_LINK_CSS_SELECTOR));
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", showAdvancedSearchLinkElement);
			
			WebElement countriesDropdownElement = driver.findElement(By.cssSelector(NOV_COUNTRIES_DROPDOWN_CSS_SELECTOR));
			
			List<String> countriesDropdownOptions = SeleniumHelper.getDropdownTextOptions(countriesDropdownElement);
			
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (String country : countriesDropdownOptions) {
				
				if (!"–Filter by Country–".equals(country)) {
					
					logger.info("Scraping available company contact information for " + country + "......");
					
					countriesDropdownElement = driver.findElement(By.cssSelector(NOV_COUNTRIES_DROPDOWN_CSS_SELECTOR));
					
					js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", countriesDropdownElement);
					
					Select countriesDropdown = new Select(countriesDropdownElement);
					
					countriesDropdown.selectByVisibleText(country);
					Thread.sleep(1000);
					
					String html_content = driver.getPageSource();
					
					Document websiteDocument = Jsoup.parse(html_content);
					
					Elements officeLocationsElements = websiteDocument.select(NOV_OFFICE_LOCATIONS_CSS_SELECTOR);
					
					Set<String> fullCompanyContactInfoSetPerLocation = JsoupHelper.getElementsTextToSet(officeLocationsElements);
					fullCompanyContactInfoSet.addAll(fullCompanyContactInfoSetPerLocation);
					
				}
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, NOV_LOCATIONS_URL, fullCompanyContactInfoSet);
			
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
