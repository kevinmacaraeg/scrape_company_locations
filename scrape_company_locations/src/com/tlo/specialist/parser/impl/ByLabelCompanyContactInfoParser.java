package com.tlo.specialist.util.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.util.CompanyContactInfoParser;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.StringHelper;

public class IBMCompanyContactInfoParser implements CompanyContactInfoParser {
	
	String[] fullCompanyContactInfoWords;
	
	@Override
	public CompanyContactInformation parseCompanyContactInformation(String masterCompanyId, String masterCompanyName, String websiteLocationsLink, String fullCompanyContactInformation) throws Exception {
		CompanyContactInformation companyContactInfo = null;
		try {
				
			if (fullCompanyContactInformation.contains("General contact information")) {
				String address = null;
				String phoneNumber = null;
				String faxNumber = null;
				String email = null;
				
				fullCompanyContactInformation = removeNoiseWords(fullCompanyContactInformation);
				fullCompanyContactInfoWords = fullCompanyContactInformation.split(Constants.SPACE);
				for (String label : Constants.IBM_COMPANY_CONTACT_INFO_LABELS) {
					for (int i = 0; i < fullCompanyContactInfoWords.length; i++) {
						String currentWord = fullCompanyContactInfoWords[i];
						if (label.equalsIgnoreCase(currentWord.trim()) || currentWord.contains(label)) {
							if ("Tel:".equalsIgnoreCase(currentWord)) {
								phoneNumber = getContactInformation(currentWord, i);
							} else if ("Fax:".equalsIgnoreCase(currentWord)) {
								faxNumber = getContactInformation(currentWord, i);
							} else if ("E-mail:".equalsIgnoreCase(currentWord)) {
								email = fullCompanyContactInfoWords[i + 1].trim();
							} else if (currentWord.contains("E-mail:")) {
								email = currentWord.split(Constants.COLON)[1].trim();
							} else if ("Address".equalsIgnoreCase(currentWord) || "Address:".equalsIgnoreCase(currentWord)) {
								if (StringHelper.isEmpty(address)) {
									address = getContactInformation(currentWord, i);
								} else {
									address = address + Constants.WINDOWS_NEW_LINE + getContactInformation(currentWord, i);
								}
							}
						}
					}
				}
				
				companyContactInfo = new CompanyContactInformation();
				companyContactInfo.setMasterCompanyId(masterCompanyId);
				companyContactInfo.setMasterCompanyName(masterCompanyName);
				companyContactInfo.setCompanyLocationsUrl(websiteLocationsLink);
				companyContactInfo.setAddress(StringHelper.replaceNullValue(address, Constants.EMPTY_STRING).trim());
				companyContactInfo.setPhoneNumber(StringHelper.replaceNullValue(phoneNumber, Constants.EMPTY_STRING).trim());
				companyContactInfo.setFaxNumber(StringHelper.replaceNullValue(faxNumber, Constants.EMPTY_STRING).trim());
				companyContactInfo.setEmail(StringHelper.replaceNullValue(email, Constants.EMPTY_STRING).trim());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return companyContactInfo;
	}

	@Override
	public List<CompanyContactInformation> parseCompanyContactInformation(String masterCompanyId, String masterCompanyName, String websiteLocationsLink, Set<String> fullCompanyContactInformationSet) throws Exception {
		List<CompanyContactInformation> companyContactInfoList = null;
		try {
			
			companyContactInfoList = new ArrayList<CompanyContactInformation>();
			
			for (String fullCompanyContactInfo : fullCompanyContactInformationSet) {
				
				CompanyContactInformation companyContactInfo = parseCompanyContactInformation(masterCompanyId, masterCompanyName, websiteLocationsLink, fullCompanyContactInfo);
				
				if (companyContactInfo != null) {
					companyContactInfoList.add(companyContactInfo);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return companyContactInfoList;
	}

	public String removeNoiseWords(String fullCompanyContactInfo) {
		return fullCompanyContactInfo.replace("General contact information", Constants.EMPTY_STRING)
				.replace("General information", Constants.EMPTY_STRING)
				.replace("General inquiries", Constants.EMPTY_STRING)
				.replace("Shopping Tel:", Constants.EMPTY_STRING)
				.replace("IBM Business Partners Information", Constants.EMPTY_STRING)
				.replaceAll("Visit IBM in your Country IBM's various branches and virtual branches are designed to provide services to the regional customers in the most convenient and rapid way.", Constants.EMPTY_STRING)
				.replaceAll("(?i)Technical support Electronic service requests can be submitted for hardware or software under warranty or with a support contract.", Constants.EMPTY_STRING)
				.replaceAll("(?i)Support", Constants.EMPTY_STRING)
				.replaceAll("www.ibm.com[^\\s]*", Constants.EMPTY_STRING)
				.replaceAll("Open Service Request.*", Constants.EMPTY_STRING)
				.replaceAll("[1-9] Note [1-9]:.*", Constants.EMPTY_STRING)
				.replaceAll("[1-9\\S]For calls made.*|[1-9\\S] For calls made.*", Constants.EMPTY_STRING)
				.replaceAll("\\SEnglish only during off shift hours|[1-9] English only during off shift hours", Constants.EMPTY_STRING)
				.replaceAll("[1-9] English only during off shift|[1-9]English only during off shift", Constants.EMPTY_STRING)
				.replaceAll("\\Scoverage 08-19X5|[1-9] coverage 08-19X5", Constants.EMPTY_STRING)
				.replaceAll("[1-9] English/Canada French|[1-9]English/Canada French", Constants.EMPTY_STRING)
				.replaceAll("[1-9] Toll Free number.*|[1-9]Toll Free number.*", Constants.EMPTY_STRING)
				.replaceAll("[1-9] General Information.*|[1-9]General Information.*", Constants.EMPTY_STRING)
				.trim();
		
	}
	
	private String getContactInformation(String currentWord, int currentWordIndex) throws Exception {
		StringBuilder contactInformationBuilder = null;
		try {
			contactInformationBuilder = new StringBuilder();
			for (int j = currentWordIndex + 1; j < fullCompanyContactInfoWords.length; j++) {
				String nextWord = fullCompanyContactInfoWords[j];
				if (!StringHelper.stringArrayContainsString(Constants.IBM_COMPANY_CONTACT_INFO_LABELS, nextWord)) {
					String[] splitByColon = {nextWord};
					if (!Constants.COLON.equalsIgnoreCase(nextWord)) {
						splitByColon = nextWord.split(Constants.COLON);
					}
					if (!StringHelper.stringArrayContainsString(Constants.IBM_COMPANY_CONTACT_INFO_LABELS, splitByColon[0])) {
						contactInformationBuilder.append(nextWord).append(Constants.SPACE);
					} else {
						break;
					}
				} else {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return contactInformationBuilder.toString().trim();
	}
}
