package com.sevenorcas.openstyle.app.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.progenso.desma.app.Utilities;
import com.progenso.desma.app.anno.Dto;
import com.progenso.desma.app.anno.Field;
import com.progenso.desma.app.entities.BaseEntity;
import com.progenso.desma.app.entities.lang.LangKey;
import com.progenso.desma.app.exception.ValidationException;

/**
 * User entity<p>
 *
 * Notes:<ul>
 * <li>The <code>companyNr</code> field is the primary login company for the user. The <code>companyNrs</code> field
 *     (if used) allows the user to log into another company. This must be in combination with the login screen
 *     capturing and sending the desired company number.</li>
 * </ul><p>
 *
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Entity
@Table(name="_yzh_user", schema="cntrl")
@SequenceGenerator(name="ID_SEQUENCE",sequenceName="seq_id_entity",allocationSize=1)
public class User extends BaseEntity implements Serializable {

	final static public int    USERID_MIN_LENGTH             = 2;
	final static public int    USERID_MAX_LENGTH             = 30;
	final static public String USERID_TO_EXCLUDE             = "; '";
	
	final static public int    PASSWORD_MIN_LENGTH           = 8;
	final static public int    PASSWORD_MAX_LENGTH           = 10;
	final static public String PASSWORD_TO_INCLUDE           = "_!$&/?#";
	final static public String PASSWORD_TO_EXCLUDE           = "; '";
	
	final static public String DUMMY_PASSWORD                = "xxx";
	
	final static public int    CONFIG_TYPE_EXPORT            = 1;
	final static public int    CONFIG_TYPE_FIX_LIST          = 2;
	final static private int[] CONFIG_TYPES                  = new int[] {CONFIG_TYPE_EXPORT, CONFIG_TYPE_FIX_LIST};
	
	//CONFIG_IMPORT_TYPE are deleted after import
	final static public int    CONFIG_IMPORT_NOREPEAT_MOULDS = 1;
	final static private int[] CONFIG_IMPORT_TYPES           = new int[] {CONFIG_IMPORT_NOREPEAT_MOULDS};
	
	final static public String CONFIG_IMPORT_NOREPEAT_MOULDS_CODE = "prepErr1";
	
	/** ID Field. */   
	@Id  
	@Field(appType=APP_TYPE_ID)
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="ID_SEQUENCE")
	private Long id;
	
	
	@Field(notNull=true, min=USERID_MIN_LENGTH, max=USERID_MAX_LENGTH, label="User")
	@Column(name="_jhew")
	private String userid;

	@Field(edit="true", max=PASSWORD_MAX_LENGTH, label="Pass")
	@Column(name="_gghae")
	private String password;
	
	@Transient
	private Boolean passwordUpdate;

	@Field(edit="true")
	private Boolean admin;
	
	@Field(edit="true")
    @Column(name="comp_nrs")
	private String companyNrs;
    
	@Field(notNull=true, min=2, max=2, label="Lang")
    @Column(name="lang_code")
	private String languageCode;
    
	@Column(name="config")
    private String config;
	
    /**                                       
	 * Site code.<br>
	 * Used to uniquely identify the user organization for the given <code>company number</code>. Note a site code may apply to a number of company numbers.<p>  
	 * 
	 * eg if <code>site = "abc"</code>, then client report pdf generation can use logo files appended 
	 * with the site code, such as "header_logo_abc.png".
	 * 
	 * Move this to a separate entity 
	 */
    @Deprecated
    private String site;
    
    @Field(edit="true")
    private Boolean locked;
    
    @Field(label="LoginTrys")
    private Integer trys;
    
    @Field
    private String groups;
    
    @Field
    @Column(name="params")
	private String parameters;

    @Field(format="dd.mm.yyyy hh:mm")
    @Column(name="last_login")
	private Date lastLogin;

    @Field(format="dd.mm.yyyy hh:mm")
    @Column(name="last_logout")
    private Date lastLogout;
    
    /**
	 * User roles.
	 */
	@Field
	@OneToMany(cascade={CascadeType.ALL},mappedBy="user")
	private List <UserRole> roles = new ArrayList<UserRole>(); 
	
    
    
    ////////////////////// Methods //////////////////////////////////	
	
	/**
	 * Default Constructor
	 */
	public User() {
		initialise();
		admin = false;
	}

	/**
	 * Client initializations for <b>this</b> dto 
	 */
	@Dto(init=true)
	public void initialise(){
		languageCode = LangKey.getDefaultLanguageCode();
		locked = false;
		trys   = 0;
	}
	
	
	/**
	 * Add a role entity 
	 * @param UserRole object to add
	 */
	public void addUserRole(UserRole role){
		role.setUser(this);
		role.setCompanyNr(companyNr);
		roles.add(role);
	}
	
	/**
	 * Find a role entity by its ID
	 * @param id
	 * @return
	 */
	public UserRole findUserRoleById(Long id) {
		return (UserRole)findBaseEntityById(id, roles);
	}
	
	/**
	 * Find a UserRole entity by role_id
	 * @param role_id
	 * @return
	 */
	public UserRole findUserRoleByRoleId(Long role_id) {
		for (UserRole rec: roles){
			if (rec.getRoleId().equals(role_id)){
				return rec;
			}
		}
		return null;
	}
	
	
	/**
	 * Validate this entity.
	 * @throws ValidationException if password is invalid
	 */
	public void validate () throws ValidationException{
		
		if (isPasswordUpdate() && !isValidPassword(password)){
			addValidateException ("PassInvalid");
		}
		if (!languageCode.equals(LangKey.validate(languageCode))){
			addValidateException ("LangInvalid");
		}
		
		
		throwValidateException();
	}
	
	/**
	 * Test userid password for validity (particularly against XSS)
	 * @param userid
	 * @return
	 */
	static public boolean isValidUserid(String userid){
		if (userid == null 
				|| userid.length() < USERID_MIN_LENGTH
				|| userid.length() > USERID_MAX_LENGTH){
			return false;
		}
		
		//Test userid specifically excludes
		if (testStringContains(userid, USERID_TO_EXCLUDE)){
			return false;
		}

		return true;
	}


	/**
	 * Test password password for validity (particularly against XSS)
	 * @param password
	 * @return
	 */
	static public boolean isValidPassword(String password){
		if (password == null 
				|| password.length() < PASSWORD_MIN_LENGTH
				|| password.length() > PASSWORD_MAX_LENGTH){
			return false;
		}
		
		
		//Test password includes
		if (!testStringContains(password, PASSWORD_TO_INCLUDE)){
			return false;
		}

		//Test password specifically excludes
		if (testStringContains(password, PASSWORD_TO_EXCLUDE)){
			return false;
		}
		
		//Test upper and lower
		boolean upper = false;
		boolean lower = false;
		boolean digit = false;
		for (int i=0; i<password.length(); i++){
			Character c = password.charAt(i);
			if (Character.isUpperCase(c)){
				upper = true;
			}
			else if (Character.isLowerCase(c)){
				lower = true;
			}
			else if (Character.isDigit(c)){
				digit = true;
			}
			//Test password excludes other special cases
			else if (!testStringContains("" + c, PASSWORD_TO_INCLUDE)){
				return false;
			}
		}
		
		if (!upper || !lower || !digit){
			return false;
		}
		
		return true;
	}
	
	/**
	 * Does the passed in string contain characters from the passed in list
	 * @param String to test
	 * @param String of characters
	 * @return
	 */
	static public boolean testStringContains(String string, String characters){
		for (int i=0; i<characters.length(); i++){
			String c = "" + characters.charAt(i);
			if (string.indexOf(c) != -1){
				return true;
			}
		}
		return false;
	}


   /**
     * User <code>config</code> field can be encoded with different configurations. Encoding the config field requires
     * the complete config and the partial field to insert 
     * @param String total config field
     * @param String specific config field
     * @param int config type
     * @return
     */
    static public String encodeConfig(String config, String partial, int type){
        return encodeConfigField(config, partial, type, CONFIG_TYPES);
    }

    /**
     * User <code>config_import</code> field can be encoded with different configurations. Encoding the config field requires
     * the complete config and the partial field to insert 
     * @param String total config field
     * @param String specific config field
     * @param int config type
     * @return
     */
    static public String encodeConfigImport(String config, String partial, int type){
        return encodeConfigField(config, partial, type, CONFIG_IMPORT_TYPES);
    }
    
    /**
     * User <code>config_import</code> field can be encoded with different configurations. Encoding the config field requires
     * the complete config and the partial field to insert 
     * @param String total config field
     * @param String specific config field
     * @param int config type
     * @return
     */
    static private String encodeConfigField(String config, String partial, int type, int[] types){
        Hashtable<String, String> e = Utilities.fromParameterEncode(config);
        String k = "" + type;
        
        if (partial == null || partial.isEmpty()){
            e.remove(k);
        }
        else{
            e.put(k, partial);
        }
        
        for (int t: types){
            String v = e.get("" + t);
            if (v != null){
                e.put("" + t, Utilities.encodeLevel2(v));
            }
        }
        
        return Utilities.toParameterEncode2(e);
    }
    
    /**
     * Return the specific User encoded <code>config</code> field for the passed in config type
     * @param String total config field
     * @param int config type
     * @return specific config field
     */
    static public String decodeConfig(String config, int type){
        Hashtable<String, String> params = Utilities.fromParameterEncode(config);
        return Utilities.fromParameterLevel2(params, "" + type, null);
    }
	
    //////////////////////Getters / Setters //////////////////////////////////
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getAdmin() {
		return admin;
	}
	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public String getCompanyNrs() {
		return companyNrs;
	}
	public void setCompanyNrs(String companyNrs) {
		this.companyNrs = companyNrs;
	}

	public String getLanguageCode() {
		return languageCode;
	}
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}

	public Boolean getLocked() {
		return locked;
	}
	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Integer getTrys() {
		return trys;
	}
	public void setTrys(Integer trys) {
		this.trys = trys;
	}

	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}

	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public Date getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLastLogout() {
        return lastLogout;
    }
    public void setLastLogout(Date lastLogout) {
        this.lastLogout = lastLogout;
    }

    public boolean isPasswordUpdate() {
		return passwordUpdate != null && passwordUpdate;
	}
	public void setPasswordUpdate(Boolean passwordUpdate) {
		this.passwordUpdate = passwordUpdate;
	}

	public List<UserRole> getRoles() {
		return roles;
	}
	public void setRoles(List<UserRole> roles) {
		this.roles = roles;
	}

    public String getConfig() {
        return config;
    }
    public void setConfig(String config) {
        this.config = config;
    }

	
		
	
}
