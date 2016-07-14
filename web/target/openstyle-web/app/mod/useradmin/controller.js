 'use strict';

angular.module('app.mod.useradmin.controllers', [])


    /**
     * User Administration list data
     */
    .controller('userAdminCtrl', [
            '$rootScope',
            '$scope',
            'acGlobal',
            'acController',
            'useradminModel',
            'useradminUsersRemote',
             
            function($rootScope, $scope, acGlobal, acController, useradminModel, useradminUsersRemote){
             
                var config = acController.createConfig('userAdminCtrl', useradminModel.userAdmin);
                config.title                = 'UserAdmin';
                config.remotePost           = useradminUsersRemote;
                config.sort_predicate_field = 'Userid';
                acController.configure($scope, config);
                
                $scope.getMessageLink = function(){
                    return 'rest/login/changepassword/message?lang=' + acGlobal.globals().language;
                };
            }
    ]);
