package com.sevenorcas.openstyle.app.service.sql;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import com.sevenorcas.openstyle.app.service.entity.BaseEntity;

/**
 * Extended JDBC ResultSet object<p>
 * 
 * [License] 
 * @author John Stewart
 */
public class ResultSetX {

	final static protected String FIELD_ID        = "id_stdfield"; 
	final static protected String FIELD_COMP_NR   = "comp_nr_stdfield";
	final static protected String FIELD_CREATE_TS = "create_ts_stdfield";
	final static protected String FIELD_CREATE_ID = "create_id_stdfield";
	final static protected String FIELD_UPDATE_TS = "update_ts_stdfield";
	final static protected String FIELD_UPDATE_ID = "update_id_stdfield";
	final static protected String FIELD_ACTIVE    = "active_stdfield";
	
	private ArrayList<Object[]> set;
	
	/**
	 * Query column names (assumed to be in result set order)
	 */
	private ArrayList<String> columnNames;
	/**
	 * Cache of column index for column name
	 */
	private Hashtable<String, Integer> columnNameIndex;
	
	
	private int pointer = -1;
	private String tempTable;
	
	/**
	 * Constructor
	 * @param String temporary table name result set reads from (null == no table)
	 */
	public ResultSetX(String tempTable) {
		this.tempTable = tempTable;
		set = new ArrayList<>();
		columnNames = new ArrayList<>();
		columnNameIndex = new Hashtable<>();
	}

	
	public String getTempTable() {
		return tempTable;
	}
	public boolean isTempTable() {
		return tempTable != null;
	}



	private boolean hasNext() {
		return pointer >= 0 && pointer < set.size();
	}

	public boolean next() {
		pointer++;
		return hasNext();
	}

	public boolean previous() {
		pointer--;
		return hasNext();
	}
	
	public int count() {
		return set.size();
	}

	
	/**
	 * Add a data row
	 * @param Object[] datarow
	 */
	public void addRow(Object[] datarow){
		set.add(datarow);
	}
	
	/**
	 * Add a column name. Assumes this is the order of the query result columns
	 * @param String name
	 */
	public void addColumnName(String name){
		columnNames.add(name);
	}
	
	/**
	 * Find a column name. 
	 * @param String name
	 */
	public int findColumnName(String name){
		if (columnNameIndex.containsKey(name)){
			return columnNameIndex.get(name);
		}
		
		int index = -1;
		for (int i=0; i<columnNames.size(); i++){
			String nameX = columnNames.get(i);
			if (nameX.equals(name)){
				index = i+1;
				break;
			}
		}
		columnNameIndex.put(name, index);
		return index;
	}
	
	
	/**
	 * Set the passed in entity with its base fields
	 * @param BaseEntity to set fields for
	 * @param String table name of base entity
	 * @throws Exception
	 */
	public void setBaseEntityFields(BaseEntity entity, String tablename) throws Exception{
		
		tablename = tablename != null? tablename : "";
		int index = findColumnName(tablename + FIELD_ID);
		
		entity.setId(       getLong(index++));
		entity.setCompanyNr(getInt(index++));
		entity.setCreated(  getTimestamp(index++));
		entity.setCreatedId(getLong(index++));
		entity.setUpdated(  getTimestamp(index++));
		entity.setActive(   getBoolean(index++));
	}
	
	
	public Long getLong(int sqlColumnNumber) throws Exception{
		Object[] row = set.get(pointer);
		return (Long) row[sqlColumnNumber - 1];
	}
	
	public Integer getSum(int sqlColumnNumber) throws Exception{
	    return getCount(sqlColumnNumber);
	}
	public Integer getIntMax(int sqlColumnNumber) throws Exception{
        return getInt(sqlColumnNumber);
    }
	
	public Integer getCount(int sqlColumnNumber) throws Exception{
		Object[] row = set.get(pointer);
		Long x = (Long) row[sqlColumnNumber - 1];
		return x != null ? x.intValue() : null;
	}
	
	public String getString(int sqlColumnNumber) throws Exception{
		Object[] row = set.get(pointer);
		return (String) row[sqlColumnNumber - 1];
	}
	
	/**
	 * Return a non null string (convert to empty if required)
	 * @param sqlColumnNumber
	 * @return
	 * @throws Exception
	 */
	public String getStringNullAsEmpty(int sqlColumnNumber) throws Exception{
        Object[] row = set.get(pointer);
        String s = (String) row[sqlColumnNumber - 1];
        return s != null? s : "";
    }
	
	/**
	 * Return an <code>Integer</code> value
	 * @param int sql column number
	 * @return Integer (or null)
	 * @throws Exception
	 */
	public Integer getInt(int sqlColumnNumber) throws Exception{
		Object[] row = set.get(pointer);
		return (Integer) row[sqlColumnNumber - 1];
	}
	
	/**
	 * Return an <code>int</code> value (or default if sql is null)
	 * @param int sql column number
	 * @param int default value if sql is null
	 * @return Integer (or null)
	 * @throws Exception
	 */
	public Integer getInt(int sqlColumnNumber, int defaultValue) throws Exception{
		Object[] row = set.get(pointer);
		if (row[sqlColumnNumber - 1] == null){
			return defaultValue;
		}
		return (Integer) row[sqlColumnNumber - 1];
	}
	
	/**
	 * Return a <code>Double</code> value
	 * @param int sql column number
	 * @return Double (or null)
	 * @throws Exception
	 */
	public Double getDouble(int sqlColumnNumber) throws Exception{
		return getDouble(sqlColumnNumber, -1, null);
	}

	
	/**
	 * Return a <code>Double</code> value
	 * @param int sql column number
	 * @param int number of decimal places
	 * @return Double (or null)
	 * @throws Exception
	 */
	public Double getDouble(int sqlColumnNumber, int decimals) throws Exception{
		return getDouble(sqlColumnNumber, decimals, null);
	}
	
	/**
	 * Return a <code>Double</code> value
	 * @param int sql column number
	 * @param int number of decimal places
	 * @param Double default value if sql is null
	 * @return Double (or null)
	 * @throws Exception
	 */
	public Double getDouble(int sqlColumnNumber, int decimals, Double defaultValue) throws Exception{
		Object[] row = set.get(pointer);
		if (row[sqlColumnNumber - 1] == null){
			return defaultValue;
		}
		BigDecimal d = (BigDecimal)row[sqlColumnNumber - 1];
		if (decimals != -1){
		    d = d.setScale(decimals, RoundingMode.HALF_UP);
		}
				
		return d.doubleValue();
	}
	
	
	public Date getDate(int sqlColumnNumber) throws Exception{
		Object[] row = set.get(pointer);
		Date d = (Date) row[sqlColumnNumber - 1];
		if (d == null){
			return d;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	public Date getTimestamp(int sqlColumnNumber) throws Exception{
		Object[] row = set.get(pointer);
		return (Date) row[sqlColumnNumber - 1];
	}

	
	public Boolean getBoolean(int sqlColumnNumber) throws Exception{
		Object[] row = set.get(pointer);
		return (Boolean) row[sqlColumnNumber - 1];
	}
	
}
