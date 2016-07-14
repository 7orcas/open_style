'use strict';

angular.module('mod.rpt.service', [])

    
    /**
      * Preparation Material Report Selection
      */
    .factory('rptPrepMatRemote', function(acRemote, rptModel) {
        return acRemote.createRemote (rptModel.prepMat);
    })

     /**
      * Preparation Material Report Run
      */
    .factory('rptPrepMatRunRemote', function(acRemote, rptModel) {
        return acRemote.createRemote (rptModel.prepMatRun);
    })
    
    
    /**
      * Material Report Preparation Categories
      */
    .factory('prepMatCategoriesRemote', function(acRemote, rptModel) {
        return acRemote.createRemote (rptModel.categoriesMat);
    })
    
    /**
     * Material Report export
     */
   .factory('rptPrepMatExportRemote', function(acRemote, rptModel) {
       return acRemote.createRemote (rptModel.prepMatExport);
   })
    
;


