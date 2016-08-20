package com.sevenorcas.openstyle.main;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.sevenorcas.openstyle.app.mod.lang.LanguageService;
import com.sevenorcas.openstyle.app.mod.user.UserParam;


/**
 * Main Menu Service
 *  
 * [License] 
 * @author John Stewart
 */
@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class MainMenuServiceImp implements MainMenuService {
	
	@EJB private LanguageService   languageService;
	@EJB private MainMenuDao       mainMenuDao;
	
	public MainMenuServiceImp() {
	}
	
	
	/**
	 * Retrieve list of entities.
	 * @param UserParam object
	 * @param Sql object
	 */
	public List<MainMenu> list (UserParam params, MainMenuSql sql) throws Exception {
		return mainMenuDao.list(params, sql == null? new MainMenuSql(params).setOrderBySeq() : sql);
	}
	
    
}
