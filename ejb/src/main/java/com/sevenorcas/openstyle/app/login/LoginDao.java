package com.sevenorcas.openstyle.app.login;

import java.util.Hashtable;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.repo.EntityRepoI;

/**
 * Local Login Repository interface  
 * 
 * [License] 
 * @author John Stewart
 */
@Local
public interface LoginDao extends EntityRepoI{
	public Login findByUserId (String userid) throws Exception;
	public Hashtable<String, String> permissionsByUserId (String userid) throws Exception;
	public void updateLastLogin (String userid) throws Exception;
	public void updateLastLogout (String userid) throws Exception;
	public void updatePassword (String userid, String password_new) throws Exception;
}
