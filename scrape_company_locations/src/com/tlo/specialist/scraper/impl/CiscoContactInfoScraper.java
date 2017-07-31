package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
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

public class CiscoContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(CiscoContactInfoScraper.class.getName());
	
	private static final String CISCO_WORLDWIDE_OFFICE_LOCATIONS_URL = "http://www.cisco.com/cisco/web/siteassets/contacts/index.html";
	
	private static final String CISCO_REGIONS_DROPDOWN_CSS_SELECTOR = "select#contactRegion";
	
	private static final String CISCO_COUNTRIES_DROPDOWN_CSS_SELECTOR = "select#countryLang";
	
	private static final String CISCO_RADIO_BUTTONS_CSS_SELECTOR = "fieldset#level1>input";
	
	private static final String CISCO_OFFICE_LOCATIONS_CSS_SELECTOR = "div.address-info>p";
	
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + CISCO_WORLDWIDE_OFFICE_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(CISCO_WORLDWIDE_OFFICE_LOCATIONS_URL);
			Thread.sleep(3000);
			
			WebElement regionsDropdownElement = driver.findElement(By.cssSelector(CISCO_REGIONS_DROPDOWN_CSS_SELECTOR));
			
			List<String> regionsDropdownOptions = SeleniumHelper.getDropdownTextOptions(regionsDropdownElement);
			
			logger.info("Getting URLs for each country locations......");
			Set<String> locationsURL = new HashSet<String>();
			for (String regionsDropdownOption : regionsDropdownOptions) {
				
				regionsDropdownElement = driver.findElement(By.cssSelector(CISCO_REGIONS_DROPDOWN_CSS_SELECTOR));
				Select regionsDropdown = new Select(regionsDropdownElement);
				
				regionsDropdown.selectByVisibleText(regionsDropdownOption);
				
				WebElement countriesDropdownElement = driver.findElement(By.cssSelector(CISCO_COUNTRIES_DROPDOWN_CSS_SELECTOR));
				
				List<String> countriesDropdownOptions = SeleniumHelper.getDropdownTextOptions(countriesDropdownElement);
				removeNonEnglishCountriesIfCountriesHaveEnglishOption(countriesDropdownOptions);
				
				for (String countriesDropdownOption : countriesDropdownOptions) {
					
					if (!"select one".equalsIgnoreCase(countriesDropdownOption)) {
						
						countriesDropdownElement = driver.findElement(By.cssSelector(CISCO_COUNTRIES_DROPDOWN_CSS_SELECTOR));
						
						Select countriesDropdown = new Select(countriesDropdownElement);
						countriesDropdown.selectByVisibleText(countriesDropdownOption);
						
						List<WebElement> radioButtonsElements = driver.findElements(By.cssSelector(CISCO_RADIO_BUTTONS_CSS_SELECTOR));
						WebElement radioButtonToLookForOfficesElement = radioButtonsElements.get(radioButtonsElements.size() - 1);
						
						radioButtonToLookForOfficesElement.click();
						
						Thread.sleep(1000);
						
						locationsURL.add(driver.getCurrentUrl());
					
					}
					
				}
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationsURL, CISCO_OFFICE_LOCATIONS_CSS_SELECTOR);
			
			logger.info("Writing contact information to file......");
			String outputFileName = service.getOutputFileName(masterCompanyName);
			File excelFile = FileHelper.constructFile(outputFilePath, outputFileName);
			ExcelHelper.writeCompanyContactInfoToExcelFile(excelFile, companyContactInformationList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	void removeNonEnglishCountriesIfCountriesHaveEnglishOption(List<String> countries) throws Exception {
		try {
			Map<String, Boolean> countryHasEnglishMap = new HashMap<String, Boolean>();
			for (String country : countries) {
				if (!"select one".equalsIgnoreCase(country)) {
					String splitter = Constants.DASH;
					if (country.contains(Constants.EN_DASH)) {
						splitter = Constants.EN_DASH;
					}
					String[] countryLanguage = country.split(splitter);
					country = countryLanguage[0].trim();
					String language = countryLanguage[1].trim();
					boolean hasEnglish = "English".equalsIgnoreCase(language);
					if (countryHasEnglishMap.containsKey(country)) {
						if (!countryHasEnglishMap.get(country)) {
							countryHasEnglishMap.put(country, hasEnglish);
						}
					} else {
						countryHasEnglishMap.put(country, hasEnglish);
					}
				}
			}
			
			for (String countryFromMap : countryHasEnglishMap.keySet()) {
				boolean hasEnglish = countryHasEnglishMap.get(countryFromMap);
				if (hasEnglish) {
					Iterator<String> countriesIterator = countries.iterator();
					while (countriesIterator.hasNext()) {
						String countryFromList = countriesIterator.next();
						if (countryFromList.contains(countryFromMap) && !countryFromList.contains("English")) {
							countriesIterator.remove();
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
}
