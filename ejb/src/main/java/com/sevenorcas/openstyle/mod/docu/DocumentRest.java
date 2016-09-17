package com.sevenorcas.openstyle.mod.docu;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;

import com.sevenorcas.openstyle.app.application.ApplicationParameters;
import com.sevenorcas.openstyle.app.application.ApplicationService;
import com.sevenorcas.openstyle.app.mod.company.CompanyService;
import com.sevenorcas.openstyle.app.mod.login.RestAroundInvoke;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.dto.DefinitionService;
import com.sevenorcas.openstyle.app.service.dto.ReturnDto;
import com.sevenorcas.openstyle.mod.docu.ent.DocumentDto;


/**
 * Login REST
 *
 * [License]
 * @author John Stewart
 */
@Stateless
@Path("/doc")
@Produces({"application/json"})
@GZIP
@Consumes({"application/json"})
@HeaderDecoratorPrecedence
@Interceptors(RestAroundInvoke.class)
public class DocumentRest {

    /** Application singleton */ 
    private ApplicationParameters appParam = ApplicationParameters.getInstance();
    
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(DocumentRest.class);
	
	@EJB private DocumentService    documentService;
	
	@EJB private CompanyService     companyService;
	@EJB private ApplicationService appService;
	
	@EJB private DefinitionService     definitionService;
	
	
@PersistenceContext (unitName = "openstyleDS")
private EntityManager em;
	
	
	/**
	 * Call to get a document control object.<p>
	 * 
	 * @param standard parameters
	 * @param document id
	 * @return ReturnDto object containing the <code>Control</code> object
	 */
	@GET
	@Path("selection")
	public ReturnDto selection(@QueryParam(UserParam.QUERY_PARAM) UserParam params,
			@QueryParam("id")   Long id) throws Exception {
		
		DocumentCtl ctl = new DocumentCtl (params);
		ctl.setDocId(id);
		
		ReturnDto r = new ReturnDto(new DocumentDto(documentService.findById(params, id)));
		r.setControlObject(ctl);
		
r.setModel(definitionService.definitions(ctl.getClass().getName(), params));
		
		return r;
	}

	/**
	 * Call to document html view.
	 * 
	 * @param standard parameters
	 * @param Controller object
	 * @param true = activate removing loading script
	 * @param reset window scroll to x,y
	 * @return HTML
	 */
	@GET
	@Path("view")
	@Produces({"text/html;charset=UTF-8"})
	public String view(@QueryParam(UserParam.QUERY_PARAM) UserParam params,
	        @QueryParam("cnt")  DocumentCtl cnt,
	        @QueryParam("rl")   Boolean removeLoading,
	        @QueryParam("rs")   String resetScroll) throws Exception {
		

		return documentService.html(params, cnt).view();
	}


	
}

