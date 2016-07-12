package com.sevenorcas.openstyle.app.company;

import java.util.List;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.repo.EntityRepoI;
import com.sevenorcas.openstyle.app.user.UserParam;

/**
 * Local Company Repository interface  
 * 
 * [License] 
 * @author John Stewart
 */
@Local
public interface CompanyDao extends EntityRepoI{
	public List<Company> list (UserParam params, CompanySql search) throws Exception;
	public Company findById (UserParam params, Long id) throws Exception;
	public Company findByCode (UserParam params, String code) throws Exception;
	public Company findByNr (UserParam params, Integer nr) throws Exception;
	public Company findByNr_NoEntityManager (UserParam params, Integer nr) throws Exception;
	public Company save (UserParam params, Company company) throws Exception;
	public void cache (Company company) throws Exception;
	public boolean isCache (Long id) throws Exception;
	public boolean isValidCompanyNr (Integer nr) throws Exception;
}
