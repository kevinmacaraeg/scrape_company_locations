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
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.SeleniumHelper;

public class EMCContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(EMCContactInfoScraper.class.getName());
	
	private static final String EMC_GLOBAL_CONTACTS_URL = "https://www.emc.com/contact/contact-us.htm";
	
	private static final String EMC_COUNTRIES_DROPDOWN_CSS_SELECTOR = "select#changeLocale";
	
	private static final String EMC_OFFICE_LOCATIONS_CSS_SELECTOR = "div.location-template";
	
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + EMC_GLOBAL_CONTACTS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(EMC_GLOBAL_CONTACTS_URL);
			Thread.sleep(3000);
			
			logger.info("Getting countries from dropdown......");
			WebElement countriesDropdownElement = driver.findElement(By.cssSelector(EMC_COUNTRIES_DROPDOWN_CSS_SELECTOR));
			List<String> countriesDropdownOptions = SeleniumHelper.getDropdownTextOptions(countriesDropdownElement);
			
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (String country : countriesDropdownOptions) {
				
				logger.info("Getting contact information for " + country + "......");
				
				countriesDropdownElement = driver.findElement(By.cssSelector(EMC_COUNTRIES_DROPDOWN_CSS_SELECTOR));
				
				Select countriesDropdown = new Select(countriesDropdownElement);
				
				countriesDropdown.selectByVisibleText(country);
				Thread.sleep(1000);
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(EMC_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				for (Element officeLocationElement : officeLocationsElements) {
					fullCompanyContactInfoSet.add(country + Constants.SPACE + officeLocationElement.text());
				}
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing scraped contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, EMC_GLOBAL_CONTACTS_URL, fullCompanyContactInfoSet);

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
