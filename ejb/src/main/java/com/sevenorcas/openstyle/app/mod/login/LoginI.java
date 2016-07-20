package com.sevenorcas.openstyle.app.mod.login;

/**
 * Login constants
 * 
 * [License]
 * @author John Stewart
 */
public interface LoginI {

	/**
	* Successful login by client
	*/
	final static public int LOGIN_SUCCESS                 = 0;
	
	/**
	* The requested client login company number is invalid.<br>
	* It either does not exist or the client does not have permission to access it. 
	*/
	final static public int LOGIN_INVALID_COMPANY_NUMBER  = 1;
	
	/**
	* Client login is invalid, ie rejected for any number of reasons such as:
	* <ul>- no valid HttpSession</ul>
	* <ul>- invalid userid / password</ul>
	* <ul>- invalid user parameter</ul>
	* <ul>- invalid Captcha key (if implemented)</ul> 
	*/
	final static public int LOGIN_INVALID                 = 2;
	
	/**
	* The client login has exceeded the allowed trys and is no locked.<br>
	* The unlock process must now be used to re-allow access.
	* @see LOGIN_MAX_TRYS 
	*/
	final static public int LOGIN_LOCKED                  = 3;
	
	/**
	* The client has logged out. 
	*/
	final static public int LOGIN_LOGGED_OUT              = 4;
	
	/**
	* The number of allowed Captcha trys (if implemented). 
	*/
	final static public int LOGIN_INVALID_CAPTURE          = 5;
	
	/**
	* The number of allowed login trys before a userid is locked.
	* @see LOGIN_LOCKED 
	*/
	final static public int LOGIN_MAX_TRYS                = 6;
	
	/**
	* The client login is not active.
	*/
	final static public int LOGIN_INACTIVE                = 7;
	
	/**
	 * Service user name
	 */
	final static public String SERVICE_USERID             = "service";
	final static public long   SERVICE_ID                 = 0L;
	
}
