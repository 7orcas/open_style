'use strict';

/**
 * @version app.login.service_login
 * 
 * @description
 * Services for user to log into the server.<p>
 * 
 * See main documentation: Browser Login Process<p>
 * 
 * [License]
 * @author John Stewart
 */
angular.module('app.login.service_login', ['ngResource'])

	
	 /**
      * Call server to request a login 
      */
	.factory('loginRemote', function($resource) {
		
		var res = $resource('rest/login', {}, 
				{_get: {method:'GET', 
					    params: {forx:'@forx', 
					    	     fgh: '@fgh', 
					    	     ts1: '@ts1', 
					    	     ts2: '@ts2', 
					    	     ts3: '@ts3', 
					    	     ts4: '@ts4', 
					    	     lan: '@lang',
					    	     cha: '@chall', 
								 res: '@res'
					    },
					    isArray:false}
			    });
		
		res.get = function(params, callback) {
            var result = res._get(params, function (){
            	callback(result); 
            });
		};
    		
		return res;
	})
	

	/**
      * Get <code>ReCaptcha</code> public key
      */
	.factory('publicKeyRemote', function($resource) {
		
		var res = $resource('rest/login/publickey', {}, 
				{_get: {method:'GET', 
					    params: {test: true},
					    isArray:false}
			    });
		
		res.get = function(params, callback) {
            var result = res._get(params, function (){
            	if (angular.isFunction(callback)){
            		callback(result); 
            	}
            });
		};
    		
		return res;
	})
	

;