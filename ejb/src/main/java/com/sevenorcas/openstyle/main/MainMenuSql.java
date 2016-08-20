package  com.sevenorcas.openstyle.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sevenorcas.openstyle.app.mod.user.BaseUserParam;
import com.sevenorcas.openstyle.app.service.rest.RestUtilities;
import com.sevenorcas.openstyle.app.service.sql.BaseSql;

/**
 * Main Menu SQL object
 * 
 * 
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
public class MainMenuSql extends BaseSql{
	
	/**
	 * Order by company sequence
	 */
	@JsonIgnore private Boolean orderBySeq;
	
	/**
	 * Default Constructor.<p>
	 * 
	 * Sets uniqueCodes = false
	 * @param params
	 */
	public MainMenuSql(BaseUserParam params) {
		super(params);
		orderBySeq = false;
	}
	
	/**
	 * JSON string constructor
	 * @param String JSON object
	 */
	public MainMenuSql(String json) throws Exception{
	    RestUtilities.deserializeJson(this, json);
	}

	/**
	 * Convenience method to set active only flag
	 * @return this object
	 */
	@Override
	@JsonIgnore
	public MainMenuSql setActiveOnly() {
		this.activeOnly = true;
		return this;
	}
	
	/**
	 * Convenience method to set order by flag
	 * @return this object
	 */
	@JsonIgnore
	public MainMenuSql setOrderBySeq() {
		orderBySeq = true;
		return this;
	}
	
	
    @JsonIgnore
	public Boolean getOrderBySeq() {
		return orderBySeq;
	}
    @JsonIgnore
	public boolean isOrderBySeq() {
		return orderBySeq != null && orderBySeq;
	}
	public void setOrderBySeq(Boolean orderByName) {
		this.orderBySeq = orderByName;
	}

	
	
}
