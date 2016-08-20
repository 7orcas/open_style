package com.sevenorcas.openstyle.mod.docu.type;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.sevenorcas.openstyle.app.mod.user.UserParam;


/**
 * Document Type Service
 *  
 * [License] 
 * @author John Stewart
 */
@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class DocumentTypeServiceImp implements DocumentTypeService {
	
	@EJB private DocumentTypeDao dao;
	
	public DocumentTypeServiceImp() {
	}
	
	
	/**
	 * Retrieve list of entities.
	 * @param UserParam object
	 * @param MainMenu Sql object
	 */
	public List<DocumentTypeEnt> list (UserParam params, DocumentTypeSql sql) throws Exception {
		return dao.list(params, sql == null? new DocumentTypeSql(params) : sql);
	}
	    
}
