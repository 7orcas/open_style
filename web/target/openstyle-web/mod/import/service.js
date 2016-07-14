'use strict';

angular.module('mod.import.service', [])

    
     /**
      * Logisoft Import status
      */
    .factory('importLogiRemote', function(acRemote, importModel) {
        return acRemote.createRemote (importModel.importlogi);
    })
        

     /**
      * File Import status
      */
    .factory('importFileRemote', function(acRemote, importModel) {
        return acRemote.createRemote (importModel.importfile);
    })

    
    /**
      * File Import validate
      */
    .factory('importFileValidateRemote', function(acRemote, importModel) {
        return acRemote.createRemote (importModel.importfilevalid);
    })
    
    /**
      * Conflicts  
      */
    .factory('importConflictRemote', function(acRemote, importModel) {
        return acRemote.createRemote (importModel.importconflict);
    })
    
    /**
     * Conflicts export
     */
    .factory('importConflictExportRemote', function(acRemote, importModel) {
    	return acRemote.createRemote (importModel.importconflictExport);
    })

    /**
      * Start Logisoft Data Import 
      */
    .factory('importStartRemote', function(acRemote, importModel) {
        return acRemote.createRemote (importModel.importstart);
    })


    /**
      * Start Logisoft CSV Create 
      */
    .factory('createCsvRemote', function(acRemote, importModel) {
        return acRemote.createRemote (importModel.createcsv);
    })

    
    /**
      * Start Logisoft CSV Import 
      */
    .factory('importCsvRemote', function(acRemote, importModel) {
        return acRemote.createRemote (importModel.importcsv);
    })
    
    
     /**
      * Reset fixes 
      */
    .factory('importResetFixRemote', function(acRemote, importModel) {
        return acRemote.createRemote (importModel.importreset);
    })
    
     /**
      * Last import export
      */
    .factory('mdataImportExportRemote', function(acRemote, importModel) {
        return acRemote.createRemote (importModel.importExport);
    })
    
;


