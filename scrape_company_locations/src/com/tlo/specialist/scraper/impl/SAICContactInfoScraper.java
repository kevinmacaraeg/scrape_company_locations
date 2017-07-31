package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;

public class SAICContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(SAICContactInfoScraper.class.getName());
	
	private static final String SAIC_LOCATIONS_URL = "http://contacts.saic.com/loctblext2.nsf/Allstates?";
	
	private static final String SAIC_NEXT_PAGE_BUTTON_CSS_SELECTOR = "form>a:nth-of-type(2)";
	
	private static final String SAIC_OFFICE_LOCATIONS_CSS_SELECTOR = "div#locationTable>table>tbody>tr>td>font";

	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + SAIC_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(SAIC_LOCATIONS_URL);
			Thread.sleep(3000);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			
			boolean hasNextPage = true;
			int pageNumber = 1;
			String previousElementsText = Constants.EMPTY_STRING;
			do {
				
				logger.info("Scraping company contact information for Page " + pageNumber + "......");
				WebElement nextPageLinkElement = null;
				try {
					nextPageLinkElement = driver.findElement(By.cssSelector(SAIC_NEXT_PAGE_BUTTON_CSS_SELECTOR));
				} catch (NoSuchElementException e) {
					hasNextPage = false;
				}
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationElements = websiteDocument.select(SAIC_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				String currentElementsText = officeLocationElements.text();
				
				if (!previousElementsText.equalsIgnoreCase(currentElementsText)) {
					Set<String> fullCompanyContactInfoSet = new HashSet<String>();
					for (Element officeLocationElement : officeLocationElements) {
						fullCompanyContactInfoSet.add(officeLocationElement.text());
					}
					
					List<CompanyContactInformation> companyContactInformationListPerPage = service.parseContactInformation(masterCompanyId, masterCompanyName, driver.getCurrentUrl(), fullCompanyContactInfoSet);
					companyContactInformationList.addAll(companyContactInformationListPerPage);
				} else {
					hasNextPage = false;
				}
				
				if (hasNextPage) {
					previousElementsText = officeLocationElements.text();
					nextPageLinkElement.click();
					pageNumber++;
					Thread.sleep(1000);
				}
				
			} while (hasNextPage);
			
			driver.quit();
				
			logger.info("Writing company contacts to output file...");
			String outputFileName = service.getOutputFileName(masterCompanyName);
			File excelOutputFile = FileHelper.constructFile(outputFilePath, outputFileName);
			ExcelHelper.writeCompanyContactInfoToExcelFile(excelOutputFile, companyContactInformationList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}	
	
}
