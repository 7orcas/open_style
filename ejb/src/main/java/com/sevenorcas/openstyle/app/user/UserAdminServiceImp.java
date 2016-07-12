package com.sevenorcas.openstyle.app.user;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.http.HttpServletRequest;

import com.sevenorcas.openstyle.app.AppException;
import com.sevenorcas.openstyle.app.Utilities;
import com.sevenorcas.openstyle.app.login.LoginI;
import com.sevenorcas.openstyle.app.login.LoginService;
import com.sevenorcas.openstyle.app.perm.Permission;
import com.sevenorcas.openstyle.app.sql.BaseSql;


/**
 * User Admin service.<p>
 * 
 *  
 * [License] 
 * @author John Stewart
 */
@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class UserAdminServiceImp implements UserAdminService {

	@EJB private LoginService loginService;
	@EJB private UserDao      userDao;
	
	public UserAdminServiceImp() {
	}
	
	/**
	 * Retrieve list of users as DTO's.
	 * @param UserParam object
	 * @param BaseSql search object
	 */
	public List<UserDto> listDto (UserParam params, BaseSql search) throws Exception {
		List<User> list = list(params, search);
		
		List<UserDto> listX = new ArrayList<>();  
		for (User entity: list){
		    if (entity.getId().equals(LoginI.SERVICE_ID)){
		        continue;
		    }
		    
		    UserDto u = new UserDto(entity);
		    u.setPassword(User.DUMMY_PASSWORD);
			listX.add(u);
		}
		
		return listX;
	}
	
	/**
	 * Retrieve list of users.
	 * @param UserParam object
	 * @param BaseSql search object
	 */
	@Permission(service=true)
	public List<User> list (UserParam params, BaseSql search) throws Exception {
		return userDao.list(search == null? new BaseSql(params) : search);
	}	
	
	/**
	 * Save user records.
	 * @param UserParam object
	 * @param List of User objects to save
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<User> save (UserParam params, List<User> list) throws Exception {
		List<User> listX = new ArrayList<User>();
		for (User e: list){
			listX.add(save(params, e));
		}
		return listX;
	}
	
	
	/**
	 * Save new user record.
	 * @param UserParam object
	 * @param LangKey object
	 */
	@Permission(service=true)
	public User save (UserParam params, User record) throws Exception {
		if (record.isPasswordUpdate()){
			record.setPassword(Utilities.md5(record.getPassword()));
    	}
		return userDao.save(params, record);
	}

    /**
	 * Find a User entity by its ID
	 * @param Long id
	 * @return
	 */
	public User findById (UserParam params, Long id) throws Exception{
		return userDao.findById(id);
	}


	/**
	 * Force a logout (ie invalidate session) for the passed in user id.
	 * @param UserParam object
	 * @param String userid to logout
	 * @param HttpServletRequest object
	 * @throws Exception if no valid login's found
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void forceLogout(UserParam params, String userid, HttpServletRequest req)  throws Exception{
		
		List<UserParam> logins = (List)req.getSession().getServletContext().getAttribute(UserParam.LOGIN_SET);
		if (logins == null){
			AppException a = new AppException("No Valid Logins");
			a.logThisException();
			a.emailThisException();
			throw a;
		}
		
		for (UserParam p: logins){
			if (p.getUserId().equals(userid)){
				loginService.logout(p, req);
			}
		}
			
	}
	

	/**
	 * Return the <code>Export</code> configuration string for the passed in user
	 * @param params
	 * @return
	 */
	public String getUserConfigExport(UserParam params) throws Exception{
	    return userDao.getUserConfig(params, User.CONFIG_TYPE_EXPORT);
	}
	
	
	/**
     * Save the <code>Export</code> configuration string for the passed in user
     * @param params
     * @param encoded <code>Export</code> configuration
     * @return
     */
    public void updateUserConfigExport(UserParam params, String encode) throws Exception{
        userDao.updateUserConfig(params, encode, User.CONFIG_TYPE_EXPORT);
    }
	
    
    /**
     * Return the <code>Fix List</code> configuration string for the passed in user
     * @param params
     * @return
     */
    public String getUserConfigFixList(UserParam params) throws Exception{
        return userDao.getUserConfig(params, User.CONFIG_TYPE_FIX_LIST);
    }
    
    
    /**
     * Save the <code>Fix List</code> configuration string for the passed in user
     * @param params
     * @param encoded <code>Export</code> configuration
     * @return
     */
    public void updateUserConfigFixList(UserParam params, String encode) throws Exception{
        userDao.updateUserConfig(params, encode, User.CONFIG_TYPE_FIX_LIST);
    }

    /**
     * Save the passed in configuration string for the passed in user to the config_import field.<p>
     * This field is deleted during an import.
     * @param params
     * @param encoded <code>Export</code> configuration
     * @param type
     * @return
     */
    public void updateUserConfigImport(UserParam params, String encode, int type) throws Exception{
        userDao.updateUserConfigImport(params, encode, type);
    }
    
    /**
     * Return the no repeat codes for the passed in user id
     * @param userID
     * @return
     */
    public String getNoRepeatCodes (Long userId) throws Exception{
    	String config = userDao.getUserConfigImport(userId);
    	Hashtable<String, String> params = Utilities.fromParameterEncode(config);
    	String codes = "";
    	if (params.containsKey("" + User.CONFIG_IMPORT_NOREPEAT_MOULDS)){
    		codes += params.get("" + User.CONFIG_IMPORT_NOREPEAT_MOULDS);
    	}
    	return codes;
    }
    
    /**
     * Clear the config_import field for all users (including service) for the company number in the passed in user parameters 
     * @param params
     * @throws Exception
     */
    public void deleteUserConfigImport(UserParam params)throws Exception{
        userDao.deleteUserConfigImport(params.getCompany());
    }
    
    
}
