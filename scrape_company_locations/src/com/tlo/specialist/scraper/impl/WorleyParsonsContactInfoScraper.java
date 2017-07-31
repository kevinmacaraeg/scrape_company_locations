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

public class WorleyParsonsContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(WorleyParsonsContactInfoScraper.class.getName());
	
	private static final String WORLEY_PARSONS_OFFICES_URL = "http://www.worleyparsons.com/Contact/Pages/OfficeListings.aspx";
	
	private static final String WORLEY_PARSONS_CONTACT_INFO_CSS_SELECTOR = "div#wpzWide>div.AspNet-WebPart>table>tbody>tr";

	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + WORLEY_PARSONS_OFFICES_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(WORLEY_PARSONS_OFFICES_URL);
			Thread.sleep(3000);
			
			String html_content = driver.getPageSource();
			
			driver.quit();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements contactInfoElements = websiteDocument.select(WORLEY_PARSONS_CONTACT_INFO_CSS_SELECTOR);
			
			String country = Constants.EMPTY_STRING;
			String city = Constants.EMPTY_STRING;
			String address = Constants.EMPTY_STRING;
			String phoneNumber = Constants.EMPTY_STRING;
			String faxNumber = Constants.EMPTY_STRING;
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (Element contactInfoElement : contactInfoElements) {
				
				Elements contactInfoDetailsElements = contactInfoElement.select(Constants.HTML_ELEMENT_TD);
				
				if (contactInfoDetailsElements.size() == 1) {
					
					String content = contactInfoDetailsElements.text();
					
					if (content.contains("Region")) {
						continue;
					} else if (content.contains("Country")) {
						country = content.replace("Country:", Constants.EMPTY_STRING);
					}
					
				} else if (contactInfoDetailsElements.size() == 5) {
					
					for (int i = 0; i < contactInfoDetailsElements.size(); i++) {
						
						Element contactInfoDetailElement = contactInfoDetailsElements.get(i);
						
						if (i == 0) {
							city = contactInfoDetailElement.text();
						} else if (i == 2) {
							address = contactInfoDetailElement.text();
						} else if (i == 3) {
							phoneNumber = contactInfoDetailElement.text();
						} else if (i == 4) {
							faxNumber = contactInfoDetailElement.text();
						}
						
					}
					
					address = address.trim() + Constants.SPACE + city.trim() + Constants.SPACE + country.trim();
					
					CompanyContactInformation companyContactInformation = new CompanyContactInformation();
					companyContactInformation.setMasterCompanyId(masterCompanyId);
					companyContactInformation.setMasterCompanyName(masterCompanyName);
					companyContactInformation.setCompanyLocationsUrl(WORLEY_PARSONS_OFFICES_URL);
					companyContactInformation.setAddress(address);
					companyContactInformation.setPhoneNumber(phoneNumber.trim());
					companyContactInformation.setFaxNumber(faxNumber.trim());
					
					companyContactInformationList.add(companyContactInformation);
					
				} else {
					
					continue;
					
				}
				
			}
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
				
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
