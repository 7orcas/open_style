'use strict';

angular.module('app.mod.patches.service', [])
    
     /**
      * List
      */
    .factory('patchesRemote', function(acRemote, patchesModel) {
        return acRemote.createRemote (patchesModel.list);
    })
    
;


