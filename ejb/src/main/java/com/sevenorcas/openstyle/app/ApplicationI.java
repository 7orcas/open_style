package com.sevenorcas.openstyle.app;


/**
 * Application wide definitions and constants.<p>
 * 
 * The purpose of <b>this</b> interface is to have one file containing default values. 
 *    
 * [License]
 * @author John Stewart
 */
public interface ApplicationI {

	final static public String APP_NAME                  = "openstyle";
	
	/*-******************************************************************************
	 * Entity field types.
	 * Used to define standard fields within an entity / dto class. Default 
	 * behaviors are assigned using these types.
	 *******************************************************************************/
	
	/**
	 * Default field type (ie unassigned)
	 */
	final static public int FIELD_TYPE_UNASSIGNED   = 0;
	
	/**
	 * ID (<code>Long</code>) Field. Attributes include:
	 * <ul>- unique in system for a top level entity.</ul>
	 * <ul>- unique within a top level entity's <code>List</code> for child entitys. These may or may not be system unique.</ul>
	 * <ul>- new objects created by the client must be unique and negative for the client.</ul>
	 * <ul>- the client automatically generates a <code>getObjectById()</code> method.</ul>
	 */
	final static public int FIELD_TYPE_ID           = 51;
	
	/**
	 * Key (<code>String</code>) Field. Attributes include:
	 * <ul>- not null.</ul>
	 * <ul>- unique in system / company number for a top level entity.</ul>
	 * <ul>- unique within a top level entity's <code>List</code> for child entities. These may or may not be system unique.</ul>
	 * <ul>- new objects created by the client must be unique.</ul>
	 * <ul>- the client automatically generates a <code>getObjectByKey()</code> method.</ul>
	 */
	final static public int FIELD_TYPE_KEY          = 61;
	
	/**
	 * Active (<code>Boolean</code>) Field. 
	 */
	final static public int FIELD_TYPE_ACTIVE       = 63;
	
	/**
	 * Currency field.<br>
	 * Default format is 2 decimal places.
	 */
	final static public int FIELD_TYPE_CURRENCY     = 52;
	
	/**
	 * Reference Code (<code>String</code>) field.<br>
	 * ie <b>this</b> field contains a string value that references the actual value.<br>
	 * eg Language keys are a code reference to a language value.
	 */
	final static public int FIELD_TYPE_REF_CODE     = 53;
	
	/**
	 * Reference ID (<code>Long</code>) field.<br>
	 * ie <b>this</b> field contains a long value that references the actual value.<br>
	 */
	final static public int FIELD_TYPE_REF_ID       = 54;
	
	/**
	 * Reference field, ie <b>this</b> field contains the key to a key-value pair.<br>
	 * Typically this field is used with key=value pairs as defined in <code>values</code> element in <code>@Field</code> annotation.<p>
	 * 
	 * Note: <b>this</b> fields key must exist in the key-value list. 
	 * @see Field
	 */
	final static public int FIELD_TYPE_LOOKUP_REF   = 55;
	
	/**
	 * Reference field, ie <b>this</b> field contains the key to a key-value pair.<br>
	 * Typically this field is used with key=value pairs as defined in <code>values</code> element in <code>@Field</code> annotation.<p>
	 * 
	 * Note: <b>this</b> fields key does not have to exist in the key-value list. ie this ApplicationType is less restrictive compared 
	 * to the FIELD_TYPE_LOOKUP_REF. 
	 */
	final static public int FIELD_TYPE_LOOKUP_VALUE = 56;
	
	/**
	 * Entity / Child Entity unique number field (Note: unique within a company).<br>
	 * Note: the client automatically generates a <code>getObjectByNr()</code> method on the list object. 
	 */
	final static public int FIELD_TYPE_ENTITY_NR    = 57;
	
	/**
	 * Percentage field.<br>
	 * Default format is 2 decimal places with an appended '%' symbol. 
	 */
	final static public int FIELD_TYPE_PERCENTAGE   = 58;
	
	/**
	 * Description (<code>String</code>) field.<br>
	 * Note: This may be a language based field. 
	 */
	final static public int FIELD_TYPE_DESCR        = 59;
	
	/**
	 * Entity / Child Entity position number field.<br>
	 * Note: the client automatically generates a <code>getObjectByPosNr()</code> method on the parent object. 
	 */
	final static public int FIELD_TYPE_POS_NR       = 60;
	
	
	/**
	 * Entity sort number field.<br>
	 * Used by client to sort (order) the entity list. 
	 */
	final static public int FIELD_TYPE_SORT_NR       = 62;
	
	
	/**
	 * Entity language key field.<br>
	 */
	final static public int FIELD_TYPE_LANGKEY       = 64;
	
	
	/**
	 * System field.<br>
	 * Used by client for control purposes (e.g. CRUD permission value) 
	 */
	final static public int FIELD_TYPE_SYS_FIELD     = 101;
	
	
	
	/*-******************************************************************************
	 * Application defaults.
	 *******************************************************************************/
	
	/**
	 * Default maximum <code>String</code> field length, ie if <code>max</code> element in <code>@Field</code> annotation is not defined.<p>
	 */
	final static public double DEFAULT_MAX_STRING    = 15D;
	
	
	
}
