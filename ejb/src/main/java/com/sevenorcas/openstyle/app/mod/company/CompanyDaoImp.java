package com.sevenorcas.openstyle.app.mod.company;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.repo.BaseDao;
import com.sevenorcas.openstyle.app.service.sql.ResultSetX;
import com.sevenorcas.openstyle.app.service.sql.StatementX;

/**
 * Company Repository 
 * 
 * [License] 
 * @author John Stewart
 */ 
@Stateless
public class CompanyDaoImp extends BaseDao implements CompanyDao{

	/**
	 * Persistence context corresponds to the persistence-unit in 
	 * ejb/src/main/resources/META-INF/persistence.xml
	 * 
	 * For multiple contexts see http://www.hostettler.net/blog/2012/11/20/multi-tenancy/ (this in not yet implemented)
	 */
	@PersistenceContext(unitName="openstyleDS")
	private EntityManager em;
   
	/**
	 * Default Constructor
	 */
	public CompanyDaoImp(){}
	
	
	/**
	 * Retrieve company list
	 * @param User object
	 * @param Company Sql object
	 * @return Company list
	 */
    public List<Company> list (UserParam params, CompanySql sql) throws Exception{
	    
        StatementX x = StatementX
				.create("SELECT t.code, t.customer_nr, t.config "
						+ "FROM " + T_COMPANY + " t "
						+ "ORDER BY " + (sql.isOrderByName()?"t.code":"t.comp_nr") + " ")
			    .addActive(sql, "t")
				.appendBaseEntityFields("t");
		
        if (sql.isIgnore0()){
            x.addWhere("id <> " + MISSING_REFERENCE_ID);
        }
        
		ResultSetX rs = x.executeQuery(sql); 
		
		List<Company> list = new ArrayList<Company>();
		
		while(rs.next()){
			Company m = new Company();
			list.add(m);
			
			int count = 1;
			
			m.setCode(rs.getString(count++));
			m.setCustomerNr(rs.getInt(count++));
			m.setConfig(rs.getString(count++));
			m.decode(m.getConfig());
			rs.setBaseEntityFields(m, "t");

			initialise(params, m);
		}
				
		return list;
	}
	
    /**
	 * Is the passed in company number valid
	 * @param Integer number
	 * @return
	 */
	public boolean isValidCompanyNr (Integer nr) throws Exception{
		try{
			ResultSetX rs = StatementX.create("SELECT t.active "
					+ "FROM " + T_COMPANY + " t "
					+ "WHERE comp_nr = " + nr)
					.executeQuery();
			
			while(rs.next()){
				boolean b = rs.getBoolean(1);
				return b;
			}
			
		}
		catch (Exception e){}
				
		return false;
	}
    
	
    /**
     * Save new / update company record
     * @param UserParam object
     * @param company
     * @return
     */
    public Company save (UserParam params, Company company) throws Exception{
    	
    	boolean isNew = company.isNew();
    	Integer delete = company.isDelete()? company.getCompanyNr() : null;
    	
    	company.setConfig(company.encode());
    	Company rec = super.save(params, company, em);
    	if (rec != null){
    	    initialise(params, rec);
    	    
	    	if (isNew){
			    createSequences(params, rec.getCompanyNr());
			}
    	}
    	else{
    		deleteSequences (params, delete);
    	}
    	return rec;
    }
    
    
    /**
     * Initialize the passed in record
     * @param rec
     */
    private void initialise(UserParam params, Company rec) throws Exception{
        rec.decode(rec.getConfig());
        cache(rec);
    }
    
    /**
     * Create sequences for the passed in company number
     * @param UserParam object
     * @param Integer company number
     * @return
     */
    private void createSequences (UserParam params, Integer comp_nr) throws Exception{
    	
    	StringBuffer sb = new StringBuffer();
    	for (String s: SEQUENCE_COMP_NRS){
    		sb.append("CREATE SEQUENCE " + s + comp_nr + " "
    				+ "INCREMENT 1 "
    				+ "MINVALUE 1 "
    				+ "MAXVALUE 9223372036854775807 "
    				+ "START 1 "
    				+ "CACHE 1;");
    	}
    	
    	StatementX.create(sb.toString()).executeUpdate();
	}
    
    /**
     * Delete sequences for the passed in company number
     * @param UserParam object
     * @param Integer company number
     * @return
     */
    private void deleteSequences (UserParam params, Integer comp_nr) throws Exception{
    	
    	StringBuffer sb = new StringBuffer();
    	for (String s: SEQUENCE_COMP_NRS){
    		sb.append("DROP SEQUENCE IF EXISTS " + s + comp_nr + ";");
    	}
    	
    	StatementX.create(sb.toString()).executeUpdate();
		
    }
    
	
    /**
     * Save company record in cache
     * @param company
     * @return
     */
    public void cache (Company company) throws Exception{
    	cache.putCompany(company);
    }
    
    /**
     * Is the company record in cache?
     * @param company_id
     * @return
     */
    public boolean isCache (Long id) throws Exception{
    	return cache.isCompany(id);
    }
    
    /**
     * Find an entity by its ID
     * @param UserParam object
     * @param Long id
     * @return
     */
    public Company findById (UserParam params, Long id) throws Exception{
        return findById (params, id, true);
    }
    
    
    /**
	 * Find an entity by its ID
	 * @param UserParam object
	 * @param Long id
	 * @param flag true = use entity manager
	 * @return
	 */
	public Company findById (UserParam params, Long id, boolean emf) throws Exception{
		Company rec = cache.getCompany(id);
		if (rec == null){
		    if (emf){
		        rec = super.findById(Company.class, id, em);
		    }
		    else{
		        rec = loadCompany(params, id);
		    }
		    
			if (rec != null){
			    initialise(params, rec);
			}
		}
		return rec;
	}

	/**
	 * Load a company object using SQL
	 * @param company id
	 * @return
	 */
	private Company loadCompany (UserParam params, Long id) throws Exception{
	    StatementX x = StatementX
                .create("SELECT t.id,t.comp_nr,t.customer_nr,t.code,t.config "
                        + "FROM " + T_COMPANY + " t ")
                .addWhere("id=" + id)
                .appendBaseEntityFields("t");
        
        ResultSetX rs = x.executeQuery(); 
        
        Company c = null;
        
        while(rs.next()){
            c = new Company();
            
            int count = 1;
            
            c.setId(rs.getLong(count++));
            c.setCompanyNr(rs.getInt(count++));
            c.setCustomerNr(rs.getInt(count++));
            c.setCode(rs.getString(count++));
            c.setConfig(rs.getString(count++));
            c.decode(c.getConfig());
            rs.setBaseEntityFields(c, "t");

            initialise(params, c);
        }
                
        return c;
	}
	
	/**
	 * Find an entity by its code 
	 * @param UserParam object
	 * @param String code
	 * @return
	 */
	public Company findByCode (UserParam params, String code) throws Exception{
		
		ResultSetX rs = StatementX
				.create("SELECT t.id FROM " + T_COMPANY + " t " 
					  + "WHERE t.code = '" + code + "' ")
				.executeQuery(null);
		
		Long id = null;
		
		while(rs.next()){
			id = rs.getLong(1);
		}
		
		return findById(params, id);
	}
	
	/**
     * Find an entity by its number 
     * @param UserParam object
     * @param String code
     * @return
     */
    public Company findByNr (UserParam params, Integer nr) throws Exception{
        return findByNr (params, nr, true);
    }
	
	/**
     * Find an entity by its number (without using entity manager) 
     * @param UserParam object
     * @param String code
     * @return
     */
    public Company findByNr_NoEntityManager (UserParam params, Integer nr) throws Exception{
        return findByNr (params, nr, false);
    }
	
	/**
	 * Find an entity by its number 
	 * @param UserParam object
	 * @param String code
	 * @param flag true = use entity manager
	 * @return
	 */
	private Company findByNr (UserParam params, Integer nr, boolean em) throws Exception{
		
	    Company rec = cache.getCompanyByNumber(nr);
        if (rec != null){
            return rec;
        }
	    
		ResultSetX rs = StatementX
				.create("SELECT t.id FROM " + T_COMPANY + " t " 
					  + "WHERE t.comp_nr = " + nr + " ")
				.executeQuery(null);
		
		Long id = null;
		
		while(rs.next()){
			id = rs.getLong(1);
		}
		
		if (id == null){
			return null;
		}
		
		return findById(params, id, em);
	}
	
	
}
