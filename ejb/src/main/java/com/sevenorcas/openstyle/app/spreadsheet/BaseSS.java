package com.sevenorcas.openstyle.app.spreadsheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;


import com.sevenorcas.openstyle.app.ApplicationI;
import com.sevenorcas.openstyle.app.company.Company;
import com.sevenorcas.openstyle.app.lang.LanguageServiceImp.Language;



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
     * Constructor 
     * Initialize objects
     */
    public BaseSS(UserParam params, Language lang, Company comp, List<Size> listSz, SizeMap sizeMap){
        this.lang    = lang;
        this.company = comp;
        this.listSz  = listSz;
        this.sizeMap = sizeMap;
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
     * Set <b>this</b> objects size index.<p>
     * 
     * Thanks to http://stackoverflow.com/questions/933447/how-do-you-cast-a-list-of-supertypes-to-a-list-of-subtypes
     * 
     * @param List of row objects that contain the size code to process
     */
    protected void setSizeIndex(List<SizeMapI> list){
        
        //Use StyleGroupI records to build list
        if (listSz == null && sizeMap == null){
            setSizeIndex_NoSize(list);
        }
        else if (sizeMap != null){
            setSizeIndex_SizeMap(list);
        }
        else {
            setSizeIndex_SizeList(list);
        }
        
    }
    
    /**
     * Build sorted list of sizes based on row objects and size object list 
     * 
     * @param List of row objects that contain the size code to process
     * @param size map object
     */
    private void setSizeIndex_SizeMap(List<SizeMapI> list){
        
        List<String> groups = new ArrayList<>();
        
        List<Long> ids = new ArrayList<>();
        List<MSize> sizes = new ArrayList<>();
        
        //Find empty group sizes
        for (SizeMapI rec: list){
            Long s_id = rec.getSizepId();
            String gr = rec.getStyleGroup();
            
            //Look up size to find group
            if (gr == null){
                gr = findFirstNonEmptyGroupForSizeId(s_id);
            }
            
            if (gr.isEmpty() && !ids.contains(s_id)){
                ids.add(s_id);
                sizes.add(sizeMap.findSize(s_id));
            }
            
            if (!gr.isEmpty() && !groups.contains(gr)){
                groups.add(gr);
            }
        }
        
        Collections.sort(sizes, new Comparator<MSize>(){
            public int compare(MSize s1, MSize s2) {
                return s1.getSort().compareTo(s2.getSort());
            }});
        
        //Reconfigure size map object
        List<String> groupsX = sizeMap.getGroupsExcludeEmpty();
        for (String gr : groupsX){
            if (!groups.contains(gr)){
                sizeMap.removeGroup(gr); 
            }
        }
        
        sizeMap.removeEmptyGroupAndSizeIds();
        for (MSize s : sizes){
            sizeMap.addSize(s.getId(), s.getCode(), s.getCombine(), "", s.getSort());
        }
        
        sizeMap.configThisObject();
        numberOfSizes = sizeMap.getAdjustedNumberOfIndexes();
    }
    
    /**
     * Return the first size group for the passed in size id
     * @param sizepId
     * @return
     */
    protected String findFirstNonEmptyGroupForSizeId (Long sizepId){
        if (!isMouldGroups){
            return "";
        }
        return sizeMap != null? sizeMap.findFirstNonEmptyGroupForSizeId(sizepId) : "";
    }
    
    
    /**
     * Build sorted list of sizes based on row objects only 
     * 
     * @param List of row objects that contain the size code to process
     */
    private void setSizeIndex_NoSize(List<SizeMapI> list){
        sizeMap = new SizeMap(SizeMap.STYLE_GROUP);
        List<Long> ids = new ArrayList<>();
        int sort = 1;
        
        for (SizeMapI rec: list){
            if (!ids.contains(rec.getSizepId())){
                numberOfSizes++;
                ids.add(rec.getSizepId());
                sizeMap.addSize(rec.getSizepId(), 
                        rec.getSize(), 
                        "", //combine 
                        "", //group
                        sort++);        
            }
        }
        sizeMap.configThisObject();
    }
    
    
    /**
     * Build sorted list of sizes based on row objects and size object list 
     * 
     * @param List of row objects that contain the size code to process
     */
    private void setSizeIndex_SizeList(List<SizeMapI> list){
        sizeMap = new SizeMap(SizeMap.STYLE_GROUP);
        
        for (Size sz : listSz){
            boolean found = false;
            for (SizeMapI rec: list){
                if (rec.getSizepId().equals(sz.getId())){
                    found = true;
                    break;
                }
            }
            
            if (found){
                numberOfSizes++;
                sizeMap.addSize(sz.getId(), 
                        sz.getCode(), 
                        "", //combine 
                        "", //group
                        sz.getSort());  
            }
        }
        sizeMap.configThisObject();
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
