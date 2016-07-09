package com.sevenorcas.openstyle.app.mail;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sevenorcas.openstyle.app.AppException;


/**
 * Email utility class<p>
 * 
 * Clients can use <b>this</b> class to send emails, <b><u>HOWEVER</u></b> it is better to use the <code>MailService</code> service. 
 * 
 * [License] 
 * @author John Stewart
 */
public class Mail {

	/**
	 * Send an email
	 * @param String email fromAddress
	 * @param String email toAddress
	 * @param String email ccAddress
	 * @param String email bccAddress
	 * @param String email subject
	 * @param String email body
	 * @throws Exception
	 */
	public void sendEmail(String fromAddress, String toAddress, String ccAddress, String bccAddress, String subject, String body) throws Exception {
		sendEmail(fromAddress, toAddress, ccAddress, bccAddress, subject, body, null);
	}
	
	/**
	 * Send an email
	 * @param String email fromAddress
	 * @param String email toAddress
	 * @param String email ccAddress
	 * @param String email bccAddress
	 * @param String email subject
	 * @param String email body
	 * @param String email attachmentFileName
	 * @throws Exception
	 */
	public void sendEmail(String fromAddress, String toAddress, String ccAddress, String bccAddress, String subject, String body, String attachmentFileName) throws Exception {
		
    	if (fromAddress == null || fromAddress.trim().length() == 0
    			|| toAddress == null || toAddress.trim().length() == 0 
    			|| body == null || body.trim().length() == 0){
    		
    		AppException ax = new AppException("fromAddress="+fromAddress +  ",toAddress="+toAddress + ",body="+body);
    		ax.logThisException();
    		ax.emailThisException();
			throw ax;
		}
    	
    	Properties props = new Properties();
    	//Works
    	props.setProperty("mail.transport.protocol", "smtp");
    	props.setProperty("mail.smtp.auth", "true");
    	
    	Session mailSession = Session.getDefaultInstance(props, null);
    	
    	try{
		    InternetAddress from = new InternetAddress(fromAddress);
		    
		    MimeMessage message = new MimeMessage(mailSession);
		    message.setSubject(subject);
		    message.setFrom(from);
		    message.setReplyTo(new Address[]{from});
		    
		    if(toAddress != null && toAddress.length() > 0){
		    	message.addRecipients(Message.RecipientType.TO, parseAddress(toAddress));
		    }
		    
		    if(ccAddress != null && ccAddress.length() > 0){
		    	message.addRecipients(Message.RecipientType.CC, parseAddress(ccAddress));
		    }
		    
			if(bccAddress != null && bccAddress.length() > 0){
				message.addRecipients(Message.RecipientType.BCC, parseAddress(bccAddress));
			}
		    
			/* create the message */
			Multipart multipart = new MimeMultipart();
			message.setContent(multipart);
			
			/* create the body part of the message */			
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(body);					
			multipart.addBodyPart(messageBodyPart);
			
		    message.saveChanges();
		      
		    Transport transportx = mailSession.getTransport();
		    transportx.connect("mail.integratedsystems.de", //host    "localhost" - don't work
		    		           587,                         //port (25 is normal port) 587
		    		           "logskunden",                // user name
		    		           "logging"                    //password
		    		           ); 
		      
		    transportx.sendMessage(message, message.getAllRecipients());
		    transportx.close();

		} catch (AddressException e) {
			throw new Exception ("Address Exception", e);
		} catch (Exception e) {
			throw new Exception ("ErrUnknown", e);
		}	    	
    }
	
	/**
	 * Parse comma delimited address string
	 * @param String email address
	 * @return Array of javax.mail.Address objects 
	 * @throws AddressException
	 */
	private Address[] parseAddress(String address) throws AddressException {
    	ArrayList <Address> adresses = new ArrayList<Address>();
    	address = address.replaceAll(";", ",");
    	String[] strings = address.split(",");
    	for (int ii = 0; ii < strings.length; ii++){
    		InternetAddress internetAddress = new InternetAddress(strings[ii]);
    		adresses.add(internetAddress);
    	}
    	return adresses.toArray(new Address[]{});    	
    }
	
	

}
