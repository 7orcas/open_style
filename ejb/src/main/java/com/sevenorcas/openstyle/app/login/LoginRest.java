package com.progenso.desma.interfaces.rest.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;

import com.progenso.desma.ApplicationI;
import com.progenso.desma.app.ApplicationParameters;
import com.progenso.desma.app.anno.NoSessionRequired;
import com.progenso.desma.app.entities.ReturnDto;
import com.progenso.desma.app.entities.lang.LangKey;
import com.progenso.desma.app.entities.login.ChangePasswordDto;
import com.progenso.desma.app.entities.login.LoginI;
import com.progenso.desma.app.entities.useradmin.User;
import com.progenso.desma.app.entities.useradmin.UserDto;
import com.progenso.desma.app.interceptors.RestAroundInvoke;
import com.progenso.desma.entities.app.Company;
import com.progenso.desma.entities.app.Login;
import com.progenso.desma.entities.app.LoginDto;
import com.progenso.desma.entities.app.UserParam;
import com.progenso.desma.html.app.entities.login.ChangePasswordHtml;
import com.progenso.desma.service.app.ApplicationService;
import com.progenso.desma.service.app.CompanyService;
import com.progenso.desma.service.app.LanguageService;
import com.progenso.desma.service.app.LanguageServiceImp.Language;
import com.progenso.desma.service.app.LoginService;




/**
 * Login REST
 *
 * [License]
 * @author John Stewart
 */
@Stateless
@Path("/login")
@Produces({"application/json"})
@GZIP
@Consumes({"application/json"})
@HeaderDecoratorPrecedence
@Interceptors(RestAroundInvoke.class)
public class LoginRest {

    /** Application singleton */ 
    private ApplicationParameters appParam = ApplicationParameters.getInstance();
    
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(LoginRest.class);
	
	@EJB private LoginService       loginService;
	@EJB private LanguageService    langService;
	@EJB private CompanyService     companyService;
	@EJB private ApplicationService appService;
	
	
	/**
	 * Call weblogin.
	 * 
	 * @param fnr
	 * @param user
	 * @param pass
	 * @param language code from login
	 * @param language code was requested
	 * @param captch challenge
	 * @param captch response
	 * @return Login object
	 */
	@GET
	@Path("")
	@NoSessionRequired
	public LoginDto weblogin(
			@Context HttpServletRequest httpRequest,
			@QueryParam("forx") Integer nr,
			@QueryParam("fgh")  String user,
			@QueryParam("ts3")  String pass,
			@QueryParam("lan")  String language,
			@QueryParam("lanr") Boolean langRequest,
			@QueryParam("cha")  String challenge,
			@QueryParam("res")  String response) {
		
		Language l = null;

		
		//Test the application has a valid properties file
		if (!appParam.isValid()){
			
			//Encode response with default language
			try{
				l = langService.getLanguage(LangKey.getDefaultLanguageCode());
			} catch (Exception e){}

			LoginDto login = new LoginDto(LoginI.LOGIN_INVALID);
			login.setMessage("<div> No Application Properties File<br>" + (l != null? l.getLabel("SeeAdmin") : "See System Admin") + "</div>");
			return login;
		}
		
		
		//Test if User requested a language code
		String lang = langRequest != null && langRequest && language != null && language.equalsIgnoreCase(LangKey.validate (language))? 
				      LangKey.validate (language) : null; 
		
		Login login = null;
		try {
			login = loginService.login(nr, user, pass, lang, challenge, response, httpRequest);
		} catch (Exception e) {
			return new LoginDto(LoginI.LOGIN_INVALID);
	    }

		

		//Test valid and active company
		nr = nr != null? nr : login.getCompanyNr();
		try{
			if (!companyService.isValidCompanyNr(nr)){
				return new LoginDto(LoginI.LOGIN_INVALID);
			}
		} catch (Exception e) {
			return new LoginDto(LoginI.LOGIN_INVALID);
	    }
		
		
		LoginDto rec = new LoginDto(login);
		
		if (!rec.isSuccess()){
			
			//Encode response with users language
			try{
				l = langService.getLanguage(rec.getLanguage());
			} catch (Exception e){}
			
			String responseHtml = "<div>" + (l != null? l.getLabel("Login:" + (rec.getResponse() != null? rec.getResponse() : LoginI.LOGIN_INVALID)) : "");
			if (login.isLocked()){
				responseHtml = responseHtml + "<br>" + (l != null? l.getLabel("SeeAdmin") : "See System Admin");
			}
			
			rec.setMessage(responseHtml + "</div>");
		}
		else{
			rec.setLocationHref(appParam.getMainPage());
		}
		
		if (rec.isSuccess()) {
			// create session if does not already exist
			HttpSession s = httpRequest.getSession(true);
			
			UserParam u = new UserParam(login);
			u.setLoginDateTime(System.currentTimeMillis());
			u.setHttpSession(s);
						
			//ToDo use a service / config to get correct user param object
			s.setAttribute(UserParam.QUERY_PARAM, u);
		}
		
		return rec;
	}
	
	
	/**
	 * Log out
	 */
	@GET
	@Path("/exit")
	public LoginDto logout(@QueryParam(UserParam.QUERY_PARAM) UserParam userParam, @Context HttpServletRequest httpRequest) {
		try{
		    loginService.logout(userParam, httpRequest);
		} catch (Exception e){}
		if (userParam.isService()){
		    try {
		        appService.closeSqlConnection(userParam);
		    } catch (Exception e) {}
		}
		return new LoginDto(LoginI.LOGIN_INVALID);
	}
	

	
	/**
	 * Call to initialize login.
	 * If true, then return user presets
	 * @param Client local to UTC time zone offset (in minutes)
	 * @return 
	 */
	@GET
	@Path("/initialise")
	public LoginDto initialise(@QueryParam(UserParam.QUERY_PARAM) UserParam params, 
	        @Context HttpServletRequest req,
	        @QueryParam("tzo") Integer timezoneOffset) {

		if(req.getSession(false) == null){
			return new LoginDto(LoginI.LOGIN_INVALID);
		}
		
		try {
			
			Login login = null;
			try {
				login = loginService.initialise(req);
			} catch (Exception e) {
				return new LoginDto(LoginI.LOGIN_INVALID);
		    }

			LoginDto rec = new LoginDto(login);
			
			//Get client / server time zone offset
			try{
			    Date ds = new Date();
			    rec.setTimezoneOffset(TimeZone.getDefault().getOffset(ds.getTime()) + (timezoneOffset * 60 * 1000));
			}
			catch (Exception x){
			    rec.setTimezoneOffset(0);
			}
			
						
			//Store user permissions
			HttpSession s = req.getSession(true);
			UserParam u = (UserParam)s.getAttribute(UserParam.QUERY_PARAM);
			u.setPermissions(login.getPermissions());
			s.setAttribute(UserParam.QUERY_PARAM, u);
			
			
			//Client can use this value to stop automatic timeout of session
			if (appParam.getHeartbeat() > 0){
				rec.setHeartbeatTimer(appParam.getHeartbeat() * 60 * 1000); //minutes
			}
			//Client can use this value to test session timeout
            if (appParam.getSessionTimeoutTest() > 0){
                rec.setSessionTimeoutTest(appParam.getSessionTimeoutTest() * 1000); //seconds
            }
			
            Company c = companyService.findByNr(params, params.getCompany());
            rec.setTestCompany(c.isTestCompany());
            
            if (c.isHelpFileRoot()){
                rec.setHelpUrlRoot(c.getHelpFileRoot());
            }
            else{
                rec.setHelpUrlRoot(appParam.getHelpFileRoot());
            }
			
			if (params.isService()){
			    rec.setCompanyCode(c.getCompanyNr() + ":" + c.getCode());
			    rec.setRootContext(rec.getRootContext() + ApplicationI.SERVICE_INDEX_PAGE_EXT);
			    rec.setDebugMode(appParam.isDebug());
			}
			
			return rec;
		} catch (Exception e) {
			return new LoginDto(LoginI.LOGIN_INVALID);
		}
	}
	
	/**
	 * Change a user password
	 * @param UserParam object
	 * @param String pass_current (md5)
	 * @param String pass_new (md5)
	 * @param String pass_confirm (md5)
	 * @return
	 */
	@GET
	@Path("/changepassword")
	public ReturnDto changePassword(@QueryParam(UserParam.QUERY_PARAM) UserParam userParam, 
			@QueryParam("ts1") String pass_current,
			@QueryParam("ts2") String pass_new,
			@QueryParam("ts3") String pass_confirm) throws Exception{
		
		//If pass_current is null then this is a call to get model
		if (pass_current != null){
			loginService.changePassword(userParam, pass_current, pass_new, pass_confirm);
			return new ReturnDto("OK");
		}
		
		ChangePasswordDto c = new ChangePasswordDto();
		c.setInclude(User.PASSWORD_TO_INCLUDE);
		return new ReturnDto(c);
	}
	
	
	/**
	 * Call to get a change password message partial html view.
	 * 
	 * @param standard parameters
	 * @return HTML
	 */
	@GET
	@Produces({"text/html;charset=UTF-8"})
	@Path("/changepassword/message")
	public String includeChangepasswordMessage(@QueryParam(UserParam.QUERY_PARAM) UserParam userParam) throws Exception {
		return ChangePasswordHtml.getView(userParam.getLanguageCode());
	}
	

	/**
	 * Return ReCaptcha public key
	 */
	@GET
	@Path("/publickey")
	@NoSessionRequired
	public LoginDto recaptchaPublicKey(@Context HttpServletRequest req) {
		LoginDto rec = new LoginDto(null);
		rec.setCaptchaPublicKey(loginService.recaptchaPublicKey(req));
		rec.setResponse(LoginI.LOGIN_INVALID);
		return rec;
	}

	
	/**
	 * Receive a heart-beat to keep user logged in
	 */
	@GET
	@Path("/heartbeat")
	public ReturnDto heartbeat(@Context HttpServletRequest httpRequest) {
		return new ReturnDto("OK");
	}
	
	
	
	/**
	 * Return list of logged in users
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GET
	@Path("/logins")
	public ReturnDto logins(@QueryParam(UserParam.QUERY_PARAM) UserParam userParam, @Context HttpServletRequest req) {
		
		try {
			if (!userParam.isAdmin()){
				throw new Exception("NoPermission");
			}
			
			List<UserParam> logins = (List)req.getSession().getServletContext().getAttribute(UserParam.LOGIN_SET);
			if (logins == null){
				return new ReturnDto(new ArrayList<>());
			}
			
			ArrayList<UserDto> users = new ArrayList<>();
			for (UserParam p: logins){
				users.add(new UserDto(p));
			}
			
			return new ReturnDto(users);
			
		} catch (Exception e) {
			return new ReturnDto(e);
		}
	}
	
	
}

