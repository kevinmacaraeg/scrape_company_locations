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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;

public class IntelCorporationContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(IntelCorporationContactInfoScraper.class.getName());
	
	private static final String INTEL_WORLDWIDE_URL = "http://www.intel.com/content/www/us/en/location/worldwide.html";
	
	private static final String INTEL_OFFICE_LOCATIONS_CSS_SELECTOR = "div.android-scroll.table-responsive>table.table>tbody>tr.data";
	
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + INTEL_WORLDWIDE_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(INTEL_WORLDWIDE_URL);
			Thread.sleep(3000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			logger.info("Selecting elements with contact information......");
			Elements officeLocations = websiteDocument.select(INTEL_OFFICE_LOCATIONS_CSS_SELECTOR);
			
			logger.info("Getting contact information......");
			String country = Constants.EMPTY_STRING;
			String city = Constants.EMPTY_STRING;
			Set<String> fullCompanyContactInfoSet = new HashSet<String>();
			for (Element officeLocation : officeLocations) {
				Elements officeLocationDetails = officeLocation.select(Constants.HTML_ELEMENT_TD);
				
				if (officeLocationDetails.size() == 4) {
					country = officeLocationDetails.first().text();
					city = officeLocationDetails.get(1).text();
				} else if (officeLocationDetails.size() == 3) {
					city = officeLocationDetails.first().text();
				}
				
				String address = officeLocationDetails.get(officeLocationDetails.size() - 1).text();
				
				String companyContactInformation = address.trim() + Constants.SPACE + city.trim() + Constants.SPACE + country.trim();
				fullCompanyContactInfoSet.add(companyContactInformation);
			}
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = service.parseContactInformation(masterCompanyId, masterCompanyName, INTEL_WORLDWIDE_URL, fullCompanyContactInfoSet);
			
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
