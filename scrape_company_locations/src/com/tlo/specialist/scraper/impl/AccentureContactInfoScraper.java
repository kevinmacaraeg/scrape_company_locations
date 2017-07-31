package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.StringHelper;

public class AccentureContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(AccentureContactInfoScraper.class.getName());
	
	private static final String ACCENTURE_OFFICE_DIRECTORY_URL = "https://www.accenture.com/ph-en/office-directory";
	
	private static final String[] ACCENTURE_SEARCH_LOCATION_STRINGS = {"Asia", "Middle East", "Africa", "Europe", "Americas"};
	
	private static final String ACCENTURE_OFFICE_DIRECTORY_SEARCH_BOX_ID = "location-search-query";
	
	private static final String ACCENTURE_OFFICE_DIRECTORY_FIND_BUTTON_ID = "btn-find";
	
	private static final String ACCENTURE_LOCATIONS_CSS_SELECTOR = "div.item-location";
	
	private static final String ACCENTURE_ADDRESS_CSS_SELECTOR = "span.address";
	
	private static final String ACCENTURE_CITY_CSS_SELECTOR = "span.city";
	
	private static final String ACCENTURE_FAX_CSS_SELECTOR = "span.fax";
	
	private static final String ACCENTURE_PHONE_CSS_SELECTOR = "span.phone";

	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		List<CompanyContactInformation> companyContactInformationList = null;
		try {
			companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String searchLocationString : ACCENTURE_SEARCH_LOCATION_STRINGS) {
				WebDriver driver = new ChromeDriver();
				driver.get(ACCENTURE_OFFICE_DIRECTORY_URL);
				
				logger.info("Searching for " + searchLocationString + "...");
				WebElement searchLocationBox = driver.findElement(By.id(ACCENTURE_OFFICE_DIRECTORY_SEARCH_BOX_ID));
			    searchLocationBox.sendKeys(searchLocationString);
			    
			    WebElement findButton = driver.findElement(By.id(ACCENTURE_OFFICE_DIRECTORY_FIND_BUTTON_ID));
			    findButton.click();
			    
			    Thread.sleep(3000);
			    
			    JavascriptExecutor jse = (JavascriptExecutor)driver;
			    for (int second = 0;; second++) {
			    	if (second >= 30) {
			    		break;
			    	}
			    	jse.executeScript("window.scrollBy(0, Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));", "");
			    	Thread.sleep(500);
			    }
			    
			    logger.info("Scraping company contacts...");
			    String html_content = driver.getPageSource();
			    
			    Document websiteDocument = Jsoup.parse(html_content);
			    
			    Elements divsContainingCompanyContactInfo = websiteDocument.select(ACCENTURE_LOCATIONS_CSS_SELECTOR);
			    
			    for (Element individualDiv : divsContainingCompanyContactInfo) {
			    	Elements addressSpan = individualDiv.select(ACCENTURE_ADDRESS_CSS_SELECTOR);
			    	Elements citySpan = individualDiv.select(ACCENTURE_CITY_CSS_SELECTOR);
			    	Elements faxSpan = individualDiv.select(ACCENTURE_FAX_CSS_SELECTOR);
			    	Elements phoneSpan = individualDiv.select(ACCENTURE_PHONE_CSS_SELECTOR);
			    	
			    	String address = addressSpan.text() + Constants.SPACE  + citySpan.text();
			    	String phone = phoneSpan.text();
			    	phone = phone.replace("Phone:", Constants.EMPTY_STRING).trim();
			    	String fax = faxSpan.text();
			    	fax = fax.replace("Fax:", Constants.EMPTY_STRING).trim();
			    	
			    	CompanyContactInformation companyContactInfo = new CompanyContactInformation();
			    	companyContactInfo.setMasterCompanyId(masterCompanyId);
			    	companyContactInfo.setMasterCompanyName(masterCompanyName);
			    	companyContactInfo.setCompanyLocationsUrl(ACCENTURE_OFFICE_DIRECTORY_URL);
			    	companyContactInfo.setAddress(StringHelper.replaceNullValue(address, Constants.EMPTY_STRING).trim());
			    	companyContactInfo.setPhoneNumber(StringHelper.replaceNullValue(phone, Constants.EMPTY_STRING).trim());
			    	companyContactInfo.setFaxNumber(StringHelper.replaceNullValue(fax, Constants.EMPTY_STRING).trim());
			    	
			    	companyContactInformationList.add(companyContactInfo);
			    	
			    }
			    
			    driver.quit();
			}
			
			logger.info("Writing company contacts to output file...");
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			String outputFileName = service.getOutputFileName(masterCompanyName);
			File excelOutputFile = FileHelper.constructFile(outputFilePath, outputFileName);
			ExcelHelper.writeCompanyContactInfoToExcelFile(excelOutputFile, companyContactInformationList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
}
