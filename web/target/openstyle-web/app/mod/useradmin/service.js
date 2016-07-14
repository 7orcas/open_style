'use strict';

angular.module('app.mod.useradmin.service', [])

    

     /**
      * User List
      */
    .factory('useradminUsersRemote', function(acRemote, useradminModel) {
        return acRemote.createRemote (useradminModel.userAdmin);
    })
    
    
    
    
;
