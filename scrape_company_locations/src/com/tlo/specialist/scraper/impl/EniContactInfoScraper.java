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
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;

public class EniContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(EniContactInfoScraper.class.getName());
	
	private static final String[] ENI_IN_THE_WORLD_URLS = {"https://www.eni.com/en_IT/eni-world/index.shtml#europe", "https://www.eni.com/en_IT/eni-world/index.shtml#america", "https://www.eni.com/en_IT/eni-world/index.shtml#africa", "https://www.eni.com/en_IT/eni-world/index.shtml#asiaoceania"};
	
	private static final String ENI_WEBSITE_URL = "https://www.eni.com/en_IT/eni-world";
	
	private static final String ENI_COUNTRY_LINKS_CSS_SELECTOR = "div#countries-list>div.owl-wrapper-outer>div.owl-wrapper>div.owl-item>div>ul>li>a";
	
	private static final String ENI_OFFICE_LOCATIONS_CSS_SELECTOR = "ul.contacts-list>li>p";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			logger.info("Getting location URLs......");
			Set<String> locationURLs = new HashSet<String>();
			for (String eniInTheWorldURL : ENI_IN_THE_WORLD_URLS) {
				
				logger.info("Connecting to " + eniInTheWorldURL + "......");
				WebDriver driver = new ChromeDriver();
				driver.get(eniInTheWorldURL);
				Thread.sleep(3000);
				
				String html_content = driver.getPageSource();
				
				driver.quit();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements countryLinksElements = websiteDocument.select(ENI_COUNTRY_LINKS_CSS_SELECTOR);
				
				Set<String> locationURLsPerRegion = JsoupHelper.getElementsHrefAttributes(countryLinksElements);
				locationURLs.addAll(locationURLsPerRegion);
			
			}
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(ENI_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationURLs, ENI_OFFICE_LOCATIONS_CSS_SELECTOR);
			
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
