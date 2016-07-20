package com.sevenorcas.openstyle.app.service.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Entity attribute annotation.<p>
 *
 * [License]
 * @author John Stewart
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EntityAttributes {

	
    /**
     * Entity Reference Language key for <b>this</b> entity
     */
    String refLangKey() default "";  	

	
}
