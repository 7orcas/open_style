package com.sevenorcas.openstyle.app.service.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sevenorcas.openstyle.app.application.ApplicationI;


/**
 * The <code>@Field</code> annotation is widely used within entities to control processing behavior and formats for fields and methods.<p>
 *  
 * The underlining concept of the <code>@Field</code> annotation is to provide (as much as possible) one source of truth. Ie it allows the placement of all
 * configuration and format information within a single class file.<p>
 *  
 * This annotation is used in a number of places:<br>
 * <ul>
	 * <li>entity definition to client (ie clients can call a REST method to obtain an entities field definitions, returned via a <code>List {@literal <}FieldDefDto{@literal >}</code>).</li>  
	 * <li>entity export formats to pdf and spreadsheets. Note the field / method modifier is ignored, ie can be a private field/method.</li>
	 * <li>parameter validation within a REST call (ie when a client sends an entity as a JSON parameter, it can be validated via its 
	 *    <code>@Field</code> annotation.</li>
	 * <li>identify new object initialization method in entity (used in the client)</li>
 * </ul>      
 * <p>
 * 
 * [License]
 * @author John Stewart
 */

@Documented
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Field {

	/**
	 * Used by the client and export process to name the field / method.<br> 
	 * eg <code>accessor="xyz"</code> will result in the client creating getXyz(), setXyz(), etc methods. Also the export xml will create a field named "xyz".<br>
	 * If not specified then the field / method name is used.
	 */
	String accessor() default ""; 
	
	/**
	 * Used by the client and export process as the label key for the field.<br>
	 * If not specified then the field / method name is used as label key.
	 */
	String label() default ""; 
	
	/**
	 * Used by the client and export process to format a field.<br>  
	 * Note this is context dependent on field type. eg:
	 * <ul>- the format represents the number of decimals for double field</ul>
	 * <ul>- "dd.mm.yyyy" represents a date format for a date field</ul>
	 */
	String format() default ""; 

	/**
	 * If annotated as <code>readonly = true</code> then the field is not editable.
	 * @see edit
	 */
	boolean readonly() default false;
	
	/**
	 * Controls for edit-ability of a field. The order of precedence is:
	 * <ol>1. if element <code>readonly = true</code>, then field is not editable, ie <b>this</b> element is ignored.</ol>
	 * <ol>2. if edit = 'true', then field is editable.</ol>
	 * <ol>3. if edit = 'new', then field is editable for new objects (ie not yet persisted in database).</ol>
	 * <ol>4. if element is empty then edit-ability is derived from other elements, eg if element exists for min/max, notNull then edit is derived as true.</ol>
	 * <ol>5. if element is empty and no other derivable element is set then the readonly element is set to true.</ol>
	 * @see readonly  
	 */
	String edit() default ""; 

	/**
	 * Validation element. If set <code>true</code> then the field may not contain a <code>null</code> value.
	 */
	boolean notNull() default false;
	
	/**
	 * Validation element. If set <code>>-1</code> then the field must contain a value equal to or greater than the min.<br>
	 * Note this is context dependent on the field type, eg:
	 * <ul>- integer / double field: min represents the actual value</ul>
	 * <ul>- string field: min represents the value length</ul>
	 */
	double min() default -1;
    
    /**
	 * Validation element. If set <code>>-1</code> then the field must contain a value equal to or less than the max.<br>
	 * Note this is context dependent on the field type, eg:
	 * <ul>- integer / double field: max represents the actual value</ul>
	 * <ul>- string field: max represents the value length</ul>
	 * <p>
	 * 
	 * Note: if not set and field type is a <code>String</code> then <code>ApplicationI.DEFAULT_MAX_STRING</code> is applied
	 * @see ApplicationI.DEFAULT_MAX_STRING
	 */
	double max() default -1; 

    
    /**
     * <b>This</b> element can be assigned to help with processing. The available values are defined in ApplicationI. eg:
     * <ul>- TYPE_POSITION_NR: position number field and can be considered unique within child object list</ul>
     * <ul>- TYPE_LOOKUP_REF: reference lookup field (see values element)</ul>
     * <ul>- TYPE_ID: unique id field</ul>
     * <p>
     * 
     * @see ApplicationI
     * @see values
     */
	int appType() default ApplicationI.FIELD_TYPE_UNASSIGNED;
    
    /**
     * Key=Value pairs used with <code>TYPE_LOOKUP_REF</code> and <code>TYPE_LOOKUP_VALUE</code> type. The <code>values</code> element contains possible values for a field.<br>
     * eg for a field <code>value = "2"</code> and element <code>values="1=Yes,2=No,3=Cancel"</code>, the display format will be the language key value of "No".
     *   
     * @see ApplicationI
     * @see type
     */
	String values() default ""; 
	
	/**
	 * Class name of a child <code>List</code>. This is required for the DefinitionService as Java erases <code>List</code> types.<br>  
	 * Notes:<br>
	 * <ul>- class name can be fully qualified or relative to the app package.</ul>
	 * <ul>- for inner classes start annotation with '$' and give only class name</ul>
	 */
    String childClass() default ""; 
    
	
	/**
	 * Class name for field / method return <code>type</code>. Generally used for methods to indicate the Java return type  
	 */
    String type() default ""; 
    
    /**
   	 * <code>lang=true</code> indicates to the client to use the language file to translate the field value (ie message is encoded).<p>
   	 */
   	boolean lang() default false; 	
    
    
	//////////////////////////  Export annotations  /////////////////////////////////
    
	/**
	 * Validation element. If set <code>false</code> then the field / method is not included in a JSON export (for REST methods to client)
	 */
	boolean isExportJson() default true;
	
    /**
	 * Validation element. If set <code>false</code> then the field / method is not included in the export xml (for pdf exports)
	 */
	boolean isExportPdf() default true;

	
	/**
	 * Pdf export format element. If set <code>true</code> then object is rendered via its toString() method
	 */
	boolean isExportPdfToString() default false; 
	
	/**
	 * Pdf export format element. If not empty then value is used as pdf label key.
	 */
	String labelPdf() default ""; 
	
	/**
	 * Validation element. If set <code>false</code> then the field / method is not included in a spreadsheet export
	 */
	boolean isExportSpreadSheet() default true;
	
	
}
