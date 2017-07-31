package com.tlo.specialist.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyDetail;
import com.tlo.specialist.domain.CompanySubsidiary;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;
import com.tlo.specialist.util.StringHelper;

public class ScrapeCompanySubsidiariesService {
	
	private static Logger logger = Logger.getLogger(ScrapeCompanySubsidiariesService.class.getName());
	
	private static final String GOOGLE_WEBSITE_URL = "https://www.google.com";
	
	private static final String GOOGLE_SEARCH_TEXTBOX_CSS_SELECTOR = "input#lst-ib";
	
	private static final String GOOGLE_SEARCH_RESULTS_CSS_SELECTOR = "div._NId>div.srg>div.g>div>div>h3>a";
	
	private static final String COMPANY_SUBSIDIARIES_ELEMENTS_CSS_SELECTOR = "table>tbody>tr";

	public void scrapeCompanySubsidiaries(String companyDetailsFilePath, String sheetName, String outputFilePath) throws Exception {
		try {
			List<CompanyDetail> companyDetailsListFromFile = ExcelHelper.getCompanyDetailsListFromFile(companyDetailsFilePath, sheetName);
			
			for (CompanyDetail companyDetail : companyDetailsListFromFile) {
				
				String masterCompanyId = companyDetail.getMasterCompanyId();
				String masterCompanyName = companyDetail.getMasterCompanyName();
				
				logger.info("******** Scraping Company Subsidiaries of " + masterCompanyName + " ******");
				
				logger.info("Connecting to " + GOOGLE_WEBSITE_URL + "......");
				WebDriver driver = new ChromeDriver();
				driver.get(GOOGLE_WEBSITE_URL);
				Thread.sleep(3000);
				
				logger.info("Searching for company's subsidiaries via Google search......");
				WebElement searchTextboxElement = driver.findElement(By.cssSelector(GOOGLE_SEARCH_TEXTBOX_CSS_SELECTOR));
				
				searchTextboxElement.sendKeys(masterCompanyName + " Subsidiaries SEC");
				searchTextboxElement.submit();
				Thread.sleep(5000);
				
				String html_content = driver.getPageSource();
				
				driver.quit();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements searchResultsElements = websiteDocument.select(GOOGLE_SEARCH_RESULTS_CSS_SELECTOR);
				
				Set<String> dataHrefSet = JsoupHelper.getElementsHrefAttributes(searchResultsElements);
				
				List<CompanySubsidiary> companySubsidiariesList = new ArrayList<CompanySubsidiary>();
				for (String dataHref : dataHrefSet) {
					
					if (dataHref.contains("www.sec.gov/Archives/edgar/data") && !dataHref.endsWith(".pdf")) {
						logger.info("Connecting to " + dataHref + "......");
						driver = new ChromeDriver();
						driver.get(dataHref);
						Thread.sleep(3000);
						
						html_content = driver.getPageSource();
						
						driver.quit();
						
						logger.info("Scraping for company subsidiaries available......");
						websiteDocument = Jsoup.parse(html_content);
						
						String subsidiaryAsOf = getSubsidiaryAsOf(websiteDocument.text());
						
						Elements companySubsidiariesElements = websiteDocument.select(COMPANY_SUBSIDIARIES_ELEMENTS_CSS_SELECTOR);
						
						for (Element companySubsidiaryElement : companySubsidiariesElements) {
							
							Elements companySubsidiaryDetailsElements = companySubsidiaryElement.select(Constants.HTML_ELEMENT_TD);
							
							String subsidiary = Constants.EMPTY_STRING;
							String jurisdiction = Constants.EMPTY_STRING;
							for (Element companySubsidiaryDetailElement : companySubsidiaryDetailsElements) {
								
								String companySubsidiaryDetail = StringHelper.replaceEachNonBreakingSpaceWithSpace(companySubsidiaryDetailElement.text()).trim();
								
								if (StringHelper.isNotEmpty(companySubsidiaryDetail)) {
									if (StringHelper.isEmpty(subsidiary)) {
										subsidiary = companySubsidiaryDetail;
									} else if (StringHelper.isEmpty(jurisdiction)) {
										jurisdiction = companySubsidiaryDetail;
									}
								}
								
								if (StringHelper.isNotEmpty(subsidiary) && StringHelper.isNotEmpty(jurisdiction)) {
									break;
								}
								
							}
							
							if (StringHelper.isNotEmpty(subsidiary) && StringHelper.isNotEmpty(jurisdiction)) {
								
								CompanySubsidiary companySubsidiary = new CompanySubsidiary();
								
								companySubsidiary.setMasterCompanyId(masterCompanyId);
								companySubsidiary.setMasterCompanyName(masterCompanyName);
								companySubsidiary.setSubsidiaryUrl(dataHref);
								companySubsidiary.setSubsidiary(subsidiary);
								companySubsidiary.setJurisdiction(jurisdiction);
								companySubsidiary.setSubsidiaryAsOf(subsidiaryAsOf);
								
								companySubsidiariesList.add(companySubsidiary);
							}
							
						}
					}
					
				}
				
				if (companySubsidiariesList.size() > 0) {
					logger.info("Writing contact subsidiaries to file......");
					String outputFileName = getOutputFileName(masterCompanyName);
					File excelFile = FileHelper.constructFile(outputFilePath, outputFileName);
					ExcelHelper.writeCompanySubsidiariesToExcelFile(excelFile, companySubsidiariesList);
				} else {
					logger.info("No subsidiaries found!");
				}
				
				logger.info("******** Done scraping Company Subsidiaries of " + masterCompanyName + " ******");
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	public String getOutputFileName(String masterCompanyName) {
		return masterCompanyName.replaceAll(Constants.REGEX_NON_ALPHANUMERIC_OR_SPACE, Constants.EMPTY_STRING).replace(Constants.SPACE, Constants.UNDERSCORE) + "_Subsidiaries.xlsx";
	}
	
	public String getSubsidiaryAsOf(String websiteText) throws Exception {
		String subsidiaryAsOf = Constants.EMPTY_STRING;
		try {
			Pattern pattern = Pattern.compile("as of \\w+ \\d+, \\d+|As of \\w+ \\d+, \\d+");
			Matcher matcher = pattern.matcher(websiteText);
			
			if (matcher.find()) {
				subsidiaryAsOf = matcher.group();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return subsidiaryAsOf;
	}
	
}
