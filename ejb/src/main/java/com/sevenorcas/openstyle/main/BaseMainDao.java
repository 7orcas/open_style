package com.sevenorcas.openstyle.main;

import com.sevenorcas.openstyle.app.service.repo.BaseDao;
import com.sevenorcas.openstyle.mod.docu.ent.DocumentEnt;
import com.sevenorcas.openstyle.mod.docu.type.DocumentTypeEnt;

/**
 * Base Main Repository<p>
 * 
 * [License] 
 * @author John Stewart
 */
public class BaseMainDao extends BaseDao{

	//Table name definitions
	public static String T_MAIN_MENU                 = tableName(MainMenuEnt.class);
	public static String T_DOCUMENT                  = tableName(DocumentEnt.class);
	public static String T_DOCUMENT_TYPE             = tableName(DocumentTypeEnt.class);
	
	
}
