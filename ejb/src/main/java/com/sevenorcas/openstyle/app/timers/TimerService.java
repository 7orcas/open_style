package com.sevenorcas.openstyle.app.timers;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.user.UserParam;


/**
 * Local interface to Timer Service
 *
 * [License]
 * @author John Stewart
 */
@Local
public interface TimerService {
	public void cancelTimers(UserParam userParam) throws Exception;
	public void startTimers(UserParam userParam) throws Exception;
}
