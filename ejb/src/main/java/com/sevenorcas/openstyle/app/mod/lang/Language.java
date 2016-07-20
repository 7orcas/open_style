package com.sevenorcas.openstyle.app.mod.lang;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Language cache class.<br>
 * Used to improve system performance.<br>
 * Note: Language cache objects can be requested by other services to process language dependent responses.<br>
 * Note: The cache key is a concatenation of the language code plus the language set. <p>
 * 
 * [License] 
 * @author John Stewart
 */
public class Language implements LanguageI{
	
	/** List of language key-value pairs */ private Hashtable<String, String> keyValues;
	/** Month codes in order             */ private List<String> months;
	/** Short Month codes in order       */ private List<String> monthsShort;
    
	/**
	 * Constructor
	 * @param List of language key-value pairs.
	 */
	public Language(Hashtable<String, String> keyValues, List<String> months, List<String> monthsShort) {
		this.keyValues   = keyValues;
		this.months      = months;
		this.monthsShort = monthsShort;
	}
	
	
	public boolean contains (String key){
		if (keyValues == null){
			return false;
		}
		int index = key.indexOf("%");
		
		if (index != -1){
			key = key.substring(0, index);
		}
		
		return keyValues.containsKey(key);
	}
	
	/**
	 * Return a language value for the passed in <code>key</code>.<p>
	 * 
	 * The passed in <code>key</code> may contain a "%" character followed by a value. This is a substitute value.<br> 
	 * Note the language value must contain corresponding "%%" characters. These will be substituted with the passed values.<p>
	 * 
	 * eg<br>
	 * <code>
	 * <ul>language code="abd%5"</ul>
	 * <ul>value="This is a text for %% people"</ul>
	 * <ul>returned="This is a text for 5 people"</ul>
	 * </code>
	 *    
	 * @param String language key (may contain "%" substitutes)
	 * @return language value
	 */
	public String getLabel(String key){
		if (keyValues == null || key == null){
			return key;
		}
		int index = key.indexOf("%");
		
		if (index == -1){
			if (keyValues.containsKey(key)){
				return keyValues.get(key);
			}
			return key;
		}
		
		
		ArrayList<String> values = new ArrayList<String>();
		String [] s = key.split("%");
		for (int i=1;i<s.length;i++){
			values.add(s[i]);
		}
        key = key.substring(0, index);
		
        String text = keyValues.get(key);
        index = text.indexOf("%%");		
        int pointer = 0;
        
		//Find '%%' substitutes
		while (index != -1){
            
            //Test for passed place holders within the id
            if (values.size() > pointer){
                text = text.substring(0,index) + values.get(pointer) + text.substring(index+2, text.length());  
            }
            
            pointer++;
            index = text.indexOf("%%");
        }
		
		return text;
	}
	
	/**
	 * Return a language key for the passed in <code>text</code>.<p>
	 *    
	 * @param String language text
	 * @return language key (or text if not found)
	 */
	public String getKey(String text){
		if (keyValues == null || text == null){
			return text;
		}
		
		Enumeration<String> keys = keyValues.keys();
		while (keys.hasMoreElements()){
			String key = keys.nextElement();
			if (keyValues.get(key).equalsIgnoreCase(text)){
				return key;
			}
		}
		return text;
	}

	/**
     * Return true if the passed in language key exists.<p>
     *    
     * @param String language key
     * @return true if exists
     */
    public boolean isKey(String key){
        if (keyValues == null || key == null){
            return false;
        }
        
        Enumeration<String> keys = keyValues.keys();
        while (keys.hasMoreElements()){
            String keyX = keys.nextElement();
            if (keyX.equals(key)){
                return true;
            }
        }
        return false;
    }


    public List<String> getMonths() {
        return months;
    }

    public List<String> getMonthsShort() {
        return monthsShort;
    }
	
    
	
}