package com.sevenorcas.openstyle.app.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * Generic Lookup Data Transfer Object.<p>
 *
 * <b>This</b> class is used to:<ul>
 *     <li>transfer look strings to the client</li>
 *     <li>send the selected string back to the client</li>
 * </ul><p>
 * 
 * @author John Stewart
 */
@SuppressWarnings("serial")
public class LookupDto implements Serializable {
	
	@JsonProperty(value="l")	public String lookup;
	
	
	/**
	 * Default Constructor
	 */
	public LookupDto() {
	}
	
	/**
	 * Lookup value Constructor
	 */
	public LookupDto(String lookup) {
		this.lookup = lookup;
	}
	
	
	@JsonIgnore
	public String getLookup() {
		return lookup;
	}
	public void setLookup(String lookup) {
		this.lookup = lookup;
	}
		
}
