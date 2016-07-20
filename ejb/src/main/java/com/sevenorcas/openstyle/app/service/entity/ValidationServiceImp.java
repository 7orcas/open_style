package com.sevenorcas.openstyle.app.service.entity;

import java.util.Hashtable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Transient;

import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.application.Utilities;
import com.sevenorcas.openstyle.app.service.dto.BaseDto;
import com.sevenorcas.openstyle.app.service.dto.DefinitionService;
import com.sevenorcas.openstyle.app.service.dto.FieldDefDto;


/**
 * Entity validation service.<p>
 * 
 * Each field in an entity is validated via its <code>@Field</code> annotation, including a number of element options.<br>
 * Validation processing flow is:
 * <ul>1. Client calls <code>REST</code> URL.</ul>
 * <ul>2. <code>REST</code> method will deserialize JSON to its respective entity.</ul>
 * <ul>3. After deserialization, <b>this</b> service is called to validate the entity.</ul>
 * <ul>4. If a validation error occurs then a <code>ValidationException</code> is thrown and normal exception processing then applies.</ul>
 * <p>   
 * 
 * 
 * @see Field
 * @see ValidationException  
 *
 * [License]
 * @author John Stewart
 */

@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class ValidationServiceImp implements ValidationService, ApplicationI {
	
	
	@EJB private DefinitionService definitionService;
	
	/**
	 * Public constructor
	 */
	public ValidationServiceImp() {}
	
	
	/**
	 * Validate the passed in object.
	 * The method throws a validation exception if any fields are not consistent with their <code>@Field</code> annotation definition 
	 */
	@SuppressWarnings("rawtypes")
	public void validate(Object obj) throws ValidationException, Exception{

		if (obj == null){ 
			throw new Exception("ErrUnknown");
		}

		String name = null;
		
		//If list then peek inside to get first object class
		if (obj instanceof List){
			List l = (List)obj;
			if (l.size() == 0){
				return;
			}
			name = l.get(0).getClass().getName();
		}
		else{
			name = obj.getClass().getName();
		}
		
		Hashtable<String, FieldDefDto> fields = definitionService.definitionsTable(name, null, null);
		
		ValidationException ex = null;
		ex = validate(obj, fields, ex);
		if (ex != null){
			throw ex;
		}
	}
	
	/**
	 * Validate the passed in entity.<br>
	 * Processing involves using Java reflection for each field in the entity and comparing its value with its <code>@Field</code> annotation.   
	 * Errors are cumulatively stored in an exception object. Once all fields and child objects are validated, then an exception is 
	 * thrown (if any validation errors exist).<p>  
	 * 
	 * Note: This is a recursive method where child objects within the entity are also validated against <b>this</b> method.
	 *  
	 * @param Object entity to validate
	 * @param Hashtable containing the field name (as key) and field definition (as value)
	 * @param ValidationException - current validation exception
	 * 
	 * @see Field
	 * @see ValidationException 
	 */
	@SuppressWarnings("rawtypes")
	public ValidationException validate(Object obj, Hashtable<String, FieldDefDto> fields, ValidationException ex) throws Exception{
		
		//Recursive call to this method
		if (obj instanceof List){
			List list = (List)obj;
		    for (Object o : list){
		    	ex = validate(o, fields, ex);
		    }
		}
		
		Long id = null;
		if (obj instanceof BaseDto){
			id = ((BaseDto)obj).getId();
		}
		
		
		
		
		for (java.lang.reflect.Field field: obj.getClass().getFields()) {
			field.setAccessible(true);
			
			if (field.isAnnotationPresent(Transient.class)){
			    continue;
			}
		    
			FieldDefDto def = fields.get(field.getName());
		    
		    if (def != null){
		    	Object value = field.get(obj);
		    	
		    	if (def.isNotNull() && value == null){
		    		ex = setMessage(ex, id, def, "NotNull");
		    	}
		    	
		    	if (value == null){
		    		continue;
		    	}
		    	
		    	
			    String typeS = field.getType().getName();
			    typeS = typeS.substring(typeS.lastIndexOf(".") + 1);
			    int type = FieldDefDto.getType(typeS);
			   
			   
			    //Test normal java types 
			    switch (type){
			   
			       case TYPE_STRING:
			    	   if (value instanceof String []){
			    		   for (Object objx: (String [])value){
			    			   if (objx != null){
			    				   validate(objx, fields, ex);
			    			   }
			    		   }
			    		   break;
			    	   }
			    	   ex = validate(ex, id, def, (String)value);
			    	   break;
			   
			       case TYPE_INTEGER:
			    	   if (value instanceof Integer []){
				    	   for (Object objx: (Integer [])value){
			    			   if (objx != null){
			    				   validate(objx, fields, ex);
			    			   }
			    		   }
				    	   break;
			    	   }
			    	   ex = validate(ex, id, def, (Integer)value);
			    	   break;
			    	   
			       case TYPE_DOUBLE:
			    	   if (value instanceof Double []){
				    	   for (Object objx: (Double [])value){
			    			   if (objx != null){
			    				   validate(objx, fields, ex);
			    			   }
			    		   }
				    	   break;
			    	   }
			    	   ex = validate(ex, id, def, (Double)value);
			    	   break;
			    	   
			       case TYPE_LIST:
			    	   ex = validate((List)value, fields, ex);
			    	   break;
			    	   
			       //TODO: Add more validations
			    }
			   
			   
			    if (!def.isApplicationType()){
			    	continue;
			    }
			   
			    //Test application types 
			    switch (def.getApplicationType()){
			   
			       case FIELD_TYPE_LOOKUP_REF:
			    	   ex = validateLookupValue(ex, id, def, value);
			    	   break;
			    	   
			       case FIELD_TYPE_CURRENCY:
			    	   ex = validateCurrency(ex, id, def, value);
			    	   break;
			    	   
			       //ToDo: Add more validations
			    	   
			       default:
			    	   //Do nothing
			    }
			   
			   
		    }
		}
		
		return ex;
	}

	
	
	/**
	 * Validate the passed in field string value.
	 * 
	 * @param ValidationException current exception object (maybe null)
	 * @param Long entity id
	 * @param FieldDefDto definition
	 * @param String field value
	 * @return ValidationException (created object if a validate error occurs)
	 */
	private ValidationException validate(ValidationException ex, Long id, FieldDefDto def, String value){
		
		if (def.isMin() && value.trim().length() < def.getMin()){
 		    ex = setMessage(ex, id, def, "MinLength%" + def.getMin().intValue());
 	    }
		
 	    else if (def.isMax() && def.getMax() > 0 && value.length() > def.getMax()){
 	    	ex = setMessage(ex, id, def, "MaxLength%" + def.getMax().intValue());
 	    }
		return ex;
	}
	
	
	/**
	 * Validate the passed in integer value.
	 * 
	 * @param ValidationException current exception object (maybe null)
	 * @param Long entity id
	 * @param FieldDefDto definition
	 * @param Integer field value
	 * @return ValidationException (created object if a validate error occurs)
	 */
	private ValidationException validate(ValidationException ex, Long id, FieldDefDto def, Integer value){
		
		if (def.isMin() && value.intValue() < def.getMin()){
 		    ex = setMessage(ex, id, def, "MinValue%" + def.getMin().intValue());
 	    }
 	    else if (def.isMax() && value.intValue() > def.getMax()){
 	    	ex = setMessage(ex, id, def, "MaxValue%" + def.getMax().intValue());
 	    }
		return ex;
	}
	
	
	/**
	 * Validate the passed in Double value.
	 * 
	 * @param ValidationException current exception object (maybe null)
	 * @param Long entity id
	 * @param FieldDefDto definition
	 * @param Double field value
	 * @return ValidationException (created object if a validate error occurs)
	 */
	private ValidationException validate(ValidationException ex, Long id, FieldDefDto def, Double value){
		
		if (def.isMin() && (value == null || value.doubleValue() < def.getMin())){
 		    ex = setMessage(ex, id, def, "MinValue%" + def.getMin());
 	    }
 	    else if (def.isMax() && value.doubleValue() > def.getMax()){
 	    	ex = setMessage(ex, id, def, "MaxValue%" + def.getMax());
 	    }
		return ex;
	}
	
	
	
	/**
	 * Validate the passed in currency value field.
	 * 
	 * @param ValidationException current exception object (maybe null)
	 * @param Long entity id
	 * @param FieldDefDto definition
	 * @param String field value
	 * @return ValidationException (created object if a validate error occurs)
	 */
	private ValidationException validateCurrency(ValidationException ex, Long id, FieldDefDto def, Object value){
		
		if (value instanceof String){
			
			try{
				Utilities.parseDouble((String)value);
			}
			catch (Exception e){
				ex = setMessage(ex, id, def, "InvalidEntry");
			}
		}
		
		
		return ex;
	}
	
	
	/**
	 * Validate the passed in lookup value field.
	 * 
	 * @param ValidationException current exception object (maybe null)
	 * @param Long entity id
	 * @param FieldDefDto definition
	 * @param String field value
	 * @return ValidationException (created object if a validate error occurs)
	 */
	private ValidationException validateLookupValue(ValidationException ex, Long id, FieldDefDto def, Object value){
		
		String s = def.getValues();
		
		//No defined values
		if (s.length() == 0){
			return ex;
		}
		
		boolean isInt = (value instanceof Integer);
		
		
		boolean found = false;
		String [] s1 = s.split(",");
		for (String s2: s1){
			String [] s3 = s2.split("="); 
			String key = s3[0];
			
			
			if (isInt){
				Integer keyI = Integer.parseInt(key);
				if (keyI.intValue() == ((Integer)value).intValue()){
					found = true;
					break;
				}
			}
			else{
				if (key.equalsIgnoreCase(value.toString())){
					found = true;
					break;
				}
			}
			
		}
		
		if (!found){
			ex = setMessage(ex, id, def, "InvalidEntry");
		}
		
		return ex;
	}

	
	
	
	/**
	 * Add a validation message, and create validate exception if it doesn't exist.
	 * 
	 * @param ValidationException current exception object (may be null)
	 * @param Long entity id
	 * @param FieldDef definition
	 * @param String error message
	 * @return ValidationException (created object)
	 */
	private ValidationException setMessage(ValidationException ex, Long id, FieldDefDto def, String message){
		if (ex == null){
			ex = new ValidationException("InvalidEntry");
		}
		
		ex.addMessageList(id, def.getAccessor(), message, null);
		return ex;
	}

	
	
	
}
