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

public class QuintilesIMSContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(QuintilesIMSContactInfoScraper.class.getName());
	
	private static final String QUINTILES_LOCATIONS_URL = "http://www.quintiles.com/locations?_ga=1.147612981.348758074.1489993220";
	
	private static final String QUINTILES_JAPAN_LOCATIONS_URL = "http://www.quintiles.co.jp/about-us/quintiles-transnational-japan";
	
	private static final String QUINTILES_WEBSITE_URL = "http://www.quintiles.com";
	
	private static final String QUINTILES_COUNTRY_LINKS_CSS_SELECTOR = "div.row.locations>div>a";
	
	private static final String QUINTILES_OFFICE_LOCATIONS_CSS_SELECTOR = "div.location-address.section";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			logger.info("Connecting to " + QUINTILES_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(QUINTILES_LOCATIONS_URL);
			Thread.sleep(5000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			logger.info("Getting location URLs......");
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements countryLinksElements = websiteDocument.select(QUINTILES_COUNTRY_LINKS_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> locationURLs = JsoupHelper.getElementsHrefAttributes(countryLinksElements);
			locationURLs.add(QUINTILES_JAPAN_LOCATIONS_URL);
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(QUINTILES_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String locationURL : locationURLs) {

				logger.info("Connecting to " + locationURL + "......");
				driver = new ChromeDriver();
				driver.get(locationURL);
				Thread.sleep(5000);
				
				html_content = driver.getPageSource();
				String currentLocationsUrl = driver.getCurrentUrl();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				logger.info("Selecting elements with contact information......");
				Elements contactInfoElements = websiteDocument.select(QUINTILES_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				Set<String> fullCompanyContactInfoSet = new HashSet<String>();
				logger.info("Getting contact information......");
				for (Element contactInfoElement : contactInfoElements) {
					StringBuilder fullCompanyContactInfoBuilder = null;
					for (Element contactInfoElementChild : contactInfoElement.children()) {
						if (Constants.HTML_ELEMENT_I.equals(contactInfoElementChild.tagName())) {
							if (fullCompanyContactInfoBuilder != null) {
								fullCompanyContactInfoSet.add(fullCompanyContactInfoBuilder.toString());
							}
							fullCompanyContactInfoBuilder = new StringBuilder();
						} else {
							fullCompanyContactInfoBuilder.append(contactInfoElementChild.text()).append(Constants.SPACE);
						}
					}
					if (fullCompanyContactInfoBuilder != null) {
						fullCompanyContactInfoSet.add(fullCompanyContactInfoBuilder.toString());
					}
				}
				
				List<CompanyContactInformation> companyContactInformationListPerCountry = service.parseContactInformation(masterCompanyId, masterCompanyName, currentLocationsUrl, fullCompanyContactInfoSet);
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
