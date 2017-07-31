package com.tlo.specialist.scraper.impl;

import java.io.File;
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

public class ParkerHannifinContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(ParkerHannifinContactInfoScraper.class.getName());
	
	private static final String PARKER_HANNIFIN_GLOBAL_LOCATIONS_URL = "http://www.parker.com/portal/site/PARKER/menuitem.0b23c5347871c8a9ae472677427ad1ca/?vgnextoid=87495672481c7310VgnVCM10000023cc1dacRCRD&vgnextfmt=default";
	
	private static final String PARKER_HANNIFIN_WEBSITE_URL = "http://www.parker.com";
	
	private static final String PARKER_HANNIFIN_MAP_AREAS_CSS_SELECTOR = "div#map>map>area";
	
	private static final String PARKER_HANNIFIN_COUNTRY_LINKS_CSS_SELECTOR = "div.div_Indent1>a";
	
	private static final String PARKER_HANNIFIN_OFFICE_LOCATIONS_CSS_SELECTOR = "div#col_right>div.column_data_right>table>tbody>tr>td:nth-of-type(1)";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + PARKER_HANNIFIN_GLOBAL_LOCATIONS_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(PARKER_HANNIFIN_GLOBAL_LOCATIONS_URL);
			Thread.sleep(3000);
			
			logger.info("Getting location URLs......");
			List<WebElement> mapAreasElements = driver.findElements(By.cssSelector(PARKER_HANNIFIN_MAP_AREAS_CSS_SELECTOR));
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			for (WebElement mapAreaElement : mapAreasElements) {
				
				js.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", mapAreaElement);
				Thread.sleep(1000);
				
			}
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements countryLinksElements = websiteDocument.select(PARKER_HANNIFIN_COUNTRY_LINKS_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> locationURLs = JsoupHelper.getElementsHrefAttributes(countryLinksElements);
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(PARKER_HANNIFIN_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationURLs, PARKER_HANNIFIN_OFFICE_LOCATIONS_CSS_SELECTOR);
			
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
