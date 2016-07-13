package com.sevenorcas.openstyle.app.login;

import java.io.Serializable;


/**
 * Application specific login object<p>
 *  
 * [License]
 * @author John Stewart
 */

@SuppressWarnings("serial")
public class LoginDto extends BaseLoginDto implements Serializable {

	
	public LoginDto(Login login) {
		super(login);
	}
	
	public LoginDto(int response) {
		super(response);
	}
	
    		
	
}
