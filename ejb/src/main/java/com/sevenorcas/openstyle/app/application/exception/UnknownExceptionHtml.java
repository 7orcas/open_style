package com.sevenorcas.openstyle.app.application.exception;

import org.jsoup.nodes.Element;

import com.sevenorcas.openstyle.app.application.html.BaseHtml;
import com.sevenorcas.openstyle.app.lang.Language;

/**
 * Unknown Exception HTML page (for return error's from a call to a view).<p>
 * 
 * This page will call client javascript functions to action the exception.<p>
 * 
 * [License]
 * @author John Stewart
 */
public class UnknownExceptionHtml extends BaseHtml {
    
	/**
     * Constructor
     */
    public UnknownExceptionHtml (){
    }
    
    
    /**
     * Return partial html view.
     * 
     * @param Exception the caused error
     * @return HTML as String
     */
    public String getView(Exception ex, Language lang) throws Exception {
        
        //Create and return page
        Element body = createDocument();
        removeLoading(body);
        
        String message = ex.getMessage();
        boolean known = false;
        if (ex instanceof AppException){
            known = true;
        }
        
        Element page = body.appendElement("div").addClass("err-page")
                           .appendElement("div").addClass("err-page-inner");
        
        String title  = "ErrUnknown";
        String admin  = "SeeAdmin";
        
        if (lang != null){
            title   = lang.getLabel(title);
            admin   = lang.getLabel(admin);
            message = lang.getLabel(message);
        }
        else{
            title = "An unknown error has occurred";
            admin = "Please see your system administrator";
        }
        
        
        if (!known){
            page.appendElement("img")
                .addClass("oops-image")
                .attr("src", "img/oops.jpg");
            
            page.appendElement("div")
                .addClass("err-unknown")
                .text(title);

            page.appendElement("div")
                .addClass("see-admin")
                .text(admin);
        }
        
        if (message != null){
        	page.appendElement("div")
        	    .addClass("err-detail")
        	    .text(message);
        }
        
        
        return output(body.html());
    }
        
    
    
}
