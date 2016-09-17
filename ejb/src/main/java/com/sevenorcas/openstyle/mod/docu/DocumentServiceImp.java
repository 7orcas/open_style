package com.sevenorcas.openstyle.mod.docu;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.sevenorcas.openstyle.app.mod.lang.LanguageService;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.mod.docu.ent.DocumentEnt;
import com.sevenorcas.openstyle.mod.docu.html.DocumentHtml;


/**
 * Document Service
 *  
 * [License] 
 * @author John Stewart
 */
@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class DocumentServiceImp implements DocumentService {
	
	@EJB private LanguageService    langService;
	@EJB private DocumentDao        dao;
	
	public DocumentServiceImp() {
	}
	
	
	/**
	 * Retrieve list of entities.
	 * @param UserParam object
	 * @param MainMenu Sql object
	 */
	public List<DocumentEnt> list (UserParam params, DocumentCtl sql) throws Exception {
		return dao.list(params, sql == null? new DocumentCtl(params) : sql);
	}
	
	
	/**
	 * Retrieve entity by it's primary id.
	 * @param UserParam object
	 * @param Long entity id
	 */
	public DocumentEnt findById (UserParam params, Long id) throws Exception {
		return dao.findById(id);
	}
	
	/**
	 * Retrieve the html object for the passed in control object.
	 * @param UserParam object
	 * @param Control object
	 */
	public DocumentHtml html (UserParam params, DocumentCtl ctl) throws Exception {
		
		DocumentEnt ent = dao.findById(ctl.getDocId());
		DocumentHtml html = new DocumentHtml(params, langService.getLanguage(params.getLanguageCode()), ctl, ent);
		
		return html;
	}
	
	
}
