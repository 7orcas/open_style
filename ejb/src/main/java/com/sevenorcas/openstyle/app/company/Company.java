package com.sevenorcas.openstyle.app.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.sevenorcas.openstyle.app.entity.Field;



/**
 * Company entity<p>
 * 
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Entity
@Table(name="company", schema="cntrl")
@SequenceGenerator(name="ID_SEQUENCE",sequenceName="seq_id_entity",allocationSize=1)
public class Company extends BaseEntity implements Serializable {

		
	/** ID Field. */   
	@Id  
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="ID_SEQUENCE")
	protected Long id;
	
	private String config;
	
	@Field(edit="true", min=1, max=20) 
	private String code;
	
	@Field(edit="true", notNull=false, max=20, label="Ids") 
    private String codeId;
	
	@Field(edit="new", notNull=true, label="Company") 
	@Column(name="customer_nr")
	private Integer customerNr;
	
    ////////////////////// Transient Fields //////////////////////////////////

	
	
    
    ////////////////////// Methods //////////////////////////////////	
	
	/**
	 * Default Constructor
	 */
	public Company() {
		super();
	}
	
	
	
}
