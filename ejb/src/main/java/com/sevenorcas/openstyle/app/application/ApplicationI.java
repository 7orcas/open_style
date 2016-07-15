package com.sevenorcas.openstyle.app.application;


/**
 * Application wide definitions and constants.<p>
 * 
 * The purpose of <b>this</b> interface is to have one file containing default values. 
 *    
 * [License]
 * @author John Stewart
 */
public interface ApplicationI {

	///////////////////////////  General definitions    /////////////////////////////////////
		
	final static public int    COMPANY_NUMBER_IGNORE     = 0;
	final static public long   MISSING_REFERENCE_ID      = 0L;
	final static public String MISSING_REFERENCE_LANGKEY = "Invalid";
	final static public String MISSING_ENTITY_CODE       = "000";
	final static public String UNKNOWN_CODE              = "?";
	
	final static public String DATE_FORMAT_JSON          = "dd.MM.yyyy";
	
	final static public String APP_NAME                  = "openstyle";
//D	final static public String WEB_MODULE_CONTEXT_ROOT   = "/" + APP_NAME + "/"; //defined in ear/pom.xml
	final static public String SERVICE_INDEX_PAGE_EXT    = "/service";
	
	final static public String NAME_CONTEXT_LOOKUP       = "java:app/" + APP_NAME  + "-ejb-1.0-SNAPSHOT/";
	
	final static public String DOMAIN_NAME               = "com.progenso.desma";
	final static public String ENTITY_BASE_PACKAGE       = DOMAIN_NAME + ".entities.";
	final static public String APP_ENTITY_BASE_PACKAGE   = DOMAIN_NAME + ".app.entities.";
	
	/** Entities with ids below this number require service status to update or delete */
	final static public long   ENTITY_PERMANENT_ID       = 10000L;

	

	///////////////////////////  Schemas  /////////////////////////////////////////////////////////
	final static public String SCHEMA_TEMP            = "temp";
	final static public String SCHEMA_CNTRL           = "cntrl";
	
	///////////////////////////  Sequences  /////////////////////////////////////////////////////////
	final static public String SEQUENCE_TEMP          = SCHEMA_TEMP + ".seq_id_temp";
	final static public String SEQUENCE_REPORT        = "seq_id_report";
	final static public String SEQUENCE_ENTITY        = "seq_id_entity";
	final static public String SEQUENCE_LANGUAGE      = SCHEMA_CNTRL + ".seq_id_language";
	final static public String SEQUENCE_INSTANCE      = "seq_nr_instance"; //+ comp_nr
    final static public String [] SEQUENCE_COMP_NRS   = {SEQUENCE_INSTANCE}; 
    

	///////////////////////////  Client return statuses    /////////////////////////////////////	
	
	final static public int RETURN_STATUS_OK        = 0;
	final static public int RETURN_STATUS_WARNING   = 1;
	final static public int RETURN_STATUS_ERROR     = 2;
	final static public int RETURN_STATUS_MAX_ROWS  = 3;
	final static public int RETURN_STATUS_INVALID   = 4;
	final static public int RETURN_STATUS_EXISTS    = 5;
	final static public int RETURN_STATUS_NO_PERM   = 6;
	final static public int RETURN_STATUS_REFERENCE = 7;
	

	
	///////////////////////////  Permission Values (CRUD)  ///////////////////////////////////////////	
	
	final static public String PERM_CREATE            = "C";
	final static public String PERM_READ              = "R";
	final static public String PERM_UPDATE            = "U";
	final static public String PERM_DELETE            = "D";
	
	///////////////////////////  System Task Status  ///////////////////////////////////////////	
		
	final static public int TASK_STATUS_NOT_CREATED    = 1;
	final static public int TASK_STATUS_RUNNING        = 2;
	final static public int TASK_STATUS_FINISHED       = 3;
	final static public int TASK_STATUS_FINISHED_ERROR = 4;
	final static public int TASK_STATUS_FINISHED_WARN  = 5;

	
    ///////////////////////////  Encoding fields  /////////////////////////////////////////////////////////
    /** Encoded key-value delimiter1         */ final static public String ENCODE_DELIMITER_1          = ",";
    /** Encoded key-value delimiter2         */ final static public String ENCODE_DELIMITER_2          = "=";
    /** Encoded key-value level 2 delimiter1 */ final static public String ENCODE_LEVEL2_DELIMITER_1   = "|";

	
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
	
	/** Java Object            */   final static public int TYPE_OBJECT            = 0;
	/** Java Integer           */   final static public int TYPE_INTEGER           = 1;
	/** Java Long              */   final static public int TYPE_LONG              = 2;
	/** Java String            */   final static public int TYPE_STRING            = 3;
	/** Java Double            */   final static public int TYPE_DOUBLE            = 4;
	/** Java Boolean           */   final static public int TYPE_BOOLEAN           = 5;
	/** Java Date              */   final static public int TYPE_DATE              = 6;
	/** Java List or ArrayList */   final static public int TYPE_LIST              = 20;

	
	/**
	 * Default maximum <code>String</code> field length, ie if <code>max</code> element in <code>@Field</code> annotation is not defined.<p>
	 */
	final static public double DEFAULT_MAX_STRING    = 15D;
	
	
	
}
