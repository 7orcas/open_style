package com.sevenorcas.openstyle.app.user;

import java.util.List;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.repo.EntityRepoI;
import com.sevenorcas.openstyle.app.sql.BaseSql;

/**
 * Local System User Repository interface  
 * 
 * [License] 
 * @author John Stewart
 */
@Local
public interface UserDao extends EntityRepoI{
	public List<User> list (BaseSql search) throws Exception;
	public User findById (Long id) throws Exception;
	public User save (UserParam params, User entity) throws Exception;
	
	public String getUserConfig(UserParam params, int type)throws Exception;
	public String getUserConfigImport(Long userId)throws Exception;
	
	public void updateUserConfig(UserParam params, String encode, int type)throws Exception;
	public void updateUserConfigImport(UserParam params, String encode, int type) throws Exception;
	public void deleteUserConfigImport(Integer comp_nr)throws Exception;
}
