'use strict';

angular.module('mod.fix.service', [])

    
    /**
      * Selection
      */
    .factory('fixListRemote', function(acRemote, fixModel) {
        return acRemote.createRemote (fixModel.list);
    })

     /**
      * Report Run
      */
    .factory('fixListRunRemote', function(acRemote, fixModel) {
        return acRemote.createRemote (fixModel.listRun);
    })

    /**
     * Fix Report export
     */
   .factory('fixListExportRemote', function(acRemote, fixModel) {
       return acRemote.createRemote (fixModel.fixListExport);
   })
    
;


