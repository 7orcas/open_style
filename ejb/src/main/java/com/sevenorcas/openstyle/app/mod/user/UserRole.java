package com.sevenorcas.openstyle.app.mod.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.service.entity.BaseEntity;
import com.sevenorcas.openstyle.app.service.entity.Field;


/**
 * The <code>UserRole</code> class is the join of permission keys and roles.<p>  
 * 
 * <b>This</b> class is tightly coupled with the <code>BaseLogin</code> and <code>Role</code> classes.<p>
 * 
 * @see LoginServiceImp
 * 
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
@Entity
@Table(name="_yzh_user_role", schema="cntrl")
public class UserRole extends BaseEntity implements Serializable {
	
	/**
	 * ID Field.<p>
	 * @see ApplicationI
	 */
	@Id
	protected Long id;
	
	
	/** Header foreign key */
	@ManyToOne
	@JoinColumn(name="_yzh_user_id")
	private User user;

	@Column(name="_yzh_user_id", insertable=false,updatable=false)
	private Long userId;

	@Field
	@Column(name="_yzh_role_id")
	private Long roleId;
	
    
    ////////////////////// Main class methods  //////////////////////////////////
    
    /**
     * Create role object
     */
    public UserRole() {
    	super();
    }

    
	
    ////////////////////// Getters / Setters //////////////////////////////////    
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	
    
}
