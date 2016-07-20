package com.sevenorcas.openstyle.app.service.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.application.ApplicationParameters;
import com.sevenorcas.openstyle.app.application.Utilities;
import com.sevenorcas.openstyle.app.application.exception.AppException;
import com.sevenorcas.openstyle.app.service.log.ApplicationLog;
import com.sevenorcas.openstyle.app.service.repo.BaseDao;

/**
 * Extended JDBC PreparedStatement<p>
 * 
 * Thanks to http://stackoverflow.com/questions/14402874/why-am-i-unable-to-use-prepared-statements-to-update-a-database
 * 
 * [License] 
 * @author John Stewart
 */
public class PreparedStatementX implements ApplicationI{

	static protected ApplicationParameters appParam = ApplicationParameters.getInstance();

	private Connection connection;
	private PreparedStatement statement;
	
	private PreparedStatementX (){}
	private List<String> parameters;
	private boolean log = false;
	
	
	/**
	 * Create <code>StatementX</code> instance using default data source.
	 * @param query
	 * @return
	 * @throws Exception
	 */
	static public PreparedStatementX create(String query) throws Exception{
		return create(query, appParam.getPostgresDatasource());
	}
	
	/**
	 * Create <code>StatementX</code> instance.<p>
	 * 
	 * Thanks to http://stackoverflow.com/questions/241003/how-to-get-a-value-from-the-last-inserted-row<p>
	 * 
	 * @param query
	 * @param datascource
	 * @return
	 * @throws Exception
	 */
	static private PreparedStatementX create(String query, String datascource) throws Exception{
		PreparedStatementX statement = new PreparedStatementX();
		statement.connection = getConnection(datascource);
		statement.statement = statement.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		statement.log (query);
		statement.parameters = new ArrayList<String>();
		
		return statement; 
	}

	
	/**
	 * Get connection 
	 * @param datascource
	 * @return
	 * @throws Exception
	 */
    static private Connection getConnection(String datascource) throws Exception{
		
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
     * Log <b>this</b> objects statements
     * @param log
     */
    public void setLog(boolean log) {
        this.log = log;
    }

    /**
     * JDBC PreparedStatement.setString()
     * @param index
     * @param value
     * @throws Exception
     */
    public void setString(int index, String value) throws Exception{
        if (value == null){
            statement.setNull(index, Types.NULL);
        }
        else{
            statement.setString(index, value);
        }
    	if (log){
    	    parameters.add(value != null? "'" + value.toString() + "'": "NULL");
    	}
    }
    
    
    /**
     * JDBC PreparedStatement.setLong()
     * @param index
     * @param value
     * @throws Exception
     */
    public void setLong(int index, Long value) throws Exception{
        if (value == null){
            statement.setNull(index, Types.NULL);
        }
        else{
            statement.setLong(index, value);
        }
        
    	if (log){
    	    parameters.add(value != null? value.toString() : "NULL");
    	}
    }

    /**
     * JDBC PreparedStatement.setDate()
     * @param index
     * @param value
     * @throws Exception
     * 
     * Thanks to http://stackoverflow.com/questions/530012/how-to-convert-java-util-date-to-java-sql-date
     */
    public void setDate(int index, Date value) throws Exception{
        if (value == null){
            statement.setNull(index, Types.NULL);
        }
        else{
            statement.setDate(index, new java.sql.Date(value.getTime()));
        }
        if (log){
            parameters.add(value != null? Utilities.formatDate(value, "dd.MMM.yy") : "NULL");
        }
    }
    
    /**
     * JDBC PreparedStatement.setDate()
     * @param index
     * @param value
     * @throws Exception
     * 
     * Thanks to http://stackoverflow.com/questions/530012/how-to-convert-java-util-date-to-java-sql-date
     */
    public void setTimeStamp(int index, Date value) throws Exception{
        if (value == null){
            statement.setNull(index, Types.NULL);
        }
        else{
            statement.setTimestamp(index, new java.sql.Timestamp(value.getTime()));
        }
        if (log){
            parameters.add(value != null? Utilities.formatDate(value, "dd.MMM.yy hh:mm:ss") : "NULL");
        }
    }
    
    /**
     * JDBC PreparedStatement.setInt()
     * @param index
     * @param value
     * @throws Exception
     */
    public void setInt(int index, Integer value) throws Exception{
        if (value == null){
            statement.setNull(index, Types.NULL);
        }
        else{
            statement.setInt(index, value);
        }
    	if (log){
    	    parameters.add(value != null? value.toString() : "NULL");
    	}
    }
    
    /**
     * JDBC PreparedStatement.setDouble()
     * @param index
     * @param value
     * @throws Exception
     */
    public void setDouble(int index, Double value) throws Exception{
        if (value == null){
            statement.setNull(index, Types.NULL);
        }
        else{
            statement.setDouble(index, value);
        }
        
        if (log){
            parameters.add(value != null? value.toString() : "NULL");
        }
    }
    
    /**
     * JDBC PreparedStatement.setBoolean()
     * @param index
     * @param value
     * @throws Exception
     */
    public void setBoolean(int index, Boolean value) throws Exception{
        statement.setBoolean(index, value);
        if (log){
            parameters.add(value != null? value.toString() : "NULL");
        }
    }
    

	/**
	 * Execute the prepared query.<p>
	 * 
	 * @param BaseSql object, can be <code>null</code>. Note currently ignored, but future use will implement offsets.
	 * @return Extended result set
	 * @throws Exception
	 */
	public void executeUpdate() throws Exception{
		
		StringBuffer params = null;
		if (log){
		    params = new StringBuffer(" params=");
		    int x = 0;
		    for (String s: parameters){
		        params.append((x++>0?",":"") + s);
		    }
		}
		
		try {
			statement.executeUpdate();
			
			if (log){
			    log("executeUpdate" + params.toString());
			    parameters.clear();
			}
			
			
		} catch (Exception e){
			try{
				log("Rollback, " + (params != null? params.toString() : "") + ": ex=" + e.getMessage());
				connection.rollback();
			}
			catch (Exception ex){
				log("Can't Rollback: ex=" + ex.getMessage());
				throw e;
			}
			close(null, connection);
		} 
	}
    
	/**
     * Close the statement in <b>this</b> object.
     * 
     * @see http://www.mastertheboss.com/jboss-datasource/how-to-configure-a-datasource-with-jboss-7
     */
    public void commitAndCloseStatement() throws Exception{
    	try{
    		connection.commit();
		}
		catch (Exception ex){
			log("Can't commit preparaed statement, ex=" + ex.getMessage());
			connection.rollback();
			throw ex;
		}
    	finally{
    	    close(statement, connection);
    	    statement  = null;
            connection = null;
    	}
    }
	
	
	/**
     * Close the sql objects in <b>this</b> object.
     * 
     * @see http://www.mastertheboss.com/jboss-datasource/how-to-configure-a-datasource-with-jboss-7
     * @param Statement
     * @param Connection
     */
    private void close(PreparedStatement statement, Connection connection) {
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
     */
	private void log (String query){
		if (query != null){
			String [] statements = query.split(";");
			for (String s : statements){
				ApplicationLog.debugNoCaller(s + ";");
			}
			
		}
	}

	/**
	 * Return the JDBC statement
	 * @return
	 */
    public PreparedStatement getStatement() {
        return statement;
    }
    
    
	
	
}
