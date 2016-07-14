'use strict';

angular.module('app.mod.user.service', [])

    

     /**
      * User List
      */
    .factory('userChangePasswordRemote', function(acRemote, userModel) {
        return acRemote.createRemote (userModel.changePassword);
    })
    
    
    
    
;


