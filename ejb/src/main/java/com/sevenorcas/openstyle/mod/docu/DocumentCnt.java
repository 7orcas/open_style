package  com.sevenorcas.openstyle.mod.docu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.mod.user.BaseUserParam;
import com.sevenorcas.openstyle.app.service.entity.Field;
import com.sevenorcas.openstyle.app.service.rest.RestUtilities;
import com.sevenorcas.openstyle.app.service.sql.BaseSql;

/**
 * Document Controller object
 * 
 * 
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
public class DocumentCnt extends BaseSql{
	
	
	/**
	 * Only return active rows.<br>
	 */
	@JsonProperty(value="d1") @Field(edit="true")  public Long docId;

	
	/**
	 * Default Constructor.<p>
	 * @param params
	 */
	public DocumentCnt(BaseUserParam params) {
		super(params);
	}
	
	/**
	 * JSON string constructor
	 * @param String JSON object
	 */
	public DocumentCnt(String json) throws Exception{
	    RestUtilities.deserializeJson(this, json);
	}

	/**
	 * Convenience method to set active only flag
	 * @return this object
	 */
	@Override
	@JsonIgnore
	public DocumentCnt setActiveOnly() {
		this.activeOnly = true;
		return this;
	}
	
	
}
