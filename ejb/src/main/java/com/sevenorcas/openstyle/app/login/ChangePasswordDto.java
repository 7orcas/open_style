package com.sevenorcas.openstyle.app.login;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.dto.BaseDto;
import com.sevenorcas.openstyle.app.dto.Dto;
import com.sevenorcas.openstyle.app.entity.Field;
import com.sevenorcas.openstyle.app.user.User;



/**
 * Change password data transfer object<p>
 *  
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Dto(entity="useradmin.User")
public class ChangePasswordDto extends BaseDto implements Serializable {

	@Field(edit="true", min=User.PASSWORD_MIN_LENGTH, max=User.PASSWORD_MAX_LENGTH) 
	@JsonProperty(value="a") 
	public String password;
	
	@Field(edit="true", min=User.PASSWORD_MIN_LENGTH, max=User.PASSWORD_MAX_LENGTH, accessor="passwordNew") 
	@JsonProperty(value="b") 
	public String password_new;
	
	@Field(edit="true", min=User.PASSWORD_MIN_LENGTH, max=User.PASSWORD_MAX_LENGTH, accessor="passwordConf") 
	@JsonProperty(value="c") 
	public String password_conf;
	
	@Field 
	@JsonProperty(value="e") 
	public String include;
	
	
	/**
	 * Default Constructor
	 */
	public ChangePasswordDto() {
		super(null);
	}

	@JsonIgnore
	public void setInclude(String include) {
		this.include = include;
	}

		
	
}
