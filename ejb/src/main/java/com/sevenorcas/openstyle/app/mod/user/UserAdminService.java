package com.sevenorcas.openstyle.app.mod.user;

import java.util.List;

import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;

import com.sevenorcas.openstyle.app.service.sql.BaseSql;

/**
 * Local interface to User Admin Service
 *
 * [License]
 * @author John Stewart
 */
@Local
public interface UserAdminService {
	public List<User> list (UserParam params, BaseSql search) throws Exception;
	public List<UserDto> listDto (UserParam params, BaseSql search) throws Exception;
	public User save (UserParam params, User record) throws Exception;
	public List<User> save (UserParam params, List<User> list) throws Exception;
	public User findById (UserParam params, Long id) throws Exception;
	public void forceLogout(UserParam userParam, String userid, HttpServletRequest req)  throws Exception;
	
	public String getUserConfigExport(UserParam params) throws Exception;
	public void updateUserConfigExport(UserParam params, String encode) throws Exception;
	
	public String getUserConfigFixList(UserParam params) throws Exception;
	public void updateUserConfigFixList(UserParam params, String encode) throws Exception;
	
	public void updateUserConfigImport(UserParam params, String encode, int type) throws Exception;
	public void deleteUserConfigImport(UserParam params)throws Exception;

}
