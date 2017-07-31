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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class PrudentialFinancialContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(PrudentialFinancialContactInfoScraper.class.getName());
	
	private static final String PRUDENTIAL_WORLDWIDE_LOCATIONS_URL = "http://corporate.prudential.com/view/page/corp/31891";
	
	private static final String PRUDENTIAL_COUNTRY_LINKS_CSS_SELECTOR = "div.country>a";
	
	private static final String PRUDENTIAL_COUNTRY_CONTENT_CLOSE_BUTTON_CSS_SELECTOR = "div#tooltip>a.close";
	
	private static final String PRUDENTIAL_OFFICE_LOCATIONS_CSS_SELECTOR = "p.address_block";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			logger.info("Connecting to " + PRUDENTIAL_WORLDWIDE_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(PRUDENTIAL_WORLDWIDE_LOCATIONS_URL);
			Thread.sleep(5000);
			
			List<WebElement> countryLinksElements = driver.findElements(By.cssSelector(PRUDENTIAL_COUNTRY_LINKS_CSS_SELECTOR));
			
			logger.info("Scraping available company contact information......");
			
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (WebElement countryLinkElement : countryLinksElements) {
				
				countryLinkElement.click();
				Thread.sleep(1000);
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationElements = websiteDocument.select(PRUDENTIAL_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				Set<String> fullCompanyContactInfoSetPerCountry = JsoupHelper.getElementsTextToSet(officeLocationElements);
				fullCompanyContactInfoSet.addAll(fullCompanyContactInfoSetPerCountry);
				
				WebElement closeButtonElement = driver.findElement(By.cssSelector(PRUDENTIAL_COUNTRY_CONTENT_CLOSE_BUTTON_CSS_SELECTOR));
				closeButtonElement.click();
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, PRUDENTIAL_WORLDWIDE_LOCATIONS_URL, fullCompanyContactInfoSet);
			
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
