package com.sevenorcas.openstyle.app.service.dto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DTO Field encoding
 * 
 * [License]
 * @author John Stewart
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DtoEncode {
	String id();
	
	/**
	 * true == encoding can do substrings (ie a mix of current field and substring from another field)
	 * @return
	 */
	boolean isSub() default false;
}
