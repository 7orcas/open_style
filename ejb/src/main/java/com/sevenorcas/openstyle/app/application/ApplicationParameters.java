package com.sevenorcas.openstyle.app.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.jboss.logging.Logger;

import com.sevenorcas.openstyle.app.user.UserParam;


/**
 * Singleton class to read in and store application specific parameters.<p>
 * 
 * <b>This class reads in a properties file located in the <code>standalone/deployment</code> directory.</b> The file contains 
 * default parameters. If a parameter is missing form the file then a hard-coded value is used (if appropriate).<p>
 * 
 * <b>This<b> class is used extensively through out the application.
 * 
 * [License] 
 * @author John Stewart
 */
public class ApplicationParameters implements ApplicationI {

	private Properties properties;
	static private ApplicationParameters self;
	
	/**
	 * Absolute path to the JBoss home directory. 
	 * Read from <code>System.getProperty("jboss.home.dir")</code> 
	 */
	public static String jbossHome = null;
	
	
	private static final Logger LOG = Logger.getLogger(ApplicationParameters.class);

	//True = the application properties file is valid 
	private boolean valid = false;

	//True = <b>this</b> application properties file is being run in test mode 
	public static boolean testMode = false;
	
	
	/**
	 * Redirect page once user has successfully logged
	 */
	private String mainPage;
	
	/**
	 * Unique company number that a userid can log into. The application facilitates multiple companies.  
	 */
	private Integer defaultCompanyNr;
	
	//Paths
	private String homePath;
	private String deployPath;
	private String tempFilePath;  
	private String pdfTemplatesPath;
	private String importFilePath;
	private String helpFileRoot;
	private String serviceLockFilePath;
		
	
	//Enable the use of admin user rights
	//true=this application will enforce 'update' functions for admin users only
	//false=Admin user type is ignored, ie all users have equal rights
	private boolean enableAdminUsers;

	//Enable the use of service account
	//True = service (back door) login is active
	private boolean enableServiceAccount;
	
	//Service password for full access
	private String serviceAccountPassword;
	
	//Debug mode allows testing and development options. 
	//For production environments this MUST be false!
	private boolean debug;
	private String  debugPdfTemplate;
	private boolean debugTimers;
	private boolean debugReset;
	
	//Supported 2 letter language codes (in order of preference, ie order of search if specific langauge code value is missing)
	private String languageCodes;

	//Postgres Datasource
	private String postgresDatasource;
	
	//Mail config
	private String mailFrom;
	private String mailHost;
	private int mailPort = 0;
	private String mailUser;
	private String mailPassword;

	//False login mail
	private boolean mailFalseLoginAttempt = false;
	private String loginMailTo;
	private String loginMailCc;
	private String loginMailBcc;
	
	//Application error mail
	private boolean mailApplicationError = false;
	private String errorMailSubject;
	private String errorMailTo;
	private String errorMailCc;
	private String errorMailBcc;
	
	//Captcha config
	private boolean enableCaptcha = false; 
	private String captchaPrivateKey;
	private String captchaPublicKey;
	
    //Number of minutes between each client heartbeat. Values <= 0 result in no heartbeat. 
	private int heartbeat = 0;
	
	//Number of seconds between testing for client timeouts. Values <= 0 result in no test. 
    private int sessionTimeoutTest = 0;
	

	//Timer task configurations
	private ArrayList<String> timers = null;

	//Cache of pages
	private boolean cachePages;
	private boolean cachePages_delete;
	
	//Date formats
	private String dateFormatDefault;
	private String dateFormatShort;
	private String dateFormatDto;
	private int dateFormatMonthDefault;
	
	
	
	
		
	/**
	 * Reload <b>this</b> file
	 * @return
	 */
	public ApplicationParameters reload(){
		self = new ApplicationParameters();
		return this;
	}
	
	
	/**
	 * Create and initialize
	 */
	private ApplicationParameters (){
	
		homePath = System.getProperty("jboss.home.dir");
		if (homePath == null){
			homePath = jbossHome;
		}
		
		deployPath = homePath;
		if (!deployPath.endsWith(File.separator)){
        	deployPath = deployPath + File.separator;
        }
		deployPath = deployPath + "standalone" + File.separator + "deployments" + File.separator;
		
		
		properties = new Properties();
		 
    	try {
            //load a properties file
    		String n = deployPath + getEarName() + ".properties";
    		properties.load(new FileInputStream(n));
 
    		postgresDatasource     = load("PostgresDatasource");
    		    		
    		debug                  = load("Debug", false);
    		debugTimers            = load("Debug.timers", false);
    		debugPdfTemplate       = load("Debug.pdf.template", null);
    		debugReset             = load("Debug.reset", false);
    		
    		mainPage               = load("App.mainPage");
    		defaultCompanyNr       = load("App.company.default", 0);
    		languageCodes          = load("App.languageCodes", "en");
    		heartbeat              = load("App.heartbeat", 0);
    		sessionTimeoutTest     = load("App.sessionTimeoutTest", 0);
    		cachePages             = load("App.cachePages", false);
    		cachePages_delete      = load("App.cachePages.delete_on_startup", false);
    		
    		enableAdminUsers       = load("App.adminUsers.enable", true);
    		enableServiceAccount   = load("App.serviceAccount", true);
    		serviceAccountPassword = load("App.serviceAccount.password", null);
    		
    		enableCaptcha          = load("Captcha.enable", false);
    		captchaPrivateKey      = load("Captcha.privateKey");
    		captchaPublicKey       = load("Captcha.publicKey");

    		tempFilePath           = path(load("Path.tempFiles"));
    		pdfTemplatesPath       = path(load("Path.pdfTemplates"));
    		importFilePath         = path(load("Path.importFiles"));
    		serviceLockFilePath    = path(load("Path.serviceLockFile"));
    		helpFileRoot           = load("Url.helpFileRoot"); 
    		
    		dateFormatDto          = date("Date.format.dto", "dd-MM-yyyy");
    		dateFormatDefault      = date("Date.format.default", "dd.MM.yy");
    		dateFormatMonthDefault = load("Date.format.month.default", 2);
    		dateFormatShort        = date("Date.format.short", "dd-MMM");
    		
    		mailHost               = load("Mail.config.host");
    		mailPort               = load("Mail.config.port", 0);
    		mailUser               = load("Mail.config.user");
    		mailPassword           = load("Mail.config.password");
    		mailFrom               = load("Mail.config.from");
    		
    		mailFalseLoginAttempt  = load("Mail.falselogin.enable", false);
    		loginMailTo            = load("Mail.falselogin.to");
    		loginMailCc            = load("Mail.falselogin.cc");
    		loginMailBcc           = load("Mail.falselogin.bcc");
    		
    		mailApplicationError   = load("Mail.error.enable", false);
    		errorMailSubject       = load("Mail.error.subject");
    		errorMailTo            = load("Mail.error.to");
    		errorMailCc            = load("Mail.error.cc");
    		errorMailBcc           = load("Mail.error.bcc");
    		    		
    		String timersstring = load("Timers", null);
    		if (timersstring != null){
    			timers = new ArrayList<String>();
    			String[] s = timersstring.split(",");
    			for (String t: s){
    				timers.add(t);
    			}
    		}
    		
    		valid = true;
    		
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
		
	}
	
	
	/**
	 * Get <code>ear</code> file name from <code>application.xml</code>
	 * Thanks to https://community.jboss.org/thread/109475?start=15&tstart=0  
	 * @return ear name
	 */
	private static String getEarName() {
		String name = null;
//WF10 TODO
//		 try {
//			name = (String)new InitialContext().lookup("java:app/AppName");
//		} catch (NamingException e) {
//			if (!testMode){
//				e.printStackTrace();
//			}
//		}
		name = name != null && name.length() > 0? name : APP_NAME;
		return name;
	}
	
	/**
	 * Configure path. If path starts with $$ then it is treated as an absolute path, otherwise the path is considered relative to the JBoss home directory.
	 * @param path
	 * @return
	 */
	private String path (String path){
		int index = path.indexOf("$$");
        if (index != -1){
        	path = path.substring(index+2);
        }
        path = path.replace("\\", File.separator); 
        if (path.length() > 0 && !path.endsWith(File.separator)){
        	path = path + File.separator;
        }
        return path;
	}
	
	
	/**
	 * Singleton
	 * @return
	 */
	static public ApplicationParameters getInstance(){
		if (self == null){
			self = new ApplicationParameters();
		}
		return self;
	}
	
	
	private boolean load(String prop, boolean defaultValue){
		String value = properties.getProperty(prop);
		
		if (value == null){
			return defaultValue;
		}
		
		value = value.toLowerCase();
		if (value.equals("true")){
			return true;
		}
		return false;
	}
	
	private int load(String prop, int defaultValue){
		try{
			return Integer.parseInt(properties.getProperty(prop));
		}
		catch (Exception e){
			if (properties.getProperty(prop) != null){
				LOG.error("Invalid Property: " + prop);
			}	
			return defaultValue;
		}
	}
	
	
	private String load(String prop){
		return load(prop, "");
	}

	private String date(String prop, String defaultFormat){
        return load(prop, defaultFormat);
    }
	
	private String load(String prop, String defaultValue){
		String value = properties.getProperty(prop);
		
		if (value == null){
			return defaultValue;
		}
		
		return value;
	}

	private boolean test(String s){
		return s != null && s.length() > 0;
	}
	
	/**
	 * Is the <code>Application.properties</code> file valid? 
	 * @return valid
	 */
	public boolean isValid() {
		return valid;
	}
	
	public boolean isDebug() {
		return debug;
	}
	public ApplicationParameters setDebug(boolean value) {
		debug = value;
		return this;
	}
    public boolean isDebugTimers() {
        return debugTimers;
    }
    public ApplicationParameters setDebugTimers(boolean debugTimers) {
        this.debugTimers = debugTimers;
        return this;
    }
    public String getDebugPdfTemplate() {
        return debugPdfTemplate;
    }
    public ApplicationParameters setDebugPdfTemplate(String debugPdfTemplate) {
        this.debugPdfTemplate = debugPdfTemplate;
        return this;
    }
    public boolean isDebugReset() {
        return debugReset;
    }
    
    

    

	public String getMainPage() {
		return mainPage;
	}
	public ApplicationParameters setMainPage(String mainPage) {
		this.mainPage = mainPage;
		return this;
	}


	public Integer getDefaultCompanyNumber() {
		return defaultCompanyNr;
	}
	public ApplicationParameters setDefaultCompanyNumber(Integer defaultCompanyNr){
		this.defaultCompanyNr = defaultCompanyNr;
		return this;
	}
	
	
	
	public String getHomePath() {
		return homePath;
	}

	public String getDeployPath() {
		return deployPath;
	}

	public String getCachePath() {
		return deployPath + ApplicationI.APP_NAME + ".cache/";
	}
	
	/**
	 * Temporary file paths are specific to the client company number
	 * @param UserParam User parameters containing the client company number
	 * @return temporary file path
	 */
	public String getTempFilePath(UserParam params) {
		return tempFilePath + (params != null? params.getCompany() + File.separator : "");
	}
	public ApplicationParameters setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
        return this;
    }
    public boolean isTempFilePath() {
		return test(tempFilePath);
	}
	
	/**
     * Pdf Templates file path
     * @return file path
     */
    public String getPdfTemplatesPath() {
        return pdfTemplatesPath;
    }
    public boolean isPdfTemplatesPath() {
        return test(pdfTemplatesPath);
    }
    
    
    /**
     * Service lock file path
     * @return file path
     */
    public String getServiceLockFilePath() {
        return serviceLockFilePath;
    }
    public ApplicationParameters setServiceLockFilePath(String serviceLockFilePath) {
        this.serviceLockFilePath = serviceLockFilePath;
        return this;
    }


    /**
     * Import file paths are specific to the client company number
     * @param UserParam User parameters containing the client company number
     * @return file path
     */
    public String getImportFilePath(UserParam params) {
        return importFilePath + (params != null? params.getCompany() + File.separator : "");
    }
    public boolean isImportFilePath() {
        return test(importFilePath);
    }
    
    
    
	public String getHelpFileRoot() {
		return helpFileRoot;
	}
	public boolean isHelpFileRoot() {
		return test(helpFileRoot);
	}
	
	public Integer getHeartbeat() {
		return heartbeat;
	}
	public int getSessionTimeoutTest() {
        return sessionTimeoutTest;
    }


	public String getPostgresDatasource() {
		return postgresDatasource;
	}
	public ApplicationParameters setPostgresDatasource(String postgresDatasource) {
		this.postgresDatasource = postgresDatasource;
		return this;
	}


	public boolean isMailConfigured() {
		return test(mailHost) 
				&& mailPort != 0
				&& test(mailFrom);
	}
	public String getMailFrom() {
		return mailFrom;
	}
	
	
	public boolean isMailFalseLoginAttempt() {
		return mailFalseLoginAttempt;
	}

	public String getLoginMailTo() {
		return loginMailTo;
	}
	public boolean isLoginMailTo() {
		return test(loginMailTo);
	}
	
	public String getLoginMailCc() {
		return loginMailCc;
	}
	public String getLoginMailBcc() {
		return loginMailBcc;
	}
	
	public boolean isMailApplicationError() {
		return mailApplicationError;
	}

	public String getErrorMailTo() {
		return errorMailTo;
	}
	public boolean isErrorMailTo() {
		return test(errorMailTo);
	}
	
	public String getErrorMailSubject() {
		return errorMailSubject;
	}
	public String getErrorMailCc() {
		return errorMailCc;
	}
	public String getErrorMailBcc() {
		return errorMailBcc;
	}
	
	
	public String getMailHost() {
		return mailHost;
	}

	public Integer getMailPort() {
		return mailPort;
	}

	public String getMailUser() {
		return mailUser;
	}

	public String getMailPassword() {
		return mailPassword;
	}

	public boolean isEnableAdminUsers() {
		return enableAdminUsers;
	}
	
	public boolean isEnableServiceAccount() {
		return enableServiceAccount;
	}
	
	public String getServiceAccountPassword() {
		return serviceAccountPassword;
	}


	public ArrayList<String> getTimers() {
		return timers;
	}
	public ApplicationParameters setTimers(ArrayList<String> timers) {
		this.timers = timers;
		return this;
	}


	/////////////////////////////// ReCaptcha config ///////////////////////////////////////////////////
	public boolean isEnableCaptcha() {
		return enableCaptcha;
	}
	public ApplicationParameters setEnableCaptcha(boolean enableCaptcha) {
		this.enableCaptcha = enableCaptcha;
		return this;
	}

	public String getCaptchaPrivateKey() {
		return captchaPrivateKey;
	}
	public String getCaptchaPublicKey() {
		return captchaPublicKey;
	}


	/////////////////////////////// Language configuration ///////////////////////////////////////////////////
	
	public String getLanguageCodes() {
		return languageCodes;
	}
	public ApplicationParameters setLanguageCodes(String languageCodes) {
		this.languageCodes = languageCodes;
		return this;
	}
	/**
	 * Return the first language code
	 * @return
	 */
	public String getFirstLanguageCode() {
        String [] s = languageCodes.split(",");
        return s[0];
    }
	
	
	/////////////////////////////// Application specific configuration ///////////////////////////////////////////////////
	
	
	
	public boolean isCachePages() {
		return cachePages;
	}
	public ApplicationParameters setCachePages(boolean cachePages) {
		this.cachePages = cachePages;
		return this;
	}
	public boolean isCachePages_delete() {
		return cachePages_delete;
	}
	public ApplicationParameters setCachePages_delete(boolean cachePages_delete) {
		this.cachePages_delete = cachePages_delete;
		return this;
	}
	public String getDateFormatDto() {
		return dateFormatDto;
	}
	public ApplicationParameters setDateFormatDto(String dateFormatDto) {
		this.dateFormatDto = dateFormatDto;
		return this;
	}
	public String getDateFormatDefault() {
		return dateFormatDefault;
	}
	public ApplicationParameters setDateFormatDefault(String dateFormatDefault) {
		this.dateFormatDefault = dateFormatDefault;
		return this;
	}
	public int getDateFormatMonthDefault() {
        return dateFormatMonthDefault;
    }
    public ApplicationParameters setDateFormatMonthDefault(int dateFormatMonthDefault) {
        this.dateFormatMonthDefault = dateFormatMonthDefault;
        return this;
    }
    public String getDateFormatShort() {
		return dateFormatShort;
	}
	public ApplicationParameters setDateFormatShort(String dateFormatShort) {
		this.dateFormatShort = dateFormatShort;
		return this;
	}
	
	
    
	
}
