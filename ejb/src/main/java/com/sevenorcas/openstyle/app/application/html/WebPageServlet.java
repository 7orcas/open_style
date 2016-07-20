package com.sevenorcas.openstyle.app.application.html;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.sevenorcas.openstyle.app.application.ApplicationParameters;
import com.sevenorcas.openstyle.app.application.Utilities;
import com.sevenorcas.openstyle.app.mod.company.Company;
import com.sevenorcas.openstyle.app.mod.company.CompanyService;
import com.sevenorcas.openstyle.app.mod.lang.Language;
import com.sevenorcas.openstyle.app.mod.lang.LanguageService;
import com.sevenorcas.openstyle.app.mod.login.BaseLogin;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.log.ApplicationLog;

/**
 * Interceptor to process html and javascript files for the following:<ul>
 *     <li>strip unauthorized <code>service</code> and <code>admin</code> code</li>
 *     <li>resolve language labels within <code>html</code> files</li>
 *     <li>add language code to login page</li>
 * </ul><p>      
 * 
 * I.e. if the logged in user is not a service / admin user, then relevant code is removed prior to being sent to the browser.<p>
 * 
 * Notes:<ul>
 *     <li>Code between "service:start" and "service:end" tags is removed if user is not a <code>service</code> user</li>
 *     <li>Code between "admin:start" and "admin:end" tags is removed if user is not a <code>service</code> user</li>
 *     <li><b>This</b> servlet is defined in the <code>openstyle-web/src/main/webapp/WEB-INF/web.xml file</code></li>
 * </ul>
 *
 * TODO: Expand to include groups
 *
 * Thanks to http://jsoup.org/ (html parser)
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
public class WebPageServlet extends HttpServlet {

	/** Application singleton */ protected ApplicationParameters appParam = ApplicationParameters.getInstance();

	final static private String  SERVICE_START = "service:start";
	final static private String  SERVICE_END   = "service:end";
	final static private String  ADMIN_START   = "admin:start";
	final static private String  ADMIN_END     = "admin:end";
	final static private String  HELP_START    = "userhelp:start";
    final static private String  HELP_END      = "userhelp:end";
    final static private String  IMPORT_DEF    = "import:def";
	
	//Embedded flag to prevent caching
	final static private String  NO_CACHE      = "no-cache:true";
	
	//Embedded class name to be called with the file contents
	final static private String  CALLBACK      = "callback-class:";
	
	//Language label function definition
	final static private String LABEL_IGNOR           = "label:dontstrip";
	final static private String LABEL_START           = "{{label('";
    final static private int    LABEL_START_LENGTH    = 9;
    final static private String LABEL_END1            = "');}}";
    final static private int    LABEL_END1_LENGTH     = 5;
    final static private String LABEL_END2            = "')}}";
    final static private int    LABEL_END2_LENGTH     = 4;
    final static private String LABEL_FN_START        = "label('";
    final static private int    LABEL_FN_START_LENGTH = 7;
    final static private String LABEL_FN_END1         = "')";
    final static private int    LABEL_FN_END1_LENGTH  = 2;

    //Root Files
    final static private String LOGIN_FILE              = "index_login_page";
    final static private String MAIN_FILE               = "main_page";
    
    //Includes
    final static private String DATA_ACTION_MEUN        = "data-actionmenu";
    final static private String DATA_PERMISSION_KEY     = "data-permission-key";
    final static private String SIDEBARACTION_LABEL     = "sidebaraction-label";
    final static private String SIDEBARACTION_APPEND    = "sidebaraction-append";
    final static private String DATA_ANGULAR_INCLUDE    = "data-ng-include";
    final static private String DATA_ANGULAR_CLICK      = "data-ng-click";
    final static private String DATA_SOURCE             = "data-src";
    
    final static private String HTML_EXTENSION          = ".html";
    final static private int    HTML_EXTENSION_LENGTH   = 5;
    final static private String JS_COMMENT_START        = "/**";
    final static private String JS_COMMENT_END          = "*/";
    final static private String JS_COMMENT_SINGLE       = "//";
    
    
    final static public String JS_RECORD_NEW           = "recordNew";
    final static public String JS_RECORD_EDIT          = "recordEdit";
    final static public String JS_RECORD_DELETE        = "recordDelete";
    final static public String JS_RECORD_SAVE          = "recordSave";
    final static public String JS_RECORD_UNDO          = "recordUndo";
    final static public String JS_RECORD_EXPORT_SS     = "exportSS";
    final static public String JS_LOOKUP_ADV           = "lookupAdvance";
    
    final static private String  [] ACTION_LIST        = {JS_RECORD_NEW,JS_RECORD_EDIT,JS_RECORD_DELETE,JS_RECORD_SAVE,JS_RECORD_UNDO,JS_RECORD_EXPORT_SS,JS_LOOKUP_ADV};
    
    
    /** Used in junit testing to load files     */ protected String testRoot;
    
	/** Language Service to encode messages     */ private LanguageService langService;
	/** Company Service to test conditions      */ private CompanyService  companyService;
	
	
    
	/**
     * Initialize the servlet.
     * @see HttpServlet#init().
     */
    public void init() throws ServletException {
    }

    /**
     * Process HEAD request. This returns the same headers as GET request.
     * @see HttpServlet#doHead(HttpServletRequest, HttpServletResponse).
     */
    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Process GET request.
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse).
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Process the actual request.
     * 
     * Thanks to http://stackoverflow.com/questions/13990356/how-to-read-file-from-ear-war-jar
     * 
     * @param request The request to be processed.
     * @param response The response to be created.
     * @throws IOException If something fails at I/O level.
     */
    private void processRequest (HttpServletRequest request, HttpServletResponse response) throws IOException {
    	
    	String page = request.getServletPath();
    	
    	boolean html = page.endsWith(HTML_EXTENSION); 
    	
    	//Test for a filter page redirect
    	if (request.getAttribute("FilterPage") != null){
    		page = (String)request.getAttribute("FilterPage");
    	}
    	
    	
    	HttpSession session = request.getSession(false);
    	UserParam userParam = null;
    	String cachePage    = page;
    	Language langList   = null;
    	String langCode     = null;
    	boolean serviceAttr = false;
    	Company company     = null;
    	
    	
    	//Login is a special page
		if (page.indexOf(LOGIN_FILE) != -1){
			
			//Test for service
			String x = (String)request.getAttribute("service");
			serviceAttr = x != null && x.equals(WebPageFilter.SERVICE_ATTR);
			
			//Don't check for a session
			//Don't sub getLabel js functions 
			langCode = (String)request.getAttribute("LangCode");
			
			String ext = "." + (langCode != null? langCode : "");
			if (serviceAttr){
				ext += ".s";
				userParam = new UserParam(1,"en","service",BaseLogin.SERVICE_ID);
				userParam.setService(true);
			}
			
			cachePage = page.substring(0, page.length() - HTML_EXTENSION_LENGTH) + ext + HTML_EXTENSION;
		}
		else{
			if (session == null){
				return;
			}
			userParam   = (UserParam)session.getAttribute(UserParam.QUERY_PARAM);
			langCode    = userParam.getLanguageCode();
			
			String ext = "." + userParam.getLanguageCode();
			if (userParam.isService()){
				ext += ".s";
			}
			else if (userParam.isAdmin()){
				ext += ".a";
			}
			
			if (page.endsWith(HTML_EXTENSION)){
				cachePage = page.substring(0, page.length() - HTML_EXTENSION_LENGTH) + ext + HTML_EXTENSION;   
			}
		}
    	
		
		
		langService = Utilities.lookupService(langService, "LanguageServiceImp");
		try {
			langList = html && langService != null? langService.getLanguage(langCode) : null;
		} catch (Exception e) {
			ApplicationLog.error(e);
		}
		
		if (userParam != null){
    		companyService = Utilities.lookupService(companyService, "CompanyServiceImp");
    		try {
                company = companyService.findByNr(userParam, userParam.getCompany());
    		} catch (Exception e) {
                ApplicationLog.error(e);
            }
		}
    	

		
		try {
			String fileString = null;
			File cache = null;
			
			if (appParam.isCachePages()){
				cache = new File (appParam.getCachePath() + (cachePage.startsWith("/")? cachePage.substring(1, cachePage.length()): cachePage));
				if (cache.exists()){
					try {
						byte[] encoded = Files.readAllBytes(Paths.get(cache.getAbsolutePath()));
						fileString = new String(encoded, Charset.defaultCharset());
					} catch (Exception e){}
				}
			}
			
			if (fileString == null){
				
				boolean cacheFile = true;
				
				if (html){
					HtmlFile h = processHtml (page, userParam, langCode, langList, company);
					fileString = h.file;
					cacheFile = !h.no_cache;
				}
				else{
					fileString = processJs (page, userParam, langCode, langList, company);
				}
				

				//Cache file
				if (cacheFile && appParam.isCachePages()){
					new File(cache.getParent()).mkdirs();
					PrintWriter out = new PrintWriter(cache);
					out.print(fileString);
					out.close();
				}
			}
			
			
			if (page.endsWith(".js")){
				response.setContentType("text/javascript");
			}
			else{
				//Thanks to http://www.novell.com/documentation/extendas35/docs/help/books/TechDepartmentList.html
				//Test for service to help with chopping and changing languages in debug
//				if (userParam != null && !userParam.isService() && page.indexOf(MAIN_FILE) == -1){
//					response.setHeader("Cache-Control", "max-age=86400");
//				}

				//Needs to go before response.getWriter() 
				//Thanks to http://www.java2s.com/Code/JavaAPI/javax.servlet.http/HttpServletResponsesetContentTypetexthtmlcharsetUTF8.htm
				response.setContentType("text/html;charset=UTF-8");
			}
			
			
			response.getWriter().print(fileString);
			
			
		} catch (Exception e) {
			ApplicationLog.error(e);
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
        
        
        response.setStatus(HttpServletResponse.SC_OK); 
    	
    }
 
    
    
    /**
     * Process the passed in html file name and return a skinny html page
     * @param html relative path and filename
     * @param user parameter object
     * @param langauge code
     * @param Langauge object 
     * @param Company company
     * @return
     * @throws Exception
     */
    protected HtmlFile processHtml (String filename, UserParam userParam, String langCode, 
            Language langList, Company company) throws Exception{
    	
    	HtmlFile htmlFile = loadHtml (filename, userParam, langCode, langList, company);
    	if (filename.indexOf(LOGIN_FILE) != -1){
    		return htmlFile;
    	}
    	if (filename.indexOf(appParam.getMainPage()) != -1){
    		return htmlFile;
    	}
    	
    	Document htmlDoc = Jsoup.parse(htmlFile.file);
    	
    	
    	//Load in action menu
    	Elements actionMenuEl = htmlDoc.getElementsByAttribute(DATA_ACTION_MEUN);
    	if (actionMenuEl.hasAttr(DATA_SOURCE)){
    	
    	    boolean isUsed = false;
    	    
    		//Get Permission Key
    		Elements permissionEl = htmlDoc.getElementsByAttribute(DATA_PERMISSION_KEY);
    		String permissionKey = permissionEl.attr(DATA_PERMISSION_KEY);
    		
    		boolean create = true;
    		boolean update = true;
    		boolean delete = true;
    		
    		if (userParam != null && permissionKey.length() > 0){
    			create = userParam.isCreate(permissionKey);
        		update = userParam.isUpdate(permissionKey);
        		delete = userParam.isDelete(permissionKey);
    		}
    		
    		
    		String actions = actionMenuEl.attr(DATA_ACTION_MEUN);
    		
    		create = create && actions.indexOf("recordNew") != -1;
        	update = update && actions.indexOf("recordEdit") != -1;
        	delete = delete && actions.indexOf("recordDelete") != -1;
        	boolean save = (create || update || delete) && actions.indexOf("recordSave") != -1;
        	boolean undo = (create || update || delete) && actions.indexOf("recordUndo") != -1;
        	boolean exportSS = actions.indexOf("exportSS") != -1;
        	boolean label = false;

        	boolean lookup    = actions.indexOf("lookup") != -1;
        	boolean lookupAdv = actions.indexOf("lookupAdvance") != -1;

        	//Reset labels (if given)
        	Hashtable<String, String> labels = new Hashtable<>();
        	                      //{"recordNew","recordEdit","recordDelete","recordSave","recordUndo","exportSS","lookupAdvance"}; actionList
        	Boolean [] actionPerm = {create,     update,      delete,        save,        undo,        exportSS,   lookupAdv};
        	
        	for (int i=0; i<ACTION_LIST.length; i++){
        		if (!actionPerm[i]){
        			continue;
        		}
        		int index = actions.indexOf(ACTION_LIST[i] + ":'");
        		if (index != -1){
        		    int l = (ACTION_LIST[i] + ":'").length();
        			int index1 = actions.indexOf("'", index+l+1);
        			labels.put(ACTION_LIST[i], actions.substring(index + l, index1));
        		}
        	}

        	
        	String src = actionMenuEl.attr(DATA_SOURCE);
    		if (src.startsWith("'")){
    			src = src.substring(1);
    		}
    		if (src.endsWith("'")){
    			src = src.substring(0,src.length()-1);
    		}
    		if (!src.startsWith("/")){
    			src = "/" + src;
    		}
    		HtmlFile htmlInclude = loadHtml (src, userParam, langCode, null, company);
    		
    		Document menuDoc = Jsoup.parse(htmlInclude.file);
    		
    		//Add Standard elements
    		Elements menuEl = menuDoc.getElementsByAttribute(DATA_ANGULAR_CLICK);
    		Iterator<Element> it = menuEl.iterator();
    		
    		while (it.hasNext()){
    			Element el = it.next();
    			String x = el.attr(DATA_ANGULAR_CLICK);

    			if (!lookup && x.indexOf("lookupAction") != -1){
    			    el.parent().remove();
    			    continue;
    			}
    	
    			for (int i=0; i<ACTION_LIST.length; i++){
    				if (x.indexOf(ACTION_LIST[i]) != -1){
    					
    					//Thanks to http://stackoverflow.com/questions/16446358/jsoup-remove-elements
    					if (!actionPerm[i]){
    					    el.remove(); 
    						break;
    	        		}
    					
    					//Check callback-class if action is disabled
                        if (htmlFile.callBack != null && !htmlFile.callBack.isPermission(ACTION_LIST[i])){
                            el.remove();
                            break;
                        }
    					
    					String t = el.text();
    					if (labels.containsKey(ACTION_LIST[i]) && t != null && t.indexOf("{{label('") != -1){
    						int index = t.indexOf("{{label('");
    		        		int l = 9;
    		        		if (index != -1){
    		        			int index1 = t.indexOf("'", index+l+1);
    		        			t = t.substring(0, index + l) + labels.get(ACTION_LIST[i]) + t.substring(index1);
    		        			el.text(t);
    		        		}
        				}
    					
    					if (t != null){
    						String l = stripLanguage(t, langList);
    						el.text(l);
    					}
    					
    					isUsed = true;
    					label  = true;
    					break;
        			}	
    				
    			}

    			
    		}


    		//Add extra (ie defined in file) elements
    		Element append = menuDoc.getElementById(SIDEBARACTION_APPEND);
    		if (append != null){
    				
				it = actionMenuEl.iterator();
				while (it.hasNext()){
					Element elx = it.next();
					Iterator<Node> itx = elx.childNodes().iterator();
					ArrayList<Element> els = new ArrayList<>();
					
					while (itx.hasNext()){
						Node n = itx.next();
						if (n instanceof Element){
							els.add((Element) n);
						}
					}
					
					//Reverse the order
					for (int i=els.size()-1; i>=0; i--){
						append.after(els.get(i).outerHtml());
//						label = true;
						isUsed = true;
					}
				}
    		}
    		
    		
    		if (!label){
    			Element labelEl = menuDoc.getElementById(SIDEBARACTION_LABEL);
    			labelEl.remove();
    		}
    		
//WF10 TODO    		
//    		menuDoc.outputSettings().prettyPrint(false);
//    		menuDoc.outputSettings().indentAmount(0);
    		String html = menuDoc.body().html();
    		html = html.replace("  ", "");
    		
    		Element el = htmlDoc.getElementsByAttribute(DATA_ACTION_MEUN).first();
    		el.html(html);
    		el.removeAttr(DATA_ANGULAR_INCLUDE);
    		el.removeAttr(DATA_SOURCE);
    		el.removeAttr(DATA_ACTION_MEUN);
    		
    		if (!isUsed){
//WF10 TODO    		    actionMenuEl.remove();
    		}
    	}

//WF10 TODO
//    	htmlDoc.outputSettings().prettyPrint(false);
//    	htmlDoc.outputSettings().indentAmount(0);
    	String html = htmlDoc.body().html();
    	htmlFile.file = html.replace("  ", "");
    	
    	return htmlFile;
    }
    
    	
    
    /**
     * Process the passed in javascript file name and return a skinnier file
     * @param html relative path and filename
     * @param user parameter object
     * @param langauge code
     * @param Langauge object 
     * @param Company company
     * @return
     * @throws Exception
     */
    private String processJs (String filename, UserParam userParam, String langCode, 
            Language langList, Company company) throws Exception{
    	
    	StringBuilder sb = new StringBuilder();
    	InputStream is = null;
    	
		try {
			is = testRoot == null?
	    		 getServletContext().getResourceAsStream(filename):
	    		 new FileInputStream(new File(testRoot + filename));
			
			boolean service   = false;
			boolean admin     = false;
			boolean comment   = false;
			@SuppressWarnings("unused")
			int lineNr = 1;
			int index  = -1;
			
			try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
				String line = br.readLine();
				
				while (line != null) {
					
					//Don't strip language label functions
					if (langList != null && line.indexOf(LABEL_IGNOR) != -1){
						langList = null;
					}

					if (userParam != null 
							&& !userParam.isService() 
							&& line.indexOf(SERVICE_START) != -1){
						service = true;		
					}
					
					if (userParam != null 
							&& !userParam.isAdmin() 
							&& line.indexOf(ADMIN_START) != -1){
						admin = true;		
					}
					
					//Does the passed in line have an unclosed comment?
					if (line.indexOf(JS_COMMENT_START) != -1 && line.indexOf(JS_COMMENT_END) == -1){
						comment = true;
					}
					
					if (!service && !admin && !comment){
				
						//Import Menu State
						if (line.indexOf(IMPORT_DEF) != -1){
						    if (company == null){
                                line = "";
                            }
						    
						}

						//Remove single line comments
						index = line.indexOf(JS_COMMENT_SINGLE);
						if (index != -1){
						    line = line.substring(0, index);
						}
						
						//Remove white spaces and in-line comments
						line = line.trim();
						
						
						//Add in line
						if (line.length() > 0){
							sb.append(line);
							sb.append(System.lineSeparator());
						}
						
					}
					
					if (userParam != null
							&& !userParam.isAdmin() 
							&& line.indexOf(ADMIN_END) != -1){
						admin = false;		
					}
					
					if (userParam != null
							&& !userParam.isService() 
							&& line.indexOf(SERVICE_END) != -1){
						service = false;		
					}
					
					//Is the passed in line a closing comment?
					if (comment && line.indexOf(JS_COMMENT_END) != -1){
						comment = false;
					}
					
					line = br.readLine();
					lineNr++;
				}
			}
		
		} finally {
	        try { is.close(); } catch (Throwable ignore) {}
	    }

		return sb.toString();
    }
    
    
    /**
     * Process the passed in file name and return a skinny html page
     * @param String file name being processed
     * @param user parameter object
     * @param langauge code
     * @param Langauge object 
     * @param Company company
     * @return
     * @throws Exception
     */
    private HtmlFile loadHtml (String filename, UserParam userParam, String langCode, 
            Language langList, Company company) throws Exception{
    	
    	StringBuilder sb = new StringBuilder();
    	InputStream is = null;
    	HtmlFile htmlFile = new HtmlFile();
    	
    	try {
    		is = testRoot == null?
    			 getServletContext().getResourceAsStream(filename):
    			 new FileInputStream(new File(testRoot + filename));
    		
    	    boolean serviceUser = userParam != null && userParam.isService();
    		boolean adminUser   = serviceUser || (userParam != null && userParam.isAdmin());
    		boolean onlineHelp  = appParam.getHelpFileRoot() != null && appParam.getHelpFileRoot().length()>0;
    		
//WF10 TODO    		
//    		if (company != null && company.getHelpFileRoot() != null && company.getHelpFileRoot().length()>0){
//    		    onlineHelp  = true;
//    		}
    		
			boolean service   = false;
			boolean admin     = false;
			boolean help      = false;
			boolean comment   = false;
			int lineNr = 1;
			int index = -1;
			
			try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
				String line = br.readLine();
				
				while (line != null) {
					
					if (htmlFile.callBack != null){
						line = htmlFile.callBack.process(line);
					}
					
					if (line.indexOf(NO_CACHE) != -1){
						htmlFile.no_cache = true;		
					}
					
					if ((index = line.indexOf(CALLBACK)) != -1){
						String classname  = line.substring(index + CALLBACK.length()).trim();
						htmlFile.callBack = (HtmlCallBackI)Utilities.create(Utilities.findClass(classname), userParam, company);
					}
					
					//Don't strip language label functions
					if (langList != null && line.indexOf(LABEL_IGNOR) != -1){
						langList = null;
					}
	
					if (!serviceUser && line.indexOf(SERVICE_START) != -1){
						service = true;		
					}
					
					if (!adminUser && line.indexOf(ADMIN_START) != -1){
						admin = true;		
					}
					
					if (!onlineHelp && line.indexOf(HELP_START) != -1){
					    help = true;       
                    }
					
					//Comments
					int x1 = line.indexOf("<!--");
					int x2 = line.indexOf("-->");
					
					if (x1 != -1 && x2 == -1){
					    comment = true;
					}
					
					if (!service && !admin && !help && !comment && !isConditionFail(line, userParam, company)){
				
					    //Comments
					    if (x1 != -1 && x2 != -1){
					        String line1 = x1 > 0 ? line.substring(0, x1) : "";
					        String line2 = line.substring(x2 + 3);
					        line = line1 + line2;
					    }
					    
						//Sub label code
						if (langList != null){
							line = stripLanguage(line, langList);
						}
						
						//Find html lines that contain {{}} in html. These need to be inspected and possibly removed.
						if (line.indexOf("{{") != -1){
							suspiciousText(lineNr, filename, line);
						}
						
						//Add Language code value
						if (langCode != null && 
								line.indexOf("<input id='langcode' type='hidden' name='LanguageCode' value='xx'>") != -1){
							line = line.replace("xx", langCode);
						}
						
						//Add in line
						if (line.length() > 0){
							sb.append(line);
						}
					}
					
					if (line.indexOf(HELP_END) != -1){
					    help = false;       
					}
					if (line.indexOf(ADMIN_END) != -1){
						admin = false;		
					}
					if (line.indexOf(SERVICE_END) != -1){
						service = false;		
					}
					
					if (x2 != -1){
                        comment = false;
                    }
					
					line = br.readLine();
					lineNr++;
				}
			}
			
    	} finally {
    		try { is.close(); } catch (Throwable ignore) {}
    	}
		
    	htmlFile.file = sb.toString();
    	
    	return htmlFile;
    }

    
    
    
    /**
     * Remove language 'label' javascript function
     * @param String line
     * @param Language object
     * @return
     */
    private String stripLanguage(String line, Language langList){
    	line = stripLanguage(0, line, langList, false, LABEL_START, LABEL_END1, LABEL_END2, LABEL_START_LENGTH, LABEL_END1_LENGTH, LABEL_END2_LENGTH);
    	line = stripLanguage(0, line, langList, true,  LABEL_FN_START, LABEL_FN_END1, null, LABEL_FN_START_LENGTH, LABEL_FN_END1_LENGTH, 0);
    	return line;
    }
    
    
    /**
     * Remove language 'label' javascript function
     * @param int start position
     * @param String line
     * @param Language object
     * @param mulitple search parameters
     * @return
     */
    private String stripLanguage(int start, String line, Language langList, 
    		boolean useQuotes, String labelStart, String labelEnd1, String labelEnd2, int labelStartLength, int labelEnd1Length, int labelEnd2Length){

    	int index1 = line.indexOf(labelStart, start);
    	int index2 = -1;
    	int len2   = labelEnd1Length;
    	
    	if (index1 != -1){
    		
    		index2 = line.indexOf(labelEnd1, index1);
    		
    		if (index2 == -1 && labelEnd2 != null){
    			index2 = line.indexOf(labelEnd2, index1);
        		len2   = labelEnd2Length;
    		}
    		
    		if (index2 != -1){
    			String key = line.substring(index1 + labelStartLength, index2); 
    			line = line.substring(0, index1)
    					+ (useQuotes?"'":"")
    					+ langList.getLabel(key) 
    					+ (useQuotes?"'":"")
    					+ line.substring(index2 + len2);
    		}
    	}
    	
    	if (index2 != -1){
    		line = stripLanguage(index2 + len2 + 1, line, langList, 
    				useQuotes, labelStart, labelEnd1, labelEnd2, labelStartLength, labelEnd1Length, labelEnd2Length);
    	}
    	
    	return line;
    }
  
   
    
    /**
     * Record suspicious text
     * @param int lineNr
     * @param String source file name
     * @param String suspicious line
     */
    private void suspiciousText(int lineNr, String filename, String line){
    	if (appParam.isCachePages()){
			String f = appParam.getCachePath() + "suspicious.txt";
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f, true)))) {
				out.println(filename + ":" + lineNr + " " + line);
			}catch (IOException e) {
				//exception handling left as an exercise for the reader
			}
		}
    	
    }
    
    
    /**
     * Does the passed in line fail a condition?
     * @param String html line
     * @param User parameter object
     * @param Company company
     * @return
     */
    private boolean isConditionFail (String line, UserParam userParam, Company company){
        
        int index = line.indexOf("condition:");
        if (index == -1){
            return false;
        }
        
        
        if (line.indexOf("service", index+1) != -1 && (userParam == null || !userParam.isService())){
            return true;
        }
        
        return false;
    }
    
    
    
    /**
     * Convenience class to return file along with flags
     * @author john.stewart
     *
     */
    private class HtmlFile {
    	String file;
    	boolean no_cache = false;
    	HtmlCallBackI callBack = null;
    }
    
    
}
