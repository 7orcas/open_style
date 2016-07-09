package com.sevenorcas.openstyle.app.spreadsheet;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;


/**
 * Spreadsheet utility class<p>
 * 
 * Clients can use <b>this</b> class to create spreadsheets which are then used by <code>SpreadSheetService</code> service to create the file.<p>
 * 
 * This class works tightly with the <code>SpreadsheetCell</code> class.<p>
 *
 * Thanks to http://poi.apache.org/spreadsheet/quick-guide.html
 * 
 * [License] 
 * @author John Stewart
 */
public class SpreadSheet implements SpreadSheetI {

    final static private int DEFAULT_STYLE_ID  = -1;
	
	private Hashtable <Long, CellObject> cells;
	
	//Default formats (priority: 1st cell, 2nd row, 3rd column)
	private Hashtable <Integer, List<Object>> rows;
	private Hashtable <Integer, List<Object>> columns;
	private Hashtable <Integer, Integer> columnWidths;
	
	private String sheetname,
	               dateFormat,
	               numberFormat;
	
	private int col = 0,
	            row = 0,
	            columnMax,
	            rowMax; 

	private Integer freezePane1,
	                freezePane2,
	                freezePane3,
	                freezePane4;
	
	
	/**
	 * Cell styles are reusable (max of 4000).<ul>
	 * <li>key = style number</li>
	 * <li>value = HSSFCellStyle</li>
	 * </ul> 
	 */
	private Hashtable<Integer, HSSFCellStyle> styles = null; 
	private Colors colorCustomers;
    private HSSFWorkbook workBook;
	
	
	/**
	 * 
	 * @param sheet name
	 * @param date format (can be null)
	 */
	public SpreadSheet (String sheetname, String dateFormat, String numberFormat, Colors colorCustomers){
		this.sheetname      = sheetname;
		this.dateFormat     = dateFormat;
		this.numberFormat   = numberFormat;
		this.colorCustomers = colorCustomers;
		
		columnMax   = 0;
        rowMax      = 0;
        
        cells   = new Hashtable<Long,    CellObject>();
        rows    = new Hashtable<Integer, List<Object>>();
        columns = new Hashtable<Integer, List<Object>>();
        styles  = new Hashtable<Integer, HSSFCellStyle>();
        
        columnWidths = new Hashtable<Integer, Integer>();
	}
	
	public String getSheetname() {
		return sheetname;
	}
	
	
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public void incrementCol() {
		this.col++;
	}
	
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public void incrementRow() {
		this.row++;
	}

	public int getLastColumn() {
		return columnMax;
	}

	public int getLastRow() {
		return rowMax;
	}

	public void setColumnWidth(int col, int width){
	    columnWidths.put(col, width);
	}
	
	public int getColumnWidth(int col){
        Integer x = columnWidths.get(col);
        return x != null? x : -1;
    }
	
	/**
	 * Add a 2nd priority (cell is always top) format to current row
	 * @param format
	 */
	public void addRowFormat(Object format){
	    addFormat(rows, row, format);
	}
	
	/**
     * Add a 2nd priority (cell is always top) format to current column
     * @param col
     * @param format
     */
    public void addColumnFormat(Object format){
        addColumnFormat(col, format);
    }
	
    /**
     * Add a 2nd priority (cell is always top) format to current column
     * @param col
     * @param format
     */
    public void addColumnFormat(int col, Object format){
        addFormat(columns, col, format);
    }
    
	/**
     * Add a format 
     * @param table
     * @param key
     * @param format
     */
    private void addFormat(Hashtable <Integer, List<Object>> t, int key, Object format){
        List<Object> r = t.get(key);
        if (r == null){
            r = new ArrayList<Object>();
            t.put(key, r);
        }
        if (!isFormat(t, key, format)){
            r.add(format);
        }
    }
	
    
    
    /**
     * Return if row format exists
     * @param row
     * @param format
     * @return
     */
    private boolean isRowFormat(int row, Object format){
        return isFormat(rows, row, format);
    }
    
    /**
     * Return if column format exists
     * @param col
     * @param format
     * @return
     */
    private boolean isColFormat(int col, Object format){
        return isFormat(columns, col, format);
    }
    
    /**
     * Return a row color format (if exists)
     * @param col
     * @param format
     * @return
     */
    private SpreadsheetColor getRowColor(int row, boolean background){
        return getColor(rows, row, background);
    }
    
    /**
     * Return a column color format (if exists)
     * @param col
     * @param format
     * @return
     */
    private SpreadsheetColor getColColor(int col, boolean background){
        return getColor(columns, col, background);
    }

    
    /**
     * Return a row or column color format (if exists)
     * @param col
     * @param format
     * @return
     */
    private SpreadsheetColor getColor(Hashtable <Integer, List<Object>> t, int row, boolean background){
        List<Object> r = t.get(row);
        if (r == null){
            return null;
        }
        for (Object x: r){
            if (x instanceof SpreadsheetColor){
                SpreadsheetColor c = (SpreadsheetColor)x;
                if (background && c.isBackground()){
                    return c;
                }
                if (!background && !c.isBackground()){
                    return c;
                }
            }
        }
        return null;
    }
    
    
    /**
     * Test for a format 
     * @param table
     * @param key
     * @param format
     */
    private boolean isFormat(Hashtable <Integer, List<Object>> t, int key, Object format){
        List<Object> r = t.get(key);
        if (r == null){
            return false;
        }
        for (Object x: r){
            if (!x.getClass().getName().equals(format.getClass().getName())){
                continue;
            }
            if (x.toString().equals(format.toString())){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Cell hashtable key
     * @param column
     * @param row
     * @return
     */
    private Long cellKey(int column, int row){
        return (Long)new Long((column + 1) * 1000000 + row);
    }
    
	protected Object getCellValue(int column, int row){
		CellObject cellObject = cells.get(cellKey(column, row));
		Object object = cellObject != null? cellObject.getObject() : null;
		
		if (object != null){
			SpreadsheetCell cell = cellObject.getCell();
            
			switch (cell.getClazz() != null? cell.getClazz() : CLASS_STRING){
			    case CLASS_PERCENTAGE:
//			    	object = ((Double)object) / 100;
			    	break;
			
			    case CLASS_BOOLEAN:
//			    	Boolean valueB = (Boolean)object;
//			    	object = valueB? mainSL.getLangText("JA") : mainSL.getLangText("NEIN"); 
//			    	break;
			}
			
        }
		
		return object;
	}
	
	public SpreadsheetCell getCell(int column, int row){
	    CellObject cellObject = cells.get(cellKey(column, row));
		return cellObject != null? cellObject.getCell() : null;
	}
	
	protected void addCell (int column, int row){
		if (columnMax < column){
			columnMax = column;
		}
		if (rowMax < row){
			rowMax = row;
		}
	}
	
	
	
	/**
	 * Add a data column and then increment the cell column counter.
	 * @param object
	 * @param style id
	 * @return
	 */
	public SpreadsheetCell addCell (Object object, int styleId){
		SpreadsheetCell cell = addCell (getCol(), getRow(), object, styleId);
		incrementCol();
		return cell; 
	}
	
	/**
     * Add a data column
     * @param column
     * @param row
     * @param object
     * @return
     */
    public SpreadsheetCell addCell (int column, int row, Object object, int styleId){
        SpreadsheetCell cell = new SpreadsheetCell(column, row, styleId, this);
        cells.put(cellKey(column, row), new CellObject(object, cell));
        return cell;
    }
	
	/**
     * Set a color by substituting an used color
     * 
     * @param workbook
     * @param SpreadsheetColor
     * @return
     */
	public HSSFColor setColor(HSSFWorkbook workbook, SpreadsheetColor c){
	    return setColor(workbook, c.r, c.g, c.b);
	}
	
	/**
	 * Set a color by substituting an used color
	 * 
	 * Thanks to http://stackoverflow.com/questions/10528516/poi-setting-background-color-to-a-cell
	 * Thanks to http://stackoverflow.com/questions/842817/how-does-java-convert-int-into-byte
	 * 
	 * @param workbook
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public HSSFColor setColor(HSSFWorkbook workbook, int r,int g, int b){
	    HSSFPalette palette = workbook.getCustomPalette();
	    HSSFColor hssfColor = null;
	    try {
	        byte rb = (byte)(r);
	        byte gb = (byte)(g);
	        byte bb = (byte)(b);
	        
	        hssfColor= palette.findColor(rb, gb, bb); 
	        if (hssfColor == null ){
	            short s = colorCustomers.getColorIndex(colorCustomers.getNextIndex());
	            palette.setColorAtIndex(s, rb, gb, bb);
	            hssfColor = palette.getColor(s);
	        }
	    } catch (Exception e) {}

	    return hssfColor;
	}
	
	
	public boolean containsStyleId(int styleId){
	    return styles.containsKey(styleId);
	}
	
	public HSSFCellStyle getStyle(int styleId){
        return styles.get(styleId);
    }
	

	/**
	 * Set a cell style as header
	 * 
	 * Thanks to http://www.experts-exchange.com/Programming/Languages/Java/Q_24242777.html
	 * 
	 * @param workbook
	 * @param style id
	 * @param rowStyle
	 * @param column
	 * @param wb
	 * @return
	 */
	protected void setStyleHeader (HSSFWorkbook wb, HSSFCellStyle style, int styleId){ //EX1, int row, int column, SpreadsheetCell cell){
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		style.setLocked(true);
		style.setBottomBorderColor(HSSFColor.WHITE.index);
		style.setLeftBorderColor(HSSFColor.WHITE.index);
		style.setRightBorderColor(HSSFColor.WHITE.index);
		style.setTopBorderColor(HSSFColor.WHITE.index);
		style.setFillForegroundColor(getHeaderBGColorIndex());
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	}
	
	public short getHeaderBGColorIndex(){
	    return HSSFColor.GREY_25_PERCENT.index;
	}
	
	
	protected void setStyleBold (HSSFWorkbook wb, HSSFCellStyle style, int styleId){ //EX1, int row, int column, SpreadsheetCell cell){
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
	}
	
	
	
	/**
     * Test if row format exists, if so return it otherwise use the default
     * @param row
     * @param format
     * @param default format
     * @return
     */
    private int testRowFormat (int row, int format, int defaultFormat){
        if (isRowFormat(row, format)){
            return format;
        }
        return defaultFormat;
    }
	

    /**
     * Test if column format exists, if so return it otherwise use the default
     * @param column
     * @param format
     * @param default format
     * @return
     */
    private int testColFormat (int col, int format, int defaultFormat){
        if (isColFormat(col, format)){
            return format;
        }
        return defaultFormat;
    }
    
	/**
     * Get a default cell format
     * @param wb
     * @return
     */
    public HSSFCellStyle getCellStyleDefault(HSSFWorkbook wb, int row, int col){
        if (styles.containsKey(DEFAULT_STYLE_ID)){
            return styles.get(DEFAULT_STYLE_ID);
        }
        HSSFCellStyle style = wb.createCellStyle();
        setCellStyle(wb, style, null, DEFAULT_STYLE_ID);
        return style;
    }
	
    
    /**
     * Format a style
     * @param wb
     * @return
     */
    public void setCellStyle(HSSFWorkbook wb, HSSFCellStyle style, SpreadsheetCell cell, int styleId){
        
        int row = cell != null? cell.getRow() : -1;
        int col = cell != null? cell.getColumn() : -1; 
        
        
        //Check row or column formats
        int formatTyp = cell != null? cell.getFormatTyp() : FORMAT_NORMAL; 
        formatTyp = formatTyp == FORMAT_NORMAL? testRowFormat(row, FORMAT_HEADER, formatTyp) : formatTyp;
        formatTyp = formatTyp == FORMAT_NORMAL? testRowFormat(row, FORMAT_BOLD,   formatTyp) : formatTyp;
        formatTyp = formatTyp == FORMAT_NORMAL? testColFormat(col, FORMAT_HEADER, formatTyp) : formatTyp;
        formatTyp = formatTyp == FORMAT_NORMAL? testColFormat(col, FORMAT_BOLD,   formatTyp) : formatTyp;
        
        
        switch (formatTyp){
            case FORMAT_HEADER:
                if (cell != null){
                    cell.setFormatTyp(FORMAT_HEADER);
                }
                setStyleHeader(wb, style, cell.getStyleId());
                break;
            case FORMAT_BOLD:
                if (cell != null){
                    cell.setFormatTyp(FORMAT_BOLD); 
                }
                setStyleBold(wb, style, cell.getStyleId());
                break;
        }
        

        
        //Check row or column justifications
        int justification = cell != null? cell.getJustification() : ALIGN_UNDEFINED;
        justification = justification == ALIGN_UNDEFINED? testRowFormat(row, ALIGN_LEFT,  justification) : justification;
        justification = justification == ALIGN_UNDEFINED? testRowFormat(row, ALIGN_CENTER,justification) : justification;
        justification = justification == ALIGN_UNDEFINED? testRowFormat(row, ALIGN_RIGHT, justification) : justification;
        justification = justification == ALIGN_UNDEFINED? testColFormat(col, ALIGN_LEFT,  justification) : justification;
        justification = justification == ALIGN_UNDEFINED? testColFormat(col, ALIGN_CENTER,justification) : justification;
        justification = justification == ALIGN_UNDEFINED? testColFormat(col, ALIGN_RIGHT, justification) : justification;
        
        switch (justification){
            case ALIGN_LEFT:   style.setAlignment(HSSFCellStyle.ALIGN_LEFT);   break;
            case ALIGN_RIGHT:  style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);  break;
            case ALIGN_CENTER: style.setAlignment(HSSFCellStyle.ALIGN_CENTER); break;
            default: //none
        }
        
        //Check row or column vertical align
        int valign = cell != null? cell.getValign() : VALIGN_UNDEFINED;
        valign = valign == VALIGN_UNDEFINED? testRowFormat(row, VALIGN_TOP,   valign) : valign;
        valign = valign == VALIGN_UNDEFINED? testRowFormat(row, VALIGN_CENTER,valign) : valign;
        valign = valign == VALIGN_UNDEFINED? testRowFormat(row, VALIGN_BOTTOM,valign) : valign;
        valign = valign == VALIGN_UNDEFINED? testColFormat(col, VALIGN_TOP,   valign) : valign;
        valign = valign == VALIGN_UNDEFINED? testColFormat(col, VALIGN_CENTER,valign) : valign;
        valign = valign == VALIGN_UNDEFINED? testColFormat(col, VALIGN_BOTTOM,valign) : valign;
        
        switch (valign){
            case VALIGN_TOP:   style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);    break;
            case VALIGN_CENTER:style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); break;
            case VALIGN_BOTTOM:style.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM); break;
            default:style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        }
        
        //Text wrap
        int wrap = cell != null? cell.getWrap() : -1;
        wrap = wrap != -1? testRowFormat(row, TEXT_WARP_ON,  wrap) : wrap;
        wrap = wrap != -1? testColFormat(col, TEXT_WARP_ON,  wrap) : wrap;
        if (wrap == TEXT_WARP_ON){
            style.setWrapText(true);
        }
        
        if (cell != null && cell.getBgColor() != null){
            SpreadsheetColor c = cell.getBgColor();
            style.setFillForegroundColor(setColor(wb, c).getIndex());
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        }
        else if (getRowColor(row, true) != null){
            SpreadsheetColor c = getRowColor(row, true);
            style.setFillForegroundColor(setColor(wb, c).getIndex());
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        }
        else if (getColColor(col, true) != null){
            SpreadsheetColor c = getColColor(col, true);
            style.setFillForegroundColor(setColor(wb, c).getIndex());
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        }
        
        int border = cell != null? cell.getBorder() : -1;
        border = border != -1? testRowFormat(row, BORDER_ON,  border) : border;
        border = border != -1? testColFormat(col, BORDER_ON,  border) : border;
        if (border == BORDER_ON){
            style.setBorderTop((short) 1); 
            style.setBorderBottom((short) 1);
            style.setBorderRight((short) 1); 
            style.setBorderLeft((short) 1); 
        }
        
        styles.put(styleId, style);
    }

	
	private class CellObject {
		Object object;
		SpreadsheetCell cell;
		private CellObject(Object object, SpreadsheetCell cell) {
			this.object = object;
			this.cell = cell;
		}
		public Object getObject() {
			return object;
		}
		public SpreadsheetCell getCell() {
			return cell;
		}
	}


    public String getDateFormat() {
        return dateFormat;
    }

    public HSSFWorkbook getWorkBook() {
        return workBook;
    }

    public void setWorkBook(HSSFWorkbook workBook) {
        this.workBook = workBook;
    }

    /**
     * Set up freeze pane for <b>this</b> sheet
     * @param freezePane1
     * @param freezePane2
     * @param freezePane3
     * @param freezePane4
     */
    public void setFreezePane(Integer freezePane1, Integer freezePane2, Integer freezePane3, Integer freezePane4) {
        this.freezePane1 = freezePane1;
        this.freezePane2 = freezePane2;
        this.freezePane3 = freezePane3;
        this.freezePane4 = freezePane4;
    }
	
    
    /**
     * Freeze pane (if configured)<p>
     * 
     * Thanks to https://poi.apache.org/spreadsheet/quick-guide.html#Splits
     * 
     * @param sheet
     */
    public void createFreezePane(HSSFSheet sheet) {
        if (freezePane1 != null){
            sheet.createFreezePane(freezePane1, freezePane2, freezePane3, freezePane4);
            //sheet.createSplitPane(2000,2000,0,0,Sheet.PANE_LOWER_LEFT);
        }
    }
    
	
	
}
