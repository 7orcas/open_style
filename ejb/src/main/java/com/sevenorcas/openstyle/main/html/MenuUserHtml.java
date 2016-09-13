package com.sevenorcas.openstyle.main.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sevenorcas.openstyle.app.application.html.BaseHtml;
import com.sevenorcas.openstyle.app.mod.lang.Language;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.sql.BaseCnt;

/**
 * User Menu for Main html page<p> 
 *  
 * [License]
 * @author John Stewart
 */
public class MenuUserHtml extends BaseHtml {

    /**
	 * User drop down menu functions
	 * <ul>
	 * 	   <li>permission (+ divider) (+ child element)</li>
	 *     <li>child element (if applicable)</li>
	 *     <li>language key</li>
	 *     <li>icon class</li>
	 *     <li>function</li>
	 *     <li>id (optional)</li>
	 * </ul>
	 */
    static private String [][] USER_MENU = {
			{"user",       "PassChange",   "fa fa-key",         "changePassword"},
			
			{"service,divider"},
			{"service",    "UserAdmin",    "fa fa-users",       "userAdmin"},
			{"service",    "LangAdmin",    "fa fa-language",    "langAdmin"},
			{"service",    "LangAdminR",   "fa fa-spinner",     "langReload"},
			{"service",    "CompanyAdmin", "fa fa-cogs",        "companyAdmin"},
			{"service",    "FixR",         "fa fa-exclamation-triangle",  "clearFixes",   "clearFixes_status"},
			{"service",    "FixRLS",       "fa fa-exclamation-triangle",  "reapplyLSFix", "reapplyLSFix_status"},
			
			{"service,divider"},
			{"service",    "TimersC",      "fa fa-times",       "cancelTimers"},
			{"service",    "TimersS",      "fa fa-clock-o",     "startTimers"},
			{"service",    "AppPropR",     "fa fa-refresh",     "reloadAppProp"},
			
			{"service,divider"},
			{"service",    "Patches",      "fa fa-database",    "patches"},
			{"service",    "Debug",        "fa fa-bug",         "toggleDebug"},
			{"service,ce", "debug_status", "fa fa-check",       "position:absolute;margin-top:-20px;"},
			
			{"user,divider"},
			{"user",       "Logout",       "fa fa-sign-out fa-fw", "logout"},
	};
    
    /**
	 * Constructor
	 * @param User parameters
	 * @param language object
	 */
	public MenuUserHtml(UserParam params, Language lang) {
		super (params, lang, new BaseCnt(params));
		
		//Special case, main needs the head section 
		Document doc = Jsoup.parse("<head></head>");
		page = doc.select("head").first();
	}
	
    
    /**
     * Set the User Drop Down Menu
     * @param Header element
     */
    public void view(Element nav){
    	Element ul = tagUl(nav).addClass("nav navbar-top-links navbar-right");
    	Element li = tagLi(ul).addClass("dropdown");
    	
    	Element a = tagA(li, "#").addClass("dropdown-toggle").attr("data-toggle", "dropdown");
    	tagI(a, "fa fa-user fa-fw");
    	tagI(a, "fa fa-caret-down");
    	
    	ul = tagUl(li).addClass("dropdown-menu dropdown-user");
    	
    	Element item = null;
    	
    	for (int i=0;i<USER_MENU.length;i++){
    		String [] tag = USER_MENU[i];

    		String x  = tag[0];
    		String[]y = x.split(",");
    		
    		boolean service = y[0].equals("service");
    		if (service && !params.isService()){
    			continue;
    		}
    		
    		String type = y.length>1?y[1]:"";
    		
    		if (type.equals("divider")){
    			tagLi(ul).addClass("divider");
    			continue;
    		}
    		
    		//Child element of last item
    		if (type.equals("ce")){
    			String id    = tag[1];
        		String clazz = tag[2];
        		String style = tag[3];
    			
        		tagI(item, clazz)
        		  .addClass(clazz)
        		  .attr("id", id)
        		  .attr("style", style);
        		
    			continue;
    		}
    		
    		//<li><a class="user-menu-service" href="" data-ng-click="userAdmin()"><i class="fa fa-users"></i>&nbsp;{{label('UserAdmin')}}</a>
    		
    		String label = tag[1];
    		String clazz = tag[2];
    		String fn    = tag[3];
    		String id    = tag.length>4?tag[4]:null;
    		
    		item = tagLi(ul);
    		Element z = tagA(item, "")
    			   .addClass(service?"user-menu-service":"user-menu")
    		       .attr("data-ng-click", fn + "()");
    		
    		//z.text(NBSP + label(label));
    		
    		if (id != null){
    			z.attr("id", id);
    		}
    		
    		tagI(z, clazz);
    		span(z).text(NBSP + label(label));
    		
    	}
    	
    	
    }

	
}
