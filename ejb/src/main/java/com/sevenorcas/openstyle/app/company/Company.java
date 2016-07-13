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

import com.sevenorcas.openstyle.app.Utilities;
import com.sevenorcas.openstyle.app.entity.BaseEntity;
import com.sevenorcas.openstyle.app.entity.ConfigI;
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
public class Company extends BaseEntity implements ConfigI, Serializable {

		
	/** ID Field. */   
	@Id  
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="ID_SEQUENCE")
	protected Long id;
	
	private String config;
	
	@Field(edit="true", min=1, max=20) 
	private String code;
	
	@Field(edit="new", notNull=true, label="Company") 
	@Column(name="customer_nr")
	private Integer customerNr;
	
    ////////////////////// Transient Fields //////////////////////////////////

	/** true == this is a test company              */ @Transient @Field(edit="true", label="CompT")      private Boolean testCompany;
	
    
    ////////////////////// Methods //////////////////////////////////	
	
	/**
	 * Default Constructor
	 */
	public Company() {
		super();
	}
	
	/**
	 * Convert <b>this</b> entities configuration to a <code>String</code> for saving.
	 * @return encoded String
	 */
	@Transient
	public String encode(){
		ArrayList<String> params = new ArrayList<>();
		
		Utilities.toParameter(params, "P1", testCompany);
		
		return Utilities.toParameterEncode(params);
	}
	
	
	/**
	 * Convert encoded configuration <b>String</b> to <b>this</b> entities attributes.
	 * @param String configuration
	 */
	@Transient
	public void decode(String config){
		Hashtable<String, String> params = Utilities.fromParameterEncode(config);
		
		testCompany                = Utilities.fromParameter(params, "P1", false);
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
	
	public Integer getCustomerNr() {
		return customerNr;
	}
	public void setCustomerNr(Integer customerNr) {
		this.customerNr = customerNr;
	}
	
	public Boolean getTestCompany() {
		return testCompany;
	}
	public void setTestCompany(Boolean testCompany) {
		this.testCompany = testCompany;
	}
	@Transient
	public boolean isTestCompany() {
		return testCompany != null && testCompany;
	}
}
