package com.sevenorcas.openstyle.app.login;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Application specific login entity<p>
 *  
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Entity
@Table(name="_yzh_user", schema="cntrl")
public class Login extends BaseLogin implements Serializable {

	
	/**
	 * Create invalid login object
	 * Used by Entity Manager
	 */
	@SuppressWarnings("unused")
	private Login() {
		super();
		initialise();
	}

	/**
	 * Create invalid login object
	 * @param language
	 * @return Invalid login object
	 */
	public Login (String language){
		super(language);
		initialise();
	}

	
	/**
	 * Create a valid login object
	 * @param company
	 * @param userid
	 * @param user_id
	 * @param language
	 * @return Valid login object
	 */
	public Login (Integer company, String userid, Long user_id, String language){
		super(company, userid, user_id, language);
		initialise();
	}
	

	/**
	 * Initialize a login object with application specific attributes
	 */
	private void initialise(){
	}
	
	
	
	/**
	 * Validation of login group.<br>
     * Note <b>this</b> method can veto the login if groups are incorrect.
	 * @param success
	 */
	public void validate(){
		
	}
	
	
	
    
    ////////////////////// Getters / Setters //////////////////////////////////    
    
	

		
	
}
