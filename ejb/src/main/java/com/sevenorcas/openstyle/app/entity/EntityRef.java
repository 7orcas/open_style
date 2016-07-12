package com.sevenorcas.openstyle.app.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * The <code>@EntityRef</code> annotation is used to identify dependent entities on the class that <b>this</b> annotation is defined.<p>
 *  
 * I.e. Used if there is a reference in the database between tables or a logical reference.<p>
 *  
 * This annotation is used in the following places:<br>
 * <ul>
 *     <li><code>BaseDao</code> during entity deletes. This method assumes the referencing table column is called <code>[dependent entity name]_id</code></li>  
 * </ul>      
 * <p>
 * 
 * [License]
 * @author John Stewart
 */

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EntityRef {
	
	/**
     * Comma separated list of entity class names that dependent on <b>this</b> class.<p>
     *   
     * The name can either be fully qualified or relative to <code>ApplicationI.ENTITY_BASE_PACKAGE</code>.
     * @see ApplicationI
     * @see type
     */
	String entities() default ""; 
}
