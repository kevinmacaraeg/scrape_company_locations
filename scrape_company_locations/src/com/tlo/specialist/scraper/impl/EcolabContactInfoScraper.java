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

public class EcolabContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(EcolabContactInfoScraper.class.getName());
	
	private static final String ECOLAB_LOCATIONS_URL = "http://www.ecolab.com/about/locations";
	
	private static final String ECOLAB_WEBSITE_URL = "http://www.ecolab.com";
	
	private static final String ECOLAB_LOCATION_LINKS_CSS_SELECTOR = "div.coveo-result-frame.CoveoResult>div.coveo-result-row>div.coveo-result-cell>div.coveo-result-row>div.coveo-result-cell>a.CoveoResultLink";
	
	private static final String ECOLAB_NEXT_PAGE_BUTTON_CSS_SELECTOR = "div.CoveoPager>ul.coveo-pager-list>li.coveo-pager-next.coveo-pager-anchor.coveo-pager-list-item>a";
	
	private static final String ECOLAB_OFFICE_LOCATIONS_CSS_SELECTOR = "div.page-wrapper>div.container>div.score-column2.wide-left.locations>div.score-right";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			logger.info("Connecting to " + ECOLAB_LOCATIONS_URL  + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(ECOLAB_LOCATIONS_URL);
			Thread.sleep(10000);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Getting location URLs......");
			boolean hasNextButton = true;
			Set<String> locationURLs = new HashSet<String>();
			while (hasNextButton) {
				
				WebElement nextPageButtonElement = null;
				try {
					nextPageButtonElement = driver.findElement(By.cssSelector(ECOLAB_NEXT_PAGE_BUTTON_CSS_SELECTOR));
				} catch (NoSuchElementException e) {
					hasNextButton = false;
				}
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements locationLinksElements = websiteDocument.select(ECOLAB_LOCATION_LINKS_CSS_SELECTOR);
				
				Set<String> locationURLsPerPage = JsoupHelper.getElementsHrefAttributes(locationLinksElements);
				locationURLsPerPage = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(ECOLAB_WEBSITE_URL, locationURLsPerPage);
				locationURLs.addAll(locationURLsPerPage);
				
				if (hasNextButton) {
					nextPageButtonElement.click();
					Thread.sleep(3000);
				}
				
			}
			
			driver.quit();
			
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationURLs, ECOLAB_OFFICE_LOCATIONS_CSS_SELECTOR);
			
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
