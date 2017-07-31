package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
import com.tlo.specialist.util.JsoupHelper;
import com.tlo.specialist.util.SeleniumHelper;

public class AlgarTechContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(AlgarTechContactInfoScraper.class.getName());
	
	private static final String ALGAR_TECH_WHO_WE_ARE_URL = "http://algartech.com/en/the-company/who-we-are/";
	
	private static final String ALGAR_TECH_COUNTRIES_DROPDOWN_CSS_SELECTOR = "select#select-pais";
	
	private static final String ALGAR_TECH_SECONDARY_DROPDOWNS_CSS_SELECTOR = "select.select-secundario";
	
	private static final String ALGAR_TECH_OFFICE_LOCATIONS_CSS_SELECTOR = "div.infowindow";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + ALGAR_TECH_WHO_WE_ARE_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(ALGAR_TECH_WHO_WE_ARE_URL);
			Thread.sleep(3000);
			
			WebElement countriesDropdownElement = driver.findElement(By.cssSelector(ALGAR_TECH_COUNTRIES_DROPDOWN_CSS_SELECTOR));
			
			List<String> countriesDropdownOptions = SeleniumHelper.getDropdownTextOptions(countriesDropdownElement);
			
			for (String country : countriesDropdownOptions) {
				logger.info("Scraping company contact information for " + country + "......");
				
				countriesDropdownElement = driver.findElement(By.cssSelector(ALGAR_TECH_COUNTRIES_DROPDOWN_CSS_SELECTOR));
				
				Select countriesDropdown = new Select(countriesDropdownElement);
				
				countriesDropdown.selectByVisibleText(country);
				
				Thread.sleep(3000);
				
				List<WebElement> secondaryDropdownElements = driver.findElements(By.cssSelector(ALGAR_TECH_SECONDARY_DROPDOWNS_CSS_SELECTOR));
				
				for (WebElement secondaryDropdownElement1 : secondaryDropdownElements) {
					
					if (secondaryDropdownElement1.isDisplayed()) {
						
						String secondaryDropdownElement1ID = secondaryDropdownElement1.getAttribute(Constants.HTML_ELEMENT_ATTR_ID);
						
						List<String> secondaryDropdown1Options = SeleniumHelper.getDropdownTextOptions(secondaryDropdownElement1);
						
						for (String secondaryDropdown1Option : secondaryDropdown1Options) {
							
							logger.info("Scraping company contact information for " + secondaryDropdown1Option + "......");
							
							Select secondaryDropdown1 = new Select(secondaryDropdownElement1);
							
							secondaryDropdown1.selectByVisibleText(secondaryDropdown1Option);
							
							Thread.sleep(3000);
							
							for (WebElement secondaryDropdownElement2 : secondaryDropdownElements) {
								
								if (secondaryDropdownElement2.isDisplayed()) {
									
									String secondaryDropdownElement2ID = secondaryDropdownElement2.getAttribute(Constants.HTML_ELEMENT_ATTR_ID);
									
									if (!secondaryDropdownElement1ID.equals(secondaryDropdownElement2ID)) {
										
										List<String> secondaryDropdown2Options = SeleniumHelper.getDropdownTextOptions(secondaryDropdownElement2);
										
										for (String secondaryDropdown2Option : secondaryDropdown2Options) {
											
											logger.info("Scraping company contact information for " + secondaryDropdown2Option + "......");
											
											Select secondaryDropdown2 = new Select(secondaryDropdownElement2);
											
											secondaryDropdown2.selectByVisibleText(secondaryDropdown2Option);
											
											Thread.sleep(3000);	
											
										}
										
									}
									
								}
								
							}
							
						}
						
					}
					
				}
				
			}
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements officeLocationsElements = websiteDocument.select(ALGAR_TECH_OFFICE_LOCATIONS_CSS_SELECTOR);
			
			Set<String> fullCompanyContactInfoSet = JsoupHelper.getElementsTextToSet(officeLocationsElements);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, ALGAR_TECH_WHO_WE_ARE_URL, fullCompanyContactInfoSet);
			
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
