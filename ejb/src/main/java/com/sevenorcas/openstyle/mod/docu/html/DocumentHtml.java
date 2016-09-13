package com.sevenorcas.openstyle.mod.docu.html;

import org.jsoup.nodes.Element;

import com.sevenorcas.openstyle.app.application.html.BaseHtml;
import com.sevenorcas.openstyle.app.mod.lang.Language;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.mod.docu.DocumentCtl;
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
	 * @param Controller
	 * @param Main menu list
	 */
	public DocumentHtml(UserParam params, Language lang, DocumentCtl ctl, DocumentEnt ent){
		super (params, lang, ctl);
		initialise();
		this.ent = ent;
	}
	
	
	/**
     * Generate the main page html code.
     * @return html code
     */
    public String view(){
    	
    	toolBar(page);
    	
    	div(page).text(ent.getText());
    	
    	for (SectionEnt s : ent.getSections()){
    		Element el = div(page);
    		
    		if (ctl.isEditMode()){
    			el.appendElement("input")
				  .attr("type", "text")
				  .attr("ng-model", "dto." + s.getId());
    		}
    		else{
    			el.text(s.getText());
    		}
    		
    	}
    	
        return output();
    }

    
    public Element toolBar(Element el){
    	return tagA(el, "")
    	.text("zEdit")
    	.attr("data-ng-click", "_edit()")
    	;
    	
    }
    
}
