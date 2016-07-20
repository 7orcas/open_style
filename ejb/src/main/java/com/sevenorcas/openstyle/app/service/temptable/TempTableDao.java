package com.sevenorcas.openstyle.app.service.temptable;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.repo.EntityRepoI;

/**
 * Local Company Repository interface  
 * 
 * [License] 
 * @author John Stewart
 */
@Local
public interface TempTableDao extends EntityRepoI{
	public String getNameNoRegister (UserParam params, String suffix) throws Exception;
	public String getNameNoRegister (Integer companyNr, String userId, Long id, String suffix) throws Exception;
	public boolean exists(String table) throws Exception;
	public void drop (String table) throws Exception;
	public void dropAllTempTablesByCompNr (UserParam params) throws Exception;
}
