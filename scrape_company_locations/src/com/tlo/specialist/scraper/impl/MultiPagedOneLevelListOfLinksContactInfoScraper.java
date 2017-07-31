package com.tlo.specialist.scraper.impl;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.service.ScrapeCompanyContactInfoService;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.StringHelper;

public class MultiPagedOneLevelListOfLinksContactInfoScraper implements CompanyContactInfoScraper {

	private static Logger logger = Logger.getLogger(MultiPagedOneLevelListOfLinksContactInfoScraper.class.getName());
	
	private static String centralLocationsURL;
	
	private static String websiteURL;
	
	private static String linksCssSelector;
	
	private static String linkAttributeForURL;
	
	private static String officeLocationsCssSelector;
	
	private static Set<String> additionalURLsSet;

	@Override
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception {
		try {
			
			init(masterCompanyId);
			
			ScrapeCompanyContactInfoService service = new ScrapeCompanyContactInfoService();
			
			logger.info("Getting location URLs......");
			Set<String> locationURLs = service.scrapeLinksURLsByCssSelector(centralLocationsURL, linksCssSelector, linkAttributeForURL);
			locationURLs.addAll(additionalURLsSet);
			locationURLs = service.prependWebsiteURLIfCurrentURLsHaveNoProtocol(websiteURL, locationURLs);
			
			List<CompanyContactInformation> companyContactInformationList = service.scrapeCompanyContactInfoByURLAndCssSelector(masterCompanyId, masterCompanyName, locationURLs, officeLocationsCssSelector);
			
			logger.info("Writing contact information to file......");
			service.writeCompanyContactInformationToFile(masterCompanyName, outputFilePath, companyContactInformationList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	void init(String masterCompanyId) throws Exception {
		try {
			
			Properties systemProperties = new Properties();
			URL url = Thread.currentThread().getContextClassLoader().getResource("properties/multiPagedOneLevelListOfLinksProperties.properties");
			systemProperties.load(url.openStream());
			
			centralLocationsURL = systemProperties.getProperty(masterCompanyId + ".central.locations.url").trim();
			if (StringHelper.isEmpty(centralLocationsURL)) {
				throw new Exception(masterCompanyId + ".central.locations.url" + " property is missing. Please update properties file!");
			}
			
			websiteURL = systemProperties.getProperty(masterCompanyId + ".website.url").trim();
			if (StringHelper.isEmpty(websiteURL)) {
				throw new Exception(masterCompanyId + ".website.url" + " property is missing. Please update properties file!");
			}
			
			linksCssSelector = systemProperties.getProperty(masterCompanyId + ".links.css.selector").trim();
			if (StringHelper.isEmpty(linksCssSelector)) {
				throw new Exception(masterCompanyId + ".links.css.selector" + " property is missing. Please update properties file!");
			}
			
			officeLocationsCssSelector = systemProperties.getProperty(masterCompanyId + ".office.locations.css.selector").trim();
			if (StringHelper.isEmpty(officeLocationsCssSelector)) {
				throw new Exception(masterCompanyId + ".office.locations.css.selector" + " property is missing. Please update properties file!");
			}
			
			linkAttributeForURL = systemProperties.getProperty(masterCompanyId + ".link.attr.for.url").trim();
			if (StringHelper.isEmpty(linkAttributeForURL)) {
				throw new Exception(masterCompanyId + ".link.attr.for.url" + " property is missing. Please update properties file!");
			}
			
			String addtionalLocationURLs = systemProperties.getProperty(masterCompanyId + ".addtional.location.urls").trim();
			additionalURLsSet = new HashSet<String>();
			if (StringHelper.isNotEmpty(addtionalLocationURLs)) {
				String[] separateLocationURLs = addtionalLocationURLs.split(Constants.SPACE);
				for (String separateLocationURL : separateLocationURLs) {
					additionalURLsSet.add(separateLocationURL);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
}
