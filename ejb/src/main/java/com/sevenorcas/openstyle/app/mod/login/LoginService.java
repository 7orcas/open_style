package com.sevenorcas.openstyle.app.mod.login;

import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;

import com.sevenorcas.openstyle.app.mod.user.UserParam;

/**
 * Local interface to Login Service
 *
 * [License]
 * @author John Stewart
 */
@Local
public interface LoginService {
	public Login login(Integer company, String userid, String password, String language, String challenge, String response, HttpServletRequest httpRequest) throws Exception;
	public Login initialise(HttpServletRequest httpRequest) throws Exception;
	public void logout(UserParam userParam, final HttpServletRequest httpRequest) throws Exception;
	public String changePassword(UserParam userParam, String pass_current, String pass_new, String pass_confirm) throws Exception; 
	public String recaptchaPublicKey(HttpServletRequest httpRequest);
	public void updateLastLogout (String userid) throws Exception;
}
