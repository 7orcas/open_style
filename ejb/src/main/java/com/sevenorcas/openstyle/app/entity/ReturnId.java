package com.sevenorcas.openstyle.app.entity;

import com.sevenorcas.openstyle.app.ApplicationI;


/**
 * Class to return to database id's that match specifically defined values.<p>
 * 
 * The values are defined by the implementing class that uses <b>this</b> object.   
 *  
 * [License] 
 * @author John Stewart
 */
public class ReturnId implements ApplicationI{
	
	
	/**
	 * ID Field.<p>
	 * @see ApplicationI
	 */
	private Long id;
	
	/**
	 * Record values (used to find the corresponding id's)
	 */
	private Object[] values;
	
	
	/**
	 * Default constructor<p> 
	 */
	public ReturnId (Long id, Object[] values){
		this.id = id;
		this.values = values;
	}
	
		
    //////////////////////Getters / Setters //////////////////////////////////	

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}


	public Object[] getValues() {
		return values;
	}
	public void setValues(Object[] values) {
		this.values = values;
	}
	
	
	

}
