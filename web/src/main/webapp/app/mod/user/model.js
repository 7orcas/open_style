'use strict';

angular.module('app.mod.user.model', [])

    /**
      * User model definitions
      */
    .service('userModel', function() {
        
        var self = {};
             
        
        self.changePassword = {
            url:          'login/changepassword',
            state:        'userChangePassword',
            model:        'login.ChangePasswordDto',
            cacheObjects: true,
            remote:       null
        };
               
        
        return self;
    })

     
     
