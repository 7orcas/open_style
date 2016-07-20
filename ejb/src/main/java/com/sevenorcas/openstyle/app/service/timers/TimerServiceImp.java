package com.sevenorcas.openstyle.app.service.timers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.application.ApplicationParameters;
import com.sevenorcas.openstyle.app.application.Utilities;
import com.sevenorcas.openstyle.app.application.exception.AppException;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.cache.CacheServiceImp;
import com.sevenorcas.openstyle.app.service.log.ApplicationLog;
import com.sevenorcas.openstyle.app.service.perm.Permission;

/**
 * Timer service<p>
 *  
 * Timers are callback events at a specified time, after a specified elapsed time, or after a specified interval<p> 
 *
 * [License]
 * @author John Stewart
 */

@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class TimerServiceImp implements TimerService, TimedObject, TimerI, ApplicationI {
	
	final static private int INDEX_COMPANY_NUMBER = 0;
	final static private int INDEX_SERVICE_NAME   = 1;
	final static private int INDEX_MEHTOD_NAME    = 2;
	final static private int INDEX_METHOD_PARAMS  = 3;
	final static private int INDEX_REPEAT_CODE    = 4;
	final static private int INDEX_STARTUP_TIME   = 5;
	
	private CacheServiceImp cache = CacheServiceImp.getInstance();
	
	@Resource
	private SessionContext context;
	
		
	/** Default Constructor */
	public TimerServiceImp() {}
	
	
	/**
	 * Cancel previously registered timers.<p>
	 * 
	 * @param UserParam parameters (if null then all timers are canceled)
	 * @throws Exception  
	 */
	@Permission(service=true)
	public void cancelTimers(UserParam userParam) throws Exception{
		ApplicationLog.info("Cancel Timers called");
		Collection<Timer> timers = context.getTimerService().getTimers();
		for (Timer t: timers){
			Object info = t.getInfo();
			if (info != null && info instanceof TimerInfo){
				TimerInfo i = (TimerInfo)info;
				if (userParam == null || userParam.ignoreCompany() || i.getCompanyNr().equals(userParam.getCompany())){
					i.setCancelled(true);
					try{
						t.cancel();
						ApplicationLog.info("Cancel timer: "  + i.getLog());
					}
					catch (Exception e){
						ApplicationLog.error("Can't cancel timer: "  + i.getLog());
					}
				}
			}
		}
		
	}
		
	
	/**
	 * Start registered timers.<p>
	 * 
	 * The <code>Applications Properties</code> file contains the valid timer configurations. This is read and then 
	 * used to create and start timers.<p>
	 * 
	 * Notes:<ul>
	 *     <li>All timers are canceled first for the passed in company number and restarted (if company number = 0 then
	 *         all company numbers are started)</li>
	 *     <li>Format: [company number]:[ejb service relative class name]:[ejb service method name]:[method call parameter]:[repeat code]:[initial start]<br>
	 *         repeat code:  repeat interval + m=minutes,d=days,o=one off (ie run only on startup)<br>
     *         initial start:  for repeat code days only: hh:mm</li>
	 * </ul><p>
	 * 
	 * @param UserParam parameters (if null then all timers are canceled)
	 * @throws Exception  
	 */
	@Permission(service=true)
	public void startTimers(UserParam userParam) throws Exception{
		ApplicationLog.info("Start Timers called");

		cancelTimers(userParam);
		
		//Get configured timers
		ArrayList<String> timers = ApplicationParameters.getInstance().getTimers();
		for (int i=0; timers != null && i<timers.size(); i++){
			
			String t = timers.get(i);
			try{
				String[] s = t.split(":");
				Integer compNr = Integer.parseInt(s[INDEX_COMPANY_NUMBER]);
				
				if (!userParam.ignoreCompany() && !userParam.getCompany().equals(compNr)){
					continue;
				}
				
				String service = s[INDEX_SERVICE_NAME];
				String method  = s[INDEX_MEHTOD_NAME];
				String param   = s[INDEX_METHOD_PARAMS];
				Integer repeat = 0;
				if (s[INDEX_REPEAT_CODE].length() > 1){
					repeat = Integer.parseInt(s[INDEX_REPEAT_CODE].substring(0, s[INDEX_REPEAT_CODE].length() - 1));
				}
				String repeatC = s[INDEX_REPEAT_CODE].substring(s[INDEX_REPEAT_CODE].length() - 1);

				TimerInfo info = new TimerInfo(compNr, t);
				info.setService(service);
				info.setMethod(method);
				info.setParameters(param.length() > 0? param : null);
				info.setServiceUser(userParam.isService());
				
				if (repeatC.equalsIgnoreCase("m")){
					info.setRepeat(true);
					startTimerRepeat(userParam, info, repeat, TIMER_REPEAT_MINUTES);
				}
				else if (repeatC.equalsIgnoreCase("d")){
					info.setRepeat(true);
					String start   = s[INDEX_STARTUP_TIME];
					Date d = Utilities.today(Integer.parseInt(start.substring(0, 2)), Integer.parseInt(start.substring(2)));
					startTimerRepeat(userParam, info, repeat, TIMER_REPEAT_DAYS, d);
				}
				else if (repeatC.equalsIgnoreCase("o")){
					info.setRepeat(false);
					startTimerImmediately(userParam, info);
				}
				//Badly configured
				else{
					throw new Exception();
				}
				ApplicationLog.info("Configured timer: " + t);
				
			} catch (Exception e) {
				ApplicationLog.error("Can't configure timer: " + t);
			}	
		}
		
	}

	
	
	/**
	 * Start a timer immediately.<p>
	 * 
	 * @param UserParam parameters  
	 * @param TimerInfo object with configurations
	 * @throws Exception
	 */
	private void startTimerImmediately(UserParam userParam, TimerInfo info) throws Exception{
		validate(info);
		context.getTimerService().createTimer(new Date(), info);
	}


	/**
	 * Start an repeating timer.<p>
	 * 
	 * @param UserParam parameters  
	 * @param TimerInfo object with configurations
	 * @param int value of repeat interval
	 * @param int value of repeat interval type (eg minutes, days)
	 * @throws Exception
	 */
	private void startTimerRepeat(UserParam userParam, TimerInfo info, int repeatInterval, int repeatOption) throws Exception{
		validate(info);
		startTimerRepeat(userParam, info, repeatInterval, repeatOption, null);
	}
	
	/**
	 * Start an repeating timer.<p>
	 * 
	 * @param UserParam parameters  
	 * @param TimerInfo object with configurations
	 * @param int value of repeat interval
	 * @param int value of repeat interval type (eg minutes, days)
	 * @param Date start time (eg days may configure when the first time is to expire), can be null
	 * @throws Exception
	 */
	private void startTimerRepeat(UserParam userParam, TimerInfo info, int repeatInterval, int repeatOption, Date startTime) throws Exception{
		
		startTime = startTime != null? startTime : (new Date());
		long interval = 0;
		switch(repeatOption){
		    case TIMER_REPEAT_MINUTES:
		    	interval = 1000 * 60 * repeatInterval;
		    	break;
		    case TIMER_REPEAT_DAYS:
		    	interval = 1000 * 60 * 60 * 24;
		    	break;
		    default:
		    	throw new Exception("Invalid Timer Repeat Interval Option");
		}
		
		context.getTimerService().createTimer(startTime, interval, info);
	}

	
	
	/**
	 * Run a timer task
	 * @param timer
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void ejbTimeout(Timer timer) {
		
		if (timer == null
				|| timer.getInfo() == null
				|| !(timer.getInfo() instanceof TimerInfo)){
			return;
		}
		TimerInfo info = (TimerInfo)timer.getInfo();
		if (!info.isValid()){
			return;
		}
		
		
		try{
			Object service = getTimerService(info);
		    java.lang.reflect.Method m = getTimerMethod(info);
		    
		    Object[] param = null;
		    if (info.getParameters() == null){
		    	param = new Object[1];
		    	param[0] = info.getUserParam();
		    }
		    else{
		    	param = new Object[2];
		    	param[0] = info.getUserParam();
		    	param[1] = info.getParameters();
		    }
		    
		    m.invoke(getLocalInterface(info).cast(service), param);
		
		    ApplicationLog.info("Run timer:"  + info.getLog());
		
		} catch(Exception e){
			AppException ex = new AppException("Can't run timer: " + info.getLog());
			ex.emailThisException();
			ex.logThisException();
			ApplicationLog.error(ex);
			info.setValid(true);
		}   
		
	}
	

	/**
	 * Test the passed in timer is valid 
	 * @param TimerInfo object to test
	 * @return
	 */
	private void validate(TimerInfo info) throws Exception{
		try{
			if (info.isCancelled()){
				throw new Exception("Timer already cancelled");
			}
			
			java.lang.reflect.Method m = getTimerMethod(info);
			m.toString();
		}
		catch(Exception e){
			AppException ex = new AppException("Invalid timer: " + info.getLog());
			ex.emailThisException();
			ex.logThisException();
			throw ex;
		}
	}
	
	
	/**
	 * Return the service ejb to action the passed in timer info object.<p>
	 * 
	 * Note: the ejb's local interface must be used for the lookup 
	 * @param info
	 * @return Class
	 * @throws Exception
	 */
	private Object getTimerService(TimerInfo info) throws Exception{
		return Utilities.lookupService(getLocalInterface(info).cast(null), info.getServiceClassShortName());
	}
	
	/**
	 * Return the timer task method
	 * @param info
	 * @return
	 * @throws Exception
	 */
	private java.lang.reflect.Method getTimerMethod(TimerInfo info) throws Exception{
		java.lang.reflect.Method m = null;
		
		if (info.isRepeat()){
			m = cache.getMethod(info.getService(), info.getMethod());
			if (m != null){
				return m;
			}
		}
		
		m = Utilities.findMethod(getLocalInterface(info), info.getMethod());
		
		if (info.isRepeat()){
			cache.putMethod(m, info.getService(), info.getMethod());
		}
		
		return m;
	}
	
	
	/**
	 * Return the ejb's local interface.<p>
	 * @param TimerInfo info
	 * @return Class
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Class getLocalInterface(TimerInfo info) throws Exception{
		Class clazz = null;
		
		if (info.isRepeat()){
			clazz = cache.getLocalInterfaceClass(info.getService());
			if (clazz != null){
				return clazz;
			}
		}
		
		
		clazz = Utilities.findClass(info.getService(), null);
		Class[] interfaces = clazz.getInterfaces();
		for (Class c: interfaces){
			if (c.getAnnotation(javax.ejb.Local.class) != null){
				
				if (info.isRepeat()){
					cache.putLocalInterfaceClass(c, info.getService());
				}
				return c;
			}
		}
		return null;
	}
	
	
	
	
	
	
	
}
