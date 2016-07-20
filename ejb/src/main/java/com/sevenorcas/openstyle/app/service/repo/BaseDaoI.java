package com.sevenorcas.openstyle.app.service.repo;

import javax.ejb.Local;

/**
 * Local Base Repository interface  
 * 
 * [License] 
 * @author John Stewart
 */
@Local
public interface BaseDaoI {
	public long nextTempId () throws Exception;
}
