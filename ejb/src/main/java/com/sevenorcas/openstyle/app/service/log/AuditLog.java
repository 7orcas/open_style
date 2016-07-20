package com.sevenorcas.openstyle.app.service.log;

import org.jboss.logging.Logger;

/**
 * Audit (persistent) Logger<p>
 * 
 * All application audit logging uses <b>this</b> class.<p>
 * 
 * Refer to <a href="{@docRoot}/../doc-files/logging-server.html">Server Logging</a><br>
 * 
 * @author John Stewart
 */
public class AuditLog extends BaseLog {

	/** Jboss logger  */ private static final Logger LOG = Logger.getLogger(AuditLog.class);
	
	/**
	 * Log message to application audit log. This is a persistent log.
	 * @param String message to log
	 */
	static public void log(String message){
		LOG.info("[" + getCallerCallerClassName() + "] "  + message);
	}
	
	/**
	 * Login message
	 * @param String message to log
	 */
	static public void login(String message){
		LOG.info("[" + getCallerCallerClassName() + "] LOGIN "  + message);
	}
	
	/**
	 * Logout message
	 * @param String message to log
	 */
	static public void logout(String message){
		LOG.info("[" + getCallerCallerClassName() + "] LOGOUT "  + message);
	}
	
}
