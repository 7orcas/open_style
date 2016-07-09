package com.progenso.desma.service.app;

import java.util.List;

import javax.ejb.Local;

import com.progenso.desma.entities.app.Company;
import com.progenso.desma.entities.app.CompanyDto;
import com.progenso.desma.entities.app.CompanySql;
import com.progenso.desma.entities.app.UserParam;

/**
 * Local interface to Company Service
 *
 * [License]
 * @author John Stewart
 */

@Local
public interface CompanyService {
	public List<Company> list (UserParam userParam, CompanySql sql) throws Exception;
	public List<CompanyDto> listDto (UserParam params, CompanySql sql) throws Exception;
	
	public Company findById (UserParam params, Long id) throws Exception;
	public Company findByCode (UserParam params, String code) throws Exception;
	public Company findByNr (UserParam params, Integer nr) throws Exception;
	public Company findByNr_NoEntityManager (UserParam params, Integer nr) throws Exception;
	
	public List<Company> save (UserParam params, List<Company> list) throws Exception;
	public void cache (Company company) throws Exception;
	public boolean isCache (Long id) throws Exception;
	
	public boolean isValidCompanyNr (Integer nr) throws Exception;
	
}
