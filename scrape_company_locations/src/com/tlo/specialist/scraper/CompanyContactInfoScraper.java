package com.tlo.specialist.scraper;

public interface CompanyContactInfoScraper {
	
	public void scrapeCompanyContactInformation(String masterCompanyId, String masterCompanyName, String outputFilePath) throws Exception;

}
