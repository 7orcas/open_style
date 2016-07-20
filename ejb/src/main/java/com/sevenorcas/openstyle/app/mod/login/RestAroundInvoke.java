package com.sevenorcas.openstyle.app.mod.login;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.ws.rs.Produces;

import com.sevenorcas.openstyle.app.application.BaseIntercepter;
import com.sevenorcas.openstyle.app.application.exception.UnknownExceptionHtml;
import com.sevenorcas.openstyle.app.mod.lang.Language;
import com.sevenorcas.openstyle.app.mod.lang.LanguageService;
import com.sevenorcas.openstyle.app.service.dto.Dto;
import com.sevenorcas.openstyle.app.service.dto.ReturnDto;
import com.sevenorcas.openstyle.app.service.entity.ValidateI;
import com.sevenorcas.openstyle.app.service.entity.ValidationException;
import com.sevenorcas.openstyle.app.service.entity.ValidationService;



/**
 * REST AroundInvoke Interceptor<p>
 * 
 * <b>This</b> class is the default intercepter wrapper for all REST calls. Its purpose is to wrap all client REST calls and perform functions that
 * are generic. The design goal is to remove such 'boiler plate' functions from the 'normal' business logic of the REST call.<p>  
 * 
 * The following functions are applied to all client REST calls:<ul>
 * 
 *     <li><b>Exception processing</b><br>
 *         Exceptions are caught within <b>this</b> class and returned to the client via a <code>ReturnDto</code> object. A downstream interceptor
 *         will further process the caught exception. Note that the implementing REST method can override most of this behavior by wrapping its method in a
 *         try catch block.<br>
 *         <a href="{@docRoot}/../doc-files/exceptions-rest.html">See REST Exception Handling</a></li><br>
 *    
 *    <li><b>Validation</b><br>
 *         Objects that implement <code>ValidateI</code> interface will be validated via their <code>isValid()</code> method. If validation fails then a 
 *         <code>ValidationException</code> is thrown. Note this behavior can be overwritten with the <code>@Dto.validate=false</code> annotation.<br>
 *         <a href="{@docRoot}/../doc-files/validations-rest.html">See Validations</a>
 *         </li>
 *    
 *    <li><b>Include control objects in return ReturnDto</b><br>
 *         Inspect first call parameter annotated with <code>@Dto.includeInReturnDto=true</code> to insert into the <code>ReturnDto</code> object's 
 *         <code>control</code> field. Note if the REST method has already added a control object then <b>this</b> class will not overwrite it.  
 *         </li>
 *    
 * </ul>
 * 
 * [License]
 * @author John Stewart
 */
public class RestAroundInvoke extends BaseIntercepter {

	/** Validation Service */ private ValidationService validationService;
	/** Language   Service */ private LanguageService   languageService;
	
	/**
	 * Wrap call
	 * @param InvocationContext call
	 * @return Called object or caught exception wrapped in a <code>ReturnDto</code> object
	 * @throws Exception
	 */
	@AroundInvoke
	public Object restInterceptor (InvocationContext ictx) throws Exception {

		try {
			
			//Validate parameters
			validate(ictx.getParameters());
			
			Object object = ictx.proceed();
			
			//Insert control object (if found) 
			if (object instanceof ReturnDto){
				ReturnDto rtn = (ReturnDto)object;
				if (rtn.getControlObject() == null){
					insertControlObject(rtn, ictx.getParameters());
				}
				return rtn;
			}
			
			
			return object;
		} 
		//wrap exception for downstream processing
		catch (Exception e) {
			log(e, ictx);
			
			//Client is expecting ReturnDto object 
			Class <?>clazz = ictx.getMethod().getReturnType();
			if (clazz.getName().indexOf(ReturnDto.class.getName()) != -1){
				return new ReturnDto(e);
			}

            //Return configured exception page with message
            if (clazz.getName().indexOf(String.class.getName()) != -1){
                languageService = lookupService(languageService, "LanguageServiceImp");
                Language l = languageService.getLanguage(null);
                return (new UnknownExceptionHtml()).getView(e, l);
            }
			
			
	        //Client is expecting HTML text, ie @Produces({"text/html;charset=UTF-8"})
			Annotation[] a = ictx.getMethod().getAnnotations();
			for (int i=0; a!=null && i<a.length; i++){
			    Annotation an = a[i];
			    if (an instanceof Produces){
			        Produces p = (Produces)an;
			        String [] s = p.value();
			        for (String sx : s){
			            if (sx.indexOf("text/html") != -1){
			                languageService = lookupService(languageService, "LanguageServiceImp");
			                Language l = languageService.getLanguage(null);
			                return l.getLabel(e.getMessage());
			            }
			        }
			    }
			    
			}
			
			throw e;
		}
	}


	/**
	 * Look for first parameter annotated with <code>@Dto.includeInReturnDto=true</code> to insert into 
	 * the <code>ReturnDto</code> object's <code>control</code> field.<br>
	 *      
	 * @param ReturnDto return object to client  
	 * @param Object [] parameters
	 */
	private void insertControlObject(ReturnDto rtn, Object [] params){
		if (params != null){
			for (int ii = 0; ii < params.length; ii++){
				Object param = params[ii]; 
				if (param == null){
					continue;
				}
				if (insertControlObject(param)){
					rtn.setControlObject(param);
					return;
				}
				
			}
		}
	}
	
	/**
	 * Look for annotation <code>@Dto.includeInReturnDto=true</code> in class of subclasses.<br>
	 *      
	 * @param Object parameter
	 * @return true/false result
	 */
	private boolean insertControlObject(Object param){
		Dto anno = param.getClass().getAnnotation(Dto.class);
		if (anno != null){
			return anno.includeInReturnDto();
		}
		return false;
	}
	
	
	
	/**
	 * Validate passed in objects
	 * @param Object [] parameters
	 * @throws Exception
	 */
	private void validate(Object [] params) throws Exception{
        if (params != null){
			for (int ii = 0; ii < params.length; ii++){
				if (params[ii] != null){
					validate(params[ii]);
				}
			}
		}
	}
	
	
	/**
	 * Validate passed in dto. This is a two step process:<ul>
	 *     <li>The <code>ValidationService</code> will validate the dto via its <code>@Field</code> annotations (this can be overwritten
	 *         by setting <code>@Dto.validate() == false </code>).</li>
	 *     <li>If the dto implements <code>ValidateI</code> then its <code>validate()</code> method will be called</li>          
	 * </ul>          
	 *  
	 * @param Object dto to validate
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private void validate(Object dto) throws Exception{
		try{

			Object a = dto;
			
			//If list then peek inside to get first object class
			if (dto instanceof List){
				List l = (List)dto;
				ValidationException ex = null;
				for (Object o: l){
					try{
						validate(o);
					}
					catch (ValidationException v){
						if (ex == null){
							ex = new ValidationException("Invalid");
						}
						ex.addMessageList(v);
					}
				}
				
				if (ex != null){
					throw ex;
				}
				return;
			}
			
			Dto anno = a.getClass().getAnnotation(Dto.class);
			if (anno != null && anno.validate()){
				validationService = lookupService(validationService, "ValidationServiceImp");
				validationService.validate(dto);
			}
			
			if (dto instanceof ValidateI){
				((ValidateI)dto).validate();
			}
			
			
		}
		catch(Exception e){
			if (e instanceof ValidationException){
				throw e;
			}
			throw new ValidationException("InvalidEntry");
		}
	}

	
	
	
}
