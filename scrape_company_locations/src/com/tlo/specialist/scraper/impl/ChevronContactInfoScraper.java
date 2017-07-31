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

public class ChevronContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(ChevronContactInfoScraper.class.getName());
	
	private static final String CHEVRON_WORLDWIDE_URL = "https://www.chevron.com/worldwide";
	
	private static final String CHEVRON_WEBSITE_URL = "https://www.chevron.com";
	
	private static final String CHEVRON_COUNTRY_LINKS_CSS_SELECTOR = "ul.countryList>li>a:nth-of-type(1)";
	
	private static final String CHEVRON_CONTENT_DIV_CSS_SELECTOR = "div.content-basics.container-fluid.width-1280";
	
	private static final String CHEVRON_OFFICE_LOCATIONS_CSS_SELECTOR = "div.col.centered.width-800>div.cb-content";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + CHEVRON_WORLDWIDE_URL +"......");
			WebDriver driver = new ChromeDriver();
			driver.get(CHEVRON_WORLDWIDE_URL);
			Thread.sleep(3000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			logger.info("Getting company location URLs for each country......");
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements countryLinks = websiteDocument.select(CHEVRON_COUNTRY_LINKS_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> locationURLs = JsoupHelper.getElementsHrefAttributes(countryLinks);
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(CHEVRON_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String locationURL : locationURLs) {
				logger.info("Connecting to " + locationURL +"......");
				driver = new ChromeDriver();
				driver.get(locationURL);
				Thread.sleep(3000);
				
				html_content = driver.getPageSource();
				
				driver.quit();
				
				logger.info("Getting contact information available......");
				websiteDocument = Jsoup.parse(html_content);
				
				Elements contentDivElements = websiteDocument.select(CHEVRON_CONTENT_DIV_CSS_SELECTOR);
				
				Element contactUsDivElement = null;
				for (Element contentDivElement : contentDivElements) {
					if (contentDivElement.text().contains("contact us")) {
						contactUsDivElement = contentDivElement;
						break;
					}
				}
				
				Set<String> fullCompanyContactInfoSet = new HashSet<String>();
				if (contactUsDivElement != null) {
					Elements officeLocationsElement = contactUsDivElement.select(CHEVRON_OFFICE_LOCATIONS_CSS_SELECTOR);
					
					Elements pElements = officeLocationsElement.select(Constants.HTML_ELEMENT_P);
					
					if (pElements.size() > 0) {
						for (Element pElement : pElements) {
							fullCompanyContactInfoSet.add(pElement.text());
						}
					} else {
						fullCompanyContactInfoSet.add(officeLocationsElement.text());
					}
				}
				
				logger.info("Parsing scraped contact information.......");
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
