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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class AgilisysContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(OnePagedClickLinksToDisplayContactInfoScraper.class.getName());
	
	private static final String AGILLISYS_OUR_OFFICES_URL = "https://www.agilisys.co.uk/about/our-offices";
	
	private static final String AGILISYS_LOCATION_PARENT_LINKS_CSS_SELECTOR = "ul.side-page-menu.pure-offset-sm-21-24.pure-offset-lg-19-24>li:last-of-type>ul.side-page-menu--submenu>li>a";
	
	private static final String AGILISYS_LOCATION_CHILDREN_LINKS_CSS_SELECTOR = "ul.side-page-menu.pure-offset-sm-21-24.pure-offset-lg-19-24>li:last-of-type>ul.side-page-menu--submenu>li.parent-submenu--header.active>ul.side-page-menu--submenu>li>a";
	
	private static final String AGILISYS_OFFICE_LOCATIONS_CSS_SELECTOR = "div.office-details";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			logger.info("Connecting to " + AGILLISYS_OUR_OFFICES_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(AGILLISYS_OUR_OFFICES_URL);
			Thread.sleep(5000);
			
			logger.info("Getting location links......");
			List<WebElement> parentLocationLinksElements = driver.findElements(By.cssSelector(AGILISYS_LOCATION_PARENT_LINKS_CSS_SELECTOR));
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			
			Set<String> fullContactInformationSet = new HashSet<String>();
			for (WebElement parentLocationLinkElement : parentLocationLinksElements) {
				
				logger.info("Clicking a parent link......");
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", parentLocationLinkElement);
				Thread.sleep(3000);
				
				try {
					
					List<WebElement> childrenLocationLinksElements = driver.findElements(By.cssSelector(AGILISYS_LOCATION_CHILDREN_LINKS_CSS_SELECTOR));
					
					for (WebElement childLocationLinkElement : childrenLocationLinksElements) {
							
						logger.info("Clicking a child link......");
						js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", childLocationLinkElement);
						Thread.sleep(3000);
						
						String html_content = driver.getPageSource();
						
						Document websiteDocument = Jsoup.parse(html_content);
						
						logger.info("Selecting elements with contact information......");
						Elements contactInfoElements = websiteDocument.select(AGILISYS_OFFICE_LOCATIONS_CSS_SELECTOR);
						
						logger.info("Getting contact information......");
						Set<String> fullContactInformationSetPerLink = JsoupHelper.getElementsTextToSet(contactInfoElements);
						fullContactInformationSet.addAll(fullContactInformationSetPerLink);
						
					}
				
				} catch (NoSuchElementException e) {
					//continue
				}
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				logger.info("Selecting elements with contact information......");
				Elements contactInfoElements = websiteDocument.select(AGILISYS_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				logger.info("Getting contact information......");
				Set<String> fullContactInformationSetPerLink = JsoupHelper.getElementsTextToSet(contactInfoElements);
				fullContactInformationSet.addAll(fullContactInformationSetPerLink);
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing each company contact information retrieved......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, AGILLISYS_OUR_OFFICES_URL, fullContactInformationSet);
			
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
