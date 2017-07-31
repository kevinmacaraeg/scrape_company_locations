package com.tlo.specialist.util;

import java.util.List;
import java.util.Set;

import com.tlo.specialist.domain.CompanyContactInformation;

public interface CompanyContactInfoParser {
	
	public CompanyContactInformation parseCompanyContactInformation(String masterCompanyId, String masterCompanyName, String websiteLocationsLink, String fullCompanyContactInformation) throws Exception;
	
	public List<CompanyContactInformation> parseCompanyContactInformation(String masterCompanyId, String masterCompanyName, String websiteLocationsLink, Set<String> fullCompanyContactInformationSet) throws Exception;
	
	public String removeNoiseWords(String fullCompanyContactInfo);
	
}
