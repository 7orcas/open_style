package com.sevenorcas.openstyle.main.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sevenorcas.openstyle.app.application.html.BaseHtml;
import com.sevenorcas.openstyle.app.mod.lang.Language;
import com.sevenorcas.openstyle.app.mod.user.UserParam;

/**
 * Main html page<p> 
 *  
 * Thanks to:<ol>
 *     <li>http://stackoverflow.com/questions/1341089/using-meta-tags-to-turn-off-caching-in-all-browsers</li>   
 * </ol><p>
 *    
 * [License]
 * @author John Stewart
 */
public class MainPageHtml extends BaseHtml {

	
	/**
	 * Constructor
	 * @param User parameters
	 * @param language object
	 */
	public MainPageHtml(UserParam params, Language lang) {
		super (params, lang);
		
		//Special case, main needs the head section 
		Document doc = Jsoup.parse("<head></head>");
		page = doc.select("head").first();
	}
	
	
	/**
     * Generate the main page html code.
     * @return html code
     */
    public String view(){
    	tagHead();
    	
    	Element body = tag("body");
    	body.attr("data-ng-controller", "mainCtrl");
    	
    	//set body element as main
    	Element html = page;
    	page = body; 
    	
    	busyIcon();
    	
    	Element main = div();
    	main.addClass("page_header")
    		.attr("id", "wrapper")
    		.attr("style", "display:none");
    	
    	header(main);
    	
    	div(body).text("TESTING");
    	
    	tagScripts(body);
    	
    	
    	//Need to reset main element
    	page = html;
        return output();
    }

    
    /**
     * Set the main header
     * @param Main page element
     */
    private void header(Element main){
    	Element nav = main.appendElement("nav")
   			.addClass("navbar navbar-default navbar-fixed-top")
   			.attr("style", "margin-bottom: 0;")
    	    .attr("role", "navigation");
    	
    	new MenuMainHtml(params, lang).view(nav);
    	
    	Element h = div(nav).addClass("navbar-header");
    	
    	//TODO Is this button useful?
    	//Visible when view is reduced
    	Element b = button(h, "button")
    			.addClass("navbar-toggle")
    			.attr("data-toggle", "collapse")
    			.attr("data-target", ".sidebar-collapse");
    	
    	span(b).addClass("sr-only")
    	       .text("Toggle navigation");
    	span(b).addClass("icon-bar");
    	span(b).addClass("icon-bar");
    	span(b).addClass("icon-bar");
    	
    	
    	//Main Page Title (up top)    
    	div(nav).addClass("app-title")
    	        .appendElement("span").attr("id", "appTitle");

    	new MenuUserHtml(params, lang).view(nav);
    }
    
    
    
    
    /**
     * Set the 'busy' (processing) icon
     */
    private void busyIcon(){
    	div().attr("id", "processing")
    	     .attr("style", "position:fixed;top:0;left:0;right:0;bottom:0;z-index:10000;background-color:gray;background-color:rgba(70,70,70,0.2);")
    	     .appendElement("div")
    	     .attr("style", "position:absolute;top:50%;left:50%;")
    	     .attr("src", "img/ajax-loader.gif")
    	     .attr("alt", "");
    }
    
    
	
	/**
	 * Html <code>head</code> tags
	 * <ul>
	 * 	   <li>tag + attribute names</li>
	 *     <li>list of attributes</li>
	 * </ul>
	 */
	static private String [][] HEAD_TAGS = {
			{"meta,http-equiv,content", "content-type",  "text/html; charset=utf-8"},
			{"meta,http-equiv,content", "Cache-Control", "no-store"},
			{"meta,http-equiv,content", "expires",       "0"},
			{"meta,http-equiv,content", "pragma",        "no-cache"},
			{"meta,http-equiv,content", "viewport",      "width=device-width, initial-scale=1.0"},
			
			{"link,type,href,rel",      "image/x-icon",  "img/favicon.ico", "shortcut icon"},
			
			
			//Bootstrap: front-end framework http://getbootstrap.com/
			//thanks to http://stackoverflow.com/questions/18205738/how-to-use-glyphicons-in-bootstrap-3-0 
			{"link,type,rel,href",      "text/css",  "stylesheet", "lib/bootstrap/bootstrap-3.1.1.min.css"},
			{"link,type,rel,href",      "text/css",  "stylesheet", "lib/bootstrap/bootstrap-glyphicons.3.0.0.css"},
			
			//Font Awesome: scalable vector icons   http://fortawesome.github.io/Font-Awesome/ 
			{"link,type,rel,href",      "text/css",  "stylesheet", "lib/3rdparty/font-awesome.min_4.6.3.css"},
			
			//JQuery css library  https://jqueryui.com/  
			{"link,type,rel,href",      "text/css",  "stylesheet", "lib/jquery/jquery-ui-1.10.2.custom.min.css"},
			
			//SB Admin, 3rd-party Bootstrap Admin Theme http://startbootstrap.com/sb-admin-v2
			{"link,type,rel,href",      "text/css",  "stylesheet", "lib/sb-admin/sb-admin-2.css"},
			
			
			{"link,type,rel,href",      "text/css",  "stylesheet", "app/css/colorpicker.css"},
			
			
			{"link,type,rel,href",      "text/css",  "stylesheet/less", "app/css/app.less"},    
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "app/css/dialog.less"}, 
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "app/css/menu.less"},   
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "app/css/page.less"},   
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "app/css/table.less"},  
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "app/mod/langadmin/view/page.less"}, 
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "app/mod/user/view/page.less"},      
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "app/mod/useradmin/view/page.less"}, 
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "app/mod/company/view/page.less"},   
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "app/mod/patches/view/page.less"},   
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "template/page.less"},  
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "template/dialog/generic_dialog.less"}, 
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "mod/calendar/view/page.less"}, 
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "mod/mdata/view/page.less"},    
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "mod/prep/view/page.less"},     
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "mod/simu/view/page.less"},     
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "mod/period/view/page.less"},   
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "mod/import/view/page.less"},   
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "mod/export/view/page.less"},   
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "mod/rpt/view/page.less"},      
	        {"link,type,rel,href",      "text/css",  "stylesheet/less", "mod/fix/view/page.less"},      
	};
	
    
    /**
     * Generate the head tag.
     * @return tag
     */
    private void tagHead(){
        
    	Element head = tag("head");
    	
    	tag(head, "title").attr("ng-bind", "appTitle");

    	for (int i=0;i<HEAD_TAGS.length;i++){
    		String [] tag = HEAD_TAGS[i];

    		String x  = tag[0];
    		String[]a = x.split(",");

    		Element el    = tag(head, a[0]);
    		
    		int c = 1;
    		
    		for (int j=1;j<a.length;j++){
    			el.attr(a[j], tag[c++]);
    		}
    	}
    }
    

	/**
	 * Javascript tags
	 * <ul>
	 * 	   <li>script type</li>
	 *     <li>src attribute</li>
	 * </ul>
	 */
	static private String [][] SCRIPTS = {
			{"dev",     "lib/less/less-1.5.0.min.js"},
			
			//Standard JQuery library
			{"app",     "lib/jquery/jquery-2.1.0.js"},
			
			//Used by uploader directive
			{"app",     "lib/jquery/jquery-ui-1.10.0.custom.min.js"},
			{"app",     "lib/jquery/jquery.fileupload.js"},
			
			 //Standard Angular library 
	        {"app",     "lib/angular/angular-1.2.16.js"},
	        
	        //Cookies used in session timeout tests
	        //Thanks to https://github.com/ivpusic/angular-cookie 
	        {"app",     "lib/angular/angular-cookie_201014.js"},
	        
	        //3rd-party module to manage routing (more powerful than standard Angular angular-route library 
	        //https://github.com/angular-ui/ui-router/wiki 
	        {"app",     "lib/angular/angular-ui-router-0.2.10.js"},
	        
	        //factory which creates a resource object to interact with RESTful server-side data sources 
	        //http://docs.angularjs.org/api/ngResource/service/$resource 
	        {"app",     "lib/angular/angular-resource-1.2.16.js"},
	        
	        //Bootstrap components written in AngularJS: 
	        //http://angular-ui.github.io/bootstrap/ 
	        //Do not mimimize. It is customerized for date picker 
	        {"app",     "lib/angular/ui-bootstrap-tpls-0.10.0.CHANGED.js"},
	        
	        //Bootstrap components  
	        //http://getbootstrap.com/ 
	        {"app",     "lib/bootstrap/bootstrap-3.1.1.js"},    
	        
	        //Drag 'n drop  
	        //http://www.directiv.es/Angular-DragDrop
	        //http://ganarajpr.github.io/angular-dragdrop/ 
	        {"app",     "lib/angular/draganddrop-131110.js"},
	        
	        //SB Admin, 3rd-party Bootstrap Admin Theme
	        //http://startbootstrap.com/sb-admin-v2  
	        {"app",     "lib/sb-admin/sb-admin-2.js"},
	        {"app",     "lib/sb-admin/jquery.metisMenu.js"},
	        
	        //Right Click Context menu
	        //https://github.com/ianwalter/ng-context-menu/  
	        {"app",     "lib/angular/ng-context-menu.CHANGED.js"},
	        
	    
	        //Utility javascript library (various sources) 
	        {"app",     "lib/3rdparty/javascript_utils.js"},
	        
	        //3rd-party module ecrypt passwords using MD5 hash 
	        //http://www.myersdaily.org/joseph/javascript/md5-text.html 
	        {"app",     "lib/3rdparty/md5.js"},
	       
	        //3rd-party module to remove watches once the text is rendered
	        //https://github.com/Pasvaz/bindonce
	        //http://angular-tips.com/blog/2013/08/removing-the-unneeded-watches/ 
	        {"app",     "lib/3rdparty/bindonce_0.3.1.js"},
	        
	        //Application Utility scripts 
	        {"app",     "app/common/javascript_utils.js"},
	        {"app",     "app/common/color_picker.js"},
	        
	        
	        //Application Specific scripts 
	        {"app",     "app.js"},
	        {"app",     "state.js"},
	        {"app",     "controller.js"},
			
	        //Application Modules
	        {"mod",     "app/login/service.js"},
	        {"mod",     "app/common/global.js"},   
	        {"mod",     "app/common/cache.js"},
	        {"mod",     "app/common/model.js"},
	        {"mod",     "app/common/controller.js"},
	        {"mod",     "app/common/remote.js"},
	        {"mod",     "app/common/service.js"},
	        {"mod",     "app/common/userobject.js"},
	        {"mod",     "app/common/dialog.js"},
	        {"mod",     "app/common/directive.js"},
	        {"mod",     "app/common/directive_menu.js"},
	        {"mod",     "app/common/directive_table.js"},
	    
            //User Modules
	        {"mod",     "app/lang/model.js"},
	        {"mod",     "app/lang/service.js"},
	        {"mod",     "app/mod/user/controller.js"},
	        {"mod",     "app/mod/user/model.js"},
	        {"mod",     "app/mod/user/service.js"}, 
	        {"mod",     "mod/mdata/controller.js"},
	        {"mod",     "mod/mdata/model.js"},
	        {"mod",     "mod/mdata/service.js"},
	        {"mod",     "mod/calendar/controller.js"},
	        {"mod",     "mod/calendar/model.js"},
	        {"mod",     "mod/calendar/service.js"},
	        {"mod",     "mod/import/controller.js"},
	        {"mod",     "mod/import/model.js"},
	        {"mod",     "mod/import/service.js"},
	        {"mod",     "mod/rpt/controller.js"},
	        {"mod",     "mod/rpt/model.js"},
	        {"mod",     "mod/rpt/service.js"},
	        {"mod",     "mod/prep/controller.js"},
	        {"mod",     "mod/prep/model.js"},
	        {"mod",     "mod/prep/service.js"},
	        {"mod",     "mod/simu/controller.js"},
	        {"mod",     "mod/simu/model.js"},
	        {"mod",     "mod/simu/service.js"},
	        {"mod",     "mod/period/controller.js"},
	        {"mod",     "mod/period/model.js"},
	        {"mod",     "mod/period/service.js"},
	        {"mod",     "mod/export/controller.js"},
	        {"mod",     "mod/export/model.js"},
	        {"mod",     "mod/export/service.js"},
	        {"mod",     "mod/fix/controller.js"},
	        {"mod",     "mod/fix/model.js"},
	        {"mod",     "mod/fix/service.js"},
	        
            //service only modules
	        {"service", "app/common/service_menu.js"},
	        {"service", "app/mod/langadmin/controller.js"},
	        {"service", "app/mod/langadmin/model.js"},
	        {"service", "app/mod/langadmin/service.js"},
	        {"service", "app/mod/useradmin/controller.js"},
	        {"service", "app/mod/useradmin/model.js"},
	        {"service", "app/mod/useradmin/service.js"},
	        {"service", "app/mod/company/controller.js"},
	        {"service", "app/mod/company/model.js"},
	        {"service", "app/mod/company/service.js"},
	        {"service", "app/mod/company/directive.js"},
	        {"service", "app/mod/patches/controller.js"},
	        {"service", "app/mod/patches/model.js"},
	        {"service", "app/mod/patches/service.js"}, 
	};
	
    
    /**
     * Generate the script tags.
     * @return tag
     */
    private void tagScripts(Element body){
        
    	for (int i=0;i<SCRIPTS.length;i++){
    		String [] tag = SCRIPTS[i];

    		String type = tag[0];
    		
    		if (type.equals("service") && !params.isService()){
    			continue;
    		}

    		Element el = tag(body, "script");
    		el.attr("src", tag[1]);
    	}
    }

    
}
