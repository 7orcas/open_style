package com.sevenorcas.openstyle.app.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import com.sevenorcas.openstyle.app.perm.Permission;
import com.sevenorcas.openstyle.app.user.BaseUserParam;
import com.progenso.desma.app.entities.ReturnId;
import com.progenso.desma.app.entities.ReturnIdDto;

import com.progenso.desma.app.servercache.ServerCache;
import com.sevenorcas.openstyle.app.user.UserParam;


/**
 * Service to provide multiple language processing functionality to the client.<p>
 * 
 * Languages values (ie text) are accessed via a language code and key. All language codes share the same key, but of course different languages have different values.<br>
 * eg 
 * <ul>English: <code>code="en", key="Yes", value="Yes"</code></ul>    
 * <ul>Deutsch: <code>code="de", key="Yes", value="Ja"</code></ul><p>
 * 
 * An extra configuration attribute also exists, <code>set</code>. This code is used to logically group language keys into modules.<br>
 * eg <code>set = "Login"</code> contains key-value pairs relevant to the Login process.<br>
 * The client has the option to use this attribute in its call to <b>this</b> service. In a future release modules will be linked to a <code>set</code>, 
 * enabling the client to selectively load key-value pairs as required.<p>  
 *  
 * Also in a future release the user site and company code attributes will become extra language configuration attributes. This will enable 
 * site/company specific language values to override default values.<p>    
 *  
 * The client uses the language key to display a label or language dependent field value. This combined with the clients language code will enable the display of the correct
 * text value. See <code>app/lang/service.js</code> javascript module for client side processing.<p>
 *  
 * Currently language definitions are store within the mock file <code>LanguageMock</code>. This will change in the future to be stored in a database table. However
 * it is not expected that <b>this</b> class will change as a result.<p>
 * 
 * TODO: Implement site/company code language attributes to enable override of default values.<br>
 * TODO: Implement languages in a database table.<br>
 *
 * @see LangDto
 * 
 * [License] 
 * @author John Stewart
 */

@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class LanguageServiceImp implements LanguageService {

	
	/**
	 * Current language implementation. This will change in the future to be stored in database table. 
	 */
	//private LanguageMock langMock =  new LanguageMock();
	@EJB private LanguageDao languageDao;
	
	/**
	 * Cache table of language key-value pairs.<br>
	 * The key is language code. 
	 */
	private ServerCache cache = ServerCache.getInstance();
	
	
	public LanguageServiceImp() {
	}
	
	
	/** 
	 * Return language key-value pairs per for the passed in <code>set</code>. If <code>set</code> is empty
	 * then all key-value pairs are returned.
	 * 
	 * @param BaseUserParam user parameters containing language code
	 * @param String language set (if empty the all key-value pairs are returned).
	 * @param Boolean client only records
	 * @return List LangDto objects
	 */
	@Override
	public Hashtable<String, String> lang(BaseUserParam params, String set, Boolean client) throws Exception {
		return langX(params.getLanguageCode(), params.getCompany(), set, client);
	}
	
	
	/** 
	 * Return language key-value pairs per for the passed in <code>set</code>. If <code>set</code> is empty
	 * then all key-value pairs are returned.<p>
	 * 
	 * @param String language code
	 * @param Integer company code
	 * @param String language set (if empty the all key-value pairs are returned).
	 * @return Hashtable of language key-value pairs
	 */
	private Hashtable<String, String> langX(String lang, Integer companyNr, String sets, Boolean client) throws Exception {
		
		sets = (sets!=null?sets.toLowerCase():"");
		client = client != null? client : false;
		
		lang = LangKey.validate(lang);
		Hashtable<String, String> keyValues = cache.getLanguage(lang + sets + (client?"-c":""));
		
		if (keyValues != null){
			return keyValues;
		}
		
		//Get List and process
		LangSql sql = new LangSql(new UserParam(companyNr, lang, "", -1L));
		sql.setSets(sets);
		
		if (client){
			sql.setClient(LangSql.SELECTION_YES);
		}
		
		Hashtable<String, LangKey> list = languageDao.list(sql);
		
		String [] langCodes = LangKey.getLanguageCodes();
		keyValues = new Hashtable<String, String>(); 
		Enumeration<String>keys = list.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			
			LangKey k = list.get(key);
			LangValue v = k.findByLangCode(lang);
			
			//Find according to language priority
			if (v == null || !v.isValid()){
				for (String code: langCodes){
					v = k.findByLangCode(code);
					if (v != null && v.isValid()){
						break;
					}
				}
			}
				
			if (v != null && v.isValid()){
				keyValues.put(k.getKey(), v.getText());
			}
		}
		
		cache.putLanguage(lang + sets + (client?"-c":""), keyValues);
		
		return keyValues;
	}	
	
	public void emptyCache(){
	    cache.clearLanguage();
	}
	
	/**
     * Return day short codes for passed in language code
     * @param lang code
     * @return
     */
    public List<String> getDaysShort(String langCode) throws Exception{
        langCode = LangKey.validate(langCode);
        List<String> days = cache.getLanguageList(langCode + "-d");
        
        if (days != null){
            return days;
        }
        days = new ArrayList<>(7);
        
        Hashtable<String, String> lang = langX(langCode, null, null, null);
        for (int i=1;lang != null && i<=12;i++){
            days.add(lang.get("Day" + i + "S"));
        }
        cache.putLanguageList(langCode + "-d", days);
        return days;
    }
	
	/**
	 * Return months of year for passed in language code
	 * @param lang code
	 * @return
	 */
	public List<String> getMonths(String langCode) throws Exception{
	    langCode = LangKey.validate(langCode);
        List<String> months = cache.getLanguageList(langCode + "-m");
        
        if (months != null){
            return months;
        }
        months = new ArrayList<>(12);
        
        Hashtable<String, String> lang = langX(langCode, null, null, null);
        for (int i=1;lang != null && i<=12;i++){
            months.add(lang.get("Month" + i));
        }
        cache.putLanguageList(langCode + "-m", months);
        return months;
	}
	
	/**
     * Return months of year (3 letter codes) for passed in language code
     * @param lang code
     * @return
     */
    public List<String> getMonthsShort(String langCode) throws Exception{
        langCode = LangKey.validate(langCode);
        List<String> months = cache.getLanguageList(langCode + "-s");
        
        if (months != null){
            return months;
        }
        months = new ArrayList<>(12);
        
        Hashtable<String, String> lang = langX(langCode, null, null, null);
        for (int i=1;lang != null && i<=12;i++){
            months.add(lang.get("Month" + i + "S"));
        }
        cache.putLanguageList(langCode + "-s", months);
        return months;
    }
	
	
	/** 
	 * Return language key-value pairs for the passed in <code>sql</code> search object. 
	 *   
	 * @param LangSql search object
	 * @return List LangKey objects (containing their respective LangValue objects)
	 */
	@Override
	public List<LangKey> langAll(LangSql sql) throws Exception{
		
		Hashtable<String, LangKey> list = languageDao.list(sql);
		List<LangKey> listX = new ArrayList<>(); 
		
		Enumeration<String>keys = list.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			listX.add(list.get(key));
		}
		
		Collections.sort(listX, new Comparator<LangKey>() {
			public int compare(LangKey o1, LangKey o2) {
				return o1.getKey().compareToIgnoreCase(o2.getKey());
			}
		});
		
		return listX;
	}
	
	
	/**
	 * Return requested language cache object. If null, then the default language code cache object is returned.
	 * @param String language code
	 * @return Language cache object 
	 */
	@Override
	public Language getLanguage (String lang) throws Exception {
		Hashtable<String, String> keyValues = langX(lang, null, null, null); 
		return new Language(keyValues, getMonths(lang), getMonthsShort(lang));
	}
	
	/**
	 * Save records.
	 * @param UserParam object
	 * @param List of LangKey objects to save
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<LangKey> save (UserParam params, List<LangKey> list) throws Exception {
		List<LangKey> listX = new ArrayList<LangKey>();
		for (LangKey e: list){
			e = save(params, e);
			if (e != null){
				listX.add(e);
			}
		}
		return listX;
	}
	
	/**
	 * Load record id's for the passed in language value objects.<p>
	 * 
	 * Note: This method assumes the <code>LangListDto</code> are for <code>LangValue</code> records.
	 * 
	 * @param UserParam object
	 * @param List of LangListDto objects to load id's for
	 */
	public List<ReturnIdDto> findRecordIds (UserParam params, List<LangListDto> list) throws Exception {
		List<String[]> listX = new ArrayList<String[]>();
		for (LangListDto e: list){
			String[] x = new String[]{e.getKey(), e.getLangCode()};
			listX.add(x);
		}
		List<ReturnId> listY = languageDao.findRecordIds (params, listX, new LangSql (params));
		
		List<ReturnIdDto> listZ = new ArrayList<ReturnIdDto>();
		
		for (LangListDto e: list){
			ReturnId r = null;
			
			for (ReturnId x: listY){
				Object [] v = x.getValues();
				if (e.getKey().equals((String)v[0])
						&& e.getLangCode().equalsIgnoreCase((String)v[1])){
					r = x;
					break;
				}
			}
			
			listZ.add(new ReturnIdDto(r.getId(), e.getId()));
		}
		
		
		return listZ;
	}
	
	/**
	 * Save new record.
	 * @param UserParam object
	 * @param LangKey object
	 */
	@Permission(service=true)
	public LangKey save (UserParam params, LangKey rec) throws Exception {
		return languageDao.save(params, rec);
	}
	
	
	/**
	 * Find a LangKey entity by its ID
	 * @param Long id
	 * @return
	 */
	public LangKey findLangKeyById (Long id) throws Exception{
		return languageDao.findLangKeyById(id);
	}
	
	/**
	 * Find a LangKey entity by a LangValue ID (ie its child)
	 * @param Long id
	 * @return
	 */
	public LangKey findLangKeyByLangValueId (Long id) throws Exception{
		return languageDao.findLangKeyByLangValueId(id);
	}

	public LangKey findLangKeyByCode (String code) throws Exception{
		return languageDao.findLangKeyByCode(code);
	}
	
	
}
