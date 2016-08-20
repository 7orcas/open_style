package com.sevenorcas.openstyle.mod.docu.type;

import java.io.Serializable;
import java.util.ArrayList;

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
 * Document Type entity<p>
 * 
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Entity
@Table(name="document_type", schema="public")
@SequenceGenerator(name="ID_SEQUENCE_GEN",sequenceName="seq_id_entity",allocationSize=1)
public class DocumentTypeEnt extends BaseEntity implements ConfigI, Serializable {

	public final static int CODE_LENGTH = 10; 
	
	
	/** ID Field. */   
	@Id  
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="ID_SEQUENCE_GEN")
	protected Long id;
	
	private String config;
	
	@Field(edit="true", min=1, max=CODE_LENGTH) 
	private String code;
	
	    
    ////////////////////// Methods //////////////////////////////////	
	
	/**
	 * Default Constructor
	 */
	public DocumentTypeEnt() {
		super();
	}
	
	/**
	 * Convert <b>this</b> entities configuration to a <code>String</code> for saving.
	 * @return encoded String
	 */
	@Transient
	public String encode(){
		ArrayList<String> params = new ArrayList<>();
		return Utilities.toParameterEncode(params);
	}
	
	
	/**
	 * Convert encoded configuration <b>String</b> to <b>this</b> entities attributes.
	 * @param String configuration
	 */
	@Transient
	public void decode(String config){
		//Hashtable<String, String> params = Utilities.fromParameterEncode(config);
	}
	
    ////////////////////// Getters / Setters //////////////////////////////////	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
}
