package com.tlo.specialist.scraper.impl;

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
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;

public class AltranContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(AltranContactInfoScraper.class.getName());
	
	private static final String ALTRAN_WORLDWIDE_OFFICES_URL = "http://www.altran.com/altran-in-the-world.html#/.WRT1GFWGPIU";
	
	private static final String ALTRAN_OFFICES_PER_COUNTRY_CSS_SELECTOR = "ul#countries-list>li.country";
	
	private static final String ALTRAN_COUNTRY_CSS_SELECTOR = "a.country-title";
	
	private static final String ALTRAN_OFFICES_CSS_SELECTOR = "ul.country-places>li.places>div.address";

	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			WebDriver driver = new ChromeDriver();
			driver.get(ALTRAN_WORLDWIDE_OFFICES_URL);
			Thread.sleep(5000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
				
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements officesPerCountryElements = websiteDocument.select(ALTRAN_OFFICES_PER_COUNTRY_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (Element countryOfficesElement : officesPerCountryElements) {
				
				Elements countryElement = countryOfficesElement.select(ALTRAN_COUNTRY_CSS_SELECTOR);
				String country = countryElement.text();
				
				logger.info("Scraping contact information for " + country + "......");
				
				Elements officesElements = countryOfficesElement.select(ALTRAN_OFFICES_CSS_SELECTOR);
				
				Set<String> fullCompanyContactInfoSet = new HashSet<String>();
				for (Element officeElement : officesElements) {
					fullCompanyContactInfoSet.add(officeElement.text() + Constants.SPACE + country);
				}
				
				logger.info("Parsing scraped contact information......");
				List<CompanyContactInformation> companyContactInformationListPerCountry = service.parseContactInformation(masterCompanyId, masterCompanyName, ALTRAN_WORLDWIDE_OFFICES_URL, fullCompanyContactInfoSet);
				companyContactInformationList.addAll(companyContactInformationListPerCountry);
				
			}
			
			logger.info("Writing company contacts to output file...");
			String outputFileName = service.getOutputFileName(masterCompanyName);
			File excelOutputFile = FileHelper.constructFile(outputFilePath, outputFileName);
			ExcelHelper.writeCompanyContactInfoToExcelFile(excelOutputFile, companyContactInformationList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}	
	
}
