package com.sevenorcas.openstyle.app;

import java.io.Serializable;

import com.sevenorcas.openstyle.app.log.ApplicationLog;



/**
 * Application Exception identifier.<p>
 * 
 * Note: By default thrown exceptions using <b>this</b> class (or any of its subclasses) are not logged or emailed to <code>sysadmin</code>, 
 * unless explicitly set via the <code>setLogable()</code> and <code>setEmailable()</code> methods respectively.<p>
 * 
 * [License]
 * @author John Stewart
 */
@SuppressWarnings("serial")
public class AppException extends Exception implements Serializable {
	
	
	/** Control to log <b>this</b> exception     */ private boolean logThisException      = false;
	/** Control to email <b>this</b> exception   */ private boolean emailThisException    = false;
	/** Message detail (passed direct to client) */ private String  detailMessage         = null;
	/** Always return the message to the client  */ private boolean returnMessageToClient = false;
	
	/**
	 * Is <b>this</b> instance of the exception log-able?
	 * @return true/false value
	 */
	public boolean isLogThisException() {
		return logThisException;
	}
	
	/**
     * Set <b>this</b> instance to log-able and email-able.
     */
    public AppException logAndEmailThisException() {
        this.logThisException = true;
        this.emailThisException = true;
        return this;
    }
    
	
	/**
	 * Set <b>this</b> instance to log-able.
	 */
	public AppException logThisException() {
		this.logThisException = true;
		return this;
	}
	
	/**
	 * Reset <b>this</b> instance to not log-able.
	 */
	public void dontLogThisException() {
		this.logThisException = false;
	}
	
	/**
	 * Is <b>this</b> instance of the exception email-able?
	 * @return true/false value
	 */
	public boolean isEmailThisException() {
		return emailThisException;
	}
	
	/**
	 * Set <b>this</b> instance to email-able.
	 */
	public AppException emailThisException() {
		this.emailThisException = true;
		return this;
	}
	
	/**
     * Set a detailed message.
     */
    public AppException setDetailMessage(String m) {
        detailMessage = m;
        return this;
    }
	
	/**
	 * Reset <b>this</b> instance to not email-able.
	 */
	public void dontEmailThisException() {
		this.emailThisException = false;
	}
	
	
	/**
	 * Return a new <code>AppException</code> object
	 * @return
	 */
	public static AppException create() {
		AppException ex = new AppException();
		return ex;
	}
	
	/**
	 * Return a new <code>AppException</code> object
	 * @param message
	 * @return
	 */
	public static AppException create(String message) {
		AppException ex = new AppException(message);
		return ex;
	}
	
	/**
	 * Return a new <code>AppException</code> object
	 * @param message
	 * @return
	 */
	public static AppException createUnknownException() {
		AppException ex = new AppException("ErrUnknown");
		return ex;
	}
	
	
	/**
	 * Log <b>this</b> exception in the <code>ApplicationLog</code>
	 */
	public AppException error(){
		ApplicationLog.error(this);
		return this;
	}
	
	
	/**
	 * Default constructor
	 */
	public AppException() {
		super();
	}
	/**
	 * Message constructor
	 * @param message
	 */
	public AppException(String message) {
		super(message);
	}

	
    public String getDetailMessage() {
        return detailMessage;
    }

	public boolean isReturnMessageToClient() {
		return returnMessageToClient;
	}
	public AppException returnMessageToClient() {
		returnMessageToClient = true;
		return this;
	}
	
	
}
