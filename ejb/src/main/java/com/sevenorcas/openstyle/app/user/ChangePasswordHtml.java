package com.sevenorcas.openstyle.app.user;

import com.sevenorcas.openstyle.app.Utilities;
import com.sevenorcas.openstyle.app.lang.Language;
import com.sevenorcas.openstyle.app.lang.LanguageService;


/**
 * Change password HTML view definition.<p>
 *  
 * [License]
 * @author John Stewart
 */

public class ChangePasswordHtml {

	static private LanguageService langService;
	
	/**
	 * Return Change Password Requirements partial html view.
	 * 
	 * @param String language code
	 * @return HTML as String
	 */
	static public String getView(String lang) throws Exception {
		
		langService = Utilities.lookupService(langService, "LanguageServiceImp");
		Language l = langService != null? langService.getLanguage(lang) : null;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("<div class=\"changepassword-con\">");
		
		String label = l.getLabel("PassReqLen");
		int index = label.indexOf("%%");
		if (index != -1){
			label = label.substring(0, index) 
					+ "<b>" + User.PASSWORD_MIN_LENGTH + "</b>"  
					+ label.substring(index + 2);
		}
		sb.append("<div class=\"changepassword-len\">" + label + "</div>&nbsp;");
		
		label = l.getLabel("PassReqInc");
		index = label.indexOf("%%");
		if (index != -1){
			label = label.substring(0, index) 
					+ "<div class = \"changepassword-inh\">" + User.PASSWORD_TO_INCLUDE + "</div>"  
					+ label.substring(index + 2);
		}
		sb.append("<div class=\"changepassword-in\">" + label + "</div>");
		
		sb.append("</div>");

       		
		return sb.toString();
		
	}

	
	
}
