'use strict';


/**
 * Login application.
 * 
 * This is a stand-alone application. Once the user has successfully logged in, the browser is
 * directed to the main page. The controller for the main page must then request a user
 * initialization to obtain all user presets and default values.
 * 
 * 
 * [License]
 * @author John Stewart
 */

//application level module
var App = angular.module('login', [        
                                   
       'ngRoute',                  //angular-route-1.2.16.min.js
       'ngResource',               //angular-resource-1.2.16.min.js
   	
       'app.login.controller',
       'app.login.object_login',
       
       'app.lang.service',
       'app.common.global',
	] 
	
);


//Dummy provider for remote.js
App.provider ('$state', function(){
    this.$get = function() {
        return {};
    };
});

//Dummy provider for remote.js
App.provider ('acDialogs', function(){
    this.$get = function() {
        return {};
    };
});

//Dummy provider for remote.js
App.provider ('acCache', function(){
    this.$get = function() {
        return {};
    };
});


/**
 * Focus on an element directive.<p>
 * see http://stackoverflow.com/questions/14833326/how-to-set-focus-in-angularjs
 */
App.directive('focusOn', function() {
    return function(scope, elem, attr) {
	    scope.$on('focusOn', function(e, name) {
	        if(name === attr.focusOn) {
	            elem[0].focus();
	        }
	    });
	};
});

/**
 * Focus on an element function.<p>
 * see http://stackoverflow.com/questions/14833326/how-to-set-focus-in-angularjs
 */
App.factory('focus', function ($rootScope, $timeout) {
	return function(name) {
	    $timeout(function (){
	        $rootScope.$broadcast('focusOn', name);
	    }, 50);
	}
});




