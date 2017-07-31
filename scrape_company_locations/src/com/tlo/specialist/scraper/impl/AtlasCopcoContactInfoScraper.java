package com.tlo.specialist.scraper.impl;

import java.io.File;
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

public class AtlasCopcoContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(AtlasCopcoContactInfoScraper.class.getName());
	
	private static final String ATLAS_COPCO_CONTACT_US_URL = "http://www.atlascopco.com/us/contactus/";
	
	private static final String ATLAS_COPCO_COUNTRIES_DROPDOWN_CSS_SELECTOR = "select#countries";
	
	private static final String ATLAS_COPCO_ALL_COUNTRIES_DROPDOWN_VALUE = "--";
	
	private static final String ATLAS_COPCO_SUBMIT_BUTTON_CSS_SELECTOR = "input#SubmitButton";
	
	private static final String ATLAS_COPCO_OFFICE_LOCATIONS_CSS_SELECTOR = "div.contact-components>div.contact-row>div.contact-component";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + ATLAS_COPCO_CONTACT_US_URL +"......");
			WebDriver driver = new ChromeDriver();
			driver.get(ATLAS_COPCO_CONTACT_US_URL);
			Thread.sleep(3000);
			
			WebElement countriesDropdownElement = driver.findElement(By.cssSelector(ATLAS_COPCO_COUNTRIES_DROPDOWN_CSS_SELECTOR));
			
			Select countriesDropdown = new Select(countriesDropdownElement);
			
			countriesDropdown.selectByValue(ATLAS_COPCO_ALL_COUNTRIES_DROPDOWN_VALUE);
			
			WebElement submitButtonElement = driver.findElement(By.cssSelector(ATLAS_COPCO_SUBMIT_BUTTON_CSS_SELECTOR));
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", submitButtonElement);
			Thread.sleep(3000);
					
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements officeLocationsElements = websiteDocument.select(ATLAS_COPCO_OFFICE_LOCATIONS_CSS_SELECTOR);
			
			Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
				
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, ATLAS_COPCO_CONTACT_US_URL, fullCompanyContactInfoSet);
			
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
