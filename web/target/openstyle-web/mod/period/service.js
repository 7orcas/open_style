'use strict';

angular.module('mod.period.service', [])

    
    
    /**
      * Period Selection
      */
    .factory('periodRemote', function(acRemote, periodModel) {
        return acRemote.createRemote (periodModel.periodSelection);
    })

    /**
      * Period Plans (previous and next) Selection
      */
    .factory('periodPlansRemote', function(acRemote, periodModel) {
        return acRemote.createRemote (periodModel.periodPlans);
    })
    
    /**
      * Period List
      */
    .factory('periodListRemote', function(acRemote, periodModel) {
        return acRemote.createRemote (periodModel.periodList);
    })
    
    
     /**
      * Period Simulation toggle machine view 
      */
    .factory('periodToggleRemote', function(acRemote, periodModel) {
        return acRemote.createRemote (periodModel.periodToggle);
    })

    /**
      * Period Set Active  
      */
    .factory('periodSetActiveRemote', function(acRemote, periodModel) {
        return acRemote.createRemote (periodModel.periodSetActive);
    })
    
    
    /**
      * Current period number
      */
    .factory('periodCurrentRemote', function(acRemote, periodModel) {
    	return acRemote.createRemote (periodModel.periodCurrent);
    })
    
    /**
      * Move group or instance
      */
    .factory('periodMoveRemote', function(acRemote, periodModel) {
    	return acRemote.createRemote (periodModel.periodMove);
    })    

    
    /**
      * Update period quantities
      */
    .factory('periodUpdateRemote', function(acRemote, periodModel) {
    	return acRemote.createRemote (periodModel.periodUpdate);
    })    
    
    
    /**
      * Export to Spreadsheet
      */
    .factory('periodExportSSRemote', function(acRemote, periodModel) {
        return acRemote.createRemote (periodModel.periodExportSS);
    })

    
    ;


