package com.progenso.desma.app.timer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.progenso.desma.ApplicationI;
import com.progenso.desma.app.entities.lang.LangKey;
import com.progenso.desma.app.entities.login.LoginI;
import com.progenso.desma.app.exception.AppException;
import com.progenso.desma.app.interceptors.BaseIntercepter;
import com.progenso.desma.app.log.ApplicationLog;
import com.progenso.desma.entities.app.UserParam;
import com.progenso.desma.service.app.TimerService;

/**
 * Start timers upon <code>JBoss</code> startup and cancel upon shutdown.<p>
 *  
 * Thanks to http://bryansaunders.net/blog/2012/09/19/startup-shutdown-callbacks-in-ejb-3-1/ 
 *  
 * [License]
 * @author John Stewart
 */

@Startup
@Singleton
public class TimerStartup implements ApplicationI{
	
	
	/** Timer Service  */ private TimerService timerService;
	
	/**
	 * Start timers
	 */
	@PostConstruct 
    void atStartup() {
		ApplicationLog.info("TimerStartup called");
		
		UserParam userParam = new UserParam(UserParam.IGNORE_COMPANY_NUMBER, LangKey.getDefaultLanguageCode(), LoginI.SERVICE_USERID, LoginI.SERVICE_ID);
		userParam.setService(true); //required for permissions
		try {
			timerService = BaseIntercepter.lookupService(timerService, "TimerServiceImp");
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
	@PreDestroy
	void onShutdown() {
		ApplicationLog.info("TimerShutdown called");
		
		UserParam userParam = new UserParam(UserParam.IGNORE_COMPANY_NUMBER, LangKey.getDefaultLanguageCode(), LoginI.SERVICE_USERID, LoginI.SERVICE_ID);
		userParam.setService(true); //required for permissions
		try {
			timerService = BaseIntercepter.lookupService(timerService, "TimerServiceImp");
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
