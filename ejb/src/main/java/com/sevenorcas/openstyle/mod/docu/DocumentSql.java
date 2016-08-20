package  com.sevenorcas.openstyle.mod.docu;

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
public class DocumentSql extends BaseSql{
	
	/**
	 * Default Constructor.<p>
	 * @param params
	 */
	public DocumentSql(BaseUserParam params) {
		super(params);
	}
	
	/**
	 * JSON string constructor
	 * @param String JSON object
	 */
	public DocumentSql(String json) throws Exception{
	    RestUtilities.deserializeJson(this, json);
	}

	/**
	 * Convenience method to set active only flag
	 * @return this object
	 */
	@Override
	@JsonIgnore
	public DocumentSql setActiveOnly() {
		this.activeOnly = true;
		return this;
	}
	
	
}
