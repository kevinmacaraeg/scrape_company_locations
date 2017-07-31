package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class AkerSolutionsContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(AkerSolutionsContactInfoScraper.class.getName());
	
	private static final String AKER_SOLUTIONS_LOCATIONS_URL = "http://akersolutions.com/contact/offices/?region=&country=&gotoResult=True";
	
	private static final String AKER_SOLUTIONS_OFFICE_LOCATIONS_CSS_SELECTOR = "ul.list-articles>li.list-articles-item>a>div";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + AKER_SOLUTIONS_LOCATIONS_URL +"......");
			WebDriver driver = new ChromeDriver();
			driver.get(AKER_SOLUTIONS_LOCATIONS_URL);
			Thread.sleep(3000);
			
			logger.info("Loading all available company contact information......");
			JavascriptExecutor jse = (JavascriptExecutor)driver;
		    for (int second = 0;; second++) {
		    	if (second >= 30) {
		    		break;
		    	}
		    	jse.executeScript("window.scrollBy(0, Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));", "");
		    	Thread.sleep(500);
		    }
		    
		    logger.info("Scraping available company contact information......");
		    String html_content = driver.getPageSource();
		    
		    driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements officeLocationsElements = websiteDocument.select(AKER_SOLUTIONS_OFFICE_LOCATIONS_CSS_SELECTOR);
			
			Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
				
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, AKER_SOLUTIONS_LOCATIONS_URL, fullCompanyContactInfoSet);
			
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
