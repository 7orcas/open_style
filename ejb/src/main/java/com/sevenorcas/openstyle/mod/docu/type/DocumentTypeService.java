package com.sevenorcas.openstyle.mod.docu.type;

import java.util.List;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.mod.user.UserParam;


/**
 * Local interface to Document Type Service
 *
 * [License]
 * @author John Stewart
 */

@Local
public interface DocumentTypeService {
	public List<DocumentTypeEnt> list (UserParam userParam, DocumentTypeSql sql) throws Exception;
	
}
