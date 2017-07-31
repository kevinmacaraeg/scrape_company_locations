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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.SeleniumHelper;

public class KPMGContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(KPMGContactInfoScraper.class.getName());
	
	private static final String KPMG_LOCATIONS_URL = "https://home.kpmg.com/xx/en/home/about/offices.html";
	
	private static final String KPMG_COUNTRY_DROPDOWN_CSS_SELECTOR = "select#allCountries";
	
	private static final String KPMG_CONTACT_INFO_CSS_SELECTOR = "div.xslBodyGroupItem>div:nth-of-type(2)";
	
	private static final String KPMG_CITY_CSS_SELECTOR = "div>strong>a";
	
	private static final String KPMG_CONTACT_DETAILS_CSS_SELECTOR = "div>div:first-of-type";
	
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {			
			WebDriver driver = new ChromeDriver();
			driver.get(KPMG_LOCATIONS_URL);
			Thread.sleep(3000);
			
			WebElement dropdownElement = driver.findElement(By.cssSelector(KPMG_COUNTRY_DROPDOWN_CSS_SELECTOR));
			List<String> countries = SeleniumHelper.getDropdownTextOptions(dropdownElement);
			
			Select countriesDropdown = new Select(dropdownElement);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
					
			logger.info("Getting contact information for each country......");
			for (String country : countries) {
				countriesDropdown.selectByVisibleText(country);
				Thread.sleep(2000);
				
				String html_content = driver.getPageSource();
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements locationElements = websiteDocument.select(KPMG_CONTACT_INFO_CSS_SELECTOR);
				
				for (Element locationElement : locationElements) {
					Elements cityElement = locationElement.select(KPMG_CITY_CSS_SELECTOR);
					String city = cityElement.text();
					Elements addressElement = locationElement.select(KPMG_CONTACT_DETAILS_CSS_SELECTOR);
					String contactInfo = addressElement.text();
					CompanyContactInformation companyContactInfo = service.parseContactInformation(masterCompanyId, masterCompanyName, KPMG_LOCATIONS_URL, contactInfo);
					if (companyContactInfo != null) {
						String address = companyContactInfo.getAddress();
						address = address + Constants.SPACE + city + Constants.SPACE + country;
						companyContactInfo.setAddress(address);
						companyContactInformationList.add(companyContactInfo);
					}
				}
			}
			
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
