package com.sevenorcas.openstyle.app.mod.lang;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.entity.ReturnId;
import com.sevenorcas.openstyle.app.service.repo.BaseDao;
import com.sevenorcas.openstyle.app.service.sql.ResultSetX;
import com.sevenorcas.openstyle.app.service.sql.StatementX;
import com.sevenorcas.openstyle.app.service.temptable.TempTableDao;

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
	    
		
		String lookup = null;
		
		//User advanced search
		if (sql.isSearchAdvanced()){
			lookup = StatementX.appendLookupAnd ("lk.code", sql.getKey(), lookup);
			lookup = StatementX.appendLookupAnd ("lk.sets", sql.getSets(), lookup);
			lookup = StatementX.appendLookupAnd ("lc.lang_text", sql.getText(), lookup);
			
			if (!sql.isMissingCodes()){
			    lookup = StatementX.appendLookupAnd ("lc.lang_text", sql.getCode(), lookup);
			}
		}
		
		//User selected code from drop down
		if (lookup == null){
			lookup = StatementX.appendLookupAnd ("lk.code", sql.getLookupCode(), lookup);
			lookup = StatementX.appendLookupAnd ("lc.lang_text", sql.getLookupText(), lookup);
		}
		
		//User entered string
		if (lookup == null && sql.isLookup()){
			lookup = StatementX.appendLookupOr ("lk.code", sql.getLookup(), lookup);
			lookup = StatementX.appendLookupOr ("lc.lang_text", sql.getLookup(), lookup);
		}
		
		
		
		/* *************************************************************************************
		 * Missing codes is a special case. Build a list of ids first.
		 **************************************************************************************/
		String tt_ids = null; 
		if (sql.isMissingCodes()){
			
			tt_ids = tempTableDao.getNameNoRegister(null, "lang");
			
			StatementX.create("CREATE TABLE " + tt_ids + " (id bigint, lang_text character varying);"
					+ "CREATE INDEX " + tt_ids.replace(".", "_") + "_uc ON " + tt_ids + " USING btree (id);")
					.executeUpdate();
			
			for (String c: LangKey.getLanguageCodes()){
				
				if (sql.getCode() != null && !sql.getCode().equalsIgnoreCase(c)){
					continue;
				}
				
				StatementX.create("INSERT INTO " + tt_ids + " "
						+ "SELECT lk.id, lc.lang_text "
						+ "FROM " + T_LANG_KEY + " lk " 
						    + "LEFT JOIN " + T_LANG_CODE + " lc ON (lk.id = lc.lang_key_id AND lc.code='" + c + "') "
						+ "GROUP BY lk.id, lc.code "    
						+ "HAVING lc.code IS NULL;")
						.addWhere(lookup)
						.executeUpdate();
			}
			lookup = null;
		}
		
		
		/* *************************************************************************************
		 * Main query
		 **************************************************************************************/
		StatementX stx = StatementX
				.create("SELECT DISTINCT lk.id, lk.code, lk.sets, lk.client, lc.code, lc.lang_text "
					  + "FROM " + T_LANG_KEY + " lk "
					  		+ "LEFT JOIN " + T_LANG_CODE + " lc ON (lk.id = lc.lang_key_id) "
					  + (tt_ids != null? ", " + tt_ids + " tid " : "")
					  + "ORDER BY lk.code, lc.code")
				.appendBaseEntityFields("lk,lc")
				.addWhere(getCompanyNrSql(sql, "lc"))
				.addWhere(lookup)
				.countIfRequired(sql);
		
		
		if (tt_ids != null){
			stx.addWhere("tid.id = lk.id");
			stx.addWhere("lc.lang_text='en'");
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
			
			LangCode v = new LangCode();
			k.addLangValue(v);
			v.setLangcode(rs.getString(count++));
			v.setText(rs.getString(count++));
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
		
		StringBuffer sb = new StringBuffer("SELECT lc.id, lk.code, lc.lang_text "
				  + "FROM " + T_LANG_KEY + " lk,"
				  		    + T_LANG_CODE + " lc "
				  + "WHERE lk.id = lc.lang_key_id "
				  + "AND (");
		
		int count = 0;
		for (String [] s: list){
			sb.append((count++ > 0? " OR ":"") + "(lk.code = '" + s[0] + "' AND lc.lang_text = '" + s[1] + "')");
		}
		sb.append(")");
		
		ResultSetX rs = StatementX
				.create(sb.toString())
				.addWhere(getCompanyNrSql(sql, "lc"))
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
		
		ResultSetX rs = StatementX
				.create("SELECT lang_key_id FROM " + T_LANG_CODE)
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
		ResultSetX rs = StatementX
				.create("SELECT id FROM " + T_LANG_KEY)
				.addWhere("code = '" + code + "'")
				.executeQuery();
		
		Long id = null; 
		
		while(rs.next()){
			id = rs.getLong(1);
		}
		
		return id != null? findLangKeyById (id) : null;
	}
	
	
}
