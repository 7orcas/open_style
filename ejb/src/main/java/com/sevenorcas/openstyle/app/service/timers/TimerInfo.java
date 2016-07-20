package com.sevenorcas.openstyle.app.service.timers;

import java.io.Serializable;

import com.sevenorcas.openstyle.app.mod.lang.LangKey;
import com.sevenorcas.openstyle.app.mod.login.LoginI;
import com.sevenorcas.openstyle.app.mod.user.UserParam;

/**
 * <code>TimerInfo</code> object to contain necessary timer configuration.<p>
 *  
 * [License]
 * @author John Stewart
 */
public class TimerInfo implements Serializable{
	private static final long serialVersionUID = 1L;

	/** Timers run against a company nr (0 == all)                */ private Integer companyNr;
	/** Timer task service ejb name                               */ private String service;
	/** Timer task method name to call                            */ private String method;
	/** Timer task method parameters to be passed in              */ private String parameters;
	/** Config string if timer is from application properties     */ private String config;
	/** Timer task run as service user                            */ private boolean serviceUser = false;
	/** Does time have valid service.method call?(tested on setup)*/ private boolean valid = true;
	/** Is this timer repeating?                                  */ private boolean repeat = false;
	/** Has this timer been cancelled?                            */ private boolean cancelled = false;
	
	public TimerInfo (Integer companyNr, String config){
		this.companyNr = companyNr;
		this.config    = config;
	}
	
	/**
	 * Return a log message id
	 * @return
	 */
	public String getLog(){
		if (config != null){
			return config;
		}
		return service 
				+ "." + method
				+ " (" + (parameters != null? parameters : "") + ")"
				+ "  - config=" + config;
	}
	
	
	/**
	 * Return a valid user parameter object for <b>this</b> timer
	 * @return
	 */
	public UserParam getUserParam(){
		UserParam param = new UserParam(companyNr, LangKey.getDefaultLanguageCode(), LoginI.SERVICE_USERID, LoginI.SERVICE_ID);
		param.setService(serviceUser);
		return param;
	}
	
	/**
	 * Return the timer ejb service short class name
	 * @return
	 */
	public String getServiceClassShortName() {
		int index = service != null? service.lastIndexOf(".") : -1;
		if (index != -1){
			return service.substring(index + 1);
		}
		return service;
	}
	
	
	/////////////////////////// Getters and Setters  //////////////////////////////////////////////////////

	/**
	 * Return the timer ejb service class name
	 * @return
	 */
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}

	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}

	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public Integer getCompanyNr() {
		return companyNr;
	}
	public void setCompanyNr(Integer companyNr) {
		this.companyNr = companyNr;
	}

	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean isServiceUser() {
		return serviceUser;
	}
	public void setServiceUser(boolean serviceUser) {
		this.serviceUser = serviceUser;
	}

	public boolean isRepeat() {
		return repeat;
	}
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public boolean isCancelled() {
		return cancelled;
	}
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	
}
