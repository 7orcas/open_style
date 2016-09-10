package com.sevenorcas.openstyle.mod.docu;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.sevenorcas.openstyle.app.mod.user.UserParam;


/**
 * Document Service
 *  
 * [License] 
 * @author John Stewart
 */
@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class DocumentServiceImp implements DocumentService {
	
	@EJB private DocumentDao dao;
	
	public DocumentServiceImp() {
	}
	
	
	/**
	 * Retrieve list of entities.
	 * @param UserParam object
	 * @param MainMenu Sql object
	 */
	public List<DocumentEnt> list (UserParam params, DocumentCnt sql) throws Exception {
		return dao.list(params, sql == null? new DocumentCnt(params) : sql);
	}
	    
}
