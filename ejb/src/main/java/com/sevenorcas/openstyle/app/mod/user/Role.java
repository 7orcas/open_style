package com.sevenorcas.openstyle.app.mod.user;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.service.entity.BaseEntity;


/**
 * The <code>Role</code> class contains CRUD (create, read, update and delete) permissions per permission key.<p>  
 * 
 * <b>This</b> class is tightly coupled with the <code>BaseLogin</code> and <code>UserRole</code> classes.<p>
 * 
 * @see LoginServiceImp
 * 
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
@Entity
@Table(name="_yzh_role", schema="cntrl")
public class Role extends BaseEntity implements Serializable {
	
	/**
	 * ID Field.<p>
	 * @see ApplicationI
	 */
	@Id
	protected Long id;
	
	private String role_name;
	private String role_value;
	private String role_descr;
	
    
    ////////////////////// Main class methods  //////////////////////////////////
    
    /**
     * Create role object
     */
    public Role() {
    	super();
    }

    
	
    ////////////////////// Getters / Setters //////////////////////////////////    
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	public String getRole_value() {
		return role_value;
	}
	public void setRole_value(String role_value) {
		this.role_value = role_value;
	}
	public String getRole_descr() {
		return role_descr;
	}
	public void setRole_descr(String role_descr) {
		this.role_descr = role_descr;
	}

    
}
