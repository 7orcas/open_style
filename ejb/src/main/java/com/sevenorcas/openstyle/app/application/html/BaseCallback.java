package com.sevenorcas.openstyle.app.application.html;

import com.sevenorcas.openstyle.app.application.ApplicationI;

/**
 * Base Class to provide HTML generic callback functions.<p>
 * 
 * Note the implementing class is called from <code>WebPageServlet</code>.
 * 
 * @see WebPageServlet
 * 
 * [License] 
 * @author John Stewart
 */

public abstract class BaseCallback extends BaseHtml implements HtmlCallBackI, ApplicationI {

    /*
     * Defined javascript calls (template/app/sidebar_actions.html)
     */
    final static private String RECORD_NEW   = "recordNew()";
    final static private String RECORD_EDIT  = "recordEdit()";
    final static private String RECORD_SAVE  = "recordSave()";
    final static private String RECORD_UNDO  = "recordUndo()";
    
    final static protected String SPACE_HEAD = "<div class=\"so41-spc so0-h\">" + NBSP + "</div>";
    final static protected String SPACE      = "<div class=\"so41-spc so0-h\">" + NBSP + "</div>";
    
	
	protected String incrementIndex(String line, int index){
		return line.replace("index=\"0\"", "index=\"" + index + "\"");
	}
	
	protected String stripComments(String line){
		line = line.replace("<!-- ", "<");
		line = line.replace(" -->", ">");
		line = line.replace("<!--", "<");
		line = line.replace("-->", ">");
		return line.trim();
	}
	
	protected String stripInlineCommentAtEnd(String line){
	    int i = line.indexOf("<!--");
	    if (i != -1){
	        return line.substring(0, i);
	    }
        return line;
    }
	
	protected String stripClass(String line){
	    int i1 = line.indexOf("class=\"");
        int i2 = i1 != -1? line.indexOf("\"", i1+8) : -1;
        String clazz = "";
        if (i1 != -1 && i2 != -1){
            clazz = line.substring(i1+7, i2);
        }
        return clazz;
    }
	
	protected String outputLineHeader(String clazz, String value){
	    return "<div class=\"" + CLASS_TABLE_COL_HEAD + " " + clazz + "\">"
                + value 
                + "</div>";
	}
	
	protected String outputLineRow(String clazz, String value){
        return "<div class=\"" + CLASS_TABLE_COL_ROW  + " " + clazz + "\">"
                + value 
                + "</div>";
    }
	
	/**
	 * Implementing class can override <b>this</b> method to control
	 * actions in the <code>WebPageServlet</code> class.
	 */
	public boolean isPermission(String ngClick){
	    return true;
	}
	
	/**
     * Filter javascript function calls to recordNew, recordEdit, recordSave and recordUndo
     * @return line
     */
    public String filterSidebarUpdateFunctions(String line){
        return filterSidebarFunctions(line, true, true, true, true);
    }
	
	
	/**
     * Filter javascript function calls
     * @param flag true = filter recordNew
     * @param flag true = filter recordEdit
     * @param flag true = filter recordSave
     * @param flag true = filter recordUndo
     * @return line
     */
    public String filterSidebarFunctions(String line, boolean isNew, boolean isEdit, boolean isSave, boolean isUndo){
        
        if (isNew && line.indexOf(RECORD_NEW) != -1){
            return "";
        }
        if (isEdit && line.indexOf(RECORD_EDIT) != -1){
            return "";
        }
        if (isSave && line.indexOf(RECORD_SAVE) != -1){
            return "";
        }
        if (isUndo && line.indexOf(RECORD_UNDO) != -1){
            return "";
        }
        
        
        return line;
    }

	
}

