package com.sevenorcas.openstyle.app.mod.company;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.mod.lang.Language;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.dto.BaseDto;
import com.sevenorcas.openstyle.app.service.dto.Dto;
import com.sevenorcas.openstyle.app.service.entity.Field;;


/**
 * Company entity data transfer object<p>
 *  
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Dto
public class CompanyDto extends BaseDto implements Serializable {

    @JsonProperty(value="xx")  @Field(edit="true") public Long saveId; //Work around to force saves
	@JsonProperty(value="a")   public Boolean active;
	@JsonProperty(value="b")   @Field(edit="new", notNull=true, label="Number") public Integer companyNr;
	@JsonProperty(value="b9")  public Integer customerNr;
	@JsonProperty(value="c")   public String code;
	@JsonProperty(value="id")  public String codeId;
	
    @JsonProperty(value="tz")  public Integer timezoneGMT;
    @JsonProperty(value="tx")  @Field(edit="true", max=10000) public String timezoneGMTValue;
    
    
	
	/**
	 * Default Constructor
	 */
	public CompanyDto() {
		super(null);
	}
	
	/**
	 * Entity Constructor
	 * @param entity
	 */
	public CompanyDto(Company entity, Language l) {
		super(entity);
		saveId              = 0L;
	}

	/**
	 * Set company entity
	 * @param params
	 * @param entity
	 */
	public void setEntityFields(UserParam params, Company entity) throws Exception{
	    super.setEntityFields(params, entity);
	    
	  
	}
	
	@JsonIgnore
	static public int value(String values, String value){
	    String [] sx = values.split(",");
        for (String s : sx){
            String [] sz = s.split("=");
            if (sz[1].equals(value)){
                return Integer.parseInt(sz[0]);
            }
        }
        return -1;
	}
	
	
	
	
}
