package com.sevenorcas.openstyle.app.dto;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.company.Company;
import com.sevenorcas.openstyle.app.user.UserParam;



/**
 * Local interface to Definitions Service
 *
 * [License]
 * @author John Stewart
 */
@Local
public interface DefinitionService {
	public ArrayList<FieldDefDto> definitions(String classname, UserParam userParam) throws Exception;
	public Hashtable<String, FieldDefDto> definitionsTable(String classname, UserParam userParam, Company company) throws Exception;
}
