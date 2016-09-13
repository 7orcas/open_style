package  com.sevenorcas.openstyle.mod.docu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.mod.user.BaseUserParam;
import com.sevenorcas.openstyle.app.service.entity.Field;
import com.sevenorcas.openstyle.app.service.rest.RestUtilities;
import com.sevenorcas.openstyle.app.service.sql.BaseCnt;

/**
 * Document Controller object
 * 
 * 
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
public class DocumentCtl extends BaseCnt{
	
	
	/**
	 * Only return active rows.<br>
	 */
	@JsonProperty(value="d1") @Field(edit="true")  public Long docId;

	
	/**
	 * Default Constructor.<p>
	 * @param params
	 */
	public DocumentCtl(BaseUserParam params) {
		super(params);
	}
	
	/**
	 * JSON string constructor
	 * @param String JSON object
	 */
	public DocumentCtl(String json) throws Exception{
		super(json);
	    RestUtilities.deserializeJson(this, json);
	}

	/**
	 * Convenience method to set active only flag
	 * @return this object
	 */
	@Override
	@JsonIgnore
	public DocumentCtl setActiveOnly() {
		this.activeOnly = true;
		return this;
	}

	
	@JsonIgnore
	public Long getDocId() {
		return docId;
	}
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	
	
	
	
}
