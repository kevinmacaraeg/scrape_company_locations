package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class MercerContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(MercerContactInfoScraper.class.getName());
	
	private static final String MERCER_OFFICE_LOCATIONS_URL = "https://www.mercer.com/about-us/locations.html";
	
	private static final String MERCER_LOCATION_LINKS_CSS_SELECTOR = "section.small-12.default.row.mrc-hideregion>section>section>section>section>ul>li>a";
	
	private static final String MERCER_MODAL_CLOSE_BUTTON_CSS_SELECTOR = "article.modal>a.close";
	
	private static final String MERCER_OFFICE_LOCATIONS_CSS_SELECTOR = "div.gm-style-iw>div>div>table>tbody";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + MERCER_OFFICE_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(MERCER_OFFICE_LOCATIONS_URL);
			Thread.sleep(3000);
			
			logger.info("Scraping available company contact information......");	
				
			List<WebElement> locationLinksElements = driver.findElements(By.cssSelector(MERCER_LOCATION_LINKS_CSS_SELECTOR));
			
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();	
			for (WebElement locationLinkElement : locationLinksElements) {
				
				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", locationLinkElement);
				Thread.sleep(3000);
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(MERCER_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				Set<String> fullCompanyContactInfoSetPerLocation = JsoupHelper.getElementsTextToSet(officeLocationsElements);
				fullCompanyContactInfoSet.addAll(fullCompanyContactInfoSetPerLocation);
				
				WebElement closeButtonElement = driver.findElement(By.cssSelector(MERCER_MODAL_CLOSE_BUTTON_CSS_SELECTOR));
				
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", closeButtonElement);
				Thread.sleep(1000);
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, MERCER_OFFICE_LOCATIONS_URL, fullCompanyContactInfoSet);
			
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
