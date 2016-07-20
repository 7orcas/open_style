package com.sevenorcas.openstyle.app.service.mail;

import javax.ejb.Local;

/**
 * Email service local interface.
 * 
 * Clients use <b>this<b> service to send emails.
 * 
 * [License]
 * @author John Stewart
 */
@Local
public interface MailService {
	public void sendMail (String subject, String body, String to, String cc, String bcc);
}
