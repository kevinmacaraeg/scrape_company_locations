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

public class HenkelContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(HenkelContactInfoScraper.class.getName());
	
	private static final String HENKEL_GLOBAL_PRESENCE_URL = "http://www.henkel.com/company/global-presence";
	
	private static final String HENKEL_LOAD_MORE_BUTTON_CSS_SELECTOR = "div.load-wrap>div.load-more>a";
	
	private static final String HENKEL_LOCATION_DETAIL_CSS_SELECTOR = "div.location.simple";
	
	private static final String HENKEL_ADDRESS_CSS_SELECTOR = "span.address";
	
	private static final String HENKEL_PHONE_CSS_SELECTOR = "ul.link-list.list-unstyled>li>div.tel";
	
	private static final String HENKEL_FAX_CSS_SELECTOR = "ul.link-list.list-unstyled>li>div.fax";
	
	private static final String HENKEL_EMAIL_CSS_SELECTOR = "ul.link-list.list-unstyled>li>a.mail";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + HENKEL_GLOBAL_PRESENCE_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(HENKEL_GLOBAL_PRESENCE_URL);
			Thread.sleep(3000);
			
			logger.info("Loading more offices......");
			boolean hasLoadMoreButton = true;
			while (hasLoadMoreButton) {
				try {
					WebElement loadMoreButtonElement = driver.findElement(By.cssSelector(HENKEL_LOAD_MORE_BUTTON_CSS_SELECTOR));
					JavascriptExecutor js = (JavascriptExecutor) driver;
					js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", loadMoreButtonElement);
					Thread.sleep(3000);
					if (!loadMoreButtonElement.isDisplayed()) {
						hasLoadMoreButton = false;
					}
				} catch (NoSuchElementException e) {
					hasLoadMoreButton = false;
				}
			}
			
			logger.info("Scraping all available company contact information......");
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements locationDetailsElements = websiteDocument.select(HENKEL_LOCATION_DETAIL_CSS_SELECTOR);
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (Element locationDetailElement : locationDetailsElements) {
				
				Elements addressElement = locationDetailElement.select(HENKEL_ADDRESS_CSS_SELECTOR);
				Elements phoneElement = locationDetailElement.select(HENKEL_PHONE_CSS_SELECTOR);
				Elements faxElement = locationDetailElement.select(HENKEL_FAX_CSS_SELECTOR);
				Elements emailElement = locationDetailElement.select(HENKEL_EMAIL_CSS_SELECTOR);
				
				String address = addressElement.text();
				String phoneNumber = phoneElement.text();
				String faxNumber = faxElement.text();
				String email = emailElement.attr(Constants.HTML_ELEMENT_ATTR_HREF);
				
				email = email.replace("mailto:", Constants.EMPTY_STRING);
				
				CompanyContactInformation companyContactInformation = new CompanyContactInformation();
				
				companyContactInformation.setMasterCompanyId(masterCompanyId);
				companyContactInformation.setMasterCompanyName(masterCompanyName);
				companyContactInformation.setCompanyLocationsUrl(HENKEL_GLOBAL_PRESENCE_URL);
				companyContactInformation.setAddress(address);
				companyContactInformation.setPhoneNumber(phoneNumber);
				companyContactInformation.setFaxNumber(faxNumber);
				companyContactInformation.setEmail(email);
				
				companyContactInformationList.add(companyContactInformation);
				
			}
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
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
