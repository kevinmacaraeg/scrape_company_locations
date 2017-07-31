package com.tlo.specialist.util;

import java.util.HashMap;
import java.util.Map;

public class Constants {

	public static final String EMPTY_STRING = "";
	public static final String SINGLE_QUOTE = "\'";
	public static final String DOUBLE_QUOTE = "\"";
	public static final String LEFT_DOUBLE_QUOTE = "\u201C";
	public static final String RIGHT_DOUBLE_QUOTE = "\u201D";
	public static final String COMMA = ",";
	public static final String PERIOD = ".";
	public static final String FORWARD_SLASH = "/";
	public static final String BACKWARD_SLASH = "\\";
	public static final String WINDOWS_NEW_LINE = "\n";
	public static final String TAB = "\t";
	public static final String SPACE = " ";
	public static final String DASH = "-";
	public static final String EN_DASH = "–";
	public static final String UNDERSCORE = "_";
	public static final String COLON = ":";
	public static final String SEMI_COLON = ";";
	public static final String QUESTION_MARK = "?";
	public static final String NON_BREAKING_SPACE = "&nbsp;";
	public static final String NON_BREAKING_SPACE1 = "\u00A0";
	public static final String NON_BREAKING_SPACE2 = "\u2007";
	public static final String NON_BREAKING_SPACE3 = "\u202F";
	
	public static final String DELIMITER_COMMA = ",";
	
	//LITERALS
	public static final String LITERAL_ASCII = "ascii";
	public static final String LITERAL_HTTP = "http";
	public static final String LITERAL_WWW = "www";
	
	//REGEX
	public static final String REGEX_SPECIAL_LETTERS = "[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+";
	public static final String REGEX_INVISIBLE_CONTROL_CHAR = "\\p{C}";
	public static final String REGEX_HTML_TAG = "<[^>]+>";
	public static final String REGEX_ANY_WORD1_ANY_BETWEEN_MAX2_WORD2_ANY = ".*\\bSTRING1\\W*(?:\\w+\\W+){0,2}?STRING2\\b.*";
	public static final String REGEX_ANY_WORD1_ANY_BETWEEN_MAX2_WORD2_ANY_BETWEEN_MAX2_WORD3_ANY = ".*\\bSTRING1\\W*(?:\\w+\\W+){0,2}?STRING2\\W*(?:\\w+\\W+){0,2}?STRING3\\b.*";
	public static final String REGEX_WHITESPACES = "\\s+";
	public static final String REGEX_TWO_OR_MORE_SPACES = " {2,}";
	public static final String REGEX_NON_ALPHANUMERIC_OR_SPACE = "[^A-Za-z0-9 ]";
	public static final String REGEX_EMAIL = "(?:[a-zA-Z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&\'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
	public static final String REGEX_PHONE_NUMBER = "\\(\\+\\d+\\) \\d+\\-\\d+\\-\\d+"
													+ "|\\(\\+\\d+\\)\\d+\\-\\d+\\-\\d+"
													+ "|\\(\\+\\d+\\) \\d+ \\d+ \\d+ \\d+"
													+ "|\\(\\+\\d+\\) \\d+ \\d+ \\d+"
													+ "|\\(\\+\\d+\\) \\d+ \\d+"
													+ "|\\(\\+\\d+\\) \\d+"
													+ "|\\+\\d+\\-\\(\\d+\\)\\-\\d+\\-\\d+"
													+ "|\\+\\d+\\(\\d+\\)\\d+ \\d+ \\d+ \\d+"
													+ "|\\+\\d+\\(\\d+\\)\\d+ \\d+ \\d+"
													+ "|\\+\\d+\\(\\d+\\)\\d+ \\d+"
													+ "|\\+\\d+\\(\\d+\\)\\d+"
													+ "|\\+\\d+\\(\\d+\\) \\d+ \\d+ \\d+ \\d+"
													+ "|\\+\\d+\\(\\d+\\) \\d+ \\d+ \\d+"
													+ "|\\+\\d+\\(\\d+\\) \\d+ \\d+"
													+ "|\\+\\d+\\(\\d+\\) \\d+"
													+ "|\\+\\d+\\(\\d+\\)\\d+\\- \\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\) \\d+ \\d+\\-\\d+"
													+ "|\\+\\d+ \\(\\d+\\) \\d+\\-\\d+\\-\\d+"
													+ "|\\+\\d+ \\(\\d+\\) \\d+\\-\\d+"
													+ "|\\+\\d+ \\(\\d+\\)\\-\\d+\\-\\d+"
													+ "|\\+\\d+ \\(\\d+\\)\\d+ \\d+ \\- \\d+"
													+ "|\\+\\d+ \\(\\d+\\)\\d+ \\d+ \\d+ \\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\)\\d+ \\d+ \\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\)\\d+ \\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\)\\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\)\\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\)\\d+"
													+ "|\\+\\d+ \\(\\d+\\) \\d+ \\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\) \\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\) \\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\) \\d+"
													+ "|\\+\\d+\\-\\d+\\- \\d+\\-\\d+"
													+ "|\\+\\d+ \\d+ \\d+\\-\\d+"
													+ "|\\+\\d+ \\d+\\-\\d+\\-\\d+\\-\\d+"
													+ "|\\+\\d+ \\d+\\-\\d+\\-\\d+"
													+ "|\\+\\d+ \\d+\\-\\d+"
													+ "|\\+\\d+\\-\\d+\\-[A-Z]+\\-[A-Z]+"
													+ "|\\+\\d+\\-\\d+\\-\\d+\\-\\d+"
													+ "|\\+\\d+\\-\\d+\\-\\d+"
													+ "|\\+\\d+\\-\\d+"
													+ "|\\+\\d+ \\d+\\.\\d+\\.\\d+"
													+ "|\\+\\d+\\.\\(\\d+\\)\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+"
													+ "|\\+\\d+\\.\\(\\d+\\)\\d+\\.\\d+\\.\\d+\\.\\d+"
													+ "|\\+\\d+\\.\\(\\d+\\)\\d+\\.\\d+\\.\\d+"
													+ "|\\+\\d+\\.\\(\\d+\\)\\d+\\.\\d+"
													+ "|\\+\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+, \\W+\\. \\d+\\-\\d+"
													+ "|\\+\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+, \\W+\\. \\d+"
													+ "|\\+\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+\\/\\d+\\/\\d+\\/\\d+"
													+ "|\\+\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+"
													+ "|\\+\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+"
													+ "|\\+\\d+\\.\\d+\\.\\d+\\.\\d+"
													+ "|\\+\\d+\\.\\d+\\.\\d+"
													+ "|\\+\\d+\\.\\d+"
													+ "|\\+\\d+ \\d+ \\d+ \\d+\\/\\d+\\/\\d+\\/\\d+"
													+ "|\\+\\d+ \\d+ \\d+ \\d+\\/\\d+"
													+ "|\\+\\d+ \\d+\\/\\d+"
													+ "|\\+\\d+ \\d+ \\d+ \\/\\d+"
													+ "|\\+\\d+ \\d+ \\d+ \\d+ \\d+\\-\\d+"
													+ "|\\+\\d+ \\d+ \\d+ \\d+ \\d+ \\(ext\\. \\d+\\)"
													+ "|\\+\\d+ \\d+ \\d+\\/\\d+\\/\\d+"
													+ "|\\+\\d+ \\d+ \\d+ \\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\d+ \\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\d+"
													+ "|\\+\\d{6,}"
													+ "|\\+\\d+ \\(\\d+\\)\\d+ \\d+ \\d+"
													+ "|\\+ \\d+ \\(\\d+\\) \\d+ \\d+ \\d+"
													+ "|\\+ \\d+ \\d+ \\d+ \\d+ \\d+ \\(ext\\. \\d+\\)"
													+ "|\\+ \\d+ \\d+ \\d+ \\d+ \\d+"
													+ "|\\+ \\d+ \\d+ \\d+ \\d+"
													+ "|\\+ \\d+ \\d+"
													+ "|\\(\\d+ \\d+\\) \\d+ \\d+ \\d+ \\d+"
													+ "|\\(\\d+ \\d+\\) \\d+ \\d+"
													+ "|\\( \\d+ \\) \\d+\\ \\d+ \\d+\\/ \\d+"
													+ "|\\( \\d+ \\) \\d+\\ \\d+ \\d+"
													+ "|\\(\\d+\\)\\-\\d+\\-\\d+"
													+ "|\\(\\d+\\) \\d+\\.\\d+ Ext\\. \\d+"
													+ "|\\(\\d+\\) \\d+\\.\\d+"
													+ "|\\(\\d+\\) \\d+\\-\\d+, Ext\\. \\d+"
													+ "|\\(\\d+\\) \\d+\\- \\d+"
													+ "|\\(\\d+\\) \\d+\\-\\d+"
													+ "|\\(\\d+\\)\\d+\\-\\d+"
													+ "|\\d+\\.\\d+\\.\\d+ \\d+\\.\\d+"
													+ "|\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+"
													+ "|\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+"
													+ "|\\d+\\.\\d+\\.\\d+\\.\\d+"
													+ "|\\d+\\.\\d+\\.\\d+"
													+ "|\\d+\\-\\d+\\-\\d+\\-[A-Z]+ \\(\\d+\\)"
													+ "|\\d+\\-\\d+\\-[A-Z0-9]+ \\(\\d+\\)"
													+ "|\\d+\\-\\d+\\-[A-Z0-9]+"
													+ "|\\d+\\-\\d+\\-\\d+\\-\\d+ \\(toll free\\)"
													+ "|\\d+\\-\\d+\\-\\d+ \\(Toll Free\\)"
													+ "|\\d+\\-\\d+\\-\\d+ \\d+\\s"
													+ "|\\d+\\-\\d+\\-\\d+ \\d+"
													+ "|\\d+\\-\\d+ \\d+ \\d+"
													+ "|\\d+\\-[A-Z]+ \\(\\d+\\)"
													+ "|\\d+\\-\\d+\\-\\d+\\-\\d+"
													+ "|\\d{3,}\\-\\d{3,}\\-\\d{3,}"
													+ "|\\d{4,}\\-\\d{5,}" 
													+ "|\\d+ \\(\\d+\\)\\d+ \\d+ \\d+"
													+ "|\\d+ \\(\\d+\\) \\d+\\-[A-Z]+ \\(\\d+\\)"
													+ "|\\d+ \\(\\d+\\) \\d+ \\d+"
													+ "|\\d+ \\d+ \\d+ \\d+ ext. \\d+"
													+ "|\\d+ \\d+ \\d+\\-\\d+"
													+ "|\\d+\\- \\d+"
													+ "|\\d+ \\d+ \\d+ \\d+ \\d+"
													+ "|\\d+ \\d+ \\d+ \\d+"
													+ "|\\d{2,} \\d{1,} \\d{4,}"
													+ "|\\d{1,} \\d{2,} \\d{4,}"
													+ "|\\d{7,}";

	public static final String REGEX_WEB_URL = "^(https:\\/\\/|http:\\/\\/)?(www\\.)?([a-zA-Z0-9\\-]+\\.)[a-z]+((\\/|\\.)\\S*)*$|^(https:\\/\\/|http:\\/\\/)?([a-zA-Z0-9\\-]+\\.)([a-zA-Z0-9\\-]+\\.)[a-z]+((\\/|\\.)\\S*)*$";
	
	//HTML
	public static final String HTML_ELEMENT_A = "a";
	public static final String HTML_ELEMENT_H3 = "h3";
	public static final String HTML_ELEMENT_HR = "hr";
	public static final String HTML_ELEMENT_I = "i";
	public static final String HTML_ELEMENT_IMG = "img";
	public static final String HTML_ELEMENT_P = "p";
	public static final String HTML_ELEMENT_TD = "td";
	public static final String HTML_ELEMENT_TR = "tr";
	
	public static final String HTML_ELEMENT_ATTR_ALT = "alt";
	public static final String HTML_ELEMENT_ATTR_DATA_ID = "data-id";
	public static final String HTML_ELEMENT_ATTR_ID = "id";
	public static final String HTML_ELEMENT_ATTR_HREF = "href";
	public static final String HTML_ELEMENT_ATTR_DATA_HREF = "data-href";
	public static final String HTML_ELEMENT_ATTR_VALUE = "value";
	
	public static final Map<String, String> US_STATES_MAP;
	static {
	    US_STATES_MAP = new HashMap<String, String>();
	    US_STATES_MAP.put("AL", "Alabama");
	    US_STATES_MAP.put("AK", "Alaska");
	    US_STATES_MAP.put("AB", "Alberta");
	    US_STATES_MAP.put("AZ", "Arizona");
	    US_STATES_MAP.put("AR", "Arkansas");
	    US_STATES_MAP.put("BC", "British Columbia");
	    US_STATES_MAP.put("CA", "California");
	    US_STATES_MAP.put("CO", "Colorado");
	    US_STATES_MAP.put("CT", "Connecticut");
	    US_STATES_MAP.put("DE", "Delaware");
	    US_STATES_MAP.put("DC", "District Of Columbia");
	    US_STATES_MAP.put("FL", "Florida");
	    US_STATES_MAP.put("GA", "Georgia");
	    US_STATES_MAP.put("GU", "Guam");
	    US_STATES_MAP.put("HI", "Hawaii");
	    US_STATES_MAP.put("ID", "Idaho");
	    US_STATES_MAP.put("IL", "Illinois");
	    US_STATES_MAP.put("IN", "Indiana");
	    US_STATES_MAP.put("IA", "Iowa");
	    US_STATES_MAP.put("KS", "Kansas");
	    US_STATES_MAP.put("KY", "Kentucky");
	    US_STATES_MAP.put("LA", "Louisiana");
	    US_STATES_MAP.put("ME", "Maine");
	    US_STATES_MAP.put("MB", "Manitoba");
	    US_STATES_MAP.put("MD", "Maryland");
	    US_STATES_MAP.put("MA", "Massachusetts");
	    US_STATES_MAP.put("MI", "Michigan");
	    US_STATES_MAP.put("MN", "Minnesota");
	    US_STATES_MAP.put("MS", "Mississippi");
	    US_STATES_MAP.put("MO", "Missouri");
	    US_STATES_MAP.put("MT", "Montana");
	    US_STATES_MAP.put("NE", "Nebraska");
	    US_STATES_MAP.put("NV", "Nevada");
	    US_STATES_MAP.put("NB", "New Brunswick");
	    US_STATES_MAP.put("NH", "New Hampshire");
	    US_STATES_MAP.put("NJ", "New Jersey");
	    US_STATES_MAP.put("NM", "New Mexico");
	    US_STATES_MAP.put("NY", "New York");
	    US_STATES_MAP.put("NF", "Newfoundland");
	    US_STATES_MAP.put("NC", "North Carolina");
	    US_STATES_MAP.put("ND", "North Dakota");
	    US_STATES_MAP.put("NT", "Northwest Territories");
	    US_STATES_MAP.put("NS", "Nova Scotia");
	    US_STATES_MAP.put("NU", "Nunavut");
	    US_STATES_MAP.put("OH", "Ohio");
	    US_STATES_MAP.put("OK", "Oklahoma");
	    US_STATES_MAP.put("ON", "Ontario");
	    US_STATES_MAP.put("OR", "Oregon");
	    US_STATES_MAP.put("PA", "Pennsylvania");
	    US_STATES_MAP.put("PE", "Prince Edward Island");
	    US_STATES_MAP.put("PR", "Puerto Rico");
	    US_STATES_MAP.put("QC", "Quebec");
	    US_STATES_MAP.put("RI", "Rhode Island");
	    US_STATES_MAP.put("SK", "Saskatchewan");
	    US_STATES_MAP.put("SC", "South Carolina");
	    US_STATES_MAP.put("SD", "South Dakota");
	    US_STATES_MAP.put("TN", "Tennessee");
	    US_STATES_MAP.put("TX", "Texas");
	    US_STATES_MAP.put("UT", "Utah");
	    US_STATES_MAP.put("VT", "Vermont");
	    US_STATES_MAP.put("VI", "Virgin Islands");
	    US_STATES_MAP.put("VA", "Virginia");
	    US_STATES_MAP.put("WA", "Washington");
	    US_STATES_MAP.put("WV", "West Virginia");
	    US_STATES_MAP.put("WI", "Wisconsin");
	    US_STATES_MAP.put("WY", "Wyoming");
	    US_STATES_MAP.put("YT", "Yukon Territory");
	}
	
	//Company Contact Information
	public static final String[] PHONE_NUMBER_LABELS = {"Main Telephone:", "Telephone Number :", "Telephone number:", "Telephones:", "Telephone:", " Telephone", "Téléphone:", "Telefone:", "Telefon:", "Teléfono Principal:", "Teléfono principal:", "Teléfono:", " Teléfono ", "Telefono:", "Telefonnummer:", "Fone:", "Phone number:", "Phone number", "Main Phone:", "PHONE:", "Phone:", "Phone :", "phone:", "Phone ", " phone ", "PH:", "Ph.:", "Ph :", "Ph:", "ph:", "P:", "p:", " p.",  "T//", "(T)", " T. ", " T:", "T :", " t:", "General Tel:", "General Tel :", "General:", "Tél. :", "Tél:", "Tél :", "Tel#:", "TEL:", "TEL :", " TEL ","Tel:", "Tel :", "Tel. #:", "Tel.:", "Tel.", " Tel ", " tel.", " tel ", "Tlf.:", "SÃ­mi:", "Main Number:", "Main number:", "Main Line:", "Main:", "Principal:", "Toll-Free:", "Toll-free :", "Toll-free:", "Toll Free:", "Office Line:", "Office:", "Landline:", "Within:", "Front Desk:", "Sales:", "Vendas:", "Recepción:", "International Dial:", "Switchboard:", " Switchboard ", "number:", "Contact No.:", "Contact no:", "Landline Nos.:", "No -", "General inquiries:", "Local:", "Call @", "Call:", "exchange:", "Voice:"};
	public static final String[] FAX_NUMBER_LABELS = {"Main Fax:", "Faksas:", "Faks.", "Fax Number:", "Fax number:", "Fax number", "Fax No:", "eFax:", "Fax#:", "Fax:", "Fax :", "FAX:", "FAX :", "FAX", "fax:", "Fax.", "fax.", " fax ", "Fax -", " Fax ", "fx:", "(F)", " F.", "F:", "f:", " f.", "Telecopier:", "Telefaks:", "Facsimile:"};
	public static final String[] EMAIL_LABELS = {"Email Address:", "Email ID :", "EMAIL", "Email -", "Email:", "Email  :", "Email :", "E-mail:", "E-mail :", "E-Mail:", "E-Mail", "Email", "e-mail:", "email:", " E:", " e:", "Mail:", "E-post:", "Netfang:", "Correo electrónico:", "Correo:", "@:"};
	
	public static final String[] COMPANY_CONTACT_INFO_LABELS = {"Website:", "Website", "CONTACT", "Contact:", "Telephone:", "Telephone", "Téléphone:", "Téléphone", "Telefono:", "Telefon", "Phone:", "Phone",  "Tél:", "Tél ", "TEL", "Tel:", "FAX:", "FAX", "Fax:", "Fax", "Faks", "Facsimile", "E-mail:", "E-mail", "Email:", "Email", "Mail", "E-Posta", "address", "Adresse", "Add:", "Address", "Address:", "Adres", "Notes:", "Access", "Dirección:", "Desk:"};
	
	//Noise Words
	public static final String[] SIMPLE_COMPANY_CONTACT_INFO_PARSER_NOISE_WORDS = {"GET DIRECTIONS",
			"Get Directions",
			"Get directions",
			"Get direction from Go!",
			"View Map & Directions",
			"Map & Directions",
			"Map and Directions",
			"Map and directions",
			"Map/Directions",
			"Directions & Parking",
			"Parking & Directions",
			"DRIVING DIRECTIONS",
			"Driving Directions",
			"Click here for directions",
			"Click for directions",
			"See directions on Google",
			"See directions",
			"Google Directions",
			"DIRECTIONS",
			"Directions",
			"directions",
			"See On Map",
			"See on Map",
			"Show location on map",
			"Show On Map",
			"Show on map",
			"Click here for map.",
			"Click here for map",
			"View google map here",
			"View on Google Maps",
			"View on Google maps",
			"View in Google Maps",
			"View in Google maps",
			"Visit Google Maps »",
			"Open in Google Maps",
			"Open Google maps",
			"Google Maps",
			"Google maps",
			"Google Map",
			"Google map",
			"View Location on a Map",
			"View location on map",
			"View Location Map",
			"View on Map",
			"View on map",
			"View Map",
			"View map",
			"VIEW MAP",
			"view map",
			"View full details",
			"View details",
			"Ver mapa",
			"Voir sur la carte",
			"Présentation",
			"Map This Location",
			"Map Location",
			"Location Map",
			"Location map",
			"Locate on a map",
			"Locate Us",
			"link to map",
			"Campus Maps",
			"Campus Map",
			"Detailed map",
			"See map",
			"Map It",
			"Map it",
			"map it",
			"Mapa",
			"MAP",
			"Map",
			"map",
			"Visit Dealer Locator",
			"Visit Contact Us Page",
			"Contactos globales",
			"Contacte a Ventas",
			"Worldwide Contacts",
			"Main contacts",
			"Contact Sales",
			"Contact us>>",
			"Contact Us!",
			"Contact Us:",
			"Contact us!",
			"Contact us:",
			"Contact Us",
			"Contact us",
			"Contact form",
			"Contact details",
			"Contact Information",
			"Contact Info",
			"Further contact options",
			"Other contact options",
			"Sales team contact details",
			"Find a local sales office",
			"Find a local sales agent",
			"Find a local distributor",
			"Find a local customer service office",
			"Find your local office",
			"Find a local broker",
			"Find a Partner",
			"Find a partner",
			"Find Us",
			"Find us",
			"Become a Partner",
			"Current Partners",
			"Find Kames Capital in your country",
			"Office Locator",
			"Enquiry Form",
			"Enquiry form",
			"General Inquiries",
			"Click here to see all contact details.",
			"Click here for other enquiries",
			"Home & personal care",
			"Unilever Bangladesh Careline:",
			"Pure It Careline:",
			"8:30-16:30 Monday-Friday on working days",
			"9:00-18:00 Monday-Friday on working days",
			"9:00-17:00 Monday-Friday on working days",
			"Consumer Care line",
			"Consumer Careline:",
			"* This is a Representative Office only. It is neither a bank, a branch, an agency, nor a depository institution. As such, it is not insured by the FDIC or any other agency of the United States",
			"Website:",
			"You may also call our main switchboard at +44 20 3322 4806",
			"Licensed by the Bermuda Monetary Authority under the Investment Funds Act 2006",
			"Copenhagen Representative Office",
			"There are more than 12,700 branch office locations in the United States and Canada.",
			"Find an Edward Jones Financial Advisor office near you.",
			"Americas office map PDF 0.16Mb",
			"Europe office map PDF 0.33Mb",
			"Asia, Middle East, and Africa office map PDF 0.22Mb",
			"View site in EnglishPolski",
			"View site in EnglishDeutsch",
			"View site in English",
			"View site in Deutsch",
			"View site in Italiano",
			"View site in Polski",
			"View site in Norsk",
			"London office map",
			"Paris office map",
			"Houston office map",
			"Norton Support",
			"Norton-Support",
			"Business Support",
			"Customer Support",
			"Authentication Services",
			"Corporate Information",
			"Suporte Norton",
			"Suporte para empresas",
			"Atendimento ao cliente",
			"Virksomhedssalg",
			"Oplysninger om virksomheden",
			"Support pour les entreprises",
			"Support client",
			"Kundensupport",
			"Unternehmensinformationen",
			"Asistencia empresarial",
			"Asistencia al cliente",
			"Business Sales",
			"Websites and social media profiles",
			"Country Website",
			"TRS Staffing Solutions website",
			"NuScale Power website",
			"Stork website",
			"AMECO website",
			"Sacyr Fluor Website",
			"COOEC-Fluor website",
			"ICA Fluor website",
			"FCCL website",
			"CFPS website",
			"SMFI website",
			"Visit Web Page",
			"visit their web site",
			"Visit Website",
			"Visit website",
			"visit website",
			"Visit the site",
			"Website [+]",
			"Website",
			"website",
			"Dirección:",
			"Address Info",
			"Visiting address:",
			"Address:",
			"address:",
			"Address",
			"address",
			"Job search",
			"Job Search",
			"Search jobs in this area",
			"Search for jobs",
			"Search our partner members by A-Z listing or specialty, and read about joint customer successes.",
			"Search",
			"Career Opportunities",
			"Careers:",
			">>",
			"(external link)",
			"(link is external)",
			"Write to us",
			"View Our Satellite Campuses",
			"MORE INFORMATION",
			"More Information",
			"More Info",
			"More info",
			"More details",
			"Subsidiaries:",
			"Send e-mail",
			"Email Us",
			"EMAIL US",
			"|",
			"Office Details",
			"Select this Branch",
			"More numbers",
			"Find a local Production Solutions dealer in your area:",
			"Find a local Office Products dealer in your area:",
			"Présentation",
			"Click to call",
			"Global HQ",
			"Country HQ",
			"Send Us a Message",
			"Global Information Supplier Careers Accounts Payable",
			"If you don't find your country in the list, see our worldwide contacts list.",
			"View all featured platinum and gold partners, or search our complete A-Z listing.",
			"Read about partner program levels, channel opportunities and how to apply.",
			"Read about our program levels, channel opportunities and requirements for applying.",
			"PartnerNet keeps you connected to all the latest training, marketing and membership resources.",
			"Sign in to PartnerNet",
			"Get training, marketing and membership resources for current partners.",
			"Recherchez nos partenaires par ordre alphabétique, de A à Z ou par spécialité. Découvrez nos témoignages clients communs.",
			"Avec PartnerNet restez connecté à toutes les sources d'informations : formations, marketing et programmes partenaires.",
			"Découvrez les différents niveaux, les avantages et les modalités du programme Partenaire SAS.",
			"Accédez au PartnerNet",
			"Devenir partenaire",
			"Démarrer",
			"En savoir plus",
			"Trouver un partenaire",
			"Learn More",
			"Learn more",
			"Get started",
			"Si no se encuentra en la lista, por favor visite nuestra lista de contactos a nivel mundial.",
			"Conozca las vías de soporte",
			"Training Kontakt",
			"Phone/Fax",
			"Media inquiries",
			"Back to Top",
			"Back to top",
			"Google  Data data ©2017",
			"Google  data ©2017",
			"Data  data ©2017",
			"Google Terms of Use",
			"Report a  error",
			"Terrain Satellite Labels",
			"Near Public Transit",
			"Disabled Access",
			"How to Arrive",
			"SHOW PHONE NUMBERS",
			"How to get there",
			"For SolarWinds MSP product information, sales and technical support, contact your nearest distributor in the first instance.",
			"Visitor Information",
			"Not Available",
			"Name:",
			"Online Form",
			"Navegar con Waze",
			"Submit a Request",
			"Click here"};

}
