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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.domain.CompanyLocationsDetail;
import com.tlo.specialist.util.CompanyContactInfoParser;
import com.tlo.specialist.util.CompanyContactInfoParserFactory;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;

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
					companyContactInformationList = scrapeCompanyContactInfoByLinkAndCssSelector(masterCompanyId, masterCompanyName, companyLocationsUrl, cssSelector);
					
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
	
	public List<CompanyContactInformation> scrapeCompanyContactInfoByLinkAndCssSelector(String masterCompanyId, String masterCompanyName, String link, String cssSelector) throws Exception {
		List<CompanyContactInformation> companyContactInformationList = null;
		try {
			
			logger.info("Connecting to " + link + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(link);
			Thread.sleep(5000);
			
			String html_content = driver.getPageSource();
			
			driver.close();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			logger.info("Selecting elements with contact information......");
			Elements contactInfoElements = websiteDocument.select(cssSelector);
			
			logger.info("Getting contact information......");
			Set<String> fullContactInformationSet = new HashSet<String>();
			for (Element element : contactInfoElements) {
				String contactInfo = element.text();
				fullContactInformationSet.add(contactInfo);
			}
			
			logger.info("Number of retrieved contact information :: " + fullContactInformationSet.size());
			
			logger.info("Parsing each company contact information retrieved......");
			companyContactInformationList = parseContactInformation(masterCompanyId, masterCompanyName, link, fullContactInformationSet);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return companyContactInformationList;
	}
	
	public List<CompanyContactInformation> scrapeCompanyContactInfoByLinkAndCssSelector(String masterCompanyId, String masterCompanyName, List<String> websiteLocationLinksList, String cssSelector) throws Exception {
		List<CompanyContactInformation> companyContactInformationList = null;
		try {
			companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String link : websiteLocationLinksList) {

				logger.info("Connecting to " + link + "......");
				WebDriver driver = new ChromeDriver();
				driver.get(link);
				Thread.sleep(5000);
				
				String html_content = driver.getPageSource();
				
				driver.close();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				logger.info("Selecting elements with contact information......");
				Elements contactInfoElements = websiteDocument.select(cssSelector);
				
				logger.info("Getting contact information......");
				for (Element element : contactInfoElements) {
					String contactInfo = element.text();
					CompanyContactInformation companyContactInformation = parseContactInformation(masterCompanyId, masterCompanyName, link, contactInfo);
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

//	public void scrapeCompanyContactInfoToFileByLinkAndCssSelector(String outputFilePath, String outputFileName, String link, String cssSelector) throws Exception {
//		try {
//			
//			logger.info("Connecting to " + link + "......");
//			WebDriver driver = new ChromeDriver();
//			driver.get(link);
//			Thread.sleep(5000);
//			
//			String html_content = driver.getPageSource();
//			
//			driver.close();
//			
//			Document websiteDocument = Jsoup.parse(html_content);
//			
//			logger.info("Selecting elements with contact information......");
//			Elements contactInfoElements = websiteDocument.select(cssSelector);
//			
//			logger.info("Getting contact information......");
//			Set<String> fullContactInformationSet = new HashSet<String>();
//			for (Element element : contactInfoElements) {
//				String contactInfo = element.text();
//				fullContactInformationSet.add(contactInfo);
//			}
//			
//			logger.info("Number of retrieved contact information :: " + fullContactInformationSet.size());
//			
//			logger.info("Parsing each company contact information retrieved......");
//			List<CompanyContactInformation> parsedCompanyContactInformationList = parseContactInformation(link, fullContactInformationSet);
//			
//			logger.info("Writing contact information to file......");
//			File excelFile = FileHelper.constructFile(outputFilePath, outputFileName);
//			ExcelHelper.writeCompanyContactInfoToExcelFile(excelFile, parsedCompanyContactInformationList);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		}
//	}
	
//	public void scrapeCompanyContactInfoToFileByInputFile(String outputFilePath, String outputFileName, String inputExcelFilePath) throws Exception {
//		try {
//			logger.info("Getting inputs from file......");
//			File excelInputFile = new File(inputExcelFilePath);
//			Map<String, String> linkCssSelectorMap = ExcelHelper.getScrapeContactInfoInputsFromFile(excelInputFile);
//			
//			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
//			for (String websiteLink : linkCssSelectorMap.keySet()) {
//				
//				String cssSelector = linkCssSelectorMap.get(websiteLink);
//				
//				//logger.info("Connecting to " + websiteLink + "......");
//				Document websiteDocument = JsoupHelper.getWebsiteDocument(websiteLink);
//				
//				//logger.info("Selecting elements with contact information......");
//				Elements contactInfoElements = websiteDocument.select(cssSelector);
//				
//				//logger.info("Getting contact information......");
//				Set<String> fullContactInformationSet = new HashSet<String>();
//				for (Element element : contactInfoElements) {
//					String contactInfo = element.text();
//					fullContactInformationSet.add(contactInfo);
//				}
//				
//				//logger.info("Parsing each company contact information retrieved......");
//				List<CompanyContactInformation> parsedCompanyContactInformationList = parseContactInformation(websiteLink, fullContactInformationSet);
//				companyContactInformationList.addAll(parsedCompanyContactInformationList);
//			}
//			
//			logger.info("Number of retrieved contact information :: " + companyContactInformationList.size());
//			
//			File excelOutputFile = FileHelper.constructFile(outputFilePath, outputFileName);
//			ExcelHelper.writeCompanyContactInfoToExcelFile(excelOutputFile, companyContactInformationList);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		}
//	}
	
	public CompanyContactInformation parseContactInformation(String masterCompanyId, String masterCompanyName, String websiteLink, String fullCompanyContactInformation) throws Exception {
		CompanyContactInformation companyContactInformation = null;
		try {
			
			CompanyContactInfoParser parser = CompanyContactInfoParserFactory.getParser(websiteLink);
			if (parser != null) {
				companyContactInformation = parser.parseCompanyContactInformation(masterCompanyId, masterCompanyName, websiteLink, fullCompanyContactInformation);
			} else {
				String errorMessage = "No existing parser yet for " + websiteLink + "!";
				throw new Exception(errorMessage);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return companyContactInformation;
	}
	
	public List<CompanyContactInformation> parseContactInformation(String masterCompanyId, String masterCompanyName, String websiteLink, Set<String> fullCompanyContactInformationSet) throws Exception {
		List<CompanyContactInformation> companyContactInformationList = null;
		try {
			
			CompanyContactInfoParser parser = CompanyContactInfoParserFactory.getParser(websiteLink);
			if (parser != null) {
				companyContactInformationList = parser.parseCompanyContactInformation(masterCompanyId, masterCompanyName, websiteLink, fullCompanyContactInformationSet);
			} else {
				String errorMessage = "No existing parser yet for " + websiteLink + "!";
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
}
