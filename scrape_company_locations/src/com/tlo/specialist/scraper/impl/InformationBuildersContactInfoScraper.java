package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class InformationBuildersContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(InformationBuildersContactInfoScraper.class.getName());
	
	private static final String INFORMATION_BUILDERS_WORLDWIDE_OFFICES_URL = "http://www.informationbuilders.com/about_us/world_wide";
	
	private static final String INFORMATION_BUILDERS_WEBSITE_URL = "http://www.informationbuilders.com";
	
	private static final String INFORMATION_BUILDERS_COUNTRY_DROPDOWN_OPTIONS_CSS_SELECTOR = "div.content>form>p>select>option";
	
	private static final String INFORMATION_BUILDERS_CITY_LINKS_CSS_SELECTOR = "div.view-content>table.views-table.cols-3>tbody>tr>td:first-of-type>a";
	
	private static final String INFORMATION_BUILDERS_OFFICE_LOCATIONS_CSS_SELECTOR = "div.content-branch";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			logger.info("Connecting to " + INFORMATION_BUILDERS_WORLDWIDE_OFFICES_URL +"......");
			WebDriver driver = new ChromeDriver();
			driver.get(INFORMATION_BUILDERS_WORLDWIDE_OFFICES_URL);
			Thread.sleep(3000);
			
			logger.info("Getting location URLs......");
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements countryLocationsElements = websiteDocument.select(INFORMATION_BUILDERS_COUNTRY_DROPDOWN_OPTIONS_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> countryURLs = JsoupHelper.getElementsAttributeValue(Constants.HTML_ELEMENT_ATTR_VALUE, countryLocationsElements);
			countryURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(INFORMATION_BUILDERS_WEBSITE_URL, countryURLs);
			
			Set<String> locationURLs = new HashSet<String>();
			for (String countryURL : countryURLs) {
				
				logger.info("Connecting to " + countryURL +"......");
				driver = new ChromeDriver();
				driver.get(countryURL);
				Thread.sleep(3000);
				
				html_content = driver.getPageSource();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				Elements cityLinksElements = websiteDocument.select(INFORMATION_BUILDERS_CITY_LINKS_CSS_SELECTOR);
				
				if (cityLinksElements.size() > 0) {
					
					Set<String> cityURLs = JsoupHelper.getElementsHrefAttributes(cityLinksElements);
					locationURLs.addAll(cityURLs);
					
				} else {
					
					locationURLs.add(countryURL);
					
				}
				
				
			}
			
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(INFORMATION_BUILDERS_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationURLs, INFORMATION_BUILDERS_OFFICE_LOCATIONS_CSS_SELECTOR);
			
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
