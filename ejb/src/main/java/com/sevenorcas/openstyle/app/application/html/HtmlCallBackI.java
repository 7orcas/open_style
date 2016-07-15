package com.sevenorcas.openstyle.app.application.html;


/**
 * Classes implement <b>this</b> interface to be called back via the <code>WebPageServlet</code> when a page is being loaded.
 * 
 * @see WebPageServlet
 * 
 * [License] 
 * @author John Stewart
 */
public interface HtmlCallBackI {
	
	final public String CALLBACK       = "callback";
	final public String CALLBACK_HEAD  = CALLBACK + "-head";
	final public String CALLBACK_FOOT  = CALLBACK + "-foot";
	final public String CALLBACK_FIELD = CALLBACK + "-field";
	
    public String process(String line);
    public boolean isPermission(String ngClick);
}
