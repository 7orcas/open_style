'use strict';

angular.module('app.mod.patches.model', [])

    /**
      * Database patches model definitions
      */
    .service('patchesModel', function(acModel) {
        
        var self = {};
        
        self.list = acModel.createModelDef('patches', 'patches99/list', 'app.PatchDto');
        self.cacheObjects = false;
        
        return self;
    });

     
     
