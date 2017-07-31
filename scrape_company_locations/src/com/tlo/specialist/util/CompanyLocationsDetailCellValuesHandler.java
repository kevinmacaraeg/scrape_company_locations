package com.tlo.specialist.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import com.tlo.specialist.domain.CompanyLocationsDetail;

public class CompanyLocationsDetailCellValuesHandler implements SheetContentsHandler  {
	
	private static final int FIRST_ROW_INDEX = 0;
	
	private static final int NUM_OF_COL = 7;
	
	public static List<CompanyLocationsDetail> companyLocationsDetailsList;
	
	private static List<String> rowCellValuesList;
	
	private boolean firstCellOfRow;
	private int currentRow;
    private int currentCol;
	
	@Override
	public void cell(String cellReference, String formattedValue, XSSFComment comment) {
		if (currentRow > FIRST_ROW_INDEX) {
			if (firstCellOfRow) {
				firstCellOfRow = false;
			}
			
			if (cellReference == null) {
				cellReference = new CellAddress(currentRow, currentCol).formatAsString();
			}
			 
			int thisCol = (new CellReference(cellReference)).getCol();
			int missedCols = thisCol - currentCol - 1;
			for (int i = currentCol; i < currentCol + missedCols; i++) {
				rowCellValuesList.add(Constants.EMPTY_STRING);
			}
			currentCol = thisCol;
			rowCellValuesList.add(formattedValue.trim());
		}
	}

	@Override
	public void endRow(int rowNum) {
		if (currentRow > FIRST_ROW_INDEX) {
			
			while (rowCellValuesList.size() < NUM_OF_COL) {
				rowCellValuesList.add(Constants.EMPTY_STRING);
			}
			
			CompanyLocationsDetail companyLocationsDetail = new CompanyLocationsDetail();
			companyLocationsDetail.setMasterCompanyId(rowCellValuesList.get(0));
			companyLocationsDetail.setMasterCompanyName(rowCellValuesList.get(1));
			companyLocationsDetail.setWebsite(rowCellValuesList.get(2));
			companyLocationsDetail.setEmployeeSize(rowCellValuesList.get(3));
			companyLocationsDetail.setCompanyLocationsUrl(rowCellValuesList.get(4));
			companyLocationsDetail.setCompanyLocationsWebsitePageStructure(rowCellValuesList.get(5));
			companyLocationsDetail.setCompanyLocationsCssSelector(rowCellValuesList.get(6));

			companyLocationsDetailsList.add(companyLocationsDetail);
		}
	}

	@Override
	public void headerFooter(String text, boolean isHeader, String tagName) {
		//SKIP
	}

	@Override
	public void startRow(int rowNum) {
		if (rowNum == FIRST_ROW_INDEX) {
			companyLocationsDetailsList = new ArrayList<CompanyLocationsDetail>();
		}
		rowCellValuesList = new ArrayList<String>();
		firstCellOfRow = true;
		currentRow = rowNum;
		currentCol = -1;
	}

}
