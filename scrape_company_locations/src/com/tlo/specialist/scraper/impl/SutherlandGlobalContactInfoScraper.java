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

public class SutherlandGlobalContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(SutherlandGlobalContactInfoScraper.class.getName());
	
	private static final String SUTHERLAND_GLOBAL_LOCATIONS_URL = "http://www.sutherlandglobal.com/Sutherland-Locations.aspx";
	
	private static final String SUTHERLAND_GLOBAL_COUNTRY_LINKS_CSS_SELECTOR = "div.pBody>div.row>div.col-sm-9.col-sm-push-3>div.row>div.col-sm-3>ul.list-unstyled>li>a";
	
	private static final String SUTHERLAND_GLOBAL_MODAL_CLOSE_BUTTON_CSS_SELECTOR = "div.modal-footer>button.btn.btn-default";
	
	private static final String SUTHERLAND_GLOBAL_OFFICE_LOCATIONS_CSS_SELECTOR = "div#myModal>div.modal-dialog.modal-lg>div.modal-content>div.modal-body>div#divPanel>div.row>div.col-sm-7.col-sm-push-5>ul.list-unstyled";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + SUTHERLAND_GLOBAL_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(SUTHERLAND_GLOBAL_LOCATIONS_URL);
			Thread.sleep(3000);
			
			List<WebElement> countryLinksElements = driver.findElements(By.cssSelector(SUTHERLAND_GLOBAL_COUNTRY_LINKS_CSS_SELECTOR));
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			
			logger.info("Scraping available company contact information......");
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (WebElement countryLinkElement : countryLinksElements) {
				
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", countryLinkElement);
				Thread.sleep(3000);
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(SUTHERLAND_GLOBAL_OFFICE_LOCATIONS_CSS_SELECTOR);
				Set<String> fullCompanyContactInfoSetPerCountry = JsoupHelper.getElementsTextToSet(officeLocationsElements);
				fullCompanyContactInfoSet.addAll(fullCompanyContactInfoSetPerCountry);
				
				WebElement modalCloseButtonElement = driver.findElement(By.cssSelector(SUTHERLAND_GLOBAL_MODAL_CLOSE_BUTTON_CSS_SELECTOR));
				
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", modalCloseButtonElement);
				Thread.sleep(1000);
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, SUTHERLAND_GLOBAL_LOCATIONS_URL, fullCompanyContactInfoSet);
			
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
