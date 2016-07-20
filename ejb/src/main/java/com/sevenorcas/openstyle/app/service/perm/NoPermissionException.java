package com.sevenorcas.openstyle.app.service.perm;

import com.sevenorcas.openstyle.app.application.exception.AppException;

/**
 * Exception identifier if user does not have permission to action a method.<p>
 * 
 * [License]
 * @author John Stewart
 */
@SuppressWarnings("serial")
public class NoPermissionException extends AppException{

	/** Method name       */  private String method;
	/** Permission key    */  private String key;
	/** Permission value  */  private String value;
	
	public NoPermissionException(String method) {
		super();
		logThisException();
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

		
	
	
}
