package com.sevenorcas.openstyle.app.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to control export (pdf, spreadsheet) behavior of an entity.<br>
 * Note that JSON export to client is handled via dto annotation.
 * 
 * [License]
 * @author John Stewart
 * @see PdfServiceImp
 * @see SpreadSheetServiceImp
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Export {
	
	/**
	 * Name given to entity in export file, ie<br>
	 * <ul>- if pdf export then this is the xml element encompassing the entity.</ul>
	 * <ul>- if spreadsheet export then this is the name of the tab.</ul>
	 * <p>
	 * 
	 * If no value is given the the entity class name is used.
	 */
	String entity() default "";
	
	/**
	 * Set element to <code>isExportPdf=false</code> to prevent the entity from being exported to a pdf.
	 */
	boolean isExportPdf() default true;
	
	/**
     * Should <b>this</b> element be wrapped in an element within the pdf xml document?.
     */
    boolean isPdfWrap() default true;
    
	
	/**
	 * Set element to <code>isExportSpreadSheet=false</code> to prevent the entity from being exported to a spreadsheet.
	 */
	boolean isExportSpreadSheet() default true;
	
	
}
