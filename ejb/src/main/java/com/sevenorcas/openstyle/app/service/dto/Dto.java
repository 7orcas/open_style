package com.sevenorcas.openstyle.app.service.dto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DTO (Data Transfer Object) Entity annotation to control behavior.<p>
 * 
 * The <code>@Dto</code> annotation is for identifying:<ul>
 *     <li>source entity for the DTO's field annotations</li>
 *     <li>entities in REST call parameters that require validation (unless <code>validate=false</code> is explicitly set)</li>
 *     <li>entities returned from REST calls that should be included in <code>ReturnDto</code> object's control field</li>
 * </ul>
 *  
 * [License]  
 * @author John Stewart
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Dto {

	
	/**
	 * If set to <code>false</code>, <b>this</b> annotation will prevent the <code>RestAroundInvoke</code> interceptor
	 * from validating the entity via its <code>@Field</code> definitions during a REST call. 
	 */
	boolean validate() default true;
	
	
	/**
	 * If set to <code>true</code>, <b>this</b> annotation will include the object in the <code>ReturnDto</code> objects control
	 * field after a REST call. 
	 */
	boolean includeInReturnDto() default false;
	
	/**
	 * <code>Entity</code> class name <b>this</b> Dto is based on. This class is 'looked-up' to find the DTO's field definitions.<p>
	 * 
	 * The class name resolution process to attempt to find the class in the following order:<ol>
	 *     <li>Assume the passed in class name is an inner clazz within the DTO class</li>
	 *     <li>Assume the passed in class name is in the same package / sub-package as the DTO</li>
	 *     <li>Assume the passed in class name is fully qualified and apply <code>Class.forName(classname)</code></li>
	 *     <li>Prefix <code>ApplicationI.ENTITY_BASE_PACKAGE</code> and apply</li>
	 * </ol><p> 
	 * 
	 * Note if <b>this</b> value is empty (ie 0 length string) then the implementing classes fully qualified name is used minus 'Dto'.
	 * Obviously both classes must be in the same package.
	 */
	String entity() default "";
	
    /**
	 * An annotated entity method with <code>init=true</code> provides the client with 
	 * initial values when constructing new objects (of the entities type/class).
	 * <p>
	 *
	 * The method will attempt to pass attributes in the following order:<ol>
     *     <li><code>UserParam</code></li>
     *     <li><code>Company</code></li>
     *     <li><code>NULL</code></li>
     * </ol><p>
     * 
	 * eg: the following method will inform the client to set field 'xxx' to 'zzz' when 
	 *     constructing a new object:<p>
	 * <pre><code>
     * {@literal @}Dto(init=true)
	 * public void setup(){
	 *    setXxx('zzz');
	 * }
	 * </code></pre>
	 */
	boolean init() default false; 	

	
}
