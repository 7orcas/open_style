'use strict';

angular.module('mod.simu.model', [])

    /**
      * Plan Simulation model definitions
      */
    .service('simuModel', function($rootScope, acCache, acModel, acGlobal) {

        
        var self = {};
        
        
        /**************************************************************************************
         * Simulation Selection
         **************************************************************************************/
        
        self.simuselection = acModel.createModelDef('simu', 'simu/selection', 'plan.SimuSql');
        
        /**
         * Is the passed in value empty?
         */
		var isEmpty = function (value){
			return value === null || value.length === 0;  
		};
        
        /**
         * Is the current search advanced?
         */
		var isAdvancedSearch = function (){
			return !isEmpty(this.getOrderNr())
			        || !isEmpty(this.getPositionNr())
			        || !isEmpty(this.getStyle())
					|| !isEmpty(this.getVariant())
					|| !isEmpty(this.getMouldCode())
					|| !isEmpty(this.getSize())
					|| !isEmpty(this.getColor())
					|| !isEmpty(this.getDelDateFrom())
					|| !isEmpty(this.getDelDateTo()
					|| this.isDuplicateMould()
					|| this.isConflictMould()
					);
		};
		
		self.simuselection.fields = [{fn:'isAdvancedSearch',fx: isAdvancedSearch}];
        
		self.simulist = acModel.createModelDef(null, 'simu/list', 'plan.SimuListDto');
        
		
        /**************************************************************************************
         * Simulation Run
         **************************************************************************************/
        
        self.simurun = acModel.createModelDef('simu', 'simu/run', 'plan.SimuSql');
        self.simurun.fields = [{fn:'isAdvancedSearch',fx: isAdvancedSearch}];
        
        self.simuLoadPage = acModel.createModelDef(null, 'simu/view', 'plan.SimuSql');
        self.simuLoadPage.fields = [{fn:'isAdvancedSearch',fx: isAdvancedSearch}];
        

        self.config = acModel.createModelDef('simuconfig', 'simu/config/list', 'plan.ConfigDto');
        self.config.fields = [{config:'deleteable'},
                              {config:'selectable'},
                              {listfn:'selectList', fx: acModel.selectList},];
        
        
        /**************************************************************************************
         * Simulation Engine 2 preparation
         **************************************************************************************/
        self.simuengine2prep = acModel.createModelDef('simu', 'simu/engine2prep', 'plan.SimuSql');
        self.simuengine2prep.prepid       = null;
        self.simuengine2prep.planconfigid = null;
        self.simuengine2prep.createnew    = false;
        
        
        /**************************************************************************************
         * Simulation Export
         **************************************************************************************/
        self.simuexportSS      = acModel.createModelDef('simu', 'simu/spreadsheet');
        self.simuexportCsv     = acModel.createModelDef('simu', 'simu/csv');
        self.simuexportCsvDate = acModel.createModelDef('simu', 'simu/csvlastdate');
        
        
        /**************************************************************************************
         * Start Moulds
         **************************************************************************************/
        self.startMould = acModel.createModelDef('startMoulds', 'simu/startmould/list', 'plan.StartMouldDto');
        self.startMould.cacheObjects = false;
        self.startMould.modelSql = 'sql.BaseSql';
        self.startMould.dependencies = ['mdataShiftRemote'];
        
        
        /**************************************************************************************
         * Simulation Updates
         **************************************************************************************/
        self.simuUpdate      = acModel.createModelDef(null, 'simu/update',     null);
        self.simuAddStyle    = acModel.createModelDef(null, 'simu/addStyle',  'plan.StyleAddDto');
        self.simuMoveStyle   = acModel.createModelDef(null, 'simu/moveStyles','plan.StyleMoveDto');
        self.simuAddMould    = acModel.createModelDef(null, 'simu/addMould',  'plan.MouldAddDto');
        self.simuMoveMould   = acModel.createModelDef(null, 'simu/moveMould',  null);
        self.simuMouldCode   = acModel.createModelDef(null, 'simu/mouldcodes', null);
        self.simuDeleteMould = acModel.createModelDef(null, 'simu/deleteMould',null);
        
        /**************************************************************************************
         * Simulation Fix
         **************************************************************************************/
        self.simuFixValid   = acModel.createModelDef(null, 'simu/fixvalid',null);
        self.simuFix        = acModel.createModelDef(null, 'simu/fixaction',null);
        self.simuFixCurrent = acModel.createModelDef(null, 'simu/fixcurrent',null);
        
        
        /**************************************************************************************
         * Simulation Progress
         **************************************************************************************/
        self.simuprogress = acModel.createModelDef('simu', 'simu/progress');
        
        
        /**************************************************************************************
         * Period Simulations
         **************************************************************************************/
        self.periodToggle = acModel.createModelDef(null, 'simu/toggleMachine', null);
        
        
        return self;
         
    });

     
     
