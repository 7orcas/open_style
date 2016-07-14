'use strict';

angular.module('mod.export.service', [])

     /**
      * Selection
      */
    .factory('exportRemote', function(acRemote, exportModel) {
        return acRemote.createRemote (exportModel.exportselection);
    })

    
    /**
      * Export
      */
    .factory('exportFixed', function(acRemote, exportModel) {
        return acRemote.createRemote (exportModel.exportfixed);
    })
    
;


