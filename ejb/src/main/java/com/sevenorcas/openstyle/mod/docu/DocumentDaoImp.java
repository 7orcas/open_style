package com.sevenorcas.openstyle.mod.docu;

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
 * Document Repository 
 * 
 * [License] 
 * @author John Stewart
 */ 
@Stateless
public class DocumentDaoImp extends BaseMainDao implements DocumentDao{

	/**
	 * Persistence context corresponds to the persistence-unit in 
	 * ejb/src/main/resources/META-INF/persistence.xml
	 */
	@PersistenceContext(unitName="openstyleDS")
	private EntityManager em;
   
	/**
	 * Default Constructor
	 */
	public DocumentDaoImp(){}
	
	
	/**
	 * Retrieve entity list
	 * @param User object
	 * @param Sql object
	 * @return Entity list
	 */
    public List<DocumentEnt> list (UserParam params, DocumentCnt sql) throws Exception{
	    
        StatementX x = StatementX
				.create("SELECT t.main_menu_id, t.seq, t.document_type_id, t.document_text "
						+ "FROM " + T_DOCUMENT + " t "
						+ "ORDER BY t.main_menu_id, t.seq")
			    .addActive(sql, "t")
			    .addCompNr(sql, "t")
				.appendBaseEntityFields("t");
		
		ResultSetX rs = x.executeQuery(sql); 
		
		List<DocumentEnt> list = new ArrayList<DocumentEnt>();
		
		while(rs.next()){
			DocumentEnt m = new DocumentEnt();
			list.add(m);
			
			int count = 1;
			
			m.setMainMenuId(rs.getLong(count++));
			m.setSeq(rs.getInt(count++));
			m.setTypeId(rs.getLong(count++));
			m.setText(rs.getString(count++));
			rs.setBaseEntityFields(m, "t");
		}
				
		return list;
	}
	
    
}
