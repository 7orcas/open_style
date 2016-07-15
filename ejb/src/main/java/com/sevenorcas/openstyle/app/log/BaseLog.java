package com.sevenorcas.openstyle.app.log;

import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.application.ApplicationParameters;
import com.sevenorcas.openstyle.app.application.Utilities;
import com.sevenorcas.openstyle.app.application.exception.AppException;
import com.sevenorcas.openstyle.app.mail.MailService;

/**
 * Base Class for all Loggers to extend<p>
 * 
 * <b>This</b> class is to be extended by actual logger classes. It contains generic logging methods. 
 * 
 * [License]
 * @author John Stewart
 *
 */
abstract public class BaseLog implements ApplicationI {
	
	/** Application singleton   */ protected static ApplicationParameters appParam = ApplicationParameters.getInstance();
	/** Email service           */ private static MailService mailService;
	/** Email service looked up */ private static boolean mailServicelookup = false;
	
	
	/**
	 * Return the caller class name and line number<p>
	 * 
	 * Note: This method <b><u>NEEDS</u></b> to be called from the log method, as it assumes that the 3rd
	 * trace element is the caller class.
	 * 
	 * @return String caller class name and line number
	 */
	public static String getCallerCallerClassName() {
		return getCallerCallerClassName(3, null, null);
	}
	
	/**
	 * Return the caller class name and line number that matches the passed in start of class name<p>
	 * 
	 * Note: If passed in name is <code>null</code> then the 3rd trace element is assumed to be the caller class.
	 * 
	 * @param String start of class name to search for
	 * @return String caller class name and line number
	 */
	public static String getCallerCallerClassName(String startClassName) { 
		return getCallerCallerClassName(3, startClassName, null);
	}
	
	/**
	 * Return the caller class name and line number that matches the passed in start of class name<p>
	 * 
	 * Note: If passed in name is <code>null</code> then the 3rd trace element is assumed to be the caller class.
	 * 
	 * @param String start of class name to search for
	 * @param Exception to use instead of current thread 
	 * @return String caller class name and line number
	 */
	public static String getCallerCallerClassName(String startClassName, Exception e) { 
		return getCallerCallerClassName(3, startClassName, e);
	}
	
	
	/**
	 * Return the caller class name and line number that matches the passed in criteria<p>
	 * 
	 * The order is:<ol>
	 *     <li>If index != -1 then use index from current <code>Thread</code></li>
	 *     <li>If start of class name != null then search current <code>Thread</code> for first occurrence</li>
	 * </ol>
	 * <p>
	 * Note: If exception != null then use this instead of the current <code>Thread</code>
	 * 
	 * @param int index of stack trace (plus 1 for this method)
	 * @param String start of class name to search for in stack trace
	 * @param Exception use instead of current thread 
	 * @return String caller class name and line number
	 */
	public static String getCallerCallerClassName(int index, String startClassName, Exception e) { 
        try{
        	StackTraceElement[] stElements = e != null? e.getStackTrace() : Thread.currentThread().getStackTrace();
        	StackTraceElement ste = null;
        	if (startClassName == null){
        		ste = stElements[index + 1];
        	}
        	else{
        		for (StackTraceElement el: stElements){
        			if (el.getClassName().startsWith(startClassName)){
        				ste = el;
        				break;
        			}
        		}
        	}
        	
        	return ste.getClassName() + "," + ste.getLineNumber();
        }
        catch (Exception ex){
        	return startClassName != null? startClassName : "";
        }
     }
	
	/**
	 * Email the passed in exception (exception is tested if is to be emailed to <code>sysadmin</code>).
	 * @param Throwable exception to email
	 * @param String caller class name and line number
	 */
	public static void email(Throwable e, String caller){
		if (!appParam.isMailConfigured() 
				|| !appParam.isMailApplicationError()){
			return;
		}

		mailService = lookupService(mailService, "MailService");
		if (mailService == null){
			return;
		}
		
		//Only allow indicated application exceptions to be emailed
		if (e instanceof AppException){
			AppException a = (AppException)e;
			if (!a.isEmailThisException()){
				return;
			}
			a.dontEmailThisException();
		}
		
		
		String subject = appParam.getErrorMailSubject().length() > 0? appParam.getErrorMailSubject() : "Application Exception Thrown"; 
		String message = (e.getMessage() != null && e.getMessage().length() > 0? e.getMessage() : "ERROR") + "\n\n" + caller;
		mailService.sendMail (subject, message, appParam.getErrorMailTo(), appParam.getErrorMailCc(), appParam.getErrorMailBcc());
	}
	
	
	/**
	 * Lookup a bean via the passed in interface and class name
	 * @param T service
	 * @param String service name
	 * @return looked up service or null if not found
	 */
	private static <T> T lookupService(T service, String name) {
		if (service == null && !mailServicelookup) {
			service = Utilities.lookupService(service, name);
			mailServicelookup = true;
		}
		return service;
	}
    
	
	
}
