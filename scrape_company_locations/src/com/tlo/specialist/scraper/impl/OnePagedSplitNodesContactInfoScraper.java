package com.tlo.specialist.scraper.impl;

import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.JsoupHelper;
import com.tlo.specialist.util.StringHelper;

public class OnePagedSplitNodesContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(OnePagedSplitNodesContactInfoScraper.class.getName());
	
	private static String centralLocationsURL;
	
	private static String officeLocationsCssSelector;
	
	private static String splitNodesBy;
	
	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {

			init(masterCompanyId);
			
			logger.info("Connecting to " + centralLocationsURL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(centralLocationsURL);
			Thread.sleep(3000);
			
			logger.info("Scraping all available company contact information......");
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements officeLocationsElements = websiteDocument.select(officeLocationsCssSelector);
			
			Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSetBySplittingNodes(officeLocationsElements, splitNodesBy);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, centralLocationsURL, fullCompanyContactInfoSet);
			
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
			URL url = Thread.currentThread().getContextClassLoader().getResource("properties/onePagedSplitNodesProperties.properties");
			systemProperties.load(url.openStream());
			
			centralLocationsURL = systemProperties.getProperty(masterCompanyId + ".central.locations.url").trim();
			if (StringHelper.isEmpty(centralLocationsURL)) {
				throw new Exception(masterCompanyId + ".central.locations.url" + " property is missing. Please update properties file!");
			}
			
			officeLocationsCssSelector = systemProperties.getProperty(masterCompanyId + ".office.locations.css.selector").trim();
			if (StringHelper.isEmpty(officeLocationsCssSelector)) {
				throw new Exception(masterCompanyId + ".office.locations.css.selector" + " property is missing. Please update properties file!");
			}
			
			splitNodesBy = systemProperties.getProperty(masterCompanyId + ".split.nodes.by").trim();
			if (StringHelper.isEmpty(splitNodesBy)) {
				throw new Exception(masterCompanyId + ".split.nodes.by" + " property is missing. Please update properties file!");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

}
