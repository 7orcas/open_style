package com.sevenorcas.openstyle.app.service.spreadsheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.sevenorcas.openstyle.app.application.ApplicationParameters;
import com.sevenorcas.openstyle.app.mod.lang.LanguageService;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.entity.Export;
import com.sevenorcas.openstyle.app.service.entity.Field;

/**
 * Service to export an entity to a spreadsheet file.<p>
 * 
 * The passed in entity class must be annotated with <code>@Export</code> (the 'entity' attribute is optional and is used to name the spreadsheet tab).<p>
 * 
 * The first occurrence of the <code>@SpreadSheetDef</code> annotation is used to get the list of fields / methods that make up the columns of the 
 * spreadsheet. The passed in entity maybe a simple object, a <code>List</code> or an object with multiple <code>List</code>s (either as children or
 * as multiple hierarchical parent-child objects).  
 * In the case of a passed in <code>List</code> the first object is read to get the initial <code>@SpreadSheetDef</code> annotation.<p> 
 * 
 * The file is generated and saved to the temporary directory (as defined in the <code>ApplicationParameters</code> singleton). The client is returned
 * the generated filename. The client must then recall the server to retrieve the file.<p>
 *  
 * TODO: Make spreadsheet export Locale aware via the UserParam object. This can then format values if the @Field annotation is used.
 * TODO: Allow call methods to be passed the UserParam object for format and language processing.
 * TODO: Append userid to filename to provide security to stop other users retrieving it. 
 * TODO: Refactor annotations to allow headings and new sheets if export.entity is used per list
 * TODO: Unit Test
 *  
 * [License] 
 * @author John Stewart*
 */
@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class SpreadSheetServiceImp implements SpreadSheetService {

    private ApplicationParameters appParam = ApplicationParameters.getInstance();
    
	@EJB private LanguageService languageService;
	
	

	/**
	 * Generate and save the file to the temporary file directory
	 * 
	 * @param UserParam standard parameters
	 * @param object to be exported to spreadsheet
	 * @param base filename. An incremental number name maybe appended to create uniqueness. 
	 * @param language code
	 * @param spreadsheet password (optional)
	 * @return generated file name
	 * @throws Exception
	 */
	public String generateFile(UserParam userParam, Object exportObject, String outFilename, String language, String password) throws Exception{
		
		if (outFilename == null){
			throw new Exception("InvalidFilename");
		}
        
		String filename = appParam.getTempFilePath(userParam) + outFilename;
		int index = filename.indexOf(outFilename);
		
		//Increment file name
		filename = filename(filename, 0); 

		generateSpreadSheetFile(exportObject, filename, language, password);
		return filename.substring(index);
	}
	
	
	/**
	 * Return generated file from the temporary directory 
	 * 
	 * @param UserParam standard parameters
	 * @param filename
	 * @param true == delete file
	 * @return
	 * @throws Exception
	 */
	public Response returnFile(UserParam userParam, String filename, boolean delete) throws Exception{
		File file = new File(appParam.getTempFilePath(userParam) + filename);
		FileInputStream fis = new FileInputStream(file);
		Response res = Response.ok(org.apache.commons.io.IOUtils.toByteArray(fis))
				.header("Content-Disposition","attachment; filename="+filename)
				.build();
		fis.close();
		if (delete){
			file.delete();
		}
		return res;
	}
	
	
	
	/**
	 * Generate the spreadsheet file.
	 * TODO: Do code to make password optional  
	 * @param object to be exported to spreadsheet
	 * @param output filename
	 * @param language
	 * @param spreadsheet password (optional)
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private void generateSpreadSheetFile(final Object exportObject, String outFilename, String language, final String password) throws Exception{
		
		//Get necessary master-data
		final Hashtable<String, Object>mdata = new Hashtable<>();
		mdata.put("Language", languageService.getLanguage(language));
		
		
		//Set parameters
		HSSFWorkbook wb = new HSSFWorkbook(); 
			
		Object header = null;
		
		if (exportObject instanceof List){
			List list = (List)exportObject;
			if (list.size() == 0){
				throw new Exception("Emtpy List");
			}
			header = list.get(0);
		}
		else{
			header = exportObject;
		}
		
		Def def = getDefinition(header, true);
		List<SpreadSheet> sheets = new ArrayList<>();
		Colors cc = new Colors();
		
		if (exportObject instanceof FreeFormSpreadsSheetI){
		    sheets = ((FreeFormSpreadsSheetI)exportObject).getSpreadSheetList();
		}
		else if (exportObject instanceof List){
		    sheets.add(exportList((List)exportObject, new SpreadSheet(def.entity, "", "", cc)));
		}
		else{
		    sheets.add(setValues(def, exportObject, new SpreadSheet(def.entity, "", "", cc)));
		}
		
				
		exportSpreadSheet(sheets, wb);
		
		
		//Write file
		File file = new File(outFilename);
		File dirs = new File(file.getParent());
		dirs.mkdirs();
		FileOutputStream fileOut = new FileOutputStream(outFilename);
		wb.write(fileOut);
		fileOut.close();
		
	}
	
	/**
	 * Export a list to a sheet
	 * @param list
	 * @param sheet to export to
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private SpreadSheet exportList(List list, SpreadSheet sheet) throws Exception{
		if (list.size() == 0){
			return sheet;
		}
		Object header = list.get(0);
		Def def = getDefinition(header, false);
		setColumns(def, sheet);
		sheet.incrementRow();
		
		for (Object obj: list){
			setValues(def, obj, sheet);
			sheet.incrementRow();
		}
		return sheet;
	}
	
	
	

	/**
	 * Get the export definition. This contains the export objects fields and methods to be used in each column.  
	 * <p>
	 * Note: This method maybe called multiple times (ie for different lists).
	 * 
	 * @param export object
	 * @param flag to test <code>@Export</code> annotation. This annotation is only used once.
	 * @return definition object
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Def getDefinition(Object obj, boolean requiresExport) throws Exception{
		
		Def def = new Def ();
		Class clazz = obj.getClass();

		Export anno = obj.getClass().getAnnotation(Export.class);
		
		if (requiresExport && (anno == null || !anno.isExportSpreadSheet())){
			throw new Exception("Object must be annotated with Export");
		}
		
		
	    if (anno != null && anno.entity().length() > 0){
			def.entity = anno.entity(); 
		}
		else if (requiresExport){
			def.entity = clazz.getSimpleName();
		}
		
	    if (obj instanceof FreeFormSpreadsSheetI){
	        return def;
	    }
	    
	    
		Method method = null;
		
		//TODO: Use an attribute to determine this rather than first occurrence? Or maybe not.
        //Get first definition method
		while (true){
			for (Method m: clazz.getDeclaredMethods()) {
				m.setAccessible(true);
				SpreadSheetDef d = m.getAnnotation(SpreadSheetDef.class);
				if (d != null){
					method = m;
					break;
				}
			}
			if (method != null || clazz.getSuperclass() == null){
				break;
			}
			clazz = clazz.getSuperclass();
		}
		
		if (method == null){
			throw new Exception("ErrUnknown");
		}
		
		
		def.accessors = (List)method.invoke(obj);
		
		for (Object a : def.accessors){
			
			if (a instanceof java.lang.reflect.Field){
				java.lang.reflect.Field f = (java.lang.reflect.Field)a;
				f.setAccessible(true);
			}
			if (a instanceof Method){
				Method m = (Method)a;
				m.setAccessible(true);
			}
		}
		
		
		return def;
	}
	
	/**
	 * Output columns in the spreadsheet (as defined via the <code>getDefinition</code> method).
	 * 
	 * @param export definition
	 * @param sheet to output to
	 * @throws Exception 
	 */
	private void setColumns(Def def, SpreadSheet sheet) throws Exception{
		
		sheet.setCol(0);

		for (Object a : def.accessors){
			String key = "?";
			
			if (a instanceof java.lang.reflect.Field){
				java.lang.reflect.Field field= (java.lang.reflect.Field)a;
				Field f = field.getAnnotation(Field.class);
				key = f != null? f.label() : field.getName();
			}
			if (a instanceof Method){
				Method m = (Method)a;
				Field f = m.getAnnotation(Field.class);
				key = f != null? f.label() : m.getName();
			}
			sheet.addCell (sheet.getCol(), sheet.getRow(), key, getCellStyleId(sheet.getCol(), true)).setBold();
			sheet.incrementCol();
		}
	}
	
	
	
	/**
	 * Output an actual value to a sheet cell.
	 * 
	 * TODO: Use the UserParam object to format Locale, language, etc.
	 * 
	 * @param export definition
	 * @param object to export
	 * @param sheet to export to
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	private SpreadSheet setValues(Def def, Object obj, SpreadSheet sheet) throws Exception{
		
		sheet.setCol(0);

		for (Object a : def.accessors){
			Object value = null;
			
			if (a instanceof java.lang.reflect.Field){
				value = ((java.lang.reflect.Field)a).get(obj);
			}
			if (a instanceof Method){
				value = ((Method)a).invoke(obj);
			}
			
			
			if (value instanceof List){
				exportList((List)value, sheet);
			}
			else{
				sheet.addCell (sheet.getCol(), sheet.getRow(), value, getCellStyleId(sheet.getCol(), false));
				sheet.incrementCol();
			}
			
		}
		
		return sheet;
	}
	
	/**
	 * Generate a spreadsheet style id
	 * @param col
	 * @param header
	 * @return
	 */
	private int getCellStyleId(int col, boolean header){
	    return (col + 1) * (header?1:10000);
	}

   
	/**
	 * Export sheet to workbook
	 * @param spreadSheet
	 * @param workbook
	 * @throws Exception
	 */
    private void exportSpreadSheet(List<SpreadSheet> sheets, HSSFWorkbook wb) throws Exception{
		
        for (SpreadSheet ss: sheets){
        	HSSFSheet sheet = wb.createSheet(ss.getSheetname());
        	ss.setWorkBook(wb);
        	ss.createFreezePane(sheet);
        	
        	
        	
        	/*-***************************************************************
             * Default column widths
             ****************************************************************/
        	for (int column = 0; column <= ss.getLastColumn(); column++){
        	    if (ss.getColumnWidth(column) != -1){
                    sheet.setColumnWidth(column, ss.getColumnWidth(column));
                } 
        	}
        	
        	
    		/*-***************************************************************
    		 * Output row data
    		 ****************************************************************/
    		for (int row = 0; row <= ss.getLastRow(); row++){
    			for (int column = 0; column <= ss.getLastColumn(); column++){    
    				
    			    HSSFRow sheetRow = sheet.getRow(row);
    			    if (sheetRow == null){
    			        sheetRow = sheet.createRow(row);
    			    }
    			    
    			    HSSFCell cell = sheetRow.createCell(column);
    			    
                    SpreadsheetCell cellX =  ss.getCell(column, row);
    				if (cellX != null){
    
    					
    					if (cellX.getCellRangeAddress() != null){
    					    sheet.addMergedRegion(cellX.getCellRangeAddress());
    					}
    					
    					//Ex
    					HSSFCellStyle style = cellX.getCellStyle(wb);
    					cell.setCellStyle(style);
    					
    					boolean set = ss.getColumnWidth(column) == -1;
    					if (set && cellX.isHeader() && cellX.getWidth() != null){
    						sheet.setColumnWidth(column, cellX.getWidth());
    					}

    					cellX.setCellValue(cell, wb);
    				}
    				else{
    				    HSSFCellStyle style = ss.getCellStyleDefault(wb, row, column);
                        cell.setCellStyle(style);
    				}
    								
    			} 
    		}
        }
	}

   
   
    /**
     * Append incremental number to file name (if filename already exists)
     * @param filename
     * @param instances of filename
     * @return
     */
    protected String filename (String filename, int counter){
    	
    	String file = null;
    	String ext = null;
        
        int index = filename.indexOf(".");
        if (index != -1){
        	file = filename.substring(0, index);
        	ext = filename.substring(index);
        }
        else{
        	file = filename;
        	ext = ".xls";
        }
        
        String filename1 = file +  
                           (counter > 0? "-" + counter : "") +
                           ext; 
    	
        File f = new File(filename1);
        if (f.exists()){
        	return filename (filename, ++counter);
        }
        
    	return filename1;
    }
	
    
    /**
     * Internal class to hold export definitions
     * 
     */
    private class Def{
    	List <Object> accessors;
    	String entity = null;
    }
    
	
}
