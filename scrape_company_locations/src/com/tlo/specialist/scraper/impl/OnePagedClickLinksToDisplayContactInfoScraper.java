package com.tlo.specialist.scraper.impl;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
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
import com.tlo.specialist.util.JsoupHelper;
import com.tlo.specialist.util.StringHelper;

public class OnePagedClickLinksToDisplayContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(OnePagedClickLinksToDisplayContactInfoScraper.class.getName());
	
	private static String centralLocationsURL;
	
	private static String linksToBeClickedCssSelector;
	
	private static String officeLocationsCssSelector;

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			init(masterCompanyId);
			
			logger.info("Connecting to " + centralLocationsURL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(centralLocationsURL);
			Thread.sleep(5000);
			
			logger.info("Getting links to be clicked......");
			List<WebElement> linksToBeClickedElements = driver.findElements(By.cssSelector(linksToBeClickedCssSelector));
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			
			Set<String> fullContactInformationSet = new HashSet<String>();
			for (WebElement linkToBeClickedElement : linksToBeClickedElements) {
				
				logger.info("Clicking a link......");
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", linkToBeClickedElement);
				Thread.sleep(3000);
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				logger.info("Selecting elements with contact information......");
				Elements contactInfoElements = websiteDocument.select(officeLocationsCssSelector);
				
				logger.info("Getting contact information......");
				Set<String> fullContactInformationSetPerLink = JsoupHelper.getElementsTextToSet(contactInfoElements);
				fullContactInformationSet.addAll(fullContactInformationSetPerLink);
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing each company contact information retrieved......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, centralLocationsURL, fullContactInformationSet);
			
			logger.info("Writing contact information to file......");
			service.writeCompanyContactInformationToFile(masterCompanyName, outputFilePath, companyContactInformationList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	void init(String masterCompanyId) throws Exception {
		try {
			
			Properties systemProperties = new Properties();
			URL url = Thread.currentThread().getContextClassLoader().getResource("properties/onePagedClickLinksToDisplayProperties.properties");
			systemProperties.load(url.openStream());
			
			centralLocationsURL = systemProperties.getProperty(masterCompanyId + ".central.locations.url").trim();
			if (StringHelper.isEmpty(centralLocationsURL)) {
				throw new Exception(masterCompanyId + ".central.locations.url" + " property is missing. Please update properties file!");
			}
			
			linksToBeClickedCssSelector = systemProperties.getProperty(masterCompanyId + ".links.css.selector").trim();
			if (StringHelper.isEmpty(linksToBeClickedCssSelector)) {
				throw new Exception(masterCompanyId + ".links.css.selector" + " property is missing. Please update properties file!");
			}
			
			officeLocationsCssSelector = systemProperties.getProperty(masterCompanyId + ".office.locations.css.selector").trim();
			if (StringHelper.isEmpty(officeLocationsCssSelector)) {
				throw new Exception(masterCompanyId + ".office.locations.css.selector" + " property is missing. Please update properties file!");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
}
