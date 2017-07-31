package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.StringHelper;

public class ZurichInsuranceContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(ZurichInsuranceContactInfoScraper.class.getName());
	
	private static final String ZURICH_LOCATION_FINDER_URL = "https://www.zurich.com/en/location-finder";
	
	private static final String ZURICH_COUNTRY_OPTIONS_CSS_SELECTOR = "div.dropdown-menu.open>ul.dropdown-menu.inner.selectpicker>li>a";
	
	private static final String ZURICH_SHOW_MORE_BUTTON_CSS_SELECTOR = "div.list-showmore>a.link.link-icon";
	
	private static final String ZURICH_CONTACT_INFO_CSS_SELECTOR = "div.list-holder>div.listitem.bg-01>div.row:nth-of-type(2)";
	
	private static final String ZURICH_OFFICE_LOCATIONS_CSS_SELECTOR = "div:first-of-type";
	
	private static final String ZURICH_EMAIL_CSS_SELECTOR = "a.link.link-standard.location-email";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + ZURICH_LOCATION_FINDER_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(ZURICH_LOCATION_FINDER_URL);
			Thread.sleep(3000);
			
			List<WebElement> countryOptionsElements = driver.findElements(By.cssSelector(ZURICH_COUNTRY_OPTIONS_CSS_SELECTOR));
			int numberOfCountryOptions = countryOptionsElements.size();
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			
			logger.info("Scraping available company contact information......");
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (int i = 0; i < numberOfCountryOptions; i++) {
				
				countryOptionsElements = driver.findElements(By.cssSelector(ZURICH_COUNTRY_OPTIONS_CSS_SELECTOR));
				
				WebElement countryOptionElement = countryOptionsElements.get(i);
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", countryOptionElement);
				Thread.sleep(1000);
				
				boolean hasShowMoreButton = true;
				while (hasShowMoreButton) {
					
					try {
						
						WebElement showMoreButton = driver.findElement(By.cssSelector(ZURICH_SHOW_MORE_BUTTON_CSS_SELECTOR));
						
						if (showMoreButton.isDisplayed()) {
							js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", showMoreButton);
							Thread.sleep(3000);
						} else {
							hasShowMoreButton = false;
						}
						
					} catch (NoSuchElementException e) {
						hasShowMoreButton = false;
					}
					
				}
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements contactInfoElements = websiteDocument.select(ZURICH_CONTACT_INFO_CSS_SELECTOR);
				
				Set<String> fullCompanyContactInfoSetPerCountry = new HashSet<String>();
				for (Element contactInfoElement : contactInfoElements) {
					
					Elements officeLocationsElements = contactInfoElement.select(ZURICH_OFFICE_LOCATIONS_CSS_SELECTOR);
					Elements emailElements = contactInfoElement.select(ZURICH_EMAIL_CSS_SELECTOR);
					
					String fullCompanyContactInfo = officeLocationsElements.text();
					String emailInfo = emailElements.attr(Constants.HTML_ELEMENT_ATTR_HREF).replace("mailto:", Constants.EMPTY_STRING);
					
					fullCompanyContactInfo = StringHelper.isEmpty(emailInfo) ? fullCompanyContactInfo : fullCompanyContactInfo + " Email: " + emailInfo;
					
					fullCompanyContactInfoSetPerCountry.add(fullCompanyContactInfo);
					
				}
				
				fullCompanyContactInfoSet.addAll(fullCompanyContactInfoSetPerCountry);
					
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Parsing scraped company contact information......");
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, ZURICH_LOCATION_FINDER_URL, fullCompanyContactInfoSet);
			
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
