package com.sevenorcas.openstyle.app.service.dto;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.mod.lang.LanguageServiceImp;
import com.sevenorcas.openstyle.app.service.entity.Field;



/**
 * Entity field definition object, ie meta data about a field.<p>
 * 
 * This object is available for clients for their own processing purposes.<br>
 * eg client side:
 * <ul>- validation</ul>
 * <ul>- initialization of new objects</ul>
 * <ul>- formating</ul>
 * <ul>- processing behavior</ul>
 * <p> 
 *  
 * The <code>DefinitionService</code> uses both Java reflection and the <code>@Field</code> annotation to build a list
 * of <code>FieldDefDto</code> objects. These are then sent via JSON to the client.  
 *  
 *  
 * [License]
 * @author John Stewart
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldDefDto implements Serializable, ApplicationI{
	
	/**
	 * <code>ApplicationI</code> types with their corresponding Java types  
	 */
	static private Object[] javaTypes ={
		TYPE_INTEGER, "Integer",
		TYPE_LONG,    "Long",
		TYPE_STRING,  "String",
		TYPE_DOUBLE,  "Double",
		TYPE_BOOLEAN, "Boolean",
		TYPE_DATE,    "Date",
		TYPE_LIST,    "List",
		TYPE_LIST,    "Array",
		TYPE_LIST,    "ArrayList",
	};
	
	/**
	 * <b>Accessor</b><br>
	 * Used to create getter, setter and other name dependent functions on the client.<p>
	 * 
	 * eg if <code>accessor="Status"</code> then the client will create functions:
	 * <ul>- getStatus()</ul>
	 * <ul>- setStatus()</ul>
	 * <p>
	 * 
	 * Also depending on the "Application Type" field, other functions may be created. 
	 * eg if <code>ApplicationType="TYPE_LOOKUP_REF"</code> then the client will create function:
	 * <ul>- lookupStatus()</ul>  
	 * <p>
	 * 
	 * @see ApplicationI
	 */
	@JsonProperty(value="n")
	public String accessor; 
	
	/**
	 * <b>Dto</b><br>
	 * DTO (Data Transfer Object) / JSON field name
	 */
	@JsonProperty(value="d")
	public String dto; 
	
	/**
	 * <b>Type</b><br>
	 * ie Java type name (eg Integer, String, Double, Date, etc)
	 * <code>ApplicationI</code> type (ie int code as defined in <code>ApplicationI</code> corresponding to the actual Java type).
	 */
	@JsonProperty(value="t")
	public Integer type; 
	
	/**
	 * <b>ApplicationType.</b><br>
	 * This value allows the client to provide default behavior and formatting. Eg type maybe APP_TYPE_ID, which the client can
	 * derive as unique identifier for an Entity.
	 * @see ApplicationI
	 */
	@JsonProperty(value="a")
	public Integer applicationType;
	
	/**
	 * <b>Label Key</b><br>
	 * Language label key. Use by client to "lookup" the corresponding language value for the given key.
	 * @see LanguageServiceImp
	 */
	@JsonProperty(value="l")
	public String labelKey;
	
	/**
	 * <b>Format</b><br>
	 * Note this is context dependent on Type. eg:
	 * <ul>- the format represents the number of decimals for double field</ul>
	 * <ul>- "dd.mm.yyyy" represents a date format for a date field</ul>
	 * @see Field
	 */
	@JsonProperty(value="f")
	public String format; 
	
	/**
	 * <b>Values</b><br>
     * Key=Value pairs used with <code>TYPE_LOOKUP_REF</code> and <code>TYPE_LOOKUP_VALUE</code> type. The <code>values</code> element in <code>Field</code> annotation contains 
     * possible values for a field.<br>
     * eg for a field <code>value = "2"</code> and element <code>values="1=Yes,2=No,3=Cancel"</code>, the display format will be the language key value of "No".
     *   
     * @see ApplicationI
     * @see Field
     */
	@JsonProperty(value="v")
	public String values;
	
	/**
	 * <b>Min</b><br>
	 * The minimum value for a field. Note this is context dependent on the field type, eg:
	 * <ul>- integer / double field: min represents the actual value</ul>
	 * <ul>- string field: min represents the value length</ul>
	 * @see Field
	 */
	@JsonProperty(value="m")
	public Double minimum;
	
	/**
	 * <b>Max</b><br>
	 * The maximum value for a field. Note this is context dependent on the field type, eg:
	 * <ul>- integer / double field: max represents the actual value</ul>
	 * <ul>- string field: max represents the value length</ul>
	 * <p>
	 * 
	 * Note: if not set in via the <code>Field</code> annotation and field type is a <code>String</code> then <code>ApplicationI.DEFAULT_MAX_STRING</code> is applied
	 * @see Field
	 */
	@JsonProperty(value="x")
	public Double maximum; 
	
	/**
	 * <b>NotNull</b><br>
	 * If set <code>true</code> then the field may not contain a <code>null</code> value.
	 * @see Field
	 */
	@JsonProperty(value="u")
	public Boolean notNull; 
	
	/**
	 * <b>ModelOnly</b><br>
	 * Special field. If set <code>true</code> then the field is included in the model only (ie they will only be available in the model and not be added to data objects)
	 */
	@JsonProperty(value="y")
	public Boolean modelOnly;
	
	
	/**
	 * <b>NewRecordValue</b><br>
	 * The initial value for a new record. The <code>init</code> element in <code>Dto</code> annotation is used to indicate an Entity method that contains the initial values.  
	 * @see Dto
	 */
	@JsonProperty(value="z")
	public Object newRecordValue; 
	
	/**
	 * <b>Edit</b><br>
	 * Controls for edit-ability of a field. The order of precedence is:
	 * <ol>1. if element <code>readonly = true</code> in the <code>Field</code> annotation, then field is not editable.</ol>
	 * <ol>2. if edit = 'true', then field is editable.</ol>
	 * <ol>3. if edit = 'new', then field is editable for new objects (ie not yet persisted in database).</ol>
	 * <ol>4. if element is empty then edit-ability is derived from other elements, eg if element exists for min/max, notNull then edit is derived as true.</ol>
	 * <ol>5. if element is empty and no other derivable element is set then the readonly element is set to true.</ol>
	 * @see Field  
	 */
	@JsonProperty(value="e")
	public String editable; 
	
	/**
	 * Child object definitions. Ie entity may contain none, one or many child containers. A recursive call is used to define all child objects.<br>
	 * Note: there is no limit on the deep of parent-child relationships.
	 */
	@JsonProperty(value="c")
	public ArrayList<FieldDefDto> fields = null; 
	
	/**
   	 * <b>Lang</b><br>
	 * Special field. If set <code>true</code> indicates to the client to use the language file to translate the field value (ie message is encoded).
   	 */
	@JsonProperty(value="p")
   	public Boolean lang;
	
	/**
   	 * <b>Encoded id field</b><br>
	 * The <code>Encode ID</code> field indicates that this field has been encoded within the JSon object returned to the client 
	 * (providing the return object is flagged encoded).<p>
	 * 
	 * The id field contains an index pointer to the actual field value.<p> 
	 * 
	 * @see DtoEncode
   	 */
	@JsonProperty(value="k")
   	public String encode;
	
	/**
   	 * <b>Encoded substitution flag</b><br>
	 * The <code>Encode substitution</code> flag indicates that this field may have been partially encoded, i.e. a delimiter id indicates which
	 * part of the field points to the actual value.<p>
	 * 
	 * @see Encoded id field
	 * @see DtoEncode
   	 */
	@JsonProperty(value="j")
   	public Boolean encodeSub;
	
	/**
     * <b>Field array size</b><br>
     * If field <code>value</code> is not NULL and > 0 then the field is an array of size <code>value</code>.
     */
    @JsonProperty(value="b")
    public Integer array; 
	
	
	/**
	 * Constructor
	 * @param dto field name
	 * @param accessor name
	 */
	public FieldDefDto(String dto, String accessor) {
		this.dto = dto;
		this.accessor = accessor;
	}


	@JsonIgnore
	public String getDto() {
		return dto;
	}
	public void setDto(String dto) {
		this.dto = dto;
	}

	@JsonIgnore
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}

	@JsonIgnore
	public String getEdit() {
		return editable;
	}
	public void setEdit(String edit) {
		this.editable = edit;
	}

	@JsonIgnore
	public Integer getApplicationType() {
		return applicationType;
	}
	public void setApplicationType(Integer applicationType) {
		this.applicationType = applicationType;
	}
	@JsonIgnore
	public boolean isApplicationType() {
		return applicationType != null;
	}
	
	
	@JsonIgnore
	public String getAccessor() {
		return accessor;
	}
	public void setAccessor(String accessor) {
		this.accessor = accessor;
	}

	@JsonIgnore
	public String getLabel() {
		return labelKey;
	}
	public void setLabel(String label) {
		this.labelKey = label;
	}

	@JsonIgnore
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}

	@JsonIgnore
	public Double getMin() {
		return minimum;
	}
	public void setMin(Double min) {
		this.minimum = min;
	}
	@JsonIgnore
	public boolean isMin() {
		return minimum != null;
	}

	@JsonIgnore
	public Double getMax() {
		return maximum;
	}
	public void setMax(Double max) {
		this.maximum = max;
	}
	@JsonIgnore
	public boolean isMax() {
		return maximum != null;
	}

	@JsonIgnore
	public Boolean getNotNull() {
		return notNull;
	}
	public void setNotNull(Boolean notNull) {
		this.notNull = notNull;
	}
	@JsonIgnore
	public boolean isNotNull() {
		return notNull != null && notNull;
	}
	
	@JsonIgnore
	public Boolean getLang() {
		return lang;
	}
	public void setLang(Boolean lang) {
		this.lang = lang;
	}
	@JsonIgnore
	public boolean isLang() {
		return lang != null && lang;
	}

	@JsonIgnore
	public String getEncode() {
		return encode;
	}
	public void setEncode(String encode) {
		this.encode = encode;
	}

	@JsonIgnore
	public Boolean getEncodeSub() {
		return encodeSub;
	}
	public void setEncodeSub(Boolean encodeSub) {
		this.encodeSub = encodeSub;
	}


	@JsonIgnore
	public String getValues() {
		return values;
	}
	public void setValues(String values) {
		this.values = values;
	}

	@JsonIgnore
	public Object getNewRecordValue() {
		return newRecordValue;
	}
	public void setNewRecordValue(Object newRecordValue) {
		this.newRecordValue = newRecordValue;
	}
	
		
	@JsonIgnore
	public ArrayList<FieldDefDto> getChildObject() {
		return fields;
	}
	public void setChildObject(ArrayList<FieldDefDto> childList) {
		this.fields = childList;
	}

	@JsonIgnore
	public Boolean getModelOnly() {
		return modelOnly;
	}
	public void setModelOnly(Boolean modelOnly) {
		this.modelOnly = modelOnly;
	}
	
	@JsonIgnore
    public Integer getArray() {
        return array;
    }
    public void setArray(Integer a) {
        this.array = a;
    }
    @JsonIgnore
    public boolean isArray() {
        return array != null && array.intValue() > 0;
    }
	
		
	////////////////////// Utility methods //////////////////////////////






	/**
	 * Pass accessor name to get capitalized name.<br>
	 * Note if accessor is empty then the actual field name is returned.  
	 * @param Field annotation
	 * @param Java Field (reflection)
	 * @return capitalized name
	 */
	static public String getAccessor (Field f, java.lang.reflect.Field field){
		String accessor = f != null? f.accessor() : "";
		if (accessor.length() == 0){
		    accessor = field.getName();
		}
		return formatAccessor(accessor);
	}
	
	
	/**
	 * Pass accessor name to get capitalized name.<br>
	 * Note if accessor is empty then the actual field name is returned.  
	 * @param Field annotation
	 * @param Java Method (reflection)
	 * @return capitalized name
	 */
	static public String getAccessor (Field f, java.lang.reflect.Method method){
		String accessor = f != null? f.accessor() : "";
		if (accessor.length() == 0){
		    accessor = method.getName();
		}
		return formatAccessor(accessor);
	}
	
	/**
	 * Format the accessor name to be capitalized
	 * @param String accessor
	 * @return capitalized name
	 */
	static public String formatAccessor (String accessor){
		return accessor.substring(0,1).toUpperCase() + accessor.substring(1);
	}
	
	
	/**
	 * Get corresponding field type value.
	 * @param String field type
	 * @return
	 */
	static public int getType (String type){
		for (int i=0; type != null && i<javaTypes.length; i+=2){
			type = type.endsWith(";")? type.substring(0, type.length()-1): type;
			if (type.endsWith((String)javaTypes[i+1])){
				return (Integer)javaTypes[i];
			}
		}
		return TYPE_OBJECT;
	}
	
	
	
	
}
