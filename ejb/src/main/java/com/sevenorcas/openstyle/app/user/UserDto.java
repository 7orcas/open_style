package com.sevenorcas.openstyle.app.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.AppException;
import com.sevenorcas.openstyle.app.dto.BaseDto;
import com.sevenorcas.openstyle.app.dto.Dto;
import com.sevenorcas.openstyle.app.entity.Field;
import com.sevenorcas.openstyle.app.lang.LangKey;

/**
 * User entity data transfer object<p>
 *  
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
@Dto(entity="User")
public class UserDto extends BaseDto implements Serializable {

	@JsonProperty(value="a")  public String userid;
	@JsonProperty(value="b")  public String password;
	@JsonProperty(value="c")  @Field(edit="true", notNull=true) public Integer companyNr;
	@JsonProperty(value="d")  public Boolean admin;
	@JsonProperty(value="e")  public String companyNrs;
	@JsonProperty(value="f")  public String languageCode;
	@JsonProperty(value="g")  public String site;
	@JsonProperty(value="h")  public Boolean locked;
	@JsonProperty(value="j")  public Integer trys;
	@JsonProperty(value="k")  public String groups;
	@JsonProperty(value="l")  public String parameters;
	@JsonProperty(value="m")  public Date lastLogin;
	@JsonProperty(value="n")  public Date lastLogout;
	
	
	@JsonProperty(value="z")
	@Field(childClass="useradmin.UserRoleDto")
	public List<UserRoleDto>roles = new ArrayList<>();
	
	/**
	 * Default Constructor
	 */
	public UserDto() {
		super(null);
	}
	
	
	/**
	 * Entity Constructor
	 * @param LangKey entity
	 */
	public UserDto(User entity) {
		super(entity);
		
		for (UserRole rec: entity.getRoles()){
			roles.add(new UserRoleDto(rec));
		}
	}

	/**
	 * Constructor via <code>BaseUserParam</code>.
	 * @param BaseUserParam logged in user parameters
	 */
	public UserDto(BaseUserParam p) {
		super(null);
		userid = p.getUserId();
	}
	
	
	
	/**
	 * Update the passed in <code>Entity</code> object with <b>this</b> DTOs values
	 * @param UserParam object
	 * @param LangKey object to update
	 */
	//@Override
	public void update(UserParam userParam, User entity) throws AppException{
		//Password is a special case
		String p = entity.getPassword();
		entity.setPasswordUpdate(password != null || entity.isNew());
		
		setEntityFields (userParam, entity);
		
		if (!entity.isPasswordUpdate()){
			entity.setPassword(p);
		}
		
		String lang = LangKey.validate(languageCode);
		if (lang.equals(languageCode)){
			entity.setLanguageCode(lang);
		}
		
		
		for (UserRoleDto dto: roles){
			UserRole role = entity.findUserRoleByRoleId(dto.role_id);
			if (role == null){
				role = new UserRole();
				role.setRoleId(dto.role_id);
				entity.addUserRole(role);
			}
		}
		
		entity.validate();
	}
	
	/**
	 * Unlock <b>this</b> record?
	 * @return
	 */
	public boolean unlock(){
		return locked == null || locked == false; 
	}


    public void setPassword(String password) {
        this.password = password;
    }
    public void resetDummyPassword() {
        if (password != null && password.equals(User.DUMMY_PASSWORD)){
            password = null;
        }
    }
	
	
}
