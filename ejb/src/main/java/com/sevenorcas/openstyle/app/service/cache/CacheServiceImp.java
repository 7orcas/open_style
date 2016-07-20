package com.sevenorcas.openstyle.app.service.cache;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.sevenorcas.openstyle.app.mod.company.Company;
import com.sevenorcas.openstyle.app.mod.lang.LangKey;



/**
 * Singleton class to cache application objects.<p>
 * 
 * Currently this is a simple implementation using a Hashtable.<p>
 * 
 * TODO However future versions may be extended to use <code>Hazelcast</code>.<p>
 * 
 * Refer http://www.hazelcast.com/
 * 
 * [License] 
 * @author John Stewart
 */
public class CacheServiceImp implements CacheService{

	static private CacheServiceImp instance;
	private Hashtable<String, Object> table;
	
	private CacheServiceImp (){
		table = new Hashtable<>();
	}
	
	static public CacheServiceImp getInstance(){
		if (instance == null){
			instance = new CacheServiceImp();
		}
		return instance;
	}
	
	/**
	 * Return a consistent cache type suffix
	 * @param String cache key 
	 * @param String cache type
	 * @return concatenated key value
	 */
	private String key(String key, String type){
		return key + ":" + type;
	}
	
	/**
	 * Get a <code>String</code> value
	 * @param String key
	 * @return String value
	 */
	private String getString(String key){
		return (String)table.get(key);
	}
	
	
	/**
	 * Company Object
	 * @param Long company id
	 */
	public Company getCompany(Long id){
		return (Company)table.get(key("" + id, COMPANY_OBJECT));
	}
	
	/**
	 * Company Object
	 * @param Company object
	 */
	public void putCompany(Company obj){
		table.put(key("" + obj.getId(), COMPANY_OBJECT), obj);
		table.put(key("" + obj.getCompanyNr(), COMPANY_ID), obj.getId());
	}
	
	/**
	 * Is Company Object in cache?
	 * @param Company id
	 */
	public boolean isCompany(Long id){
		return table.containsKey(key("" + id, COMPANY_OBJECT));
	}
	
	/**
     * Company Object by number
     * @param Integer company nr
     */
    public Company getCompanyByNumber(Integer nr){
        Long id = (Long)table.get(key("" + nr, COMPANY_ID));
        if (id == null){
            return null;
        }
        return (Company)getCompany(id);
    }
    
	/**
	 * SQL Table name
	 * @param Class of entity
	 * @return Cached table name (or null if not cached)
	 */
	@SuppressWarnings("rawtypes")
	public String getTableName(Class clazz){
		return getString(key(clazz.getName(), SQL_TABLE_NAME));
	}
	
	/**
	 * SQL Table name
	 * @param Class of entity
	 * @param String table name 
	 */
	@SuppressWarnings("rawtypes")
	public void putTableName(Class clazz, String value){
		table.put(key(clazz.getName(), SQL_TABLE_NAME), value);
	}
	
	
	/**
     * Table language key
     * @param Class of entity
     * @return Cached table name (or null if not cached)
     */
    @SuppressWarnings("rawtypes")
    public String getTableLangKey(Class clazz){
        return getString(key(clazz.getName(), SQL_TABLE_LANG_KEY));
    }
    
    /**
     * Table language key
     * @param Class of entity
     * @param String table name 
     */
    @SuppressWarnings("rawtypes")
    public void putTableLangKey(Class clazz, String value){
        table.put(key(clazz.getName(), SQL_TABLE_LANG_KEY), value);
    }
	
	/**
	 * SQL Table-Field name
	 * @param Class of entity
	 * @param String field name
	 * @return Cached table name (or null if not cached)
	 */
	@SuppressWarnings("rawtypes")
	public String getTableColumnName(Class clazz, String fieldname){
		return getString(key(clazz.getName() + "." + fieldname, SQL_TABLE_COLOUMN));
	}
	
	/**
	 * SQL Table-Field name
	 * @param Class of entity
	 * @param String field name
	 * @param String table name 
	 */
	@SuppressWarnings("rawtypes")
	public void putTableColumnName(Class clazz, String fieldname, String value){
		table.put(key(clazz.getName() + "." + fieldname, SQL_TABLE_COLOUMN), value);
	}
	
	
	/**
	 * Method to cache the passed in classes fields. 
	 * @param Class to get fields 
	 * @param Boolean true = include fields from super classes
	 * @return Hashtable of fields
	 */
	@SuppressWarnings("rawtypes")
	public Hashtable<String, java.lang.reflect.Field> putFields (Class clazz, boolean includeSuperClasses){
		Hashtable<String, java.lang.reflect.Field> list = new Hashtable<>();
		getFields (clazz, includeSuperClasses, list);
		table.put(key(clazz.getName() + "." + (includeSuperClasses?"all":""), CLASS_FIELDS), list);
		return list;
	}

	
	
	/**
	 * Method to get passed in classes fields. If cached then the cache is returned. 
	 * @param CLass to get fields 
	 * @param Boolean true = include fields from super classes
	 * @return Hashtable of fields
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Hashtable<String, java.lang.reflect.Field> getFields (Class clazz, boolean includeSuperClasses){
		if (table.containsKey(key(clazz.getName() + "." + (includeSuperClasses?"all":""), CLASS_FIELDS))){
			return (Hashtable<String, java.lang.reflect.Field>)table.get(key(clazz.getName() + "." + (includeSuperClasses?"all":""), CLASS_FIELDS));
		}
		return putFields (clazz, includeSuperClasses);
	}
	
	/**
	 * Method to get passed in classes fields and return as a list. If cached then the cache is returned. 
	 * @param CLass to get fields 
	 * @param Boolean true = include fields from super classes
	 * @return List of fields
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<java.lang.reflect.Field> getFieldList (Class clazz, boolean includeSuperClasses){
		Hashtable<String, java.lang.reflect.Field> f = getFields (clazz, includeSuperClasses);
		
		List<java.lang.reflect.Field> list = new ArrayList();
		Enumeration<String> keys = f.keys();
		while(keys.hasMoreElements()){
			list.add(f.get(keys.nextElement()));
		}
		return list;
	}
	
	
	/**
	 * Return Class for passed in class name 
	 * @param String class name (can be relative)
	 * @return Cached class (or null if not cached)
	 */
	@SuppressWarnings("rawtypes")
	public Class getLocalInterfaceClass(String classname){
		return (Class)table.get(key(classname, EJB_LOCAL_INTERFACE));
	}
	
	/**
	 * Cache a class
	 * @param Class of entity
	 * @param String class name (can be relative) 
	 */
	@SuppressWarnings("rawtypes")
	public void putLocalInterfaceClass(Class clazz, String classname){
		table.put(key(classname, EJB_LOCAL_INTERFACE), clazz);
	}
	

	/**
	 * Return Method for passed in class name.method name 
	 * @param String class name (can be relative)
	 * @param String method name 
	 * @return java.lang.reflect.Method (or null if not cached)
	 */
	public java.lang.reflect.Method getMethod(String classname, String method){
		return (java.lang.reflect.Method)table.get(key(classname + "." + method, CLASS_METHOD));
	}
	
	/**
	 * Cache a Method for passed in class name.method name 
	 * @param java.lang.reflect.Method  
	 * @param String class name (can be relative)
	 * @param String method name 
	 */
	public void putMethod(java.lang.reflect.Method m, String classname, String method){
		table.put(key(classname + "." + method, CLASS_METHOD), m);
	}

	
	/**
	 * Language key-value pairs
	 * @param String language code
	 * @return Cached language key-value pairs (or null if not cached)
	 */
	@SuppressWarnings({"unchecked" })
	public Hashtable<String, String> getLanguage(String languageCode){
		return (Hashtable<String, String>)table.get(key(languageCode, LANGUAGE_CODE));
	}
	
	/**
	 * Language key-value pairs
	 * @param String language code
	 * @param Hashtable of language key-value pairs  
	 */
	public void putLanguage(String languageCode, Hashtable<String, String>values){
		table.put(key(languageCode, LANGUAGE_CODE), values);
	}
	
	/**
     * Language list
     * @param String language code
     * @return Cached language list (or null if not cached)
     */
    @SuppressWarnings({"unchecked" })
    public List<String> getLanguageList(String languageCode){
        return (List<String>)table.get(key(languageCode, LANGUAGE_LIST));
    }
	
	/**
     * Language list
     * @param String language code
     * @param List language month codes  
     */
    public void putLanguageList(String languageCode, List<String>values){
        table.put(key(languageCode, LANGUAGE_LIST), values);
    }
	
	
	/**
     * Clear the Language cache
     */
    public void clearLanguage(){
        for (String code: LangKey.getLanguageCodes()){
            table.remove(key(code, LANGUAGE_CODE));
        }
    }
	
	
	
	/**
	 * Method to get passed in classes fields (including super classes if required).<p>
	 * 
	 * Note that implementing classes will override superclass fields.
	 *  
	 * @param CLass to get fields
	 */
	@SuppressWarnings("rawtypes")
	private void getFields (Class clazz, boolean includeSuperClasses, Hashtable<String, java.lang.reflect.Field> list){

		if (includeSuperClasses && clazz.getSuperclass() != null){
			getFields (clazz.getSuperclass(), includeSuperClasses, list);
		}
		
		//Get list of this DTO's fields
		for (java.lang.reflect.Field field: clazz.getDeclaredFields()) {
		    field.setAccessible(true);
		    list.put(field.getName(), field);
		}
		
		
	}

	
	
	
	
}
