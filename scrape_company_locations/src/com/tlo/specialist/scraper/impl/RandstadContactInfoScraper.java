package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class RandstadContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(RandstadContactInfoScraper.class.getName());
	
	private static final String RANDSTAD_GLOBAL_REACH_URL = "https://www.randstad.com/global-reach/";
	
	private static final String RANDSTAD_WEBSITE_URL = "https://www.randstad.com";
	
	private static final String RANDSTAD_COUNTRY_LINKS_CSS_SELECTOR = "ul.worldwidemenu-continents-page>li>ul>li>a";
	
	private static final String RANDSTAD_COMPANY_LIST_INNER_CSS_SELECTOR = "div.company-list-inner";
	
	private static final String RANDSTAD_ADDRESS_CSS_SELECTOR = "div.company-list-address";
	
	private static final String RANDSTAD_PHONE_CSS_SELECTOR = "div.company-list-contact";
	
	private static final String RANDSTAD_EMAIL_CSS_SELECTOR = "div.company-list-web";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			logger.info("Connecting to " + RANDSTAD_GLOBAL_REACH_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(RANDSTAD_GLOBAL_REACH_URL);
			Thread.sleep(5000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			logger.info("Getting location URLs......");
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements countryLinksElements = websiteDocument.select(RANDSTAD_COUNTRY_LINKS_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> locationURLs = JsoupHelper.getElementsHrefAttributes(countryLinksElements);
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(RANDSTAD_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String locationURL : locationURLs) {
				
				logger.info("Connecting to " + locationURL + "......");
				driver = new ChromeDriver();
				driver.get(locationURL);
				Thread.sleep(5000);
				
				html_content = driver.getPageSource();
				String currentCompanyLocationsUrl = driver.getCurrentUrl();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				logger.info("Selecting elements with contact information......");
				Elements companyInnerListElements = websiteDocument.select(RANDSTAD_COMPANY_LIST_INNER_CSS_SELECTOR);
				
				logger.info("Getting contact information......");
				for (Element companyInnerListElement : companyInnerListElements) {
					
					Elements companyAddressElements = companyInnerListElement.select(RANDSTAD_ADDRESS_CSS_SELECTOR);
					Elements companyPhoneElements = companyInnerListElement.select(RANDSTAD_PHONE_CSS_SELECTOR);
					Elements companyEmailElements = companyInnerListElement.select(RANDSTAD_EMAIL_CSS_SELECTOR);
					
					String address = companyAddressElements.text();
					String phoneNumber = companyPhoneElements.text();
					String email = companyEmailElements.text();
					
					phoneNumber = phoneNumber.replace("Call ", Constants.EMPTY_STRING);
					email = email.replaceAll(Constants.REGEX_WEB_URL, Constants.EMPTY_STRING);
					
					CompanyContactInformation companyContactInformation = new CompanyContactInformation();
					companyContactInformation.setMasterCompanyId(masterCompanyId);
					companyContactInformation.setMasterCompanyName(masterCompanyName);
					companyContactInformation.setCompanyLocationsUrl(currentCompanyLocationsUrl);
					companyContactInformation.setAddress(address);
					companyContactInformation.setPhoneNumber(phoneNumber);
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
