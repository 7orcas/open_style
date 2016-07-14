'use strict';

/**
 * @module acUserObj
 * @version app.common.userobject
 * 
 * @description
 * Settings for a logged in user.<p> 
 * 
 * Once a user has successfully logged in (refer to 'Browser Login Process'), then this object is 
 * initialized via a call to the server.
 * 
 * [License]
 * @author John Stewart
 */
angular.module('app.common.userobject', [])

    /**
      * Create 'User' object with utility/convenience functions
      */
    .service('acUserObj', function($rootScope, initialiseRemote, logoutRemote, acGlobal) {
        var obj = {};
        obj.admin   = false; //default
        obj.service = false; //default
        obj.logged_in;
                
        /**
         * Initialize <b>this</b> object with the result from a call to the server.
         * @param server return object
         */
        obj.initialize = function(callback) {
            
            initialiseRemote.get(function (result){
                
                if (result.rs !== 0){
                    $rootScope.$broadcast('event:loginFailed');
                    return;
                }
                
                obj.logged_in = true;
                
                acGlobal.globals().site                = result.s;
                acGlobal.globals().company             = result.nr;
                acGlobal.globals().testCompany         = result.ct;
                acGlobal.globals().companyCode         = result.cc;
                acGlobal.globals().language            = result.lg;
                acGlobal.globals().userid              = result.uid;
                acGlobal.globals().heartbeatTimer      = result.ht;
                acGlobal.globals().sessionTimeoutTimer = result.st;
                acGlobal.globals().helpUrlRoot         = result.hr;
                acGlobal.globals().decimalSymbol       = result.ds;
                acGlobal.globals().timezoneOffset      = result.tz;
                acGlobal.globals().dateFormat          = result.df;
                acGlobal.globals().dateFormatMonth     = result.dfn;
                acGlobal.globals().indexPage           = result.rc;
                acGlobal.globals().remoteUrlPrefix     = result.ru;
                
                //Set no repeat codes 
                //TODO split for comma delimited codes
                if (angular.isDefined(result.nc)
                		&& result.nc.length > 0){
                	$rootScope.norepeat = {};
                	$rootScope.norepeat[result.nc] = true;
                }
                
                
                var fn = function(df){
                	var a = [];
                	if (df !== null){
                    	for (var i=0;i<df.length;i++){
                    		var x = df[i];
                    		if (x !== null){
                    			a.push(x);
                    		}
                    	}
                    }
                	return a;
                };
                
                acGlobal.globals().daysShort   = fn(result.dfd);
                acGlobal.globals().monthsShort = fn(result.dfs);
                acGlobal.globals().months      = fn(result.dfm);
                
                
                if (result.ad !== null){
                    obj.admin = result.ad;
                }
                else{
                    obj.admin = false;
                }

                if (result.sr !== null){
                    obj.service = result.sr;
                }
                else{
                    obj.service = false;
                }

                if (angular.isDefined(callback)){
                    callback(result);
                }
            });
            
        };
        
        /**
         * Logout user. The resulting actions are a call to the server and redirection to the 
         * application login screen.
         */
        obj.logout = function() {
            this.logged_in = false;
            
            logoutRemote.get(function (result){
                //Do Nothing
            });
            $rootScope.$broadcast('event:logout');
        };

        /**
         * Return true if <b>this</b> user is logged in.
         */
        obj.isLogin = function() {
            return this.logged_in;
        };

        /**
         * Return true if <b>this</b> user has an Administration status. <b>This</b> status allows the user
         * certain privileges (depending on this is coded in each module).
         */
        obj.isAdmin = function() {
            return this.admin;
        };
        
        /**
         * Return true if <b>this</b> user has an Service status. <b>This</b> status allows the user
         * all privileges (including service only functions).
         */
        obj.isService = function() {
            return this.service;
        };

        return obj;
    })
    
    
;