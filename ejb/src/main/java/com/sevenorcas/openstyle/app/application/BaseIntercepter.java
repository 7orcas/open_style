package com.sevenorcas.openstyle.app.application;

import javax.interceptor.InvocationContext;

import com.sevenorcas.openstyle.app.application.exception.AppException;
import com.sevenorcas.openstyle.app.log.ApplicationLog;
import com.sevenorcas.openstyle.app.user.UserParam;




/**
 * Abstract Base Intercepter.<p>   
 *
 * Contains generic methods for intercepters.
 * @author John Stewart
 */
abstract public class BaseIntercepter implements ApplicationI {

	
	/**
	 * Test if exception is to be logged, and if so then log it in the <code>ApplicationLog</code>.
	 * 
	 * @param Exception to log
	 * @param InvocationContext for <b>this</b> log event
	 */
	protected void log(Exception e, InvocationContext ictx){
		String caller = ApplicationLog.getCallerCallerClassName(ictx.getMethod().getDeclaringClass().getName(), e);
		if (caller.indexOf(",") == -1){
			caller = caller + "." + ictx.getMethod().getName();  
		}
		log(e, caller);
	}
	
	
	/**
	 * Test if exception is to be logged, and if so then log it in the <code>ApplicationLog</code>.
	 * 
	 * @param Exception to log
	 * @param String originating caller and line number where exception occurred  
	 */
	protected void log(Exception e, String caller){
		
		if (e instanceof AppException){
			AppException a = (AppException)e; 
			if (!a.isLogThisException()){
				//do nothing
				return;
			}
			a.dontLogThisException();
		}
		else{
			//Get root exception
			while(true){
				if (e.getCause() == null){
					break;
				}
				Throwable t = e.getCause();
				if (t instanceof Exception){
					e = (Exception)t;
				}
				else{
					break;
				}
			}
		}
		ApplicationLog.error(e, caller);
	}
	
	
	/**
	 * Lookup a bean via the passed in interface and class name
	 * @param T service
	 * @param String service name
	 * @return looked up service or null if not found
	 */
	static public <T> T lookupService(T service, String name) {
    	return Utilities.lookupService(service, name);
	}
    
    
    /**
	 * Find and return the user parameter object
	 * @param Object [] parameters
	 * @returns UserParam object if found
	 */
	protected UserParam getUserParam(Object [] params) throws Exception{
        if (params != null){
			for (int ii = 0; ii < params.length; ii++){
				if (params[ii] != null && (params[ii] instanceof UserParam)){
					return (UserParam)params[ii];
				}
			}
		}
        return null;
	}
	
    
}