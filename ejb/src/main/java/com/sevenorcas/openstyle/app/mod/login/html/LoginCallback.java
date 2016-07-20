package com.sevenorcas.openstyle.app.mod.login.html;

import java.util.List;

import com.sevenorcas.openstyle.app.application.Utilities;
import com.sevenorcas.openstyle.app.application.html.BaseCallback;
import com.sevenorcas.openstyle.app.application.html.WebPageServlet;
import com.sevenorcas.openstyle.app.mod.company.Company;
import com.sevenorcas.openstyle.app.mod.company.CompanyService;
import com.sevenorcas.openstyle.app.mod.company.CompanySql;
import com.sevenorcas.openstyle.app.mod.user.UserParam;

/**
 * Class to insert language icons and company selections in <code>index_login_page.html</code>.<p>
 * 
 * This class name is embedded within <code>index_login_page.html</code> and called from <code>WebPageServlet</code>.
 * 
 * @see WebPageServlet
 * 
 * [License] 
 * @author John Stewart
 */

public class LoginCallback extends BaseCallback {

	final static private String LANGUAGE_ICONS_INSERT    = "LangIcons:insert"; 
	final static private String COMPANY_LIST_INSERT      = "CompanyList:insert";

	final static private int INDEX_LANG_CODE  = 0;
    final static private int INDEX_LANG_FILE  = 1;
    final static private int INDEX_LANG_COUNT = 2;
    
    final static private String[] LANGUAGE_ICONS = {
        "en", "img/united_kingdom_flag.png",
        "de", "img/germany_flag.png",
        "it", "img/italy_flag.png",
    };
    
    static private CompanyService        companyService;
    private boolean isService = false;
	private List<Company> list;
    
	/**
	 * Default constructor
	 */
	public LoginCallback (UserParam params) throws Exception{
		isService = params.isService();
		
		if (isService){
			companyService = Utilities.lookupService(companyService, "CompanyServiceImp");
			list = companyService.list(params, new CompanySql(params).setActiveOnly().setOrderByName());
		}
		
	}
	
	
	
	/**
	 * Filter the passed in html line according to the company configurations
	 * @param String line
	 * @return configured line
	 */
	public String process(String line){
		
		if (line.indexOf(LANGUAGE_ICONS_INSERT) != -1){
			return getLanguageIcons();
		}
		
		
		if (isService && line.indexOf(COMPANY_LIST_INSERT) != -1){
			StringBuffer sb = new StringBuffer();
			sb.append("<select "
					+ "class=\"companies\" "
					+ "ng-model=\"lcomp\">");
			
			for (Company c : list){
				if (c.isActive()){
					sb.append("<option value=\"" + c.getCompanyNr() + "\">"+ c.getCode() + "</option>");
				}
			}
			sb.append("</select>");
			return sb.toString();
		}
		
		
		return line;
	}
	
	/**
     * Sort (order) language icons via application properties file
     * @param htmlFile
     */
    private String getLanguageIcons(){
        
        String s1 = appParam.getLanguageCodes();
        String s2[] = s1.split(",");
        
        if (s2.length < 2){
            return "";
        }
        
        StringBuffer sb = new StringBuffer();
        
        for (String l: s2){
            sb.append((sb.length()>0?NBSP:"")
                    + "<a class=\"lang_buttons\" ng-click=\"changeLanguage('" + l + "')\">"
                        + "<img src=\"" + getLanguageFile(l) + "\">"
                    + "</a>");
        }
        
        return sb.toString();
    }
    
    /**
     * Find language icon for passed in language code
     * @param code
     * @return
     */
    private String getLanguageFile (String code){
        for (int i=0;i<LANGUAGE_ICONS.length; i+=INDEX_LANG_COUNT){
            if (code.toLowerCase().equals(LANGUAGE_ICONS[i+INDEX_LANG_CODE])){
                return LANGUAGE_ICONS[i+INDEX_LANG_FILE];
            }
        }
        return null;
    }
    	
	
}

