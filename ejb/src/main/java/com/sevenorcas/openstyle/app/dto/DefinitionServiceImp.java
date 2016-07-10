package com.sevenorcas.openstyle.app.dto;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.ApplicationI;
import com.sevenorcas.openstyle.app.Utilities;
import com.sevenorcas.openstyle.app.company.Company;
import com.sevenorcas.openstyle.app.company.CompanyService;
import com.sevenorcas.openstyle.app.entity.EntityI;
import com.sevenorcas.openstyle.app.entity.Field;
import com.sevenorcas.openstyle.app.perm.Permission;
import com.sevenorcas.openstyle.app.user.UserParam;



/**
 * Entity definitions service<p>
 * 
 * This class inspects <code>@Field</code> annotated entities and their respective DTO's to provide a list of <code>FieldDefDto</code> objects. These objects
 * contain specific definitions for each annotated field within the entity and / or DTO.<p>
 * 
 * Comments:<ul>
 *     
 *     <li>A DTO may reference an Entity via its <code>@Dto(entity="xxx")</code> annotation. The annotation will cause <b>this</b> class to 
 *         inspect the entity for matching field / method names with the DTO. If found then the entities <code>@Field</code> annotation will be used (unless the DTO 
 *         provides its own overriding <code>@Field</code> annotation).</li><br>
 * 
 *     <li>Field definitions are cumulative, ie a super class may define a field with some attributes and the DTO class may add more attributes (or override the attributes
 *         of the super class).</li><br>
 *         
 *     <li>The <code>@JsonProperty(value=)</code> annotation is also read and passed to the <code>FieldDefDto</code> object (ie this is the JSON field name).</li><br>
 *     
 *     <li>If the <code>@Field.isExportJson</code> annotation value is <code>FALSE</code>, then the field / method is ignored.</li><br>
 *     
 *     <li>Both fields and methods can be annotated with the <code>@Field</code> annotation.</li><br>
 *     
 *      
 *      
 * </ul>
 * Note:   
 *  
 * [License]
 * @author John Stewart
 */

@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class DefinitionServiceImp implements DefinitionService, ApplicationI {
		
    @EJB private CompanyService companyService;
    
    
	/** Default Constructor */
	public DefinitionServiceImp() {}
	
	
	/**
	 * Return a list of <code>FieldDefDto</code> objects containing the field definitions for the passed in Entity / DTO object class name.
	 * @param String class name of Entity / DTO object. This can be the fully qualified name or the suffix after the <code>ApplicationI.ENTITY_BASE_PACKAGE</code> prefix.
	 * @param UserParam contains user permission key-value pairs.  
	 * @return List containing the field definitions 
	 */
	public ArrayList<FieldDefDto> definitions(String classname, UserParam userParam) throws Exception{
		
	    Company company = userParam != null ? companyService.findByNr(userParam, userParam.getCompany()) : null;
		Hashtable<String, FieldDefDto> table = definitionsTable(classname, userParam, company);
		
		ArrayList<FieldDefDto> list = new ArrayList<>(table.size());
		ArrayList<String> fields = new ArrayList<>(table.size());

		Enumeration<String> keys = table.keys();
		while (keys.hasMoreElements()){
			fields.add(keys.nextElement());
		}
		
		Collections.sort(fields);
		for (String field: fields){
			list.add(table.get(field));
		}
		
		return list;
	}
		
	
	/**
	 * Return a <code>Hashtable</code> of <code>FieldDefDto</code> objects containing the field name (as key) and field definition (as value) for the passed in 
	 * Entity / DTO object class name.
	 * 
	 * This method inspects classes in order of:<ol>
	 *     <li>actual class</li>
	 *     <li>refer class</li>
	 * </ol><p>
	 * 
	 * @param String class name of Entity / DTO object. This can be the fully qualified name or the suffix after the <code>ApplicationI.ENTITY_BASE_PACKAGE</code> prefix.
	 * @param UserParam contains user permission key-value pairs. 
	 * @param Company object
	 * @return Hashtable containing the field name (as key) and field definition (as value) 
	 */
	@SuppressWarnings("rawtypes")
	public Hashtable<String, FieldDefDto> definitionsTable(String classname, UserParam userParam, Company company) throws Exception{
		
		Class clazz = Utilities.findClass(classname);
		
		if (clazz == null){ //ToDo Throw error and let a filter handle it
			throw new Exception("ErrUnknown");
		}

		//Get class instance to get default values
		Object obj = Utilities.create(clazz, userParam, company);
		ArrayList<String> fields = new ArrayList<>();
		Hashtable<String, FieldDefDto> table = new Hashtable<>();
		
		//Get definitions from class
		definitions(classname, userParam, obj, clazz, table, fields, true);
		
		//Get definitions from refer entity classes
		String [] entClasses = findEntityClass(clazz);
		for (int i=0; entClasses != null && i<entClasses.length; i++){
			Class entityClazz = Utilities.findClass(entClasses[i], clazz);
			
			if (entityClazz != null){
				definitions(entClasses[i], userParam, Utilities.create(entityClazz, userParam, company), entityClazz, table, fields, false);

				//Get permission form 1. entity + user param's, or if unavailable then use default base entity permission 
				setPermissions (entityClazz, userParam, table);
			}
		}
		//Get permission form current DTO class 
		if (entClasses == null){
			setPermissions (clazz, userParam, table);
		}
		
		
		return table;
	}
	
	
	
	/**
	 * Set the permission field's values via a lookup on the <code>entity.permissionKey</code> method. The key is then used to lookup the 
	 * <code>UserParam</code> permissions</li>. 
	 * 
	 * If:<ul>
	 *     <li>If the class has not entity annotated, then the current dto class can be used.</li>
	 *     <li>If entity key is <code>EntityI.DEFAULT_PERM</code> then user has full CRUD permissions.</li>
	 *     <li>If entity or userParam keys are not found, then return an empty string (ie no permission).</li>
	 * </ul><p>     
	 * 
	 * Thanks to http://stackoverflow.com/questions/2599440/how-can-i-access-a-private-constructor-of-a-class
	 * 
	 * @param Class of Entity
	 * @param UserParam user object
	 * @param Hashtable of fields
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setPermissions (Class entityClazz, UserParam userParam, Hashtable<String, FieldDefDto> table){
		
		String key = null;
		java.lang.reflect.Method method = Utilities.findMethod(entityClazz, "permissionKey");
		
		//Find permission key entity
	    try {
	    	Constructor constructor = entityClazz.getDeclaredConstructor();
	    	constructor.setAccessible(true);
	    	Object objX = constructor.newInstance();
	    	key = (String)method.invoke(objX, new Object[0]);
	    } catch (Exception e) {
	    	//e.printStackTrace();
	    }
		
	    //Get superclass definitions		
		if (key == null && entityClazz.getSuperclass() != null){
			setPermissions (entityClazz.getSuperclass(), userParam, table);
			return;
		}
		
		//Unknown class
		if (key == null){
			return;
		}
		
		String value = userParam != null? userParam.getPermission(key) : null;
		
		if (value == null && key.equals(EntityI.DEFAULT_PERM)){
			value = "CRUD";
		}
		else if (userParam != null && userParam.isAdmin()){
			value = "CRUD";
		}
		
		value = value != null? value : Permission.NO_PERMISSION;
		FieldDefPermDto f = new FieldDefPermDto(key, value);
		table.put("permission", f);
	}
	
	
	
	
	/**
	 * Test if field can be added to definition
	 * @param String field name
	 * @param List fields
	 * @param Boolean true=add to fields list, false=if not in fields list then don't define
	 * @return true = include field in definition
	 */
	private boolean isDefinition(String name, ArrayList<String> fields, boolean addFields){
		if (addFields){
	    	if (!fields.contains(name)){
	    		fields.add(name);
	    	}
	    }
	    else{
	    	if (!fields.contains(name)){
	    		return false;
	    	}
	    }
		return true;
	}
	
	/**
	 * Build field definitions for passed in class.<p>
	 * 
	 * This is a recursive method that inspects super classes via the <code>@Dto(entity="xxx")</code> annotation. Order of inspection is:<ol>
	 *     <li>super classes</li>
	 *     <li>actual class</li>
	 * </ol><p>
	 *   
	 * Note that order allows the implementing class to 'overwrite' referred and super class definitions. 
	 * 
	 * @param String class name
	 * @param UserParam contains user permission key-value pairs.
	 * @param Object instance of class
	 * @param Class 
	 * @param Hashtable containing the field name (as key) and field definition (as value)
	 * @param ArrayList of field names
	 * @param boolean true = add all fields to field names list, false == only use field in field names list 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private void definitions(String classname, UserParam userParam, Object obj, Class clazz, Hashtable<String, FieldDefDto> table, ArrayList<String> fields, boolean addFields) throws Exception{
		
		//Get superclass definitions		
		if (clazz.getSuperclass() != null){
			definitions(classname, userParam, obj, clazz.getSuperclass(), table, fields, addFields);
		}
		
		FieldDefDto def = null;
		
		//Annotated fields
		for (java.lang.reflect.Field field: clazz.getDeclaredFields()) {
		    field.setAccessible(true);
			String name = field.getName();
		    
		    if (!isDefinition(name, fields, addFields)){
		    	continue;
		    }
		    
		    JsonProperty json = field.getAnnotation(JsonProperty.class);
		    DtoEncode encode = field.getAnnotation(DtoEncode.class);
		    Field f = field.getAnnotation(Field.class);
		    
		    if (f != null || json != null){
		    	
		    	def = table.get(name);
		    	if (def == null){
		    		table.put(name, def = new FieldDefDto(name, FieldDefDto.getAccessor (f, field)));
		    	}
		    	
		    	Object initialiseValue = obj != null? field.get(obj) : null;

		    	Class<?> c = field.getType();
		    	if (c.isArray()){
		    	    int s = 100; //default
		    	    //Thanks to http://stackoverflow.com/questions/15907178/java-reflection-get-size-of-array-object
		    	    if (initialiseValue != null){
		    	        s = Array.getLength(initialiseValue);
		    	    }
		    	    def.setArray(s); 
		    	}
		    	
		    	definitionField(def, f, json, encode, field.getType().getName(), initialiseValue, classname, userParam);
		   }
		}
		
		//Annotated methods
		for (java.lang.reflect.Method method: clazz.getDeclaredMethods()) {
		    method.setAccessible(true);
			String name = method.getName();
		    
		    if (!isDefinition(name, fields, addFields)){
		    	continue;
		    }
		    
		    JsonProperty json = method.getAnnotation(JsonProperty.class);
		    DtoEncode encode = method.getAnnotation(DtoEncode.class);
		    Field f = method.getAnnotation(Field.class);
		    if (f != null || json != null){
		    	
		    	def = table.get(name);
		    	if (def == null){
		    		table.put(name, def = new FieldDefDto(name, FieldDefDto.getAccessor (f, method)));
		    	}
		    	
		    	String type = method.getGenericReturnType() != null? method.getGenericReturnType().toString() : null; 
		    	type = type != null? type : "String";
		    	definitionField(def, f, json, encode, type, null, classname, userParam);
		   }
		}
		
		
	}


	/**
	 * Populate the passed in <code>FieldDefDto</code> object with the <code>@Field</code> annotations.
	 * @param FieldDefDto object
	 * @param Field annotation
	 * @param JsonProperty annotation
	 * @param DtoEncode annotation
	 * @param String java type 
	 * @param Object initialize (eg new record) value
	 * @param String class name
	 * @param UserParam contains user permission key-value pairs.
	 * @throws Exception
	 */
    private void definitionField(FieldDefDto def, Field fieldAnno, JsonProperty json, DtoEncode encode, String type, Object initialiseValue, String classname, UserParam userParam) throws Exception{


    	//Field type
    	if (type == null){
    		type = "String"; //Default 
    	}
    	else if (type.lastIndexOf(".") != -1){
    		type = type.substring(type.lastIndexOf(".") + 1);
    	}
    	
    	def.setType(FieldDefDto.getType(type));
    	
    	//Force string default
	    if (def.getType() != null && def.getType() == TYPE_STRING){
		    def.setMax(DEFAULT_MAX_STRING);
	    }
    	
	    
    	//Dto (JSON) name
    	if (json != null && json.value().length() > 0){
    		def.setDto(json.value());
    	}
    	
    	//Is this field encoded?
    	if (encode != null){
    		def.setEncode(encode.id());
    		if (encode.isSub()){
    			def.setEncodeSub(true);
    		}
    	}
    	
    	if (initialiseValue != null){
		    def.setNewRecordValue(initialiseValue);
	    }
    	
	    if (fieldAnno != null){
		    
		    //Client accessor name
		    if (fieldAnno.accessor().length() > 0){
		    	def.setAccessor(FieldDefDto.formatAccessor (fieldAnno.accessor()));
		    }
			
		    //Recursive call to get child definition
			if (fieldAnno.childClass().length() > 0){
			    String child = fieldAnno.childClass();
			   
			    //Inner class
			    if (child.startsWith("$")){
				    child = classname + child;
			    }
			   	def.setChildObject(definitions(child, userParam));
			}
		   
		    if (fieldAnno.label().length() > 0){
			    def.setLabel(fieldAnno.label());
		    }
		    if (fieldAnno.values().length() > 0){
		 	    def.setValues(fieldAnno.values());
		    }
		   
		    if (fieldAnno.min() > 0){
			    def.setMin(fieldAnno.min());
		    }
		   
		    if (fieldAnno.max() > 0){
			    def.setMax(fieldAnno.max());
		    }
		   
		    if (fieldAnno.notNull()){
			    def.setNotNull(fieldAnno.notNull());
		    }
		    if (fieldAnno.format().length() > 0){
			    def.setFormat(fieldAnno.format());
		    }
		   
		    if (fieldAnno.appType() > 0){
			    def.setApplicationType(fieldAnno.appType());
		    }
		   
		    if (fieldAnno.lang()){
			    def.setLang(fieldAnno.lang());
		    }
		   
		    if (fieldAnno.readonly()){
			    def.setEdit("false");
		    }
		    else if (fieldAnno.edit().length() > 0){
			    def.setEdit(fieldAnno.edit());
		    }
		    //Derive edit-ability
		    else{
			    if (fieldAnno.min() > 0
			    	   || fieldAnno.max() > 0
			    	   || fieldAnno.notNull()
					   || fieldAnno.appType() == APP_TYPE_SORT_NR){
			 	    def.setEdit("true");   
			    }
			    else{
				    def.setEdit("false");
			    }
		    }
		   
		   
		    //Application Type defaults (if not previously set)
		    switch(fieldAnno.appType()){
		   
		    	case APP_TYPE_ID:
		    	case APP_TYPE_REF_CODE:
		    	case APP_TYPE_KEY:
		    		def.setNotNull(true);
		    		break;
		    		
		    	case APP_TYPE_SORT_NR:
		    		if (fieldAnno.min() == 0D){
		    			def.setMin(1D);
		 		    }		
		    		if (fieldAnno.max() == 0D){
		 			   def.setMax(6D);
		 		    }
		    		if (!fieldAnno.readonly()){
					    def.setEdit("true");
				    }
		    		def.setNotNull(true);
		    		break;
		   
		   }
		   
		   
	    }		
	}

    
	
	/**
	 * Return a referred entity definition classes via its <code>@Dto(entity="xxx")</code> annotation
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	static public String[] findEntityClass(Class clazz){
		Annotation[] annos = clazz.getAnnotations(); 
		for (Annotation a: annos){
			if (a instanceof Dto){
				Dto dto = (Dto)a;
				
				//Inferred dto
				if(dto.entity().length() == 0){
					String d = clazz.getName().substring(0, clazz.getName().length() - 3); //ie minus 'Dto'
					return new String []{d};
				}
				
				return dto.entity().split(",");
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	
}
