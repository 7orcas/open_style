package com.sevenorcas.openstyle.app.service.spreadsheet;

import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Spreadsheet cell utility class<p>
 * 
 * This class works tightly with the <code>Spreadsheet</code> class.  
 * 
 * [License] 
 * @author John Stewart
 */
public class SpreadsheetCell implements SpreadSheetI {
    private int column,
                row,
                styleId,
                formatTyp,
                width,
                justification,
                valign,
                border,
                wrap;
    
    private SpreadSheet sheet;
    private Integer clazz;
    private Object object; //control object 
    private SpreadsheetCell headerCell;
    private CellRangeAddress crAdress;
    private SpreadsheetColor bgColor;
    private SpreadsheetCellCallBackI callback;
    private SpreadsheetCellStyleCallBackI callbackStyle;
    
    protected SpreadsheetCell (int column, int row, int styleId, SpreadSheet sheet){
        this.column      = column;
        this.row         = row;
        this.styleId     = styleId;
        this.sheet       = sheet;
        
        width         = -1;
        formatTyp     = FORMAT_NORMAL;
        justification = ALIGN_UNDEFINED;
        valign        = VALIGN_UNDEFINED;
        border        = BORDER_OFF;
        wrap          = TEXT_WARP_OFF;
        sheet.addCell (column, row);
    }
    
    
    public int getColumn() {
        return column;
    }
    public int getRow() {
        return row;
    }
    
    public void setHeaderCell(SpreadsheetCell headerCell) {
        this.headerCell = headerCell;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    public void setJusitification(int jusitification) {
        this.justification = jusitification;
    }

    public Object getObject() {
        return object;
    }
    public void setObject(Object object) {
        this.object = object;
    }

    public void setClazz(Integer clazz) {
        this.clazz = clazz;
    }
    public Integer getClazz() {
        if (clazz == null && headerCell != null){
            return headerCell.getClazz();
        }
        return clazz;
    }
    public void setValign(int valign) {
        this.valign = valign;
    }
    public void setWrap(int wrap) {
        this.wrap = wrap;
    }
    public void setBorder(int border) {
        this.border = border;
    }

    public void setBold(){
        formatTyp = FORMAT_BOLD;
    }
    public void setHeader(){
        formatTyp = FORMAT_HEADER;
    }
    public boolean isHeader(){
        return formatTyp == FORMAT_HEADER;
    }
    
    public void addMergedRegion(int r1, int r2, int c1, int c2){
       crAdress = new CellRangeAddress(r1, r2, c1, c2);
    }
    public CellRangeAddress getCellRangeAddress() {
        return crAdress;
    }

    public void setBackgroundColorRGB(int r,int g, int b){
        bgColor = new SpreadsheetColor (r,g, b);
    }
    public void setBackgroundColorRGB(SpreadsheetColor c){
        bgColor = c;
    }
    
    /**
     * Return cell width based on predefined setting or it's class
     * @return
     */
    public Integer getWidth(){
        if (headerCell != null){
            return headerCell.getWidth();
        }
        
        if (width != -1){
            return width;
        }
        if (clazz == null){
            return null;
        }
        switch (clazz){
            case CLASS_INTEGER:
            case CLASS_INTEGER_LEFT:
                return 3000;
            case CLASS_DOUBLE:
            case CLASS_STRING:
                return 5000;
            case CLASS_DATE:
            case CLASS_LONG:
            case CLASS_BOOLEAN:
            case CLASS_CHARACTER:
            case CLASS_PERCENTAGE:
            case CLASS_UKURS:
        }
        
        return null;
    }
    
    
    
    /**
     * Callback to allow format of cell value
     * @param callback
     */
    public void setCallback(SpreadsheetCellCallBackI callback) {
        this.callback = callback;
    }

    /**
     * Callback to allow format of cell style
     * @param callback
     */
    public void setCallbackStyle(SpreadsheetCellStyleCallBackI callbackStyle) {
        this.callbackStyle = callbackStyle;
    }


    protected String getCellStyleFormat(){
        return "" + formatTyp 
                  + "," + justification 
                  + "," + valign 
                  + "," + border 
                  + "," + wrap
                  + (bgColor != null? "," + bgColor.total() : 0);
    }
    
    /**
     * Get cell format<p>
     * 
     * Thanks to http://stackoverflow.com/questions/15248284/using-poi-how-to-set-the-cell-type-as-number
     * @param wb
     * @return
     */
    public HSSFCellStyle getCellStyle(HSSFWorkbook wb){
        
        
        //EX1
        if (sheet.containsStyleId(styleId)){
            return sheet.getStyle(styleId);
        }
        
        HSSFCellStyle style = wb.createCellStyle();
        Integer clazzX = clazz != null? clazz : (headerCell != null? headerCell.clazz : null);  
        
        switch (clazzX != null? clazzX : CLASS_STRING){
            case CLASS_DATE:
                if (!isHeader()){
                    CreationHelper createHelper = wb.getCreationHelper();
                    style = wb.createCellStyle();
                    style.setDataFormat(createHelper.createDataFormat().getFormat(
                            sheet.getDateFormat() != null? sheet.getDateFormat() : "m/d/yy"));
                }
                style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                break;  
                
            case CLASS_PERCENTAGE:
                style = wb.createCellStyle();
                style.setDataFormat(wb.createDataFormat().getFormat("0.00%"));
                style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                break;
                
            case CLASS_INTEGER:
            case CLASS_LONG:
                style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
                break;
                
            case CLASS_DOUBLE:
            case CLASS_UKURS:
                style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
                break;
            
            case CLASS_STRING:
            case CLASS_INTEGER_LEFT:
            case CLASS_BOOLEAN:
            case CLASS_CHARACTER:
            default:
                style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        }
        
        sheet.setCellStyle(wb, style, this, styleId);
        return style;
    }
    
    /**
     * Set the cell formatted value
     * @param cell
     */
    public void setCellValue (HSSFCell cell, HSSFWorkbook wb){
        Object object = sheet.getCellValue(column, row);
        if (object == null){
            return;
        }
        
        Integer clazzX = clazz != null? clazz : (headerCell != null? headerCell.clazz : null);  
        
        switch (!isHeader() && clazzX != null? clazzX : CLASS_STRING){
            case CLASS_INTEGER:
            case CLASS_INTEGER_LEFT:
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                cell.setCellValue(((Integer)object));
                if (callbackStyle != null){
                    cell.setCellStyle(callbackStyle.getCellStyle(wb, sheet, cell));
                }
                return;
                
            case CLASS_LONG:
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                cell.setCellValue(((Long)object));
                if (callbackStyle != null){
                    cell.setCellStyle(callbackStyle.getCellStyle(wb, sheet, cell));
                }
                return;
                
            case CLASS_PERCENTAGE:
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                Double objectD = (Double)object / 100;
                cell.setCellValue((objectD));
                if (callbackStyle != null){
                    cell.setCellStyle(callbackStyle.getCellStyle(wb, sheet, cell));
                }
                return;
                
            case CLASS_DOUBLE:
            case CLASS_UKURS:
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                cell.setCellValue(((Double)object));
                if (callbackStyle != null){
                    cell.setCellStyle(callbackStyle.getCellStyle(wb, sheet, cell));
                }
                return;
                
            case CLASS_DATE:
                if (object instanceof Date){
                    cell.setCellValue(((Date)object));
                    return;
                }
                break;
                
            case CLASS_BOOLEAN:
                Boolean valueB = (Boolean)object;
                object = valueB? "Yes" : "No"; // mainSL.getLangText("JA") : mainSL.getLangText("NEIN"); 
                break;
            
            //String is default
            default:
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                if (callback != null){
                    cell.setCellValue(callback.getCellValue (wb, sheet, cell, object.toString()));
                    return;
                }
                
                HSSFRichTextString richString = new HSSFRichTextString(object.toString());
                cell.setCellValue(richString);
                cell.setCellValue(object.toString());
                return;
        }
        
        cell.setCellValue(object.toString());
        
    }
    
    
    
    
    /**
     * Override method for comparison
     */
    public boolean equals(Object object){
        if (this == object){
            return true;
        }
        if (object instanceof SpreadsheetCell){
            SpreadsheetCell key = (SpreadsheetCell)object;
            
            if (key.column == this.column && key.row == this.row){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Override method for comparison
     */
    public int hashCode(){
        return ("" + column + "," + row).hashCode(); 
    }

    public int getFormatTyp() {
        return formatTyp;
    }
    public void setFormatTyp(int formatTyp) {
        this.formatTyp = formatTyp;
    }


    public int getJustification() {
        return justification;
    }


    public int getValign() {
        return valign;
    }


    public int getBorder() {
        return border;
    }


    public int getWrap() {
        return wrap;
    }


    public SpreadsheetCell getHeaderCell() {
        return headerCell;
    }


    public SpreadsheetColor getBgColor() {
        return bgColor;
    }


    public int getStyleId() {
        return styleId;
    }


    public SpreadSheet getSheet() {
        return sheet;
    }

    
    
    
    
}
