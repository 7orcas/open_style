package com.sevenorcas.openstyle.app.mod.lang;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.dto.Dto;
import com.sevenorcas.openstyle.app.service.entity.Field;
import com.sevenorcas.openstyle.app.service.rest.RestUtilities;
import com.sevenorcas.openstyle.app.service.sql.BaseSql;

/**
 * Language sql search object
 * 
 * [License]
 * @author john.stewart
 */
@SuppressWarnings("serial")
public class LangSql extends BaseSql{
	
	static final public int SQL_LIMIT = 90;
	static final public String TEXT_SEPARATOR   = ":";

   //All-Yes-No selection (here for convenience for implementing classes)
	final static public int SELECTION_NO      = 0;
	final static public int SELECTION_YES     = 1;
    final static public int SELECTION_ALL     = 2;
    final static public String SELECTION_VALUES = 
            SELECTION_ALL  + "=All,"     
        +   SELECTION_YES  + "=Yes," 
        +   SELECTION_NO   + "=No,";

	
	@JsonProperty(value="la") @Field(edit="true", max=LangKey.KEY_LENGTH,        label="LangKey") private String key;
	@JsonProperty(value="lb") @Field(edit="true", max=LangCode.LANGCODE_LENGTH, label="LangCode") private String code;
	@JsonProperty(value="lc") @Field(edit="true", max=LangKey.SETS_LENGTH)   private String sets;
	@JsonProperty(value="ld") @Field(edit="true", max=LangCode.TEXT_LENGTH) private String text;
	@JsonProperty(value="le") @Field(edit="true") private Boolean missingCodes;
	
	/** Include/Exclude client flag */     
	@JsonProperty(value="lf") 
	@Field (appType=FIELD_TYPE_LOOKUP_REF, values=SELECTION_VALUES)
	private Integer client;
	
	
	/**
     * User parameter constructor
     */
	public LangSql(UserParam params) {
		super(params);
	}
	
	/**
	 * JSon string constructor
	 * @param json
	 */
	public LangSql(String json) throws Exception{
	    RestUtilities.deserializeJson(this, json);
	}
	
    //////////////////////// Methods //////////////////////////////////////////////////
	
	
	@JsonIgnore
	public boolean isSearchAdvanced(){
		return (key != null && !key.isEmpty())
				|| (code != null && !code.isEmpty())
				|| (sets != null && !sets.isEmpty())
		        || (text != null && !text.isEmpty())
		        || client != null && client.intValue() != SELECTION_ALL;
	}
	
	
	@JsonIgnore
	public boolean isSet() {
		return sets != null && !sets.isEmpty();
	}
	
	/**
	 * Client initializations for <b>this</b> dto 
	 */
	@Dto(init=true)
	public void initialise(){
		limit = SQL_LIMIT;
	}
	
	@JsonIgnore
	public boolean isMissingCodes() {
		return missingCodes != null && missingCodes;
	}
	
	@JsonIgnore
	public boolean isLookupCode() {
		return getLookupCode() != null;
	}
	
	@JsonIgnore
	public String getLookupCode(){
		if (lookup == null || lookup.indexOf(TEXT_SEPARATOR) == -1){
			return null;
		}
		String x = lookup.substring(0, lookup.indexOf(TEXT_SEPARATOR));
		return x.trim().length() > 0 ? x.trim(): null; 
	}
	
	@JsonIgnore
	public boolean isLookupText() {
		return getLookupText() != null;
	}
	
	@JsonIgnore
	public String getLookupText(){
		if (lookup == null || lookup.indexOf(TEXT_SEPARATOR) == -1){
			return null;
		}
		String x = lookup.substring(lookup.indexOf(TEXT_SEPARATOR)+1);
		return x.trim().length() > 0 ? x.trim(): null; 
	}
	
	@JsonIgnore
	public boolean isClientTrue() {
		return client != null && client == SELECTION_YES;
	}
	
	@JsonIgnore
	public boolean isClientFalse() {
		return client != null && client == SELECTION_NO;
	}
	
    ////////////////////////Getters / Settters //////////////////////////////////////////////////
	
	@JsonIgnore
	public String getSets() {
		return sets;
	}
	public void setSets(String sets) {
		this.sets = sets;
	}

	@JsonIgnore
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	@JsonIgnore
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	@JsonIgnore
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	@JsonIgnore
	public Integer getClient() {
		return client;
	}
	public void setClient(Integer client) {
		this.client = client;
	}

	@JsonIgnore
	public Boolean getMissingCodes() {
		return missingCodes;
	}
	public void setMissingCodes(Boolean missingCodes) {
		this.missingCodes = missingCodes;
	}

	
	
	
}
