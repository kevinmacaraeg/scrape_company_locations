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
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class AecomContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(AecomContactInfoScraper.class.getName());
	
	private static final String AECOM_OFFICES_URL = "http://www.aecom.com/offices/";
	
	private static final String AECOM_WEBSITE_URL = "http://www.aecom.com";
	
	private static final String AECOM_COUNTRY_LINKS_CSS_SELECTOR = "div.ae-dropdown-content.countries>ul.col>li>a";
	
	private static final String AECOM_NEXT_BUTTON_CSS_SELECTOR = "div.pagination>a.next-page-link";
	
	private static final String AECOM_OFFICE_LOCATIONS_CSS_SELECTOR = "main#main>div.flex-view.flex-view-office.flex-view-grid>ul.col>li";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + AECOM_OFFICES_URL +"......");
			WebDriver driver = new ChromeDriver();
			driver.get(AECOM_OFFICES_URL);
			Thread.sleep(3000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			logger.info("Getting company location URLs for each country......");
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements countryLinks = websiteDocument.select(AECOM_COUNTRY_LINKS_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> locationURLs = JsoupHelper.getElementsHrefAttributes(countryLinks);
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(AECOM_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String locationURL : locationURLs) {
				
				logger.info("Connecting to " + locationURL + "......");
				driver = new ChromeDriver();
				driver.get(locationURL);
				Thread.sleep(3000);
				
				logger.info("Getting contact information......");
				boolean hasNextPage = true;
				int pageNumber = 1;
				do {
					logger.info("Scraping contact information from Page " + pageNumber + "......");
					WebElement nextPageButtonElement = null;
					try {
						nextPageButtonElement = driver.findElement(By.cssSelector(AECOM_NEXT_BUTTON_CSS_SELECTOR));
					} catch (NoSuchElementException e) {
						hasNextPage = false;
					}
					
					html_content = driver.getPageSource();
					
					websiteDocument = Jsoup.parse(html_content);
					
					Elements officeLocationsElements = websiteDocument.select(AECOM_OFFICE_LOCATIONS_CSS_SELECTOR);
					
					Set<String> fullCompanyContactInfoSet = new HashSet<String>();
					for (Element officeLocationElement : officeLocationsElements) {
						fullCompanyContactInfoSet.add(officeLocationElement.text());
					}
					
					List<CompanyContactInformation> companyContactInformationListPerPage = service.parseContactInformation(masterCompanyId, masterCompanyName, driver.getCurrentUrl(), fullCompanyContactInfoSet);
					companyContactInformationList.addAll(companyContactInformationListPerPage);
					
					if (hasNextPage) {
						try {
							nextPageButtonElement.click();
							Thread.sleep(5000);
							pageNumber++;
						} catch (ElementNotVisibleException e) {
							hasNextPage = false;
						}
					}
				} while (hasNextPage);
				
				driver.quit();
				
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
