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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class ABBContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(ABBContactInfoScraper.class.getName());
	
	private static final String ABB_WHERE_TO_FIND_US_URL = "http://www.abb.com/cawp/abbzh252/93accd6f33415725c1256ae7004e7188.aspx";
	
	private static final String ABB_WEBSITE_URL = "http://www.abb.com";
	
	private static final String ABB_REGION_LINKS_CSS_SELECTOR = "span.arrow.bigArrow.textWithImage>a";
	
	private static final String ABB_COUNTRY_LINKS_CSS_SELECTOR = "span.arrow.bigArrow.textWithoutImage>a";
	
	private static final String ABB_COUNTRY_CSS_SELECTOR = "div#mainRegion>div.section>h1";
	
	private static final String ABB_OFFICE_LOCATIONS_CSS_SELECTOR = "table.referenceTable>tbody>tr";
	
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + ABB_WHERE_TO_FIND_US_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(ABB_WHERE_TO_FIND_US_URL);
			Thread.sleep(3000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements regionLinksElements = websiteDocument.select(ABB_REGION_LINKS_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> regionURLs = JsoupHelper.getElementsHrefAttributes(regionLinksElements);
			regionURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(ABB_WEBSITE_URL, regionURLs);
			
			Set<String> locationURLs = new HashSet<String>();
			for (String regionURL : regionURLs) {
				driver = new ChromeDriver();
				driver.get(regionURL);
				Thread.sleep(3000);
				
				html_content = driver.getPageSource();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				Elements countryLinksElements = websiteDocument.select(ABB_COUNTRY_LINKS_CSS_SELECTOR);
				
				Set<String> countryURLs = JsoupHelper.getElementsHrefAttributes(countryLinksElements);
				locationURLs.addAll(countryURLs);
			}
			
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(ABB_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String locationURL : locationURLs) {
				
				driver = new ChromeDriver();
				driver.get(locationURL);
				Thread.sleep(3000);
				
				html_content = driver.getPageSource();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				Elements countryElement = websiteDocument.select(ABB_COUNTRY_CSS_SELECTOR);
				
				Elements addressesElements = websiteDocument.select(ABB_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				String country = countryElement.text().replace("Addresses in ", Constants.EMPTY_STRING).trim();
				
				Set<String> fullCompanyContactInfoSet = new HashSet<String>();
				for (Element addressElement : addressesElements) {
					Elements addressDetailsElement = addressElement.select(Constants.HTML_ELEMENT_TD);
					
					if (addressDetailsElement.size() == 4) {
						String streetAddress = addressDetailsElement.get(3).text();
						String city = addressDetailsElement.get(1).text();
						String phone = addressDetailsElement.get(2).text();
						
						String fullCompanyContactInformation = streetAddress.trim() + Constants.SPACE + city.trim() + Constants.SPACE + country + Constants.SPACE + phone.trim();
						fullCompanyContactInfoSet.add(fullCompanyContactInformation);
					}
				}
				
				List<CompanyContactInformation> companyContactInformationListPerCountry = service.parseContactInformation(masterCompanyId, masterCompanyName, locationURL, fullCompanyContactInfoSet);
				companyContactInformationList.addAll(companyContactInformationListPerCountry);
				
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
