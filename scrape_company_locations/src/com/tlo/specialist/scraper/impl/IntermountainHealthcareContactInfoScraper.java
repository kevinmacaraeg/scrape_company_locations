package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.ArrayList;
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

public class IntermountainHealthcareContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(IntermountainHealthcareContactInfoScraper.class.getName());
	
	private static final String INTERMOUNTAIN_HEALTHCARE_LOCATIONS_URL = "https://intermountainhealthcare.org/locations/search-results/?locationtype=hospital";
	
	private static final String INTERMOUNTAIN_HEALTHCARE_NEXT_BUTTON_LINK_TEXT = "Next";
	
	private static final String INTERMOUNTAIN_HEALTHCARE_OFFICE_LOCATIONS_CSS_SELECTOR = "div.card-location__meta";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + INTERMOUNTAIN_HEALTHCARE_LOCATIONS_URL +"......");
			WebDriver driver = new ChromeDriver();
			driver.get(INTERMOUNTAIN_HEALTHCARE_LOCATIONS_URL);
			Thread.sleep(3000);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
				
			logger.info("Scraping available contact information......");
			boolean hasNextPage = true;
			int pageNumber = 1;
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			do {
				logger.info("Scraping contact information from Page " + pageNumber + "......");
				WebElement nextPageButtonElement = null;
				try {
					nextPageButtonElement = driver.findElement(By.linkText(INTERMOUNTAIN_HEALTHCARE_NEXT_BUTTON_LINK_TEXT));
				} catch (NoSuchElementException e) {
					hasNextPage = false;
				}
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(INTERMOUNTAIN_HEALTHCARE_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
				
				List<CompanyContactInformation> companyContactInformationListPerPage = service.parseContactInformation(masterCompanyId, masterCompanyName, driver.getCurrentUrl(), fullCompanyContactInfoSet);
				companyContactInformationList.addAll(companyContactInformationListPerPage);
				
				if (hasNextPage) {
					String currentURL = driver.getCurrentUrl();
					if (nextPageButtonElement.isDisplayed() && nextPageButtonElement.isEnabled()) {
						JavascriptExecutor js = (JavascriptExecutor) driver;
						js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", nextPageButtonElement);
						Thread.sleep(5000);
						if (currentURL.equals(driver.getCurrentUrl())) {
							hasNextPage = false;
						} else {
							pageNumber++;
						}
					} else {
						hasNextPage = false;
					}
				}
			} while (hasNextPage);
				
			driver.quit();
			
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
