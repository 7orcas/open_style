package com.sevenorcas.openstyle.app.login;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.progenso.desma.ApplicationI;
import com.progenso.desma.app.ApplicationParameters;
import com.progenso.desma.app.entities.BaseEntity;
import com.progenso.desma.app.entities.lang.LangKey;
import com.progenso.desma.app.entities.useradmin.User;
import com.progenso.desma.entities.app.Login;
import com.progenso.desma.interfaces.rest.app.LoginRest;
import com.progenso.desma.service.app.LoginServiceImp;


/**
 * The <code>BaseLogin</code> class contains essential fields, attributes and methods for the implementing Login entity.<br>
 * <b>This</b> class is <code>abstracted</code> so that the implementing project can add functionality relevant to their needs 
 * (eg more identity fields such as Customer Number).<p>  
 * 
 * <b>This</b> class is tightly coupled with the <code>LoginService</code> and <code>LoginRest</code> classes, as well as the 
 * client side <code>app/login/object_login.js</code> javascript file.<p>
 * 
 * <b>This</b> class must be extended by a concrete login class.
 * 
 * @see LoginServiceImp
 * @see LoginRest
 * @see app/login/object_login.js
 * 
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
@MappedSuperclass
abstract public class BaseLogin extends BaseEntity implements LoginI, Serializable {
	
	
    ////////////////////// Constants  //////////////////////////////////
	
	final static private int MAX_TRYS                     = 3;
	final static private int LOGIN_UNVALIDATED            = -1;
	final static public  int MD5_PASSWORD_MAX_LENGTH      = 35;
	//Duplicate DELETE final static public  long SERVICE_ID                  = 0L;
	final static public  long PROGRESS_ID                 = -99L; //TODO: refactor as progress has no id
	
    ////////////////////// Fields  //////////////////////////////////

	@Transient
	protected ApplicationParameters appParam = ApplicationParameters.getInstance();
	
	
	/**
	 * ID Field.<p>
	 * @see ApplicationI
	 */
	@Id
	protected Long id;

	@Column(name="_jhew")
	protected String userid;

	@Column(name="_gghae")
	private String password;

	private Boolean admin;
	
    
    @Column(name="comp_nrs")
	private String companyNrs;
    
    @Column(name="lang_code")
	private String languageCode;
    
    /**                                       
	 * Site code.<br>
	 * Used to uniquely identify the user organization for the given <code>company number</code>. Note a site code may apply to a number of company numbers.<p>  
	 * 
	 * eg if <code>site = "abc"</code>, then client report pdf generation can use logo files appended 
	 * with the site code, such as "header_logo_abc.png". 
	 */
    private String site;
    
    protected Boolean locked;
    protected Integer trys;
    protected String groups;
    
    @Column(name="params")
	protected String parameters;

    @Column(name="last_login")
	protected Date lastLogin;
    
    //TODO Add to db
    @Transient
    private String decimalSymbol;

    //TODO Add to db
    @Transient
	public String dateFormat;
    @Transient
    public String dateMonthFormat;
    @Transient
    public List<String> daysShort;
    @Transient
    public List<String> months;
    @Transient
    public List<String> monthsShort;
    @Transient
	public String noRepeatCodes;
    
    ////////////////////// Transient Fields  //////////////////////////////////

    @Transient
    private Boolean service;
    
    @Transient
    protected boolean validPassword = false;
    
    /**
	 * Permission key value pairs.<ul>
	 *     <li>key = permission key are defined on the entity</li>
	 *     <li>value = CRUD (create, read, update and delete) permission as defined on a role</li>
	 * </ul><p>
	 * 
	 * Note: The highest possible CRUD value is stored for key.
	 */
    @Transient
    protected Hashtable<String, String> permissions;
    
    /**
     * Result of validation.<br>
     * Note various methods can validate <b>this</b> login and set it to an invalid value. ie like a veto system. 
     */
    @Transient
    protected int success = LOGIN_UNVALIDATED;
    
    @Transient
    protected boolean imageCapture = false;
    
    @Transient
    protected Integer clientResponse;
    
    @Transient
    protected String rootContext;
	
    @Transient
    protected String remoteUrlPrefix;
    
    ////////////////////// Main class methods  //////////////////////////////////
    
    /**
     * Create invalid login object
     */
    protected BaseLogin() {
    	super();
    	initialise();
    }
    
    /**
     * Create invalid login object
     * @param language
     * @return Invalid login object
     */
    public BaseLogin (String language){
    	super();
    	setLanguageCode(LangKey.validate(language));
    	setSuccess(Login.LOGIN_INVALID);
    	initialise();
    }

    
    /**
	 * Create a valid login object
	 * @param company
	 * @param userid
	 * @param user_id
	 * @param language
	 * @return Valid login object
	 */
	public BaseLogin (Integer company, String userid, Long user_id, String language){
		super();
		setUserid(userid);
		setId(user_id);
		setCompanyNr(company);
		setLanguageCode(LangKey.validate(language));
		initialise();
	}
    
	

	/**
	 * Initialize login object
	 */
	private void initialise(){
		if (getSite() == null){
			setSite(appParam.getSite());
		}
		if (getLanguageCode() == null){
			setLanguageCode(LangKey.getDefaultLanguageCode());
		}
		rootContext = "/" + ApplicationI.APP_NAME;
				
	    //Defined in WebApplication.java
		remoteUrlPrefix = "rest/";
	}
	
	
	
	
    /**
     * Validate <b>this</b> login object via the implemented <code>Login</code> class.<p>
     * 
     * The implementing class sets <code>success</code> field with appropriate value if login is not successful.
     */
    abstract public void validate();
    
    
    /**
	 * Test userid password for validity (against XSS)
	 * @param userid
	 * @return
	 */
	static public boolean isValidUserid(String userid){
		if (userid == null || userid.length() > User.USERID_MAX_LENGTH){
			return false;
		}
		
		//Test userid specifically excludes
		if (User.testStringContains(userid, User.USERID_TO_EXCLUDE)){
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
		if (password == null || password.length() > MD5_PASSWORD_MAX_LENGTH){
			return false;
		}
		
		//Test password specifically excludes
		if (User.testStringContains(password, User.PASSWORD_TO_EXCLUDE)){
			return false;
		}
		
		return true;
	}
    
    
    /**
     * Validate <b>this</b> login object
     * @param String user given md5_password
     * @param Integer specific company number user is loggin into (if NULL then users default number is used)
     * @return int: LOGIN_SUCCESS if successful, otherwise a number corresponding to the LOGIN_XXX value
     */
	@Transient
    public int validate(String md5_password, Integer nr) {
		
    	validPassword = testPasswordMd5(password, md5_password);
    	
    	if (validPassword && locked){
    		success = LOGIN_LOCKED;
    		return LOGIN_LOCKED;
    	}
    	
    	if (!active){
    		success = LOGIN_INACTIVE;
    		return LOGIN_INACTIVE;
    	}
    	
    	if (!validPassword){
    		trys++;
    		if (trys > MAX_TRYS){
    			setLocked(true);
    		}
    		
    		success = LOGIN_INVALID;
    		return LOGIN_INVALID;
    	}
    	
    	if (nr != null && !same(companyNr, nr)){
    		success = LOGIN_INVALID_COMPANY_NUMBER;
    		return LOGIN_INVALID_COMPANY_NUMBER;
    	}
    	
    	//Call implementing classes validation method
    	validate();

    	//previous method has invalidated this login
    	if (success != LOGIN_UNVALIDATED){
    		return success;
    	}
    	
    	//reset false trys
    	setTrys(0);
    	
		return LOGIN_SUCCESS;
	}
    
    
    /**
	 * Test MD5 passwords for a match
	 * @param pw_user
	 * @param pw_db
	 * @return
	 */
	static public boolean testPasswordMd5(String pw_user, String pw_db){
		if (pw_user == null){
			return false;
		}
		
		if (pw_db == null || pw_user.equals(pw_db)){
			return true;
		}
		//Work around TODO fix it
		//javascript sometimes returns extra 0
		if (pw_user.substring(1).equals(pw_db)){
			return true;
		}
		//Work around TODO fix it
		//javascript sometimes returns extra 0
		if (pw_user.equals(pw_db.substring(1))){
			return true;
		}
		
		return false;
	}
	
    @Transient
    public boolean isSuccess() {
		return success == LOGIN_SUCCESS;
	}
    
    @Transient
    public void addMonths(String month) {
        if (months == null){
            months = new ArrayList<>();
        }
        months.add(month);
    }
    
    @Transient
    public void addMonthsShort(String month) {
        if (monthsShort == null){
            monthsShort = new ArrayList<>();
        }
        monthsShort.add(month);
    }

    @Transient
    public void addDaysShort(String day) {
        if (daysShort == null){
            daysShort = new ArrayList<>();
        }
        daysShort.add(day);
    }
	
    
    ////////////////////// Getters / Setters //////////////////////////////////    
	
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
	public boolean isAdmin() {
		return admin != null && admin;
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
	public String getCompanyNrs() {
		return companyNrs;
	}
	public void setCompanyNrs(String companyNrs) {
		this.companyNrs = companyNrs;
	}
	public Boolean isLocked() {
		return locked != null && locked;
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
	public void setParameters(String params) {
		this.parameters = params;
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
	public boolean isImageCapture() {
		return imageCapture;
	}
	public void setImageCapture(Boolean imageCapture) {
		this.imageCapture = imageCapture;
	}
	public Date getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	public Integer getClientResponse() {
		return clientResponse;
	}
	public void setClientResponse(Integer clientResponse) {
		this.clientResponse = clientResponse;
	}
	public int getSuccess() {
		return success;
	}
	public boolean isValidPassword() {
		return validPassword;
	}
	public Hashtable<String, String> getPermissions() {
		return permissions;
	}
	public void setPermissions(Hashtable<String, String> permissions) {
		this.permissions = permissions;
	}
	public String getDecimalSymbol() {
		return decimalSymbol;
	}
	public void setDecimalSymbol(String decimalSymbol) {
		this.decimalSymbol = decimalSymbol;
	}
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	public String getDateFormatMonth() {
        return dateMonthFormat;
    }
    public void setDateFormatMonth(String dateFormatMonth) {
        this.dateMonthFormat = dateFormatMonth;
    }
    public String getNoRepeatCodes() {
		return noRepeatCodes;
	}
	public void setNoRepeatCodes(String noRepeatCodes) {
		this.noRepeatCodes = noRepeatCodes;
	}

	public List<String> getDaysShort() {
        return daysShort;
    }
    public void setDaysShort(List<String> daysShort) {
        this.daysShort = daysShort;
    }
    public List<String> getMonths() {
        return months;
    }
    public void setMonths(List<String> months) {
        this.months = months;
    }
    public List<String> getMonthsShort() {
        return monthsShort;
    }
    public void setMonthsShort(List<String> monthsShort) {
        this.monthsShort = monthsShort;
    }

    public String getRootContext() {
		return rootContext;
	}
	public void setRootContext(String rootContext) {
		this.rootContext = rootContext;
	}
	public String getRemoteUrlPrefix() {
		return remoteUrlPrefix;
	}
	public void setRemoteUrlPrefix(String remoteUrlPrefix) {
		this.remoteUrlPrefix = remoteUrlPrefix;
	}

	/**
	 * Result of validation.<br>
     * Note various methods can validate <b>this</b> login and set it to an invalid value. ie like a veto system.
	 * @param success
	 */
	public void setSuccess(int success) {
		this.success = success;
	}
	
	
	
	
}
