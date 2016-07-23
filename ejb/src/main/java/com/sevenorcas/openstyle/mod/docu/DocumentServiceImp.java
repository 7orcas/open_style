package com.sevenorcas.openstyle.mod.docu;

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
public class DocumentServiceImp implements DocumentService {
	
	@EJB private LanguageService   languageService;
	
	public DocumentServiceImp() {
	}
	
	    
}
