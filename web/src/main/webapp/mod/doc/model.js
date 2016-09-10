'use strict';

angular.module('mod.doc.model', [])

    /**
      * Documentation model definitions
      */
    .service('docModel', function($rootScope, acCache, acModel, acGlobal) {

        
        
        var self = {};
        
        
        self.doc = acModel.createModelDef('showDoc', 'doc/selection', 'docu.DocumentCnt');
        
        
        
        
        
        return self;
         
    });

     
     
