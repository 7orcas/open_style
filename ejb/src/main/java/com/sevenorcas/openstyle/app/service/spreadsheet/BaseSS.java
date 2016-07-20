package com.sevenorcas.openstyle.app.service.spreadsheet;

import java.util.Hashtable;

import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.mod.company.Company;
import com.sevenorcas.openstyle.app.mod.lang.Language;
import com.sevenorcas.openstyle.app.mod.user.UserParam;



/**
 * Base spreadsheet generation class.<p>
 *  
 * This class contains size utility functions common to a number of spreadsheets.<p> 
 *  
 * [License]
 * @author John Stewart
 */
public class BaseSS implements ApplicationI, SpreadSheetI {

    static final protected int STYLE_TITLE         = 1;
    static final protected int STYLE_TOTAL_BASE    = 19;
    
    static final protected String RED  = "255,45,34";
    static final protected String BLUE = "53,94,227";
   
    //Cell formats
    protected Colors cc;
    protected Hashtable <String, HSSFFont> fonts;
    protected Hashtable <String, SpreadsheetColor> bgColors;
    protected Hashtable <String, Integer> bgColorsIds;
    protected Hashtable <String, Integer> styleIds;
    
    //Cell styles
    private HSSFCellStyle styleInvalid;
    private HSSFCellStyle styleQuantityRed;
    private HSSFCellStyle styleQuantityBlue;
    
    protected Language lang;
    protected Company company;
    
    /**
     * Constructor 
     * Initialize objects
     */
    public BaseSS(UserParam params, Language lang, Company comp){
        this.lang    = lang;
        this.company = comp;
        initialise();
    }

    /**
     * Initialize objects
     */
    private void initialise(){
    	fonts       = new Hashtable<>();
    	bgColors    = new Hashtable<>();
    	bgColorsIds = new Hashtable<>();
    	cc          = new Colors();
    	styleIds    = new Hashtable<>();
    }
    
  
    
     
    /**
     * Set the passed in cell to invalid
     * @param cell
     * @return
     */
    protected void styleInvalid(SpreadsheetCell cell){
        
        cell.setCallback(new SpreadsheetCellCallBackI(){
            public HSSFRichTextString getCellValue(HSSFWorkbook wb, SpreadSheet sheet, HSSFCell cell, String value) {
                
                if (styleInvalid == null){
                    HSSFFont font = fonts.get("red");
                    if (font == null){
                        String[] s = RED.split(",");
                        HSSFColor c = sheet.setColor(wb, Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2]));
                        font = wb.createFont();
                        if (c != null){
                            font.setColor(c.getIndex());
                        }
                        fonts.put("red", font);
                    }
                    
                    HSSFCellStyle style = wb.createCellStyle();
                    style.setFont(font);
                    style.setAlignment((short)ExtendedFormatRecord.CENTER);
                    styleInvalid = style;
                }
                
                cell.setCellStyle(styleInvalid);
                HSSFRichTextString richString = new HSSFRichTextString(value);
                return richString;
            }});
    }
    
    /**
     * Set the passed in cell to red
     * @param cell
     * @return
     */
    protected void styleRed(SpreadsheetCell cell){
        cell.setCallbackStyle(new SpreadsheetCellStyleCallBackI() {
            public HSSFCellStyle getCellStyle(HSSFWorkbook wb, SpreadSheet sheet, HSSFCell cell) {

                HSSFFont font = fonts.get("red");
                if (font == null){
                    String[] s = RED.split(",");
                    HSSFColor c = sheet.setColor(wb, Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2]));
                    font = wb.createFont();
                    if (c != null){
                        font.setColor(c.getIndex());
                    }
                    fonts.put("red", font);
                }
                if (styleQuantityRed == null){
                    HSSFCellStyle style = wb.createCellStyle();
                    style.setFont(font);
                    style.setAlignment((short)ExtendedFormatRecord.CENTER);
                    styleQuantityRed = style;
                }
                return styleQuantityRed;
            }
        });
    }
    
    /**
     * Set the passed in cell to blue
     * @param cell
     * @return
     */
    protected void styleBlue(SpreadsheetCell cell){
        cell.setCallbackStyle(new SpreadsheetCellStyleCallBackI() {
            public HSSFCellStyle getCellStyle(HSSFWorkbook wb, SpreadSheet sheet, HSSFCell cell) {

                HSSFFont font = fonts.get("blue");
                if (font == null){
                    String[] s = BLUE.split(",");
                    HSSFColor c = sheet.setColor(wb, Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2]));
                    font = wb.createFont();
                    if (c != null){
                        font.setColor(c.getIndex());
                    }
                    font.setItalic(true);
                    fonts.put("blue", font);
                }
                if (styleQuantityBlue == null){
                    HSSFCellStyle style = wb.createCellStyle();
                    style.setFont(font);
                    styleQuantityBlue = style;
                }
                return styleQuantityBlue;
            }
        });
    }
    
    /**
     * Set the passed in cell background to passed in RGB color
     * @param cell
     * @param RGB color
     * @param style id
     * @return
     */
    protected void styleBackground(SpreadsheetCell cell, String rgb, int styleId){
        SpreadsheetColor bgc = bgColors.get(rgb);
        Integer id = bgColorsIds.get(rgb);
        if (bgc == null){
            bgc = new SpreadsheetColor(rgb);
            bgColors.put(rgb,bgc);
            id = styleId++;
            bgColorsIds.put(rgb,id);
        }
        cell.setBackgroundColorRGB(bgc);
    }


    /**
     * Convenience method to merge cells
     * @param sheet
     * @param cell
     * @param cols
     */
    protected void mergeColumns (SpreadSheet sheet, SpreadsheetCell cell, int cols){
        cell.addMergedRegion (sheet.getRow(), sheet.getRow(), sheet.getCol() - 1, sheet.getCol() + (cols - 2));
        for (int i=0; i<cols-1; i++){
            sheet.incrementCol();
        }
    }
    
    /**
     * Convenience method to merge cells
     * @param sheet
     * @param cell
     * @param rows
     */
    protected void mergeRows (SpreadSheet sheet, SpreadsheetCell cell, int rows){
        cell.addMergedRegion (sheet.getRow() - rows, sheet.getRow(), sheet.getCol() - 1, sheet.getCol() - 1);
    }
    
    /**
     * Sheet title 
     * @param sheet
     * @param startCol
     * @param title
     * @param Number of columns to span
     */
    protected void outputTitle(SpreadSheet sheet, int startCol, String title, int columnCount) throws Exception{
        
        sheet.addRowFormat(FORMAT_BOLD);
        sheet.setCol(startCol);
        sheet.addColumnFormat(ALIGN_CENTER);
        
        SpreadsheetCell cell = sheet.addCell (title, STYLE_TITLE);
        
        cell.setCallback(new SpreadsheetCellCallBackI(){
            public HSSFRichTextString getCellValue(HSSFWorkbook wb, SpreadSheet sheet, HSSFCell cell, String value) {
                HSSFFont font = wb.createFont();
                font.setFontHeightInPoints((short)16);
                HSSFCellStyle style = wb.createCellStyle();
                style.setFont(font);
                style.setAlignment((short)ExtendedFormatRecord.CENTER);
                cell.setCellStyle(style);
                
                HSSFRichTextString richString = new HSSFRichTextString(value);
                return richString;
            }});
        
        
        
        mergeColumns (sheet, cell, columnCount);
        sheet.incrementRow();
    }
        
    /**
     * Output column spacer (using default width of 500)
     * @param sheet
     */
    protected void outputColumnSpacer(SpreadSheet sheet){
        sheet.setColumnWidth(sheet.getCol(), 500);
        sheet.incrementCol();
    }
    
    /**
     * Set the passed in cell to date format
     * @param cell
     * @return
     */
    protected String styleBooleanFormat(Boolean value){
        if (value != null && value){
            return "√";
        }
        return "";
    }
}
