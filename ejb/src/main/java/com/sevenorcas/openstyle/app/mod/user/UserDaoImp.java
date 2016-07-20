package com.sevenorcas.openstyle.app.mod.user;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.sevenorcas.openstyle.app.mod.login.LoginI;
import com.sevenorcas.openstyle.app.service.repo.BaseDao;
import com.sevenorcas.openstyle.app.service.sql.BaseSql;
import com.sevenorcas.openstyle.app.service.sql.ResultSetX;
import com.sevenorcas.openstyle.app.service.sql.StatementX;

/**
 * System User Repository 
 * 
 * [License] 
 * @author John Stewart
 */ 
@Stateless
public class UserDaoImp extends BaseDao implements UserDao{

	/**
	 * Default Constructor
	 */
	public UserDaoImp(){}
	
	@PersistenceContext(unitName="openstyleDS")
	private EntityManager em;
	
	
	/**
	 * Retrieve transaction code list
	 * @param BaseSql object
	 * @return User list
	 */
    public List<User> list (BaseSql search) throws Exception{
	    
		String uid = columnName(User.class, "userid");
		
		ResultSetX rs = StatementX
				.create("SELECT t." + uid + ", t.lang_code, t.admin, t.comp_nrs, t.locked, t.trys, t.groups, t.params, t.last_login, t.last_logout "
					  + "FROM " + T_USER + " t "
					  + "ORDER BY t." + uid)
				.addCompNr(search, "t")
				.appendBaseEntityFields("t")
				.executeQuery(search);
		
		List<User> list = new ArrayList<User>();
		
		while(rs.next()){
			User m = new User();
			list.add(m);
			
			int count = 1;
			
			m.setUserid(rs.getString(count++));
			m.setLanguageCode(rs.getString(count++));
			m.setAdmin(rs.getBoolean(count++));
			m.setCompanyNrs(rs.getString(count++));
			m.setLocked(rs.getBoolean(count++));
			m.setTrys(rs.getInt(count++));
			m.setGroups(rs.getString(count++));
			m.setParameters(rs.getString(count++));
			m.setLastLogin(rs.getTimestamp(count++));
			m.setLastLogout(rs.getTimestamp(count++));
			
			rs.setBaseEntityFields(m, "t");
		}
			
		String ur  = tableName(UserRole.class);
		String rid = columnName(UserRole.class, "roleId");
		uid = columnName(UserRole.class, "userId");
		
		rs = StatementX
				.create("SELECT ur." + uid + ", ur." + rid + " "
					  + "FROM " + ur + " ur ")
                .addCompNr(search, "ur")
                .appendBaseEntityFields("ur")
				.executeQuery(search);
		
		while(rs.next()){
			UserRole m = new UserRole();
			int count = 1;
			
			m.setUserId(rs.getLong(count++));
			m.setRoleId(rs.getLong(count++));
			rs.setBaseEntityFields(m, "ur");
			
			for (User rec: list){
				if (rec.getId().equals(m.getUserId())){
					rec.addUserRole(m);
				}
			}
		}
		

		
		
		return list;
	}
	
    /**
     * Return config string for passed in config type
     * @param UserParam object
     * @param Config type
     * @return
     */
    public String getUserConfig(UserParam params, int type)throws Exception{
        
        ResultSetX rs = StatementX
                .create("SELECT t.config "
                      + "FROM " + T_USER + " t "
                      + "WHERE t.id = " + params.getUser_id())
                .executeQuery(null);
        
        String config = null;
        while(rs.next()){
            config = rs.getString(1);
        }
        
        if (config == null || config.isEmpty()){
            return null;
        }
        
        return User.decodeConfig(config, type);
    }
    
    /**
     * Return config_import field as encode parameters
     * @param user id
     * @return
     */
    public String getUserConfigImport(Long userId)throws Exception{
        
        ResultSetX rs = StatementX
                .create("SELECT t.config_import "
                      + "FROM " + T_USER + " t "
                      + "WHERE t.id = " + userId)
                .executeQuery(null);
        
        String config = null;
        while(rs.next()){
            config = rs.getString(1);
        }
        return config;
    }
    
    /**
     * Update / Save the config string for passed in config type
     * @param UserParam object
     * @param Config encoded string
     * @param Config type
     * @return
     */
    public void updateUserConfig(UserParam params, String encode, int type) throws Exception{
        
        ResultSetX rs = StatementX
                .create("SELECT t.config "
                      + "FROM " + T_USER + " t "
                      + "WHERE t.id = " + params.getUser_id())
                .executeQuery(null);
        
        String config = null;
        while(rs.next()){
            config = rs.getString(1);
        }
        
        String c = User.encodeConfig(config, encode, type);
        
        StatementX.create("UPDATE " + T_USER + " SET config = '" + c + "' " 
              + "WHERE id = " + params.getUser_id())
        .executeUpdate();
        
    }
    
    /**
     * Update / Save the config string for passed in config type
     * @param UserParam object
     * @param Config encoded string
     * @param Config type
     * @return
     */
    public void updateUserConfigImport(UserParam params, String encode, int type) throws Exception{
        
        ResultSetX rs = StatementX
                .create("SELECT t.config_import "
                      + "FROM " + T_USER + " t "
                      + "WHERE t.id = " + params.getUser_id())
                .executeQuery(null);
        
        String config = null;
        while(rs.next()){
            config = rs.getString(1);
        }
        
        String c = User.encodeConfigImport(config, encode, type);
        
        StatementX.create("UPDATE " + T_USER + " SET config_import = '" + c + "' " 
              + "WHERE id = " + params.getUser_id())
        .executeUpdate();
        
    }
	
    /**
     * Clear the config_import field for all users (including service) for the passed in company number
     * @param company number
     * @throws Exception
     */
    public void deleteUserConfigImport(Integer comp_nr)throws Exception{
    	StatementX.create("UPDATE " + T_USER + " "
    			+ "SET config_import = NULL "
                + "WHERE comp_nr = " + comp_nr + " "
                + "OR id = " + LoginI.SERVICE_ID
                )
                .executeUpdate();
    }
    
    
    /**
     * Save new / update record
     * @param UserParam object
     * @param ReportRun entity
     * @return
     */
    public User save (UserParam params, User entity) throws Exception{
    	return super.save(params, entity, em);
    }
	
    
    /**
	 * Find an entity by its ID
	 * @param Long id
	 * @return
	 */
	public User findById (Long id) throws Exception{
		return super.findById(User.class, id, em);
	}
	
	
}
