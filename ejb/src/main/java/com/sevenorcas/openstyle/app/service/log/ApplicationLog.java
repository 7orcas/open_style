package com.sevenorcas.openstyle.app.service.log;

import org.jboss.logging.Logger;

import com.sevenorcas.openstyle.app.application.ApplicationParameters;

/**
 * Application Logger<p>
 * 
 * All general logging uses <b>this</b> class.<p>
 * 
 * [License]
 * @author John Stewart
 */
public class ApplicationLog extends BaseLog {

	/**
	 * Log (uses <code>ApplicationParameters</code> so that log message goes to application log)  
	 */
	private static final Logger LOG = Logger.getLogger(ApplicationParameters.class);
	
	/**
	 * TRACE Log message to application log. This is a temporary log.
	 * @param String message to log
	 */
	static public void trace(String message){
		trace(message, getCallerCallerClassName());
	}
	
	/**
	 * TRACE Log message to application log. This is a temporary log.
	 * @param String message to log
	 * @param String caller class name and line number
	 */
	static public void trace(String message, String caller){
		LOG.trace("[" + caller + "] "  + message);
	}
	
	/**
	 * INFO Log message to application log. This is a temporary log.
	 * @param String message to log
	 */
	static public void info(String message){
		info(message, getCallerCallerClassName());
	}
	
	/**
	 * INFO Log message to application log. This is a temporary log.
	 * @param String message to log
	 * @param String caller class name and line number
	 */
	static public void info(String message, String caller){
		LOG.info("[" + caller + "] "  + message);
	}
	
	/**
	 * INFO Log message to application log with no caller. This is a temporary log.
	 * @param String message to log
	 */
	static public void infoNoCaller(String message){
		LOG.info(message);
	}
	
	
	/**
	 * DEBUG Log message to application log. This is a temporary log.
	 * @param String message to log
	 */
	static public void debug(String message){
		debug(message, getCallerCallerClassName());
	}
	
	/**
	 * DEBUG Log message to application log. This is a temporary log.
	 * @param String message to log
	 * @param String caller class name and line number
	 */
	static public void debug(String message, String caller){
		LOG.debug("[" + caller + "] "  + message);
	}
	
	/**
	 * DEBUG Log message to application log with no caller. This is a temporary log.
	 * @param String message to log
	 */
	static public void debugNoCaller(String message){
		LOG.debug(message);
	}
	
	/**
	 * WARN Log message to application log. This is a temporary log.
	 * @param String message to log
	 */
	static public void warn(String message){
		warn(message, getCallerCallerClassName());
	}
	
	/**
	 * WARN Log message to application log. This is a temporary log.
	 * @param String message to log
	 * @param String caller class name and line number
	 */
	static public void warn(String message, String caller){
		LOG.warn("[" + caller + "] "  + message);
	}
	
	/**
	 * ERROR Log message to application log. This is a temporary log.
	 * @param String message to log
	 */
	static public void error(String message){
		error(message, getCallerCallerClassName());
	}
	
	/**
	 * ERROR Log message to application log. This is a temporary log.
	 * @param String message to log
	 * @param String caller class name and line number
	 */
	static public void error(String message, String caller){
		LOG.error("[" + caller + "] "  + message);
	}
	
	
	/**
	 * ERROR Log <code>Throwable</code> exception to application log. This is a temporary log.<br>
	 * Also, it exception is emailable, then action the email.
	 * @param Throwable exception to log
	 */
	static public void error(Throwable e){
		String caller = getCallerCallerClassName();
		error(e, caller);
	}
	
	/**
	 * ERROR Log <code>Throwable</code> exception to application log. This is a temporary log.<br>
	 * Also, if exception is emailable, then action the email.
	 * @param Throwable exception to log
	 * @param String caller class name and line number
	 */
	static public void error(Throwable e, String caller){
		String message = e.getMessage() != null && e.getMessage().length() > 0? e.getMessage() : "ERROR";
		LOG.error("[" + caller + "] " + message);
		LOG.error(e);
		email(e, caller);
	}
	
	/**
	 * Log a duration time (compares current time with passed in time)
	 * @param String message to log
	 * @param Long start of time
	 * @return Long new time
	 */
	static public long debugTime (String message, long time){
		long timeX = System.currentTimeMillis();
		debug(message + ": " + (timeX-time) + "ms");
		return timeX;
	}

	
	
}
