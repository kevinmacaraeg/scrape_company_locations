package com.tlo.specialist.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import com.tlo.specialist.domain.CompanyDetail;

public class CompanyDetailCellValuesHandler implements SheetContentsHandler {

	private static final int FIRST_ROW_INDEX = 0;
	
	private static final int NUM_OF_COL = 6;
	
	public static List<CompanyDetail> companyDetailsList;
	
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
			
			CompanyDetail companyDetail = new CompanyDetail();
			companyDetail.setMasterCompanyId(rowCellValuesList.get(0));
			companyDetail.setMasterCompanyName(rowCellValuesList.get(1));
			companyDetail.setWebsite(rowCellValuesList.get(2));
			companyDetail.setLinkedInUrl(rowCellValuesList.get(3));
			companyDetail.setSbParentIndustry(rowCellValuesList.get(4));

			companyDetailsList.add(companyDetail);
		}
	}

	@Override
	public void headerFooter(String text, boolean isHeader, String tagName) {
		//SKIP
	}

	@Override
	public void startRow(int rowNum) {
		if (rowNum == FIRST_ROW_INDEX) {
			companyDetailsList = new ArrayList<CompanyDetail>();
		}
		rowCellValuesList = new ArrayList<String>();
		firstCellOfRow = true;
		currentRow = rowNum;
		currentCol = -1;
	}	
	
}
