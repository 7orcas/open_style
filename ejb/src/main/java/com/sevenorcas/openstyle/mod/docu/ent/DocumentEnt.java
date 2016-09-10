package com.sevenorcas.openstyle.mod.docu.ent;

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
@Table(name="document", schema="public")
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
	
	    
	/**
     * Sections.
     */
    @Field
    @OneToMany(cascade={CascadeType.ALL},mappedBy="parent")
    @OrderBy("seq")
    private List <SectionEnt> sections = new ArrayList<SectionEnt>(); 
    
	
	
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
	public DocumentEnt setMainMenuId(Long mainMenuId) {
		this.mainMenuId = mainMenuId;
		return this;
	}

	public Long getTypeId() {
		return typeId;
	}
	public DocumentEnt setTypeId(Long typeId) {
		this.typeId = typeId;
		return this;
	}

	public int getSeq() {
		return seq;
	}
	public DocumentEnt setSeq(int seq) {
		this.seq = seq;
		return this;
	}

	public String getText() {
		return text;
	}
	public DocumentEnt setText(String text) {
		this.text = text;
		return this;
	}

    public List<SectionEnt> getSections() {
		return sections;
	}
    public DocumentEnt setSections(List<SectionEnt> sections) {
		this.sections = sections;
		return this;
	}

	
	
	
}
