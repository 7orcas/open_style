package com.sevenorcas.openstyle.main;

import java.util.List;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.repo.EntityRepoI;

/**
 * Local Main Menu Repository interface  
 * 
 * [License] 
 * @author John Stewart
 */
@Local
public interface MainMenuDao extends EntityRepoI{
	public List<MainMenu> list (UserParam params, MainMenuSql search) throws Exception;
}
