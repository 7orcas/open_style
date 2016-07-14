package com.sevenorcas.openstyle.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

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

	/**
	 * Absolute path to the JBoss home directory. 
	 * Read from <code>System.getProperty("jboss.home.dir")</code> 
	 */
	public static String jbossHome = null;
	
	
	private static final Logger LOG = Logger.getLogger(ApplicationParameters.class);

	//True = the application properties file is valid 
	private boolean valid = false;

	//True = <b>thhis</b> application properties file is being run in test mode 
	public static boolean testMode = false;
	
	/**
	 * Site is used to uniquely identify a range of company numbers
	 */
	private String site;
	
	/**
	 * Redirect page once user has successfully logged
	 */
	private String mainPage;
	
	/**
	 * Unique company number that a userid can log into. The application facilitates multiple companies.  
	 */
	private Integer companyNumber;
	
	//Paths
	private String homePath;
	private String deployPath;
	private String tempFilePath;  
	private String pdfTemplatesPath;
	private String importFilePath;
	private String helpFileRoot;
	private String simuProgressFilePath;
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
	private String debugPdfTemplate;
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
	
	
	private Properties properties;
	static private ApplicationParameters self;

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
	
    ///////////////////////////// Application specific attributes ///////////////////////////////////////////////////
	//Note the Hashtable keys's are this applications company number
	
	
	
		
	/**
	 * Reload <b>this</b> file
	 * @return
	 */
	public void reload(){
		self = new ApplicationParameters();
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
 
    		postgresDatasource = load("PostgresDatasource");
    		
    		
    		debug            = load("Debug", false);
    		debugTimers      = load("Debug.timers", false);
    		debugPdfTemplate = load("Debug.pdf.template", null);
    		debugReset       = load("Debug.reset", false);
    		
    		site          = load("Site");
    		mainPage      = load("MainPage");
    		companyNumber = load("Company", 0);
    		
    		languageCodes = load("LanguageCodes", "en");
    		    		
    		cachePages        = load("CachePages", false);
    		cachePages_delete = load("CachePages.delete_on_startup", false);
    		
    		enableAdminUsers       = load("EnableAdminUsers", true);
    		enableServiceAccount   = load("EnableServiceAccount", true);
    		serviceAccountPassword = load("ServiceAccountPassword", null);
    		
    		mailFrom     = load("MailFrom");
    		mailHost     = load("MailHost");
    		mailUser     = load("MailUser");
    		mailPassword = load("MailPassword");
    		mailPort     = load("MailPort", 0);

    		heartbeat             = load("Heartbeat", 0);
    		sessionTimeoutTest    = load("SessionTimeoutTest", 0);
    		
    		mailFalseLoginAttempt = load("MailFalseLoginAttempt", false);
    		loginMailTo           = load("LoginMailTo");
    		loginMailCc           = load("LoginMailCc");
    		loginMailBcc          = load("LoginMailBcc");
    		
    		mailApplicationError  = load("MailApplicationError", false);
    		errorMailSubject      = load("ErrorMailSubject");
    		errorMailTo           = load("ErrorMailTo");
    		errorMailCc           = load("ErrorMailCc");
    		errorMailBcc          = load("ErrorMailBcc");
    		
    		enableCaptcha     = load("EnableCaptcha", false);
    		captchaPrivateKey = load("CaptchaPrivateKey");
    		captchaPublicKey  = load("CaptchaPublicKey");
 
    		tempFilePath          = path(load("TempFilePath"));
    		pdfTemplatesPath      = path(load("PdfTemplatesPath"));
    		importFilePath        = path(load("ImportFilePath"));
    		simuProgressFilePath  = path(load("SimuProgressFilePath"));
    		serviceLockFilePath   = path(load("ServiceLockFilePath"));
    		helpFileRoot          = load("HelpFileRoot"); 
    		
    		String timersstring = load("Timers", null);
    		if (timersstring != null){
    			timers = new ArrayList<String>();
    			String[] s = timersstring.split(",");
    			for (String t: s){
    				timers.add(t);
    			}
    		}
    		
    		dateFormatDto             = date("Date.format.dto", "dd-MM-yyyy");
    		dateFormatDefault         = date("Date.format.default", "dd.MM.yy");
    		dateFormatMonthDefault    = load("Date.format.month.default", 2);
    		dateFormatShort           = date("Date.format.short", "dd-MMM");
    		
            ///////////////////////////// Application specific configuration ///////////////////////////////////////////////////
    		
    		
    		
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
	
	private long load(String prop, long defaultValue){
		try{
			return Long.parseLong(properties.getProperty(prop));
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
	public void setDebug(boolean value) {
		debug = value;
	}
    public boolean isDebugTimers() {
        return debugTimers;
    }
    public void setDebugTimers(boolean debugTimers) {
        this.debugTimers = debugTimers;
    }
    public String getDebugPdfTemplate() {
        return debugPdfTemplate;
    }
    public void setDebugPdfTemplate(String debugPdfTemplate) {
        this.debugPdfTemplate = debugPdfTemplate;
    }
    public boolean isDebugReset() {
        return debugReset;
    }
    
    

    public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}

	public String getMainPage() {
		return mainPage;
	}
	public void setMainPage(String mainPage) {
		this.mainPage = mainPage;
	}


	public Integer getDefaultCompanyNumber() {
		return companyNumber;
	}
	public void setDefaultCompanyNumber(Integer companyNumber) {
		this.companyNumber = companyNumber;
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
	public void setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
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
     * Simulation batch progress file path
     * @return file path
     */
    public String getSimuProgressFilePath() {
        return simuProgressFilePath;
    }
    public void setSimuProgressFilePath(String simuProgressFilePath) {
        this.simuProgressFilePath = simuProgressFilePath;
    }

    /**
     * Service lock file path
     * @return file path
     */
    public String getServiceLockFilePath() {
        return serviceLockFilePath;
    }
    public void setServiceLockFilePath(String serviceLockFilePath) {
        this.serviceLockFilePath = serviceLockFilePath;
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
	public void setPostgresDatasource(String postgresDatasource) {
		this.postgresDatasource = postgresDatasource;
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
	public void setTimers(ArrayList<String> timers) {
		this.timers = timers;
	}


	/////////////////////////////// ReCaptcha config ///////////////////////////////////////////////////
	public boolean isEnableCaptcha() {
		return enableCaptcha;
	}
	public void setEnableCaptcha(boolean enableCaptcha) {
		this.enableCaptcha = enableCaptcha;
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
	public void setLanguageCodes(String languageCodes) {
		this.languageCodes = languageCodes;
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
	public void setCachePages(boolean cachePages) {
		this.cachePages = cachePages;
	}
	public boolean isCachePages_delete() {
		return cachePages_delete;
	}
	public void setCachePages_delete(boolean cachePages_delete) {
		this.cachePages_delete = cachePages_delete;
	}
	public String getDateFormatDto() {
		return dateFormatDto;
	}
	public void setDateFormatDto(String dateFormatDto) {
		this.dateFormatDto = dateFormatDto;
	}
	public String getDateFormatDefault() {
		return dateFormatDefault;
	}
	public void setDateFormatDefault(String dateFormatDefault) {
		this.dateFormatDefault = dateFormatDefault;
	}
	public int getDateFormatMonthDefault() {
        return dateFormatMonthDefault;
    }
    public void setDateFormatMonthDefault(int dateFormatMonthDefault) {
        this.dateFormatMonthDefault = dateFormatMonthDefault;
    }
    public String getDateFormatShort() {
		return dateFormatShort;
	}
	public void setDateFormatShort(String dateFormatShort) {
		this.dateFormatShort = dateFormatShort;
	}
	
	
    
	
}
