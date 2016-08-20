package com.sevenorcas.openstyle.main;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.sql.ResultSetX;
import com.sevenorcas.openstyle.app.service.sql.StatementX;

/**
 * Main Menu Repository 
 * 
 * [License] 
 * @author John Stewart
 */ 
@Stateless
public class MainMenuDaoImp extends BaseMainDao implements MainMenuDao{

	/**
	 * Persistence context corresponds to the persistence-unit in 
	 * ejb/src/main/resources/META-INF/persistence.xml
	 */
	@PersistenceContext(unitName="openstyleDS")
	private EntityManager em;
   
	/**
	 * Default Constructor
	 */
	public MainMenuDaoImp(){}
	
	
	/**
	 * Retrieve entity list
	 * @param User object
	 * @param Sql object
	 * @return Entity list
	 */
    public List<MainMenu> list (UserParam params, MainMenuSql sql) throws Exception{
	    
        StatementX x = StatementX
				.create("SELECT t.seq, t.lang_code "
						+ "FROM " + T_MAIN_MENU + " t "
						+ (sql.isOrderBySeq()?"ORDER BY t.seq ":"")) 
			    .addActive(sql, "t");
        
		ResultSetX rs = x.executeQuery(sql); 
		
		List<MainMenu> list = new ArrayList<MainMenu>();
		
		while(rs.next()){
			MainMenu m = new MainMenu();
			list.add(m);
			
			int count = 1;
			
			m.setSeq(rs.getInt(count++));
			m.setLangCode(rs.getString(count++));

		}
				
		return list;
	}
	
	
}
