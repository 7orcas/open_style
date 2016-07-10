package  com.sevenorcas.openstyle.app.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.entity.Field;
import com.sevenorcas.openstyle.app.rest.RestUtilities;
import com.sevenorcas.openstyle.app.sql.BaseSql;
import com.sevenorcas.openstyle.app.user.BaseUserParam;

/**
 * Company SQL object
 * 
 * 
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
public class CompanySql extends BaseSql{
	
	@JsonProperty(value="ab") @Field(edit="true")  private Boolean ignore0;
	
	
	/**
	 * Order by company name (default is number)
	 */
	@JsonIgnore private Boolean orderByName;
	
	/**
	 * Default Constructor.<p>
	 * 
	 * Sets uniqueCodes = false
	 * @param params
	 */
	public CompanySql(BaseUserParam params) {
		super(params);
		ignore0     = true;
		orderByName = false;
	}
	
	/**
	 * JSON string constructor
	 * @param String JSON object
	 */
	public CompanySql(String json) throws Exception{
	    RestUtilities.deserializeJson(this, json);
	}

	/**
	 * Convenience method to set active only flag
	 * @return this object
	 */
	@Override
	@JsonIgnore
	public CompanySql setActiveOnly() {
		this.activeOnly = true;
		return this;
	}
	
	/**
	 * Convenience method to set order by name flag
	 * @return this object
	 */
	@JsonIgnore
	public CompanySql setOrderByName() {
		orderByName = true;
		return this;
	}
	
	
	@JsonIgnore
    public Boolean getIgnore0() {
        return ignore0;
    }
	
	@JsonIgnore
    public boolean isIgnore0() {
        return ignore0 == null || ignore0;
    }
    public void setIgnore0(Boolean ignore0) {
        this.ignore0 = ignore0;
    }

    @JsonIgnore
	public Boolean getOrderByName() {
		return orderByName;
	}
    @JsonIgnore
	public boolean isOrderByName() {
		return orderByName != null && orderByName;
	}
	public void setOrderByName(Boolean orderByName) {
		this.orderByName = orderByName;
	}

	
	
}
