package com.tlo.specialist.scraper.impl;

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
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;

public class RollsRoyceContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(RollsRoyceContactInfoScraper.class.getName());
	
	private static final String ROLLS_ROYCE_WORLDWIDE_PRESENCE_URL = "http://www.rolls-royce.com/worldwide-presence.aspx";
	
	private static final String ROLLS_ROYCE_COUNTRY_LOCATION_CSS_SELECTOR = "div.country-snapdown-item";
	
	private static final String ROLLS_ROYCE_ADDRESS_CSS_SELECTOR = "div.location-bx-content";
	
	private static final String ROLLS_ROYCE_PHONE_CSS_SELECTOR = "div.snapdown-telephone.snap-hd";
	
	private static final String ROLLS_ROYCE_EMAIL_CSS_SELECTOR = "div.snapdown-email.snap-hd>a";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			logger.info("Connecting to " + ROLLS_ROYCE_WORLDWIDE_PRESENCE_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(ROLLS_ROYCE_WORLDWIDE_PRESENCE_URL);
			Thread.sleep(5000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			logger.info("Getting location URLs......");
			Document websiteDocument = Jsoup.parse(html_content);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
				
			logger.info("Selecting elements with contact information......");
			Elements countryLocationElements = websiteDocument.select(ROLLS_ROYCE_COUNTRY_LOCATION_CSS_SELECTOR);
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			logger.info("Getting contact information......");
			for (Element companyInnerListElement : countryLocationElements) {
				
				Elements companyAddressElements = companyInnerListElement.select(ROLLS_ROYCE_ADDRESS_CSS_SELECTOR);
				Elements companyPhoneElements = companyInnerListElement.select(ROLLS_ROYCE_PHONE_CSS_SELECTOR);
				Elements companyEmailElements = companyInnerListElement.select(ROLLS_ROYCE_EMAIL_CSS_SELECTOR);
				
				String address = companyAddressElements.text();
				String phoneNumber = companyPhoneElements.text();
				String email = companyEmailElements.attr(Constants.HTML_ELEMENT_ATTR_HREF);

				email = email.replace("mailto:", Constants.EMPTY_STRING);
				
				CompanyContactInformation companyContactInformation = new CompanyContactInformation();
				companyContactInformation.setMasterCompanyId(masterCompanyId);
				companyContactInformation.setMasterCompanyName(masterCompanyName);
				companyContactInformation.setCompanyLocationsUrl(ROLLS_ROYCE_WORLDWIDE_PRESENCE_URL);
				companyContactInformation.setAddress(address);
				companyContactInformation.setPhoneNumber(phoneNumber);
				companyContactInformation.setEmail(email);
				
				companyContactInformationList.add(companyContactInformation);
				
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
