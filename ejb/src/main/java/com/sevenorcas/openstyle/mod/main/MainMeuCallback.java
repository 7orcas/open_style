package com.sevenorcas.openstyle.mod.main;

import com.sevenorcas.openstyle.app.application.Utilities;
import com.sevenorcas.openstyle.app.application.html.BaseCallback;
import com.sevenorcas.openstyle.app.mod.company.Company;
import com.sevenorcas.openstyle.app.mod.company.CompanyService;
import com.sevenorcas.openstyle.app.mod.user.UserParam;

/**
 * Main html page call back<p> 
 *    
 * [License]
 * @author John Stewart
 */
public class MainMeuCallback extends BaseCallback {

    final static private String SIMU_START      = "group-simu:start"; 
    final static private String SIMU_END        = "group-simu:end";
    final static private String SIMU_ITEM       = "group-simu:item";
    final static private String PERIOD_START    = "group-period:start"; 
    final static private String PERIOD_END      = "group-period:end";
	final static private String LAST_START      = "group-last:start"; 
	final static private String LAST_END        = "group-last:end";
	final static private String PLANT_START     = "group-plant:start"; 
    final static private String PLANT_END       = "group-plant:end";
    final static private String PU_CONFIG_START = "group-pu-config:start"; 
    final static private String PU_CONFIG_END   = "group-pu-config:end";
    
    
	static private CompanyService  companyService;
	
	private Company company;
	private boolean filterLine = false;
	
	/**
	 * Default constructor
	 */
	public MainMeuCallback (UserParam params) throws Exception{
		companyService  = Utilities.lookupService(companyService, "CompanyServiceImp");
		company = companyService.findByNr(params, params.getCompany());
	}
	
	
	
	/**
	 * Filter the passed in html line according to the company configurations
	 * @param String line
	 * @return configured line
	 */
	public String process(String line){
		
	    /* TODO: future development
	    if (!company.isExportSimuFileFormat()){
	        line = process(line, SIMU_START, true);
	        line = process(line, SIMU_END, false);
	        
	        if (line.indexOf(SIMU_ITEM) != -1){
	            line = line.replace("style=\"border:none;\"", "");
	        }
	    }
	    */
	    
	    
	    if (filterLine){
			return "";
		}
		
		return line;
	}
	
	/**
     * Filter the passed in html line according to the company configurations
     * @param String line
     * @param String group to look for
     * @param true = start filtering, false = stop filtering
     * @return configured line
     */
    private String process(String line, String group, boolean filterOn){
        if (line.indexOf(group) != -1){
            filterLine = filterOn;
            return "";
        }
        return line;
    }
	
}


