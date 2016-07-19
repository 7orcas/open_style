package com.sevenorcas.openstyle.app.login;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.application.ApplicationParameters;
import com.sevenorcas.openstyle.app.application.Utilities;
import com.sevenorcas.openstyle.app.application.exception.AppException;
import com.sevenorcas.openstyle.app.lang.LangKey;
import com.sevenorcas.openstyle.app.lang.LanguageService;
import com.sevenorcas.openstyle.app.log.AuditLog;
import com.sevenorcas.openstyle.app.mail.MailService;
import com.sevenorcas.openstyle.app.user.User;
import com.sevenorcas.openstyle.app.user.UserAdminService;
import com.sevenorcas.openstyle.app.user.UserParam;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;


/**
 * Progress Login (Logisoft) service.<p>
 * 
 * Logging in is a 2 step process:<ol>
 * 
 * <li>The login method is called to test the credentials. If true then the main page name is returned to 
 *     the client. The user id, company number and language are stored in the session object.</li>
 * <li>Initialize user presets against the stored session object.</li>
 *  
 * </ol>
 *  
 * [License] 
 * @author John Stewart
 */
@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class LoginServiceImp implements LoginService, LoginI, ApplicationI {
	
    private ApplicationParameters appParam = ApplicationParameters.getInstance();
    
	protected String SERVICE_PASSWORD     = appParam.getServiceAccountPassword();
	protected String SERVICE_MD5_PASSWORD = null;
	protected String SERVICE_LOCKFILE     = "service.lk";
	protected int    SERVICE_MAX_ATTEMPTS = 3;
	

	@EJB private LoginDao         loginDao; 
	@EJB private MailService      mailService;
	@EJB private LanguageService  languageService;
	@EJB private UserAdminService userAdminService;
	
@PersistenceContext(unitName="openstyleDS")
private EntityManager em;

	
	public LoginServiceImp() {
	}
	
	/**
	 * Logout of application (via <code>HttpServletRequest</code>). This action is logged in the audit log.
	 * @param UserParam
	 * @param HttpServletRequest
	 */
	@Override
	public void logout(UserParam userParam, HttpServletRequest httpRequest) throws Exception {
		String ipAddress = httpRequest.getRemoteAddr();
		
		HttpSession s = httpRequest.getSession(false);
		if (s != null){
		    if (userParam != null){
		        updateLastLogout (userParam.getUserId());
		    }
			AuditLog.logout("userid='" + (userParam != null? userParam.getUserId() : "?(userParam null)") + "', "
					+ "ip=" + ipAddress);

			s.invalidate();
		}
		else{
			AuditLog.logout("Logout in userid='NO SESSION', ip= " + ipAddress);
		}
	}
	
	/**
	 * Return ReCaptcha public key (site dependent)
	 * @param HttpServletRequest
	 * @return Captcha public key 
	 */
	@Override
	public String recaptchaPublicKey(HttpServletRequest req) {
		return appParam.getCaptchaPublicKey();
	}

	
	
	/**
	 * First step to log into application.
	 * The users credentials are validated against the database record. If Captcha is enabled, then the challenge / response pair is also validated.  
	 * Success:
	 *  - return valid Login object
	 * Failure:
	 *  - return invalid Login object (and locked message if locked and valid userid/password)  
	 */
	@Override
	public Login login(Integer company, String userid, String md5_password, String language, String challenge, String response, HttpServletRequest httpRequest) throws Exception {

		String ipAddress = httpRequest.getRemoteAddr();
		
		
		//Validate userid and password against XSS
		if (!Login.isValidUserid(userid) || !Login.isValidPassword(md5_password)){
			sendMail ("Attempted login with invalid userid / password",	mailBody(userid, md5_password, httpRequest));
			return new Login(language);
		}
		
		Login rec = loadUser(company, userid, md5_password, true, language);
		
		if (userid.equals(SERVICE_USERID) && isLoginServiceLocked()){
		    sendMail ("Attempted login to service account whilst it is locked!", mailBody(userid, md5_password, httpRequest));
		    
		    if (rec.isSuccess()){
		        rec.setLocked(true);
		    }
		    rec.setSuccess(LOGIN_LOCKED);
		    rec.setLanguageCode(language);
		    return rec;
		}
		
		//No such user id
		if (rec == null){
			sendMail ("Attempted login to unknown userid", mailBody(userid, md5_password, httpRequest));
			return new Login(language);
		}
		
		//Attempted login into service account
		else if (rec.getUserid().equals(SERVICE_USERID) && !rec.isSuccess()){
			sendMail ("Attempted login to service account!", mailBody(userid, md5_password, httpRequest));
			setLoginServiceLocked(false);
			return new Login(language);
		}
		
		//Under attack??
		else if (!rec.isValidPassword() && rec.isLocked()){
			sendMail ("Attempted login to locked account using invalid password", mailBody(userid, md5_password, httpRequest));
		}
		
		if (rec.getUserid().equals(SERVICE_USERID) && rec.isSuccess()){
		    setLoginServiceLocked(true);
		}
		
		
		//If captcha is not correct, then lock
		//ToDo Logic requires more thought. If reCaptcha is used only after x trys, then what is to stop a user from refreshing screen to avoid reCaptcha image?
		if (rec.isSuccess() && !testReCaptcha (challenge, response, ipAddress)){
			rec.setSuccess(Login.LOGIN_INVALID_CAPTURE);
		}

		
		//Configure messages to client
		switch (rec.getSuccess()){
		    	
		    case LOGIN_LOCKED:
		    case LOGIN_MAX_TRYS:
		    	rec.setClientResponse(LOGIN_LOCKED);
		    	break;
		
		    case LOGIN_SUCCESS:
		    	rec.setClientResponse(LOGIN_SUCCESS);
		    	break;
		    	
		    case LOGIN_INVALID_COMPANY_NUMBER:
		    case LOGIN_INVALID:
		    default:
		    	rec.setClientResponse(LOGIN_INVALID);
		}
				
		if (!rec.isService()){
			loginUpdate(new UserParam (company, LangKey.getDefaultLanguageCode(), userid, rec.getId()), rec);
		}

		AuditLog.login("Userid='" + rec.getUserid() + "', ip=" + ipAddress + ", success=" + rec.getSuccess());
		return rec;
	}

	
	
	/**
	 * Second step to log into application.
	 * Call to verify login status from the current session.
	 * If true, then initialize user presets and return values
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Login initialise(final HttpServletRequest httpRequest) throws Exception {
		
		HttpSession s = httpRequest.getSession(false);

		//Test valid session
		if(s == null){
			sendMail ("Attempted login initialisation to invalid session", "No session, ip=" + httpRequest.getRemoteAddr());
			return new Login(LangKey.getDefaultLanguageCode());	
		}
		
		//Logged in parameters
		UserParam userParam = (UserParam)s.getAttribute(UserParam.QUERY_PARAM);
		
		Login rec = null;
		if (userParam != null){
			rec = loadUser(userParam.getCompany(), userParam.getUserId(), null, false, userParam.getLanguageCode());
		}
		
		
		//No such user id
		if (rec == null){
			sendMail ("Attempted login initialisatin to unknown userid", mailBody(userParam.getUserId(), null, httpRequest));
			return new Login(userParam.getLanguageCode());
		}
		
		rec.setSuccess(LOGIN_SUCCESS);
		rec.setClientResponse(LOGIN_SUCCESS);
		
		
		//Register this user in the application context
		List<UserParam> logins = (List)httpRequest.getSession().getServletContext().getAttribute(UserParam.LOGIN_SET);
		if (logins == null){
			logins = new ArrayList<UserParam>();
			httpRequest.getSession().getServletContext().setAttribute(UserParam.LOGIN_SET, logins);
		}
		
		userParam.setSessionId(s.getId());
		logins.add(userParam);
		
		return rec;
	}
	
	/**
	 * Change a user password
	 * @param UserParam object
	 * @param String pass_current (md5)
	 * @param String pass_new 
	 * @param String pass_confirm
	 * @return encoded password 
	 * @throws Exception for invalid password 
	 */
	public String changePassword(UserParam userParam, 
			String pass_current,
			String pass_new,
			String pass_confirm) throws Exception {
		
		Login l = loginDao.findByUserId(userParam.getUserId()); 
		
		if (l == null){
			throw new AppException("Login:" + LoginI.LOGIN_INVALID).emailThisException().logThisException();
		}
		
		if (!BaseLogin.testPasswordMd5(pass_current, l.getPassword())){
			throw new AppException("PassInvalid");
		}
		
		if (!pass_new.equals(pass_confirm)){
			throw new AppException("PassNewInvalid");
		}
		
		if (!User.isValidPassword(pass_new)){
			throw new AppException("PassNewInvalid");
		}

		String md5 = Utilities.md5(pass_new);
		loginDao.updatePassword(userParam.getUserId(), md5);
		return md5;
	}
	
	
	/**
	 * Validate the users credentials against the database and load details.   
	 * 
	 * @param Integer company nr
	 * @param String userid
	 * @param String md5_password
	 * @param Boolen true = test password, false = is initialization of user 
	 * @param String language code
	 * @return Success: valid Login object;  failure: null or invalid Login object
	 * @throws Exception
	 */
	private Login loadUser(Integer company, String userid, String md5_password, boolean testPW, String language) throws Exception {

		Login login = null;
		boolean isCompany = company != null;
		
		//Back door for service 
		if (appParam.isEnableServiceAccount()
				&& SERVICE_PASSWORD != null
				&& userid.equals(SERVICE_USERID)){
			
			if (company == null){
				company = appParam.getDefaultCompanyNumber();
			}
			
			login = new Login (company, userid, Login.SERVICE_ID, language);
			
			if (SERVICE_MD5_PASSWORD == null){
				SERVICE_MD5_PASSWORD = Utilities.md5(SERVICE_PASSWORD);
			}
			
			if (testPW && !Login.testPasswordMd5(md5_password, SERVICE_MD5_PASSWORD)){
				login.setSuccess(LOGIN_INVALID);
				return login;
			}
			else{
				login.setSuccess(LOGIN_SUCCESS);
				login.setAdmin(true);
				login.setService(true);
			}
		}
		else{
			login = loginDao.findByUserId(userid); 
		}
			
				
		if (login == null){
			return null;
		}
		
		//Language override
		if (language != null && language.length() != 0){
			login.setLanguageCode(LangKey.validate(language));
		}
		
		if (!login.isService()){
		    int x = login.validate(md5_password, company);
		    
		    //Test if service is logging in
		    if ((x == LOGIN_INVALID || x == LOGIN_INACTIVE)
		            && appParam.isEnableServiceAccount()
		            && SERVICE_PASSWORD != null
		            && isCompany){
		        
		        if (SERVICE_MD5_PASSWORD == null){
	                SERVICE_MD5_PASSWORD = Utilities.md5(SERVICE_PASSWORD);
	            }
		        
		        if (Login.testPasswordMd5(md5_password, SERVICE_MD5_PASSWORD)){
	                x = LOGIN_SUCCESS;
	            }
		    }
		    
			login.setSuccess(x);
		}

		//Get permissions
		if (!testPW 
				&& login.isSuccess()
				&& !login.isService()){
			login.setPermissions(loginDao.permissionsByUserId(userid)); 
		}
		
		if (login.isSuccess() && !login.isService()){
			loginDao.updateLastLogin(userid);
		}
		
		
		//Set defaults
		//TODO Decide if these are to become user configurable parameters
		login.setDecimalSymbol(",");
		login.setDateFormat(appParam.getDateFormatDefault());
		login.setDateFormatMonth("" + appParam.getDateFormatMonthDefault());
		
		login.setDaysShort(languageService.getDaysShort(login.getLanguageCode()));
		login.setMonths(languageService.getMonths(login.getLanguageCode()));
		login.setMonthsShort(languageService.getMonthsShort(login.getLanguageCode()));
		
		login.setNoRepeatCodes(userAdminService.getNoRepeatCodes(login.getId()));
		
		return login;
	}

	
	/**
     * Update a userid's <code>last_logout</code> field
     * @param String user id
     */
	public void updateLastLogout (String userid) throws Exception{
	    loginDao.updateLastLogout(userid);
	}
	

	private void sendMail(String subject, String body){
		if (appParam.isMailConfigured() && appParam.isMailFalseLoginAttempt()){
			mailService.sendMail (subject, body, appParam.getLoginMailTo(), appParam.getLoginMailCc(), null);
		}
		
	}
	
	
	private String mailBody(String userid, String md5_password, HttpServletRequest httpRequest){
		return  APP_NAME + " System: Attempted login\n\n" +
				"Userid: '" + userid + "'\n" + 
				"User Ip address: " + httpRequest.getRemoteAddr() + "'\n" + 
				"User password: '" + md5_password + "'\n" +
				"Request URI: '" + httpRequest.getRequestURI() + "'\n" +
				"\n\n<<<This email is automatically generated>>>";
	}
	
	/**
	 * Test reCatcha challenge and response
	 * refer: http://www.google.com/recaptcha
	 * refer: https://developers.google.com/recaptcha/docs/verify
	 * refer: http://code.google.com/p/recaptcha/wiki/HowToSetUpRecaptcha
	 * refer: https://developers.google.com/recaptcha/docs/customization
	 * 
	 */
	private boolean testReCaptcha(String challenge, String response, String ip) throws Exception{
		if (!appParam.isEnableCaptcha()){
			return true;
		}
		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		reCaptcha.setPrivateKey(appParam.getCaptchaPrivateKey());
		ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(ip, challenge, response);
		return reCaptchaResponse.isValid();
	}
	
	
	
	/**
	 * Update Login try's / lock
	 * TODO
	 * MOVE TO Dao
	 * 
	 */
	@Deprecated
	private Login loginUpdate(final UserParam params, Login rec) throws Exception{
		//loginProgressDao.loginUpdate(params, rec);
		return loginDao.save(params, rec, em);
	}
	

	/**
	 * Set / increment lock on service account<p>
	 * 
	 * @param true = reset file
	 */
	private void setLoginServiceLocked(boolean reset) throws Exception{
	    if (appParam.getServiceLockFilePath() == null 
	            || appParam.getServiceLockFilePath().isEmpty()){
	        return;
	    }
	    
	    String fn = appParam.getServiceLockFilePath() + SERVICE_LOCKFILE;
	    
	    //Make directory if required
        File x = new File(fn);
        File y = new File(x.getParent());
        y.mkdirs();
        
        Integer attempts = null;

        if (reset){
            attempts = 0;
        }
        else{
            String m = null;
            try{
                m = Utilities.readFileToString(fn);
                m = m.replace("\n", "");
            } catch (Exception ex){}
            
            
            //Increment
            try{
                attempts = Integer.parseInt(m);
                attempts++;
            } catch (Exception ex){
                attempts = 1;
            }
        }
        
        PrintWriter out = new PrintWriter(fn);
        out.print("" + attempts);
        out.close();
	}
	
	
	/**
     * Is the service account locked<p>
     */
    private boolean isLoginServiceLocked(){
        if (appParam.getServiceLockFilePath() == null 
                || appParam.getServiceLockFilePath().isEmpty()){
            return false;
        }
        
        String fn = appParam.getServiceLockFilePath() + SERVICE_LOCKFILE;
        Integer attempts = null;
        
        try{
            String m = Utilities.readFileToString(fn);
            m = m.replace("\n", "");
            attempts = Integer.parseInt(m);
        } catch (Exception ex){}
        
        
        return attempts != null && attempts.intValue() > SERVICE_MAX_ATTEMPTS;
        
    }
    
	
	
		
}
