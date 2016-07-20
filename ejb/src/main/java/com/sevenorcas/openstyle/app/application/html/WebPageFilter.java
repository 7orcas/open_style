package com.sevenorcas.openstyle.app.application.html;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.mod.lang.LangKey;

/**
 * Filter login requests to add language code.<p>
 * 
 * <b>This</b> filter is defined in the <code>openstyle-web/src/main/webapp/WEB-INF/web.xml file</code>.
 * 
 * Thanks to http://stackoverflow.com/questions/8551331/how-to-add-a-parameter-to-the-existing-httpservletrequest-of-my-java-servlet
 * Thanks to http://stackoverflow.com/questions/2725102/how-to-use-a-servlet-filter-in-java-to-change-an-incoming-servlet-request-url
 * 
 * [License] 
 * @author John Stewart
 */
public class WebPageFilter implements Filter{

	final static protected String SERVICE_ATTR = "$%$&1234"; 
	private String [] langCodes;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		if (request instanceof HttpServletRequest) {
			String page = ((HttpServletRequest)request).getServletPath();
			boolean forward = false;
			
			//Test for known language codes
			for (String l : langCodes){
				if (page.equals("/" + l)){
					request.setAttribute("LangCode", l);
					forward = true;
					break;
				}	
			}
			
			//Test for service
			if (page.equals(ApplicationI.SERVICE_INDEX_PAGE_EXT)){
				request.setAttribute("service", SERVICE_ATTR);
				forward = true;
			}	
			
			//Refer to login page
			if (forward){
				RequestDispatcher rd = request.getRequestDispatcher("/login/index_login_page.html");
				rd.forward(request, response);
				return;
			}
		}
		
		chain.doFilter(request, response);
	}

	public void init(FilterConfig config) throws ServletException {
		langCodes = LangKey.getLanguageCodes();
	}
	public void destroy() {}
}
