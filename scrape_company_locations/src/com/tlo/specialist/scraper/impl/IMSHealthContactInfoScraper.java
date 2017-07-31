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
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;

public class IMSHealthContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(IMSHealthContactInfoScraper.class.getName());
	
	private static final String IMS_HEALTH_WEBSITE_URL = "http://www.imshealth.com/en";
	
	private static final String IMS_HEALTH_CONTACT_US_BUTTON_CSS_SELECTOR = "div.top-nav>ul.links-wrapper.cf>li.menu-item:nth-of-type(3)>a.toggler";
	
	private static final String IMS_HEALTH_OFFICE_LOCATIONS_CSS_SELECTOR = "div.results-wrapper.wrapper.contact-offices-list>div.results>ul#browseAllContactDrawer>li>div>ul>li.itm-result.location-stub";
	
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + IMS_HEALTH_WEBSITE_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(IMS_HEALTH_WEBSITE_URL);
			Thread.sleep(3000);
			
			WebElement contactUsButtonElement = driver.findElement(By.cssSelector(IMS_HEALTH_CONTACT_US_BUTTON_CSS_SELECTOR));
			
			contactUsButtonElement.click();
			Thread.sleep(3000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			logger.info("Getting contact information......");
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements officeLocationsElements = websiteDocument.select(IMS_HEALTH_OFFICE_LOCATIONS_CSS_SELECTOR);
			
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (Element officeLocationElement : officeLocationsElements) {
				fullCompanyContactInfoSet.add(officeLocationElement.text());
			}
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing scraped contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, IMS_HEALTH_WEBSITE_URL, fullCompanyContactInfoSet);

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
