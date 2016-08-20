package com.sevenorcas.openstyle.main;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.sevenorcas.openstyle.app.mod.lang.LangKey;
import com.sevenorcas.openstyle.app.service.entity.BaseEntity;
import com.sevenorcas.openstyle.app.service.entity.Field;



/**
 * Main Menu entity<p>
 * 
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Entity
@Table(name="main_menu", schema="public")
@SequenceGenerator(name="ID_SEQUENCE_GEN",sequenceName="seq_id_entity",allocationSize=1)
public class MainMenuEnt extends BaseEntity implements Serializable {

		
	/** ID Field. */   
	@Id  
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="ID_SEQUENCE_GEN")
	protected Long id;
	
	@Field(edit="new", notNull=true, label="MenuSeq") 
	private String seq;

	@Field(edit="true", min=1, max=LangKey.KEY_LENGTH) 
	@Column(name="lang_code")
	private String langCode;
	
	
	    
    ////////////////////// Methods //////////////////////////////////	
	
	/**
	 * Default Constructor
	 */
	public MainMenuEnt() {
		super();
	}
	
	
	
    ////////////////////// Getters / Setters //////////////////////////////////	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}


	public String getSeq() {
		return seq;
	}
	public MainMenuEnt setSeq(String seq) {
		this.seq = seq;
		return this;
	}

	public String getLangCode() {
		return langCode;
	}
	public MainMenuEnt setLangCode(String langCode) {
		this.langCode = langCode;
		return this;
	}

	
}
