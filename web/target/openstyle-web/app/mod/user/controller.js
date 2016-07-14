'use strict';

angular.module('app.mod.user.controllers', [])


    /**
     * User Change Password
     */
    .controller('userChangePasswordCtrl', [
            '$rootScope',
            '$scope', 
            '$state',
            'acGlobal',
            'acController',
            'userModel',
            'userChangePasswordRemote',
             
            function($rootScope, $scope, $state, acGlobal, acController, userModel, userChangePasswordRemote){
             
                var config = acController.createConfig('userChangePasswordCtrl', userModel.changePassword);
                config.title                = 'PassChange';
                config.remotePost           = userChangePasswordRemote;
                acController.configure($scope, config);
                
                /******************************
                 * Specific controller methods
                 ******************************/
                $scope.record = $scope.cacheObj;
                
                var setRequired = function(id){
                    var el = document.getElementById(id);
                    el.setAttribute('required', '');
                };
                
                $scope.changePassword = function(){

                    var r = $scope.record;
                    var test = true;
                    if (r.getPassword() === null || r.getPassword().length === 0){
                        test = false;
                        setRequired('inputPass');
                    }
                    if (r.getPasswordNew() === null || r.getPasswordNew().length === 0){
                        test = false;
                        setRequired('inputPassNew');
                    }
                    if (r.getPasswordConf() === null || r.getPasswordConf().length === 0){
                        test = false;
                        setRequired('inputPassConf');
                    }
                    
                    if (!test){
                        return;
                    }
                    
                    userChangePasswordRemote.queryForce(
                            {
                                ts1: md5(r.getPassword()), 
                                ts2: r.getPasswordNew(), 
                                ts3: r.getPasswordConf()
                            },
                            function(result){
                                $state.go('clearPage');
                                $scope.displayDialog('PassChange', 'PassChanged');
                            },
                            function(result) { //error callback
                                if (result.isReturnDto()){
                                    $scope.displayErrorDialog('PassChange', result.getMessage());
                                }
                                //Damn, uncaught error!
                                else{
                                    $scope.displayErrorUnknown();
                                }
                            });
                };
                
                $scope.getMessageLink = function(){
                    return 'rest/login/changepassword/message?lang=' + acGlobal.globals().language;
                };
                
                
            }
    ])
