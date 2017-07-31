package com.tlo.specialist.scraper.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.JsoupHelper;
import com.tlo.specialist.util.StringHelper;

public class MultiPagedOneLevelListOfLinksSplitByNodesContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(MultiPagedOneLevelListOfLinksSplitByNodesContactInfoScraper.class.getName());
	
	private static String centralLocationsURL;
	
	private static String websiteURL;
	
	private static String linksCssSelector;
	
	private static String linkAttributeForURL;
	
	private static String splitNodesBy;
	
	private static String officeLocationsCssSelector;
	
	private static Set<String> additionalURLsSet;

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			init(masterCompanyId);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Getting location URLs......");
			Set<String> locationURLs = service.scrapeLinksURLsByCssSelector(centralLocationsURL, linksCssSelector, linkAttributeForURL);
			locationURLs.addAll(additionalURLsSet);
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(websiteURL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String locationURL : locationURLs) {

				logger.info("Connecting to " + locationURL + "......");
				WebDriver driver = new ChromeDriver();
				driver.get(locationURL);
				
				try {
					WebDriverWait wait = new WebDriverWait(driver, 3);
					Alert alert = wait.until(ExpectedConditions.alertIsPresent());
		
					alert.accept();
				} catch (TimeoutException e) {
					//continue if there is no alert box
				}
				
				Thread.sleep(3000);
				
				String html_content = driver.getPageSource();
				
				driver.quit();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				logger.info("Selecting elements with contact information......");
				Elements contactInfoElements = websiteDocument.select(officeLocationsCssSelector);
				
				Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSetBySplittingNodes(contactInfoElements, splitNodesBy);
				
				logger.info("Parsing scraped company contact information......");
				List<CompanyContactInformation> companyContactInformationListPerURL = service.parseContactInformation(masterCompanyId, masterCompanyName, locationURL, fullCompanyContactInfoSet);
				companyContactInformationList.addAll(companyContactInformationListPerURL);
				
			}
			
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
			URL url = Thread.currentThread().getContextClassLoader().getResource("properties/multiPagedOneLevelListOfLinksSplitByNodesProperties.properties");
			systemProperties.load(url.openStream());
			
			centralLocationsURL = systemProperties.getProperty(masterCompanyId + ".central.locations.url").trim();
			if (StringHelper.isEmpty(centralLocationsURL)) {
				throw new Exception(masterCompanyId + ".central.locations.url" + " property is missing. Please update properties file!");
			}
			
			websiteURL = systemProperties.getProperty(masterCompanyId + ".website.url").trim();
			if (StringHelper.isEmpty(websiteURL)) {
				throw new Exception(masterCompanyId + ".website.url" + " property is missing. Please update properties file!");
			}
			
			linksCssSelector = systemProperties.getProperty(masterCompanyId + ".links.css.selector").trim();
			if (StringHelper.isEmpty(linksCssSelector)) {
				throw new Exception(masterCompanyId + ".links.css.selector" + " property is missing. Please update properties file!");
			}
			
			officeLocationsCssSelector = systemProperties.getProperty(masterCompanyId + ".office.locations.css.selector").trim();
			if (StringHelper.isEmpty(officeLocationsCssSelector)) {
				throw new Exception(masterCompanyId + ".office.locations.css.selector" + " property is missing. Please update properties file!");
			}
			
			linkAttributeForURL = systemProperties.getProperty(masterCompanyId + ".link.attr.for.url").trim();
			if (StringHelper.isEmpty(officeLocationsCssSelector)) {
				throw new Exception(masterCompanyId + ".link.attr.for.url" + " property is missing. Please update properties file!");
			}
			
			splitNodesBy = systemProperties.getProperty(masterCompanyId + ".split.nodes.by").trim();
			if (StringHelper.isEmpty(splitNodesBy)) {
				throw new Exception(masterCompanyId + ".split.nodes.by" + " property is missing. Please update properties file!");
			}
			
			String addtionalLocationURLs = systemProperties.getProperty(masterCompanyId + ".addtional.location.urls").trim();
			additionalURLsSet = new HashSet<String>();
			if (StringHelper.isNotEmpty(addtionalLocationURLs)) {
				String[] separateLocationURLs = addtionalLocationURLs.split(Constants.SPACE);
				for (String separateLocationURL : separateLocationURLs) {
					additionalURLsSet.add(separateLocationURL);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

}
