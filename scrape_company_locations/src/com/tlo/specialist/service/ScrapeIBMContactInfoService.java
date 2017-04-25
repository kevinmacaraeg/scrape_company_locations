package com.tlo.specialist.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;

public class ScrapeIBMContactInfoService {
	
	private static Logger logger = Logger.getLogger(ScrapeIBMContactInfoService.class.getName());
	
	private static final String IBM_PLANETWIDE_LOCATIONS_LINK = "https://www.ibm.com/planetwide/";
	
	private static final String PLANETWIDE_PAGE_LINKS = "ul.ibm-bullet-list>li>a";
	
	private static final String COMPANY_CONTACT_INFO_CSS_SELECTOR = "div#ibm-content-main>div.ibm-columns>div:nth-of-type(1)";
	
	public void scrapeIBMContactInfoToFile(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			WebDriver driver = new ChromeDriver();
			driver.get(IBM_PLANETWIDE_LOCATIONS_LINK);
			Thread.sleep(3000);
			
			List<WebElement> locationsLinksElementsList = driver.findElements(By.cssSelector(PLANETWIDE_PAGE_LINKS));
			
			List<String> locationsLinksList = getLocationsLinksList(locationsLinksElementsList);
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByLinkAndCssSelector(masterCompanyId, masterCompanyName, locationsLinksList, COMPANY_CONTACT_INFO_CSS_SELECTOR);	
			
			String outputFileName = service.getOutputFileName(masterCompanyName);
			File excelFile = FileHelper.constructFile(outputFilePath, outputFileName);
			ExcelHelper.writeCompanyContactInfoToExcelFile(excelFile, companyContactInformationList);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new Exception(e.getMessage());
		}
	}
	
	List<String> getLocationsLinksList(List<WebElement> locationsLinksElementsList) throws Exception {
		List<String> locationsLinksList = null;
		try {
			locationsLinksList = new ArrayList<String>();
			for (WebElement locationLinkElement : locationsLinksElementsList) {
				locationsLinksList.add(locationLinkElement.getAttribute(Constants.HTML_ELEMENT_ATTR_HREF));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return locationsLinksList;
	}

}
