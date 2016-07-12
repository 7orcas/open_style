package com.sevenorcas.openstyle.app.entity;

import com.sevenorcas.openstyle.app.AppException;

/**
 * Exception identifier deletion of entity is not possible due to other referencing entities.<p>
 * 
 * @see EntityRef
 * 
 * [License]
 * @author John Stewart
 */
@SuppressWarnings("serial")
public class ValidationReferenceException extends AppException{

	/** Referencing entities */  private String entities;
	
	public ValidationReferenceException(String entities) {
		super();
		this.entities = entities;
	}

	public String getEntities() {
		return entities;
	}
	public void setEntities(String entities) {
		this.entities = entities;
	}
		
	
	
}
