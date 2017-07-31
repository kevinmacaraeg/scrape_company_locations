package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

public class MacquarieContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(MacquarieContactInfoScraper.class.getName());
	
	private static final String MACQUARIE_OFFICE_LOCATIONS_URL = "https://www.macquarie.com/about/office-locations";
	
	private static final String MACQUARIE_REGIONS_DROPDOWN_CSS_SELECTOR = "div.cards.container-fluid>div.row>div:nth-of-type(1)>div.form-group>label.select-dropdown>select";
	
	private static final String MACQUARIE_ALL_REGIONS_OPTION = "Region";
	
	private static final String MACQUARIE_LOCATIONS_DROPDOWN_CSS_SELECTOR = "div.cards.container-fluid>div.row>div:nth-of-type(2)>div.form-group>label.select-dropdown>select";
	
	private static final String MACQUARIE_ALL_LOCATIONS_OPTION = "Location";
	
	private static final String MACQUARIE_OFFICE_LOCATIONS_CSS_SELECTOR = "ul.list-table-plain>li>div.row";
	
	private static final String MACQUARIE_ADDRESS_CSS_SELECTOR = "p.card-subtitle.displayaddress.list-table-plain-first";
	
	private static final String MACQUARIE_PHONE_CSS_SELECTOR = "a.card-cta.tertiary-cta";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + MACQUARIE_OFFICE_LOCATIONS_URL +"......");
			WebDriver driver = new ChromeDriver();
			driver.get(MACQUARIE_OFFICE_LOCATIONS_URL);
			Thread.sleep(5000);
			
			WebElement regionsDropdownElement = driver.findElement(By.cssSelector(MACQUARIE_REGIONS_DROPDOWN_CSS_SELECTOR));
			Select regionsDropdown = new Select(regionsDropdownElement);
			regionsDropdown.selectByVisibleText(MACQUARIE_ALL_REGIONS_OPTION);
			Thread.sleep(1000);
			
			WebElement locationsDropdownElement = driver.findElement(By.cssSelector(MACQUARIE_LOCATIONS_DROPDOWN_CSS_SELECTOR));
			Select locationsDropdown = new Select(locationsDropdownElement);
			locationsDropdown.selectByVisibleText(MACQUARIE_ALL_LOCATIONS_OPTION);
			Thread.sleep(1000);
			
			logger.info("Scraping available company contact information......");
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements officeLocationsElements = websiteDocument.select(MACQUARIE_OFFICE_LOCATIONS_CSS_SELECTOR);
			
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (Element officeLocationElement : officeLocationsElements) {
				
				Elements addressElements = officeLocationElement.select(MACQUARIE_ADDRESS_CSS_SELECTOR);
				Elements phoneNumberElements = officeLocationElement.select(MACQUARIE_PHONE_CSS_SELECTOR);
				
				String address = addressElements.text();
				String phoneNumber = phoneNumberElements.text();
				
				phoneNumber = phoneNumber.replace("Get Directions", Constants.EMPTY_STRING).trim();
				
				CompanyContactInformation companyContactInformation = new CompanyContactInformation();
				
				companyContactInformation.setMasterCompanyId(masterCompanyId);
				companyContactInformation.setMasterCompanyName(masterCompanyName);
				companyContactInformation.setCompanyLocationsUrl(MACQUARIE_OFFICE_LOCATIONS_URL);
				companyContactInformation.setAddress(address);
				companyContactInformation.setPhoneNumber(phoneNumber);
				
				companyContactInformationList.add(companyContactInformation);
				
			}
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
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
