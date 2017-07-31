package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.ArrayList;
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

public class DieboldContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(DieboldContactInfoScraper.class.getName());
	
	private static final String DIEBOLD_GLOBAL_LOCATIONS_URL = "http://www.dieboldnixdorf.com/en-us/global-locations";
	
	private static final String DIEBOLD_WEBSITE_URL = "http://www.dieboldnixdorf.com";
	
	private static final String DIEBOLD_REGION_LINKS_CSS_SELECTOR = "div.content.align-center.white>ul>li>a";
	
	private static final String DIEBOLD_REGION_SUMMARY_CSS_SELECTOR = "div.region-page-copy";
	
	private static final String DIEBOLD_OFFICES_CSS_SELECTOR = "ul.offices-info.toggle-target>li.location-office";

	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + DIEBOLD_GLOBAL_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(DIEBOLD_GLOBAL_LOCATIONS_URL);
			Thread.sleep(5000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			logger.info("Getting location URLs......");
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements regionLinksElements = websiteDocument.select(DIEBOLD_REGION_LINKS_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> locationURLs = JsoupHelper.getElementsHrefAttributes(regionLinksElements);
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(DIEBOLD_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			List<CompanyContactInformation> companyContactInformationListForHeadquarters = new ArrayList<CompanyContactInformation>();
			for (String locationURL : locationURLs) {
				logger.info("Connecting to " + locationURL + "......");
				driver = new ChromeDriver();
				driver.get(locationURL);
				Thread.sleep(5000);
				
				html_content = driver.getPageSource();
				
				websiteDocument = Jsoup.parse(html_content);
				
				logger.info("Getting the headquarters contact information......");
				Elements regionSummaryElement = websiteDocument.select(DIEBOLD_REGION_SUMMARY_CSS_SELECTOR);
				
				String regionSummary = regionSummaryElement.text();
				
				String[] regionSummarySplitByHeadquarters = regionSummary.split("Headquarters");
				if (regionSummarySplitByHeadquarters.length == 2) {
					CompanyContactInformation regionHeadquarters = service.parseContactInformation(masterCompanyId, masterCompanyName, driver.getCurrentUrl(), regionSummarySplitByHeadquarters[1]);
					companyContactInformationListForHeadquarters.add(regionHeadquarters);
				}
				
				driver.quit();
			}
			
			List<CompanyContactInformation> companyContactInformationListForLocalOffices = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationURLs, DIEBOLD_OFFICES_CSS_SELECTOR);
			companyContactInformationList.addAll(companyContactInformationListForHeadquarters);
			companyContactInformationList.addAll(companyContactInformationListForLocalOffices);
			
			logger.info("Writing company contacts to output file...");
			String outputFileName = service.getOutputFileName(masterCompanyName);
			File excelOutputFile = FileHelper.constructFile(outputFilePath, outputFileName);
			ExcelHelper.writeCompanyContactInfoToExcelFile(excelOutputFile, companyContactInformationList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}	
	
}
