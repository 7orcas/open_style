package com.sevenorcas.openstyle.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Structure DTO to send messages to client.<p>
 * 
 * This object is included in the <code>ReturnDto</code> object.
 * 
 * [License]
 * @author John Stewart
 */
//WF10 TODO @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ReturnMessageDto {

	@JsonProperty(value="m")  public String message;
	@JsonProperty(value="d")  public String detail;
	
	public ReturnMessageDto(String message) {
		this.message = message;
	}

	
	//////////////// Getters / Setters ////////////////////////////////
	@JsonIgnore
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	@JsonIgnore
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
}
