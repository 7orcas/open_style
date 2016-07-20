package com.sevenorcas.openstyle.app.mod.lang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.sevenorcas.openstyle.app.application.ApplicationParameters;
import com.sevenorcas.openstyle.app.service.entity.BaseEntity;
import com.sevenorcas.openstyle.app.service.entity.Field;

/**
 * Language Key entity<p>
 *
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Entity
@Table(name="langkey", schema="cntrl")
@SequenceGenerator(name="ID_SEQUENCE",sequenceName="cntrl.seq_id_language",allocationSize=1)
public class LangKey extends BaseEntity implements Serializable {
	
	static final public int KEY_LENGTH  = 20;
	static final public int SETS_LENGTH = 100;
	
	/** Application singleton */ private static ApplicationParameters appParam = ApplicationParameters.getInstance();
    private static String [] langCodes;
	
    
    
	
	/** ID Field. */   
	@Id  
	@Field(appType=FIELD_TYPE_ID)
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="ID_SEQUENCE")
	private Long id;
	
	
	@Field(notNull=true, min=1, max=KEY_LENGTH, edit="new")
	@Column(name="code")
	private String key;

	@Field(edit="true", max=SETS_LENGTH)
	private String sets;
		
	@Field(edit="true")
	protected Boolean client;
	
	/**
	 * Language value positions.
	 */
	@Field
	@OneToMany(cascade={CascadeType.ALL},mappedBy="langkey")
	@OrderBy("langcode ASC")
	private List <LangValue> values = new ArrayList<LangValue>(); 
	
	
    ////////////////////// Methods //////////////////////////////////	
	
	/**
	 * Default Constructor
	 */
	public LangKey() {
	}

	
	/**
	 * Add a language value 
	 * @param LangValue object to add
	 */
	public void addLangValue(LangValue obj){
		obj.setLangkey(this);
		obj.setCompanyNr(companyNr);
		values.add(obj);
	}
	
	/**
	 * Find a language value by language code
	 * @param langCode
	 * @return
	 */
	public LangValue findByLangCode(String langCode){
		for (LangValue v: values){
			if (v.getLangcode().equalsIgnoreCase(langCode)){
				return v;
			}
		}
		return null;
	}
	
	/**
	 * Does <b>this</b> object contain (non deleted) language values
	 * @return
	 */
	public boolean containsValues() {
		if (values == null || values.size() == 0){
			return false;
		}
		for (LangValue v: values){
			if (!v.isDelete()){
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * Does <b>this</b> object contain language values to be deleted
	 * @return
	 */
	public boolean isDeleteChild() {
		if (values == null || values.size() == 0){
			return false;
		}
		for (LangValue v: values){
			if (v.isDelete()){
				return true;
			}
		}
		
		return false;
		
	}
	
	
	/**
	 * Return the priority list of language codes for <b>this</b> application (as defined in the <code>Application Parameters</code> file)
	 * @return
	 */
	static public String [] getLanguageCodes(){
		if (langCodes != null){
			return langCodes;
		}
		String codes = appParam.getLanguageCodes();
		codes = codes != null ? codes : "en";
		langCodes = codes.split(",");
		return langCodes;
	}
	

	/**
	 * Return the default language code for <b>this</b> application (as defined in the <code>Application Parameters</code> file)
	 * @return
	 */
	static public String getDefaultLanguageCode(){
		String [] s = getLanguageCodes();
		return s[0];
	}

	/**
	 * Validate the passed in language code. If not valid the return default language code.
	 * @param String language code
	 * @return
	 */
	static public String validate(String lang){
		String [] langCodes = getLanguageCodes();
		for (int i=0; lang != null && i<langCodes.length; i++){
			if (langCodes[i].equals(lang)){
				return langCodes[i]; 
			}
		}
		return getDefaultLanguageCode();
	}

	
    //////////////////////Getters / Setters //////////////////////////////////
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	public String getSets() {
		return sets;
	}
	public void setSets(String sets) {
		this.sets = sets;
	}

	public List<LangValue> getValues() {
		return values;
	}
	public void setValues(List<LangValue> values) {
		this.values = values;
	}

	public Boolean getClient() {
		return client;
	}
	public void setClient(Boolean client) {
		this.client = client;
	}
	public boolean isClient() {
		return client != null && client;
	}
	
}
