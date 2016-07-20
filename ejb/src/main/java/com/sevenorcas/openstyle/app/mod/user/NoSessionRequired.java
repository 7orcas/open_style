package com.sevenorcas.openstyle.app.mod.user;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for REST methods. If present then access does not required a valid session.<p>
 * 
 * <font color="red">Use this annotation with care as it exposes the application to everyone on the web.</font>
 * 
 * [License]
 * @author John Stewart
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSessionRequired {
}
