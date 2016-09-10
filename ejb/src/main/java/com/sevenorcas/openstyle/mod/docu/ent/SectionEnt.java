package com.sevenorcas.openstyle.mod.docu.ent;

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
 * Document entity<p>
 * 
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Entity
@Table(name="document_section", schema="public")
@SequenceGenerator(name="ID_SEQUENCE_GEN",sequenceName="seq_id_entity",allocationSize=1)
public class SectionEnt extends BaseEntity implements Serializable {

	public final static int TEXT_LENGTH = 10000; 
	
	
	/** ID Field. */   
	@Id  
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="ID_SEQUENCE_GEN")
	protected Long id;
	
	/** Header foreign key */
	@ManyToOne
	@JoinColumn(name="document_id")
	private DocumentEnt parent;

	@Column(name="document_id", insertable=false,updatable=false)
	private Long parent_id;
	
	
	@Field(edit="true")
	private int seq;
	
	
	@Field(edit="true", min=1, max=TEXT_LENGTH) 
	@Column(name="section_text")
	private String text;
	
	    
    ////////////////////// Methods //////////////////////////////////	
	
	/**
	 * Default Constructor
	 */
	public SectionEnt() {
		super();
	}
	
	
	
    ////////////////////// Getters / Setters //////////////////////////////////	
	
	
	public DocumentEnt getParent() {
		return parent;
	}
	public SectionEnt setParent(DocumentEnt parent) {
		this.parent = parent;
		return this;
	}
	
	public Long getParent_id() {
		return parent_id;
	}
	public SectionEnt setParent_id(Long parent_id) {
		this.parent_id = parent_id;
		return this;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public int getSeq() {
		return seq;
	}
	public SectionEnt setSeq(int seq) {
		this.seq = seq;
		return this;
	}

	public String getText() {
		return text;
	}
	public SectionEnt setText(String text) {
		this.text = text;
		return this;
	}

	
	
	
}
