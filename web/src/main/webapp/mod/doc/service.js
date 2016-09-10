'use strict';

angular.module('mod.doc.service', [])

    
     /**
      * Documentation
      */
    .factory('docRemote', function(acRemote, docModel) {
        return acRemote.createRemote (docModel.doc);
    })
    
;


