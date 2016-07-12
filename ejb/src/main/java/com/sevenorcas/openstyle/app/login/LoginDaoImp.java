package com.sevenorcas.openstyle.app.login;

import java.util.Hashtable;

import javax.ejb.Stateless;

import com.sevenorcas.openstyle.app.repo.BaseDao;
import com.sevenorcas.openstyle.app.sql.ResultSetX;
import com.sevenorcas.openstyle.app.sql.StatementX;
import com.sevenorcas.openstyle.app.user.Role;
import com.sevenorcas.openstyle.app.user.User;
import com.sevenorcas.openstyle.app.user.UserRole;

/**
 * Login Repository 
 * 
 * [License] 
 * @author John Stewart
 */ 
@Stateless
public class LoginDaoImp extends BaseDao implements LoginDao{

	/**
	 * Default Constructor
	 */
	public LoginDaoImp(){}
	
	
	
	/**
	 * Find a userid record
	 * @param String user id
	 * @return Login object (or <code>null</code> if not round
	 */
	public Login findByUserId (String userid) throws Exception{
	    
		Long id = null;
		
		String table = tableName(User.class);
		String col = columnName(User.class, "userid");

		ResultSetX rs = StatementX
				.create("SELECT t.id FROM " + table + " t WHERE t." + col + "='" + userid + "'")
				.executeQuery(null);
		
		
		while(rs.next()){
			id = rs.getLong(1);
		}
				
		
		if (id == null){
			return null;
		}
		
		Login login = em.find(Login.class, id);
		
		return login;
	}
	
	
	/**
	 * Update a userid's <code>last_login</code> field
	 * @param String user id
	 */
	public void updateLastLogin (String userid) throws Exception{
		String table = tableName(User.class);
		String col = columnName(User.class, "userid");

		StatementX.create("UPDATE " + table + " "
				+ "SET last_login = CURRENT_TIMESTAMP, last_logout = NULL "
				+ "WHERE " + col + " = '" + userid + "';")
			.executeUpdate();
	}
	
	/**
     * Update a userid's <code>last_logout</code> field
     * @param String user id
     */
    public void updateLastLogout (String userid) throws Exception{
        String table = tableName(User.class);
        String col = columnName(User.class, "userid");

        StatementX.create("UPDATE " + table + " "
                + "SET last_logout = CURRENT_TIMESTAMP "
                + "WHERE " + col + " = '" + userid + "' "
                + "AND last_logout IS NULL;")
            .executeUpdate();
    }
	
	
	/**
	 * Update a userid's <code>password</code> field
	 * @param String user id
	 * @param String new password
	 */
	public void updatePassword (String userid, String password_new) throws Exception{
		String table = tableName(User.class);
		String user = columnName(User.class, "userid");
		String ps   = columnName(User.class, "password");

		StatementX.create("UPDATE " + table + " "
				+ "SET " + ps + " = '" + password_new + "' "
				+ "WHERE " + user + " = '" + userid + "';")
			.executeUpdate();
	}
	
	
	/**
	 * Get userid's permissions
	 * @param String user id
	 * @return Hashtable of key/value pairs 
	 */
	public Hashtable<String, String> permissionsByUserId (String userid) throws Exception{
		
		String userTable = tableName(User.class);
		String user = columnName(User.class, "userid");
		
		String roleTable = tableName(Role.class);
		
		String userRoleTable = tableName(UserRole.class);
		String user_id = columnName(UserRole.class, "userId");
		String role_id = columnName(UserRole.class, "roleId");
		

		ResultSetX rs = StatementX
				.create("SELECT r.role_name, r.role_value, u.id, r.id " 
		                + "FROM " + userTable + " u, "
		                          + roleTable + " r, "
		                          + userRoleTable + " j "
						+ "WHERE u." + user + "='" + userid + "' "
						+ "AND j." + user_id + "= u.id " 
						+ "AND j." + role_id + "= r.id")
				.executeQuery(null);
		
		Hashtable<String, String> permissions = new Hashtable<>();
		
		while(rs.next()){
			String key = rs.getString(1);
			String value = rs.getString(2);
			
			if (!permissions.containsKey(key)){
				permissions.put(key, value);
			}
			//Test if better permission
			else{
				String valueX = permissions.get(key);
				for (int i=0; i<value.length(); i++){
					String p = value.substring(i, i);
					if (valueX.indexOf(p) == -1){
						valueX = valueX + p;
					}
				}
			}
		}
				
		
		if (permissions.size() == 0){
			return null;
		}
		
		return permissions;
	}
	
	
	
	
}
