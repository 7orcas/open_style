package  com.sevenorcas.openstyle.app.service.sql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.mod.user.BaseUserParam;
import com.sevenorcas.openstyle.app.service.entity.Field;
import com.sevenorcas.openstyle.app.service.rest.RestUtilities;
import com.sevenorcas.openstyle.app.service.sql.BaseSql;

/**
 * Base Controller object
 * 
 * 
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
public class BaseCnt extends BaseSql{
	
	
	/**
	 * Only return active rows.<br>
	 */
	@JsonProperty(value="be") @Field(edit="true")  public Boolean editMode;

	
	/**
	 * Default Constructor.<p>
	 * @param params
	 */
	public BaseCnt(BaseUserParam params) {
		super(params);
	}
	
	/**
	 * JSON string constructor
	 * @param String JSON object
	 */
	public BaseCnt(String json) throws Exception{
		super(json);
	    RestUtilities.deserializeJson(this, json);
	}

	/**
	 * Convenience method to set active only flag
	 * @return this object
	 */
	@JsonIgnore
	public Boolean getEditMode() {
		return editMode;
	}
	public void setEditMode(Boolean editMode) {
		this.editMode = editMode;
	}
	@JsonIgnore
	public boolean isEditMode() {
		return editMode != null && editMode;
	}
	
	
	
	
}
