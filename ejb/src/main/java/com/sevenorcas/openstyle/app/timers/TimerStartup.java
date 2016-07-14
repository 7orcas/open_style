package com.sevenorcas.openstyle.app.timers;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.sevenorcas.openstyle.app.AppException;
import com.sevenorcas.openstyle.app.ApplicationI;
import com.sevenorcas.openstyle.app.Utilities;
import com.sevenorcas.openstyle.app.lang.LangKey;
import com.sevenorcas.openstyle.app.log.ApplicationLog;
import com.sevenorcas.openstyle.app.login.LoginI;
import com.sevenorcas.openstyle.app.user.UserParam;

/**
 * Start timers upon <code>JBoss</code> startup and cancel upon shutdown.<p>
 *  
 * Thanks to http://bryansaunders.net/blog/2012/09/19/startup-shutdown-callbacks-in-ejb-3-1/ 
 *  
 * [License]
 * @author John Stewart
 */

//WF10 TODO @Startup
@Singleton
public class TimerStartup implements ApplicationI{
	
	
	/** Timer Service  */ private TimerService timerService;
	
	/**
	 * Start timers
	 */
	//WF10 TODO 	@PostConstruct 
    void atStartup() {
		ApplicationLog.info("TimerStartup called");
		
		UserParam userParam = new UserParam(UserParam.IGNORE_COMPANY_NUMBER, LangKey.getDefaultLanguageCode(), LoginI.SERVICE_USERID, LoginI.SERVICE_ID);
		userParam.setService(true); //required for permissions
		try {
			timerService = Utilities.lookupService(timerService, "TimerServiceImp");
			timerService.startTimers(userParam);
		} catch (Exception e) {
			AppException ex = new AppException("Can't start timers");
			ex.emailThisException();
			ex.logThisException();
			ApplicationLog.error(ex);
			return;
		}
	}
	
	/**
	 * Cancel timers
	 * TODO: Test if this works (suspect shut down is too fast)
	 */
  //WF10 TODO @PreDestroy
	void onShutdown() {
		ApplicationLog.info("TimerShutdown called");
		
		UserParam userParam = new UserParam(UserParam.IGNORE_COMPANY_NUMBER, LangKey.getDefaultLanguageCode(), LoginI.SERVICE_USERID, LoginI.SERVICE_ID);
		userParam.setService(true); //required for permissions
		try {
			timerService = Utilities.lookupService(timerService, "TimerServiceImp");
			timerService.cancelTimers(userParam);
		} catch (Exception e) {
			AppException ex = new AppException("Can't cancel timers");
			ex.emailThisException();
			ex.logThisException();
			ApplicationLog.error(ex);
			return;
		}
	}
	
}
