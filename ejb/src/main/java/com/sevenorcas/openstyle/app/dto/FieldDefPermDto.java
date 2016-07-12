package com.sevenorcas.openstyle.app.dto;

import com.sevenorcas.openstyle.app.entity.Field;

/**
 * Entity permission definition object.<p>
 * 
 * Extended from the <code>FieldDefDto</code>, <b>this</b> object uses specific fields.<p>
 * 
 * Note this is a special field and is used in the model only. 
 *  
 * @see DefinitionServiceImp 
 * @see Field
 * 
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
//WF10 TODO @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FieldDefPermDto extends FieldDefDto {

	public FieldDefPermDto(String key, String value) {
		super("_", "Permission");
		setType(TYPE_STRING);
		setApplicationType(FIELD_TYPE_SYS_FIELD);
		setEdit("false");
		setModelOnly(true);
		setLabel(key);
		
		//Use this field to send CRUD value
		setNewRecordValue(value);
	}

}
