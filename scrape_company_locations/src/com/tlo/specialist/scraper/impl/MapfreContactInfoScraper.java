package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class MapfreContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(MapfreContactInfoScraper.class.getName());
	
	private static final String MAPFRE_EN_EL_MUNDO_URL = "https://www.mapfre.com/corporativo-es/acerca-mapfre/mapfre-en-el-mundo/";
	
	private static final String MAPFRE_OFFICE_LOCATIONS_CSS_SELECTOR = "div#map-list>ul>li>div.detail>p";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + MAPFRE_EN_EL_MUNDO_URL +"......");
			WebDriver driver = new ChromeDriver();
			driver.manage().window().setSize(new Dimension(500, 500));
			driver.get(MAPFRE_EN_EL_MUNDO_URL);
			Thread.sleep(3000);
			
			logger.info("Scraping available company contact information......");
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements officeLocationsElements = websiteDocument.select(MAPFRE_OFFICE_LOCATIONS_CSS_SELECTOR);
			
			Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
				
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, MAPFRE_EN_EL_MUNDO_URL, fullCompanyContactInfoSet);
			
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
