package  com.sevenorcas.openstyle.mod.docu.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sevenorcas.openstyle.app.mod.user.BaseUserParam;
import com.sevenorcas.openstyle.app.service.rest.RestUtilities;
import com.sevenorcas.openstyle.app.service.sql.BaseSql;

/**
 * Document Type SQL object
 * 
 * 
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
public class DocumentTypeSql extends BaseSql{
	
	/**
	 * Default Constructor.<p>
	 * @param params
	 */
	public DocumentTypeSql(BaseUserParam params) {
		super(params);
	}
	
	/**
	 * JSON string constructor
	 * @param String JSON object
	 */
	public DocumentTypeSql(String json) throws Exception{
	    RestUtilities.deserializeJson(this, json);
	}

	/**
	 * Convenience method to set active only flag
	 * @return this object
	 */
	@Override
	@JsonIgnore
	public DocumentTypeSql setActiveOnly() {
		this.activeOnly = true;
		return this;
	}
	
	
}
