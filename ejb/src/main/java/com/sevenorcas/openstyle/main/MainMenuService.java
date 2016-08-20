package com.sevenorcas.openstyle.main;

import java.util.List;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.mod.user.UserParam;


/**
 * Local interface to Main Menu Service
 *
 * [License]
 * @author John Stewart
 */

@Local
public interface MainMenuService {
	public List<MainMenu> list (UserParam userParam, MainMenuSql sql) throws Exception;
}
