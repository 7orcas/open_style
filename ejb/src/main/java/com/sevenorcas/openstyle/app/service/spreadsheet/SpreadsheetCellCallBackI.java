package com.sevenorcas.openstyle.app.service.spreadsheet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Interface for implementing class to format spreadsheet values.<p>
 * 
 * [License] 
 * @author John Stewart
 */
public interface SpreadsheetCellCallBackI {
    public HSSFRichTextString getCellValue(HSSFWorkbook wb, SpreadSheet sheet, HSSFCell cell, String value);
}
