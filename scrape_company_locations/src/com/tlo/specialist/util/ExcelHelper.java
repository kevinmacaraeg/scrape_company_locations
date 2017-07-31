package com.tlo.specialist.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.tlo.specialist.domain.CompanyContactInformation;
import com.tlo.specialist.domain.CompanyDetail;
import com.tlo.specialist.domain.CompanyLocationsDetail;
import com.tlo.specialist.domain.CompanySubsidiary;

public class ExcelHelper {
	
	private static Logger logger = Logger.getLogger(ExcelHelper.class.getName());
	
	public static List<CompanyDetail> getCompanyDetailsListFromFile(String companyDetailsFilePath, String sheetName) throws Exception {
		List<CompanyDetail> companyDetailsListFromFile = null;
		try {
			OPCPackage pkg = OPCPackage.open(companyDetailsFilePath);
			ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
	        XSSFReader xssfReader = new XSSFReader(pkg);
	        StylesTable styles = xssfReader.getStylesTable();
	        SheetIterator sheetData = (SheetIterator) xssfReader.getSheetsData();
	        boolean sheetNameExists = false;
	        while (sheetData.hasNext()) {
	            InputStream currentSheet = sheetData.next();
	            String currentSheetName = sheetData.getSheetName();
	            if (currentSheetName.equals(sheetName)) {
	            	processSheet(styles, strings, new CompanyDetailCellValuesHandler(), currentSheet);
	            	sheetNameExists = true;
	            	break;
	            }
	        }
	        
	        if (sheetNameExists) {
	        	companyDetailsListFromFile = CompanyDetailCellValuesHandler.companyDetailsList;
	        	logger.info("# of Company Details retrieved :: " + companyDetailsListFromFile.size());
	        } else {
	        	logger.error("\"" + sheetName + "\" is not a sheet of the Company Details File!");
	        	throw new Exception("\"" + sheetName + "\" is not a sheet of the Company Details File!");
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return companyDetailsListFromFile;
	}
	
	public static List<CompanyLocationsDetail> getCompanyLocationsDetailsListFromFile(String companyLocationsDetailsFilePath, String sheetName) throws Exception {
		List<CompanyLocationsDetail> companyLocationsDetailsListFromFile = null;
		try {
			OPCPackage pkg = OPCPackage.open(companyLocationsDetailsFilePath);
			ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
	        XSSFReader xssfReader = new XSSFReader(pkg);
	        StylesTable styles = xssfReader.getStylesTable();
	        SheetIterator sheetData = (SheetIterator) xssfReader.getSheetsData();
	        boolean sheetNameExists = false;
	        while (sheetData.hasNext()) {
	            InputStream currentSheet = sheetData.next();
	            String currentSheetName = sheetData.getSheetName();
	            if (currentSheetName.equals(sheetName)) {
	            	processSheet(styles, strings, new CompanyLocationsDetailCellValuesHandler(), currentSheet);
	            	sheetNameExists = true;
	            	break;
	            }
	        }
	        
	        if (sheetNameExists) {
	        	companyLocationsDetailsListFromFile = CompanyLocationsDetailCellValuesHandler.companyLocationsDetailsList;
	        	logger.info("# of Company Locations Details retrieved :: " + companyLocationsDetailsListFromFile.size());
	        } else {
	        	logger.error("\"" + sheetName + "\" is not a sheet of the Company Locations Details File!");
	        	throw new Exception("\"" + sheetName + "\" is not a sheet of the Company Locations Details File!");
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return companyLocationsDetailsListFromFile;
	}
	
	public static Map<String, String> getScrapeContactInfoInputsFromFile(File inputExcelFile) throws Exception {
		Map<String, String> linkCssSelectorMap = null;
		Workbook inputWorkbook = null;
		try {
			linkCssSelectorMap = new HashMap<String, String>();
				
			inputWorkbook = new XSSFWorkbook(inputExcelFile);
				
			Sheet firstSheet = inputWorkbook.getSheetAt(0);
				
			int numberOfPhysicalRows = firstSheet.getPhysicalNumberOfRows();
			for (int i = 1; i < numberOfPhysicalRows; i++) {
				Row currentRow = firstSheet.getRow(i);
				
				String websiteLink = getCellStringValue(currentRow.getCell(0));
				String cssSelector = getCellStringValue(currentRow.getCell(1));
				
				if (linkCssSelectorMap.containsKey(websiteLink)) {
					logger.info(websiteLink + " from the input file has duplicate!");
				} else {
					linkCssSelectorMap.put(websiteLink, cssSelector);
				}
			}
			
			logger.info("Number of inputs retrieved from file :: " + linkCssSelectorMap.size());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			closeWorkbook(inputWorkbook);
		}
		return linkCssSelectorMap;
	}
	
	public static void writeCompanyContactInfoToExcelFile(File excelFile, List<CompanyContactInformation> companyContactInformationList) throws Exception {
		Workbook contactInformationWorkbook = null;
		Sheet currentSheet = null;
		FileOutputStream fileOut = null;
		try {
			contactInformationWorkbook = new XSSFWorkbook();
			currentSheet = contactInformationWorkbook.createSheet();
			
			CellStyle style = contactInformationWorkbook.createCellStyle();
			Font font = contactInformationWorkbook.createFont();
			font.setBold(true);
			style.setFont(font); 
			
			int numberOfExistingRows = 0;
			
			Row columnHeaders = currentSheet.createRow(numberOfExistingRows++);
			
			Cell masterCompanyIdHeader = columnHeaders.createCell(0);
			masterCompanyIdHeader.setCellValue("masterCompanyId");
			masterCompanyIdHeader.setCellStyle(style);
			Cell masterCompanyNameHeader = columnHeaders.createCell(1);
			masterCompanyNameHeader.setCellValue("masterCompanyName");
			masterCompanyNameHeader.setCellStyle(style);
			Cell companyLocationsUrlHeader = columnHeaders.createCell(2);
			companyLocationsUrlHeader.setCellValue("companyLocationsUrl");
			companyLocationsUrlHeader.setCellStyle(style);
			Cell addressHeader = columnHeaders.createCell(3);
			addressHeader.setCellValue("ADDRESS");
			addressHeader.setCellStyle(style);
			Cell phoneHeader = columnHeaders.createCell(4);
			phoneHeader.setCellValue("PHONE");
			phoneHeader.setCellStyle(style);
			Cell faxHeader = columnHeaders.createCell(5);
			faxHeader.setCellValue("FAX");
			faxHeader.setCellStyle(style);
			Cell emailHeader = columnHeaders.createCell(6);
			emailHeader.setCellValue("EMAIL");
			emailHeader.setCellStyle(style);
			
			for (CompanyContactInformation contactInfo : companyContactInformationList) {
				Row newRow = currentSheet.createRow(numberOfExistingRows++);
				
				newRow.createCell(0).setCellValue(contactInfo.getMasterCompanyId());
				newRow.createCell(1).setCellValue(contactInfo.getMasterCompanyName());
				newRow.createCell(2).setCellValue(contactInfo.getCompanyLocationsUrl());
				newRow.createCell(3).setCellValue(contactInfo.getAddress());
				newRow.createCell(4).setCellValue(contactInfo.getPhoneNumber());
				newRow.createCell(5).setCellValue(contactInfo.getFaxNumber());
				newRow.createCell(6).setCellValue(contactInfo.getEmail());
			}
			
			fileOut = new FileOutputStream(excelFile);
			contactInformationWorkbook.write(fileOut);
			
			logger.info("Records written to " + excelFile.getName());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			FileHelper.closeOutputStream(fileOut);
			closeWorkbook(contactInformationWorkbook);
		}
	}
	
	public static void writeCompanySubsidiariesToExcelFile(File excelFile, List<CompanySubsidiary> companySubsidiariesList) throws Exception {
		Workbook contactInformationWorkbook = null;
		Sheet currentSheet = null;
		FileOutputStream fileOut = null;
		try {
			contactInformationWorkbook = new XSSFWorkbook();
			currentSheet = contactInformationWorkbook.createSheet();
			
			CellStyle style = contactInformationWorkbook.createCellStyle();
			Font font = contactInformationWorkbook.createFont();
			font.setBold(true);
			style.setFont(font); 
			
			int numberOfExistingRows = 0;
			
			Row columnHeaders = currentSheet.createRow(numberOfExistingRows++);
			
			Cell masterCompanyIdHeader = columnHeaders.createCell(0);
			masterCompanyIdHeader.setCellValue("masterCompanyId");
			masterCompanyIdHeader.setCellStyle(style);
			Cell masterCompanyNameHeader = columnHeaders.createCell(1);
			masterCompanyNameHeader.setCellValue("masterCompanyName");
			masterCompanyNameHeader.setCellStyle(style);
			Cell subsidiaryUrlHeader = columnHeaders.createCell(2);
			subsidiaryUrlHeader.setCellValue("subsidiaryUrl");
			subsidiaryUrlHeader.setCellStyle(style);
			Cell subsidiaryHeader = columnHeaders.createCell(3);
			subsidiaryHeader.setCellValue("subsidiary");
			subsidiaryHeader.setCellStyle(style);
			Cell jurisdictionHeader = columnHeaders.createCell(4);
			jurisdictionHeader.setCellValue("jurisdiction");
			jurisdictionHeader.setCellStyle(style);
			Cell subsidiaryAsOfHeader = columnHeaders.createCell(5);
			subsidiaryAsOfHeader.setCellValue("subsidiaryAsOf");
			subsidiaryAsOfHeader.setCellStyle(style);
			
			for (CompanySubsidiary subsidiary : companySubsidiariesList) {
				Row newRow = currentSheet.createRow(numberOfExistingRows++);
				
				newRow.createCell(0).setCellValue(subsidiary.getMasterCompanyId());
				newRow.createCell(1).setCellValue(subsidiary.getMasterCompanyName());
				newRow.createCell(2).setCellValue(subsidiary.getSubsidiaryUrl());
				newRow.createCell(3).setCellValue(subsidiary.getSubsidiary());
				newRow.createCell(4).setCellValue(subsidiary.getJurisdiction());
				newRow.createCell(5).setCellValue(subsidiary.getSubsidiaryAsOf());
			}
			
			fileOut = new FileOutputStream(excelFile);
			contactInformationWorkbook.write(fileOut);
			
			logger.info("Records written to " + excelFile.getName());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			FileHelper.closeOutputStream(fileOut);
			closeWorkbook(contactInformationWorkbook);
		}
	}
	
	static void processSheet(StylesTable styles, ReadOnlySharedStringsTable strings, SheetContentsHandler sheetHandler, InputStream sheetInputStream) throws IOException, SAXException {
        DataFormatter formatter = new DataFormatter();
        InputSource sheetSource = new InputSource(sheetInputStream);
        try {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            ContentHandler handler = new XSSFSheetXMLHandler(styles, null, strings, sheetHandler, formatter, false);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
         } catch(ParserConfigurationException e) {
            throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
         }
    }
	
	static void closeWorkbook(Workbook workbook) throws Exception {
		try {
			if (workbook != null) {
				workbook.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	static String getCellStringValue(Cell cell) throws Exception {
		String cellStringValue = Constants.EMPTY_STRING;
		try {
			if (cell != null) {
				cellStringValue = StringHelper.replaceNullValue(cell.toString(), Constants.EMPTY_STRING).trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return cellStringValue;
	}
}
