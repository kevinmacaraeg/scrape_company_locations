package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;
import com.tlo.specialist.util.StringHelper;

public class StantecContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(StantecContactInfoScraper.class.getName());
	
	private static final String STANTEC_OFFICE_LOCATIONS_URL = "http://www.stantec.com/about-us/office-locations.html";
	
	private static final String STANTEC_WEBSITE_URL = "http://www.stantec.com";
	
	private static final String STANTEC_COUNTRY_LINKS_CSS_SELECTOR = "div.cq-list-of-links.section>div.module.link-list>ul.link-list-container.clearfix>li.link-list-item>a.link-list-header";
	
	private static final String STANTEC_REGION_LINKS_CSS_SELECTOR = "div.cq-filtered-index>div.module.filter-index>ul#filter-index-list>li.filter-index-item>a";
	
	private static final String STANTEC_OFFICE_LINKS_CSS_SELECTOR = "div.cq-filtered-index>div.module.filter-index>ul#filter-index-list>li.filter-index-item>a";
	
	private static final String STANTEC_OFFICE_LOCATIONS_CSS_SELECTOR = "div.office-location";
	
	private static final String STANTEC_ADDRESS_CSS_SELECTOR = "article.address";
	
	private static final String STANTEC_PHONE_CSS_SELECTOR = "span.phone";
	
	private static final String STANTEC_FAX_CSS_SELECTOR = "span.fax";
	
	private static final String STANTEC_EMAIL_CSS_SELECTOR = "a.email";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + STANTEC_OFFICE_LOCATIONS_URL +"......");
			WebDriver driver = new ChromeDriver();
			driver.get(STANTEC_OFFICE_LOCATIONS_URL);
			Thread.sleep(3000);
			
			logger.info("Getting location URLs......");
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Elements countryLinksElements = websiteDocument.select(STANTEC_COUNTRY_LINKS_CSS_SELECTOR);
			Set<String> countryURLs = JsoupHelper.getElementsHrefAttributes(countryLinksElements);
			countryURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(STANTEC_WEBSITE_URL, countryURLs);
			
			Set<String> locationURLs = new HashSet<String>();
			for (String countryURL : countryURLs) {
				
				logger.info("Connecting to " + countryURL +"......");
				driver = new ChromeDriver();
				driver.get(countryURL);
				Thread.sleep(3000);
				
				html_content = driver.getPageSource();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				Elements regionLinksElements = websiteDocument.select(STANTEC_REGION_LINKS_CSS_SELECTOR);
				Set<String> regionURLs = JsoupHelper.getElementsHrefAttributes(regionLinksElements);
				regionURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(STANTEC_WEBSITE_URL, regionURLs);
				
				for (String regionURL : regionURLs) {
					
					if (regionURL.endsWith("-office.html")) {
						
						locationURLs.add(regionURL);
						
					} else {
						
						logger.info("Connecting to " + regionURL +"......");
						driver = new ChromeDriver();
						driver.get(regionURL);
						Thread.sleep(3000);
						
						html_content = driver.getPageSource();
						
						driver.quit();
						
						websiteDocument = Jsoup.parse(html_content);
				
						Elements officeLinksElements = websiteDocument.select(STANTEC_OFFICE_LINKS_CSS_SELECTOR);
						Set<String> officeURLs = JsoupHelper.getElementsHrefAttributes(officeLinksElements);
						locationURLs.addAll(officeURLs);
					
					}
				
				}
				
			}
			
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(STANTEC_WEBSITE_URL, locationURLs);
			
			logger.info("Scraping all available company contact information......");
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String locationURL : locationURLs) {
				
				logger.info("Connecting to " + locationURL +"......");
				driver = new ChromeDriver();
				driver.get(locationURL);
				Thread.sleep(3000);
				
				html_content = driver.getPageSource();
				String currentURL = driver.getCurrentUrl();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(STANTEC_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				Elements addressElements = officeLocationsElements.select(STANTEC_ADDRESS_CSS_SELECTOR);
				Elements phoneElements = officeLocationsElements.select(STANTEC_PHONE_CSS_SELECTOR);
				Elements faxElements = officeLocationsElements.select(STANTEC_FAX_CSS_SELECTOR);
				Elements emailElements = officeLocationsElements.select(STANTEC_EMAIL_CSS_SELECTOR);
				
				String address = addressElements.text().trim();
				String phoneNumber = phoneElements.text().trim();
				String faxNumber = faxElements.text().trim();
				String email = emailElements.text().trim();
				
				if (StringHelper.isNotEmpty(address + phoneNumber + faxNumber + email)) {
					
					CompanyContactInformation companyContactInformation = new CompanyContactInformation();
					
					companyContactInformation.setMasterCompanyId(masterCompanyId);
					companyContactInformation.setMasterCompanyName(masterCompanyName);
					companyContactInformation.setCompanyLocationsUrl(currentURL);
					companyContactInformation.setAddress(address);
					companyContactInformation.setPhoneNumber(phoneNumber);
					companyContactInformation.setFaxNumber(faxNumber);
					companyContactInformation.setEmail(email);
					
					companyContactInformationList.add(companyContactInformation);
					
				}
				
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
