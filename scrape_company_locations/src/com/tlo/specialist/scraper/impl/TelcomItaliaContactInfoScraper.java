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

public class TelcomItaliaContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(TelcomItaliaContactInfoScraper.class.getName());
	
	private static final String TIM_GEOGRAPHICAL_DISPERSION_URL = "http://www.telecomitalia.com/tit/en/about-us/geographical-dispersion.html";
	
	private static final String TIM_REGION_LINKS_CSS_SELECTOR = "ul.ti-general-tabNavLinks.ui-general-tabs-nav>li>a";
	
	private static final String TIM_COUNTRY_LINKS_CSS_SELECTOR = "div#ti-map>a";
	
	private static final String TIM_OFFICE_LOCATIONS_CSS_SELECTOR = "div.ti-map-description,div.ti-map-address";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + TIM_GEOGRAPHICAL_DISPERSION_URL +"......");
			WebDriver driver = new ChromeDriver();
			driver.get(TIM_GEOGRAPHICAL_DISPERSION_URL);
			Thread.sleep(3000);
			
			List<WebElement> regionLinksElements = driver.findElements(By.cssSelector(TIM_REGION_LINKS_CSS_SELECTOR));
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			
			logger.info("Scraping available company contact information......");
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (WebElement regionLinkElement : regionLinksElements) {
				
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", regionLinkElement);
				Thread.sleep(3000);
				
				List<WebElement> countryLinksElements = driver.findElements(By.cssSelector(TIM_COUNTRY_LINKS_CSS_SELECTOR));
				
				for (WebElement countryLinkElement : countryLinksElements) {
					
					js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", countryLinkElement);
					Thread.sleep(3000);
					
					String html_content = driver.getPageSource();
					
					Document websiteDocument = Jsoup.parse(html_content);
					
					Elements officeLocationsElements = websiteDocument.select(TIM_OFFICE_LOCATIONS_CSS_SELECTOR);
					
					Set<String> fullCompanyContactInfoSetPerCountry = JsoupHelper.getElementsTextToSet(officeLocationsElements);
					fullCompanyContactInfoSet.addAll(fullCompanyContactInfoSetPerCountry);
					
				}
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
				
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, TIM_GEOGRAPHICAL_DISPERSION_URL, fullCompanyContactInfoSet);
			
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
