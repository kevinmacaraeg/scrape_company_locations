package com.tlo.specialist.service;

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
import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.domain.CompanyLocationsDetail;
import com.tlo.specialist.parser.CompanyContactInfoParser;
import com.tlo.specialist.util.CompanyContactInfoParserFactory;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;
import com.tlo.specialist.util.StringHelper;

public class ScrapeCompanyContactInfoService {
	
	Logger logger = Logger.getLogger(ScrapeCompanyContactInfoService.class.getName());
	
	public void scrapeCompanyContactInfoByCompanyLocationsDetailsFromFile(String companyLocationsDetailsFilePath, String sheetName, String outputFilePath) throws Exception {
		try {
			List<CompanyLocationsDetail> companyLocationsDetailsList = ExcelHelper.getCompanyLocationsDetailsListFromFile(companyLocationsDetailsFilePath, sheetName);
			
			for (CompanyLocationsDetail companyLocationsDetail : companyLocationsDetailsList) {
				String masterCompanyId = companyLocationsDetail.getMasterCompanyId();
				String masterCompanyName = companyLocationsDetail.getMasterCompanyName();
				logger.info("********** Scraping locations of " + masterCompanyName + "**********");
				String companyLocationsUrl = companyLocationsDetail.getCompanyLocationsUrl();
				String cssSelector = companyLocationsDetail.getCompanyLocationsCssSelector();
				String outputFileName = getOutputFileName(masterCompanyName);
				List<CompanyContactInformation> companyContactInformationList = null;
				try {
					companyContactInformationList = scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, companyLocationsUrl, cssSelector);
					
					logger.info("Writing contact information to file......");
					File excelFile = FileHelper.constructFile(outputFilePath, outputFileName);
					ExcelHelper.writeCompanyContactInfoToExcelFile(excelFile, companyContactInformationList);
				} catch (Exception e) {
					logger.error(e.getMessage());
					logger.error("Encountered an error in scraping locations of " + masterCompanyName + "!");
				}
				
				logger.info("********** Done scraping locations of " + masterCompanyName + "**********");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	public List<CompanyContactInformation> scrapeCompanyContactInfoByURLAndCssSelector(String masterCompanyId, String masterCompanyName, String locationURL, String cssSelector) throws Exception {
		List<CompanyContactInformation> companyContactInformationList = null;
		try {
			
			logger.info("Connecting to " + locationURL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(locationURL);
			Thread.sleep(12000);
			
			String html_content = driver.getPageSource();
			
			driver.close();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			logger.info("Selecting elements with contact information......");
			Elements contactInfoElements = websiteDocument.select(cssSelector);
			
			logger.info("Getting contact information......");
			Set<String> fullContactInformationSet = JsoupHelper.getElementsTextToSet(contactInfoElements);
			
			logger.info("Number of retrieved contact information :: " + fullContactInformationSet.size());
			
			logger.info("Parsing each company contact information retrieved......");
			companyContactInformationList = parseContactInformation(masterCompanyId, masterCompanyName, locationURL, fullContactInformationSet);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return companyContactInformationList;
	}
	
	public List<CompanyContactInformation> scrapeCompanyContactInfoByURLAndCssSelector(String masterCompanyId, String masterCompanyName, Set<String> websiteLocationURLsSet, String cssSelector) throws Exception {
		List<CompanyContactInformation> companyContactInformationList = null;
		try {
			companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String locationURL : websiteLocationURLsSet) {

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
				Elements contactInfoElements = websiteDocument.select(cssSelector);
				
				logger.info("Getting contact information......");
				for (Element element : contactInfoElements) {
					String contactInfo = element.text();
					CompanyContactInformation companyContactInformation = parseContactInformation(masterCompanyId, masterCompanyName, locationURL, contactInfo);
					if (companyContactInformation != null) {
						companyContactInformationList.add(companyContactInformation);
					}
				}
				
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return companyContactInformationList;
	}
	
	public Set<String> scrapeLinksURLsByCssSelector(String linksListPageURL, String linksCssSelector, String linkAttributeForURL) throws Exception {
		Set<String> linksURLs = null;
		try {
			
			logger.info("Connecting to " + linksListPageURL  + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(linksListPageURL);
			Thread.sleep(5000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements linksElements = websiteDocument.select(linksCssSelector);
			
			linksURLs = JsoupHelper.getElementsAttributeValue(linkAttributeForURL, linksElements);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return linksURLs;
	}
	
	public CompanyContactInformation parseContactInformation(String masterCompanyId, String masterCompanyName, String websiteURL, String fullCompanyContactInformation) throws Exception {
		CompanyContactInformation companyContactInformation = null;
		try {
			
			CompanyContactInfoParser parser = CompanyContactInfoParserFactory.getParser(websiteURL);
			if (parser != null) {
				companyContactInformation = parser.parseCompanyContactInformation(masterCompanyId, masterCompanyName, websiteURL, fullCompanyContactInformation);
			} else {
				String errorMessage = "No existing parser yet for " + websiteURL + "!";
				throw new Exception(errorMessage);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return companyContactInformation;
	}
	
	public List<CompanyContactInformation> parseContactInformation(String masterCompanyId, String masterCompanyName, String websiteURL, Set<String> fullCompanyContactInformationSet) throws Exception {
		List<CompanyContactInformation> companyContactInformationList = null;
		try {
			
			CompanyContactInfoParser parser = CompanyContactInfoParserFactory.getParser(websiteURL);
			if (parser != null) {
				companyContactInformationList = parser.parseCompanyContactInformation(masterCompanyId, masterCompanyName, websiteURL, fullCompanyContactInformationSet);
			} else {
				String errorMessage = "No existing parser yet for " + websiteURL + "!";
				throw new Exception(errorMessage);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return companyContactInformationList;
	}
	
	public String getOutputFileName(String masterCompanyName) {
		return masterCompanyName.replaceAll(Constants.REGEX_NON_ALPHANUMERIC_OR_SPACE, Constants.EMPTY_STRING).replace(Constants.SPACE, Constants.UNDERSCORE) + "_Locations.xlsx";
	}
	
	public void writeCompanyContactInformationToFile(String masterCompanyName, String outputFilePath, List<CompanyContactInformation> companyContactInformationList) throws Exception {
		try {
			String outputFileName = getOutputFileName(masterCompanyName);
			File excelFile = FileHelper.constructFile(outputFilePath, outputFileName);
			ExcelHelper.writeCompanyContactInfoToExcelFile(excelFile, companyContactInformationList);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("An error encountered when writing the company contact information to file!");
			throw new Exception(e.getMessage());
		}
	}
	
	public Set<String> prependWebsiteURLIfCurrentURLsHaveNoProtocol(String websiteURL, Set<String> currentURLs) throws Exception {		
		Set<String> newCurrentURLs = null;
		try {
			newCurrentURLs = new HashSet<String>();
			for (String currentURL : currentURLs) {
				currentURL = currentURL.trim();
				if (currentURL.startsWith(Constants.FORWARD_SLASH) || (!currentURL.startsWith(Constants.LITERAL_HTTP) && !currentURL.startsWith(Constants.LITERAL_WWW))) {
					currentURL = websiteURL + StringHelper.addForwardSlashAtStart(currentURL);
				}
				newCurrentURLs.add(currentURL);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return newCurrentURLs;
	}
}
