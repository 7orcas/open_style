package com.sevenorcas.openstyle.app.service.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.metadata.ResourceMethod;

import com.sevenorcas.openstyle.app.mod.user.UserParam;


/**
 * Inject <code>UserParam</code> object into REST call.<p>
 * 
 * Initialize the <code>UserParam</code> object from the current session and inject into the URI.<p>
 * 
 * <code>UserParam</code> is a generic object and used extensively within <i>this</i> application. It contains 
 * pre-configured attributes such as:<ul>
 *     <li>Userid</li>
 *     <li>Company Number</li>
 *     <li>Current language code</li>
 * </ul>
 * 
 * [License]
 * @author John Stewart
 */

@Provider
public class RestParamPreProcess implements  ContainerRequestFilter   {
	
	/** Application Log */ private final static Logger LOG = Logger.getLogger(RestParamPreProcess.class);

	/** Injected <code>HttpServletRequest</code> */ 
	@Context private HttpServletRequest httpRequest;


	/**
	 * Inject the <code>UserParam</code> object into the URI.<p>
	 * 
	 * Thanks to http://stackoverflow.com/questions/17594910/what-is-the-proper-replacement-of-the-resteasy-3-x-preprocessinterceptor
	 * @param request context
	 */
	@Override
    public void filter(ContainerRequestContext requestContext){
		HttpSession s = httpRequest.getSession(false);
		
		if (s != null){
			try{
				UserParam userParam = getUserParam(httpRequest);
				requestContext.getUriInfo().getQueryParameters().add(UserParam.QUERY_PARAM, userParam.toJson(true));
				
			} catch (Exception e){
				LOG.error(e);
			}
		}
    }
	

	/**
     * Inject the <code>UserParam</code> object into the URI.
     * @param HttpRequest request
     * @param ResourceMethod method
     * @return null 
     */
    static public UserParam getUserParam(HttpServletRequest httpRequest){
        
        HttpSession session = httpRequest.getSession(false);
        
        if (session != null){
            try{
                UserParam userParam = (UserParam)session.getAttribute(UserParam.QUERY_PARAM);
                String l = httpRequest.getParameter("lang");
                if (l != null && l.length() > 0){
                    userParam.setLanguageCode(l);
                }
                
                if (userParam.isService()){
                    String c = httpRequest.getParameter("cn");
                    if (c != null && c.length() > 0){
                        userParam.setCompany(Integer.parseInt(c));
                    }
                }
                
                return userParam;
                
            } catch (Exception e){
                LOG.error(e);
            }
        }
        return null;
    }

	
	
	
}
