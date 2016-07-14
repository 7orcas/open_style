'use strict';

angular.module('mod.simu.service', [])

    
     /**
      * Selection
      */
    .factory('simuRemote', function(acRemote, simuModel) {
        return acRemote.createRemote (simuModel.simuselection);
    })
        
     /**
      * Simulation List
      */
    .factory('simuListRemote', function(acRemote, simuModel) {
        return acRemote.createRemote (simuModel.simulist);
    })
    
     /**
      * Run
      */
    .factory('simuRunRemote', function(acRemote, simuModel) {
        return acRemote.createRemote (simuModel.simurun);
    })

    /**
      * Simulation Engine 2 preparation
      */
    .factory('simuEngine2PrepRemote', function(acRemote, simuModel) {
        return acRemote.createRemote (simuModel.simuengine2prep);
    })
    
    
     /**
      * Load page
      */
    .factory('simuLoadPageRemote', function(acRemote, simuModel) {
        return acRemote.createRemote (simuModel.simuLoadPage);
    })
    
    
    /**
      * Export to Spreadsheet
      */
    .factory('simuExportSSRemote', function(acRemote, simuModel) {
        return acRemote.createRemote (simuModel.simuexportSS);
    })

    /**
      * Export to CSV
      */
    .factory('simuExportCsvRemote', function(acRemote, simuModel) {
        return acRemote.createRemote (simuModel.simuexportCsv);
    })
    
    /**
      * CSV last date
      */
    .factory('simuExportCsvDateRemote', function(acRemote, simuModel) {
        return acRemote.createRemote (simuModel.simuexportCsvDate);
    })
    
    
     /**
      * Plan configurations
      */
    .factory('simuConfigRemote', function(acRemote, simuModel) {
        return acRemote.createRemote (simuModel.config);
    })
    
    
    /**
      * Plan start moulds
      */
    .factory('simuStartMouldRemote', function(acRemote, simuModel) {
        return acRemote.createRemote (simuModel.startMould);
    })
    
    
     /**
      * Update simulation(job) fields
      */
    .factory('simuUpdateRemote', function(acRemote, simuModel) {
    	return acRemote.createRemote (simuModel.simuUpdate);
    })    
    
    
     /**
      * Add styles to simulation mould
      */
    .factory('simuAddStyleRemote', function(acRemote, simuModel) {
    	return acRemote.createRemote (simuModel.simuAddStyle);
    })    

    /**
      * Move styles to another simulation mould
      */
    .factory('simuMoveStyleRemote', function(acRemote, simuModel) {
    	return acRemote.createRemote (simuModel.simuMoveStyle);
    })    
    
    
    /**
      * Add mould to simulation 
      */
    .factory('simuAddMouldRemote', function(acRemote, simuModel) {
    	return acRemote.createRemote (simuModel.simuAddMould);
    })    
    

     /**
      * Move simulation moulds
      */
    .factory('simuMoveMouldRemote', function(acRemote, simuModel) {
    	return acRemote.createRemote (simuModel.simuMoveMould);
    })    

    
     /**
      * Delete simulation moulds
      */
    .factory('simuDeleteMouldRemote', function(acRemote, simuModel) {
    	return acRemote.createRemote (simuModel.simuDeleteMould);
    })    
    
    /**
      * Get mould codes for instance ids
      */
    .factory('simuMouldCodeRemote', function(acRemote, simuModel) {
    	return acRemote.createRemote (simuModel.simuMouldCode);
    })    
    
    
    /**
      * Valid pre-fix validation process
      */
    .factory('simuFixValidateRemote', function(acRemote, simuModel) {
    	return acRemote.createRemote (simuModel.simuFixValid);
    })
    
    
    /**
      * Valid fix station(s) process
      */
    .factory('simuFixRemote', function(acRemote, simuModel) {
    	return acRemote.createRemote (simuModel.simuFix);
    })
    
    /**
      * Current fix number
      */
    .factory('simuFixCurrentRemote', function(acRemote, simuModel) {
    	return acRemote.createRemote (simuModel.simuFixCurrent);
    })
    
    
     /**
      * Simulation progress 
      */
    .factory('simuProgressRemote', function(acRemote, simuModel) {
        return acRemote.createRemote (simuModel.simuprogress);
    })
    
    
    /**
      * Hide/show a period machine 
      */
    .factory('simuToggleRemote', function(acRemote, simuModel) {
        return acRemote.createRemote (simuModel.periodToggle);
    })
    
    
    ;


