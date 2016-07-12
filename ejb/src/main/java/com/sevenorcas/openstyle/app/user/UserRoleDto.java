package com.sevenorcas.openstyle.app.user;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.AppException;
import com.sevenorcas.openstyle.app.dto.BaseDto;
import com.sevenorcas.openstyle.app.dto.Dto;

/**
 * User-Role configuration entity data transfer object<p>
 *  
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Dto
public class UserRoleDto extends BaseDto implements Serializable {

	@JsonProperty(value="a")  public Boolean active;
	@JsonProperty(value="r")  public Long role_id;
		
	/**
	 * Default Constructor
	 */
	public UserRoleDto() {
		super(null);
	}
	
	
	/**
	 * Entity Constructor
	 * @param entity
	 */
	public UserRoleDto(UserRole entity) {
		super(entity);
		role_id = entity.getRoleId();
	}
	
	
	/**
	 * Client initializations for <b>this</b> dto 
	 */
	@Dto(init=true)
	public void initialise(){
		active = true;
	}
	
	
	/**
	 * Update the passed in <code>Entity</code> object with <b>this</b> DTOs values
	 * @param UserParam object
	 * @param object to update
	 */
	//@Override
	public void update(UserParam userParam, UserRole entity) throws AppException{
		setEntityFields (userParam, entity);
	}
	
	
}
