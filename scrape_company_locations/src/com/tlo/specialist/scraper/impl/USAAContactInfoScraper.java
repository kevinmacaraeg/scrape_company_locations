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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;

public class USAAContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(USAAContactInfoScraper.class.getName());
	
	private static final String USAA_LOCATIONS_URL = "https://www.usaa.com/inet/pages/about_usaa_corporate_overview_locations";
	
	private static final String USAA_OFFICE_LOCATIONS_CSS_SELECTOR = "div.yui-content>div>div.prepend-top-6.clearfix";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + USAA_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(USAA_LOCATIONS_URL);
			Thread.sleep(3000);
			
			logger.info("Scraping available company contact information......");	
				
			String html_content = driver.getPageSource();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements officeLocationsElements = websiteDocument.select(USAA_OFFICE_LOCATIONS_CSS_SELECTOR);
			
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();	
			for (Element officeLocationElement : officeLocationsElements) {
				
				Elements officeLocationChildrenElements = officeLocationElement.children();
			
				boolean isFirstPElement = true;
				for (Element officeLocationChildElement : officeLocationChildrenElements) {
					
					String currentChildTagName = officeLocationChildElement.tagName();
					
					if (Constants.HTML_ELEMENT_P.equals(currentChildTagName)) {
						
						if (isFirstPElement) {
							fullCompanyContactInfoSet.add(officeLocationChildElement.text());
							isFirstPElement = false;
						} else {
							
							continue;
							
						}
						
					} else {
						
						isFirstPElement = true;
						continue;
						
					}
					
				}
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, USAA_LOCATIONS_URL, fullCompanyContactInfoSet);
			
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
