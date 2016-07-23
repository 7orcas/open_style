package com.sevenorcas.openstyle.main.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sevenorcas.openstyle.app.application.html.BaseHtml;
import com.sevenorcas.openstyle.app.mod.lang.Language;
import com.sevenorcas.openstyle.app.mod.user.UserParam;

/**
 * Main Menu for Main html page<p> 
 *    
 * [License]
 * @author John Stewart
 */
public class MenuMainHtml extends BaseHtml {

	/**
	 * Constructor
	 * @param User parameters
	 * @param language object
	 */
	public MenuMainHtml(UserParam params, Language lang) {
		super (params, lang);
		
		//Special case, main needs the head section 
		Document doc = Jsoup.parse("<head></head>");
		page = doc.select("head").first();
	}
	
	/**
     * Set the Main Drop Down Menu
     * @param Header element
     */
    public void view(Element nav){
    	
    	headerShowHideMenu(nav);
    	
    }
	
	
	/**
     * Set the main header
     * @param Header element
     */
    private void headerShowHideMenu(Element nav){
    	Element show = div(nav)
    		.addClass("side-menu navbar-default")
	        .attr("id", "menu-sel-show")
			.attr("style", "display:none")
		    .attr("data-ng-click", "showSideMenu()");
    		    
    	show.text(NBSP + label("Menu") + NBSP);
    	tagI(show, "fa fa-caret-down");
    	
    	Element hide = div(nav)
    		.addClass("side-menu navbar-default")
	        .attr("id", "menu-sel-hide")
			.attr("style", "display:none")
		    .attr("data-ng-click", "hideSideMenu(true)");
    		    
    	hide.text(NBSP + label("Menu") + NBSP);
    	tagI(hide, "fa fa-caret-up");
    }
    

	
}
