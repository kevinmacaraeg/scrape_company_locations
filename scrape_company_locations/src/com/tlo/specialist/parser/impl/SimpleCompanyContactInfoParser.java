package com.tlo.specialist.util.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.util.CompanyContactInfoParser;
import com.tlo.specialist.util.Constants;
import com.tlo.specialist.util.StringHelper;

public class SimpleCompanyContactInfoParser implements CompanyContactInfoParser {
	
	@Override
	public CompanyContactInformation parseCompanyContactInformation(String masterCompanyId, String masterCompanyName, String websiteLocationsLink, String fullCompanyContactInformation) throws Exception {
		CompanyContactInformation companyContactInfo = null;
		try {
				
			String address = Constants.EMPTY_STRING;
			String phoneNumber = Constants.EMPTY_STRING;
			String faxNumber = Constants.EMPTY_STRING;
			String email = Constants.EMPTY_STRING;
			String phoneNumberLabel = Constants.EMPTY_STRING;
			String faxNumberLabel = Constants.EMPTY_STRING;
			String emailLabel = Constants.EMPTY_STRING;

			fullCompanyContactInformation = StringHelper.replaceEachNonBreakingSpaceWithSpace(fullCompanyContactInformation).trim();
			fullCompanyContactInformation = fullCompanyContactInformation.replaceAll(Constants.REGEX_WHITESPACES, Constants.SPACE);
			fullCompanyContactInformation = removeNoiseWords(fullCompanyContactInformation);
			fullCompanyContactInformation = fullCompanyContactInformation.replace(" T ", " T: ").replace(" F ", " F: ");
			fullCompanyContactInformation = fullCompanyContactInformation.replace(" t ", " t: ").replace(" p ", " p: ").replace(" f ", " f: ");
			fullCompanyContactInformation = fullCompanyContactInformation.replace("p+", "p:+").replace("f+", "f:+");
			
			if (StringHelper.isNotEmpty(fullCompanyContactInformation)) {
			
				String[] phoneSplit = {fullCompanyContactInformation};
				phoneNumberLabel = StringHelper.getPhoneNumberLabel(fullCompanyContactInformation);
				if (StringHelper.isNotEmpty(phoneNumberLabel)) {
					phoneSplit = fullCompanyContactInformation.split(phoneNumberLabel);
				}
	
				String[] faxSplit = {fullCompanyContactInformation};
				if (phoneSplit.length > 1) {
					
					address = phoneSplit[0].trim();
					StringBuilder phoneNumberBuilder = new StringBuilder();
					for (int i = 1; i < phoneSplit.length; i++) {
						if (i > 1 && i < phoneSplit.length) {
							phoneNumberBuilder.append(Constants.SEMI_COLON);
						}
						phoneNumberBuilder.append(phoneSplit[i].trim());
					}
					phoneNumber = phoneNumberBuilder.toString();
					
					faxNumberLabel = StringHelper.getFaxNumberLabel(phoneNumber);
					if (StringHelper.isNotEmpty(faxNumberLabel)) {
						faxSplit = phoneNumber.split(faxNumberLabel);
						if (faxSplit.length > 0) {
							phoneNumber = faxSplit[0].trim();
						}
					} else {
						faxNumberLabel = StringHelper.getFaxNumberLabel(address);
						if (StringHelper.isNotEmpty(faxNumberLabel)) {
							faxSplit = address.split(faxNumberLabel);
							if (faxSplit.length > 0) {
								address = faxSplit[0].trim();
							}
						}
					}
					
				} else {
					
					faxNumberLabel = StringHelper.getFaxNumberLabel(fullCompanyContactInformation);
					if (StringHelper.isNotEmpty(faxNumberLabel)) {
						faxSplit = fullCompanyContactInformation.split(faxNumberLabel);
					}
					
					if (faxSplit.length > 0) {
						address = faxSplit[0].trim();
					}
					
				}
				
				if (faxSplit.length > 1) {
					
					StringBuilder faxNumberBuilder = new StringBuilder();
					for (int i = 1; i < faxSplit.length; i++) {
						if (i > 1 && i < faxSplit.length) {
							faxNumberBuilder.append(Constants.SEMI_COLON);
						}
						faxNumberBuilder.append(faxSplit[i].trim());
					}
					faxNumber = faxNumberBuilder.toString();
					
				}
				
				boolean isEmailLabelInAddress = false;
				boolean isEmailLabelInPhoneNumber = false;
				boolean isEmailLabelInFax = false;
				String[] emailSplit = {address};
				emailLabel = StringHelper.getEmailLabel(address);
				if (StringHelper.isNotEmpty(emailLabel)) {
					isEmailLabelInAddress = true;
					emailSplit = address.split(emailLabel);
				} else {
					
					emailLabel= StringHelper.getEmailLabel(phoneNumber);
					 
					if (StringHelper.isNotEmpty(emailLabel)) {
						isEmailLabelInPhoneNumber = true;
						emailSplit = phoneNumber.split(emailLabel);
					} else {
						
						emailLabel= StringHelper.getEmailLabel(faxNumber);
						
						if (StringHelper.isNotEmpty(emailLabel)) {
							isEmailLabelInFax = true;
							emailSplit = faxNumber.split(emailLabel);
						} else {
							if (containsEmailSubstring(address)) {
								email = StringHelper.replaceNullValue(getEmailFromString(address), Constants.EMPTY_STRING);
								address = address.replace(email, Constants.EMPTY_STRING).trim();
							} else if (containsEmailSubstring(phoneNumber)) {
								email = StringHelper.replaceNullValue(getEmailFromString(phoneNumber), Constants.EMPTY_STRING);
								phoneNumber = phoneNumber.replace(email, Constants.EMPTY_STRING).trim();
							} else if (containsEmailSubstring(faxNumber)) {
								email = StringHelper.replaceNullValue(getEmailFromString(faxNumber), Constants.EMPTY_STRING);
								faxNumber = faxNumber.replace(email, Constants.EMPTY_STRING).trim();
							}
						}
	
					}
				}
				
				if (emailSplit.length > 1) {
					if (isEmailLabelInAddress) {
						address = emailSplit[0];
					} else if (isEmailLabelInPhoneNumber) {
						phoneNumber = emailSplit[0];
					} else if (isEmailLabelInFax) {
						faxNumber = emailSplit[0];
					}
					StringBuilder emailBuilder = new StringBuilder();
					for (int i = 1; i < emailSplit.length; i++) {
						String newEmail = emailSplit[i].trim();
						if (StringHelper.isNotEmpty(email)) {
							emailBuilder.append(Constants.SEMI_COLON);
						}
						if (StringHelper.isNotEmpty(newEmail)) {
							emailBuilder.append(newEmail);
						}
					}
					email = emailBuilder.toString();
				}
				
				StringBuilder phoneNumberBuilder = new StringBuilder();
				if (StringHelper.isNotEmpty(phoneNumber)) {
					phoneNumberBuilder.append(phoneNumber);
					if (!phoneNumber.endsWith(Constants.SEMI_COLON)) {
						phoneNumberBuilder.append(Constants.SEMI_COLON);
					}
				}
				String phoneNumbersFromAddress = StringHelper.getPhoneNumbersFromString(address);
				phoneNumberBuilder.append(phoneNumbersFromAddress);
				if (StringHelper.isNotEmpty(phoneNumbersFromAddress)) {
					address = address.replaceAll(Constants.REGEX_PHONE_NUMBER, Constants.EMPTY_STRING);
					if (!phoneNumbersFromAddress.endsWith(Constants.SEMI_COLON)) {
						phoneNumberBuilder.append(Constants.SEMI_COLON);
					}
				}
				String phoneNumbersFromEmail = StringHelper.getPhoneNumbersFromString(email);
				phoneNumberBuilder.append(phoneNumbersFromEmail);
				if (StringHelper.isNotEmpty(phoneNumbersFromEmail)) {
					email = email.replaceAll(Constants.REGEX_PHONE_NUMBER, Constants.EMPTY_STRING);
					if (!phoneNumbersFromEmail.endsWith(Constants.SEMI_COLON)) {
						phoneNumberBuilder.append(Constants.SEMI_COLON);
					}
				}
				
				phoneNumber = phoneNumberBuilder.toString();
				
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
				
				CompanyContactInformation contactInfo = parseCompanyContactInformation(masterCompanyId, masterCompanyName, websiteLocationsLink, fullCompanyContactInfo);
				
				companyContactInfoList.add(contactInfo);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return companyContactInfoList;
	}
	
	public String removeNoiseWords(String fullCompanyContactInfo) {
		String[] wordsFromFullCompanyContactInfo = fullCompanyContactInfo.split(Constants.SPACE);
		StringBuilder newFullCompanyContactInfo = new StringBuilder();
		for (String word : wordsFromFullCompanyContactInfo) {
			if (!word.matches(Constants.REGEX_WEB_URL)) {
				newFullCompanyContactInfo.append(word).append(Constants.SPACE);
			}
		}
		fullCompanyContactInfo = newFullCompanyContactInfo.toString();
		for (String noiseWord : Constants.SIMPLE_COMPANY_CONTACT_INFO_PARSER_NOISE_WORDS) {
			fullCompanyContactInfo = fullCompanyContactInfo.replace(noiseWord, Constants.EMPTY_STRING);
		}
		fullCompanyContactInfo = fullCompanyContactInfo.replaceAll(".*Visit*.*Website*.", Constants.EMPTY_STRING)
													.replaceAll("Go to.*.site\\.?", Constants.EMPTY_STRING)
													.replaceAll("More about our.*.presence >?>?", Constants.EMPTY_STRING)
													.replaceAll("Find out more about \\w+ \\w+ \\w+, \\w+ \\w+|Find out more about \\w+ \\w+ \\w+, \\w+|Find out more about \\w+ \\w+ \\w+", Constants.EMPTY_STRING)
													.replaceAll("\\d+ miles away", Constants.EMPTY_STRING);
		return fullCompanyContactInfo.trim();
	}
	
	boolean containsEmailSubstring(String currentString) throws Exception {
		if (StringHelper.isNotEmpty(currentString)) {
			return StringHelper.containsPattern(currentString, Constants.REGEX_EMAIL);
		} else {
			return false;
		}
	}
	
	boolean containsPhoneNumberSubstring(String currentString) throws Exception {
		if (StringHelper.isNotEmpty(currentString)) {
			return StringHelper.containsPattern(currentString, Constants.REGEX_PHONE_NUMBER);
		} else {
			return false;
		}
	}
	
	boolean isStringAnEmail(String currentString) throws Exception {
		return StringHelper.matchesPattern(currentString, Constants.REGEX_EMAIL);
	}

	String getEmailFromString(String currentString) throws Exception {
		String email = null;
		try {
			String[] currentStringWords = currentString.split(Constants.SPACE);
			for (String word : currentStringWords) {
				if (isStringAnEmail(word)) {
					email = word;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return email;
	}

}
