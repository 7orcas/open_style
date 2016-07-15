package com.sevenorcas.openstyle.app.entity;

import java.util.Hashtable;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.dto.FieldDefDto;


/**
 * Local interface to Entity Validation Service
 *
 * [License]
 * @author John Stewart
 */
@Local
public interface ValidationService {
	public void validate(Object obj) throws ValidationException, Exception;
	public ValidationException validate(Object obj, Hashtable<String, FieldDefDto> fields, ValidationException ex) throws Exception;
}
