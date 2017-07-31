package com.tlo.specialist.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
	
	public static boolean isEmpty(String currentString) {
		return (currentString == null || Constants.EMPTY_STRING.equalsIgnoreCase(currentString));
	}
	
	public static boolean isNotEmpty(String currentString) {
		return !isEmpty(currentString);
	}
	
	public static boolean containsSubstringIgnoreCase(String currentString, String subString) throws Exception {
		try {
			currentString = currentString.toLowerCase();
			subString = subString.toLowerCase();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return currentString.contains(subString);
	}
	
	public static boolean containsNonBreakingSpace(String currentString) {
		return currentString.contains(Constants.NON_BREAKING_SPACE) || currentString.contains(Constants.NON_BREAKING_SPACE1) || currentString.contains(Constants.NON_BREAKING_SPACE2) || currentString.contains(Constants.NON_BREAKING_SPACE3);
	}
	
	public static boolean containsPattern(String currentString, String regex) throws Exception {
		Pattern pattern = null;
		Matcher matcher = null;
		try {
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(currentString);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return matcher.find();
	}
	
	public static boolean matchesPattern(String currentString, String regex) throws Exception {
		Pattern pattern = null;
		Matcher matcher = null;
		try {
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(currentString);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return matcher.matches();
	}
	
	public static boolean stringArrayContainsString(String[] stringArray, String currentString) throws Exception {
		try {
			for (String stringFromArray : stringArray) {
				if (stringFromArray.equals(currentString)) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	public static String addForwardSlashAtStart(String currentString) {
		if (!currentString.startsWith(Constants.FORWARD_SLASH)) {
			currentString = new StringBuilder(Constants.FORWARD_SLASH).append(currentString).toString();
		}
		return currentString;
	}
	
	public static String addForwardSlashAtEnd(String currentString) {
		if (!currentString.endsWith(Constants.FORWARD_SLASH)) {
			currentString = new StringBuilder(currentString).append(Constants.FORWARD_SLASH).toString();
		}
		return currentString;
	}
	
	public static String encloseByDoubleQuotes(String currentString) {
		return new StringBuilder(Constants.DOUBLE_QUOTE).append(currentString).append(Constants.DOUBLE_QUOTE).toString();
	}
	
	public static String removeFirstAndLastCharacters(String currentString) {
		return currentString = currentString.substring(1, currentString.length() - 1);
	}
	
	public static String removeWebsiteUrlProtocols(String websiteUrl) {
		return websiteUrl.replace("http://", Constants.EMPTY_STRING).replace("www.", Constants.EMPTY_STRING);
	}
	
	public static String replaceEachNonBreakingSpaceWithSpace(String currentString) {
		return currentString.replace(Constants.NON_BREAKING_SPACE, Constants.SPACE).replace(Constants.NON_BREAKING_SPACE1, Constants.SPACE).replace(Constants.NON_BREAKING_SPACE2, Constants.SPACE).replace(Constants.NON_BREAKING_SPACE3, Constants.SPACE);
	}
	
	public static String replaceNullValue(String currentString, String replaceValue) {
		if (currentString == null) {
			currentString = replaceValue;
		}
		return currentString;
	}
	
	public static String replaceAccentedLettersWithNormalEnglishAlphabet(String currentString) throws Exception {
		String newString = null;
		try {
			String normalizedString = Normalizer.normalize(currentString, Normalizer.Form.NFKD);
			
			newString =  new String(normalizedString.replaceAll(Constants.REGEX_SPECIAL_LETTERS, Constants.EMPTY_STRING).getBytes(Constants.LITERAL_ASCII), Constants.LITERAL_ASCII);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return newString;
	}
	
	public static String getPhoneNumbersFromString(String currentString) throws Exception {
		StringBuilder phoneNumberBuilder = null;
		Pattern pattern = null;
		Matcher matcher = null;
		try {
			pattern = Pattern.compile(Constants.REGEX_PHONE_NUMBER);
			matcher = pattern.matcher(currentString);
			
			phoneNumberBuilder = new StringBuilder();
			while (matcher.find()) {
				phoneNumberBuilder.append(matcher.group(0));
				phoneNumberBuilder.append(Constants.SEMI_COLON);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return phoneNumberBuilder.toString();
	}
	
	public static String getPhoneNumberLabel(String currentString) {
		for (String phoneNumberLabel : Constants.PHONE_NUMBER_LABELS) {
			if (currentString.contains(phoneNumberLabel)) {
				return phoneNumberLabel;
			}
		}
		return Constants.EMPTY_STRING;
	}
	
	public static String getFaxNumberLabel(String currentString) {
		for (String faxNumberLabel : Constants.FAX_NUMBER_LABELS) {
			if (currentString.contains(faxNumberLabel)) {
				return faxNumberLabel;
			}
		}
		return Constants.EMPTY_STRING;
	}
	
	public static String getEmailLabel(String currentString) {
		for (String emailLabel : Constants.EMAIL_LABELS) {
			if (currentString.contains(emailLabel)) {
				return emailLabel;
			}
		}
		return Constants.EMPTY_STRING;
	}
	
	public static List<String> getISOCountriesList() throws Exception {
		List<String> countryList = null;
		try {
			
			countryList = new ArrayList<String>();
			
			Locale US = new Locale("en", "US");
			
			String[] locales = Locale.getISOCountries();
			for (String countryCode : locales) {
				countryList.add(new Locale(Constants.EMPTY_STRING, countryCode).getDisplayCountry(US));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return countryList;
	}
	
}
