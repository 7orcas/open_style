package com.sevenorcas.openstyle.main.html;

import com.sevenorcas.openstyle.app.application.Utilities;
import com.sevenorcas.openstyle.app.application.html.BaseCallback;
import com.sevenorcas.openstyle.app.mod.lang.Language;
import com.sevenorcas.openstyle.app.mod.lang.LanguageService;
import com.sevenorcas.openstyle.app.mod.user.UserParam;

/**
 * Main html page call back<p> 
 *    
 * [License]
 * @author John Stewart
 */
public class MainPageCallback extends BaseCallback {
   
	static private LanguageService  languageService;
	
	private MainPageHtml page;
	
	/**
	 * Default constructor
	 */
	public MainPageCallback (UserParam params) throws Exception{
		languageService = Utilities.lookupService(languageService, "LanguageServiceImp");
		Language lang   = languageService.getLanguage(params.getLanguageCode()); 

		page = new MainPageHtml(params, lang);
	}
	
	
	
	/**
	 * Filter the passed in html line according to the company configurations
	 * @param String line
	 * @return configured line
	 */
	public String process(String line){
		
	    if (line.indexOf("<!-- Html:generate -->") != -1){
	    	return page.view();
	    }
		
		return line;
	}
	
	
}


