package com.sevenorcas.openstyle.app.sql;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.ApplicationI;
import com.sevenorcas.openstyle.app.ApplicationParameters;
import com.sevenorcas.openstyle.app.cache.ServerCache;
import com.sevenorcas.openstyle.app.entity.EntityI;
import com.sevenorcas.openstyle.app.entity.Field;
import com.sevenorcas.openstyle.app.entity.ValidateI;
import com.sevenorcas.openstyle.app.entity.ValidationException;
import com.sevenorcas.openstyle.app.rest.RestUtilities;
import com.sevenorcas.openstyle.app.user.BaseUserParam;




/**
 * <code>BaseSql</code> is a basic class for specific slq objects to extend.<p>
 * 
 * <b>This</b> class contains essential attributes and methods for:<ul>
 *     <li>generating sql queries</li>
 *     <li><code>Service and Dao</code> method call attibutes</li>
 * </ul><p>
 * 
 * [License]
 * @author John Stewart
 */
@SuppressWarnings("serial")
//WF10 TODO @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BaseSql implements Serializable, SqlI, ApplicationI, ValidateI {

    static final public String DESCR_SEPARATOR   = ":";
    
    
	/** Application singleton  */ protected ApplicationParameters appParam = ApplicationParameters.getInstance();
	/** Server object cache    */ protected ServerCache cache = ServerCache.getInstance();
	
	
	/**
	 * Only return active rows.<br>
	 */
	@JsonProperty(value="a") @Field(edit="true")  public Boolean activeOnly;
	
	
	/**
	 * Maximum result rows.<br>
	 * If used, <b>this</b> field will limit the number of rows returned from a search. If the result
	 * set exceeds <b>this</b> value then a <code>MaxRowsException</code> is thrown.
	 * @see MaxRowsException
	 */
	@JsonProperty(value="m") @Field(edit="true")  public Integer maxRows;
	

	/**
	 * Company number<br>
	 * Injected (be default) via the <code>BaseUserParam</code> upon construction
	 */
    protected Integer companyNr;	
	
    /**
	 * Plant ID<br>
	 * Implementing class of <b>this<b> object determine the logic of this field.
	 */
    protected Long plant_id;
    
    
    //SQL query fields
    @JsonProperty(value="c") @Field(edit="true") private Integer count;
    @JsonProperty(value="l") @Field(edit="true") protected Integer limit;
    @JsonProperty(value="o") @Field(edit="true") private Integer offset;
    @JsonProperty(value="b") @Field(edit="true") protected String  orderby;
    @JsonProperty(value="d") @Field(edit="true") protected Boolean orderbyDesc;
	
    
    /**
	 * True if temporary tables should not be dropped (debug only).<br>
	 */
	@JsonProperty(value="k") @Field(edit="true")  private Boolean keepTempTables = false;
    
    /**
     * Lookup values are processed by individual queries
     */
	@JsonProperty(value="u") @Field(edit="true", max=15) protected String lookup;
	
	/**
	 * List for calling program to pass id's into and use in queries
	 */
	@JsonIgnore private List<Long> ids;
	
	/**
	 * Substitute temporary table name
	 */
	@JsonIgnore private String ttName;
	
	/**
	 * Is this a lookup from the client? e.g. in a lookup field box
	 */
	@JsonIgnore private Boolean isLookupFromClient;
	
	
	/**
	 * Empty constructor for implementing classes
	 */
	protected BaseSql(){}
    
    /**
	 * JSON string constructor
	 * @param String JSON object
	 */
	public BaseSql(String json) throws Exception{
	    RestUtilities.deserializeJson(this, json);
	}
    
    /**
     * UserParam constructor
     * @param BaseUserParam base user / query parameters
     */
    public BaseSql(BaseUserParam params) {
    	initialise (params);
	}
    
    /**
     * BaseSql constructor
     * @param BaseSql
     */
    public BaseSql(BaseSql sql) {
        companyNr  = sql.companyNr;
        activeOnly = sql.activeOnly;
    }
    
    /////////////////////// Methods ////////////////////////////////////
    
    /**
     * Initialize <b>this</b> object
     * @param BaseUserParam base user / query parameters
     */
    public void initialise (BaseUserParam params){
    	companyNr = params.getCompany();
    }
    
    
    @JsonIgnore
    public String permissionKey(){
		return EntityI.DEFAULT_PERM;
	}
    
	/**
	 * Implementing classes can override and provide a <code>validate</code> method. This will be called from the <code>RestAroundInvoke</code> interceptor 
	 * during a REST method call. If the object is not valid then a <code>ValidationException</code> is thrown.
	 * @see ValidationException 
	 */
	public void validate() throws ValidationException{
	}

	@JsonIgnore
	public boolean isActiveOnly() {
		return activeOnly != null && activeOnly;
	}

	@JsonIgnore
	public boolean isKeepTempTables() {
		return keepTempTables != null && keepTempTables;
	}
	
	/**
	 * Should the DAO <code>COUNT</code> the main table 
	 * @return
	 */
	@JsonIgnore
	public boolean isQueryCountRequired() {
		return (offset == null || offset.intValue() == 0) 
				&& limit != null 
				&& limit.intValue() > 0
				&& count == null;
	}
	
	
	@JsonIgnore
	public boolean isLookup() {
		return lookup != null && !lookup.isEmpty();
	}
	
	@JsonIgnore
    public boolean isLookupNotAdvanced() {
        return lookup != null && !lookup.isEmpty() && !lookup.equals(ADVANCED_LOOKUP_CODE);
    }
	
	/**
	 * Convenience method to set active only flag
	 * @return this object
	 */
	@JsonIgnore
	public BaseSql setActiveOnly() {
		this.activeOnly = true;
		return this;
	}
	
	/**
	 * Convenience method to reset lookup fields
	 * @return this object
	 */
	@JsonIgnore
	public BaseSql resetLookup() {
		this.lookup = null;
		return this;
	}
	
	/**
	 * Return an <code>AND</code> plan id for the <code>WHERE</code> clause 
	 * @param table
	 * @return
	 */
	@JsonIgnore
	public String wherePlantIdSql(String table){
		if (plant_id == null){
			return "";
		}
		return (table != null? table + ".": "") + "plant_id = " + plant_id;
	}
	
	/**
	 * Does <b>this</b> object contain a plant id?
	 * @return
	 */
	@JsonIgnore
	public boolean isPlantId() {
        return plant_id != null;
    }
	
	/**
	 * Convenience method to test if <b>this</b> object contains id's
	 * @return
	 */
	@JsonIgnore
	public boolean containsIds() {
        return ids != null && !ids.isEmpty();
    }
	
    /////////////////////// Getters / Setters ////////////////////////////////////

	@JsonIgnore
	public Integer getMaxRows() {
		return maxRows;
	}
	public void setMaxRows(Integer maxRows) {
		this.maxRows = maxRows;
	}
	@JsonIgnore
	public Integer getCompanyNr() {
		return companyNr;
	}
	public void setCompanyNr(Integer companyNr) {
		this.companyNr = companyNr;
	}
	@JsonIgnore
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	@JsonIgnore
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	@JsonIgnore
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	@JsonIgnore
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	@JsonIgnore
	public Boolean getOrderbyDesc() {
		return orderbyDesc;
	}
	public void setOrderbyDesc(Boolean orderbyDesc) {
		this.orderbyDesc = orderbyDesc;
	}

	@JsonIgnore
	public Boolean getActiveOnly() {
		return activeOnly;
	}
	public void setActiveOnly(Boolean activeOnly) {
		this.activeOnly = activeOnly;
	}

	@JsonIgnore
	public String getLookup() {
		return lookup;
	}
	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	

	@JsonIgnore
	public Boolean getKeepTempTables() {
		return keepTempTables;
	}
	public void setKeepTempTables(Boolean keepTempTables) {
		this.keepTempTables = keepTempTables;
	}
	

	@JsonIgnore
	public Long getPlantId() {
		return plant_id;
	}
	public void setPlantId(Long plant_id) {
		this.plant_id = plant_id;
	}

	
	@JsonIgnore
    public List<Long> getIds() {
        return ids;
    }
    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    
    @JsonIgnore
    public String getTempTableName() {
        return ttName;
    }
    public void setTempTableName(String ttName) {
        this.ttName = ttName;
    }

    @JsonIgnore
	public boolean isLookupFromClient() {
		return isLookupFromClient != null && isLookupFromClient;
	}

	public void setIsLookupFromClient(Boolean isLookupFromClient) {
		this.isLookupFromClient = isLookupFromClient;
	}

    
    
	
}
