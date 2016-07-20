package com.sevenorcas.openstyle.app.mod.lang;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.service.entity.Field;



/**
 * Language key-value pair definitions.<p>
 * 
 * Languages values (ie text) are accessed via a language code and key. All language codes share the same key, but of course different languages have different values.<br>
 * eg 
 * <ul>English: <code>code="en", key="Yes", value="Yes"</code></ul>    
 * <ul>Deutsch: <code>code="de", key="Yes", value="Ja"</code></ul><p>
 * 
 * Note: <b>This</b> class does not know which language code was used to populate the key-value pair field. This is not required as the language code was passed in via
 * the REST call from the client (or a default was used for an internal call). Either way, the calling function knows what the language code is.<p>  
 * 
 * An extra configuration attribute also exists, <code>set</code>. This code is used to logically group language keys into modules.<br>
 * eg <code>set = "Login"</code> contains key-value pairs relevant to the Login process.<br>
 * The client has the option to use this value. In the future release modules will be linked to a <code>set</code>, enabling the client to selectively load key-value pairs 
 * as required.<p>  
 *  
 * The client uses the language key to display a label or language dependent field value. This combined with the clients language code will enable the display of the correct
 * text value.<p>
 *  
 * Note: Available language codes are defined in the <code>Application.properties</code> file.<p>
 * 
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
public class LangDto implements Serializable, ApplicationI{
	
	/**
	 * Language key
	 */
	@Field (appType=FIELD_TYPE_KEY, label="Key")
	@JsonProperty(value="i")  
	public String langKey;
	
	/**
	 * Language value
	 */
	@Field (label="Text")
	@JsonProperty(value="t")
	public String text;
	
	
	//////////////////////////////////  Language Codes   ////////////////////////////////////
	
	
	/**
	 * Get language key
	 * @return language key
	 */
	@JsonIgnore
	public String getLangKey(){
		return langKey;
	}
	public void setLangKey(String langKey) {
		this.langKey = langKey;
	}

	/**
	 * Get language value
	 * @return language value
	 */
	@JsonIgnore
	public String getText(){
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
}
