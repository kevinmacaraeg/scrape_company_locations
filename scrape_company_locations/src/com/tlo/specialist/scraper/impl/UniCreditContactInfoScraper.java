package com.tlo.specialist.scraper.impl;

import java.io.File;
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

public class UniCreditContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(UniCreditContactInfoScraper.class.getName());
	
	private static final String UNICREDIT_WORLDWIDE_PRESENCE_URL = "https://www.unicreditgroup.eu/en/worldwide/our-worldwide-presence.html";
	
	private static final String UNICREDIT_WEBSITE_URL = "https://www.unicreditgroup.eu";
	
	private static final String UNICREDIT_COUNTRY_LINKS_CSS_SELECTOR = "div.country>a";
	
	private static final String UNICREDIT_MORE_INFO_LINKS_CSS_SELECTOR = "div.moreInfo>a";
	
	private static final String UNICREDIT_OFFICE_LOCATIONS_CSS_SELECTOR = "div.container>div:nth-of-type(3)";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			logger.info("Connecting to " + UNICREDIT_WORLDWIDE_PRESENCE_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(UNICREDIT_WORLDWIDE_PRESENCE_URL);
			Thread.sleep(5000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			logger.info("Getting location URLs......");
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements countryLinksElements = websiteDocument.select(UNICREDIT_COUNTRY_LINKS_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> countryURLs = JsoupHelper.getElementsHrefAttributes(countryLinksElements);
			countryURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(UNICREDIT_WEBSITE_URL, countryURLs);
			
			Set<String> locationURLs = new HashSet<String>();
			for (String countryURL : countryURLs) {
				
				logger.info("Connecting to " + countryURL + "......");
				driver = new ChromeDriver();
				driver.get(countryURL);
				Thread.sleep(5000);
				
				html_content = driver.getPageSource();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				Elements moreInfoLinksElements = websiteDocument.select(UNICREDIT_MORE_INFO_LINKS_CSS_SELECTOR);
				
				Set<String> moreInfoURLs = JsoupHelper.getElementsHrefAttributes(moreInfoLinksElements);
				locationURLs.addAll(moreInfoURLs);
				
			}
			
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(UNICREDIT_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationURLs, UNICREDIT_OFFICE_LOCATIONS_CSS_SELECTOR);
			
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
