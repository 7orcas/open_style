'use strict';

/**
 * Log-in to server object.<p> 
 * 
 * This object is used to call the server and request a log in. If successful a 
 * a $window.location.href value for the main page to the application is returned.<p>
 * 
 * Note: <b>This</b> file is tightly coupled to BaseLogin.java.<p> 
 * 
 * [License]
 * @author John Stewart
 */


angular.module('app.login.object_login', ['ngResource','app.login.service_login'])

	/**
      * Create 'User' object with utility/convenience functions
      */
	.service('alLoginObj', function($rootScope, loginRemote, publicKeyRemote) {
		var obj = {};
		obj.response     = null;
		obj.message      = null;
		obj.showCaptcha  = false;
		obj.captchaPK    = null;
		obj.locationHref = null;
				
		//Call server to request a login 
		obj.loginUser = function(company, userid, pass, lang, langReq, challenge, response, callback) {
					
		    if (!angular.isDefined(company)){
				company = 1; //DEFAULT_COMPANY
			}

		    var params = {
		    		fgh: userid, 
				    ts1: md5(pass+"1"), 
				    ts2: md5(pass+"2"), 
				    ts3: md5(pass), 
				    ts4: md5(pass+"4"),
				    lan: lang,
				    lanr: langReq,
				    cha:challenge, 
				    res:response};
		    
		    if (!testNaN(company)){
		    	params.forx = company;
		    }
				    
			loginRemote.get(params, 
				    
				    function (result){
				
						if (angular.isDefined(callback)){
							callback();
		    			}
						
						//Successful login
						if (result.rs === 0) {
							obj.locationHref = result.rf;
							$rootScope.$broadcast('event:loginSuccess');
						} 
						
						//Unsuccessful login
						else {
							obj.response = result.rs;
							obj.message = result.m;
							obj.captchaPK = result.cp;
							obj.showCaptcha = (result.ic !== null &&  result.ic === true);
							$rootScope.$broadcast('event:loginFailed');
						}
			        }
		     );
		};  
		   
		obj.captchaPublicKey = function(callback) {
			publicKeyRemote.get(function (result){
    	        obj.captchaPK = result.cp;
				if (angular.isDefined(callback)){
					callback();
    			}
	        });
		};  

		obj.getCaptchaPublicKey = function(){
			return obj.captchaPK;
		};

		
		obj.getLocationHref = function() {
		    return this.locationHref;
	    };
		    
		obj.getMessage = function() {
		    return this.message;
	    };
			
	    obj.isShowCaptcha = function(){
	    	return this.showCaptcha;
	    };
		
	   	    
		return obj;
		
	})
	
	
	
;