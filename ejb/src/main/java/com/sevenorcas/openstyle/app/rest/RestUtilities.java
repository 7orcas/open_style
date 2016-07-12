package com.sevenorcas.openstyle.app.rest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.ApplicationI;
import com.sevenorcas.openstyle.app.Utilities;
import com.sevenorcas.openstyle.app.dto.DefinitionService;
import com.sevenorcas.openstyle.app.dto.FieldDefDto;
import com.sevenorcas.openstyle.app.entity.ValidationException;
import com.sevenorcas.openstyle.app.log.ApplicationLog;
import com.sevenorcas.openstyle.app.sql.SqlI;


/**
 * REST utility class.<p>
 * 
 * <b>This</b> class contains essential attributes and methods common to most REST implementations.<p>
 * 
 * TODO: Update docu (validation and Json deserialization process has changed).
 *  
 * [License] 
 * @author John Stewart
 */
public class RestUtilities implements ApplicationI {

	/** 
	 * Definition Service.<br>
	 * Used to get Entity / DTO field definitions. 
	 */
	static private DefinitionService definitionService;	
	

	/**
     * Deserialize JSON string into an <code>Object</code> of the passed type.<p>
     * 
     * The method uses Java reflection to match JSON field names to the <code>Class</code> field names. A
     * <code>ValidationException</code> is thrown if:
     * <ul>- missing field name in <code>Class</code></ul>
     * <ul>- a mismatch in <code>Class</code> field type and JSON data</ul>
     * <p> 
     * 
     * TODO: Implement deserialization via Jackson tools 
     * 
     * @param Object to instantiate
     * @param String JSON string
	 * @return 
     */
	static public <T> T deserializeJson(T object, String json) throws ValidationException {
		
		try {
			json = json.trim();
			json = json.startsWith("{")? json.substring(1) : json;
			json = json.endsWith("}")? json.substring(0,json.length()-1) : json;
			if (json.length() == 0){
				return object;
			}
		} catch (Exception e){
			throw new ValidationException("InValCall");
		}
		
		
		
		String [] split1 = json.split(",");
		//If json values contain ',' then these will cause a split. Assumes [0] is ok.
		for (int i = split1.length - 1; i > 0 ; i--){
			if (split1[i].indexOf(":") == -1){
				split1[i-1] = split1[i-1] + "," + split1[i];
				split1[i] = "";
			}
		}
		
		
		ValidationException ex = null;
		Hashtable<String, FieldDefDto> fields = null;
		
		try {
		    definitionService = Utilities.lookupService(definitionService, "DefinitionServiceImp");
			fields = definitionService.definitionsTable(object.getClass().getName(), null, null);
		} catch (Exception e) {
			ApplicationLog.info("Can't get definitions table for " + object.getClass().getName());
		}
		
		
		
		for (String s: split1){
			if (s.length() == 0){
				continue;
			}

			//If json values contain ':' then these will cause a split. Assumes [0] is ok.
			int index = s.indexOf(":");
			String [] split2 = {s.substring(0, index), s.substring(index+1)};
			
			String name = split2[0].substring(1, split2[0].length() - 1);
			Object value = split2[1].startsWith("\"")? split2[1].substring(1, split2[1].length() - 1) : split2[1];
			
			java.lang.reflect.Field field = null;

			try{
				field = findField(name, object.getClass());
				
				String type = field.getType().getName();
				if (type.indexOf("List") != -1){
					continue;
				}
				
				if (value instanceof String && !testString(value)){
					continue;
				}
				
				value = deserializeJsonToObject (type, value);
				field.set(object, value);
				
			} catch (Exception x) {
				ApplicationLog.info(x.getMessage());
				
				if (field != null){
					if (ex == null){
						ex = new ValidationException();
					}
					
					FieldDefDto f = fields != null? fields.get(field.getName()) : null;
					String fieldname = null;
					if (f != null){
						fieldname = f.getLabel() != null? f.getLabel() : f.getAccessor();
					}
					else{
						fieldname = field.getName();
					}
					
					ex.addMessageList(fieldname, "InvalidEntry");
				}
				
				
			}
		}
		
		if (ex != null){
			throw ex;
		}
			
		return object;
	}

	
	/**
	 * Convert string into correctly formated value
	 * @param value
	 * @return
	 */
	static public Object deserializeJsonToObject (String type, Object value) throws Exception{
		
		//Test for array
		if (value instanceof String
				&& value.toString().startsWith("[")
				&& value.toString().endsWith("]")){
			String s = (String)value;
			s = s.substring(1, s.length()-1);
			String [] s1 = s.split(",");
			Object [] array = null;
			
			if (type.indexOf("Integer") != -1){
				array = new Integer[s1.length];
			}
			else if (type.indexOf("Long") != -1){
				array = new Long[s1.length];
			}
			else if (type.indexOf("Double") != -1){
				array = new Double[s1.length];
			}
			else if (type.indexOf("Boolean") != -1){
				array = new Boolean[s1.length];
			}
			else if (type.indexOf("Date") != -1){
				array = new Date[s1.length];
			}
			else if (type.indexOf("Ljava.lang.String") != -1){
                array = new String[s1.length];
            }
			
			
			for (int i=0; i<s1.length; i++){
				String string = s1[i];
				if (string.startsWith("\"")){
					string = string.length() > 1? string.substring(1) : "null";
				}
				if (string.endsWith("\"")){
					string = string.substring(0,string.length()-1);
				}
				
				if (string.equals("null")){
					array[i] = null;
				}
				else{
					array[i] =  deserializeJsonToObject (type, (Object)string);
				}
				
			}
			return array;
		}
		
		if (type.indexOf("Integer") != -1 || type.indexOf("int") != -1){
			value = Integer.parseInt(value.toString().trim());
		}
		else if (type.indexOf("Long") != -1){
			value = Long.parseLong(value.toString().trim());
		}
		else if (type.indexOf("Double") != -1){
			value = value.toString().replace(",", ".");
			value = Double.parseDouble(value.toString().trim());
		}
		else if (type.indexOf("Boolean") != -1){
			value = Boolean.parseBoolean(value.toString().trim());
		}
		else if (type.indexOf("Date") != -1){
			if (value instanceof String){
				value = deserializeJsonDate ((String)value);
			}
			SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT_JSON);
			value = f.parse(value.toString());
		}
		return value;
	}
	
	
	/**
	 * Convert string into correctly formated date string (for JSON)
	 * @param value
	 * @return
	 */
	static private String deserializeJsonDate (String value){
		
		String x = value.toString().trim().replace(",", ".");
		x = x.replace("/", ".");
		x = x.replace("-", ".");
		x = x.length() > 10? x.substring(0, 10) : x; 
		
		//assume 2 digit year passed in
		if (x.indexOf(".") != -1 && x.length() == 8){
			x = x.substring(0, 6) + "20" + x.substring(6);
		}
		else if (x.indexOf(".") == -1 && x.length() == 6){
			x = x.substring(0, 4) + "20" + x.substring(4);
		}
		
		//assume no year passed in, use current
		if (x.indexOf(".") != -1 && x.length() == 5){
			x = x + "." + Calendar.getInstance().get(Calendar.YEAR);
		}
		else if (x.indexOf(".") == -1 && x.length() == 4){
			x = x + Calendar.getInstance().get(Calendar.YEAR);
		}
		
		
		//assume no delimiter passed in
		if (x.indexOf(".") == -1 && x.length() == 8){
			x = x.substring(0, 2) + "." + x.substring(2, 4) + "." + x.substring(4);
		}
		return x;
	}
	
	/**
	 * Find a field via:<ul>
	 *     <li>field name</li>
	 *     <li><code>@JsonProperty</code> annotation value</li>
	 *     <li>super class</li>
	 * </ul>
	 * @param String field name to find
	 * @param Class of object
	 * @return java.lang.reflect.Field (or null if not found)
	 */
	static private java.lang.reflect.Field findField(String name, Class<?> clazz){
		
		//Annotated fields
		for (java.lang.reflect.Field field: clazz.getDeclaredFields()) {
		    field.setAccessible(true);
			String nameX = field.getName();
			
			if (nameX.equals(name)){
				return field;
			}
		    
			JsonProperty json = field.getAnnotation(JsonProperty.class);
			if (json != null && json.value().equals(name)){
				return field;
			}
		}
		
		if (clazz.getSuperclass() != null){
			return findField(name, clazz.getSuperclass());
		}
		
		return null;
	}
	
	
	/**
	 * Test passed in <code>Object</code> is a non empty <code>String</code>. 
	 * @param Object to test
	 * @return true/false
	 */
	static private boolean testString(Object value){
		return value != null
				&& value.toString().trim().length() > 0;
	}

	
	/**
	 * Add row to lookup results if there more records to be retrieved.
	 * @param List of LookupDto objects
	 * @param SqlI object
	 * @return
	 */
	static public void addLookupMore(List<LookupDto> list, SqlI sql){
		if (sql == null 
				|| sql.getLimit() == null 
				|| sql.getCount() == null){
			return ;
		}
		if (sql.getCount() > sql.getLimit()){
			list.add(new LookupDto("...+" + (sql.getCount() - sql.getLimit())));
		}
		
	}
	
	
}
