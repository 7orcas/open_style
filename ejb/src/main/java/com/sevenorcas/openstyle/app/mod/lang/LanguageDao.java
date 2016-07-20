package com.sevenorcas.openstyle.app.mod.lang;

import java.util.Hashtable;
import java.util.List;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.entity.ReturnId;
import com.sevenorcas.openstyle.app.service.repo.EntityRepoI;

/**
 * Local Language Repository interface  
 * 
 * [License] 
 * @author John Stewart
 */
@Local
public interface LanguageDao extends EntityRepoI{
	public Hashtable<String, LangKey> list (LangSql search) throws Exception;
	public LangKey findLangKeyById (Long id) throws Exception;
	public LangKey findLangKeyByLangValueId (Long id) throws Exception;
	public LangKey findLangKeyByCode (String code) throws Exception;
	public LangKey save (UserParam params, LangKey entity) throws Exception;
	public List<ReturnId> findRecordIds (UserParam params, List<String[]> list, LangSql sql) throws Exception;
}
