package com.sevenorcas.openstyle.app.service.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.application.ApplicationParameters;
import com.sevenorcas.openstyle.app.application.Utilities;
import com.sevenorcas.openstyle.app.application.exception.AppException;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.log.ApplicationLog;
import com.sevenorcas.openstyle.app.service.repo.BaseDao;

/**
 * Extended JDBC Statement<p>
 * 
 * See re connection pool options: https://docs.jboss.org/jbossas/docs/Server_Configuration_Guide/beta500/html/ch13s13.html
 * 
 * [License] 
 * @author John Stewart
 */
public class StatementX implements ApplicationI{

	static protected ApplicationParameters appParam = ApplicationParameters.getInstance();

	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private String query;
	
	private ArrayList<String> where;
	
	/**
	 * True == append base entity default fields to query 
	 */
	private boolean appendBaseEntityFields     = false;
	private String appendBaseEntityFieldTables = null;
	
	private String tempTable = null;
	private String tempTableIndex = null;
	
	private SqlI sqlCount;

	/**
	 * true = throw sql update exceptions
	 */
	private boolean throwSqlException = false;
	
	private StatementX (){}
	
	
	/**
	 * Create <code>StatementX</code> instance using default data source.
	 * @param query
	 * @return
	 * @throws Exception
	 */
	static public StatementX create(String query) throws Exception{
		return create(query, appParam.getPostgresDatasource());
	}
	
	/**
	 * Create <code>StatementX</code> instance.
	 * @param query
	 * @param datascource
	 * @return
	 * @throws Exception
	 */
	static public StatementX create(String query, String datascource) throws Exception{
		StatementX statement = new StatementX();
		statement.query = query;
		statement.connection = getConnection(datascource);
		statement.statement = statement.connection.createStatement();

		return statement; 
	}

	
	/**
     * Get connection (to default datasource)<p>
     * 
     * NOTE: Close the connection !!<p>
     * 
     * @param datascource
     * @return
     * @throws Exception
     */
    static public Connection getConnection() throws Exception{
        return getConnection(appParam.getPostgresDatasource());
    }
	
    
	/**
	 * Get connection 
	 * @param datascource
	 * @return
	 * @throws Exception
	 */
    static public Connection getConnection(String datascource) throws Exception{
		
		try {
		    Connection c = BaseDao.getJDBCConnection(datascource);
		    c.setAutoCommit(false);
			return c;
		} 
		catch (Exception e){
			ApplicationLog.error(e);
			throw new AppException(e.getMessage());  
		} 
	}
	
	/**
	 * Drop the passed in temp table
	 * @param table
	 * @param sql interface object
	 * @throws Exception
	 */
	static public void dropTempTable(String table, SqlI sql) throws Exception{
		
		if (table == null || (sql != null && sql.isKeepTempTables())){
			return;
		}
		
		if (!table.startsWith(SCHEMA_TEMP + ".")){
			throw AppException.create("Invalid Temp Table to DROP: " + table).logThisException().emailThisException();
		}
		
		create("DROP TABLE IF EXISTS " + table, appParam.getPostgresDatasource()).executeUpdate(); 
	}
	
	/**
	 * Append base entity fields into the query. These will be given unique names to avoid collisions.<br>
	 * Retrieval of the fields from the <code>ResultSetX</code> should be via <code>setBaseEntityFields()</code>
	 * @param String comma separated list of tables to append fields to
	 * @return Current StatementX object
	 */
	public StatementX appendBaseEntityFields(String tables){
		appendBaseEntityFields = true;
		appendBaseEntityFieldTables = (appendBaseEntityFieldTables == null? "" : appendBaseEntityFieldTables + ",") + tables;
		return this;
	}
	
	/**
	 * Append base entity fields into the query. These will be given unique names to avoid collisions.<br>
	 */
	private void appendBaseEntityFieldsX() throws Exception{
		
		int index = query.toUpperCase().indexOf(" FROM ");
		if (index == -1){
			ApplicationLog.error("Badly formed query");
			throw new AppException("Badly formed query"); 
		}
		
		appendBaseEntityFieldTables = appendBaseEntityFieldTables == null? "" : appendBaseEntityFieldTables;
		String [] tables = appendBaseEntityFieldTables.split(",");
		if (tables.length == 0){
			tables = new String[]{""};
		}
		
		StringBuffer sb = new StringBuffer();
		
		for (String table: tables){
			
			String t = table.length() > 0? table + "." : "";
			
			//Note: Order is important!!!  ResultSetX will access via this order
			sb.append(", " + t + "id AS "        + table + ResultSetX.FIELD_ID
					+ ", " + t + "comp_nr AS "   + table + ResultSetX.FIELD_COMP_NR
					+ ", " + t + "create_ts AS " + table + ResultSetX.FIELD_CREATE_TS
					+ ", " + t + "create_id AS " + table + ResultSetX.FIELD_CREATE_ID
					+ ", " + t + "update_ts AS " + table + ResultSetX.FIELD_UPDATE_TS
					+ ", " + t + "update_id AS " + table + ResultSetX.FIELD_UPDATE_ID
					+ ", " + t + "active AS "    + table + ResultSetX.FIELD_ACTIVE);
		}
		query = query.substring(0, index) + sb.toString() + query.substring(index);
		
	}
	
	/**
	 * Add a <code>WHERE</code> clause.<br>
	 * @param String comma separated list of tables to append fields to
	 * @return Current StatementX object
	 */
	public StatementX addWhere(String clause){
		if (clause == null || clause.isEmpty()){
			return this;
		}
		
		if (where == null){
			where = new ArrayList<>();
		}
		
		where.add(clause);
		return this;
	}
	
	/**
	 * Add an <code>active = TRUE</code> clause.<br>
	 * @param BaseSql to test if <code>activeOnly</code> is set
	 * @param String table to apply test
	 * @return Current StatementX object
	 */
	public StatementX addActive(BaseSql search, String table){
		if (search != null && search.isActiveOnly()){
			addWhere(table + ".active=TRUE");
		}
		return this;
	}
	
	/**
	 * Add an <code>comp_nr = [number]</code> clause.<br>
	 * @param BaseSql 
	 * @param String table to apply test
	 * @return Current StatementX object
	 */
	public StatementX addCompNr(BaseSql search, String table){
		if (search != null){
			addCompNr(search.getCompanyNr(), table);
		}
		return this;
	}
	
	/**
	 * Add an <code>comp_nr = [number]</code> clause.<br>
	 * @param UserParam user parameters
	 * @param String table to apply test
	 * @return Current StatementX object
	 */
	public StatementX addCompNr(UserParam params, String table){
		if (params != null){
			addCompNr(params.getCompany(), table);
		}
		return this;
	}

	/**
	 * Add an <code>comp_nr = [number]</code> clause.<br>
	 * @param Integer company number 
	 * @param String table to apply test
	 * @return Current StatementX object
	 */
	public StatementX addCompNr(Integer compNr, String table){
		if (compNr != null){
			addWhere((table != null? table + "." : "") + "comp_nr=" + compNr);
		}
		return this;
	}
	
	
	/**
	 * Create a temporary table of the result set.<p>
	 * 
	 * Note sql statement must contain SELECT and FROM.
	 * 
	 * @param String temporary table name
	 * @param String comma delimited index field names (can be null)
	 * @return Current StatementX object
	 */
	public StatementX createTempTable (String tempTable, String tempTableIndex){
		this.tempTable      = tempTable;
		this.tempTableIndex = tempTableIndex;
		return this;
	}
	
	/**
	 * Execute the current query.<p>
	 * @return Extended result set
	 * @throws Exception
	 */
	public ResultSetX executeQuery() throws Exception{
		return executeQuery(null);
	}
	
	
	/**
	 * Execute the current query.<p>
	 * 
	 * @param BaseSql object, can be <code>null</code>. Note currently ignored, but future use will implement offsets.
	 * @return Extended result set
	 * @throws Exception
	 */
	public ResultSetX executeQuery(SqlI sql) throws Exception{
		
		if (appendBaseEntityFields){
			appendBaseEntityFieldsX();
		}
		
		ResultSetX x = null;
		
		insertWhere();
		
		if (sql != null && sql.getLimit() != null){
			insertStatement("LIMIT", sql.getLimit());
		}
		if (sql != null && sql.getOffset() != null){
			insertStatement("OFFSET", sql.getOffset());
		}
		
		
		try {
			
			if (sqlCount != null){
				sqlCount.setCount(countDo(createCountSelect()));
			}

			//Create temporary table and then read from it
			if (tempTable != null){
				insertInto(tempTable, tempTableIndex);
				log (query);
				statement.executeUpdate(query);
				createTempTableSelect(tempTable);
			}
			
			log (query);
			resultSet = statement.executeQuery(query);
			connection.commit();
			
			ResultSetMetaData rsmd = resultSet.getMetaData();
	
			int columns = rsmd.getColumnCount();
			
			x = new ResultSetX(tempTable);
		
			//Get column names
			for (int i=0; i<columns; i++){
				x.addColumnName(rsmd.getColumnName(i+1));
			}
			
			while(resultSet.next()){
				Object[] row = new Object[columns];
				x.addRow(row);
				for (int i=0; i<columns; i++){
					row[i] = resultSet.getObject(i+1);
				}
			}
			
		} catch (Exception e){
			try{
				log("Rollback: ex=" + e.getMessage());
				connection.rollback();
			}
			catch (Exception ex){
				log("Can't Rollback: ex=" + ex.getMessage());
			}
			close(null, null, connection);
			
		} finally {
		    close(resultSet, statement, connection);
		    resultSet  = null; 
		    statement  = null;
		    connection = null;
		}
		
		return x;
	}
	
	/**
	 * Execute the current query.<p>
	 * 
	 * @param BaseSql object, can be <code>null</code>. Note currently ignored, but future use will implement offsets.
	 * @return Extended result set
	 * @throws Exception
	 */
	public void executeUpdate() throws Exception{
		
		if (appendBaseEntityFields){
			appendBaseEntityFieldsX();
		}
		
		insertWhere();
		
		try {
			
			if (tempTable != null){
				insertInto(tempTable, tempTableIndex);
			}
			
			log (query);
			statement.executeUpdate(query);
			connection.commit();
			
		} catch (Exception e){
			try{
				log("Rollback: ex=" + e.getMessage());
				connection.rollback();
			}
			catch (Exception ex){
				log("Can't Rollback: ex=" + ex.getMessage());
				throw e;
			}
			close(null, null, connection);
			
			if (throwSqlException){
				throw e;
			}
			
		} finally {
		    close(null, statement, connection);
		    statement  = null;
            connection = null;
		}
		
	}
	
	/**
	 * Insert a <Code>WHERE</code> clause
	 */
	private void insertWhere(){
		if (where == null || where.isEmpty()){
			return;
		}
		
		int iWhere  = query.toUpperCase().indexOf(" WHERE ");
		int iInsert = query.toUpperCase().indexOf(" GROUP BY ");
		iInsert = iInsert != -1? iInsert : query.toUpperCase().indexOf(" ORDER BY");
		iInsert = iInsert != -1? iInsert : query.toUpperCase().indexOf(" LIMIT ");
		
		String c = iWhere != -1? "": " WHERE ";
		for (int i=0; where != null && i<where.size(); i++){
			c += (i==0? "" : " AND ") + where.get(i);
		}
		
		if (iWhere != -1 && iInsert != -1){
			query = query.substring(0, iInsert) + " AND " + c + " " + query.substring(iInsert);
		}
		else if (iWhere != -1){
			query = query + " AND " + c;
		}
		else if (iInsert != -1){
			query = query.substring(0, iInsert) + c + " " + query.substring(iInsert);
		}
		else{
			query = query + c;
		}
		 
	}
	
	/**
	 * Insert a <Code>INTO</code> a temporary table and index (if indexFields no null)
	 * @param String temporary table name
	 * @param String comma delimited index field names (can be null)
	 */
	private void insertInto(String table, String indexFields) throws Exception{
		int iFrom  = query.toUpperCase().indexOf(" FROM ");
		query = query.substring(0, iFrom) + " INTO " + table + " " + query.substring(iFrom);
		query = query.trim().endsWith(";")? query : query + ";";
		
		if (indexFields != null){
			int index1 = table.indexOf(".");
			String name = index1 != -1? table.substring(index1+1) : table;
			query += "CREATE INDEX " + name + "_u1 ON " + table + " USING btree (" + indexFields + ")";
		}
	}
	
	/**
	 * Refactor query string for passed in temporary table (and remove all where, etc clauses)
	 * @param String temporary table name
	 */
	private void createTempTableSelect(String table) throws Exception{
			
		int index1 = query.toUpperCase().indexOf(" SELECT ");
		int index2 = query.toUpperCase().indexOf(" INTO ", index1);
		
		String select = query.substring(index1 + 6, index2);
		StringBuffer sb = new StringBuffer();
		
		int count = 0;
		String [] fields = select.split(",");
		for (String f: fields){
			f = f.trim();
			
			index1 = f.indexOf(".");
			f = index1 != -1? f.substring(index1+1) : f;
			
			index1 = f.toUpperCase().indexOf(" AS ");
			f = index1 != -1? f.substring(index1+4) : f;
			
			sb.append((count++ > 0?", ":"") + f);
		}
		
		query = "SELECT " + sb.toString() + " FROM " + table + ";";
		
	}
	
	/**
	 * Return a <code>COUNT</code> statement based on the current query
	 * @return
	 * @throws Exception
	 */
	private String createCountSelect() throws Exception{
		
		int index1 = query.toUpperCase().indexOf(" FROM ");
		int index2 = query.toUpperCase().indexOf(" ORDER ", index1);
		index2 = index2 != -1? index2 : query.toUpperCase().indexOf(" LIMIT ", index1); 
		index2 = index2 != -1? index2 : query.toUpperCase().indexOf(" OFFSET ", index1);
		index2 = index2 != -1? index2 : query.length();
		
		StringBuffer sb = new StringBuffer("SELECT COUNT(*) ");
		sb.append(query.substring(index1, index2));
		
		return sb.toString();
	}
	
	
	/**
	 * Append a lookup statement to the passed in sql statement. The join type of
	 * <code>AND</code> or <code>OR</code> is passed in. 
	 * 
	 * @param field to test
	 * @param String value to test for
	 * @param sql statement
	 * @param String join
	 * @return updated sql statement
	 * @throws Exception
	 */
	static public String appendLookup (String field, String value, String sql, String join)throws Exception{
		return appendLookupString (field, value, sql, join);
	}
	
	/**
	 * Append a lookup statement to the passed in sql statement. The join type of
	 * <code>AND</code> or <code>OR</code> is passed in. 
	 * @param field to test
	 * @param Integer value to test for
	 * @param sql comparison string (eg '=', '>', etc)
	 * @param sql statement
	 * @param String join
	 * @return updated sql statement
	 * @throws Exception
	 */
	static public String appendLookup (String field, Integer value, String comparison, String sql, String join)throws Exception{
		return appendLookupInteger (field, value, sql, comparison, join);
	}
	
	/**
	 * Append a lookup statement to the passed in sql statement. The join type of
	 * <code>AND</code> or <code>OR</code> is passed in. 
	 * @param field to test
	 * @param Date value to test for
	 * @param sql comparison string (eg '=', '>', etc)
	 * @param sql statement
	 * @param String join
	 * @return updated sql statement
	 * @throws Exception
	 */
	static public String appendLookup (String field, Date value, String comparison, String sql, String join)throws Exception{
		return appendLookupDate (field, value, sql, comparison, join);
	}
	
	
	/**
	 * Append an <code>OR</code> lookup statement to the passed in sql statement
	 * @param field to test
	 * @param String value to test for
	 * @param sql statement
	 * @return updated sql statement
	 * @throws Exception
	 */
	static public String appendLookupOr (String field, String value, String sql)throws Exception{
		return appendLookupString (field, value, sql, "OR");
	}
	
	/**
	 * Append an <code>OR</code> lookup statement to the passed in sql statement
	 * @param field to test
	 * @param Integer value to test for
	 * @param sql comparison string (eg '=', '>', etc)
	 * @param sql statement
	 * @return updated sql statement
	 * @throws Exception
	 */
	static public String appendLookupOr (String field, Integer value, String comparison, String sql)throws Exception{
		return appendLookupInteger (field, value, sql, comparison, "OR");
	}
	
	/**
	 * Append an <code>OR</code> lookup statement to the passed in sql statement
	 * @param field to test
	 * @param Long value to test for
	 * @param sql comparison string (eg '=', '>', etc)
	 * @param sql statement
	 * @return updated sql statement
	 * @throws Exception
	 */
	static public String appendLookupOr (String field, Long value, String comparison, String sql)throws Exception{
		return appendLookupLong (field, value, sql, comparison, "OR");
	}
	
	/**
	 * Append an <code>OR</code> lookup statement to the passed in sql statement
	 * @param field to test
	 * @param Date value to test for
	 * @param sql comparison string (eg '=', '>', etc)
	 * @param sql statement
	 * @return updated sql statement
	 * @throws Exception
	 */
	static public String appendLookupOr (String field, Date value, String comparison, String sql)throws Exception{
		return appendLookupDate (field, value, sql, comparison, "OR");
	}
	
	/**
	 * Append an <code>OR</code> lookup statement to the passed in sql statement
	 * @param field to test
	 * @param Boolean value to test for
	 * @param sql statement
	 * @return updated sql statement
	 * @throws Exception
	 */
	static public String appendLookupOr (String field, Boolean value, String sql)throws Exception{
		return appendLookupBoolean (field, value, sql, "OR");
	}
	
	/**
     * Append an <code>AND</code> lookup statement to the passed in sql statement
     * @param field to test
     * @param Boolean value to test for
     * @param sql statement
     * @return updated sql statement
     * @throws Exception
     */
    static public String appendLookupAnd (String field, Boolean value, String sql)throws Exception{
        return appendLookupBoolean (field, value, sql, "AND");
    }
	
	/**
	 * Append an <code>AND</code>  lookup statement to the passed in sql statement
	 * @param field to test
	 * @param String value to test for
	 * @param sql statement
	 * @return updated sql statement
	 * @throws Exception
	 */
	static public String appendLookupAnd (String field, String value, String sql)throws Exception{
		return appendLookupString (field, value, sql, "AND");
	}

	
	/**
	 * Prepare a <code>String</code> lookup statement to be appended to the passed in sql statement
	 * @param sql statement
	 * @param join type
	 * @return updated sql statement
	 * @throws Exception
	 */
	static private String appendLookupPrep (String sql, String join) throws Exception{
		//Test if sql has brackets
		boolean hasBrackets = sql != null && sql.trim().startsWith("(") && sql.trim().endsWith(")");
		
		//remove last bracket to allow appending
		sql = hasBrackets? sql.trim().substring(1, sql.trim().length() - 1) : sql;
		sql = sql == null? "" : sql + " " + join + " ";
		return sql;
	}
	
	/**
	 * Append a <code>String</code> lookup statement to the passed in sql statement
	 * @param field to test
	 * @param String value to test for
	 * @param sql statement
	 * @param join type
	 * @return updated sql statement
	 * @throws Exception
	 */
	static private String appendLookupString (String field, String value, String sql, String join)throws Exception{
		if (value == null){
			return sql;
		}
		sql = appendLookupPrep (sql, join);
		return "(" + sql + "(LOWER(" + field  +") LIKE '" + value.toLowerCase() + "%'))";
	}
	
	/**
	 * Append a <code>Integer</code> lookup statement to the passed in sql statement
	 * @param field to test
	 * @param Integer value to test for
	 * @param sql statement
	 * @param sql comparison string (eg '=', '>', etc)
	 * @param join type
	 * @return updated sql statement
	 * @throws Exception
	 */
	static private String appendLookupInteger (String field, Integer value, String sql, String comparison, String join)throws Exception{
		if (value == null){
			return sql;
		}
		sql = appendLookupPrep (sql, join);
		return "(" + sql + "(" + field  + " " + comparison + value.toString() + "))";
	}
	
	/**
	 * Append a <code>Long</code> lookup statement to the passed in sql statement
	 * @param field to test
	 * @param Long value to test for
	 * @param sql statement
	 * @param sql comparison string (eg '=', '>', etc)
	 * @param join type
	 * @return updated sql statement
	 * @throws Exception
	 */
	static private String appendLookupLong (String field, Long value, String sql, String comparison, String join)throws Exception{
		if (value == null){
			return sql;
		}
		sql = appendLookupPrep (sql, join);
		return "(" + sql + "(" + field  + " " + comparison + value.toString() + "))";
	}
	
	/**
	 * Append a <code>Date</code> lookup statement to the passed in sql statement
	 * @param field to test
	 * @param Date value to test for
	 * @param sql statement
	 * @param sql comparison string (eg '=', '>', etc)
	 * @param join type
	 * @return updated sql statement
	 * @throws Exception
	 */
	static private String appendLookupDate (String field, Date value, String sql, String comparison, String join)throws Exception{
		if (value == null){
			return sql;
		}
		sql = appendLookupPrep (sql, join);
		return "(" + sql + "(" + field  + " " + comparison + formatDate (value) + "))";
	}
	
	/**
	 * Append a <code>Boolean</code> lookup statement to the passed in sql statement
	 * @param field to test
	 * @param Boolean value to test for
	 * @param sql statement
	 * @param join type
	 * @return updated sql statement
	 * @throws Exception
	 */
	static private String appendLookupBoolean (String field, Boolean value, String sql, String join)throws Exception{
		if (value == null){
			return sql;
		}
		sql = appendLookupPrep (sql, join);
		return "(" + sql + "(" + field  + (value?" IS TRUE " : " IS FALSE ") + "))";
	}
	
	/**
	 * Set a <code>Date</code> sql statement
	 * @param Date value 
	 * @return formatted sql date 
	 * @throws Exception
	 */
	static public String formatDate (Date value) throws Exception{
		return "DATE(" +  Utilities.formatSqlDate(value) + ")";
	}
	
	
	/**
	 * Insert a statement
	 */
	private void insertStatement(String statement, int value){
		int index = query.indexOf(";");
		query = (index == -1? query : query.substring(0, index)) + " " + statement + " " + value + (index == -1? "" : ";");
	}
	
	
	/**
	 * Convenience method to test if count is required using the actual query. If so the the table count is stored in the passed in SQL object.<p>
	 * 
	 * Note: the count query is run before the creation of temporary tables.
	 *  
	 * @param Sql object
	 * @throws Exception
	 */
	public StatementX countIfRequired (SqlI sql) throws Exception{
		if (sql.isQueryCountRequired()){
			sqlCount = sql;
		}
		return this;
	}
	
	
	/**
	 * Convenience method to test if count is required. If so passed in SQL statement is used.
	 * @param Sql object
	 * @param String sql query
	 * @throws Exception
	 */
	public StatementX countIfRequired (SqlI sql, String query) throws Exception{
		if (sql.isQueryCountRequired()){
			sql.setCount(countDo (query));
		}
		return this;
	}
	
	
	/**
	 * Convenience method to get a record table count
	 * @param String table to query
	 * @return count of table
	 * @throws Exception
	 */
	static public int count (String table) throws Exception{
		return count (table, null, null);
	}

	/**
	 * Convenience method to get a record table count
	 * @param String table to query
	 * @param Integer company number filter (optional)
	 * @return count of table
	 * @throws Exception
	 */
	static public int count (String table, Integer comp_nr) throws Exception{
		return count (table, comp_nr, null);
	}
	
	/**
	 * Convenience method to get a record table count
	 * @param String table to query
	 * @param String where clause
	 * @return count of table
	 * @throws Exception
	 */
	static public int count (String table, String where) throws Exception{
		return count (table, null, where);
	}
	
	/**
	 * Convenience method to get a record table count
	 * @param String table to query
	 * @param Integer company number filter (optional)
	 * @param String where clause
	 * @return count of table
	 * @throws Exception
	 */
	static public int count (String table, Integer comp_nr, String where) throws Exception{
			
		String WHERE = "";
		if (comp_nr != null && where != null){
			WHERE = "WHERE comp_nr = " + comp_nr + " AND " + where + " ";
		}
		else if (comp_nr != null){
			WHERE = "WHERE comp_nr = " + comp_nr + " ";
		}
		else if (where != null){
			WHERE = "WHERE " + where + " ";
		}
		
		return countDo("SELECT COUNT(*) FROM " + table + " " + WHERE);
	}
	
	/**
	 * Convenience method to get a record table count
	 * @param String query
	 * @return count 
	 * @throws Exception
	 */
	static public int countDo (String query) throws Exception{
			
		ResultSetX rs = StatementX.create(query).executeQuery(null);
		
		Long count = 0L;
		while(rs.next()){
			count = rs.getLong(1);
		}
		
		return count.intValue();
	}
	
	
	/**
     * Close the sql objects in <b>this</b> object.
     * 
     * @see http://www.mastertheboss.com/jboss-datasource/how-to-configure-a-datasource-with-jboss-7
     * @param ResultSet
     * @param Statement
     * @param Connection
     */
    private void close(ResultSet resultSet, Statement statement, Connection connection) {
    	if (resultSet != null){
	    	try {
	    		resultSet.close();
			} catch (Exception e) {
				ApplicationLog.error(e);
			}
	    }
    	if (statement != null){
	    	try {
	    		statement.close();
			} catch (Exception e) {
				ApplicationLog.error(e);
			}
	    }
    	if (connection != null){
	    	try {
	    		connection.close();
			} catch (Exception e) {
				ApplicationLog.error(e);
			}
	    }
    }
	
    /**
     * Log a query
     * Thanks to http://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
     */
	private void log (String query){
		if (query != null){
		    
		    //Remove delimiters from encoded fields
		    int p = 0;
		    while (true){
		        int x1 = query.indexOf("'", p);
		        if (x1 == -1){
		            break;
		        }
		        int x2 = query.indexOf("'", x1+1);
		        if (x2 == -1){
                    break;
                }
		        p = x2 + 1;
		        
		        String s = query.substring(x1, x2);
		        s = s.replace(";", " ");
		        query = query.substring(0, x1) + s + query.substring(x2);
		    }
		    
			String [] statements = query.split(";");
			for (String s : statements){
			    s = s.replaceAll("\\s+"," ");
				ApplicationLog.debugNoCaller(s + ";");
				if (appParam.isDebug()){
				    System.out.println(s + ";");			    
				}
			}
			
		}
	}


    public String getQuery() {
        return query;
    }

    /**
     * Set the main query string
     * @param query
     */
    public void setQuery(String query) {
        this.query = query;
    }

    public Statement getStatement() {
        return statement;
    }
    
    public StatementX throwException() {
		throwSqlException = true;
		return this;
	}


	/**
     * Convenience method to strip a field to its name only.<p>
     * @param f
     * @return
     */
    static public String strip(String f) {
        int i = f.indexOf(".");
        if (i != -1){
            f = f.substring(i+1, f.length());
        }

        i = f.indexOf("AS ");
        if (i != -1){
            f = f.substring(i+3, f.length());
        }
        return f;
    }
	
}
