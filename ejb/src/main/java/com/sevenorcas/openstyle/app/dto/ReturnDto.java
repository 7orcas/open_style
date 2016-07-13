package com.sevenorcas.openstyle.app.dto;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.ApplicationI;



/**
 * <code>ReturnDto</code> is the standard return DTO(Data Transfer Object) wrapper for all client calls to the server.<p>
 * 
 * The design purpose is to provide a consistent process that can:
 * <ul>- integrate closely with <code>app/common/remote.js</code>. This javascript file is the central point for remote 
 *       calls from the client to server.</ul>
 * <ul>- provide common error handling.</ul>
 * <ul>- provide common behavior for interceptors that post process server calls (eg logging, task management).</ul>
 * <ul>- provide encoding logic for repeating <code>String</code>s within DTO fields within a <code>List</code>. Note the 
 *       DTO field must be specifically annotated with <code>DtoEncode</code>.</ul>
 * <p>
 *   
 * <b>This</b> class users <code>ApplicationI</code> return status values to indicate success, warning or errors to the client.
 *   
 * @see ApplicationI
 * @see ReturnDtoWriter
 * 
 * [License]
 * @author John Stewart
 */
//WF10 TODO @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@SuppressWarnings("serial")
public class ReturnDto implements Serializable, ApplicationI {

	final static private int    SUBSTRING_ENCODE_MIN       = 10;
	final static private String SUBSTRING_ENCODE_DELIMETER = "$$";

	/** 
	 * Encoded <code>String</code>s text list
     */  
	private ArrayList<EncodeDto> encodings = null;
	
	/** 
	 * Fields to be encoded
     */
	private ArrayList<FieldX> encodedFields = null;
	
	/** 
	 * Return Status code.<br>
	 * Possible values are:
	 * <ul>0 - ApplicationI.RETURN_STATUS_OK</ul>
	 * <ul>1 - ApplicationI.RETURN_STATUS_WARNING</ul>
	 * <ul>2 - ApplicationI.RETURN_STATUS_ERROR</ul><p>
	 * 
	 * Note: The client <code>app/common/remote.js</code> javascript decide further action based on this value. 
	 * Note: This flag <b>must</b> be instantiated before sent to client to indicate this is a ReturnDto object
	 * 
	 * @see ApplicationI
	 * @see ReturnDtoWriter
     */ 
	@JsonProperty(value="_s")
	public int returnStatus;
	
	
	/** 
	 * Return Status Message.<br>
	 * Message is returned using the clients current language code.
     */
	@JsonProperty(value="m")
	public ReturnMessageDto messageObject;
	
	
	/** 
	 * Status ID.<br>
	 * Finer gain error status, eg:
	 * <ul>ApplicationI.RETURN_STATUS_MAX_ROWS</ul>
	 * <ul>ApplicationI.RETURN_STATUS_INVALID</ul>
	 * 
	 * @see ApplicationI
	 * @see ReturnDtoWriter 
     */ 
	@JsonProperty(value="i")
	public Integer _status_id;
	
	
	/** 
	 * Return object.
     */ 
	@JsonProperty(value="o")
	public Object object;
	
	/** 
	 * Return model.
     */ 
	@JsonProperty(value="d")
	public Object definition;
	
	
	/** 
	 * Return control object.<br>
	 * Depends on the server call context, eg a query call may return the query DTO.
     */
	@JsonProperty(value="c")
	public Object control;
	
	/** 
	 * Return sql object.<br>
	 * Current call sql values.
     */
	@JsonProperty(value="q")
	public Object sql;
	
	
	/** 
	 * Encoding flag.<br>
	 * If <code>true</code> the return object strings maybe encoded for compression.<br>
	 * See <code>DtoEncode</code>.<p>
	 */ 
	@JsonProperty(value="w")
	public Boolean encoding;
	
	 
	//////////////////////// Methods //////////////////////////////////////////////////////////////
	
	/**
	 * Empty constructor will assign status ApplicationI.RETURN_STATUS_ERROR.
	 */
	public ReturnDto() {
		returnStatus = RETURN_STATUS_ERROR;
	}

	/**
	 * Object constructor will assign status ApplicationI.RETURN_STATUS_OK.
	 * @param Object return object
	 */
	public ReturnDto(Object object) {
		returnStatus = RETURN_STATUS_OK;
		this.object = object;
	}
	
	/**
	 * Exception constructor will assign status ApplicationI.RETURN_STATUS_ERROR and place the Exception in the 
	 * return object for further processing in the <code>ReturnDtoInterceptor</code>.
	 * @param Exception 
	 * @see ReturnDtoWriter
	 */
	public ReturnDto(Exception e) {
		returnStatus = RETURN_STATUS_ERROR;
		this.object = e;
	}
	
		
	/**
	 * Is the current object encodeable?
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@JsonIgnore
	public boolean isEncodeable() throws Exception{
		if (encoding != null){
			return encoding;
		}
		
		if (object == null || !(object instanceof List)){
			return false;
		}
		
		encodedFields = getFields((List)object);
		encoding = encodedFields.size() > 0;
		return encoding;
	}


	
	
	/**
	 * Start encode process.
	 */
	@SuppressWarnings("rawtypes")
	@JsonIgnore
	public void encode() throws Exception{
		encodings = new ArrayList<EncodeDto>();
		encode((List)getObject());
	}

	
	
	/**
	 * Recurse method to iterate through all <code>List</code>s and:
	 * <ul>- test if they are to be encoded</ul>
	 * <ul>- get fields to encode</ul>
	 * <ul>- call method to encode List</ul>
	 * 
	 * @param List 
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes"})
	private void encode(List list) throws Exception{
		
		if (list.isEmpty()){
			return;
		}
		
		//Look at first entry, if list then use recursive call
		Object obj = list.get(0);
		if (obj instanceof List){
			for (Object listx: list){
				encode((List)listx);
			}
		}
		
		if (list.size() < 2){
			return;
		}
		
		if (encodedFields == null){
			encodedFields = getFields(list);
		}
		
		if (encodedFields.size() == 0){
			return;
		}
		
		encode(list, encodedFields);
	}
	
	
	/**
	 * Get list of fields to encode 
	 * @param List
	 * @return ArrayList field list
	 * @throws Exception
	 */
	@SuppressWarnings({"rawtypes"})
	private ArrayList<FieldX> getFields(List list) throws Exception{
		
		ArrayList<FieldX> fields = new ArrayList<>();

		if (list.size() == 0){
			return fields;
		}
		Object obj = list.get(0);
		
		
		for (Field field: obj.getClass().getFields()) {
			field.setAccessible(true);
			
			//Only encode actual dto fields that have been specifically annotated
			DtoEncode d = field.getAnnotation(DtoEncode.class);
			if (d == null){
				continue;
			}
			
			String type = field.getType().getName();
			type = type.substring(type.lastIndexOf(".") + 1);
			
			//Recursive call
			if (type.equalsIgnoreCase("List")){
				fields.add(new FieldX(field, obj.getClass().getName(), true));
				List listx = (List)field.get(obj);
				ArrayList<FieldX> fieldsX = getFields(listx);  
				fields.addAll(fieldsX);
				continue;
			}
			
			if (!type.equalsIgnoreCase("String")){
				continue;
			}
			
			
			fields.add(new FieldX(field, obj.getClass().getName(), d));
		}
		
		
		//Find id field
		for (FieldX f: fields){
			
			if (f.id_field_name == null){
				continue;
			}
			
			for (Field field: obj.getClass().getFields()) {
				field.setAccessible(true);
				
				String name = field.getName();
				if (name.equals(f.id_field_name)){
					f.field_id = field;
					break;
				}
			}
			
		}
		
		return fields;
	}
	
	
	/**
	 * Encode list
	 * @param List to encode
	 * @param List fields to encode
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	private void encode (List list, ArrayList<FieldX> fields) throws Exception{
		
		//Encode strings using exact matches
		for (int i=0; i<list.size(); i++){
			Object obj = list.get(i);
			
			for (FieldX fieldx: fields) {
				if (!fieldx.clazz.equals(obj.getClass().getName())){
					continue;
				}
				
				if (fieldx.list){
					encode ((List)fieldx.field.get(obj), fields);
					continue;
				}
				
				
				String string = (String)fieldx.field.get(obj);
				if (string != null && !string.isEmpty()){
					
					boolean encode = true;
					String  sub    = null;
					int     id     = 0;

					//Find a current exact match
					for (int j=0; j<encodings.size(); j++){
						EncodeDto e = encodings.get(j);
						if (e.f.field.getName().equals(fieldx.field.getName())
								&& e.t.equals(string)){
							id = e.i; 
							encode = false;
							e.count++;
							break;
						}
					}
					
					//Ok, create encoding for other records
					if (encode){
						encodings.add(new EncodeDto(i, string, fieldx));
					}
					
					
					if (encode){
						//do nothing
					}
					else if (fieldx.field_id != null){
						fieldx.field_id.set(obj, id);
						fieldx.field.set(obj, sub);
					}
					else{
						fieldx.field.set(obj,"" + id);
					}
				}
			}
		}
		
		//order via longest
		Collections.sort(encodings, new Comparator<EncodeDto>() {
			public int compare(EncodeDto o1, EncodeDto o2) {
				int x = o2.t.length() - o1.t.length();
				if (x != 0) return x;
				return o1.t.compareTo(o2.t);
			}
		});
		
		//Re iterate encodings and find best matches using substrings
		for (int i=0; i<list.size(); i++){
			Object obj = list.get(i);
			
			for (FieldX fieldx: fields) {
				if (!fieldx.clazz.equals(obj.getClass().getName())
						|| fieldx.list
						|| fieldx.field_id == null
						|| !fieldx.isSubstring){
					continue;
				}
				
				Integer id = (Integer)fieldx.field_id.get(obj);
				if (id != null){
					continue;
				}
				
				int count = countOfOtherRecords(i, fieldx);
				if (count > 0){
					continue;
				}
				
				String string = (String)fieldx.field.get(obj);
				if (string != null && !string.isEmpty()){
					
					String sub = null;
					Integer [] o = findBestMatch(i, string, fieldx);
					if (o != null){
						id = o[0];
						sub = o[1] + SUBSTRING_ENCODE_DELIMETER + string.substring(o[1]);
						fieldx.field_id.set(obj, id);
						fieldx.field.set(obj, sub);
					}
					
				}
			}
		}

		
		
	}
	
	/**
	 * Find best match using a substring
	 * @param int record index
	 * @param String field value
	 * @param Fieldx field definition
	 * @return index of list and substring length (or null if not fit)
	 */
	private Integer [] findBestMatch(int index, String string, FieldX fieldx){
		
		for (int i = string.length(); i >= SUBSTRING_ENCODE_MIN; i--){
			int pointer = findBestMatch(index, string, i, fieldx);
			if (pointer > -1){
				return new Integer[] {pointer, i};
			}
		}
		return null;
	}
	
	
	/**
	 * Find best match using a substring
	 * 
	 * @param int record index
	 * @param String field value
	 * @param int field value string length to compare to
	 * @param Fieldx field definition
	 * @return
	 */
	private int findBestMatch(int index, String string, int length, FieldX fieldx){
		String s = string.substring(0, length);
		for (int k=0; k<encodings.size(); k++){
			EncodeDto e = encodings.get(k);
			if (e.f.field.getName().equals(fieldx.field.getName())
					&& e.i != index
					&& e.count > 0
					&& e.t.length() > length
					&& e.t.substring(0, length).equals(s)){
				return e.i;
			}
		}
		return -1;
	}
	
	/**
	 * Find count of other records that point to the passed in record index
	 * 
	 * @param int record index
	 * @return
	 */
	private int countOfOtherRecords(int index, FieldX fieldx){
		for (int k=0; k<encodings.size(); k++){
			EncodeDto e = encodings.get(k);
			if (e.f.field.getName().equals(fieldx.field.getName())
					&& e.i == index){
				return e.count;
			}
		}
		return 0;
	}

	
	
	
	
	/**
	 * Class to contain <code>Field</code>s to encode.
	 */
	class FieldX {
		Field field;
		Field field_id;
		String clazz;
		String id_field_name = null;
		boolean isSubstring = false;
		boolean list;
		
		
		public FieldX(Field field, String clazz, DtoEncode d) {
			this.field = field;
			this.clazz = clazz;
			this.list  = false;
			id_field_name = d.id();
			isSubstring = d.isSub();
		}
		public FieldX(Field field, String clazz, boolean list) {
			this.field = field;
			this.clazz = clazz;
			this.list  = list;
		}
		
	}
	
	/**
	 * Class to contain encoded <code>String</code> with associated <code>id</code>.
	 */
	class EncodeDto implements Serializable{
		/** index */ private int i;
		/** text  */ private String t;
		/** field */ private FieldX f;

		/** fields pointing to this */ private int count;
		
		public EncodeDto(int i, String t, FieldX f) {
			this.i = i;
			this.t = t;
			this.f = f;
			count = 0;
		}
		
	}
	
	
	
	//////////////////////// Getters / Setters //////////////////////////////////////////////////////////////
	
	@JsonIgnore
	public int getStatus() {
		return returnStatus;
	}
	public void setStatus(int status) {
		this.returnStatus = status;
	}

	@JsonIgnore
	public ReturnMessageDto getMessage() {
		return messageObject;
	}
	public void setMessage(ReturnMessageDto message) {
		this.messageObject = message;
	}

	@JsonIgnore
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	
	@JsonIgnore
	public Object getModel() {
		return definition;
	}
	public void setModel(Object object) {
		this.definition = object;
	}
	
	@JsonIgnore
	public Object getControlObject() {
		return control;
	}
	public void setControlObject(Object object) {
		this.control = object;
	}
	
	
	@JsonIgnore
	public Boolean getEncoded() {
		return encoding;
	}
	public void setEncoded(Boolean encoded) {
		this.encoding = encoded;
	}
	
	
	@JsonIgnore
	public Integer getStatusId() {
		return _status_id;
	}
	public void setStatusId(Integer statusId) {
		this._status_id = statusId;
	}

	
	@JsonIgnore
	public Object getSqlObject() {
		return sql;
	}
	public ReturnDto setSqlObject(Object sql) {
		this.sql = sql;
		return this;
	}


	
}
