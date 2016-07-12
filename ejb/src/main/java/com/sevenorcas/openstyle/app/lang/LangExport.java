package com.sevenorcas.openstyle.app.lang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sevenorcas.openstyle.app.entity.Export;
import com.sevenorcas.openstyle.app.spreadsheet.Colors;
import com.sevenorcas.openstyle.app.spreadsheet.FreeFormSpreadsSheetI;
import com.sevenorcas.openstyle.app.spreadsheet.SpreadSheet;
import com.sevenorcas.openstyle.app.spreadsheet.SpreadSheetI;



/**
 * Spreadsheet Export of Language key-value pairs.<p>
 * 
 * <b>This</b> class is used to wrap lists of <code>LangAllDto</code> objects in preparation for export to a spreadsheet.<p>
 * 
 * [License]
 * @author John Stewart
 */
@SuppressWarnings("serial")
@Export(entity="Labels")
public class LangExport implements SpreadSheetI, FreeFormSpreadsSheetI, Serializable {
	
    //Cell style ids
    static final private int STYLE_HEADER        = 1;
    static final private int STYLE_NORMAL        = 2;
    
    private Language lang;
    private List<LangKey> list;
    
	public LangExport(List<LangKey> list, Language lang) {
		this.list = list;
		this.lang = lang;
	}

	
	/**
     * Output spreadsheet.
     * 
     * @throws Exception 
     */
    public List<SpreadSheet> getSpreadSheetList() throws Exception{
        
        List<SpreadSheet> sheets = new ArrayList<>();
        
        SpreadSheet ss = new SpreadSheet("Labels", "", "", new Colors());
        sheets.add(ss);
        header(ss, 0);
        
        for (LangKey k: list){
            ss.incrementRow();
            for (LangValue v: k.getValues()){
                row (k, v, ss, 0);
            }
        }
        
        return sheets;
        
    }
	
    private void row (LangKey k, LangValue v, SpreadSheet sheet, int startCol){
        sheet.setCol(startCol);
        sheet.addCell(k.getKey(), STYLE_NORMAL);
        sheet.addCell(v.getLangcode(), STYLE_NORMAL);
        sheet.addCell(v.getText(), STYLE_NORMAL);
        sheet.addCell(k.getSets(), STYLE_NORMAL);
        sheet.addCell(k.isClient(), STYLE_NORMAL);
    }
    
    /**
     * Header row
     * @param sheet
     * @param startCol
     */
    private void header(SpreadSheet sheet, int startCol){
        sheet.addRowFormat(FORMAT_HEADER);
        
        sheet.setCol(startCol);

        sheet.addColumnFormat(ALIGN_LEFT);
        sheet.setColumnWidth(sheet.getCol(), 5000);
        sheet.addCell (lang.getLabel("Key"), STYLE_HEADER);

        sheet.addColumnFormat(ALIGN_CENTER);
        sheet.setColumnWidth(sheet.getCol(), 1500);
        sheet.addCell (lang.getLabel("Langcode"), STYLE_HEADER);
        
        sheet.addColumnFormat(ALIGN_LEFT);
        sheet.setColumnWidth(sheet.getCol(), 15000);
        sheet.addCell (lang.getLabel("Text"), STYLE_HEADER);
        
        sheet.addColumnFormat(ALIGN_LEFT);
        sheet.setColumnWidth(sheet.getCol(), 3000);
        sheet.addCell (lang.getLabel("Sets"), STYLE_HEADER);
        
        sheet.addColumnFormat(ALIGN_CENTER);
        sheet.setColumnWidth(sheet.getCol(), 2000);
        sheet.addCell (lang.getLabel("Client"), STYLE_HEADER);
        
    }
	
	
}
