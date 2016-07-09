package com.sevenorcas.openstyle.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.naming.InitialContext;
import javax.persistence.Table;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sevenorcas.openstyle.app.ApplicationI;
import com.sevenorcas.openstyle.app.app.anno.Dto;
import com.sevenorcas.openstyle.app.app.anno.EntityAttributes;
import com.sevenorcas.openstyle.app.app.entities.BaseDto;
import com.sevenorcas.openstyle.app.entity.BaseEntity;
import com.sevenorcas.openstyle.app.entity.CodeI;
import com.sevenorcas.openstyle.app.entity.IdI;
import com.sevenorcas.openstyle.app.app.entities.lang.LangCodeI;
import com.sevenorcas.openstyle.app.app.entities.lang.LangDescrI;
import com.sevenorcas.openstyle.app.AppException;
import com.sevenorcas.openstyle.app.perm.NoPermissionException;
import com.sevenorcas.openstyle.app.log.ApplicationLog;
import com.sevenorcas.openstyle.app.app.servercache.ServerCache;
import com.sevenorcas.openstyle.app.company.Company;
import com.sevenorcas.openstyle.app.entities.app.UserParam;
import com.sevenorcas.openstyle.app.service.app.LanguageServiceImp.Language;


/**
 * General utility methods.
 * 
 * [License]
 * @author John Stewart
 */
public class Utilities implements ApplicationI{

	/** Application Parameters               */ static private ApplicationParameters appParam     = ApplicationParameters.getInstance();
	/** Server object cache                  */ static private ServerCache cache                  = ServerCache.getInstance();
	/** BigDecimal value of 100              */ static private BigDecimal D100                    = new BigDecimal(100);
	/** Default date format                  */ static private SimpleDateFormat dateFormatDefault = new SimpleDateFormat(appParam.getDateFormatDefault());
	/** Default month part of date format    */ static private int dateFormatMonthDefault         = appParam.getDateFormatMonthDefault();
	/** Short date format                    */ static private SimpleDateFormat dateFormatShort   = new SimpleDateFormat(appParam.getDateFormatShort());
	/** Standard dto date format             */ static private SimpleDateFormat dateFormatDto     = new SimpleDateFormat(appParam.getDateFormatDto());	
	/** MD5 encoding object                  */ static private MessageDigest md                   = null;    
    
	/**
	 * Compare the passed in <code>Integers</code>. Accepts <code>null</code> parameters.<p>
	 * 
	 * Processing order:
	 * <ul>1. If both parameters are <code>null</code> then 0 is return</ul>
	 * <ul>2. If first parameter is <code>null</code> then -1 is return</ul>
	 * <ul>3. If second parameter is <code>null</code> then 1 is return</ul>
	 * <ul>4. Integer comparison is return</ul>
	 * @param Integer first parameter 
	 * @param Integer second parameter
	 * @return Integer comparison
	 */
	static public int compare (Integer x1, Integer x2){
		if (x1 == null && x2 == null){
			return 0;
		}
		if (x1 == null){
			return -1;
		}
		if (x2 == null){
			return 1;
		}
		return x1.compareTo(x2);
	}
	
	/**
     * Compare the passed in <code>IdI Object</code> with the passed in <code>Long</code>. Accepts <code>null</code> parameters.<p>
     * 
     * Processing order:
     * <ul>1. If both parameters are <code>null</code> (or the IdI.getId() is null) then 0 is return</ul>
     * <ul>2. If first parameter is <code>null</code> then -1 is return</ul>
     * <ul>3. If second parameter is <code>null</code> then 1 is return</ul>
     * <ul>4. Long comparison is return</ul>
     * @param Long first parameter 
     * @param Long second parameter
     * @return Long comparison
     */
    static public int compare (IdI x1, Long x2){
        if (x1 == null && x2 == null){
            return 0;
        }
        if (x1 == null || x1.getId() == null){
            return -1;
        }
        if (x2 == null){
            return 1;
        }
        return compare(x1.getId(), x2);
    }
    
	
	/**
     * Compare the passed in <code>Longs</code>. Accepts <code>null</code> parameters.<p>
     * 
     * Processing order:
     * <ul>1. If both parameters are <code>null</code> then 0 is return</ul>
     * <ul>2. If first parameter is <code>null</code> then -1 is return</ul>
     * <ul>3. If second parameter is <code>null</code> then 1 is return</ul>
     * <ul>4. Long comparison is return</ul>
     * @param Long first parameter 
     * @param Long second parameter
     * @return Long comparison
     */
    static public int compare (Long x1, Long x2){
        if (x1 == null && x2 == null){
            return 0;
        }
        if (x1 == null){
            return -1;
        }
        if (x2 == null){
            return 1;
        }
        return x1.compareTo(x2);
    }
	
    /**
     * Compare the passed in <code>Dates</code>. Accepts <code>null</code> parameters.<p>
     * 
     * Processing order:
     * <ul>1. If both parameters are <code>null</code> then 0 is return</ul>
     * <ul>2. If first parameter is <code>null</code> then -1 is return</ul>
     * <ul>3. If second parameter is <code>null</code> then 1 is return</ul>
     * <ul>4. Date comparison is return</ul>
     * @param Date first parameter 
     * @param Date second parameter
     * @return Date comparison
     */
    static public int compare (Date x1, Date x2){
        if (x1 == null && x2 == null){
            return 0;
        }
        if (x1 == null){
            return -1;
        }
        if (x2 == null){
            return 1;
        }
        return x1.compareTo(x2);
    }
    
	
	/**
     * Compare the passed in <code>Strings</code>. Accepts <code>null</code> parameters.<p>
     * 
     * Processing order:
     * <ul>1. If both parameters are <code>null</code> then 0 is return</ul>
     * <ul>2. If first parameter is <code>null</code> then -1 is return</ul>
     * <ul>3. If second parameter is <code>null</code> then 1 is return</ul>
     * <ul>4. Integer comparison is return</ul>
     * @param String first parameter 
     * @param String second parameter
     * @return Integer comparison
     */
    static public int compare (String x1, String x2){
        if (x1 == null && x2 == null){
            return 0;
        }
        if (x1 == null){
            return -1;
        }
        if (x2 == null){
            return 1;
        }
        return x1.compareTo(x2);
    }
	
    /**
     * Compare the passed in objects that implement the <code>IdI</code> interface. Accepts <code>null</code> parameters.<p>
     * 
     * Processing order:
     * <ul>1. If both parameters are <code>null</code> then 0 is return</ul>
     * <ul>2. If first parameter is <code>null</code> then -1 is return</ul>
     * <ul>3. If second parameter is <code>null</code> then 1 is return</ul>
     * <ul>4. ID comparison is return</ul>
     * @param IdI first parameter 
     * @param IdI second parameter
     * @return IdI comparison
     */
    static public int compare (IdI x1, IdI x2){
        if (x1 == null && x2 == null){
            return 0;
        }
        if (x1 == null){
            return -1;
        }
        if (x2 == null){
            return 1;
        }
        return x1.getId().compareTo(x2.getId());
    }
    
    
	/**
	 * Parse a <code>String</code> to a <code>Double</code>.<br>
	 * Replaces ',' characters with '.' (to account for German format).<p>
	 * 
	 * TODO: Replace logic with user locale settings.
	 * @param String value
	 * @return Double
	 * @exception Exception if parsing fails.
	 */
	static public Double parseDouble(String value) throws Exception{
		
		try{
			return Double.parseDouble(value);
		}
		catch (Exception e){}
		
		//German number format
		return Double.parseDouble(value.replace(",", "."));
	}
	
	
	/**
	 * Convert a <code>Double</code> to an <code>Integer</code>, multipled by 100.<p>
	 * 
	 * Processing:
	 * <ul>1. Double is rounded according to passed in round parameter</ul>
	 * <ul>2. Double is multipled by 100</ul>
	 * <ul>3. Integer part of Double is returned</ul><p>
	 * 
	 * If an exception occurs then <code>null</code> is returned.
	 * 
	 * @param Double value
	 * @param int round decimal places
	 * @return Integer (100 x Double value)
	 */
	static public Integer convertDoubleAsInteger100(Double d, int round){
		try {
			BigDecimal qty = new BigDecimal(roundDouble(d, round).toString());
			qty = qty.multiply(D100);
			return qty.intValue();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Return the passed in <code>Double</code> rounded according to the round parameter.
	 * @param Double value
	 * @param int round decimal places
	 * @return Rounded double
	 */
	static public Double roundDouble(Double d, int round){
		try {
			BigDecimal qty = new BigDecimal(d.toString());
			qty = qty.setScale(round, RoundingMode.CEILING);
			return qty.doubleValue();
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * Convert a <code>Integer</code> to a <code>Double</code>, divided by 100.<p>
	 * 
	 * If an exception occurs then <code>null</code> is returned.
	 * 
	 * @param Integer value
	 * @return Double (Integer value / 100)
	 */
	static public Double convertIntegerAsDouble100(Integer i){
		try {
			return new Double (i / 100);
		} catch (Exception e) {
			return null;
		}
	}
		
	/**
     * Format the passed in numbers to a string delimited by passed in value.<p>
     * E.g order number / position number as 123/1 
     * 
     * @param Integer first value
     * @param Integer second value
     * @param String delimiter
     * @param true = return 0 as a blank
     * @return String format
     */
	static public String format2NumbersAsString(Integer i1, Integer i2, String delimiter, boolean zeroAsBlank){
	    boolean b1 = i1 != null && (!zeroAsBlank || i1.intValue() > 0);
	    boolean b2 = i2 != null && (!zeroAsBlank || i2.intValue() > 0);
	    return  (b1? i1.toString() : "")
             +  (b2? (b1?delimiter:"") + i2.toString() : "");
    }
	
	/**
     * Format the passed in <code>Integer</code> value as:
     * <ul>- honoring the passed in <code>Locale</code></ul><p> 
     * 
     * If an exception occurs then <code>null</code> is returned.
     * 
     * @param Integer value
     * @param Locale format locale
     * @return String format
     */
    static public String format(Integer value, Locale locale){
        return format(value, locale, false);
    }
	
	/**
	 * Format the passed in <code>Integer</code> value as:
	 * <ul>- honoring the passed in <code>Locale</code></ul><p> 
	 * 
	 * If an exception occurs then <code>null</code> is returned.
	 * 
	 * @param Integer value
	 * @param Locale format locale
	 * @param true return zero's as blank
	 * @return String format
	 */
	static public String format(Integer value, Locale locale, boolean zeroAsBlank){
		try{
		    if (value.intValue() == 0 && zeroAsBlank){
		        return "";
		    }
		    
			NumberFormat formatter = locale != null? NumberFormat.getInstance(locale) : NumberFormat.getInstance();
			return formatter.format(value);
		} catch (Exception e){}
		
		return null;
	}
	
	/**
	 * Format the passed in <code>Double</code> value as a decimal format:
	 * <ul>- truncated according to passed in decimals parameter</ul>
	 * <ul>- honoring the passed in <code>Locale</code></ul><p> 
	 * 
	 * If an exception occurs then <code>null</code> is returned.
	 * 
	 * @param Double value
	 * @param Locale format locale
	 * @param decimal places
	 * @return String format
	 */
	static public String format(Double value, Locale locale, int decimals){
		try{
			NumberFormat formatter = locale != null? NumberFormat.getInstance(locale) : NumberFormat.getInstance();
			formatter.setMinimumFractionDigits(decimals);
			formatter.setMaximumFractionDigits(decimals);
			return formatter.format(value);
		} catch (Exception e){}
		
		return null;
	}
	
	
	/**
	 * Format the passed in value as a percentage according to:
	 * <ul>- truncated according to passed in format parameter</ul>
	 * <ul>- honoring the passed in <code>Locale</code></ul><p> 
	 * 
	 * If an exception occurs then <code>null</code> is returned.
	 * @param Double value
	 * @param Locale format locale
	 * @param String format string, eg "0.00%" will format with 2 decimal places and the % sign, "0" will format with 0 decimal places and no % sign
	 * @return String format
	 */
	static public String formatPercent (Double value, Locale locale, String format){
		try{
			int indexP = format.indexOf("%");
			boolean p = indexP != -1; 
			
			int indexD = format.indexOf(".");
			boolean d = indexD != -1; 
			
			String x = indexD != -1? format.substring(indexD + 1, (indexP != -1? indexP : format.length())) : "";
			int numberD = x.length();
			
			if (d){
				NumberFormat formatter = locale != null? NumberFormat.getInstance(locale) : NumberFormat.getInstance();
				formatter.setMinimumFractionDigits(numberD);
				formatter.setMaximumFractionDigits(numberD);
				return formatter.format(value) + (p?"%":"");
			}
			return value.intValue() + (p?"%":"");
			
		} catch (Exception e){}
		
		return null;
	}
	
	/**
	 * Format the passed in value as a currency according to the currency format and honoring the passed in <code>Locale</code> 
	 * @param Double value
	 * @param Locale format locale
	 * @return String format
	 */
	static public String formatCurrency (Double value, Locale locale){
		try{
			NumberFormat formatter = locale != null? NumberFormat.getInstance(locale) : NumberFormat.getInstance();
			formatter.setMinimumFractionDigits(2);
			formatter.setMaximumFractionDigits(2);
			return formatter.format(value);
		} catch (Exception e){}
		
		return null;
	}
	
	
	/**
	 * Format the passed in date using the default format
	 * @param Date to format
	 * @param Short month codes
	 * @return formatted date
	 */
	static public String formatDate(Date date, List<String> months){
	    String d = date != null? dateFormatDefault.format(date) : "";
	    if (d.length() > 0 && months != null && dateFormatMonthDefault == 3){
	        d = d.substring(0, 3) + getMonthCode(date, months) + d.substring(6);
	    }
	    return d;
	}

	
	/**
	 * Return the short week number for the passed in date.<p>
	 * 
	 * Note uses ISO first day of week is Monday.<p>
	 * 
	 * @param Date to format
	 * @return week number
	 */
	static public int formatWeekNrShort(Date date){
		if (date == null){
			return -1;
		}
		DateTime dt = new DateTime(date);
    	return dt.getWeekOfWeekyear();
	}

	/**
	 * Return the long week number (includes year) for the passed in date.<p>
	 * 
	 * Note uses ISO first day of week is Monday.<p>
	 *  
	 * @param Date to format
	 * @return week number
	 */
	static public int formatWeekNrLong(Date date){
		if (date == null){
			return -1;
		}
		int y = getYearLast2Digits(date);
    	y *= 100;
    	y += formatWeekNrShort(date);
    	return y;
	}
	

	
	/**
     * Format the passed in string date using the default format<p>
     * 
     * Thanks to http://www.mkyong.com/java/how-to-convert-string-to-date-java/
     * 
     * @param String to Date
     * @return formatted date
     */
    static public Date formatDate(String date) throws Exception{
        return date != null? dateFormatDefault.parse(date) : null;
    }
	
    /**
     * Format the passed in string date using the default format<p>
     * 
     * Thanks to http://www.mkyong.com/java/how-to-convert-string-to-date-java/
     * 
     * @param String to Date
     * @param String date format
     * @return formatted date
     */
    static public Date formatDate(String date, String format) throws Exception{
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return date != null? dateFormat.parse(date) : null;
    }
    
	/**
	 * Format the passed in date using the short format
	 * @param Date to format
	 * @param Short month codes
	 * @return formatted date
	 */
	static public String formatDateShort(Date date, List<String> months){
		String d = date != null? dateFormatShort.format(date) : "";
        if (d.length() > 0){
            d = d.substring(0, 3) + getMonthCode(date, months);
        }
        return d;
	}
	
	/**
     * Format the passed in date using the default format
     * @param Date to format
     * @param Short month codes
     * @return formatted date
     */
    static public String getMonthCode(Date date, List<String> months){
        if (date != null){
            int m = getMonthNumber(date);
            return months.get(m);
        }
        return null;
    }
	
	/**
	 * Format the passed in date using the passed in format
	 * @param Date to format
	 * @param String format string
	 * @return formatted date
	 */
	static public String formatDate(Date date, String format){
		DateFormat df = new SimpleDateFormat(format);
		return date != null? df.format(date) : "";
	}
	
	
	/**
	 * Format the passed in date for SQL
	 * @param Date to format
	 * @return formatted date
	 */
	static public String formatSqlDate(Date date){
	    if (date == null){
	        return "NULL";
	    }
	    
		Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    int year = cal.get(Calendar.YEAR);
	    int month = cal.get(Calendar.MONTH) + 1;
	    int day = cal.get(Calendar.DAY_OF_MONTH);
	    return "'" 
	        + year
	        + "-"
	        + (month < 10? "0" + month : month)
			+ "-"
			+ (day < 10? "0" + day : day)
			+ "'";
	    
	}
	
	/**
	 * Format the passed in boolean for SQL
	 * @param Boolean to format
	 * @return formatted sql
	 */
	static public String formatSqlBoolean(Boolean b){
	    if (b == null){
	        return "NULL";
	    }
	    return b?"TRUE":"FALSE";
	}
	
	/**
	 * Create todays date (with no time data)   
	 * @return date
	 */
	static public Date today(){
	    return dateClean(new Date());
	}	

	/**
	 * Clean the passed in date (with no time data)   
	 * @return date
	 */
	static public Date dateClean(Date date){
	    if (date == null){
	        return date;
	    }
		Calendar calendar = calendarClean(date);	    
	    return calendar.getTime();
	}	
	
	/**
	 * Clean the passed in date (with no time data)   
	 * @return Calendar
	 */
	static public Calendar calendarClean(Date date){
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    calendar.setTimeZone(TimeZone.getDefault());
	    return calendar;
	}	
	
	
	/**
	 * Create todays date (with hour an minutes)
	 * @param int hour
	 * @param int minute
	 * @return date
	 */
	static public Date today(int hour, int minute){
		Calendar calendar = Calendar.getInstance(); //IsprofiUtilites.locale);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);

	    calendar.setTimeZone(TimeZone.getDefault());
	    
	    return calendar.getTime();
	}	
	
	/**
	 * Add the passed in days to the passed in date    
	 * @return date
	 */
	static public Date addDaysToDate(Date d, int days){
		Calendar c = calendarClean(d);
		c.add(Calendar.DAY_OF_YEAR, days);
		return c.getTime();
	}	
	
	/**
	 * Add the passed in weeks to the passed in date    
	 * @return date
	 */
	static public Date addWeeksToDate(Date d, int weeks){
		Calendar c = calendarClean(d);
		c.add(Calendar.WEEK_OF_YEAR, weeks);
		return c.getTime();
	}	
	
	/**
     * Adjust the passed in date by the passed in time zone hours    
     * @return date
     */
    static public Date adjustTimeZone(Date d, int hours){
        Calendar c = Calendar.getInstance(); 
        c.setTime(d);
        c.setTimeZone(TimeZone.getDefault());
        c.add(Calendar.HOUR_OF_DAY, hours);
        return c.getTime();
    }
	
	/**
	 * Add the passed in months to the passed in date    
	 * @return date
	 */
	static public Date addMonthsToDate(Date d, int months){
		Calendar c = calendarClean(d);
		c.add(Calendar.MONTH, months);
		return c.getTime();
	}	
	
	/**
	 * Return the day of the week for the passed in date    
	 * @return date
	 */
	static public int getDayNumber(Date d){
		Calendar c = calendarClean(d);
		return c.get(Calendar.DAY_OF_WEEK);
	}	
	
	/**
     * Return the month number for the passed in date    
     * @return date
     */
    static public int getMonthNumber(Date d){
        Calendar c = calendarClean(d);
        return c.get(Calendar.MONTH);
    }   
	
	/**
	 * Return the week number for the passed in date    
	 * @return date
	 */
	static public int getWeekNumber(Date d){
		Calendar c = calendarClean(d);
		return c.get(Calendar.WEEK_OF_YEAR);
	}	
	
	
	/**
	 * Return the year for the passed in date    
	 * @return date
	 */
	static public int getYear(Date d){
		Calendar c = calendarClean(d);
		return c.get(Calendar.YEAR);
	}	
	
	/**
	 * Return the last 2 year digits of the passed in date.<p>
	 * 
	 * @param Date to format
	 * @return last 2 digits
	 */
	static public int getYearLast2Digits(Date date){
		if (date == null){
			return -1;
		}
		Calendar c = calendarClean(date);
    	int y = c.get(Calendar.YEAR);
    	y -=  (y / 100) * 100;
    	return y;
	}
	
	/**
	 * Return LangKey for the passed in Date day
	 * @param Date 
	 * @return
	 */
	static public String getLangKeyDay(Date d){
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(d);
		return getLangKeyDay(calendar.get(Calendar.DAY_OF_WEEK));
	}
	
	/**
	 * Return LangKey for the passed in Calendar day
	 * @param int day field from java.util.Calendar 
	 * @return
	 */
	static public String getLangKeyDay(int i){
		switch(i){
			case Calendar.SUNDAY:   return "Sunday";
			case Calendar.MONDAY:   return "Monday";
			case Calendar.TUESDAY:  return "Tuesday";
			case Calendar.WEDNESDAY:return "Wednesday";
			case Calendar.THURSDAY: return "Thursday";
			case Calendar.FRIDAY:   return "Friday";
			case Calendar.SATURDAY: return "Saturday";
			default : return "???";
		}
	}
	
	/**
	 * Return LangKey for the passed in Date month
	 * @param Date 
	 * @return
	 */
	static public String getLangKeyMonth(Date d){
		return getLangKeyMonth(d, false);
	}
	
	/**
	 * Return LangKey for the passed in Date month
	 * @param Date 
	 * @param true == short label
	 * @return
	 */
	static public String getLangKeyMonth(Date d, boolean shortLabel){
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(d);
		return getLangKeyMonth(calendar.get(Calendar.MONTH))+ (shortLabel?"S":"");
	}
	
	/**
	 * Return LangKey for the passed in Calendar month
	 * @param int month field from java.util.Calendar 
	 * @return
	 */
	static public String getLangKeyMonth(int i){
		switch(i){
			case Calendar.JANUARY:  return "Month1";
			case Calendar.FEBRUARY: return "Month2";
			case Calendar.MARCH:    return "Month3";
			case Calendar.APRIL:    return "Month4";
			case Calendar.MAY:      return "Month5";
			case Calendar.JUNE:     return "Month6";
			case Calendar.JULY:     return "Month7";
			case Calendar.AUGUST:   return "Month8";
			case Calendar.SEPTEMBER:return "Month9";
			case Calendar.OCTOBER:  return "Month10";
			case Calendar.NOVEMBER: return "Month11";
			case Calendar.DECEMBER: return "Month12";
			default : return "???";
		}
	}
	
	
	
	/**
	 * Compare the passed in <code>Dates</code> are in order.<p>
	 * 
	 * Processing order:
	 * <ul>1. If both parameters are <code>null</code> then <code>TRUE</code> return</ul>
	 * <ul>2. If either parameter is <code>null</code> then <code>TRUE</code> is return</ul>
	 * <ul>3. Date comparison is returned for first parameter is before second parameter</ul>
	 * @param Date first parameter 
	 * @param Date second parameter
	 * @return boolean 
	 */
	static public boolean validFromTo (Date x1, Date x2){
		if (x1 == null && x2 == null){
			return true;
		}
		if (x1 == null || x2 == null){
			return true;
		}
		return x1.before(x2);
	}

	
	/**
	 * Find an annotation within the passed in field list
	 * @param Class annotation
	 * @param List of fields
	 * @return java.lang.reflect.Field (or null if not found)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static public java.lang.reflect.Field findFieldAnnontation (Class annotation, List<java.lang.reflect.Field> fields){
		if (fields == null){
			return null;
		}
		for (java.lang.reflect.Field field: fields) {
			Object a = field.getAnnotation(annotation);
			if (a == null){
				return field;
			}					
		}
		return null;
	}
	
	/**
	 * Validate if the user has permission to change the passed in <code>BaseEntity</code>.<p>
	 * This test will test if entity has a service id (below ApplicationI.ENTITY_PERMANENT_ID) and that the user has a service status
	 * @param UserParam object
	 * @param Object to test
	 * @return true = permission, false = no permission
	 */
	static public boolean validateServiceUser (UserParam params, Object obj) {
		if (obj instanceof BaseEntity){
			BaseEntity entity = (BaseEntity)obj; 
			if (entity != null 
					&& entity.getId() != null
					&& entity.getId().longValue() < ENTITY_PERMANENT_ID
					&& (params == null || !params.isService())){
				return false;
			}
		}
		return true;
	}

	/**
	 * Create object from passed in class name. <b>This</b> method will use the following process to attempt to find the class:<ol>
	 *     <li>Assume the passed in class name is fully qualified and apply <code>Class.forName(classname)</code></li>
	 *     <li>Prefix <code>ApplicationI.ENTITY_BASE_PACKAGE</code> and apply</li>
	 * </ol>
	 * @param String class name
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	static public Class findClass(String classname){
		return findClass(classname, null);	
	}

	/**
	 * Create object from passed in class name. <b>This</b> method will use process to attempt to find the class:<ol>
	 *     <li>Assume the passed in class name is an inner class within the same passed in referring object clazz (if not null)</li>
	 *     <li>Assume the passed in class name is in the same package / sub-package as the passed in referring object clazz (if not null)</li>
	 *     <li>Assume the passed in class name is fully qualified and apply <code>Class.forName(classname)</code></li>
	 *     <li>Prefix <code>ApplicationI.ENTITY_BASE_PACKAGE</code> and apply</li>
	 * </ol>
	 * @param String class name
	 * @param Class of referring object
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	static public Class findClass(String classname, Class clazz){
			
		if (clazz != null){
			String pack = clazz.getCanonicalName();
			int index = pack.lastIndexOf("$");
			
			if (index != -1){ 
				pack = pack.substring(0, index);
			}
			else {
				index = pack.lastIndexOf(".");
				pack = index != -1? pack.substring(0, index) : pack;
			}

			try{
			    return Class.forName(pack + "$" + classname);
			} catch (Exception e) {}
			
			try{
				return Class.forName(pack + "." + classname);
			} catch (Exception e) {}
		}
		
		
		try{
		    return Class.forName(classname);
		} catch (Exception e) {}
		
		try{
		    return Class.forName(ENTITY_BASE_PACKAGE + classname);
		} catch (ClassNotFoundException e) {}
		
		try{
		    return Class.forName(APP_ENTITY_BASE_PACKAGE + classname);
		} catch (ClassNotFoundException e) {}
		
		try{
		    return Class.forName(DOMAIN_NAME + "." + classname);
		} catch (ClassNotFoundException e) {
			AppException a = new AppException("Not found '" + classname + "'");
			a.logThisException();
			a.emailThisException();
			ApplicationLog.error(a);
		}
		
		return null;
	}

	
	/**
	 * Create object using empty constructor to get defaults 
	 * @param clazz
	 * @param UserParam passed to constructor (can be null)
	 * @param Company object passed to constructor (can be null)  
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static public Object create(Class clazz, UserParam params, Company company){
		try {
			Object obj = null;
			
			try {
				Constructor constructor = clazz.getConstructor(UserParam.class);
				obj = constructor.newInstance(params);
			} catch (Exception e) {
				//try null constructor  
				Constructor constructor = clazz.getConstructor();
				obj = constructor.newInstance();
			}		
			
			//Look for initialization method
			for (Method method: clazz.getDeclaredMethods()) {
				Dto m = method.getAnnotation(Dto.class);
				if (m != null && m.init()){
					
				    //User params constructor
				    try{
						method.invoke(obj, params);
						return obj;
					} catch (Exception e) {}
					
				    //Company constructor
                    try{
                        method.invoke(obj, company);
                        return obj;
                    } catch (Exception e) {}
				    
					//Empty constructor
					try{
					    method.invoke(obj);
                        return obj;
                    } catch (Exception e) {}
				}
			}
			return obj;
			
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * Find a method along an object scope.
	 * @param Class entity
	 * @param String method name
	 * @return java.lang.reflect.Method
	 */
	@SuppressWarnings("rawtypes")
	static public java.lang.reflect.Method findMethod(Class entityClazz, String method){
		//Find permission key entity
	    try {
			for (java.lang.reflect.Method m: entityClazz.getDeclaredMethods()) {
			    m.setAccessible(true);
				String name = m.getName();
			    
			    if (name.equals(method)){
			    	return m;
			    }
			}				
	    } catch (Exception e) {}
		
	    //Get superclass definitions		
		if (entityClazz.getSuperclass() != null){
			return findMethod (entityClazz.getSuperclass(), method);
		}
		return null;
		
	}

	
    /**
	 * Return a string value for the passed in xml filename and xml element name<p>
	 * 
	 * Thanks to http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
	 * 
	 * @param InputStream xml file
	 * @param String xml element. Can be a '.' separated value to indicate a path to the element.
	 * @return String value.
	 * @throws Exception if not found
	 */
	static public String getXmlTextContent (InputStream is, String element) throws Exception{
		
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
		 
			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
		 
			String [] s = element.split("\\.");
			
			//Get first node
			NodeList nList = doc.getElementsByTagName(s[0]);
			Node node = nList.item(0);
			
			//work along graph(if given) to get end node
			for (int i=1; i<s.length; i++) {
				nList = node.getChildNodes();
				
				for (int j=0; j<nList.getLength(); j++) {
					node = nList.item(j);
					
					if (node.getNodeName().equals(s[i])){
						break;
					}
				}
			}
			
			if (node.getNodeName().equals(s[s.length-1])){
				return node.getTextContent();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		throw new Exception();
	}

	
	/**
	 * Lookup a bean via the passed in interface and class name
	 * @param T service
	 * @param String service name
	 * @return looked up service or null if not found
	 */
    @SuppressWarnings("unchecked")
	static public <T> T lookupService(T service, String name) {
    	if (service == null) {
			try {
				InitialContext ctx = new InitialContext();
				service = (T)ctx.lookup(NAME_CONTEXT_LOOKUP + name);
			} catch (Exception e) {
			    if (name != null && name.indexOf("LoginServiceImp") != -1){
			        //do nothing
			    }
			    else{
			        ApplicationLog.error(e);
			    }
			}
		}
		return service;
	}
	
	
    
    /**
	 * Update the passed in entity with the passed in dto.<p>
	 * 
	 * Notes:<ul>
	 *     <li>reflection is used to match fields between both objects<li>
	 *     <li>fields in <code>Dto</code> object do not include any fields from <code>super</code> objects</li>
	 *     <li><code>List</code> fields are not updated</li>
	 * </ul> 
	 *  
	 * @param UserParam object 
	 * @param Object dto
	 * @param BaseEntity entity
	 * @throws NoPermissionException
	 */
	static public void setEntityFields (UserParam params, BaseDto dto, BaseEntity entity) throws AppException{
		
		if (entity == null){
			return;
		}
		
		if (dto.isDelete()){
			if (!Utilities.validateServiceUser(params, entity)){
				throw new NoPermissionException("setEntityFields on " + entity.getClass().getName());
			}
			
			entity.setDelete();
			return;
		}
		
		Hashtable<String, java.lang.reflect.Field> fieldsDto    = cache.getFields(dto.getClass(), false);
		Hashtable<String, java.lang.reflect.Field> fieldsEntity = cache.getFields(entity.getClass(), true);
		
		//Update entity
		Enumeration<String> keys = fieldsDto.keys();
		while (keys.hasMoreElements()){
			String fieldname = keys.nextElement();
			
			if (fieldsEntity.containsKey(fieldname)){
				
				try {
					java.lang.reflect.Field field = fieldsEntity.get(fieldname);
					Object obj = fieldsDto.get(fieldname).get(dto);
					
					if (obj instanceof List){
						continue;
					}
					
					Object objE = field.get(entity);
					
					if (obj == null && objE == null){
						continue;
					}
					else if (obj == null || objE == null){
						//update
					}
					else{
						
						String s1 = null;
						String s2 = null;
						
						//Date fields: special case
						if (obj instanceof Date){
							s1 = dateFormatDto.format(obj);
							s2 = dateFormatDto.format(objE);
						}
						else{
							s1 = obj.toString();
							s2 = objE.toString();
						}
						
						
						if (s1.equals(s2)){
							continue;
						}
					}
					
					entity.setChanged();
					field.set(entity, obj);
					
					if (dto instanceof BaseDto){
						entity.setId_dto(((BaseDto)dto).getId());
					}
				
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}
		
		if (entity.isChanged() && !Utilities.validateServiceUser(params, entity)){
			throw new NoPermissionException("setEntityFields on " + entity.getClass().getName());
		}
		
	}

	/**
	 * Update the passed in to object with the passed in from object.<p>
	 * 
	 * Notes:<ul>
	 *     <li>reflection is used to match fields between both objects<li>
	 *     <li><code>List</code> fields are not updated</li>
	 * </ul>
	 *  
	 * @param Object from 
	 * @param Object to 
	 */
	static public void setFields (Object from, Object to){
		
		if (from == null){
			return;
		}
		
		Hashtable<String, java.lang.reflect.Field> fieldsFrom = cache.getFields(from.getClass(), true);
		Hashtable<String, java.lang.reflect.Field> fieldsTo   = cache.getFields(to.getClass(), true);
		
		//Update entity
		Enumeration<String> keys = fieldsTo.keys();
		while (keys.hasMoreElements()){
			String fieldname = keys.nextElement();
			
			if (fieldsFrom.containsKey(fieldname)){
				try {
					Object value = fieldsFrom.get(fieldname).get(from);
					if (value != null 
							&& !(value instanceof List)){
						fieldsTo.get(fieldname).set(to, value);
					}
				
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}
	}

	
	/**
	 * Iterate through the passed in entity graph and set standard fields, i.e.<ul>
	 *     <li><code>comp_nr</code> if new record</li>
	 *     <li><code>active</code> = true if new record</li>
	 *     <li><code>create</code> and <code>create_id</code> if new record</li> 
	 *     <li><code>update</code> and <code>update_id</code> if editing record</li>
	 * </ul><p>
	 *  
	 * @param UserParam object
	 * @param Entity T extends BaseEntity
	 */
	@SuppressWarnings("rawtypes")
	static public void setStandardFields(UserParam params, Object ent){
		
		BaseEntity entity = null;
		if (ent instanceof BaseEntity){
			entity = (BaseEntity)ent;
		}
		else{
			return;
		}
		
		//is new record
		if (entity.getId() == null || entity.getId().longValue() < 0){
			if (entity.getCompanyNr() == null){
				entity.setCompanyNr(params.getCompany());
			}
			if (entity.getActive() == null){
				entity.setActive(true);
			}
			if (entity.getCreated() == null){
				entity.setCreated(new Date());
			}
			if (entity.getCreatedId() == null){
				entity.setCreatedId(params.getUser_id());
			}
		}
		//must be an update
		else if (entity.isChanged()){
			entity.setUpdated(new Date());
			entity.setUpdatedId(params.getUser_id());
		}
		
		List<java.lang.reflect.Field> fields  = cache.getFieldList(ent.getClass(), true);
		
		//Iterate fields looking for lists
		for (java.lang.reflect.Field field: fields) {
			try {
				Object obj = field.get(entity);
				if (obj instanceof List){
					List l = (List)obj;
					for (Object x: l){
						setStandardFields(params, x);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}		
	}

	
	/**
     * Encode the passed in hashtable of parameters
     * @param Hashtable parameter lists
     * @return String encoded string
     */
    static public String toParameterEncode2(Hashtable<String, String> e){
        StringBuffer sb = new StringBuffer();
        Enumeration<String>keys = e.keys();
        int i=0;
        while (keys.hasMoreElements()){
            String k=keys.nextElement();
            sb.append((i++>0?ENCODE_DELIMITER_1:"") + k + ENCODE_DELIMITER_2 + e.get(k));
        }
        return sb.toString();
    }
	
	/**
	 * Encode the passed in parameters list
	 * @param List parameter list
	 * @return String encoded string
	 */
	static public String toParameterEncode(ArrayList<String> params){
		return toParameterEncode(params, ENCODE_DELIMITER_1);
	}
	
	/**
	 * Encode the passed in parameters list
	 * @param List parameter list
	 * @param String delimiter 1
	 * @return String encoded string
	 */
	static public String toParameterEncode(ArrayList<String> params, String delimiter){
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<params.size(); i++){
			sb.append((i>0?delimiter:"") + params.get(i));
		}
		return sb.toString();
	}
	
	/**
	 * Add the passed in value to the passed parameter list
	 * @param List parameter list
	 * @param String parameter name
	 * @param String value
	 */
	static public void toParameter(ArrayList<String> params, String param, Object value){
		toParameter(params, param, value, ENCODE_DELIMITER_2);
	}
	
	/**
	 * Add the passed in value to the passed parameter list
	 * @param List parameter list
	 * @param String parameter name
	 * @param String value
	 * @param String delimiter 
	 */
	static public void toParameter(ArrayList<String> params, String param, Object value, String delimiter){
		if (value != null){
			params.add(param + delimiter + value.toString());
		}
	}
	
	/**
	 * Add the passed in double to the passed parameter list
	 * @param List parameter list
	 * @param String parameter name
	 * @param Double value
	 * @param int number of decimals to encode
	 */
	static public void toParameter(ArrayList<String> params, String param, Double value, int decimals){
		if (value != null){
			
			if (decimals != -1){
				NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH); //force '.'
				formatter.setMinimumFractionDigits(decimals);
				formatter.setMaximumFractionDigits(decimals);
				String s = formatter.format(value);
				s = s.replace(ENCODE_DELIMITER_1, "");
				params.add(param + ENCODE_DELIMITER_2 + s);
				return;
			}
			
			params.add(param + ENCODE_DELIMITER_2 + value.toString());
		}
	}
	
	/**
	 * Add the passed in date to parameter list
	 * @param List parameter list
	 * @param String parameter name
	 * @param Date value
	 */
	static public void toParameter(ArrayList<String> params, String param, Date value){
		toParameter(params, param, value, ENCODE_DELIMITER_2);
	}
	
	/**
	 * Add the passed in date to parameter list
	 * @param List parameter list
	 * @param String parameter name
	 * @param Date value
	 * @param String delimiter
	 */
	static public void toParameter(ArrayList<String> params, String param, Date value, String delimiter){
		if (value != null){
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			params.add(param + delimiter + df.format(value));
		}
	}
	
	/**
	 * Add the passed in boolean to parameter list
	 * @param List parameter list
	 * @param String parameter name
	 * @param Boolean value
	 */
	static public void toParameter(ArrayList<String> params, String param, Boolean value){
		toParameter(params, param, value, ENCODE_DELIMITER_2);
	}
	
	/**
	 * Add the passed in boolean to parameter list
	 * @param List parameter list
	 * @param String parameter name
	 * @param Boolean value
	 * @param String delimiter
	 */
	static public void toParameter(ArrayList<String> params, String param, Boolean value, String delimiter){
		if (value != null){
			params.add(param + delimiter + (value?"t":"f"));
		}
	}
	
	/**
     * Set the passed in encoded string to be stored within another encoded string (ie a second level).
     * @param List parameter list
     * @param String parameter name
     * @param String encoded string
     */
    static public void encodeLevel2(ArrayList<String> params, String param, String value){
        if (value != null){
            value = encodeLevel2(value);
            params.add(param + ENCODE_DELIMITER_2 + value);
        }
    }
    
    /**
     * Set the passed in encoded string to be stored within another encoded string (ie a second level).
     * @param String 1st level encoded string
     * @return 2nd level encoded string
     */
    static public String encodeLevel2(String value){
        if (value != null){
            return value.replace(ENCODE_DELIMITER_1, ENCODE_LEVEL2_DELIMITER_1);
        }
        return value;
    }
    
    /**
     * Reset the passed in second level encoded string.
     * @param String 2nd level encoded string
     * @return 1st level encoded string   
     */
    static public String decodeLevel2(String value){
        if (value != null){
            return value.replace(ENCODE_LEVEL2_DELIMITER_1, ENCODE_DELIMITER_1);
        }
        return value;
    }
    
	
	/**
	 * Decode the passed in parameter config string.<p>
	 * 
	 * Note, the config string must be properly formed otherwise some or all of 
	 * the parameters are ignored.
	 * 
	 * @param String encoded string
	 * @return Hashtable<String, String>
	 */
	static public Hashtable<String, String> fromParameterEncode(String config){
		return fromParameterEncode(config, ENCODE_DELIMITER_1, ENCODE_DELIMITER_2);
	}
	
	/**
	 * Decode the passed in parameter config string.<p>
	 * 
	 * Note, the config string must be properly formed otherwise some or all of 
	 * the parameters are ignored.
	 * 
	 * @param String encoded string
	 * @param String delimiter 1
	 * @param String delimiter 2
	 * @return Hashtable<String, String>
	 */
	static public Hashtable<String, String> fromParameterEncode(String config, String delimiter1, String delimiter2){
		Hashtable<String, String> encodes = new Hashtable<>();

		if (config == null){
			return encodes;
		}
		
		String [] s1 = config.split(delimiter1);
		for (String s: s1){
			try{
			    int i = s.indexOf(delimiter2);
				String param = i!=-1? s.substring(0, i) : s;
				String value = i!=-1? s.substring(i+delimiter2.length()) : null;
				encodes.put(param, value);
			} catch (Exception e){}
		}
		return encodes;
	}

	
	/**
	 * Use the passed in <code>String</code> configuration to fill the passed entities fields.
	 * @param Hashtable parameters
	 * @param String parameter name
	 * @param String default value (if parameter not found)
	 * @return value
	 */
	static public String fromParameter(Hashtable<String, String> params, String param, String defaultValue){
		if (!params.containsKey(param)){
			return defaultValue;
		}
		try{
			return params.get(param);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Use the passed in parameters to return the passed in parameter name value
	 * @param Hashtable parameters
	 * @param String parameter name
	 * @param Integer default value (if parameter not found)
	 * @return value
	 */
	static public Integer fromParameter(Hashtable<String, String> params, String param, Integer defaultValue){
		if (!params.containsKey(param)){
			return defaultValue;
		}
		try{
			return Integer.parseInt(params.get(param));
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
     * Use the passed in parameters to return the passed in parameter name encoded (to second level) string.
     * @param Hashtable parameters
     * @param String parameter name
     * @param String default value (if parameter not found)
     * @return value
     */
    static public String fromParameterLevel2(Hashtable<String, String> params, String param, String defaultValue){
        if (!params.containsKey(param)){
            return defaultValue;
        }
        String e = params.get(param);
        e = e.replace(ENCODE_LEVEL2_DELIMITER_1, ENCODE_DELIMITER_1);
        return e;
    }
	
	
	/**
	 * Use the passed in parameters to return the passed in parameter name value
	 * @param Hashtable parameters
	 * @param String parameter name
	 * @param Long default value (if parameter not found)
	 * @return value
	 */
	static public Long fromParameter(Hashtable<String, String> params, String param, Long defaultValue){
		if (!params.containsKey(param)){
			return defaultValue;
		}
		try{
			return Long.parseLong(params.get(param));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Use the passed in parameters to return the passed in parameter name value
	 * @param Hashtable parameters
	 * @param String parameter name
	 * @param Double default value (if parameter not found)
	 * @return value
	 */
	static public Double fromParameter(Hashtable<String, String> params, String param, Double defaultValue){
		if (!params.containsKey(param)){
			return defaultValue;
		}
		try{
			return Double.parseDouble(params.get(param));
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Use the passed in parameters to return the passed in parameter name value
	 * @param Hashtable parameters
	 * @param String parameter name
	 * @param Boolean default value (if parameter not found)
	 * @return value
	 */
	static public Boolean fromParameter(Hashtable<String, String> params, String param, Boolean defaultValue){
		if (!params.containsKey(param)){
			return defaultValue;
		}
		try{
			return params.get(param).equals("t")? true : false;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	
	/**
	 * Use the passed in parameters to return the passed in parameter name value
	 * @param Hashtable parameters
	 * @param String parameter name
	 * @param Date default value (if parameter not found)
	 * @return value
	 */
	static public Date fromParameter(Hashtable<String, String> params, String param, Date defaultValue) throws Exception{
		if (!params.containsKey(param)){
			return defaultValue;
		}
		try{
			return new SimpleDateFormat("dd/MM/yyyy").parse(params.get(param)); 
		} catch (Exception e) {
			return defaultValue;
		}
	}

	
	/**
	 * Return the annotation <code>@javax.persistence.Table</code> element <code>name</code> if it exists (and is not empty). 
	 * Otherwise return the simple class name.<p>
	 * 
	 * This method uses reflection which is quite heavy. Therefore lookups are cached for performance.
	 * 
	 * @param Class of java object
	 * @return Full table name with schema
	 */
	@SuppressWarnings("rawtypes")
	static public String tableName(Class clazz){
		
		String name = cache.getTableName(clazz);
		if (name != null){
			return name;
		}
		name = tableNameX(clazz);
		if (name == null){
			name = clazz.getSimpleName();
		}
		cache.putTableName(clazz, name);
		
		return name;
	}
	
	
	/**
	 * Return the annotation <code>@javax.persistence.Table</code> element <code>name</code> if it exists (and is not empty). 
	 * Otherwise return the simple class name.<p>
	 * 
	 * This method uses reflection which is quite heavy. Therefore lookups are cached for performance.
	 * 
	 * @param Class of java object
	 * @return Full table name with schema
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static private String tableNameX(Class clazz){
	
		Table t = (Table)clazz.getAnnotation(Table.class);
		if (t != null){
			return (t.schema().length() > 0? t.schema() + "." : "") 
					+ (t.name().length() > 0? t.name() : clazz.getSimpleName()); 
		}
		
		if (clazz.getSuperclass() != null){
			return tableName(clazz.getSuperclass());
		}
		
		return null;
	}
	
	/**
     * Return the annotation <code>@EntityAttributes</code> element <code>langKey</code> if it exists (and is not empty). 
     * Otherwise return the table name.<p>
     * 
     * This method uses reflection which is quite heavy. Therefore lookups are cached for performance.
     * 
     * @param Class of java object
     * @return Full table name with schema
     */
    @SuppressWarnings("rawtypes")
    static public String tableLangKey(Class clazz){
        String key = cache.getTableLangKey(clazz);
        if (key != null){
            return key;
        }
        key = getTableLangKeyX(clazz);
        if (key == null){
            key = tableName(clazz);
        }
        cache.putTableLangKey(clazz, key);
        return key;
    }
	
    
    /**
     * Return the annotation <code>@EntityAttributes</code> element <code>langKey</code> if it exists (and is not empty). 
     * 
     * This method uses reflection which is quite heavy. Therefore lookups are cached for performance.
     * 
     * @param Class of java object
     * @return Full table name with schema
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    static private String getTableLangKeyX(Class clazz){
        EntityAttributes t = (EntityAttributes)clazz.getAnnotation(EntityAttributes.class);
        return t != null && !t.refLangKey().isEmpty()? t.refLangKey() : null;
    }

	/**
	 * Find object in list by its code (list objects must implement CodeI)
	 * @param list
	 * @param code
	 * @return object if found
	 * @throws Exception 
	 */
	static public <T> T getObjectByCode(List<T> list, String code) throws Exception{
		if (list == null || list.size() == 0 || code == null){
			return null;
		}
		
		if (!(list.get(0) instanceof CodeI)){
			throw new Exception("Invalid list object. Must implement CodeI.");
		}
		
		for (T obj: list){
			if (((CodeI)obj).getCode().equalsIgnoreCase(code)){
				return obj;
			}
		}
		return null;
	}
	

	/**
	 * Find object in list by its id (list objects must implement IdI)
	 * @param list
	 * @param code
	 * @return object if found
	 * @throws Exception 
	 */
	static public <T> T getObjectById(List<T> list, Long id) throws Exception{
		if (list == null || list.size() == 0 || id == null){
			return null;
		}
		
		if (!(list.get(0) instanceof IdI)){
			throw new Exception("Invalid list object. Must implement IdI.");
		}
		
		for (T obj: list){
			if (((IdI)obj).getId().longValue() == id){
				return obj;
			}
		}
		return null;
	}


	/**
	 * Set transient language code value
	 * @param LangCode object
	 * @param Language file
	 */
	static public void setLangCode(LangCodeI c, Language l){
		if (c != null){
			c.setCode(l.getLabel(c.getCodeLangkey()));
		}
	}	
	
	/**
	 * Set transient language description value
	 * @param LangDescr object
	 * @param Language file
	 */
	static public void setLangDescr(LangDescrI c, Language l){
		if (c != null){
			c.setDescr(l.getLabel(c.getDescrLangkey()));
		}
	}	
	
	/**
	 * Set a boolean value (if null then use default)
	 * @param Boolean value
	 * @param Boolean default
	 */
	static public boolean setField(Boolean value, boolean valueDefault){
		if (value != null){
			return value;
		}
		return valueDefault;
	}	
	
	/**
	 * Set a int value (if null then use default)
	 * @param Integer value
	 * @param int default
	 */
	static public int setField(Integer value, int valueDefault){
		if (value != null){
			return value;
		}
		return valueDefault;
	}	
	
	/**
	 * Remove an object from the passed in list
	 * @param IdI object (implements the getId() interface)
	 * @param List of IdI objects
	 * @return true == removed, false == object not found
	 */
	@SuppressWarnings("rawtypes")
    static public boolean removeFromList(IdI obj, List list) throws Exception{
        int index = -1;
        for (int i=0; obj != null && list != null && i<list.size(); i++){
            Object o = list.get(i);
            if (!(o instanceof IdI)){
                throw AppException.create("InvalidCall").logThisException().emailThisException();
            }
            IdI ox = (IdI)o;
            if (ox.getId().longValue() == obj.getId().longValue()){
                index = i;
                break;
            }
        }
        if (index != -1){
            list.remove(index);
            return true;
        }
        return false;
	}
	
    /**
     * Left Pad a string
     * @param String value
     * @param int padding
     * @return
     */
	static public String padLeft (String s, int p){
        s = s != null? s : "";
        if (s.length()>p){
            return s.substring(0, p-2) + "..";
        }
        while(s.length()<p){
            s = " " + s;
        }
        return s;
    }
	
	/**
     * Right Pad a string
     * @param String value
     * @param int padding
     * @return
     */
    static public String padRight (String s, int p){
        s = s != null? s : "";
        if (s.length()>p){
            return s.substring(0, p-2) + "..";
        }
        while(s.length()<p){
            s = s + " ";
        }
        return s;
    }

    /**
     * Center Pad a string
     * @param String value
     * @param int padding
     * @return
     */
    static public String padCenter (String s, int p){
        s = s != null? s : "";
        if (s.length()>p){
            return s.substring(0, p-2) + "..";
        }
        p = (p - s.length())/2;
        p = p<0?0:p;
        for(int i=0;i<p;i++){
            s = " " + s;
        }
        for(int i=0;i<p;i++){
            s = s + " ";
        }
        return s;
    }
    
    /**
     * Return contents of log file as a String.
     * @param String file path and name
     * @return Contents as string
     */
    static public String readFileToString(String filename){
        
        StringBuffer sb = new StringBuffer();
        InputStream is = null;
        BufferedReader br = null;
        try{
            is = new FileInputStream(new File(filename));
            br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            while (line != null) {
                sb.append(line + "\n");
                line = br.readLine();
            }
        } catch (Exception e){
            sb.append("Exception in reading: " + filename + "\n"
                    + "message: " + e.getMessage());
        } finally {
            try { br.close(); } catch (Throwable ignore) {}
            try { is.close(); } catch (Throwable ignore) {}
        }
        
        
        return sb.toString();
    }
    
    /**
     * Return the passed in hash-table as a list
     * @param Hashtable
     * @return List
     * @throws Exception 
     */
    static public <S,T> List<T> hashtableToList(Hashtable<S,T> list) throws Exception{
        if (list == null){
            return null;
        }
        List<T> listx = new ArrayList<T>();
        
        Enumeration<S> keys = list.keys();
        while (keys.hasMoreElements()){
            S s = keys.nextElement();
            T t = list.get(s);
            listx.add(t);
        }
        
        return listx;
    }
    
    /**
     * Return the passed in list as a hash-table
     * @param List
     * @return Hashtable
     * @throws Exception 
     */
    static public <T>Hashtable<Long,T>  listToHashtable(List<T> list) throws Exception{
        if (list == null){
            return null;
        }
        Hashtable<Long, T> t = new Hashtable<>();
        
        for (T o: list){
            if (!(o instanceof IdI)){
                throw AppException.create("List must implement IdI");
            }
            
            t.put(((IdI)o).getId(), o);
        }
        
        return t;
    }
    
    
    /**
     * Return the passed in array as a list
     * @param Hashtable
     * @return List
     * @throws Exception 
     */
    static public <T> List<T> arrayToList(T [] list) throws Exception{
        if (list == null){
            return null;
        }
        List<T> listx = new ArrayList<T>();
        
        for (T t : list){
            listx.add(t);
        }
        
        return listx;
    }
    
   
    /**
     * Return the passed in string as an array of strings
     * @param String to convert
     * @return List of strings (no null)
     */
    static public ArrayList<String> stringToArray(String string) throws Exception{
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; string != null && i < string.length(); i++) {
            String s = "" + string.charAt(i); 
            list.add(s);
        }
        return list;
    }

    /**
     * Return the passed in <code>StringBuffer</code> as delimited string
     * @param List of strings 
     * @return delimited string
     */
    static public String listToString(List<String> list) throws Exception{
        StringBuffer sb = new StringBuffer();
        for (int i = 0; list != null && i < list.size(); i++) {
            String s = list.get(i).trim();
            if (s.length()>0){
                sb.append((sb.length()>0?",":"") + s);
            }
        }
        return sb.toString();
    }
    
    /**
     * Convert passed in String to md5 hash
     * ref http://stackoverflow.com/questions/415953/generate-md5-hash-in-java
     * @param String to encode as md5 
     * @return encoded string
     */
    static public String md5(String string) throws Exception{
        if (md == null){
            md = MessageDigest.getInstance("MD5");
        }
        
        md.reset();
        md.update(string.getBytes("UTF-8"));
        byte[] digest = md.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        return bigInt.toString(16);
    }

    /**
     * Return true if the passed in string is not null and not empty
     * @param string value to test
     * @return
     */
    static public boolean isNotEmpty(String s){
        return s != null && !s.trim().isEmpty();
    }
    
    
}
