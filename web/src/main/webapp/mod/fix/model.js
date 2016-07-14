'use strict';

angular.module('mod.fix.model', [])

    /**
      * Fix model definitions
      */
    .service('fixModel', function($rootScope, acCache, acModel, acGlobal) {

    	var self = {};
    	
//
//        /**
//         * Return true if category found
//         */ 
//        self.containsCategory = function(array, cat){
//            for (var i=0; i < array.length; i++) {
//                if (array[i] === cat){
//                    return true;
//                }
//            }
//            return false;
//        };


    	/**************************************************************************************
         * Fix List Selection
         **************************************************************************************/
        self.list = acModel.createModelDef('fixList', 'fix/selection', 'fix.FixSql');
        self.list.cacheObjects = false;
                
//        self.list.fields       = [{fn:'containsCategory',fx: self.containsCategory},
//                                 {fn:'select',          fx: prepModel.select},
//                                 {fn:'previous',        fx: prepModel.previous},
//                                 {fn:'next',            fx: prepModel.next},
//                                ];
//        self.prepMat.dependencies = ['prepMatCategoriesRemote','mdataMaterialTypeRemote','mdataReqtypeRemote'];
             
        self.fixListExport     = acModel.createModelDef(null, 'fix/list/export');
        

        /**************************************************************************************
         * Fix List Report Run
         **************************************************************************************/
        
        self.listRun = acModel.createModelDef(null, 'fix/list/run', 'fix.FixSql');
        
        
//        /**************************************************************************************
//         * Material Report Categories Logic
//         **************************************************************************************/
//        self.categoriesMat = acModel.createModelDef(null, 'rpt/matrpt/categories', 'report.CategoriesDto');
//        self.categoriesMat.fields = [{fn:'getCategories',   fx: prepModel.getCategories},
//                                     {fn:'getCodes',        fx: prepModel.getCodes},
//                                     {fn:'getDescriptions', fx: prepModel.getDescriptions},
//                                     ];
//        

        
        
        return self;
         
    });

     
     
