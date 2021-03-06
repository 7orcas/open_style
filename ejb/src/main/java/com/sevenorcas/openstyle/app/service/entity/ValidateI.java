package com.sevenorcas.openstyle.app.service.entity;

import com.sevenorcas.openstyle.app.service.entity.ValidationException;

/**
 * Validation Interface<p>
 * 
 * Objects that implement <b>this</b> interface will be validated via their <code>isValid()</code> method. 
 * 
 * [License]
 * @author John Stewart
 */
public interface ValidateI {
	
	/**
	 * Implementing classes must provide a <code>validate</code> method.<br>
	 * When called, if the object is not valid then a <code>ValidationException</code> is thrown.
	 * @see ValidationException 
	 */
	public void validate() throws ValidationException;

}
