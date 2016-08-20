package com.sevenorcas.openstyle.app.mod.lang;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;

import com.sevenorcas.openstyle.app.mod.user.NoSessionRequired;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.dto.LookupDto;
import com.sevenorcas.openstyle.app.service.dto.ReturnDto;
import com.sevenorcas.openstyle.app.service.entity.ValidationException;
import com.sevenorcas.openstyle.app.service.perm.NoPermissionException;
import com.sevenorcas.openstyle.app.service.rest.RestUtilities;
import com.sevenorcas.openstyle.app.service.spreadsheet.SpreadSheetService;


/**
 * Language REST methods
 * 
 * [License]
 * @author John Stewart
 */
@Stateless
@Path("/lang")
@Produces({"application/json"})
@GZIP
@Consumes({"application/json"})
@HeaderDecoratorPrecedence
//WF10 TODO @Interceptors(RestAroundInvoke.class)
public class LanguageRest {

	private final static Logger LOG = Logger.getLogger(LanguageRest.class);
	
	@EJB private LanguageService    langService;
	@EJB private SpreadSheetService spreadSheetService;

	@PersistenceContext (unitName = "openstyleDS")
	private EntityManager em;
	
	/**
	 * Call to get language values.
	 * 
	 * @param fnr
	 * @param lang
	 * @return 
	 */
	@GET
	@Path("")
	@NoSessionRequired
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public ReturnDto lang(@QueryParam("lang") String lang, @QueryParam("set") String set) throws Exception{
		Hashtable<String, String> list = langService.lang(new UserParam(null, lang, null, null), set, true);
		
		List<LangDto> listX = new ArrayList<>();
		Enumeration<String>keys = list.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			String value = list.get(key);
			
			LangDto l = new LangDto();
			l.setLangKey(key);
			l.setText(value);
			listX.add(l);
		}
		
		return new ReturnDto(listX);
	}
	
	
	/**
	 * Get Lookup List .
	 * 
	 * @param UserParam standard parameters
	 * @return List of styles
	 */
	@GET
	@Path("lookup")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public ReturnDto lookup(@QueryParam(UserParam.QUERY_PARAM) UserParam userParam,
			@QueryParam("code") String code,
			@QueryParam("limit") Integer limit) throws Exception{
		
		LangSql sql = new LangSql(userParam);
		sql.setLookup(code);
		if (limit != null){
			sql.setLimit(limit);
		}
		
		List<LangKey> list = langService.langAll(sql);
		List<LookupDto> listX = new ArrayList<LookupDto>();
		
		for(LangKey k: list){
			if (!k.containsValues()){
				listX.add(new LookupDto(k.getKey()));
			}
			for(LangCode v: k.getValues()){
				listX.add(new LookupDto(k.getKey() + LangSql.TEXT_SEPARATOR + v.getText()));
			}
		}
		RestUtilities.addLookupMore(listX, sql);
		
		ReturnDto r = new ReturnDto(listX);
		r.setSqlObject(sql);
		return r;
	}
	
	/**
     * Clear the language cache (service only).
     * 
     * @param UserParam standard parameters
     * @return List of styles
     */
    @GET
    @Path("clearcache")
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReturnDto clearCache(@QueryParam(UserParam.QUERY_PARAM) UserParam userParam) throws Exception{
        if (!userParam.isService()){
            NoPermissionException pe = new NoPermissionException("Clear Language Cache");
            throw pe.logThisException().emailThisException();
        }
        langService.emptyCache();
        return new ReturnDto("OK");
    }
	
	
	/**
	 * Call to get language key-value pairs.
	 * @return 
	 */
	@GET
	@Path("/all")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public ReturnDto langAll(@QueryParam(UserParam.QUERY_PARAM) UserParam userParam, 
			@QueryParam("sql") LangSql sql)throws Exception{
		
		//First call
		if (sql == null){
			sql = new LangSql(userParam);
			sql.initialise();
		}
		else{
			sql.initialise(userParam);
		}
		
		
		List<LangKey> list = langService.langAll(sql);
		List<LangListDto> listX = langKeyToDto(list);
		
		ReturnDto r = new ReturnDto(listX);
		r.setSqlObject(sql);
		return r;
	}
	
	/**
	 * Convert <code>LangKey</code> objects to <code>LangListDto</code> objects
	 * @param list
	 * @return
	 */
	private List<LangListDto> langKeyToDto(List<LangKey> list) {
		
		List<LangListDto> listX = new ArrayList<>();
		LangListDto l = null;
		for(LangKey k: list){
			
			if (!k.containsValues()){
				listX.add(l = new LangListDto());
				l.setId(k.getId());
				l.setKey(k.getKey());
				l.setClient(k.getClient());
				l.setSets(k.getSets());
				l.setId_dto(k.getId_dto());
				
				l.setKeyOnly(true);
			}
			
			for(LangCode v: k.getValues()){
				listX.add(l = new LangListDto());
				l.setId(v.getId());
				l.setKey(k.getKey());
				l.setClient(k.getClient());
				l.setSets(k.getSets());
				l.setLangCode(v.getLangcode());
				l.setText(v.getText());
				l.setId_dto(v.getId_dto());
			}

		}
		return listX;
	}
	
	
	/**
	 * Call to save language key-value pairs.
	 * @return 
	 */
	@POST
	@Path("/all")
	public ReturnDto langSave(@QueryParam(UserParam.QUERY_PARAM) UserParam userParam, List<LangListDto> list)throws Exception{
		
		if (list.size() == 0){
			throw new ValidationException("NoRecordsToSave");
		}
		
		List<LangKey> listSave = new ArrayList<>();
		List<LangListDto> listReturn = new ArrayList<>();
		
		for (LangListDto dto: list){
			LangKey p = null;
			
			//Multiple values for same key
			for (LangKey k: listSave){
				if (k.getKey().equals(dto.getKey())){
					p = k;
					break;
				} 
			}

			//Lookup be code
			if (p == null){
				p = langService.findLangKeyByCode(dto.getKey());
			}
			
			//Try and find key record
			if (p == null){
				
				if (dto.isNew()){
					p = new LangKey();
				}
				
				//Must be value update
				else if (!dto.isKeyOnly()){
					p = langService.findLangKeyByLangValueId(dto.getId());
				}

			}
			dto.update(userParam, p);
			
			if (dto.isNew() && !dto.isDelete()){
				listReturn.add(dto);
			}
			
			if (p.isNew() || p.isDelete() || p.isDeleteChild()){
				listSave.add(p);
			}
			
			
		}
		
		langService.save(userParam, listSave);
		
		//FIXME Causes org.hibernate.StaleStateException when deleting
		em.flush();
		
		ReturnDto r = new ReturnDto("OK");
		
		if (listReturn.size() > 0){
			r.setObject(langService.findRecordIds(userParam, listReturn));
		}
			
		return r;
	}
	
	
	/**
	 * Call to generate language spreadsheet. This is the first stage of a 2 stage process. The return object includes the language spreadsheet filename (saved to a temporary directory).
	 * 
	 * @param UserParam standard parameters
	 * @param String language set
	 * @return 
	 */
	@GET
	@Path("export")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public ReturnDto generateSpreadSheet(@QueryParam(UserParam.QUERY_PARAM) UserParam userParam, 
	        @QueryParam("sql") LangSql sql){
		try{
		    
		    //First call
	        if (sql == null){
	            sql = new LangSql(userParam);
	            sql.initialise();
	        }
	        else{
	            sql.initialise(userParam);
	        }
	        sql.setLimit(null);
	        
			List<LangKey> list = langService.langAll(sql);
			LangExport e = new LangExport(list, langService.getLanguage(userParam.getLanguageCode()));
			
			return new ReturnDto(spreadSheetService.generateFile(userParam, e, "lang.xls", userParam.getLanguageCode(), null));
		} catch (Exception e) {
			LOG.error(e);
			return new ReturnDto(e);
		}
		
	}
	
	
	
	
	
}
