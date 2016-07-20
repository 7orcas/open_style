package com.sevenorcas.openstyle.app.service.spreadsheet;

import java.util.List;

/**
 * Interface for entities that use free form spreadsheets<p>
 *  
 * [License] 
 * @author John Stewart
 */
public interface FreeFormSpreadsSheetI {
    public List<SpreadSheet> getSpreadSheetList() throws Exception;
}
