package com.sevenorcas.openstyle.app.service.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJBContext;
import javax.naming.InitialContext;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.application.ApplicationParameters;



/**
 * <code>BaseEntity</code> common fields, attributes and methods for all entities.<p>
 * 
 * <b>This</b> class is intended to be lightweight.<br>
 * Note: The <code>Utilities</code> class is designed to contain helper methods for entities.  
 *  
 * @see Utilities
 *  
 * [License]
 * @author John Stewart
 */
@SuppressWarnings("serial")
@MappedSuperclass
abstract public class BaseEntity implements ApplicationI, EntityI, Serializable{
	
	static protected ApplicationParameters appParam = ApplicationParameters.getInstance(); 
	
	@Column(name="comp_nr")
	protected Integer companyNr; 
	
	@Column(name="create_ts")
	protected Date created;
	
	@Column(name="create_id")
	protected Long createdId;
	
	@Column(name="update_ts")
	protected Date updated;
	
	@Field(appType=FIELD_TYPE_ACTIVE, edit="true")
	@Column(name="active")
	protected Boolean active;
	
	/** ID from dto object */
	@Transient
	@Field
	private Long id_dto;
	@Transient
	private Boolean delete;
	@Transient
	private Boolean changed;
	@Transient
	private ValidationException validationException;
	
	
    ////////////////////// Helper methods //////////////////////////////////
	
	/**
	 * Compare the passed in strings. Note: both <code>null</code> is considered equal.
	 * @param String param1
	 * @param String param2
	 * @return
	 */
	public boolean same(String param1, String param2){
		int x = sameNull(param1, param2);
	    if (x == 0) return true;
	    if (x == 1) return false;
	    return param1.equals(param2);
	}
	
	/**
	 * Compare the passed in integers. Note: both <code>null</code> is considered equal.
	 * @param Integer param1
	 * @param Integer param2
	 * @return
	 */
	public boolean same(Integer param1, Integer param2){
	    int x = sameNull(param1, param2);
	    if (x == 0) return true;
	    if (x == 1) return false;
	    return param1.equals(param2);
	}
	
	/**
	 * Compare the passed in objects. Note: both <code>null</code> is considered equal.
	 * @param Object param1
	 * @param Object param2
	 * @return -1 == can't test, 0 == both null, 1 == different
	 */
	public int sameNull(Object param1, Object param2){
	    if (param1 == null && param2 == null){
	    	return 0;
	    }
	    if (param1 == null || param2 == null){
	    	return 1;
	    }
	    return -1;
	}
	
	
	/**
	 * Find a child entity by its ID
	 * @param Long id
	 * @param List child container
	 * @return Child object
	 */
	@SuppressWarnings("rawtypes")
	public BaseEntity findBaseEntityById(Long id, List list) {
		if (id == null || list == null){
			return null;
		}
		for (int i=0; i<list.size(); i++){
			BaseEntity child = (BaseEntity)list.get(i);
			if (child.getId() != null 
					&& child.getId().equals(id)){
				return child;
			}
		}
		return null;
	}
	
	/**
	 * Is this a new record?
	 * @return
	 */
	public boolean isNew(){
		return getId() == null || getId().longValue() < 0; 
	}
	
	/**
	 * Return the embedded string value for its corresponding numeric value
	 * @param string values
	 * @param numeric value
	 * @return
	 */
    @Transient
    protected String getSelectValue(String values, Integer v){
        String [] sx = values.split(",");
        for (String s : sx){
            String [] sz = s.split("=");
            if (sz[0].equals("" + v)){
                return sz[1]; 
            }
        }
        return "?";
    }

	
    ///////////////////// Validations ///////////////////////////////////////
	
	
	/**
	 * Create a validation exception
	 * @param Validation message (to client)
	 * @return created or existing validation exception
	 */
	public ValidationException addValidateException (String message){
		if (validationException == null){
			validationException = new ValidationException(message);
		}
		return validationException;
	}
	
	
	/**
	 * Add a validation message
	 * @param Entity with validation error
	 * @param String entity field name
	 * @param String validation message (to client)
	 * @param String programmers text (to be logged)
	 */
	public void addValidateMessage (BaseEntity entity, String field, String message, String text) {
		Long id = entity.getId_dto() != null ? entity.getId_dto() : entity.getId();
		addValidateException (message);
		validationException.addMessageList(id, field, message, text);
	}
	
	/**
	 * Does a current validation exception exist?
	 * @return true if validation exception exists
	 */
	public boolean isValidateException (){
		return validationException != null;
	}
	
	/**
	 * Throw the current validation exception (if exists) and roll back any transaction
	 */
	public void throwValidateException () throws ValidationException{
		if (isValidateException ()){
			try{
				InitialContext ic = new InitialContext();
				EJBContext context = (EJBContext) ic.lookup("java:comp/EJBContext");
				context.setRollbackOnly();
			} catch (Exception ex){}
			throw validationException;
		}
	}
	
	
	
	
	//////////////////////// Permissions ///////////////////////////////////////
	
	/**
	 * Get <b>this</b> entities permission key.<p>
	 * 
	 * At this base level the general access key is returned (this is by default full access).<p>
	 * 
	 * @see Main documentation on Permissions
	 * @return permission key
	 */
	@Transient
	public String permissionKey(){
		return DEFAULT_PERM;
	}
	
	
	
    ////////////////////// Getters / Setters //////////////////////////////////
	
	abstract public void setId(Long id);
	
	public Integer getCompanyNr() {
		return companyNr;
	}
	public void setCompanyNr(Integer companyNr) {
		this.companyNr = companyNr;
	}

	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}

	public Long getCreatedId() {
		return createdId;
	}
	public void setCreatedId(Long createdId) {
		this.createdId = createdId;
	}

	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	@Transient
	public boolean isActive() {
		return active != null && active;
	}
	
	
    @Transient
	public boolean isDelete() {
		return delete != null && delete;
	}
	public void setDelete() {
		this.delete = true;
	}
	public void setDelete(boolean value) {
		this.delete = value;
	}
	
	@Transient
	public boolean isChanged() {
		return changed != null && changed;
	}
	public void setChanged() {
		this.changed = true;
	}
	
	public Long getId_dto() {
		return id_dto;
	}
	public void setId_dto(Long id_dto) {
		this.id_dto = id_dto;
	} 
	
}
