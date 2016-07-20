package com.sevenorcas.openstyle.app.mod.lang;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.application.exception.AppException;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.dto.BaseDto;
import com.sevenorcas.openstyle.app.service.dto.Dto;
import com.sevenorcas.openstyle.app.service.dto.DtoEncode;
import com.sevenorcas.openstyle.app.service.entity.EntityI;
import com.sevenorcas.openstyle.app.service.entity.Field;
import com.sevenorcas.openstyle.app.service.entity.ValidateI;
import com.sevenorcas.openstyle.app.service.entity.ValidationException;



/**
 * Complete Language key-value pair definitions for all defined languages.<p>
 *
 * <b>This</b> class is used to:
 * <ul>- transfer all language definitions to the client</ul>
 * <ul>- export to spreadsheet</ul>
 * <p>
 * 
 * [License]
 * @author John Stewart
 */
@SuppressWarnings("serial")
@Dto(entity="LangKey,LangValue")
public class LangListDto extends BaseDto implements Serializable, ValidateI {
	
	/** Language key	           */	@JsonProperty(value="k")	public String key;
	/** Language value	           */	@JsonProperty(value="t")	public String text;
	/** Language code              */	@JsonProperty(value="c")  @DtoEncode(id="f") public String langcode;
	/** Language code encoding id  */   public Integer f;
	/** Language sets              */	@JsonProperty(value="s")  @DtoEncode(id="g") public String sets;
	/** Language sets encoding id  */   public Integer g;
	/** Client flag   	           */	@JsonProperty(value="z")	public Boolean client;
	
	
	/**
	 * Flag to indicate if record is only a key (ie no text records)
	 */
	@Field (readonly=true)
	@JsonProperty(value="x")	
	public Boolean keyOnly;
	
	
	
	
	
	/**
	 * Default Constructor
	 */
	public LangListDto() {
		super(null);
	}
	
	
	/**
     * Constructor using LangValue object
     */
    public LangListDto(LangKey k, LangValue v) {
        super(null);
        key      = k.getKey();
        client   = k.isClient();
        text     = v.getText();
        langcode = v.getLangcode();
    }
	
	//////////////////////// Methods //////////////////////////////////////////////////
	
	/**
	 * Get <b>this</b> DTO's permission key
	 * @return permission key
	 */
	public String permissionKey(){
		return EntityI.DEFAULT_PERM;
	}

	/**
	 * Default initialization
	 */
	@Dto(init=true)
	public void initialise(){
		langcode = LangKey.getDefaultLanguageCode();
		client = true;
	}
	
		
	@JsonIgnore
	public boolean isKeyOnly() {
		return keyOnly != null && keyOnly;
	}
	public void setKeyOnly(Boolean keyOnly) {
		this.keyOnly = keyOnly;
	}


	/**
	 * Update the passed in <code>Entity</code> object with <b>this</b> DTOs values
	 * @param UserParam object
	 * @param LangKey object to update
	 */
	//@Override
	public void update(UserParam userParam, LangKey entity) throws AppException{
		//Only delete values (unless all value records are deleted, then delete key)
		boolean d = isDelete();
		delete = false;
		
		setEntityFields (userParam, entity);
		
		for (LangValue v: entity.getValues()){
			if (v.getLangcode().equals(langcode)){
				v.setText(text);
				v.setDelete(d);
				return;
			}
		}

		if (d && !entity.containsValues()){
			entity.setDelete();
			return;
		}
		
		entity.addLangValue(new LangValue(text, langcode));
		
	}

	/**
	 * Check valid language code
	 */
	@Override
	public void validate() throws ValidationException{
		String [] codes = LangKey.getLanguageCodes();
		for (String l: codes){
			if (l.equals(langcode)){
				return;
			}
		}
		
		ValidationException v = new ValidationException("LangcodeInvalid");
		v.addMessageList(id, "langcode", "Invalid");
		throw v;
	}
	
    
	
	//////////////////////Getters / Setters //////////////////////////////////
	
	@JsonIgnore
	public String getKey(){
		return key;
	}
	public void setKey(String langKey) {
		this.key = langKey;
	}

	@JsonIgnore
	public String getText(){
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	@JsonIgnore
	public String getLangCode() {
		return langcode;
	}
	public void setLangCode(String langCode) {
		this.langcode = langCode;
	}

	@JsonIgnore
	public String getSets() {
		return sets;
	}
	public void setSets(String sets) {
		this.sets = sets;
	}

	@JsonIgnore
	public Boolean getClient() {
		return client;
	}
	public void setClient(Boolean client) {
		this.client = client;
	}

	
	
}
