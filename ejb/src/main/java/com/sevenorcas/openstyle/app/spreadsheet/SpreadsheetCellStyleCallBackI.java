package com.sevenorcas.openstyle.app.spreadsheet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Interface for implementing class to format spreadsheet values via call back method.<p>
 * 
 * [License] 
 * @author John Stewart
 */
public interface SpreadsheetCellStyleCallBackI {
    public HSSFCellStyle getCellStyle(HSSFWorkbook wb, SpreadSheet sheet, HSSFCell cell);
}
