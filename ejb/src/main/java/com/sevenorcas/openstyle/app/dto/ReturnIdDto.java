package com.sevenorcas.openstyle.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.ApplicationI;
import com.sevenorcas.openstyle.app.entity.Field;


/**
 * Class to return to client the client generated id and the database id match for a single object.<p>
 * 
 * I.e. when a client creates a record it will assign a temporary (negative) id. Once the record is saved a permanent
 * (non-negative) id is created. <b>This</b> class will hold both of these ids for the client to process.<p>
 * 
 * TODO: Future extension could also return the full DTO (maybe some configuration is required for new objects)
 *  
 * [License] 
 * @author John Stewart
 */
//WF10 TODO @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ReturnIdDto implements ApplicationI{
	
	
	/**
	 * ID Field.<p>
	 * @see ApplicationI
	 */
	@Field (appType=FIELD_TYPE_ID)
	@JsonProperty(value="id")  
	public Long id;
	
	/**
	 * ID Field.<p>
	 * @see ApplicationI
	 */
	@Field (appType=FIELD_TYPE_ID)
	@JsonProperty(value="id_dto")  
	public Long id_dto;
	
	
	/**
	 * Default constructor<p> 
	 */
	public ReturnIdDto (Long id, Long id_dto){
		this.id = id;
		this.id_dto = id_dto;
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

}
