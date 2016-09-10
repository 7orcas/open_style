package com.sevenorcas.openstyle.mod.docu.html;

import com.sevenorcas.openstyle.app.application.html.BaseHtml;
import com.sevenorcas.openstyle.app.mod.lang.Language;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.mod.docu.ent.DocumentEnt;
import com.sevenorcas.openstyle.mod.docu.ent.SectionEnt;

/**
 * Document html page<p> 
 *  
 * [License]
 * @author John Stewart
 */
public class DocumentHtml extends BaseHtml {

	private DocumentEnt ent;
	
	/**
	 * Constructor
	 * @param User parameters
	 * @param language object
	 * @param Main menu list
	 */
	public DocumentHtml(UserParam params, Language lang, DocumentEnt ent){
		super (params, lang);
		initialise();
		this.ent = ent;
	}
	
	
	/**
     * Generate the main page html code.
     * @return html code
     */
    public String view(){
    	
    	
    	div(page).text(ent.getText());
    	
    	for (SectionEnt s : ent.getSections()){
    		div(page).text(s.getText());
    	}
    	
        return output();
    }

        
}
