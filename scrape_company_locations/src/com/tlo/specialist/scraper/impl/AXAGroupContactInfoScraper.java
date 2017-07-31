package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.SeleniumHelper;

public class AXAGroupContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(AXAGroupContactInfoScraper.class.getName());
	
	private static final String AXA_LOCATIONS_URL = "https://www.axa.com/en/about-us/axa-world-map";
	
	private static final String AXA_COUNTRY_DROPDOWN_CSS_SELECTOR = "select.world-map__select.js-world-map__select";
	
	private static final String AXA_OFFICE_LOCATIONS_CSS_SELECTOR = "div.world-map__list";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			WebDriver driver = new ChromeDriver();
			driver.get(AXA_LOCATIONS_URL);
			Thread.sleep(3000);
			
			WebElement countriesDropdownElement = driver.findElement(By.cssSelector(AXA_COUNTRY_DROPDOWN_CSS_SELECTOR));
			
			Map<String, String> countriesDropdownOptionsWithValue = SeleniumHelper.getDropdownTextValueOptions(countriesDropdownElement);
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList =  new ArrayList<CompanyContactInformation>();
			for (String country : countriesDropdownOptionsWithValue.keySet()) {
				
				if (!"Pick a country".equalsIgnoreCase(country)) {
					
					driver = new ChromeDriver();
					driver.get(AXA_LOCATIONS_URL);
					Thread.sleep(3000);
					
					logger.info("Scraping office locations from " + country + "......");
				
					countriesDropdownElement = driver.findElement(By.cssSelector(AXA_COUNTRY_DROPDOWN_CSS_SELECTOR));
				
					Select countriesDropdown = new Select(countriesDropdownElement);
					
					String countryValue = countriesDropdownOptionsWithValue.get(country);
					countriesDropdown.selectByValue(countryValue);
					Thread.sleep(3000);
					
					String html_content = driver.getPageSource();
					
					Document websiteDocument = Jsoup.parse(html_content);
					
					Elements officeLocationsElements = websiteDocument.select(AXA_OFFICE_LOCATIONS_CSS_SELECTOR);
					
					Set<String> fullCompanyContactInfoSet = new HashSet<String>();
					for (Element officeLocationElement : officeLocationsElements) {
						fullCompanyContactInfoSet.add(officeLocationElement.text().replace("phone", "phone ").replace("fax", "fax "));
					}
					
					logger.info("Parsing scraped company contact information......");
					List<CompanyContactInformation> companyContactInformationListPerCountry = service.parseContactInformation(masterCompanyId, masterCompanyName, driver.getCurrentUrl(), fullCompanyContactInfoSet);
					companyContactInformationList.addAll(companyContactInformationListPerCountry);
					
					driver.quit();
					
				}
				
			}
			
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
