package com.tlo.specialist.util;

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
	public static final String UNDERSCORE = "_";
	public static final String COLON = ":";
	public static final String SEMI_COLON = ";";
	public static final String QUESTION_MARK = "?";
	public static final String NON_BREAKING_SPACE1 = "\u00A0";
	public static final String NON_BREAKING_SPACE2 = "\u2007";
	public static final String NON_BREAKING_SPACE3 = "\u202F";
	
	public static final String DELIMITER_COMMA = ",";
	
	//LITERALS
	public static final String LITERAL_ASCII = "ascii";
	
	//REGEX
	public static final String REGEX_SPECIAL_LETTERS = "[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+";
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
													+ "|\\+\\d+\\(\\d+\\)\\d+"
													+ "|\\+\\d+\\(\\d+\\)\\d+\\- \\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\) \\d+ \\d+\\-\\d+"
													+ "|\\+\\d+ \\(\\d+\\) \\d+\\-\\d+"
													+ "|\\+\\d+ \\(\\d+\\)\\d+ \\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\)\\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\)\\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\) \\d+ \\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\) \\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\) \\d+ \\d+"
													+ "|\\+\\d+ \\(\\d+\\) \\d+"
													+ "|\\+\\d+\\-\\d+\\- \\d+\\-\\d+"
													+ "|\\+\\d+ \\d+ \\d+\\-\\d+"
													+ "|\\+\\d+ \\d+\\-\\d+\\-\\d+\\-\\d+"
													+ "|\\+\\d+ \\d+\\-\\d+\\-\\d+"
													+ "|\\+\\d+ \\d+\\-\\d+"
													+ "|\\+\\d+\\-\\d+\\-\\d+\\-\\d+"
													+ "|\\+\\d+\\-\\d+\\-\\d+"
													+ "|\\+\\d+\\-\\d+"
													+ "|\\+\\d+ \\d+\\.\\d+\\.\\d+"
													+ "|\\+\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+, \\W+\\. \\d+\\-\\d+"
													+ "|\\+\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+, \\W+\\. \\d+"
													+ "|\\+\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+\\/\\d+\\/\\d+\\/\\d+"
													+ "|\\+\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+"
													+ "|\\+\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+"
													+ "|\\+\\d+\\.\\d+\\.\\d+\\.\\d+"
													+ "|\\+\\d+\\.\\d+\\.\\d+"
													+ "|\\+\\d+ \\d+ \\d+ \\d+ \\d+\\-\\d+"
													+ "|\\+\\d+ \\d+ \\d+ \\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\d+ \\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\d+ \\d+"
													+ "|\\+\\d+ \\d+"
													+ "|\\+\\d{6,}"
													+ "|\\+ \\d+ \\d+ \\d+ \\d+"
													+ "|\\+ \\d+ \\d+"
													+ "|\\(\\d+ \\d+\\) \\d+ \\d+ \\d+ \\d+"
													+ "|\\(\\d+ \\d+\\) \\d+ \\d+"
													+ "|\\(\\d+\\)\\-\\d+\\-\\d+"
													+ "|\\(\\d+\\) \\d+\\-\\d+, Ext. \\d+"
													+ "|\\(\\d+\\) \\d+\\-\\d+"
													+ "|\\(\\d+\\)\\d+\\-\\d+"
													+ "|\\d+\\.\\d+\\.\\d+ \\d+\\.\\d+"
													+ "|\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+"
													+ "|\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+"
													+ "|\\d+\\.\\d+\\.\\d+\\.\\d+"
													+ "|\\d+\\.\\d+\\.\\d+"
													+ "|\\d+\\-\\d+\\-[A-Z]+ \\(\\d+\\)"
													+ "|\\d+\\-\\d+\\-\\d+ \\(Toll Free\\)"
													+ "|\\d+\\-\\d+\\-\\d+ \\d+\\s"
													+ "|\\d+\\-[A-Z]+ \\(\\d+\\)"
													+ "|\\d+\\-\\d+\\-\\d+\\-\\d+"
													+ "|\\d{3,}\\-\\d{3,}\\-\\d{3,}"
													+ "|\\d{4,}\\-\\d{5,}"
													+ "|\\d+ \\(\\d+\\)\\d+ \\d+ \\d+"
													+ "|\\d+ \\d+ \\d+ \\d+ ext. \\d+"
													+ "|\\d+ \\d+ \\d+\\-\\d+"
													+ "|\\d+ \\d+ \\d+ \\d+ \\d+"
													+ "|\\d+ \\d+ \\d+ \\d+"
													+ "|\\d{2,} \\d{1,} \\d{4,}"
													+ "|\\d{1,} \\d{2,} \\d{4,}"
													+ "|\\d{7,}";

	public static final String REGEX_WEB_URL = "^(https:\\/\\/|http:\\/\\/)?(www\\.)?([a-zA-Z0-9\\-]+\\.)[a-z]+((\\/|\\.)\\S*)*$|^(https:\\/\\/|http:\\/\\/)?([a-zA-Z0-9\\-]+\\.)([a-zA-Z0-9\\-]+\\.)[a-z]+((\\/|\\.)\\S*)*$";
	
	//HTML
	public static final String HTML_ELEMENT_A = "a";
	public static final String HTML_ELEMENT_H3 = "h3";
	public static final String HTML_ELEMENT_P = "p";
	
	public static final String HTML_ELEMENT_ATTR_ALT = "alt";
	public static final String HTML_ELEMENT_ATTR_HREF = "href";
	
	//Verify Executive List
	public static final String EXEC_STATUS_CURRENT = "CURRENT";
	public static final String EXEC_STATUS_TITLE_UPDATED = "UPDATED TITLE";
	public static final String EXEC_STATUS_FIRST_AND_LAST_NAME_FOUND = "FIRST AND LAST NAME FOUND";
	public static final String EXEC_STATUS_TITLE_FOUND = "TITLE FOUND";
	public static final String EXEC_STATUS_NOT_FOUND = "NOT FOUND";
	public static final String EXEC_STATUS_SOURCE_INACCESSIBLE = "SOURCE INACCESSIBLE";
	
	//EXECUTIVE LIST FILE STATUS
	public static final String EXEC_FILE_STATUS_NO_CHANGE = "No Change";
	public static final String EXEC_FILE_STATUS_UPDATED = "Updated";
	public static final String EXEC_FILE_STATUS_NA = "NA";
	public static final String EXEC_FILE_STATUS_NET_NEW = "Net New";
	
	//Verify Executive List Exec Scrape Sources
	public static final String VERIFY_EXEC_SCRAPE_SOURCE_BLOOMBERG = "Bloomberg";
	public static final String VERIFY_EXEC_SCRAPE_SOURCE_FORBES = "Forbes";
	public static final String VERIFY_EXEC_SCRAPE_SOURCE_GOOGLE = "Google";
	public static final String VERIFY_EXEC_SCRAPE_SOURCE_MORNING_STAR = "Morning_Star";
	
	//Company Contact Information
	public static final String[] PHONE_NUMBER_LABELS = {"Telephone:", "Telephone", "Téléphone:", "Telefone:", "Telefon:", "Teléfono:", "Telefono:", "Telefonnummer:", "Telephone number:", "Phone number:", "Main Phone:", "PHONE:", "Phone:", "Phone :", "phone:", "Phone ", "phone ", "p:", " p.", "T:", "T :", "General Tel:", "General Tel :", "TEL:", "Tel:", "Tel :", "Tel.:", "Tel.", " Tel ", " tel.", " tel ", "SÃ­mi:", "Main Number:", "Main number:", "Main:", "Main Line:", "Ph:", "Toll-free :", "Toll Free:", "Office:", "Landline:", "Within:", "Front Desk:", "Sales:", "Recepción:"};
	public static final String[] FAX_NUMBER_LABELS = {"Telefaks:", "Fax Number:", "Fax number:", "Fax:", "Fax :", "FAX:", "fax:", "Fax.", "fax.", "fax ", "Fax ", "F:", "f:", " f.", "f |", "Telecopier:"};
	public static final String[] EMAIL_LABELS = {"Email:", "Email :", "E-mail:", "E-mail :", "E-Mail", "Email", "email:", "Mail:", "E-post:", "Netfang:"};
	
	public static final String[] IBM_COMPANY_CONTACT_INFO_LABELS = {"Website:", "Website", "Contact:", "Telephone:", "Téléphone:", "Téléphone", "Tél:", "Tél ", "Tel:", "Fax:", "Fax", "E-mail:", "Email:", "Email", "E-mail", "Address", "Address:"};
	
	//Noise Words
	public static final String[] SIMPLE_COMPANY_CONTACT_INFO_PARSER_NOISE_WORDS = {"Get Directions",
			"Get directions",
			"Map & Directions",
			"Map/Directions",
			"Directions & Parking",
			"Parking & Directions",
			"DIRECTIONS",
			"Directions",
			"Show On Map",
			"Click here for map.",
			"Click here for map",
			"View in Google maps",
			"View on Map",
			"View Map",
			"View map",
			"VIEW MAP",
			"View details",
			"Voir sur la carte",
			"PrÃ©sentation",
			"Campus Maps",
			"Campus Map",
			"Map It",
			"Map it",
			"map it",
			"Map",
			"map",
			"Visit Dealer Locator",
			"Visit Contact Us Page",
			"Contact us>>",
			"Contact us",
			"Contact Us",
			"Contact form",
			"Contact details",
			"Contact Info",
			"Contact",
			"Find a local sales office",
			"Find a local sales agent",
			"Find a local distributor",
			"Find a local customer service office",
			"Find your local office",
			"Find a local broker",
			"Find Kames Capital in your country",
			"Further contact options",
			"Other contact options",
			"Sales team contact details",
			"Office Locator",
			"Enquiry form",
			"Search for jobs",
			"General Inquiries",
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
			"BNY Mellon Australia Pty Ltd",
			"BNY Mellon Asset Servicing",
			"BNY Mellon Investment Management Australia Limited",
			"BNY Mellon Investment Management Singapore Pte. Ltd.",
			"BNY Mellon Centre",
			"BNY Mellon AM Latin America S.A.",
			"BNY Mellon International Operations (India) Private Ltd",
			"BNY Mellon Service KAG",
			"BNY Mellon Investment Management EMEA Ltd.",
			"Family Office Services",
			"BNY Mellon Investment Management Hong Kong Limited",
			"BNY Mellon Investment Management EMEA Limited",
			"BNY Mellon Asset Management Japan Limited",
			"BNY Mellon Institutional Services (Asia Pacific) Pte. Ltd.",
			"BNY Mellon Trust Company (Cayman) Limited",
			"BNY Mellon Alternative Investment Services Ltd.",
			"BNY Mellon Fund Management (Cayman) Limited",
			"BNY Mellon IM Korea Limited",
			"BNY Mellon Servicos Financeiros DTVM S.A. & BNY Mellon Banco S.A.",
			"BNY Mellon Corporate Headquarters",
			"BNY Mellon Investments Switzerland GmbH",
			"BNY Mellon Investment Management EMEA Ltd / Zweigniederlassung Deutschland",
			"BNY Mellon Mexico City Representative Office",
			"BNY Mellon Investment Management",
			"BNY Mellon Wealth Management",
			"The BNY Mellon",
			"BNY Mellon",
			"Taipei Branch Financial Information",
			"Pershing Securities International Limited",
			"Pershing Securities Singapore Private Limited",
			"Pershing (Channel Islands) Limited",
			"Pershing Limited",
			"Peshing Limited",
			"Pershing",
			"ARX Investimentos Ltda",
			"Licensed by the Bermuda Monetary Authority under the Investment Funds Act 2006",
			"The Bank of New York Mellon (International) Ltd",
			"The Bank of New York Mellon SA/NV, Milan Branch",
			"The Bank of New York Mellon SA/NV",
			"The Bank of New York Mellon Securities Company Japan Ltd.",
			"The Bank of New York Mellon Trust (Japan), Ltd.",
			"The Bank of New York Mellon Trust Company, N.A.",
			"The Bank of New York Mellon",
			"Walter Scott & Partners Limited",
			"Eagle Investment Systems LLC Beijing Representative Office",
			"Eagle Investment Systems Singapore Pte. Ltd.",
			"Eagle Investment Systems",
			"Alcentra Europe",
			"Pareto Investment Management",
			"Meriten Investment Management GmbH",
			"Insight Investment Management Limited",
			"Newton Investment Management",
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
			"Website [+]",
			"Website",
			"Address Info",
			"Address",
			"Job search",
			"Job Search",
			"Search",
			"Careers:",
			">>",
			"(external link)",
			"Write to us",
			"View Our Satellite Campuses",
			"MORE INFORMATION",
			"Subsidiaries:",
			"EMAIL US",
			"|",
			"Office Details",
			"Select this Branch",
			"More numbers",
			"Find a local Production Solutions dealer in your area:",
			"Find a local Office Products dealer in your area:",
			"Présentation"};

}
