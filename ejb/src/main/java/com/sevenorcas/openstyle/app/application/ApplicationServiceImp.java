package com.sevenorcas.openstyle.app.application;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.sevenorcas.openstyle.app.AppException;
import com.sevenorcas.openstyle.app.ApplicationI;
import com.sevenorcas.openstyle.app.ApplicationParameters;
import com.sevenorcas.openstyle.app.Utilities;
import com.sevenorcas.openstyle.app.company.CompanyService;
import com.sevenorcas.openstyle.app.perm.Permission;
import com.sevenorcas.openstyle.app.sql.StatementX;
import com.sevenorcas.openstyle.app.temptable.TempTableService;
import com.sevenorcas.openstyle.app.timers.TimerService;
import com.sevenorcas.openstyle.app.user.UserAdminService;
import com.sevenorcas.openstyle.app.user.UserParam;

/**
 * Application service<p>
 *  
 * Class to hold general application service functions.<p> 
 *
 * [License]
 * @author John Stewart
 */

@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class ApplicationServiceImp implements ApplicationService,  ApplicationI {
		
	@EJB private TimerService        timerService;
	@EJB private TempTableService    tempTableService;
	@EJB private UserAdminService    userAdminService;
	@EJB private CompanyService      companyService;
	
	private ApplicationParameters appParams = ApplicationParameters.getInstance();
	
	
	/** Default Constructor */
	public ApplicationServiceImp() {}
	
	
	/**
	 * Reload application properies file.<p>
	 * 
	 * @param UserParam parameters 
	 * @throws Exception  
	 */
	@Permission(service=true)
	public void reloadAppProp(UserParam params) throws Exception{
		ApplicationParameters.getInstance().reload();
		timerService.startTimers(params);
	}
	
	
	/**
     * Close Postgres SQL connection.<p>
     * 
     * @param UserParam parameters 
     * @throws Exception  
     */
    @Permission(service=true)
    public void closeSqlConnection(UserParam params) throws Exception{
        try{
            Connection c = StatementX.getConnection();
            c.close();
        }
        catch (Exception e){}
    }
	
	
    /**
     * Reset database (for presentations ONLY!!!).<p>
     * 
     * @param UserParam parameters 
     * @throws Exception  
     */
    @Permission(service=true)
    public void resetDatabase(UserParam params) throws Exception{
        if (!appParams.isDebugReset()){
            throw AppException.create("Invalid Call to reset")
                             .setDetailMessage("appParams.isDebugReset must be set")
                             .logAndEmailThisException()
                             .returnMessageToClient();
        }

        tempTableService.dropAllTempTables(params);
        userAdminService.deleteUserConfigImport(params);
        
        //Company c = companyService.findByNr(params, params.getCompany());
        //importService.importCsvLogi (params, c);
        //startMouldService.resetBackOrders(params);
        
        //Get next Monday
        Date d = new Date();
        int x = Utilities.getDayNumber(d);
        while (x != Calendar.SUNDAY){
            d = Utilities.addDaysToDate(d, 1);
            x = Utilities.getDayNumber(d);
        }
        
       
    }
    
    /**
     * Toggle debug flag.<p>
     * 
     * @param UserParam parameters 
     * @throws Exception  
     */
    @Permission(service=true)
    public boolean toggleDebug(UserParam params) throws Exception{
        try{
            appParams.setDebug(!appParams.isDebug());
        }
        catch (Exception e){}
        return appParams.isDebug();
    }
    
}
