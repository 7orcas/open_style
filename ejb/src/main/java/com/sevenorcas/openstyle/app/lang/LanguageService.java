package com.sevenorcas.openstyle.app.lang;

import java.util.Hashtable;
import java.util.List;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.dto.ReturnIdDto;
import com.sevenorcas.openstyle.app.user.BaseUserParam;
import com.sevenorcas.openstyle.app.user.UserParam;

/**
 * Local <code>LanguageService</code> bean interface.
 * 
 * [License]
 * @author John Stewart
 */

@Local
public interface LanguageService {
	public Hashtable<String, String> lang(BaseUserParam params, String set, Boolean client) throws Exception;
	public List<LangKey> langAll(LangSql sql) throws Exception;
	public Language getLanguage (String lang) throws Exception;
	public LangKey findLangKeyById (Long id) throws Exception;
	public LangKey findLangKeyByLangValueId (Long id) throws Exception;
	public LangKey findLangKeyByCode (String code) throws Exception;
	public List<LangKey> save (UserParam params, List<LangKey> list) throws Exception;
	public LangKey save (UserParam params, LangKey rec) throws Exception;
	public List<ReturnIdDto> findRecordIds (UserParam params, List<LangListDto> list) throws Exception;
	public void emptyCache();
	
	public List<String> getDaysShort(String langCode) throws Exception;
	public List<String> getMonths(String langCode) throws Exception;
	public List<String> getMonthsShort(String langCode) throws Exception;
}
