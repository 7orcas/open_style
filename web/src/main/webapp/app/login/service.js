'use strict';

/**
 * @version app.login.service_init
 * 
 * @description
 * Services for user initialization and log out.<p>
 * 
 * See main documentation: Browser Login Process<p>
 * 
 * [License]
 * @author John Stewart
 */
angular.module('app.login.service_init', ['ngResource'])


    /**
      * Initialize a user (after they have been logged in)
      * Thanks to http://www.w3schools.com/jsref/jsref_gettimezoneoffset.asp 
      */
    .factory('initialiseRemote', function($resource) {
        
        var res = $resource('rest/login/initialise', {}, 
                {_get: {method:'GET', 
                        params: {tzo: (new Date()).getTimezoneOffset()},
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


    /**
      * Logout of application
      */
    .factory('logoutRemote', function($resource) {
        
        var res = $resource('rest/login/exit', {}, 
                {_get: {method:'GET', 
                        params: {},
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