package com.sevenorcas.openstyle.app.application;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.user.UserParam;


/**
 * Local interface to Application Service
 *
 * [License]
 * @author John Stewart
 */
@Local
public interface ApplicationService {
	public void reloadAppProp(UserParam userParam) throws Exception;
	public void closeSqlConnection(UserParam userParam) throws Exception;
	public void resetDatabase(UserParam userParam) throws Exception;
	public boolean toggleDebug(UserParam params) throws Exception;
}
