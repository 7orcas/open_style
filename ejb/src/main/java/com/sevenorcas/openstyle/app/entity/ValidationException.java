package com.sevenorcas.openstyle.app.entity;

import java.io.Serializable;
import java.util.ArrayList;

import javax.ejb.ApplicationException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.application.exception.AppException;
import com.sevenorcas.openstyle.app.lang.Language;



/**
 * Exception identifier if an object is invalid.<p>
 * 
 * [License]
 * @author John Stewart
 */

@ApplicationException(rollback=true)
@SuppressWarnings("serial")
public class ValidationException extends AppException implements ApplicationI, Serializable{
	
	/** Entity list specific messages */ private ArrayList<ValidationMessage> messageList = new ArrayList<ValidationMessage>();
	/** Status to be passed to client */ public int status = RETURN_STATUS_ERROR;
	
	/**
	 * Default Constructor
	 */
	public ValidationException (){
		super();
	}
	
    /**
     * Error Message Constructor
     * @param String validation error message
     */
	public ValidationException (String message){
		super(message);
	}
	
	
	/**
	 * Add a specific field validation error message
	 * @param String field name in DTO
	 * @param String validation error message
	 */
	public void addMessageList (String fieldname, String message){
		messageList.add(new ValidationMessage(null, fieldname, message, null));
	}
	
	
	/**
	 * Add a specific entity validation error message
	 * @param Long id of DTO
	 * @param String field name in DTO
	 * @param String validation error message
	 */
	public void addMessageList (Long id, String fieldname, String message){
		messageList.add(new ValidationMessage(id, fieldname, message, null));
	}
	
	/**
	 * Add a specific entity validation error message
	 * @param Long id of DTO
	 * @param String field name in DTO
	 * @param String validation error message
	 * @param String text message from programmer (not sent to client, used for logging)
	 */
	public void addMessageList (Long id, String fieldname, String message, String text){
		messageList.add(new ValidationMessage(id, fieldname, message, text));
	}
	
	/**
	 * Add a specific field validation error messages from another exception
	 * @param ValidationException
	 */
	public void addMessageList (ValidationException ex){
		for (int i=0; ex.messageList != null && i<ex.messageList.size(); i++){
			messageList.add(ex.messageList.get(i));
		}
	}

	/**
	 * Return <code>List</code> of validation error messages.
	 * @return List
	 */
	public ArrayList<ValidationMessage> getMessageDetail() {
		return messageList;
	}

	/**
	 * Format field names and validation error messages to the user language
	 * @param Language cache object
	 */
	public void setLanguage(Language l){
		for (ValidationMessage m : messageList){
			m.f = m.f != null? l.getLabel(m.f) : null;
			m.m = m.m != null? l.getLabel(m.m) : null;
		}
	}

	/**
	 * Client error status.
	 * @return int error status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Client error status.
	 * @param int error status
	 */
	public void setStatus(int status) {
		this.status = status;
	}


	/**
	 * Class to contain validation error messages per DTO
	 */
	public class ValidationMessage {
		/** DTO Field name  */ public String f;  
		/** Error Message   */ public String m;  
		/** Programmer Text */ private String tx; 
		/** DTO ID          */ public Long id;  
		
		public ValidationMessage(Long id, String f, String m, String tx) {
			this.id = id;
			this.f = f;
			this.m = m;
			this.tx =  tx;
		}
		
		@JsonIgnore
		public String getFieldname() {
			return f;
		}
		@JsonIgnore
		public String getMessage() {
			return m;
		}
		@JsonIgnore
		public String getText() {
			return tx;
		}
		
		public Long getId() {
			return id;
		}
	}
	
	
}
