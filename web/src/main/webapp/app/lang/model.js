'use strict';

/**
 * @version app.lang.model
 * 
 * @description
 * Application language Module.
 */
angular.module('app.lang.model', [])

     /**
      * Language Module definition
      */
     .service('langModel', function(acModel) {
    	 return acModel.configureModel( 
    			 [{field:'Id',           dto:'i', min:1, max: 20,  edit:'new'},
			      {field:'Text',         dto:'t', min:1, max: 50, edit:true},
			      {config:'setters',     value:false},
			      {config:'mGetters',    value:false}]);
     })
     
;

    
    