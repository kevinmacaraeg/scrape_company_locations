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

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class JLLContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(JLLContactInfoScraper.class.getName());
	
	private static final String JLL_WORLDWIDE_LOCATIONS_URL = "http://www.jll.com/locations";
	
	private static final String JLL_REGION_LINKS_CSS_SELECTOR = "div.region_selector>ul>li>a";
	
	private static final String JLL_COUNTRY_LINKS_CSS_SELECTOR = "div.sidebar.section_navigation>ul>li>a";
	
	private static final String JLL_OFFICE_LOCATIONS_CSS_SELECTOR = "div.locale.equalize";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + JLL_WORLDWIDE_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(JLL_WORLDWIDE_LOCATIONS_URL);
			Thread.sleep(3000);
			
			logger.info("Getting available company contact information......");
			String html_content = driver.getPageSource();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements regionLinksElements = websiteDocument.select(JLL_REGION_LINKS_CSS_SELECTOR);
			
			Set<String> regionLinksElementsIDs = JsoupHelper.getElementsAttributeValue(Constants.HTML_ELEMENT_ATTR_ID, regionLinksElements);
			
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (String regionLinkElementID : regionLinksElementsIDs) {
				
				WebElement regionLinkElement = driver.findElement(By.id(regionLinkElementID));
				
				logger.info("Scraping company contact information for " + regionLinkElement.getText() + "......");
				
				regionLinkElement.click();
				Thread.sleep(3000);
				
				html_content = driver.getPageSource();
				
				websiteDocument = Jsoup.parse(html_content);
				
				Elements countryLinksElements = websiteDocument.select(JLL_COUNTRY_LINKS_CSS_SELECTOR);
				
				Set<String> countryLinksElementsIDs = JsoupHelper.getElementsAttributeValue(Constants.HTML_ELEMENT_ATTR_ID, countryLinksElements);
				
				for (String countryLinkElementID : countryLinksElementsIDs) {
					
					WebElement countryLinkElement = driver.findElement(By.id(countryLinkElementID));
					
					logger.info("Scraping company contact information for " + countryLinkElement.getText() + "......");
					
					countryLinkElement.click();
					Thread.sleep(3000);
					
					html_content = driver.getPageSource();
					
					websiteDocument = Jsoup.parse(html_content);
					
					Elements officeLocationsElements = websiteDocument.select(JLL_OFFICE_LOCATIONS_CSS_SELECTOR);
					
					for (Element officeLocationElement : officeLocationsElements) {
						fullCompanyContactInfoSet.add(officeLocationElement.text());
					}
					
				}
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, JLL_WORLDWIDE_LOCATIONS_URL, fullCompanyContactInfoSet);
			
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
