'use strict';

angular.module('app.mod.useradmin.model', [])

    /**
      * User Administration model definitions
      */
    .service('useradminModel', function(acModel) {
        
        var self = {};
             
        
        /**
         * ID fields are readonly and don't have a setter method, therefore need to override this rule
         */
        var getPassword_f = function(){
            if (this.getPassword() !== null){
                return this.getPassword();
            }
            return 'xxx';
        };      
        

        self.userAdmin = acModel.createModelDef('userAdmin', 'useradminb5c0b18/userlist', 'useradmin.UserDto');
        self.userAdmin.cacheObjects = false;
        self.userAdmin.fields = [{config:'deleteable'},
                                 {fn:'getPassword_f',    fx: getPassword_f},];
        
        return self;
    });

     
 