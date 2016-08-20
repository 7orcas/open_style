package com.sevenorcas.openstyle.mod.docu;

import java.util.List;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.mod.user.UserParam;


/**
 * Local interface to Document Service
 *
 * [License]
 * @author John Stewart
 */

@Local
public interface DocumentService {
	public List<DocumentEnt> list (UserParam userParam, DocumentSql sql) throws Exception;
	
}
