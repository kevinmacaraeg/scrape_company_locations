package com.tlo.specialist.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;

public class ScrapeDeloitteContactInfoService {
	
	private static Logger logger = Logger.getLogger(ScrapeDeloitteContactInfoService.class.getName());
	
	private static final String DELOITTE_GLOBAL_OFFICE_DIRECTORY_URL = "https://www2.deloitte.com/global/en/footerlinks/global-office-directory.html?icid=bottom_global-office-directory";
	
	private static final String DELOITTE_GLOBAL_WEBSITE_URL = "http://www2.deloitte.com";

	private static final String DELOITTE_COUNTRY_LINKS_CSS_SELECTOR = "div.country-locales";
	
	private static final String DELOITTE_OFFICE_LOCATIONS_CSS_SELECTOR = "div.offices,div.office-details-container-right-column";
	
	public void scrapeDeloitteContactInfoToFile(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Getting URLs for each country locations......");
			WebDriver driver = new ChromeDriver();
			driver.get(DELOITTE_GLOBAL_OFFICE_DIRECTORY_URL);
			Thread.sleep(5000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			    
			Document websiteDocument = Jsoup.parse(html_content);
			    
			Elements countryLinks = websiteDocument.select(DELOITTE_COUNTRY_LINKS_CSS_SELECTOR);
			
			List<String> locationLinks = new ArrayList<String>();
			for (Element countryLink : countryLinks) {
				
				Elements countryLinkAElements = countryLink.select(Constants.HTML_ELEMENT_A);
				
				boolean hasEnglish = countryLinkAElements.text().contains("English");
				
				if (hasEnglish) {
					for (Element aElement : countryLinkAElements) {
						if ("English".equalsIgnoreCase(aElement.text())) {
							String locationLink = aElement.attr(Constants.HTML_ELEMENT_ATTR_HREF);
							if (!locationLink.startsWith(DELOITTE_GLOBAL_WEBSITE_URL)) {
								locationLink = DELOITTE_GLOBAL_WEBSITE_URL + locationLink;
							}
							locationLinks.add(locationLink);
							break;
						}
					}
				} else {
					String link = countryLinkAElements.get(0).attr(Constants.HTML_ELEMENT_ATTR_HREF);
					if (!link.startsWith(DELOITTE_GLOBAL_WEBSITE_URL)) {
						link = DELOITTE_GLOBAL_WEBSITE_URL + link;
					}
					locationLinks.add(link);
				}
				
			}
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByLinkAndCssSelector(masterCompanyId, masterCompanyName, locationLinks, DELOITTE_OFFICE_LOCATIONS_CSS_SELECTOR);
			
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
