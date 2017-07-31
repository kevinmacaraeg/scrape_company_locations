package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.ArrayList;
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
import com.tlo.specialist.util.JsoupHelper;
import com.tlo.specialist.util.StringHelper;

public class NECContactInfoScraper implements CompanyContactInfoScraper {
	
	private static Logger logger = Logger.getLogger(NECContactInfoScraper.class.getName());
	
	private static final String NEC_WORLDWIDE_URL = "http://www.nec.com/en/global/office/index.html";
	
	private static final String NEC_IN_JAPAN_URL = "http://www.nec.co.jp/profile/en/branch.html";
	
	private static final String NEC_WEBSITE_URL = "http://www.nec.com";
	
	private static final String NEC_COUNTRY_LINKS_CSS_SELECTOR = "ul.linkList-01.mb30>li>a";
	
	private static final String NEC_OFFICE_LOCATIONS_CSS_SELECTOR = "div.tblScroll-01>table>tbody";
	
	private static final String NEC_JAPAN_OFFICE_LOCATIONS_CSS_SELECTOR = "table.nf-table04";

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + NEC_WORLDWIDE_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(NEC_WORLDWIDE_URL);
			Thread.sleep(3000);
	
			logger.info("Getting location URLs......");
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements countryLinksElements = websiteDocument.select(NEC_COUNTRY_LINKS_CSS_SELECTOR);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> locationURLs = JsoupHelper.getElementsHrefAttributes(countryLinksElements);
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(NEC_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String locationURL : locationURLs) {
				
				logger.info("Connecting to " + locationURL + "......");
				driver = new ChromeDriver();
				driver.get(locationURL);
				Thread.sleep(3000);
				
				logger.info("Scraping available company contact information......");
				html_content = driver.getPageSource();
				
				driver.quit();
				
				websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(NEC_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				logger.info("Parsing scraped company contact information......");
				for (Element officeLocationElement : officeLocationsElements) {
					
					String officeLocationElementText = officeLocationElement.text();
					
					String phoneLabel = "Telephone";
					String faxLabel = "Fax";
					String addressLabel = Constants.EMPTY_STRING;
					if (officeLocationElementText.contains("Address (Head Office)")) {
						addressLabel = "Address (Head Office)";
					} else if (officeLocationElementText.contains("Address")) {
						addressLabel = "Address";
					} else if (officeLocationElementText.contains("Location(Head Office)")) {
						addressLabel = "Location(Head Office)";
					} else if (officeLocationElementText.contains("Location (Head Office)")) {
						addressLabel = "Location (Head Office)";
					}  else if (officeLocationElementText.contains("Location")) {
						addressLabel = "Location";
					}
					
					Elements officeLocationDetailsElements = officeLocationElement.select(Constants.HTML_ELEMENT_TR);
					
					String address = Constants.EMPTY_STRING;
					String phoneNumber = Constants.EMPTY_STRING;
					String faxNumber = Constants.EMPTY_STRING;
					for (Element officeLocationDetailElement : officeLocationDetailsElements) {
						
						String officeLocationDetailElementText = officeLocationDetailElement.text();
						
						if (officeLocationDetailElementText.contains(addressLabel)) {
							
							address = officeLocationDetailElementText.replace(addressLabel, Constants.EMPTY_STRING).trim();
							
						} else if (officeLocationDetailElementText.contains(phoneLabel)) {
							
							phoneNumber = officeLocationDetailElementText.replace(phoneLabel, Constants.EMPTY_STRING).trim();
							
						} else if (officeLocationDetailElementText.contains(faxLabel)) {
							
							faxNumber = officeLocationDetailElementText.replace(faxLabel, Constants.EMPTY_STRING).trim();
							
						}
						
					}
					
					if (StringHelper.isNotEmpty(address + phoneNumber)) {
						
						CompanyContactInformation companyContactInformation = new CompanyContactInformation();
						
						companyContactInformation.setMasterCompanyId(masterCompanyId);
						companyContactInformation.setMasterCompanyName(masterCompanyName);
						companyContactInformation.setCompanyLocationsUrl(locationURL);
						companyContactInformation.setAddress(address);
						companyContactInformation.setPhoneNumber(phoneNumber);
						companyContactInformation.setFaxNumber(faxNumber);
						
						companyContactInformationList.add(companyContactInformation);
						
					}
				}
				
			}
			
			logger.info("Scraping company contact information from Japan......");
			List<CompanyContactInformation> companyContactInformationFromJapan = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, NEC_IN_JAPAN_URL, NEC_JAPAN_OFFICE_LOCATIONS_CSS_SELECTOR);
			companyContactInformationList.addAll(companyContactInformationFromJapan);
			
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
