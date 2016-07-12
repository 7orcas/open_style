package com.sevenorcas.openstyle.app.mail;

import javax.ejb.Stateless;

import com.sevenorcas.openstyle.app.ApplicationI;
import com.sevenorcas.openstyle.app.ApplicationParameters;
import com.sevenorcas.openstyle.app.log.ApplicationLog;

/**
 * Email service.
 * 
 * Clients use <b>this<b> service to send emails.
 * 
 * [License]
 * @author John Stewart
 */
@Stateless
//WF10 TODO @Interceptors(ServiceAroundInvoke.class)
public class MailServiceImp implements MailService, ApplicationI {
	
    private ApplicationParameters appParam = ApplicationParameters.getInstance();
	
	/**Max Email setup attempts */	final static private int MAX_EMAIL_ATTEMPTS = 10;
	/**Email setup attempts     */	private int numberEmailAttempts = 0;
	
	
	/**
	 * Default constructor
	 */
	public MailServiceImp() {
	}
	
	
	
	/**
	 * Send an email
	 * @param String email subject
	 * @param String email body
	 * @param String email to address(s)
	 * @param String (optional) email cc address(s)
	 * @param String (optional) email bcc address(s)
	 */
	public void sendMail (String subject, String body, String to, String cc, String bcc){

		if (!appParam.isMailConfigured() || numberEmailAttempts > MAX_EMAIL_ATTEMPTS){
			return;
		}
		
		
		try {
			Mail mail = new Mail();
			mail.sendEmail(appParam.getMailFrom(), 
					       to, 
					       test(cc)?cc:null, 
					       test(bcc)?bcc:null,
					       APP_NAME + ": " + subject, 
					       body);
			numberEmailAttempts = 0;
		} catch (Exception e) {
			numberEmailAttempts++;
			ApplicationLog.error("Failed email setup, attempt=" + numberEmailAttempts);
		}
		
	}
	
	/**
	 * Test non empty string
	 * @param String to test
	 * @return
	 */
	private boolean test(String s){
		return s != null && s.length() > 0;
	}
	
	
}
