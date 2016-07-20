package com.sevenorcas.openstyle.app.service.temptable;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import javax.ejb.Stateless;

import com.sevenorcas.openstyle.app.mod.user.User;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.log.ApplicationLog;
import com.sevenorcas.openstyle.app.service.repo.BaseDao;
import com.sevenorcas.openstyle.app.service.sql.StatementX;

/**
 * Temporary Table Repository 
 * 
 * [License] 
 * @author John Stewart
 */ 
@Stateless
public class TempTableDaoImp extends BaseDao implements TempTableDao{

    final private Integer DEFAULT_COMP_NR = 0; 
    
	/**
	 * Default Constructor
	 */
	public TempTableDaoImp(){}
	
	
    /**
     * Return a new temporary table (but don't register)  
     * @param String table suffix
     * @return
     */
    public String getNameNoRegister (UserParam params, String suffix) throws Exception{
    	Long id = nextTempId();
    	return getName(params, id, suffix);
    }
    
    /**
     * Return a new temporary table (but don't register)
     * @param Integer company number (can be null)
     * @param String user id (can be null)
     * @param Long table id 
     * @param String table suffix
     * @return
     */
    public String getNameNoRegister (Integer companyNr, String userId, Long id, String suffix) throws Exception{
        return getName(companyNr, userId, id, suffix);
    }
    
    /**
     * Drop a temporary table and remove from register  
     * @param String table name
     * @return
     */
    public void drop (String table) throws Exception{
        StatementX.create("DROP TABLE IF EXISTS " + table + ";")
            .executeUpdate();
        
    }
    
    /**
     * Drop all temporary tables for the passed in user's company number
     * @param params
     * @throws Exception
     */
    public void dropAllTempTablesByCompNr (UserParam params) throws Exception{
        ApplicationLog.info("Dropping all temporary tables for company_nr=" + params.getCompany() + ", userid=" + params.getUserId());
        
        StatementX.create("CREATE OR REPLACE FUNCTION footgun(IN _schema TEXT, IN _parttionbase TEXT) " 
            + "RETURNS void " 
            + "LANGUAGE plpgsql AS $$ "
            + "DECLARE row record; "
            + "BEGIN "
                + "FOR row IN " 
                    + "SELECT table_schema, table_name "
                    + "FROM information_schema.tables "
                    + "WHERE table_type = 'BASE TABLE' "
                    + "AND table_schema = _schema "
                    + "AND table_name ILIKE (_parttionbase || '%') "
                + "LOOP "
                    + "EXECUTE 'DROP TABLE ' || quote_ident(row.table_schema) || '.' || quote_ident(row.table_name); "
                    + "RAISE INFO 'Dropped table: %', quote_ident(row.table_schema) || '.' || quote_ident(row.table_name); "
                + "END LOOP; "
            + "END; "
            + "$$;").executeUpdate();
        
        StatementX.create("SELECT footgun('temp', '" + getInitialTablePrefix(params.getCompany()) + "'), 'x' AS x;").executeQuery();
        StatementX.create("DROP FUNCTION footgun (_schema TEXT, _parttionbase TEXT);").executeUpdate();
    }
    
    /**
     * Return standard temporary table name 
     * @param UserParam object
     * @param Long id
     * @param String temporary table suffix (can be null)
     * @return
     */
    private String getName(UserParam param, Long id, String suffix) throws Exception{
        return getName(param !=null? param.getCompany() : DEFAULT_COMP_NR, 
                       param !=null? param.getUserId() : "", 
                       id, 
                       suffix);
    }
    
    /**
     * Return standard temporary table name 
     * @param Integer company number (can be null)
     * @param String user id (can be null)
     * @param Long id
     * @param String temporary table suffix (can be null)
     * @return
     */
    private String getName(Integer companyNr, String userId, Long id, String suffix) throws Exception{
        
        if (userId != null){
            userId = userId.replace(".", "_");
            for (int i=0; userId != null && i<User.PASSWORD_TO_INCLUDE.length(); i++){
                String c = "" + User.PASSWORD_TO_INCLUDE.charAt(i);
                userId = userId.replace(c, "_");
            }
        }
        
        return SCHEMA_TEMP + "." + getInitialTablePrefix(companyNr)
                   + (userId != null? userId + "_" : "")
                   + id 
                   + (suffix != null? "_" + suffix : "");
    }
    
    /**
     * Return the temporary table prefix
     * @param company number
     * @return
     */
    private String getInitialTablePrefix(Integer companyNr){
        return "t" + (companyNr != null? companyNr : DEFAULT_COMP_NR) + "_";
    }
    
    
    /**
     * Test if the passed in temporary table exists 
     * 
     * http://stackoverflow.com/questions/2942788/check-if-table-exists
     * @param String table name
     * @return
     */
    public boolean exists(String table) throws Exception{
        
        ResultSet rs   = null;
        Connection con = null;
        try{

            String schema = null;
            int index = table.indexOf(".");
            if (index != -1){
                schema = table.substring(0, index);
                table = table.substring(index+1);
            }
            
            con = StatementX.getConnection();
            DatabaseMetaData dbm = con.getMetaData();
            rs = dbm.getTables(null, schema, table.toLowerCase(), null);
            // Table exists
            if (rs.next()) {
                return true;
            }
            // Table does not exist
            else {
                return false;
            }
        }
        catch (Exception e){
            return false;
        }
        finally{
            try{
                rs.close();
            }
            catch (Exception e){}
            try{
                con.close();
            }
            catch (Exception e){}
        }
    }
    
	
	
}
