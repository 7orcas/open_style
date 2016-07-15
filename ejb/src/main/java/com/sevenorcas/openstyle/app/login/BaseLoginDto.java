package com.sevenorcas.openstyle.app.login;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.application.ApplicationParameters;


/**
 * The <code>BaseLogin</code> class contains essential fields, attributes and methods for the implementing Login entity.<br>
 * <b>This</b> class is <code>abstracted</code> so that the implementing project can add functionality relevant to their needs 
 * (eg more identity fields such as Customer Number).<p>  
 * 
 * <b>This</b> class is tightly coupled with the <code>LoginService</code> and <code>LoginRest</code> classes, as well as the 
 * client side <code>app/login/object_login.js</code> javascript file.<p>
 * 
 * <b>This</b> class must be extended by a concrete class.
 * 
 * @see LoginServiceImp
 * @see LoginRest
 * @see app/login/object_login.js
 * 
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
abstract public class BaseLoginDto implements Serializable {

	
    //////////////////////////////  JSON Fields  //////////////////////////////////
	
	
	/**                                       
	 * Site code.<br>
	 * Used to uniquely identify the user organization for the given <code>company number</code>. Note a site code may apply to a number of company numbers.<p>  
	 * 
	 * eg if <code>site = "abc"</code>, then client report pdf generation can use logo files appended 
	 * with the site code, such as "header_logo_abc.png". 
	 */ 
	@JsonProperty(value="s")  
	public String  site;
	
	/** 
	 * Company number.<br>
	 * Used to uniquely identify the user organization.<p>
	 * 
	 * Note: company number is a fundamental field in the database, most tables are company number based. <b>This</b> field is often included in unique database table keys.
	 */ 
	@JsonProperty(value="nr")
	public Integer companyNr;
	
	/** 
	 * Is <b>this</b> a test company?<br>
	 * i.e. not used for real
	 */
	@JsonProperty(value="ct")
	public Boolean testCompany;
	
	
	/** 
     * Company Code.<br>
     */ 
    @JsonProperty(value="cc")
    public String companyCode;
	
	/** 
	 * User id.<br>
	 * Used to uniquely identify a user within the system.<p>
	 */
	@JsonProperty(value="uid")
	public String  userid;
		
	/** 
	 * Login response code.<br>
	 * Sent to the client after an attempted login.<p>
	 * 
	 * See client side <code>object_login.js</code> module. 
	 */
	@JsonProperty(value="rs")
	public Integer response;
	
	/** 
	 * Login response message.<br>
	 * Sent to the client after an invalid attempted login. The message uses the client language code.<p>
	 * 
	 * See client side <code>app/login/object_login.js</code> module. 
	 */
	@JsonProperty(value="m")
	public String  message;
	
	/** 
	 * User default language.<br>
	 * As stored in the user database table.<br> 
	 * Note: The client login request parameter may override this value (eg if the client requested a different language in the Login page). 
	 */
	@JsonProperty(value="lg")
	public String  languageCode;
	
	/** 
	 * User parameters.<br>
	 * As stored in the user database table.<br>
	 * These are application dependent presets, eg <code>parameter="supplier"</code> could mean that the userid is associated to a supplier.
	 */ 
	@JsonProperty(value="pr")
	public String  parameters;
	
	/** 
	 * Help URL Root.<br>
	 * Root URL of help pages. The client language code is appended to the URL along with the "helpindex.html" is file name.<p>
	 * 
	 * <b>This</b> value is defined in <code>ApplicationParameters</code>.<br> 
	 * See client side main <code>controller.js</code> module. 
	 * @see ApplicationParameters
	 */ 
	@JsonProperty(value="hr")
	public String  helpRootUrl;
		
	/** 
	 * Request an image Captcha.<br>
	 * If true then the client must login with a valid Captcha code.<p>
	 * 
	 * <b>This</b> value is defined in <code>ApplicationParameters.EnableCaptcha</code>. 
	 * @see ApplicationParameters
	 * @see http://www.google.com/recaptcha
	 */
	@JsonProperty(value="ic")
	public Boolean imageCapture;
		
	/** 
	 * Captcha public key.<br>
	 * Sent to client for Captcha.<p>
	 * 
	 * <b>This</b> value is defined in <code>ApplicationParameters.CaptchaPublicKey</code>. 
	 * @see ApplicationParameters
	 * @see http://www.google.com/recaptcha
	 */
	@JsonProperty(value="cp")
	public String  imageCatchaPublicKey;
		
	/** 
	 * Heartbeat timer.<br>
	 * Sent to client for configuring its heartbeat.<p>
	 * 
	 * <b>This</b> value is defined in <code>ApplicationParameters.Heartbeat</code>. 
	 * @see ApplicationParameters
	 */
	@JsonProperty(value="ht")
	public Integer heatbeatTimer;
		
	/** 
     * Session Timeout Test timer.<br>
     * Sent to client for configuring its timeout test.<p>
     * 
     * <b>This</b> value is defined in <code>ApplicationParameters.SessionTimeoutTest</code>. 
     * @see ApplicationParameters
     */
    @JsonProperty(value="st")
    public Integer sessionTimeoutTest;
	
	/** 
	 * Admin rights.<br>
	 * As stored in the user database table.<br>
	 * This is an application dependent value, ie generally admin users can access more functions than "normal" users.<p>
	 * 
	 * Access to admin rights can be enabled / disabled in <code>ApplicationParameters.EnableAdminUsers</code>. 
	 * @see ApplicationParameters
	 */
	@JsonProperty(value="ad")
	public Boolean admin;
	
	/** 
	 * Service rights.<br>
	 * Hard coded via login id.<br>
	 * This is an application dependent value, ie service users can access all functions including special purpose 'service only' functions (eg language management).<p>
	 */
	@JsonProperty(value="sr")
	public Boolean service;
	
	/** 
	 * Main Page Location href (Forward page).<br>
	 * Once a client has successfully login, then they are redirected to the main application page via <b>this</b> href.<p>
	 * 
	 * <b>This</b> value is defined in <code>ApplicationParameters.MainPage</code>. 
	 * @see ApplicationParameters
	 */
	@JsonProperty(value="rf")
	public String  mainPageUrl;
	
	
	/** 
     * Server Time Zone Offset.<br>
     * Sent to client for adjusting dates that are sent back to the server.<p>
     */
    @JsonProperty(value="tz")
    public Integer timezoneOffset;
	
	/** 
	 * Decimal Symbol Format.<br>
	 */
	@JsonProperty(value="ds")
	public String  decimalSymbol;
	
	/** 
	 * Gui Date Format.<br>
	 */
	@JsonProperty(value="df")
	public String  dateFormat;
	
	
	/** 
     * Number of months in Gui date format.<br>
     */
    @JsonProperty(value="dfn")
    public String  dateFormatMonth;
	
    /** 
     * Month labels.<br>
     */
    @JsonProperty(value="dfm")
    public List<String>  months;
    
    /** 
     * Short Month code labels.<br>
     */
    @JsonProperty(value="dfs")
    public List<String>  monthsShort;
    
    /** 
     * Short Day code labels.<br>
     */
    @JsonProperty(value="dfd")
    public List<String>  daysShort;
    
    /** 
	 * No Repeat codes.<p>
	 */
	@JsonProperty(value="nc")
	public String  norepeatCodes;

    
	/** 
	 * <b>This<br> applications root context for login.<p>
	 * Used by client to redirect when http session becomes invalid. 
	 */
	@JsonProperty(value="rc")
	public String  rootContext;
	
	/** 
	 * <b>This<br> applications root for REST calls.<p>
	 */
	@JsonProperty(value="ru")
	public String  remoteUrlPrefix;

	
	/** 
	 * Is <b>this<br> application is in Debug mode.<p>
	 */
	@JsonProperty(value="dm")
	public Boolean  debugMode = false;
	
	
	///////////////////////////////  Non (JSON) transferred fields  /////////////////////////////////////////////
	
	/**
	 * Flag to indicate successful login.
	 */
	@JsonIgnore 
	private boolean success = false;
	
	/**
	 * Database primary key
	 */
	@JsonIgnore 
	public Long  user_id;
	
	
    ////////////////////////////////////////////  Methods  /////////////////////////////////////////////
	
	/**
	 * Constructor
	 */
	public BaseLoginDto(BaseLogin login) {
		
		if (login == null){
			success = false;
			return;
		}
		
		success = login.isSuccess();
		setUserid(login.getUserid());
		setUser_id(login.getId());
		setCompanyNr(login.getCompanyNr());
		setSite(login.getSite());
		setRootContext(login.getRootContext());
		setRemoteUrlPrefix(login.getRemoteUrlPrefix());
		setResponse(login.getClientResponse());
		setLanguage(login.getLanguageCode());
		setParameters(login.getParameters());
		setImageCapture(login.isImageCapture());
		setAdmin(login.isAdmin());
		setService(login.isService());
		setDateFormat(login.getDateFormat());
		setDateFormatMonth(login.getDateFormatMonth());
		setDaysShort(login.getDaysShort());
		setMonths(login.getMonths());
		setMonthsShort(login.getMonthsShort());
		setDecimalSymbol(login.getDecimalSymbol());
		setNorepeatCodes(login.getNoRepeatCodes());
		
	}
	
	/**
	 * Constructor with invalid login response 
	 * @param response
	 */
	public BaseLoginDto(int response) {
		success = false;
		setResponse(response);
	}
	
	
	
	@JsonIgnore
	public boolean isSuccess(){
		return success;
	}
	
	@JsonIgnore
	public Integer getCompanyNr() {
		return companyNr;
	}
	public void setCompanyNr(Integer nr) {
		this.companyNr = nr;
	}
	
	@JsonIgnore
	public String getCompanyCode() {
        return companyCode;
    }
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    @JsonIgnore
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}

	@JsonIgnore
	public Integer getResponse() {
		return response;
	}
	public void setResponse(Integer response) {
		this.response = response;
	}
	
	@JsonIgnore
	public String getLanguage() {
		return languageCode;
	}
	public void setLanguage(String language) {
		this.languageCode = language;
	}
	
	@JsonIgnore
	public Boolean getAdmin() {
		return admin;
	}
	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
	
	@JsonIgnore
	public Boolean getService() {
		return service;
	}
	public void setService(Boolean service) {
		this.service = service;
	}

	@JsonIgnore
	public Boolean getImageCapture() {
		return imageCapture;
	}
	public void setImageCapture(Boolean imageCapture) {
		this.imageCapture = imageCapture;
	}
	@JsonIgnore
	public boolean isImageCaptured() {
		return imageCapture != null && imageCapture;
	}
	
	
	@JsonIgnore
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	@JsonIgnore
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	@JsonIgnore
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	@JsonIgnore
	public String getHelpUrlRoot() {
		return helpRootUrl;
	}
	public void setHelpUrlRoot(String helpUrlRoot) {
		this.helpRootUrl = helpUrlRoot;
	}

	@JsonIgnore
	public String getCaptchaPublicKey() {
		return imageCatchaPublicKey;
	}
	public void setCaptchaPublicKey(String captchaPublicKey) {
		this.imageCatchaPublicKey = captchaPublicKey;
	}
	
	@JsonIgnore
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	
	@JsonIgnore
	public String getLocationHref() {
		return mainPageUrl;
	}
	public void setLocationHref(String locationHref) {
		this.mainPageUrl = locationHref;
	}

	@JsonIgnore
	public Integer getHeartbeatTimer() {
		return heatbeatTimer;
	}
	public void setHeartbeatTimer(Integer heartbeatTimer) {
		this.heatbeatTimer = heartbeatTimer;
	}

	@JsonIgnore
	public Integer getSessionTimeoutTest() {
        return sessionTimeoutTest;
    }
    public void setSessionTimeoutTest(Integer sessionTimeoutTest) {
        this.sessionTimeoutTest = sessionTimeoutTest;
    }

    @JsonIgnore
    public Integer getTimezoneOffset() {
        return timezoneOffset;
    }
    public void setTimezoneOffset(Integer timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    @JsonIgnore
	public String getDecimalSymbol() {
		return decimalSymbol;
	}
	public void setDecimalSymbol(String decimalSymbol) {
		this.decimalSymbol = decimalSymbol;
	}

	@JsonIgnore
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	@JsonIgnore
	public String getDateFormatMonth() {
        return dateFormatMonth;
    }
    public void setDateFormatMonth(String dateFormatMonth) {
        this.dateFormatMonth = dateFormatMonth;
    }

    
    @JsonIgnore
    public List<String> getDaysShort() {
        return daysShort;
    }
    public void setDaysShort(List<String> daysShort) {
        this.daysShort = daysShort;
    }

    @JsonIgnore
    public List<String> getMonths() {
        return months;
    }
    public void setMonths(List<String> months) {
        this.months = months;
    }

    @JsonIgnore
    public List<String> getMonthsShort() {
        return monthsShort;
    }
    public void setMonthsShort(List<String> monthsShort) {
        this.monthsShort = monthsShort;
    }

    @JsonIgnore
	public String getRootContext() {
		return rootContext;
	}
	public void setRootContext(String rootContext) {
		this.rootContext = rootContext;
	}

	@JsonIgnore
	public String getRemoteUrlPrefix() {
		return remoteUrlPrefix;
	}
	public void setRemoteUrlPrefix(String remoteUrlPrefix) {
		this.remoteUrlPrefix = remoteUrlPrefix;
	}

	@JsonIgnore
	public String getNorepeatCodes() {
		return norepeatCodes;
	}
	public void setNorepeatCodes(String norepeatCodes) {
		this.norepeatCodes = norepeatCodes;
	}

	@JsonIgnore
	public Boolean getTestCompany() {
		return testCompany;
	}
	public void setTestCompany(Boolean testCompany) {
		this.testCompany = testCompany;
	}

	@JsonIgnore
	public Boolean getDebugMode() {
		return debugMode;
	}
	public void setDebugMode(Boolean debugMode) {
		this.debugMode = debugMode;
	}
	
	
	
}
