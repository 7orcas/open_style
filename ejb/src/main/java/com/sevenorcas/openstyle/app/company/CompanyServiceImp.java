package com.progenso.desma.service.app;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import com.progenso.desma.app.anno.Permission;
import com.progenso.desma.app.interceptors.ServiceAroundInvoke;
import com.progenso.desma.entities.app.Company;
import com.progenso.desma.entities.app.CompanyDto;
import com.progenso.desma.entities.app.CompanySql;
import com.progenso.desma.entities.app.UserParam;
import com.progenso.desma.repo.app.CompanyDao;
import com.progenso.desma.service.app.LanguageServiceImp.Language;


/**
 * Company Master Data Service
 *  
 * [License] 
 * @author John Stewart
 */
@Stateless
@Interceptors(ServiceAroundInvoke.class)
public class CompanyServiceImp implements CompanyService {
	
	@EJB private LanguageService   languageService;
	@EJB private CompanyDao        companyDao;
	
	public CompanyServiceImp() {
	}
	
	
	/**
	 * Retrieve list of companies.
	 * @param UserParam object
	 * @param Company Sql object
	 */
	public List<Company> list (UserParam params, CompanySql sql) throws Exception {
		return companyDao.list(params, sql == null? new CompanySql(params) : sql);
	}
	
	/**
	 * Retrieve list of companies as DTO's.
	 * @param UserParam object
	 * @param Comnpany Sql search object
	 */
	public List<CompanyDto> listDto (UserParam params, CompanySql sql) throws Exception {
		List<Company> list = list(params, sql);
		Language l = languageService.getLanguage(params.getLanguageCode());
		
		List<CompanyDto> listX = new ArrayList<>();  
		for (Company entity: list){
		    listX.add(new CompanyDto(entity, l));
		}
		return listX;
	}
	
	
	/**
	 * Save company records.
	 * @param UserParam object
	 * @param List of Company objects to save
	 */
	@Permission(service=true)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<Company> save (UserParam params, List<Company> list) throws Exception {
		List<Company> listX = new ArrayList<Company>();
		for (Company p: list){
			listX.add(save(params, p));
		}
		return listX;
	}
	
	/**
	 * Save new company record.
	 * @param UserParam object
	 * @param Company object
	 */
	@Permission(service=true)
	public Company save (UserParam params, Company recode) throws Exception {
		return companyDao.save(params, recode);
	}

    /**
	 * Find an entity by its ID
	 * @param UserParam object
	 * @param Long id
	 * @return
	 */
	public Company findById (UserParam params, Long id) throws Exception{
		return companyDao.findById(params, id);
	}

	/**
	 * Find an entity by its code 
	 * @param UserParam object
	 * @param String code
	 * @return
	 */
	public Company findByCode (UserParam params, String code) throws Exception{
		return companyDao.findByCode(params, code);
	}
	
	/**
	 * Find an entity by its number
	 * @param UserParam object
	 * @param Integer nr
	 * @return
	 */
	public Company findByNr (UserParam params, Integer nr) throws Exception{
		return companyDao.findByNr(params, nr);
	}

	/**
     * Find an entity by its number  (without using entity manager)
     * @param UserParam object
     * @param Integer nr
     * @return
     */
    public Company findByNr_NoEntityManager (UserParam params, Integer nr) throws Exception{
        return companyDao.findByNr_NoEntityManager(params, nr);
    }
	
    /**
     * Save company record in cache
     * @param company
     * @return
     */
    public void cache (Company company) throws Exception{
    	companyDao.cache(company);
    }

    /**
     * Is the company record in cache?
     * @param company_id
     * @return
     */
    public boolean isCache (Long id) throws Exception{
    	return companyDao.isCache(id);
    }
		
    /**
	 * Is the passed in company number valid
	 * @param Integer nr
	 * @return
	 */
	public boolean isValidCompanyNr (Integer nr) throws Exception{
		return companyDao.isValidCompanyNr(nr);
	}
    
}