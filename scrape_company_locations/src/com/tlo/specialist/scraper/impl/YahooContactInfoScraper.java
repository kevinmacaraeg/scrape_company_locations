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
import com.tlo.specialist.util.JsoupHelper;

public class YahooContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(YahooContactInfoScraper.class.getName());
	
	private static final String YAHOO_CAREER_LOCATIONS_URL = "https://careers.yahoo.com/contactus.php?country=us&lang=en";
	
	private static final String YAHOO_CAREER_US_LOCATIONS_URL = "https://careers.yahoo.com/contactus.php?country=us&lang=en";
	
	private static final String YAHOO_CAREER_AU_LOCATIONS_URL = "https://careers.yahoo.com/contactus.php?country=au&lang=en";
	
	private static final String YAHOO_CAREER_NZ_LOCATIONS_URL = "https://careers.yahoo.com/contactus.php?country=nz&lang=en";
	
	private static final String YAHOO_LOCATION_DROPDOWN_BUTTON_CSS_SELECTOR = "button#headLocation-button";
	
	private static final String YAHOO_LOCATION_LINKS_CSS_SELECTOR = "div#yui-gen0>div.bd>ul>li.yuimenuitem>a";
	
	private static final String YAHOO_OFFICE_LOCATIONS_CSS_SELECTOR = "div.para>p";
	
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + YAHOO_CAREER_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(YAHOO_CAREER_LOCATIONS_URL);
			Thread.sleep(3000);
			
			WebElement locationDropdownButtonElement = driver.findElement(By.cssSelector(YAHOO_LOCATION_DROPDOWN_BUTTON_CSS_SELECTOR));
			
			locationDropdownButtonElement.click();
			Thread.sleep(1000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements locationLinksElements = websiteDocument.select(YAHOO_LOCATION_LINKS_CSS_SELECTOR);
			List<String> locationLinksElementsTexts = JsoupHelper.getElementsTextToList(locationLinksElements);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String locationLinkText : locationLinksElementsTexts) {
				
				driver = new ChromeDriver();
				driver.get(YAHOO_CAREER_LOCATIONS_URL);
				Thread.sleep(3000);
				
				logger.info("Getting location URLs......");
				locationDropdownButtonElement = driver.findElement(By.cssSelector(YAHOO_LOCATION_DROPDOWN_BUTTON_CSS_SELECTOR));
				
				locationDropdownButtonElement.click();
				Thread.sleep(1000);
				
				List<WebElement> locationDropdownOptionsElements = driver.findElements(By.cssSelector(YAHOO_LOCATION_LINKS_CSS_SELECTOR));
				
				for (WebElement locationDropdownOptionElement : locationDropdownOptionsElements) {
					if (locationLinkText.equalsIgnoreCase(locationDropdownOptionElement.getText())) {
						
						logger.info("Scraping contact information for " + locationLinkText + "......");
						
						if ("United States".equalsIgnoreCase(locationLinkText)) {
							driver.get(YAHOO_CAREER_US_LOCATIONS_URL);
							Thread.sleep(1000);
						} else if ("Australia".equalsIgnoreCase(locationLinkText)) {
							driver.get(YAHOO_CAREER_AU_LOCATIONS_URL);
							Thread.sleep(1000);
						} else if ("New Zealand".equalsIgnoreCase(locationLinkText)) {
							driver.get(YAHOO_CAREER_NZ_LOCATIONS_URL);
							Thread.sleep(1000);
						} else {
							locationDropdownOptionElement.click();
							Thread.sleep(1000);
						}
						
						html_content = driver.getPageSource();
						
						websiteDocument = Jsoup.parse(html_content);
						
						Elements officeLocationsElements = websiteDocument.select(YAHOO_OFFICE_LOCATIONS_CSS_SELECTOR);
						
						Set<String> fullCompanyContactInfoSet = new HashSet<String>();
						for (Element officeLocationElement : officeLocationsElements) {
							fullCompanyContactInfoSet.add(officeLocationElement.text());
						}
						
						logger.info("Parsing scraped contact information......");
						List<CompanyContactInformation> companyContactInformationListPerLocation = service.parseContactInformation(masterCompanyId, masterCompanyName, driver.getCurrentUrl(), fullCompanyContactInfoSet);
						companyContactInformationList.addAll(companyContactInformationListPerLocation);
						
						driver.quit();
						
						break;
					}
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
