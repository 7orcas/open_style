package com.sevenorcas.openstyle.mod.main;

import com.sevenorcas.openstyle.app.application.Utilities;
import com.sevenorcas.openstyle.app.application.html.BaseCallback;
import com.sevenorcas.openstyle.app.application.html.UtilityHtml;
import com.sevenorcas.openstyle.app.mod.company.Company;
import com.sevenorcas.openstyle.app.mod.company.CompanyService;
import com.sevenorcas.openstyle.app.mod.user.UserParam;

/**
 * Main html page call back<p> 
 *  
 * Thanks to:<ol>
 *     <li>http://stackoverflow.com/questions/1341089/using-meta-tags-to-turn-off-caching-in-all-browsers</li>   
 * </ol><p>
 *    
 * [License]
 * @author John Stewart
 */
public class MainMeuCallback extends BaseCallback {

   
	static private CompanyService  companyService;
	
	static private String [] META_TAG = {
			"content-type",  "text/html; charset=utf-8",
			"Cache-Control", "no-store",
			"expires",       "0",
			"pragma",        "no-cache",
			"viewport",      "width=device-width, initial-scale=1.0",
	};
	
    

	
	
	private Company company;
	private UtilityHtml util;
	
	/**
	 * Default constructor
	 */
	public MainMeuCallback (UserParam params) throws Exception{
		companyService  = Utilities.lookupService(companyService, "CompanyServiceImp");
		company = companyService.findByNr(params, params.getCompany());
		
		util = new UtilityHtml();
	}
	
	
	
	/**
	 * Filter the passed in html line according to the company configurations
	 * @param String line
	 * @return configured line
	 */
	public String process(String line){
		
	    if (line.indexOf("<!-- Html:generate -->") != -1){
	    	return generate();
	    }
		
		return line;
	}
	
	/**
     * Generate the main page html code.
     * @return html code
     */
    private String generate(){
        
    	util.createHead();
    	
    	for (int i=0;i<META_TAG.length;i+=2){
    		util.createTag("meta").attr("http-equiv", META_TAG[i]).attr("content", META_TAG[i+1]);
    	}
    	util.createTag("title").attr("ng-bind", "appTitle");
    	
    	
    	
        return util.html();
    }
	
}


