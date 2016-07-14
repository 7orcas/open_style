'use strict';

angular.module('app.mod.langadmin.service', [])

     /**
      * User List
      */
    .factory('langadminRemote', function(acRemote, langadminModel) {
        var remote =  acRemote.createRemote (langadminModel.list);
        
        //Stop remote from configuring result object
        remote.processPut = function(result){
        	//Do nothing
        };
        
        return remote;
    })
    
    
    /**
      * Export to SS
      */
    .factory('langadminExportRemote', function(acRemote, langadminModel) {
        return acRemote.createRemote (langadminModel.exportSS);
    })
    
    
;


