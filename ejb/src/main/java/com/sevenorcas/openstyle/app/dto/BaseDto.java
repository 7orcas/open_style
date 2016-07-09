package com.sevenorcas.openstyle.app.dto;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.sevenorcas.openstyle.app.AppException;
import com.sevenorcas.openstyle.app.ApplicationI;
import com.sevenorcas.openstyle.app.ApplicationParameters;
import com.sevenorcas.openstyle.app.Utilities;
import com.sevenorcas.openstyle.app.entity.BaseEntity;
import com.sevenorcas.openstyle.app.entity.Field;
import com.sevenorcas.openstyle.app.user.UserParam;


/**
 * <code>BaseDto</code> contains essential fields, attributes and methods common to all DTOs (Data Transfer Object).<p>
 * 
 * <b>This</b> class is intended to be lightweight.   
 *  
 * @see Utilities
 *  
 * [License] 
 * @author John Stewart
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BaseDto implements ApplicationI{

	//Here for convenience
	protected ApplicationParameters appParam = ApplicationParameters.getInstance();
	
	
	/**
	 * ID Field.<p>
	 * @see ApplicationI
	 */
	@Field (appType=FIELD_TYPE_ID)
	@JsonProperty(value="i")  
	public Long id;
	
	/**
	 * ID Field.<p>
	 * @see ApplicationI
	 */
	@Field (appType=FIELD_TYPE_ID)
	@JsonProperty(value="id_dto")  
	public Long id_dto;
	
	/** 
	 * Delete record control (only sent by client) 
	 */ 
	@JsonProperty 
	@JsonIgnore 
	public Boolean delete;
	
	
	/**
	 * Default constructor<p> 
	 * 
	 * All DTOs should contain the following fields:<ul>
	 *     <li>id: Unique identifier is essential for client processing.</li>
	 * </ul>
	 * 
	 * @param EntityI entity
	 */
	public BaseDto (BaseEntity entity){
		if (entity != null){
		    setId(entity.getId());
		}
		setDtoFields (this, entity);		
	}
	
	

	/**
	 * Update the passed in <code>Entity</code> object with <b>this</b> DTOs values
	 * 
	 * @param UserParam object 
	 * @param BaseEntity entity
	 * @throws NoPermissionException
	 */
	public void setEntityFields(UserParam params, BaseEntity entity) throws AppException{
		Utilities.setEntityFields (params, this, entity);
		Utilities.setStandardFields(params, entity);
	}

	


	/**
	 * Update the passed in dto with the passed in entity.<p>
	 * 
	 * Notes:<ul>
	 *     <li>reflection is used to match fields between both objects<li>
	 *     <li>fields in <code>Dto</code> object do not include any fields from <code>super</code> objects</li>
	 *     <li><code>List</code> fields are not updated</li>
	 * </ul>
	 *  
	 * @param Object dto
	 * @param BaseEntity entity
	 */
	@JsonIgnore
	protected void setDtoFields (Object dto, BaseEntity entity){
		Utilities.setFields (entity, dto);
	}

	/**
	 * Convenience method to split a string.<p>
	 * 
	 * @param string
	 * @return String array
	 */
	@JsonIgnore
	public String [] split(String string){
		if (string == null || string.trim().length() == 0){
			return new String [] {};
		}
		string = string.trim();
		
		while (string.startsWith(",")){
			string = string.length() == 1? "" : string.substring(1);
		}
		while (string.endsWith(",")){
			string = string.substring(0,string.length()-1);
		}
		
		String [] s = string.split(",");
		for (int i=0; i<s.length; i++){
			s[i] = s[i].trim(); 
		}
		return s;
	}
	
    //////////////////////Getters / Setters //////////////////////////////////	

	@JsonIgnore
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	@JsonIgnore
	public Long getId_dto() {
		return id_dto;
	}
	public void setId_dto(Long id_dto) {
		this.id_dto = id_dto;
	}



	@JsonIgnore
	final public boolean isNew() {
		return id != null && id.longValue() < 0;
	}
	
	
	@JsonIgnore
	final public Boolean getDelete() {
		return delete;
	}
	
	/**
	 * Allow deserialization
	 * @param delete
	 */
	final public void setDelete(Boolean delete) {
		this.delete = delete;
	}
	@JsonIgnore
	final public boolean isDelete(){
		return delete != null && delete;
	}
	@JsonIgnore
	final public void setDelete(){
		delete = true;
	}
	
	
}
