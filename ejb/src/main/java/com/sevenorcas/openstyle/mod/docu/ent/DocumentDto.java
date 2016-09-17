package com.sevenorcas.openstyle.mod.docu.ent;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.service.dto.BaseDto;
import com.sevenorcas.openstyle.app.service.dto.Dto;



/**
 * Document DTO<p>
 * 
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Dto
public class DocumentDto extends BaseDto implements Serializable {

	@JsonProperty(value="sc") public String [] sections;
	
	public DocumentDto(DocumentEnt entity) {
		super(entity);
		sections = new String [entity.getSections().size()];
		
		for (int i=0;i<entity.getSections().size();i++){
			sections[i] = entity.getSections().get(i).getText();
		}
		
	}

	
	
}
