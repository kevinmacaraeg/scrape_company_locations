package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.ArrayList;
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

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.StringHelper;

public class CushmanAndWakefieldContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(CushmanAndWakefieldContactInfoScraper.class.getName());
	
	private static final String CRUSHMAN_WAKEFIELD_OFFICE_LOCATIONS_URL = "http://www.cushmanwakefield.com/en/offices/";
	
	private static final String CRUSHMAN_WAKEFIELD_SEARCH_TEXTBOX_CSS_SELECTOR = "input#ctl15_inLocation";
	
	private static final String CRUSHMAN_WAKEFIELD_SEARCH_BUTTON_CSS_SELECTOR = "a#ctl15_btnGo";
	
	private static final String CRUSHMAN_WAKEFIELD_OFFICE_LOCATIONS_CSS_SELECTOR = "li.resultItem";

	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + CRUSHMAN_WAKEFIELD_OFFICE_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(CRUSHMAN_WAKEFIELD_OFFICE_LOCATIONS_URL);
			Thread.sleep(3000);
			
			List<String> countryList = StringHelper.getISOCountriesList();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String country : countryList) {
				
				WebElement searchTextboxElement = driver.findElement(By.cssSelector(CRUSHMAN_WAKEFIELD_SEARCH_TEXTBOX_CSS_SELECTOR));
				WebElement searchButtonElement = driver.findElement(By.cssSelector(CRUSHMAN_WAKEFIELD_SEARCH_BUTTON_CSS_SELECTOR));
				
				logger.info("Searching office locations for " + country + "......");
				searchTextboxElement.clear();
				searchTextboxElement.sendKeys(country);
				
				searchButtonElement.click();
				Thread.sleep(1000);
				
				logger.info("Scraping available contact information......");
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(CRUSHMAN_WAKEFIELD_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				Set<String> fullCompanyContactInfoSet = new HashSet<String>();
				for (Element officeLocationElement : officeLocationsElements) {
					fullCompanyContactInfoSet.add(officeLocationElement.text());
				}
				
				logger.info("Parsing scraped company contact information......");
				List<CompanyContactInformation> companyContactInformationListPerCountry = service.parseContactInformation(masterCompanyId, masterCompanyName, driver.getCurrentUrl(), fullCompanyContactInfoSet);
				companyContactInformationList.addAll(companyContactInformationListPerCountry);
				
			}
			
			driver.quit();
				
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
