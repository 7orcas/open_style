package com.sevenorcas.openstyle.app.temptable;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.sevenorcas.openstyle.app.user.UserParam;


/**
 * Temporary Table Service
 *  
 * [License] 
 * @author jt
 */
@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class TempTableServiceImp implements TempTableService {
	
	@EJB private TempTableDao  tempTableDao;
	
	public TempTableServiceImp() {
	}
	
	
	
    /**
     * Return a new temporary table (but don't register)
     * @param UserParam object  
     * @param String table suffix
     * @return
     */
    public String getNameNoRegister (UserParam params, String suffix) throws Exception{
    	return tempTableDao.getNameNoRegister(params, suffix);
    }

    /**
     * Return a new temporary table (but don't register)
     * @param Integer company number (can be null)
     * @param String user id (can be null)
     * @param Long table id 
     * @param String table suffix
     * @return
     * @return
     */
    public String getNameNoRegister (Integer companyNr, String userId, Long id, String suffix) throws Exception{
        return tempTableDao.getNameNoRegister(companyNr, userId, id, suffix);
    }
    
    /**
     * Test if the passed in temporary table exists 
     * @param String table name
     * @return
     */
    public boolean exists(String table) throws Exception{
        return tempTableDao.exists(table);
    }

    /**
     * Drop a temporary table and remove from register
     * @param String table name
     * @return
     */
    public void drop (String table) throws Exception{
        if (table != null){
            tempTableDao.drop(table);
        }
    }
    
    
    /**
     * Drop all temporary tables for the passed in user's company number
     * @param String table name
     * @return
     */
    public void dropAllTempTables (UserParam params) throws Exception{
        tempTableDao.dropAllTempTablesByCompNr(params);
    }
}
