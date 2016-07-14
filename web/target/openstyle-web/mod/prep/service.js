'use strict';

angular.module('mod.prep.service', [])

    
     /**
      * Preparation Categories
      */
    .factory('prepCategoriesRemote', function(acRemote, prepModel) {
        return acRemote.createRemote (prepModel.categories);
    })
      
    /**
      * Preparation Selection
      */
    .factory('prepRemote', function(acRemote, prepModel) {
        return acRemote.createRemote (prepModel.prepselection);
    })

     /**
      * Preparation Run
      */
    .factory('prepRunRemote', function(acRemote, prepModel) {
        return acRemote.createRemote (prepModel.preprun);
    })

    /**
      * Export to Spreadsheet
      */
    .factory('prepExportSSRemote', function(acRemote, prepModel) {
        return acRemote.createRemote (prepModel.prepexportSS);
    })
    
    
     /**
      * Update preparation fields
      */
    .factory('prepUpdateRemote', function(acRemote, prepModel) {
        var remote = acRemote.createRemote (prepModel.prepUpdate);
        remote.showLoading = false;
        return remote;
    })
    
    
    /**
      * Sum preparation fields
      */
    .factory('prepSumRemote', function(acRemote, prepModel) {
        var remote = acRemote.createRemote (prepModel.prepSum);
        remote.showLoading = false;
        return remote;
    })
    
     /**
      * Start a period plan simulation 
      */
    .factory('prepPeriodRemote', function(acRemote, prepModel) {
    	return acRemote.createRemote (prepModel.prepPeriod);
    })    


     /**
      * Validate a period plan start DTO 
      */
    .factory('prepPeriodValidateRemote', function(acRemote, prepModel) {
    	return acRemote.createRemote (prepModel.prepPeriodValidate);
    })    

    
    
;


