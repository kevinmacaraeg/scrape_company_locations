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
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class SLLITContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(SLLITContactInfoScraper.class.getName());
	
	private static final String SLLIT_ABOUT_URL = "http://www.sliit.lk/sri-lanka-institute-of-information-technology/";
	
	private static final String SLLIT_WEBSITE_URL = "http://www.sliit.lk";
	
	private static final String SLLIT_CAMPUSES_LINKS_CSS_SELECTOR = "div.single-left-col>div.text>ul:last-of-type>li>h3>a";
	
	private static final String SLIIT_OFFICE_LOCATIONS_CSS_SELECTOR = "div.text";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			logger.info("Connecting to " + SLLIT_ABOUT_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(SLLIT_ABOUT_URL);
			Thread.sleep(5000);
			
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			logger.info("Getting location URLs......");
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements campusesLinksElements = websiteDocument.select(SLLIT_CAMPUSES_LINKS_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> locationURLs = JsoupHelper.getElementsHrefAttributes(campusesLinksElements);
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(SLLIT_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String locationURL : locationURLs) {

				logger.info("Connecting to " + locationURL + "......");
				driver = new ChromeDriver();
				driver.get(locationURL);
				
				Thread.sleep(3000);
				
				html_content = driver.getPageSource();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				logger.info("Selecting elements with contact information......");
				Elements contactInfoElements = websiteDocument.select(SLIIT_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				logger.info("Getting contact information......");
				for (Element element : contactInfoElements) {
					String contactInfo = element.text();
					String[] contactInfoSplit = contactInfo.split("Contact Information");
					contactInfo = contactInfoSplit[contactInfoSplit.length - 1];
					CompanyContactInformation companyContactInformation = service.parseContactInformation(masterCompanyId, masterCompanyName, locationURL, contactInfo);
					if (companyContactInformation != null) {
						companyContactInformationList.add(companyContactInformation);
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
