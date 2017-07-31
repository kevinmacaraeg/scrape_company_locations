package com.tlo.specialist.scraper.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

public class InfosysContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(InfosysContactInfoScraper.class.getName());
	
	private static final String INFOSYS_LOCATIONS_URL = "https://www.infosys.com/about/Pages/locations.aspx?subsidiary=Infosys";
	
	private static final String INFOSYS_SUBSIDIARIES_DROPDOWN_CSS_SELECTOR = "select.infy-subsidiaries";
	
	private static final String INFOSYS_WEBSITE_URL = "https://www.infosys.com";

	private static final String INFOSYS_COUNTRY_LINKS_CSS_SELECTOR = "div.WebPart>div>div.row>div>ul>li>a";
	
	private static final String INFOSYS_OFFICE_LOCATIONS_CSS_SELECTOR = "div#ctl00_SPWebPartManager1_g_077abff8_b8e5_497c_a8ea_a3825fc1a80c_ListViewContactCountry_ctrl0_ctl00_maincol";
	
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			WebDriver driver = new ChromeDriver();
			driver.get(INFOSYS_LOCATIONS_URL);
			Thread.sleep(3000);
			
			WebElement dropdownElement = driver.findElement(By.cssSelector(INFOSYS_SUBSIDIARIES_DROPDOWN_CSS_SELECTOR));
			
			List<String> subsidiariesDropdownOptions = SeleniumHelper.getDropdownTextOptions(dropdownElement);
			
			driver.quit();
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Getting URLs for each country locations......");
			Set<String> locationURLs = new HashSet<String>();
			for (String subsidiary : subsidiariesDropdownOptions) {
				driver = new ChromeDriver();
				driver.get(INFOSYS_LOCATIONS_URL);
				Thread.sleep(3000);
				
				dropdownElement = driver.findElement(By.cssSelector(INFOSYS_SUBSIDIARIES_DROPDOWN_CSS_SELECTOR));
				Select subsidiariesDropdown = new Select(dropdownElement);
				
				subsidiariesDropdown.selectByVisibleText(subsidiary);
				Thread.sleep(3000);
				
				String html_content = driver.getPageSource();
				
				driver.quit();
				
				Document websiteDocument = Jsoup.parse(html_content);
				
				Elements countryLinks = websiteDocument.select(INFOSYS_COUNTRY_LINKS_CSS_SELECTOR);
				
				Set<String> locationURLsPerCountry = JsoupHelper.getElementsHrefAttributes(countryLinks);
				locationURLs.addAll(locationURLsPerCountry);
			}
			
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(INFOSYS_WEBSITE_URL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationURLs, INFOSYS_OFFICE_LOCATIONS_CSS_SELECTOR);

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
