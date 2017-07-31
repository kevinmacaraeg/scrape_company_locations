package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class SDLContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(SDLContactInfoScraper.class.getName());
	
	private static final String SDL_OFFICES_URL = "http://www.sdl.com/contact/";
	
	private static final String SDL_WEBSITE_URL = "http://www.sdl.com";
	
	private static final String SDL_COUNTRY_DROPDOWN_OPTIONS_CSS_SELECTOR = "div.selectricWrapper.selectric-office-countries>div.selectricItems>div.selectricScroll>ul>li";
	
	private static final String SDL_ADDRESS_LINKS_CSS_SELECTOR = "div.office-list-item>a.title.lightbox-page.cboxElement";
	
	private static final String SDL_OFFICE_LOCATIONS_CSS_SELECTOR = "div.contact-info";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			logger.info("Connecting to " + SDL_OFFICES_URL +"......");
			WebDriver driver = new ChromeDriver();
			driver.get(SDL_OFFICES_URL);
			Thread.sleep(3000);
			
			List<WebElement> countryDropdownOptionssElements = driver.findElements(By.cssSelector(SDL_COUNTRY_DROPDOWN_OPTIONS_CSS_SELECTOR));
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			
			logger.info("Getting location URLs......");
			Set<String> locationURLs = new HashSet<String>();
			for (WebElement countryDropdownOptionElement : countryDropdownOptionssElements) {
				
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", countryDropdownOptionElement);
				Thread.sleep(1000);
				
				String html_content = driver.getPageSource();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements addressLinksElements = websiteDocument.select(SDL_ADDRESS_LINKS_CSS_SELECTOR);
				
				Set<String> locationURLsPerCountry = JsoupHelper.getElementsHrefAttributes(addressLinksElements);
				locationURLs.addAll(locationURLsPerCountry);
				
			}
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(SDL_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationURLs, SDL_OFFICE_LOCATIONS_CSS_SELECTOR);
			
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
