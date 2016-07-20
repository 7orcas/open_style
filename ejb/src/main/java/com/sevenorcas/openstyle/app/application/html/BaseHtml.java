package com.sevenorcas.openstyle.app.application.html;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.application.ApplicationParameters;
import com.sevenorcas.openstyle.app.application.exception.AppException;
import com.sevenorcas.openstyle.app.mod.lang.Language;
import com.sevenorcas.openstyle.app.service.dto.FieldDefDto;


/**
 * Base Class to provide HTML generators generic functions.<p>
 * 
 * [License] 
 * @author John Stewart
 */

//WF10 TODO Refactor css and methods
@Deprecated
public class BaseHtml implements ApplicationI {

    protected List<String> cssDefs;
    
    static final public String CLASS_TABLE_COL_HEAD     = "div-table-col-head";
    static final public String CLASS_TABLE_COL_ROW      = "table-col-row";
    
	static final public String NBSP                 = "\u00a0";
	static protected ApplicationParameters appParam = ApplicationParameters.getInstance(); 
	
	static public String returnNoRecordsFound (boolean removeLoading, Language l){
		Element body = createSection();
		if (removeLoading){
            removeLoading(body);
        }
		Element page = body.appendElement("div");
		page.appendElement("div")
		    .attr("style", "font-size:16px;margin-top:30px;margin-left:20px;text-align:left;")
		    .text(l.getLabel("NoRecordsFound"));
		
        return output(body.html());
	}
	
	static public String returnReportNotRun (boolean removeLoading, Language l){
        Element body = createSection();
        if (removeLoading){
            removeLoading(body);
        }
        Element page = body.appendElement("div");
        page.appendElement("div")
            .attr("style", "font-size:16px;margin-top:30px;margin-left:20px;text-align:left;")
            .text(l.getLabel("RptNRun"));
        return output(body.html());
    }
	
	
	/**
	 * Create an empty <code>jsoup.nodes.Document</code> and return the <code>body</code> element;
	 * @return jsoup.nodes.Element
	 */
	static public Element createDocument(){
		Document doc = Jsoup.parse("<head><body></body></head>");
		return doc.select("body").first();
	}
	
	/**
	 * Create an empty <code>jsoup.nodes.Document</code> and return the <code>section</code> element;
	 * @return jsoup.nodes.Element
	 */
	static public Element createSection(){
		Element e = createDocument();
		e.appendElement("section");
		return e;
	}
	
	
	/**
	 * Clean and compress the <code>jsoup.nodes.Document</code> for output to the client
	 * @param jsoup.nodes.Document
	 * @return
	 */
	static public String output(String s){
//		doc.outputSettings().prettyPrint(false);
//    	doc.outputSettings().indentAmount(0);
//    	String s = doc.body().html();
    	s = s.replace("class=\" ", "class=\""); //Some reason jsoup adds a space?
		return s;
	}
	
	
	
	

	/**
	 * Return Html code for standard date picker
	 * @param page element to attach to
	 * @param element 'is-open' id
	 * @return
	 */
	static public Element datepicker(Element body, String id){
			
		Element input = body.appendElement("input").addClass("form-control div-table-datepicker");
		input.attr("type", "text");
		input.attr("datepicker-popup", "{{datepickerFormat}}");
		input.attr("show-button-bar", "{{datepickerShowButtonBar}}");
		input.attr("datepicker-options", "{{datepickerDateOptions}}");
		input.attr("is-open", "opened" + id);
		input.attr("init-date", "new Date()");
		
		return input;
	
	}

	/**
	 * Return Html code for generic date picker (javascript is in common/conrtoller.js)
	 * @param page element to attach to
	 * @param element class 
	 * @param element id
	 * @return
	 */
	static public Element genericDatepicker(Element body, String clazz, String id){
			
		Element input = datepicker(body, "gdp");
        input.addClass(clazz)
	        .attr("id", id)
	        .attr("ng-model", "datepicker")
	        .attr("ng-change", "changeGenDatePicker('" + id + "')")
	        .attr("style","border: medium none;color: white;height: 0;");
		
		return input;
	}

	
	/**
	 * Style attribute width 
	 * @param width
	 * @return
	 */
	static public String getStyleWidth (int width){
		return "max-width:" + width + "px;"
				+ "min-width:" + width + "px;";
	}
	
	
	/**
	 * Return the dto field name
	 * @param fieldName
	 * @return
	 */
	static public String dtoField (String fieldName, ArrayList<FieldDefDto> fieldDefs) throws Exception{
		for (FieldDefDto f: fieldDefs){
			if (f.getAccessor().equals(fieldName)){
				return f.getDto();
			}
		}
		throw AppException.create("Invalid DTO field definition").logThisException();
	}
	
	/**
	 * Return the dto language lable key
	 * @param fieldName
	 * @return
	 */
	static public String dtoLabel (String fieldName, ArrayList<FieldDefDto> fieldDefs) throws Exception{
		for (FieldDefDto f: fieldDefs){
			if (f.getAccessor().equals(fieldName)){
				return f.getLabel() != null? f.getLabel() : f.getAccessor();
			}
		}
		throw AppException.create("Invalid DTO field definition").logThisException();
	}
	
	static public void removeLoading(Element body){
	    body.appendElement("script")
        //.attr("type", "text/javascript-lazy")
        .text("var el = document.getElementById('processing');"
            + "if (el !== null){"
                + "el.style.visibility = 'hidden';"
            + "}");
    
	}
	
	/**
	 * Return a formatted code + description string
	 * @param code
	 * @param descr
	 * @return
	 */
	static public String getCodeAndDescr(String code, String descr){
        String r = code != null? code : "";
        r += descr != null && !descr.isEmpty()? "-" + descr : "";
        return r;
    }
	
	/**
     * Return a formatted description if not null, then code 
     * @param code
     * @param descr
     * @return
     */
    static public String getDescrThenCode(String code, String descr){
        if (descr != null && !descr.isEmpty()){
            return descr;  
        }
        return code;
    }
	
    /**
     * Create page navigation widget.
     * 
     * @param Element body
     * @param int count
     * @param int limit
     * @param int offset
     * @param int maximum page numbers to display
     */
    public void pageNavigation(Element page, int count, int limit, int offset, int maxPageNumbers){
        
        if (limit >= count){
            return;
        }
        
        int pages = count / limit;
        pages += (count % limit > 0? 1 : 0); 

        int current = offset / limit;
        boolean first = offset == 0;
        boolean last  = limit + offset >= count;
        
        
        Element a = page.appendElement("span").addClass("nav-pagenumber")
                        .appendElement("i").addClass("fa fa-fast-backward");
        
        if (!first){
            a.attr("ng-click","page.select(0)");
        }
        else{
            a.attr("disabled","disabled");
            a.attr("style","cursor:inherit;");
        }
        
        
        page.appendText(NBSP);
        
        a = page.appendElement("span").addClass("nav-pagenumber")
                .appendElement("i").addClass("fa fa-step-backward");

        if (!first){
            a.attr("ng-click","page.previous()");
        }
        else{
            a.attr("disabled","disabled");
            a.attr("style","cursor:inherit;");
        }
        
        
        page.appendText(NBSP);
        
        int half = maxPageNumbers / 2;
        int start = current - half;
        start = start >= 0 ? start : 0;
        int end = current + half;
        end = (end - start) >= maxPageNumbers? end : maxPageNumbers;
        end = end <= pages ? end : pages;
        
        if ((end - start) < maxPageNumbers && start > 0){
            start -= (maxPageNumbers - (end - start));
            start = start >= 0 ? start : 0;
        }
        
        
        if (start > 0){
            page.appendElement("span").addClass("nav-pagecounter nav-pagenumber")
                .attr("style","text-decoration:none;cursor:inherit;")
                .text(" ... ");
        }
        
        for (int i=start; i<end; i++){
            String txt = (i < 9? NBSP : "") + (i+1);
            
            Element n = page.appendElement("span").addClass("nav-pagecounter nav-pagenumber");
            n.attr("id","page_number_" + i);
            n.text(txt);
            
            if (i != current){
                n.attr("ng-click","page.select("+ i + ")");
            }
            else{
                n.attr("style","text-decoration:none;cursor:inherit;");
            }
            
            page.appendText(NBSP);
        }
        
        if (end < pages){
            page.appendElement("span").addClass("nav-pagecounter nav-pagenumber")
                .attr("style","text-decoration:none;cursor:inherit;")
                .text(" ... ");
        }
        
        
        a = page.appendElement("span").addClass("nav-pagenumber")
                .appendElement("i").addClass("fa fa-step-forward");
        
        if (!last){
            a.attr("ng-click","page.next()");
        }
        else{
            a.attr("disabled","disabled");
            a.attr("style","cursor:inherit;");
        }
        
        page.appendText(NBSP);

        a = page.appendElement("span").addClass("nav-pagenumber")
                .appendElement("i").addClass("fa fa-fast-forward");
         
        if (!last){
            a.attr("ng-click","page.select(" + (pages - 1) + ")");
        }
        else{
            a.attr("disabled","disabled");
            a.attr("style","cursor:inherit;");
        }
        
    }

    /**
     * Clean and compress the <code>jsoup.nodes.Document</code> for output to the client
     * @param jsoup.nodes.Document
     * @return
     */
    public String outputX(Element body){
        
        if (cssDefs != null && cssDefs.size() > 0){
            StringBuffer sb = new StringBuffer();
            for (String s : cssDefs){
                sb.append(s + " ");
            }
            body.appendElement("style")
                .attr("type", "text/css")
                .text(sb.toString());
        }
        
        return output(body.html());
    }

    /**
     * Create scroller element (to lock the header at top of page).
     * 
     * @param Element page
     * @param String extention to class
     * @return scroller element
     */
    protected Element getScroller(Element page, String x){
        page.appendElement("div").attr("id","scroller-anchor-header" + (x!= null? x : ""));
        Element scroll = page.appendElement("div").attr("id", "scroller-header" + (x!= null? x : "")).attr("style", "z-index: 500;");
        Element scrollX = scroll.appendElement("div").addClass("div-table-toolbar-simu-sp");
        return scrollX;
    }
    
    
}
