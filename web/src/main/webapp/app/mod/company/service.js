'use strict';

angular.module('app.mod.company.service', [])

    
     /**
      * List
      */
    .factory('companyadminRemote', function(acRemote, companyModel) {
        return acRemote.createRemote (companyModel.list);
    })
    
;


