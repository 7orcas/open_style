package com.sevenorcas.openstyle.app.service.sql;

/**
 * Interface for specific slq objects that do not extend the <code>BaseSql</code> class.<p>
 * 
 * <b>This</b> interface contains essential attributes and methods for default sql behavior.<p>
 * 
 * [License]
 * @author John Stewart
 */
public interface SqlI {
    final static public String ADVANCED_LOOKUP_CODE = "***";
    
	public Integer getLimit();
	public Integer getOffset();
	public Integer getCount();
	public boolean isKeepTempTables();
	public boolean isQueryCountRequired();
	public void setCount(Integer count);
}
