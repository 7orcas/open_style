package com.sevenorcas.openstyle.app.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.sevenorcas.openstyle.app.lang.LangKey;
import com.sevenorcas.openstyle.app.log.AuditLog;
import com.sevenorcas.openstyle.app.login.BaseLogin;
import com.sevenorcas.openstyle.app.perm.Permission;



/**
 * The <code>BaseUserParam</code> class contains essential user parameters (eg company number, userid, permissions) and methods. This class
 * is <u>critical to application security</u>.<p>  
 * 
 * The attributes within <b>this</b> object are assigned after the user has successfully logged into the application.<br>
 * The parameters are stored against the users session and 'injected' into the uri query parameters for each request.<br>
 * The receiving REST method can then include <code>@QueryParam("userparam")</code> in its call signature to 
 * obtain <b>this</b> object.<p> 
 * 
 * Note: Session binding is used to track logged in users.<p>
 * 
 * This class must be extended by a concrete application class. Such a class is free to add relevant attribute and methods. 
 *  
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
abstract public class BaseUserParam implements Serializable, HttpSessionBindingListener {
    
    final static public String SERVICE_PERMISSION = "service";
    
	/**
	 * Special case: this value applies to all companies
	 */
	final static public int IGNORE_COMPANY_NUMBER = 0;
	
	/** 
	 * Company number.<br>
	 * Used to uniquely identify the user organization.<p>
	 * 
	 * Note: company number is a fundamental field in the database, most tables are company number based. <b>This</b> field is often included in unique database table keys.
	 */
	private Integer company;
	
	/** 
	 * Language code.<br>
	 * <b>This</b> field contains the currently displayed language for the client (either selected in a client page or default value assigned via login).<p>
	 * 
	 * Note: on the server side the Language key is used to format client responses and pdf / spreadsheet exports.
	 */
	private String languageCode;
	
	/** 
	 * Unique User ID.<br>
	 * Used in combination with the password and company number to login to the application.<p>
	 * 
	 * Note: the userid is used as a basis for logging client actions.
	 */
	private String userid;
	
	/** 
	 * Unique User ID (i.e. primary key to user table).<br>
	 */
	private Long user_id;
	
	/** 
	 * Administrator User flag.<br>
	 * If true, then <b>this</b> user has admin rights.
	 */
	private Boolean admin;

	/**
	 * Logged in date time stamp
	 */
	private long loginDateTime;
	
	/** 
	 * Service flag.<br>
	 * If true, then <b>this</b> user has service rights.<p>
	 * Service rights can do anything, eg delete entities with id's below <code>ApplicationI.ENTITY_PERMANENT_ID</code>.
	 */
	private Boolean service;
	
	/** 
	 * JBoss session ID.<br>
	 * Used to track currently logged in users (in combination with client heartbeat).<br>
	 * TODO: Implement logged in user admin screen 
	 */
	private String sessionId;
	
	/** 
	 * JBoss session.<br>
	 */
	private HttpSession httpSession;
	
	/**
	 * Permission key value pairs.<ul>
	 *     <li>key = permission key are defined on the entity</li>
	 *     <li>value = CRUD (create, read, update and delete) permission as defined on a role</li>
	 * </ul><p>
	 * 
	 * Note: The highest possible CRUD value is stored for key.
	 */
	private Hashtable<String, String> permissions;
	
	
	/**
	 * REST <code>@QueryParam</code> for <b>this</b> object. 
	 */
	static final public String QUERY_PARAM = "userparams";
	
	/**
	 * JBoss <code>ServletContext</code> attribute name. ie used to store list of logged in users.
	 */
	static final public String LOGIN_SET   = "logins";
	
	
	/** JSON company      parameter name   */  static protected String JSON_COMPANY     = "c";
	/** JSON userid       parameter name   */  static protected String JSON_USERID      = "u";
	/** JSON user_id      parameter name   */  static protected String JSON_USER_ID     = "i";
	/** JSON lang key     parameter name   */  static protected String JSON_LANG        = "l";
	/** JSON admin flag   parameter name   */  static protected String JSON_ADMIN       = "a";
	/** JSON service flag parameter name   */  static protected String JSON_SERVICE     = "s";
	/** JSON admin flag   parameter name   */  static protected String JSON_PERMISSIONS = "p";
	/** JSON login ts     parameter name   */  static protected String JSON_TIMESTAMP   = "t";	
	
	
    ////////////////////// Helper Methods //////////////////////////////////
	
	/**
	 * Constructor via JSON string.<br>
	 * TODO: use standard json utilities
	 * @param String JSON string representing <code>BaseUserParam</code> object
	 */
	public BaseUserParam(String json) {
		json = json.trim();
		json = json.startsWith("{")? json.substring(1) : json;
		json = json.endsWith("}")? json.substring(0,json.length()-1) : json;
		
		String [] split1 = json.split(",");
		for (String s: split1){
			int index = s.indexOf(":");
			String [] split2 = {s.substring(0, index), s.substring(index+1)};
			
			String name = split2[0].substring(1, split2[0].length() - 1);
			String value = split2[1].startsWith("\"")? split2[1].substring(1, split2[1].length() - 1) : split2[1];
			
			
			if (name.equals(JSON_COMPANY)){
				setCompany(Integer.parseInt(value));
			}
			if (name.equals(JSON_USERID)){
				setUserId(value);
			}
			if (name.equals(JSON_USER_ID)){
				setUser_id(Long.parseLong(value));
			}
			if (name.equals(JSON_LANG)){
				setLanguageCode(value);
			}
			if (name.equals(JSON_ADMIN)){
				setAdmin(Boolean.parseBoolean(value.toString()));
			}
			if (name.equals(JSON_SERVICE)){
				setService(Boolean.parseBoolean(value.toString()));
			}
			if (name.equals(JSON_PERMISSIONS)){
				getJsonPermissions(value.toString());
			}
			if (name.equals(JSON_TIMESTAMP)){
				setLoginDateTime(Long.parseLong(value.toString()));
			}
		}
	}
	/**
     * Permissions to JSON string.<br>
     * TODO: use standard json utilities
     * @return JSON string representing permissions object
	 */
	private void getJsonPermissions(String json) {
		if (json == null || json.length() == 0){
			return;
		}
		permissions = new Hashtable<>();
		String [] split1 = json.split("\\|");
		for (String s: split1){
			int index = s.indexOf("=");
			String [] split2 = {s.substring(0, index), s.substring(index+1)};
			
			String key = split2[0];
			String value = split2[1];
			
			permissions.put(key, value);
			
		}
	}
	
	
	/**
	 * Constructor via standard fields.<br>
	 * @param Integer company number
	 * @param String language key
	 * @param String user ID
	 */
	public BaseUserParam(Integer company, String lang, String userid, Long user_id) {
		this.company = company;
		this.languageCode = lang != null? lang : LangKey.getDefaultLanguageCode();
		this.userid = userid;
		this.user_id = user_id;
		this.admin = false; //force this value to be explicitly set by calling function.
	}

	/**
	 * Constructor via login DTO.<br>
	 * @param BaseLoginDto login object
	 */
	public BaseUserParam(BaseLogin obj) {
		setCompany(obj.getCompanyNr());
		setUserId(obj.getUserid());
		setUser_id(obj.getId());
		setLanguageCode(obj.getLanguageCode());
		setAdmin(obj.isAdmin());
		setService(obj.isService());
	}
	
	
	
	
	

	/**
	 * Locale is currently derived from the language.<br>
	 * Note: This may change in the future to become a configurable attribute.
	 * @return user locale
	 */
	public Locale getLocale(){
		if (languageCode == null){
			return Locale.forLanguageTag(LangKey.getDefaultLanguageCode());
		}
		return Locale.forLanguageTag(languageCode);
		
	}
	
	
	/**
     * Object to JSON string.<br>
     * TODO: use standard json utilities
     * @param boolean flag to indicate if '{}' curly braces are to be pre/appended.
     * @return JSON string representing <code>BaseUserParam</code> object
	 */
	public String toJson(boolean includeEndBraces) {
		return (includeEndBraces?"{":"") +
				"\"" + JSON_COMPANY      + "\":" + (company == null? 0 : company) + "," +
				"\"" + JSON_USERID       + "\":" + getUserId() + "," +
				"\"" + JSON_USER_ID      + "\":" + getUser_id() + "," +
				"\"" + JSON_LANG         + "\":" + getLanguageCode() + "," +
				"\"" + JSON_ADMIN        + "\":" + isAdmin() + "," +
				"\"" + JSON_SERVICE      + "\":" + isService() + "," +
				"\"" + JSON_PERMISSIONS  + "\":" + toJsonPermissions() + "," + 
				"\"" + JSON_TIMESTAMP    + "\":" + getLoginDateTime() +
				(includeEndBraces?"}":"");
	}
	/**
     * Permissions to JSON string.<br>
     * TODO: use standard json utilities
     * @return JSON string representing permissions object
	 */
	private String toJsonPermissions() {
		if (permissions == null){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		Enumeration<String> keys = permissions.keys();
		int count = 0;
		while (keys.hasMoreElements()){
			String key = keys.nextElement();
			String value = permissions.get(key);
			sb.append((count>0?"|":"") + key + "=" + value);
			count++;
		}
		return sb.toString();
	}
	
	
	
	
	@SuppressWarnings("unchecked")
    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        List<BaseUserParam> logins = (List<BaseUserParam>) event.getSession().getServletContext().getAttribute(LOGIN_SET);
        if (logins == null){
			logins = new ArrayList<BaseUserParam>();
			event.getSession().getServletContext().setAttribute(LOGIN_SET, logins);
		}
        logins.add(this);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        List<BaseUserParam> logins = (List<BaseUserParam>) event.getSession().getServletContext().getAttribute(LOGIN_SET);
        if (logins != null){
        	logins.remove(this);

        	try{
	        	AuditLog.log("Session unbound for userid= '" + getUserId() + "' " +
	        	             "(session length:" + calculateLoggedInMinutes() + "min)");
        	} 
        	catch (Exception e){
        		//Session already invalidated
        	}
        }
    }

    /**
     * Return logged in time in minutes
     * @param loginTime
     * @return
     */
    public long calculateLoggedInMinutes() throws Exception{
    	return (System.currentTimeMillis() - getLoginDateTime()) / (1000 * 60);
    }

    
    @Override
    public boolean equals(Object obj){
    	if (obj instanceof BaseUserParam){
    		BaseUserParam p = (BaseUserParam)obj;
    		if (this.getSessionId().equals(p.getSessionId())){
    			return true;
    		}
    	}
    	return false;
    }
    
    @Override
    public int hashCode(){
    	return getSessionId().hashCode();
    }
    
    
    /**
     * Add a user permission key/value pair
     * @return
     */
    public void addPermission (String key, String value){
    	if (permissions == null){
    		permissions = new Hashtable<>();
    	}
    	permissions.put(key, value);
    }
    
    /**
     * Does the user have <code>CREATE</code> permission for the passed in key? 
     * @param key
     * @return
     */
    public boolean isCreate (String key){
        if (key.indexOf(SERVICE_PERMISSION) != -1){
            return isService();
        }
        if (isAdmin()){
    		return true;
    	}
    	return isPermission(key, Permission.CREATE);
    }
    
    /**
     * Does the user have <code>READ</code> permission for the passed in key? 
     * @param key
     * @return
     */
    public boolean isRead (String key){
        if (key.indexOf(SERVICE_PERMISSION) != -1){
            return isService();
        }
        if (isAdmin()){
    		return true;
    	}
    	return isPermission(key, Permission.READ);
    }
    /**
     * Does the user have <code>UPDATE</code> permission for the passed in key? 
     * @param key
     * @return
     */
    public boolean isUpdate (String key){
        if (key.indexOf(SERVICE_PERMISSION) != -1){
            return isService();
        }
        if (isAdmin()){
    		return true;
    	}
    	return isPermission(key, Permission.UPDATE);
    }
    /**
     * Does the user have <code>DELETE</code> permission for the passed in key? 
     * @param key
     * @return
     */
    public boolean isDelete (String key){
        if (key.indexOf(SERVICE_PERMISSION) != -1){
            return isService();
        }
        if (isAdmin()){
    		return true;
    	}
    	return isPermission(key, Permission.DELETE);
    }
    
    /**
     * Is the permission type included in the CRUD?
     * @param key
     * @param type
     * @return
     */
    private boolean isPermission(String key, String type){
        String crud = permissions != null? permissions.get(key) : null;
        return crud != null && crud.indexOf(type) != -1;
    }
    
    public boolean ignoreCompany(){
    	return company != null && company.equals(IGNORE_COMPANY_NUMBER);
    }
    
    
    ////////////////////// Getters / Setters //////////////////////////////////
	public Integer getCompany() {
		return company;
	}
	public void setCompany(Integer company) {
		this.company = company;
	}
	public String getLanguageCode() {
		return languageCode == null? LangKey.getDefaultLanguageCode() : languageCode.toLowerCase();
	}
	public void setLanguageCode(String lang) {
		this.languageCode = lang;
	}
	
	public String getUserId() {
		return userid;
	}
	public void setUserId(String userId) {
		this.userid = userId;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public boolean isAdmin() {
		return isService() || (admin != null && admin);
	}
	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
	public boolean isService() {
		return service != null && service;
	}
	public void setService(Boolean service) {
		this.service = service;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public long getLoginDateTime() {
		return loginDateTime;
	}
	public void setLoginDateTime(long loginDateTime) {
		this.loginDateTime = loginDateTime;
	}
	public HttpSession getHttpSession() {
		return httpSession;
	}
	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}
	public String getPermission(String key) {
		return permissions != null? permissions.get(key) : null;
	}
	public Hashtable<String, String> getPermissions() {
		return permissions;
	}
	public void setPermissions(Hashtable<String, String> permissions) {
		this.permissions = permissions;
	}

    
    
}
