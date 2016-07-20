package com.sevenorcas.openstyle.app.mod.user;

import java.io.Serializable;

import com.sevenorcas.openstyle.app.mod.login.Login;
import com.sevenorcas.openstyle.app.service.dto.Dto;


/**
 * Application user parameters.  
 * 
 * [License] 
 * @author John Stewart
 */ 
@Dto(validate=false)
@SuppressWarnings("serial")
public class UserParam extends BaseUserParam implements Serializable {
	
	/**
	 * JSon string constructor
	 * @param json
	 */
	public UserParam(String json) {
		super(json);
	}
	
	/**
	 * Constructor for successful login
	 * @param Login object
	 */
	public UserParam(Login obj) {
		super(obj);
	}
	
	/**
	 * Convenience constructor (used for services)
	 * @param company
	 * @param lang
	 * @param userid
	 * @param user_id
	 */
	public UserParam(Integer company, String lang, String userid, Long user_id) {
		super(company, lang, userid, user_id);
	}
	

	public String toJson(boolean includeEndBraces) {
		String x =  (includeEndBraces?"{":"") +
				super.toJson(false) + 
				(includeEndBraces?"}":"");
		
		return x;
	}
	
    
    
}
