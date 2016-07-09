package com.sevenorcas.openstyle.app.perm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to control access to services via permissions.<br>
 * 
 * [License]
 * @author John Stewart
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Permission {
		
    static final public String CREATE = "C";
    static final public String READ   = "R";
    static final public String UPDATE = "U";
    static final public String DELETE = "D";
    
    
	/**
	 * Permission key, i.e. key from entity.
	 */
	String key() default "";
	
	/**
	 * Permission CRUD values.<p>
	 * Note: 'x' is used to allow a default value (ie it has no meaning)
	 */
	public enum Perm { C, R, U, D, x}
	
	/**
	 * Permission value required, i.e. CRUD value from user required to access <b>this</b> method.<p>
	 * Note: 'x' is used to allow a default value (ie it has no meaning)
	 */
	Perm value() default Perm.x;
	static final public String NO_PERMISSION = "x"; 
	
	/**
	 * If annotated as <code>service = true</code>, then the method can only be entered by a user with a "service" account status.
	 */
	boolean service() default false;
	
	/**
     * If annotated as <code>admin = true</code>, then the method can only be entered by a user with a "admin" or "service" account status.
     */
    boolean admin() default false;
	
}
