package com.tlo.specialist.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

public class ScrapePWCContactInfoService {

	private static Logger logger = Logger.getLogger(ScrapePWCContactInfoService.class.getName());
	
	private static final String PWC_FIRM_LOCATIONS_URL = "https://www.pwc.com/gx/en/about/office-locations.html";
	
	private static final String PWC_WEBSITE_URL = "https://www.pwc.com";

	private static final String PWC_COUNTRY_LINKS_CSS_SELECTOR = "div.parbase.html.section>div>table>tbody>tr>td>ul>li>a";
	
	private static final String PWC_OFFICE_LOCATIONS_CSS_SELECTOR = "div.parsys.centerPar>div.parbase.html.section>div,div.parsys.centerPar>div.nobg.parbase.section.text>div";
	
	public void scrapePWCContactInfoToFile(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Getting URLs for each country locations......");
			WebDriver driver = new ChromeDriver();
			driver.get(PWC_FIRM_LOCATIONS_URL);
			Thread.sleep(5000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			    
			Document websiteDocument = Jsoup.parse(html_content);
			    
			Elements countryLinks = websiteDocument.select(PWC_COUNTRY_LINKS_CSS_SELECTOR);
			
			List<String> locationLinks = new ArrayList<String>();
			for (Element countryLink : countryLinks) {
				
				String link = countryLink.attr(Constants.HTML_ELEMENT_ATTR_HREF);
				if (!link.startsWith(PWC_WEBSITE_URL)) {
					link = PWC_WEBSITE_URL + link;
				}
				locationLinks.add(link);
				
			}
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String locationLink : locationLinks) {
				logger.info("Connecting to " + locationLink + "......");
				driver = new ChromeDriver();
				driver.get(locationLink);
				Thread.sleep(3000);
				
				html_content = driver.getPageSource();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				logger.info("Selecting elements with contact information......");
				Elements companyLocationsDiv = websiteDocument.select(PWC_OFFICE_LOCATIONS_CSS_SELECTOR);
				Element companyLocationsDivElement = null;
				if (companyLocationsDiv.size() == 1) {
					companyLocationsDivElement = companyLocationsDiv.first();
				} else if (companyLocationsDiv.size() == 3) {
					companyLocationsDivElement = companyLocationsDiv.get(1);
				} else if (companyLocationsDiv.size() == 4) {
					companyLocationsDivElement = companyLocationsDiv.get(3);
				}
				
				if (companyLocationsDivElement != null) {
					Elements companyLocationsDivElementChildren = companyLocationsDivElement.children();
					
					Set<String> fullCompanyContactInformationSet = new HashSet<String>();
					StringBuilder companyContactInfoBuilder = null;
					int pCounter = 0;
					for (Element childElement : companyLocationsDivElementChildren) {
						String tagName = childElement.tagName();
						if (Constants.HTML_ELEMENT_H3.equals(tagName)) {
							companyContactInfoBuilder = new StringBuilder(childElement.text());
						} else if (Constants.HTML_ELEMENT_P.equals(tagName)) {
							if (companyContactInfoBuilder != null && pCounter < 2) {
								companyContactInfoBuilder.append(childElement.text());
								pCounter++;
							}
							if (pCounter == 2) {
								fullCompanyContactInformationSet.add(companyContactInfoBuilder.toString());
								pCounter = 0;
								companyContactInfoBuilder = null;
							}
						} else {
							continue;
						}
					}
					
					List<CompanyContactInformation> parsedContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, locationLink, fullCompanyContactInformationSet);
					if (parsedContactInformationList != null) {
						companyContactInformationList.addAll(parsedContactInformationList);
					}
				} else {
					logger.info("No company contact information scraped from " + locationLink);
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
