package com.sevenorcas.openstyle.mod.docu.type;

import java.util.List;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.repo.EntityRepoI;

/**
 * Local Document Type Repository interface  
 * 
 * [License] 
 * @author John Stewart
 */
@Local
public interface DocumentTypeDao extends EntityRepoI{
	public List<DocumentTypeEnt> list (UserParam params, DocumentTypeSql search) throws Exception;
	
}
