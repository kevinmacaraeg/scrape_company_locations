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

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class CadenceDesignSystemsContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(CadenceDesignSystemsContactInfoScraper.class.getName());
	
	private static final String CADENCE_CONTACT_US_URL = "https://www.cadence.com/content/cadence-www/global/en_US/home/company/contact-us.html";
	
	private static final String CADENC_COUNTRY_OPTIONS_CSS_SELECTOR = "ul.dropdown-menu.inner.selectpicker>li>a";
	
	private static final String CADENCE_GO_BUTTON_CSS_SELECTOR = "div.dropdown.section.dropdown_contact_us>input.form_button_submit";
	
	private static final String CADENCE_OFFICE_LOCATIONS_CSS_SELECTOR = "div#regionalinfo>div";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + CADENCE_CONTACT_US_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(CADENCE_CONTACT_US_URL);
			Thread.sleep(3000);
			
			List<WebElement> countryOptionsElements = driver.findElements(By.cssSelector(CADENC_COUNTRY_OPTIONS_CSS_SELECTOR));
			
			int numberOfOptions = countryOptionsElements.size();
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Scraping available company contact information......");
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (int i = 0; i < numberOfOptions; i++) {
				
				driver = new ChromeDriver();
				driver.get(CADENCE_CONTACT_US_URL);
				Thread.sleep(3000);
				
				JavascriptExecutor js = (JavascriptExecutor) driver;
				
				countryOptionsElements = driver.findElements(By.cssSelector(CADENC_COUNTRY_OPTIONS_CSS_SELECTOR));
				
				WebElement currentCountryOptionElement = countryOptionsElements.get(i);
				
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", currentCountryOptionElement);
				Thread.sleep(1000);
				
				WebElement goButtonElement = driver.findElement(By.cssSelector(CADENCE_GO_BUTTON_CSS_SELECTOR));
				
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", goButtonElement);
				Thread.sleep(1000);
				
				String html_content = driver.getPageSource();
				String currentURL = driver.getCurrentUrl();
				
				driver.quit();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(CADENCE_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
				
				List<CompanyContactInformation> companyContactInformationListPerCountry = service.parseContactInformation(masterCompanyId, masterCompanyName, currentURL, fullCompanyContactInfoSet);
				companyContactInformationList.addAll(companyContactInformationListPerCountry);
				
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
