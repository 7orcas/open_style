'use strict';

angular.module('mod.export.model', [])

    /**
      * Model definitions
      */
    .service('exportModel', function($rootScope, acCache, acModel) {
        
    	var self = {};
    	
        //View definitions (form entities.plan.SimuI)
        self.VIEW_DETAIL        = 0;
        self.VIEW_MOULD_CHANGES = 1;
        self.VIEW_OVERVIEW      = 2;
        self.VIEW_CYCLES        = 3;

        //Export file format definitions (form entities.export.ExportI)
        self.EXP_PDF            = 1;
        self.EXP_SS             = 2;
        self.EXP_CSV            = 3;

        
    	
    	/**************************************************************************************
         * Selection
         **************************************************************************************/
        
        self.exportselection = acModel.createModelDef('exportFix', 'export/selection', 'export.ExportSql');
        self.exportselection.dependencies = ['mdataPlantRemote'];
        self.exportselection.cacheObjects = false;
        
    	
        /**************************************************************************************
         * Export
         **************************************************************************************/
        self.exportfixed = acModel.createModelDef('exportFix', 'export/simu');
        
        
    	return self;
         
    });

     
     
