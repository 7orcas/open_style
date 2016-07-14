'use strict';

angular.module('mod.period.model', [])

    /**
      * Period Plan model definitions
      */
    .service('periodModel', function($rootScope, acCache, acModel, acGlobal) {

        
        var self = {};
                
        
        self.periodSelection = acModel.createModelDef('period', 'period/selection', 'plan.period.PeriodSql');
        self.periodPlans     = acModel.createModelDef(null, 'period/plans',         'plan.period.PeriodSql');
        self.periodList      = acModel.createModelDef(null, 'period/list',          'plan.period.PeriodListDto');
        self.periodToggle    = acModel.createModelDef(null, 'period/toggleMachine', null);
        self.periodSetActive = acModel.createModelDef(null, 'period/active',        'plan.period.PeriodActiveDto');
        self.periodMove      = acModel.createModelDef(null, 'period/move',          null);
        self.periodUpdate    = acModel.createModelDef(null, 'period/update',        null);
        self.periodCurrent   = acModel.createModelDef(null, 'period/current',       null);
        self.periodExportSS  = acModel.createModelDef(null, 'period/spreadsheet',   null);
        
        return self;
         
    });

     
     
