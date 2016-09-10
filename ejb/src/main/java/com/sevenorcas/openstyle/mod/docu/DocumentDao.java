package com.sevenorcas.openstyle.mod.docu;

import java.util.List;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.repo.EntityRepoI;
import com.sevenorcas.openstyle.mod.docu.ent.DocumentEnt;

/**
 * Local Document Type Repository interface  
 * 
 * [License] 
 * @author John Stewart
 */
@Local
public interface DocumentDao extends EntityRepoI{
	public List<DocumentEnt> list (UserParam params, DocumentCnt search) throws Exception;
	public DocumentEnt findById (Long id) throws Exception;
	
}
