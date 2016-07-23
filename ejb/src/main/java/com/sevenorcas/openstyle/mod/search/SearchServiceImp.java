package com.sevenorcas.openstyle.mod.search;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.sevenorcas.openstyle.app.mod.lang.LanguageService;


/**
 * Document Service<p>
 *  
 * [License] 
 * @author John Stewart
 */
@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class SearchServiceImp implements SearchService {
	
	@EJB private LanguageService   languageService;
	
	public SearchServiceImp() {
	}
	
	    
}
