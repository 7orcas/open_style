package com.sevenorcas.openstyle.app.mod.lang;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.sevenorcas.openstyle.app.service.entity.BaseEntity;
import com.sevenorcas.openstyle.app.service.entity.Field;

/**
 * Language Value entity<p>
 *
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Entity
@Table(name="langvalue", schema="cntrl")
@SequenceGenerator(name="ID_SEQUENCE",sequenceName="cntrl.seq_id_language",allocationSize=1)
public class LangValue extends BaseEntity implements Serializable {

	final static public int  LANGCODE_LENGTH    = 2;
	final static public int  TEXT_LENGTH        = 100;
	
	
	/** ID Field. */   
	@Id  
	@Field(appType=FIELD_TYPE_ID)
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="ID_SEQUENCE")
	private Long id;
	

	/** Header foreign key */
	@ManyToOne
	@JoinColumn(name="langkey_id")
	private LangKey langkey;

	@Column(insertable=false,updatable=false)
	private Long langkey_id;

	
	@Field(notNull=true, min=LANGCODE_LENGTH, max=LANGCODE_LENGTH, label="LangCode", edit="new")
	private String langcode;

	@Field(edit="true", max=TEXT_LENGTH)
	private String text;
		
    ////////////////////// Methods //////////////////////////////////	
	
	/**
	 * Default Constructor
	 */
	public LangValue() {
	}

	/**
	 * Constructor with text and langcode
	 */
	public LangValue(String text, String langcode) {
		this.text = text;
		this.langcode = langcode;
	}
	
	
	public boolean isValid() {
		return text != null && !text.isEmpty();
	}
	
	
	
    //////////////////////Getters / Setters //////////////////////////////////
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getLangcode() {
		return langcode;
	}
	public void setLangcode(String langcode) {
		this.langcode = langcode;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public LangKey getLangkey() {
		return langkey;
	}
	public void setLangkey(LangKey langkey) {
		this.langkey = langkey;
	}

	public Long getLangkey_id() {
		return langkey_id;
	}
	public void setLangkey_id(Long langkey_id) {
		this.langkey_id = langkey_id;
	}
	
}
