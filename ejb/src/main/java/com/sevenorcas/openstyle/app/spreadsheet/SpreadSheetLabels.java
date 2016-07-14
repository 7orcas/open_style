package com.sevenorcas.openstyle.app.spreadsheet;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate method returns spreadsheet labels to be used to the spreadsheet export.
 * 
 * [License]
 * @author John Stewart
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SpreadSheetLabels {
	
}
