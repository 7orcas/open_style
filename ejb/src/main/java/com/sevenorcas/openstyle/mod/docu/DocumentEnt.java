package com.sevenorcas.openstyle.mod.docu;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.sevenorcas.openstyle.app.application.Utilities;
import com.sevenorcas.openstyle.app.service.entity.BaseEntity;
import com.sevenorcas.openstyle.app.service.entity.ConfigI;
import com.sevenorcas.openstyle.app.service.entity.Field;



/**
 * Document entity<p>
 * 
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Entity
@Table(name="document_type", schema="public")
@SequenceGenerator(name="ID_SEQUENCE_GEN",sequenceName="seq_id_entity",allocationSize=1)
public class DocumentEnt extends BaseEntity implements Serializable {

	public final static int TEXT_LENGTH = 1000; 
	
	
	/** ID Field. */   
	@Id  
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="ID_SEQUENCE_GEN")
	protected Long id;
	
	/**
	 * Parent menu item
	 */
	@Field
	@Column(name="main_menu_id")
	private Long mainMenuId;
	
	/**
	 * Document Type parent id
	 */
	@Field
	@Column(name="document_type_id")
	private Long typeId;
	
	
	@Field(edit="true")
	private int seq;
	
	
	@Field(edit="true", min=1, max=TEXT_LENGTH) 
	@Column(name="document_text")
	private String text;
	
	    
    ////////////////////// Methods //////////////////////////////////	
	
	/**
	 * Default Constructor
	 */
	public DocumentEnt() {
		super();
	}
	
	
	
    ////////////////////// Getters / Setters //////////////////////////////////	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getMainMenuId() {
		return mainMenuId;
	}
	public void setMainMenuId(Long mainMenuId) {
		this.mainMenuId = mainMenuId;
	}

	public Long getTypeId() {
		return typeId;
	}
	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	
	
	
}
