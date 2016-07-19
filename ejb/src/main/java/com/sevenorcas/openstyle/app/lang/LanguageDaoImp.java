package com.sevenorcas.openstyle.app.lang;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.sevenorcas.openstyle.app.entity.ReturnId;
import com.sevenorcas.openstyle.app.repo.BaseDao;
import com.sevenorcas.openstyle.app.sql.ResultSetX;
import com.sevenorcas.openstyle.app.sql.StatementX;
import com.sevenorcas.openstyle.app.temptable.TempTableDao;
import com.sevenorcas.openstyle.app.user.UserParam;

/**
 * Language Repository 
 * 
 * [License] 
 * @author John Stewart
 */ 
@Stateless
public class LanguageDaoImp extends BaseDao implements LanguageDao{

	@EJB private TempTableDao tempTableDao;
	
	@PersistenceContext(unitName="openstyleDS")
	private EntityManager em;
	
	/**
	 * Default Constructor
	 */
	public LanguageDaoImp(){}
	
	
	/**
	 * Retrieve language key-value list
	 * @param LangSql object
	 * @return LangKey list
	 */
    public Hashtable<String, LangKey> list (LangSql sql) throws Exception{
	    
		String lk = tableName(LangKey.class);
		String lv = tableName(LangValue.class);
		
		
		String lookup = null;
		
		//User advanced search
		if (sql.isSearchAdvanced()){
			lookup = StatementX.appendLookupAnd ("lk.code", sql.getKey(), lookup);
			lookup = StatementX.appendLookupAnd ("lk.sets", sql.getSets(), lookup);
			lookup = StatementX.appendLookupAnd ("lv.text", sql.getText(), lookup);
			
			if (!sql.isMissingCodes()){
			    lookup = StatementX.appendLookupAnd ("lv.langcode", sql.getCode(), lookup);
			}
		}
		
		//User selected code from drop down
		if (lookup == null){
			lookup = StatementX.appendLookupAnd ("lk.code", sql.getLookupCode(), lookup);
			lookup = StatementX.appendLookupAnd ("lv.text", sql.getLookupText(), lookup);
		}
		
		//User entered string
		if (lookup == null && sql.isLookup()){
			lookup = StatementX.appendLookupOr ("lk.code", sql.getLookup(), lookup);
			lookup = StatementX.appendLookupOr ("lv.text", sql.getLookup(), lookup);
		}
		
		
		
		/* *************************************************************************************
		 * Missing codes is a special case. Build a list of ids first.
		 **************************************************************************************/
		String tt_ids = null; 
		if (sql.isMissingCodes()){
			
			tt_ids = tempTableDao.getNameNoRegister(null, "lang");
			
			StatementX.create("CREATE TABLE " + tt_ids + " (id bigint, langcode character varying);"
					+ "CREATE INDEX " + tt_ids.replace(".", "_") + "_uc ON " + tt_ids + " USING btree (id);")
					.executeUpdate();
			
			for (String c: LangKey.getLanguageCodes()){
				
				if (sql.getCode() != null && !sql.getCode().equalsIgnoreCase(c)){
					continue;
				}
				
				StatementX.create("INSERT INTO " + tt_ids + " "
						+ "SELECT lk.id, lv.langcode "
						+ "FROM " + lk + " lk " 
						    + "LEFT JOIN " + lv + " lv ON (lk.id = lv.langkey_id AND lv.langcode='" + c + "') "
						+ "GROUP BY lk.id, lv.langcode "    
						+ "HAVING lv.langcode IS NULL;")
						.addWhere(lookup)
						.executeUpdate();
			}
			lookup = null;
		}
		
		
		/* *************************************************************************************
		 * Main query
		 **************************************************************************************/
		StatementX stx = StatementX
				.create("SELECT DISTINCT lk.id, lk.code, lk.sets, lk.client, lv.text, lv.langcode "
					  + "FROM " + lk + " lk "
					  		+ "LEFT JOIN " + lv + " lv ON (lk.id = lv.langkey_id) "
					  + (tt_ids != null? ", " + tt_ids + " tid " : "")
					  + "ORDER BY lk.code, lv.langcode")
				.appendBaseEntityFields("lk,lv")
				.addWhere(getCompanyNrSql(sql, "lv"))
				.addWhere(lookup)
				.countIfRequired(sql);
		
		
		if (tt_ids != null){
			stx.addWhere("tid.id = lk.id");
			stx.addWhere("lv.langcode='en'");
		}
		if (sql.isClientTrue()){
			stx.addWhere("lk.client = true");
		}
		else if (sql.isClientFalse()){
			stx.addWhere("lk.client = false");
		}
		
		ResultSetX rs = stx.executeQuery(sql);
		
		Hashtable<String, LangKey> list = new Hashtable<>();
		Hashtable<Long, LangKey> listX = new Hashtable<>();
		
		while(rs.next()){
			
			int count = 1;
			Long id = rs.getLong(count++);
			LangKey k = new LangKey();
			
			if (listX.containsKey(id)){
				k = listX.get(id);
				count += 3;
			}
			else{
				k = new LangKey();
				k.setKey(rs.getString(count++));
				k.setSets(rs.getString(count++));
				k.setClient(rs.getBoolean(count++));
				rs.setBaseEntityFields(k, "lk");
				list.put(k.getKey(), k);
				listX.put(id, k);
			}
			
			LangValue v = new LangValue();
			k.addLangValue(v);
			v.setText(rs.getString(count++));
			v.setLangcode(rs.getString(count++));
			rs.setBaseEntityFields(v, "lv");
		}
			
		StatementX.dropTempTable(tt_ids, sql);
		
		return list;
	}

    
    /**
	 * Load record id's for the passed in list of key/language code values.<p>
	 * 
	 * Note: This method assumes the <code>LangListDto</code> are for <code>LangValue</code> records.
	 * 
	 * @param UserParam object
	 * @param List of key-languageCode values to find
	 * @param LangSql object
	 */
	public List<ReturnId> findRecordIds (UserParam params, List<String[]> list, LangSql sql) throws Exception {
		
		String lk = tableName(LangKey.class);
		String lv = tableName(LangValue.class);
		
		StringBuffer sb = new StringBuffer("SELECT lv.id, lk.code, lv.langcode "
				  + "FROM " + lk + " lk,"
				  		    + lv + " lv "
				  + "WHERE lk.id = lv.langkey_id "
				  + "AND (");
		
		int count = 0;
		for (String [] s: list){
			sb.append((count++ > 0? " OR ":"") + "(lk.code = '" + s[0] + "' AND lv.langcode = '" + s[1] + "')");
		}
		sb.append(")");
		
		ResultSetX rs = StatementX
				.create(sb.toString())
				.addWhere(getCompanyNrSql(sql, "lv"))
				.executeQuery(null);
		
		
		List<ReturnId> listX = new ArrayList<ReturnId>();
		
		while(rs.next()){
			Long id = rs.getLong(1);
			Object [] x = new Object[] {rs.getString(2), rs.getString(3)};
			
			listX.add(new ReturnId (id,x));
		}
		
		return listX;
	}
	
    
	
	/**
	 * Return the company number <code>SQL WHERE Clause</code> for the language table
	 * @param language value table
	 * @return
	 */
	private String getCompanyNrSql(LangSql sql, String table) {
		if (sql.getCompanyNr() == null){
			return null;
		}
		if (sql.getCompanyNr().intValue() == COMPANY_NUMBER_IGNORE){
			return table + ".comp_nr = " + COMPANY_NUMBER_IGNORE;
		}
		
		return "(" + table + ".comp_nr = " + COMPANY_NUMBER_IGNORE + " OR " 
			       + table + ".comp_nr = " + sql.getCompanyNr() + ")";
	}
	
	
    
    /**
     * Save new / update record
     * @param UserParam object
     * @param LangKey entity
     * @return
     */
    public LangKey save (UserParam params, LangKey entity) throws Exception{
    	return super.save(params, entity, em);
    }
	
    
    /**
	 * Find an LangKey entity by its ID
	 * @param Long id
	 * @return
	 */
	public LangKey findLangKeyById (Long id) throws Exception{
		return super.findById(LangKey.class, id, em);
	}
	
	/**
	 * Find an LangKey entity by a LangValue ID (ie its child)
	 * @param Long id
	 * @return
	 */
	public LangKey findLangKeyByLangValueId (Long id) throws Exception{
		String table = tableName(LangValue.class);
		ResultSetX rs = StatementX
				.create("SELECT langkey_id FROM " + table)
				.addWhere("id = " + id)
				.executeQuery();
		
		Long key_id = null; 
		
		while(rs.next()){
			key_id = rs.getLong(1);
		}
		
		return key_id != null? findLangKeyById (key_id) : null;
	}
	
	/**
	 * Find an LangKey entity by its code
	 * @param Long id
	 * @return
	 */
	public LangKey findLangKeyByCode (String code) throws Exception{
		String table = tableName(LangKey.class);
		ResultSetX rs = StatementX
				.create("SELECT id FROM " + table)
				.addWhere("code = '" + code + "'")
				.executeQuery();
		
		Long id = null; 
		
		while(rs.next()){
			id = rs.getLong(1);
		}
		
		return id != null? findLangKeyById (id) : null;
	}
	
	
}
