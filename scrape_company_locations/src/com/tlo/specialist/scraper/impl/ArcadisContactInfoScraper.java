package com.tlo.specialist.scraper.impl;

import java.io.File;
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

public class ArcadisContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(ArcadisContactInfoScraper.class.getName());
	
	private static final String ARCADIS_WHERE_WE_WORK_URL = "https://www.arcadis.com/en/global/where-we-work/";
	
	private static final String ARCADIS_ACCEPT_COOKIE_BUTTON_CSS_SELECTOR = "div.cookie-info>div.buttons>a.btn-primary.btn-arrow-right-white.cookie-accept";
	
	private static final String ARCADIS_LIST_VIEW_BUTTON_CSS_SELECTOR = "div.view-controls>a.view-controls__list";
	
	private static final String ARCADIS_ALL_COUNTRIES_OPTION_CSS_SELECTOR = "ul.dropdown-content.select-dropdown>li:nth-of-type(1)>span";
	
	private static final String ARCADIS_OFFICE_LOCATIONS_CSS_SELECTOR = "div.search-result__office";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + ARCADIS_WHERE_WE_WORK_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(ARCADIS_WHERE_WE_WORK_URL);
			Thread.sleep(3000);
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			
			WebElement acceptCookieButtonElement = driver.findElement(By.cssSelector(ARCADIS_ACCEPT_COOKIE_BUTTON_CSS_SELECTOR));
			
			js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", acceptCookieButtonElement);
			Thread.sleep(1000);
			
			WebElement listViewButtonElement = driver.findElement(By.cssSelector(ARCADIS_LIST_VIEW_BUTTON_CSS_SELECTOR));
			
			js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", listViewButtonElement);
			Thread.sleep(1000);
			
			WebElement allCountriesOptionElement = driver.findElement(By.cssSelector(ARCADIS_ALL_COUNTRIES_OPTION_CSS_SELECTOR));
				
			js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", allCountriesOptionElement);
			Thread.sleep(1000);
			
			logger.info("Scraping available company contact information......");
			String html_content = driver.getPageSource();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements officeLocationsElements = websiteDocument.select(ARCADIS_OFFICE_LOCATIONS_CSS_SELECTOR);
			
			Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing scraped contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, ARCADIS_WHERE_WE_WORK_URL, fullCompanyContactInfoSet);
			
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
