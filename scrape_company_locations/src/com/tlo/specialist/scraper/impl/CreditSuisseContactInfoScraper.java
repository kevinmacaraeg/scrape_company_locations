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

public class CreditSuisseContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(CreditSuisseContactInfoScraper.class.getName());
	
	private static final String CREDIT_SUISSE_OFFICE_LOCATOR_URL = "https://www.credit-suisse.com/sites/office-locator.html?country=us&lang=en";
	
	private static final String CREDIT_SUISSE_WEBSITE_URL = "https://www.credit-suisse.com";
	
	private static final String CREDIT_SUISSE_COUNTRY_LINKS_CSS_SELECTOR = "ul.mod_country_selector_list>li.mod_country_selector_list_item>a.mod_country_selector_link";
	
	private static final String CREDIT_SUISSE_LIST_VIEW_BUTTON_CSS_SELECTOR = "button.mod_findus_button.findus-location-view-mode-button.list.pic_icon_listview_white";

	private static final String CREDIT_SUISSE_OFFICE_LOCATION_PER_COUNTRY_LINKS_CSS_SELECTOR = "div.visuallyhidden>ul>li>a";
	
	private static final String CREDIT_SUISSE_OFFICE_LOCATIONS_CSS_SELECTOR = "div.findus-location-data-area-left";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + CREDIT_SUISSE_OFFICE_LOCATOR_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(CREDIT_SUISSE_OFFICE_LOCATOR_URL);
			Thread.sleep(3000);
			
			logger.info("Getting location URLs......");
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements countryLinksElements = websiteDocument.select(CREDIT_SUISSE_COUNTRY_LINKS_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> countryURLs = JsoupHelper.getElementsHrefAttributes(countryLinksElements);
			countryURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(CREDIT_SUISSE_WEBSITE_URL, countryURLs);
			
			Set<String> officeLocationsURLs = new HashSet<String>();
			for (String countryURL : countryURLs) {
				
				logger.info("Connecting to " + countryURL + "......");
				
				driver = new ChromeDriver();
				driver.get(countryURL);
				Thread.sleep(3000);
				
				WebElement listViewButtonElement = driver.findElement(By.cssSelector(CREDIT_SUISSE_LIST_VIEW_BUTTON_CSS_SELECTOR));
				listViewButtonElement.click();
				Thread.sleep(3000);
				
				logger.info("Getting individual URLs for each office location......");
				html_content = driver.getPageSource();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsLinksPerCountryElements = websiteDocument.select(CREDIT_SUISSE_OFFICE_LOCATION_PER_COUNTRY_LINKS_CSS_SELECTOR);
				Set<String> officeLocationsURLsPerCountry = JsoupHelper.getElementsHrefAttributes(officeLocationsLinksPerCountryElements);
				Set<String> tempSet = new HashSet<String>();
				for (String officeLocationURL : officeLocationsURLsPerCountry) {
					tempSet.add("/sites/" + officeLocationURL);
				}
				officeLocationsURLsPerCountry = tempSet;
				officeLocationsURLs.addAll(officeLocationsURLsPerCountry);

			}
			
			officeLocationsURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(CREDIT_SUISSE_WEBSITE_URL, officeLocationsURLs);
			
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, officeLocationsURLs, CREDIT_SUISSE_OFFICE_LOCATIONS_CSS_SELECTOR);
			
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
