package com.sevenorcas.openstyle.mod.docu.type;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.sql.ResultSetX;
import com.sevenorcas.openstyle.app.service.sql.StatementX;
import com.sevenorcas.openstyle.main.BaseMainDao;

/**
 * Document Type Repository 
 * 
 * [License] 
 * @author John Stewart
 */ 
@Stateless
public class DocumentTypeDaoImp extends BaseMainDao implements DocumentTypeDao{

	/**
	 * Persistence context corresponds to the persistence-unit in 
	 * ejb/src/main/resources/META-INF/persistence.xml
	 */
	@PersistenceContext(unitName="openstyleDS")
	private EntityManager em;
   
	/**
	 * Default Constructor
	 */
	public DocumentTypeDaoImp(){}
	
	
	/**
	 * Retrieve entity list
	 * @param User object
	 * @param Sql object
	 * @return Entity list
	 */
    public List<DocumentTypeEnt> list (UserParam params, DocumentTypeSql sql) throws Exception{
	    
        StatementX x = StatementX
				.create("SELECT t.code, t.config "
						+ "FROM " + T_DOCUMENT_TYPE + " t "
						+ "ORDER BY t.code")
			    .addActive(sql, "t")
			    .addCompNr(sql, "t")
				.appendBaseEntityFields("t");
		
		ResultSetX rs = x.executeQuery(sql); 
		
		List<DocumentTypeEnt> list = new ArrayList<DocumentTypeEnt>();
		
		while(rs.next()){
			DocumentTypeEnt m = new DocumentTypeEnt();
			list.add(m);
			
			int count = 1;
			
			m.setCode(rs.getString(count++));
			m.setConfig(rs.getString(count++));
			m.decode(m.getConfig());
			rs.setBaseEntityFields(m, "t");
		}
				
		return list;
	}
	
    
}
