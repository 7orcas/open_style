'use strict';

angular.module('mod.rpt.model', [])

    /**
      * Report model definitions
      */
    .service('rptModel', function($rootScope, acCache, acModel, acGlobal, prepModel) {

    	var self = {};
    	

        /**
         * Return true if category found
         */ 
        self.containsCategory = function(array, cat){
            for (var i=0; i < array.length; i++) {
                if (array[i] === cat){
                    return true;
                }
            }
            return false;
        };


    	/**************************************************************************************
         * Materials report from preparation
         **************************************************************************************/
        self.prepMat = acModel.createModelDef('prepMatRpt', 'rpt/matrpt', 'report.MatRptSql');
        self.prepMat.cacheObjects = false;
        self.prepMat.prepSql      = null;
        self.prepMat.fields       = [{fn:'containsCategory',fx: self.containsCategory},
                                     {fn:'select',          fx: prepModel.select},
                                     {fn:'previous',        fx: prepModel.previous},
                                     {fn:'next',            fx: prepModel.next},
                                     ];
        self.prepMat.dependencies = ['prepMatCategoriesRemote','mdataMaterialTypeRemote','mdataReqtypeRemote'];
             
        self.prepMatExport        = acModel.createModelDef(null, 'rpt/matrpt/export');
        

        /**************************************************************************************
         * Materials report from preparation Run
         **************************************************************************************/
        
        self.prepMatRun = acModel.createModelDef(null, 'rpt/matrpt/run', 'report.MatRptSql');
        
        
        /**************************************************************************************
         * Material Report Categories Logic
         **************************************************************************************/
        self.categoriesMat = acModel.createModelDef(null, 'rpt/matrpt/categories', 'report.CategoriesDto');
        self.categoriesMat.fields = [{fn:'getCategories',   fx: prepModel.getCategories},
                                     {fn:'getCodes',        fx: prepModel.getCodes},
                                     {fn:'getDescriptions', fx: prepModel.getDescriptions},
                                     ];
        

        
        
        return self;
         
    });

     
     
