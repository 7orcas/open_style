package com.sevenorcas.openstyle.main.html;

import java.util.List;

import com.sevenorcas.openstyle.app.application.Utilities;
import com.sevenorcas.openstyle.app.application.html.BaseCallback;
import com.sevenorcas.openstyle.app.mod.lang.Language;
import com.sevenorcas.openstyle.app.mod.lang.LanguageService;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.main.MainMenu;
import com.sevenorcas.openstyle.main.MainMenuService;

/**
 * Main html page call back<p> 
 *    
 * [License]
 * @author John Stewart
 */
public class MainPageCallback extends BaseCallback {
   
	static private LanguageService  languageService;
	static private MainMenuService  mainMenuService;
	
	private MainPageHtml page;
	
	/**
	 * Default constructor
	 */
	public MainPageCallback (UserParam params) throws Exception{
		languageService = Utilities.lookupService(languageService, "LanguageServiceImp");
		Language lang   = languageService.getLanguage(params.getLanguageCode()); 

		mainMenuService = Utilities.lookupService(mainMenuService, "MainMenuServiceImp");
		List <MainMenu> l = mainMenuService.list(params, null);
		
		page = new MainPageHtml(params, lang, l);
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


