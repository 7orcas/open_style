package com.sevenorcas.openstyle.main.html;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sevenorcas.openstyle.app.application.html.BaseHtml;
import com.sevenorcas.openstyle.app.mod.lang.Language;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.sql.BaseCnt;
import com.sevenorcas.openstyle.main.MainMenuEnt;

/**
 * Main Menu for Main html page<p> 
 *    
 * [License]
 * @author John Stewart
 */
public class MenuMainHtml extends BaseHtml {

	private List <MainMenuEnt> list;
	
	/**
	 * Constructor
	 * @param User parameters
	 * @param language object
	 */
	public MenuMainHtml(UserParam params, Language lang, List <MainMenuEnt> list) {
		super (params, lang, new BaseCnt(params));
		this.list = list;
		
		//Special case, main needs the head section 
		Document doc = Jsoup.parse("<head></head>");
		page = doc.select("head").first();
	}
	
	/**
     * Set the Main Drop Down Menu
     * @param Header element
     */
    public void view(Element el){
    	
    	headerShowHideMenu(el);
    	
    	Element menu1 = div(el)
			.addClass("navbar-default navbar-static-side")
	        .attr("id", "sideMenu")
			.attr("role", "navigation");
    	
    	Element menu2 = div(menu1)
    		.addClass("sidebar-collapse");
    	
    	Element menu3 = tagUl(menu2)
    		.addClass("nav")
    		.attr("id", "side-menu");
    	
    	
    	for (MainMenuEnt m: list){
    		tagA(tagLi(menu3),"")
    		    .attr("data-ng-click", "showDoc(" + m.getId() + ")")
    		    .text(m.getLangCode());	
    	}
    	
    	
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
    		    
//    	show.text(NBSP + label("Menu") + NBSP);
    	show.text(label("Menu") + NBSP);
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
