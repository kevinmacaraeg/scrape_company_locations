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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.ExcelHelper;
import com.tlo.specialist.util.FileHelper;
import com.tlo.specialist.util.JsoupHelper;
import com.tlo.specialist.util.SeleniumHelper;

public class NetappContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(NetappContactInfoScraper.class.getName());
	
	private static final String NETAPP_CONTACT_US_URL = "http://www.netapp.com/us/contact-us/index.aspx";
	
	private static final String NETAPP_WEBSITE_URL = "http://www.netapp.com";
	
	private static final String NETAPP_OTHER_CORPORATE_OFFICES_LINK = "section.otherOffices>ul.linkList>li>a";
	
	private static final String NETAPP_COUNTRIES_DROPDOWN_CSS_SELECTOR = "select.regularSelect";
	
	private static final String NETAPP_OFFICE_LOCATIONS_CSS_SELECTOR = "article.contact,div.container1.wireBlock>section>div.tableContainer>table>tbody>tr>td";

	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			logger.info("Connecting to " + NETAPP_CONTACT_US_URL + "......");
			WebDriver driver = new ChromeDriver();
			driver.get(NETAPP_CONTACT_US_URL);
			Thread.sleep(5000);
			
			String html_content = driver.getPageSource();
			
			Document websiteDocument = Jsoup.parse(html_content);
			
			Elements otherCorporateOfficesLinksElements = websiteDocument.select(NETAPP_OTHER_CORPORATE_OFFICES_LINK);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			Set<String> locationURLs = JsoupHelper.getElementsHrefAttributes(otherCorporateOfficesLinksElements);
			locationURLs.add(driver.getCurrentUrl());
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(NETAPP_WEBSITE_URL, locationURLs);
			
			WebElement countriesDropdownElement = driver.findElement(By.cssSelector(NETAPP_COUNTRIES_DROPDOWN_CSS_SELECTOR));
			
			List<String> countriesDropdownOptions = SeleniumHelper.getDropdownTextOptions(countriesDropdownElement);
			
			List<CompanyContactInformation> companyContactInformationList = new ArrayList<CompanyContactInformation>();
			for (String country : countriesDropdownOptions) {
				
				logger.info("Scraping contact information for " + country + "......");
				
				countriesDropdownElement = driver.findElement(By.cssSelector(NETAPP_COUNTRIES_DROPDOWN_CSS_SELECTOR));
				
				Select countriesDropdown = new Select(countriesDropdownElement);
				
				countriesDropdown.selectByVisibleText(country);
				Thread.sleep(3000);
				
				html_content = driver.getPageSource();
				
				websiteDocument = Jsoup.parse(html_content);
				
				Elements officeLocationsElements = websiteDocument.select(NETAPP_OFFICE_LOCATIONS_CSS_SELECTOR);
				
				Set<String> fullCompanyContactInfoSet = new HashSet<String>();
				for (Element officeLocationElement : officeLocationsElements) {
					fullCompanyContactInfoSet.add(officeLocationElement.text());
				}
				
				logger.info("Parsing scraped contact information......");
				List<CompanyContactInformation> companyContactInformationListPerCountry = service.parseContactInformation(masterCompanyId, masterCompanyName, driver.getCurrentUrl(), fullCompanyContactInfoSet);
				companyContactInformationList.addAll(companyContactInformationListPerCountry);
				
			}
			
			driver.quit();
			
			List<CompanyContactInformation> otherCorporateOfficesContactInformation = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationURLs, NETAPP_OFFICE_LOCATIONS_CSS_SELECTOR);
			companyContactInformationList.addAll(otherCorporateOfficesContactInformation);
			
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
