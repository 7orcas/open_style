'use strict';

angular.module('app.common.dialogs', [])

    /**
     * @doc module
     * @name app.common.dialogs
     * @description 
     * 
     * Base (common) dialog functions.<p> 
     * 
     * The design goals of <b>this</b> module are:<ol>
     *     <li>provide a consistent controller behavior within the client application.</li><br>
     *     <li>controllers have a lot of boiler plate code which should be written only once, ie here. Hence this goal is to minimize 
     *         repeated code.</li><br> 
     * </ol><p>
     * 
     * Design notes:<ul>
     *     <li></li><br>
     * </ul>
     * 
     * Module dependencies:<ul>
     *     <li>app.lang.service</li>
     *     <li>app.common.global</li>
     *     <li>app.common.userobject</li>
     * </ul>
     * 
     * TODO: IMPLEMENT THIS MODULE
     * - refactor this module to have only one controller
     * - pass in a control object with various presets
     * - use this module to create default preset
     * - unit test
     * 
     * [License]
     * @author John Stewart
     */
     .service('acBaseDialog', function($rootScope, $modal, $timeout, acUserObj, acGlobal, aLang) { //acDialogs
         
    	//DELETE var currentDialog = null;
         //DELETE var dontCloseOpenDialogFired = false;
         
         var self = {
              
                 
                 /**
                  * Open the dialog 
                  * @param dialog: default attributes 
                  * @param config: overriding attributes
                  */
                openDialog: function(dialog, config){
                    
                        config = self.configDialog(dialog, config);
                 
                        var windowClass = 'modal ';
                        if (angular.isDefined(config.windowClass)){
                        	windowClass += config.windowClass;
                        }
                        
                        var d = $modal.open({
                                   templateUrl:config.url,
                                   controller:config.controller,
                                   backdrop: true,
                                   windowClass: windowClass,
                                   resolve: {config: function () {return config;}}
                        });
                                   
                        config.dialogInstance = d;
                        config.close = function(message){
                            if (!angular.isDefined(message)){
                                message = 'ok';
                            }
                            d.close(message);
                        }
                      //DELETE currentDialog = d;
                        return d;
                },
                 
                /**
                 * Set up the dialog configuration via adding default values to the passed in 'config' object.
                 * @param dialog: default attributes (eg title, controller, etc)
                 * @param config: overriding attributes (eg title maybe changed from basic). Also contains callback. Note: if
                 *                a function is passed then this is considered as the callback with no other config attributes.
                 */
                configDialog: function(dialog, config){
                            
                            //Create config if null                 
                            if (!angular.isDefined(config) || config === null){
                                config = {}; 
                            }
                            
                            //If config is a function then this is used as the callback.
                            if (angular.isFunction(config)){
                                var callback = config;
                                config = {};
                                config.callback = callback; 
                            }
                            
                            
                            //Dialog attributes must be explicit, otherwise they are false 
                            var updateable     = initParameter (dialog.updateable, false);
                            var multiSelection = initParameter (dialog.multiSelection, false);
                            var newable        = initParameter (dialog.newable, updateable && acUserObj.isAdmin());
                            var deleteable     = initParameter (dialog.deleteable, updateable && acUserObj.isAdmin());
                            var editable       = initParameter (dialog.editable, updateable && acUserObj.isAdmin());
                            
                            //test which parameter to use
                            var getParam = function (param1, param2){
                                 if (!angular.isDefined(param1) || param1 === null){
                                     return param2;
                                 }
                                 return param1;
                            };
                            
                            //Config attributes are implicit, otherwise they use dialog settings
                            config.multiSelection = initParameter (config.multiSelection, multiSelection);
                            config.newable        = initParameter (config.newable, newable);
                            config.deleteable     = initParameter (config.deleteable, deleteable);
                            config.editable       = initParameter (config.editable, editable);
                            
                            config.title          = aLang.label(getParam (dialog.title, ""));
                            config.clazz          = getParam (config.clazz, dialog.clazz);
                            config.controller     = getParam (config.controller, dialog.controller);
                            
                            //Get relevant url
                            if (config.updateable){
                                config.url = getParam (config.url, dialog.url); 
                            }
                            else{
                                var url = config.url;
                                config.url = getParam (config.url_ro, dialog.url_ro); 
                                if (!angular.isDefined(config.url) || config.url === null){ //one last try
                                    config.url = getParam (url, dialog.url);
                                }
                            }
                            
                            
                            //Copy across remaining default dialog parameters
                            for(var p in dialog){
                                if (!angular.isDefined(config[p])){
                                    config[p] = dialog[p]; 
                                }
                            }

                            return config;
                    },
                    
             
                    
                /**
                 * Generic Dialog Utility functions
                 */
                setGenericDialog: function ($scope, config, model){
                            
                        if (angular.isDefined(model) && model != null){
                            $scope.model = model;
                            $scope.dto = model.createDto();
                        }
                        
                        $scope.header = self.app_partials + 'dialog_header.html';
                        $scope.footer = self.app_partials + 'dialog_footer.html';

                        //Validation message setup. This is used by <code>remote</code> module when a server call
                        //results in a <code>RETURN_STATUS_INVALID</code>. 
                        $scope.validationMessage = null;
                        $scope.setValidationMessage = function(message){
                            $scope.validationMessage = message;
                        };
                        
                        $scope.$on('$locationChangeStart', function(ev) {
                            ev.preventDefault();
                            window.history.go(1);
                        });
                        
                        $scope.label = function(id){
                            return aLang.label(id);
                        };
                        
                        $scope.dismiss = function(){
                            if (angular.isDefined(config) && config != null){
                                config.close('cancel');
                            }
                          //DELETE currentDialog = null;
                        };
                        
                        $scope.title = function(){
                            return aLang.label(config.title);
                        };
                        
                        $scope.actionButtonLabel = function(){
                            return aLang.label('Accept');
                        };
                        
                        $scope.dismissButtonLabel = function(){
                            return aLang.label('Cancel');
                        };
                        
                        $scope.disableFooterButtons = function (){
                            var b = angular.element(".dialog-footer-btn");
                            b.attr('disabled', 'disabled');
                        };
                        
                        $scope.enableFooterButtons = function (){
                            var b = angular.element(".dialog-footer-btn");
                            b.attr('disabled', false);
                        };
                        
                        $scope.isAdmin = function(){
                            return acUserObj.isAdmin();
                        };
                        
                    },
                    
         
         };
        
         self.app_partials = 'template/dialog/';
            
         
         // ------ private functions ------
         var initParameter = function (param, defaultValue){
             if (!angular.isDefined(param) || param === null){
                 return defaultValue;
             }
             return param ;
         };
         
         
         return self;
     }) 


     
     // Application dialogs ----------------------------------------------------------------------------------------------------------

     /**
      * Application (general) dialog methods
      */
     .service('acDialogs', function($modal, $rootScope, $window) {
         
         var dialog_url_prefix = 'template/dialog/';
         
         var self = {
             
              openDialog: function (url, controller, config){
            	  		if (!angular.isDefined(config) || config === null){
            	  			config = {};
            	  		}
            	  
            	  		var params = {
                                templateUrl:url,
                                controller:controller,
                                backdrop: false,
                                resolve: {config: function () {return config;}}
                            };
            	  		
            	  		if (angular.isDefined(config.windowClass)){
            	  			params.windowClass = config.windowClass;
            	  		}
            	  		
                        var d = $modal.open(params);

                        config.dialogInstance = d;
                        config.close = function(message){
                            if (!angular.isDefined(message)){
                                message = 'ok';
                            }
                            d.close(message);
                        };
                        return d;
                 }, 
                 
              error: function (callback){
                        var config = {};
                        config.callback = callback
                        return self.openDialog(dialog_url_prefix + 'error.html', 'acErrorDialogCtrl', config); 
                },
                
              resetChanges: function (callback){
                        var config = {};
                        config.callback = callback
                        return self.openDialog(dialog_url_prefix + 'reset_changes.html', 'acResetChangesDialogCtrl', config); 
                },
                
              message: function (title, message, callback){
                       var config = {};
                       config.title = title;
                       config.message = message;
                       config.callback = callback;
                       
                       return self.openDialog(dialog_url_prefix + 'message.html', 'acMessageDialogCtrl', config); 
                },
                
                
              timeout: function (){
            	    try{
            	    	var config = {};
            	    	return self.openDialog(dialog_url_prefix + 'message.html', 'acTimeoutDialogCtrl', config); 
            	    } catch(err){
            	    	$rootScope.relogin = false;
                        $window.location.href = acGlobal.globals().indexPage;
            	    }
            	    
                 },
                
              errorMessage: function (config){
	            	return self.openDialog(dialog_url_prefix + 'error_message.html', 'acMessageDialogCtrl', config); 
                 },
                
              yesNo: function (title, message, yesCallback, noCallback){
                       var config = {};
                       config.title = title;
                       config.message = message;
                       config.yesCallback = yesCallback;
                       config.noCallback = noCallback;
                       return self.openDialog(dialog_url_prefix + 'yes_no.html', 'acYesNoDialogCtrl', config); 
                },
         };
                
         return self;
                
     }) 
             
     
    /**
     * Open error dialog
     */
    .controller('acErrorDialogCtrl', [
             '$scope', 
             'acGlobal',
             'acBaseDialog',
             'aLang',
             'config',
             
            function($scope, acGlobal, acBaseDialog, aLang, config){
        
                acBaseDialog.setGenericDialog($scope); 
                 
                $scope.label = function(id){
                    return aLang.label(id);
                };
                 
                $scope.title = function (){
                    return $scope.label('ErrUnknown');
                };
        
                $scope.referAdmin = function (){
                    return $scope.label('SeeAdmin');
                };
                
                $scope.details = function (){
                    return $scope.label('Detail') + ': ' + $scope.label(acGlobal.globals().systemErr);
                };
                
                $scope.dismiss = function(){
                    $scope.enableFooterButtons();
                    config.close('cancel');
                };
            }
     ])
    
    
    /**
     * Open Reset Changes (ie Cancel) dialog
     */
    .controller('acResetChangesDialogCtrl', [
             '$scope', 
             'acGlobal',
             'acBaseDialog',
             'aLang',
             'config',
             
            function($scope, acGlobal, acBaseDialog, aLang, config){
        
            	acBaseDialog.setGenericDialog($scope);
            	 
                $scope.label = function(id){
                    return aLang.label(id);
                };
                 
                $scope.title = function (){
                    return $scope.label('ResetChanges');
                };
        
                $scope.yes = function(){
                    config.close('ok');
                    if (angular.isFunction(config.callback)){
                        config.callback();
                    }
                };
                
                $scope.no = function(){
                    config.close('cancel');
                };
            }
     ])  
     

    /**
     * Open Yes-No dialog
     */
    .controller('acYesNoDialogCtrl', [
             '$scope', 
             'acGlobal',
             'acBaseDialog',
             'aLang',
             'config',
             
            function($scope, acGlobal, acBaseDialog, aLang, config){
        
            	acBaseDialog.setGenericDialog($scope); 
            	 
                $scope.label = function(id){
                    return aLang.label(id);
                };
                 
                $scope.title = function (){
                    return $scope.label(config.title);
                };
        
                $scope.message = function (){
                    if (!angular.isDefined(config.message) || config.message === null){
                        return '';
                    }
                    return $scope.label(config.message);
                };
                
                $scope.yes = function(){
                    config.close('ok');
                    if (angular.isFunction(config.callback)){
                        config.callback();
                    }
                };
                
                $scope.no = function(){
                    config.close('ok');
                    if (angular.isFunction(config.noCallback)){
                        config.noCallback();
                    }
                };
                
                $scope.yes = function(){
                    config.close('ok');
                    if (angular.isFunction(config.yesCallback)){
                        config.yesCallback();
                    }
                };
            }
     ])  
     
     
    /**
     * Open Message dialog
     */
    .controller('acMessageDialogCtrl', [
             '$rootScope',
             '$scope', 
             'acGlobal',
             'acBaseDialog',
             'acNoRepeatRemote',
             'aLang',
             'config',
             
            function($rootScope, $scope, acGlobal, acBaseDialog, acNoRepeatRemote, aLang, config){
        
            	acBaseDialog.setGenericDialog($scope);
            	 
            	var showNorepeat = false;
            	if (angular.isDefined(config.norepeat) && config.norepeat !== null){
            		showNorepeat = true;
                }
            	
            	$scope.showNoRepeat = function(){
            		return showNorepeat;
                }; 
            	
                $scope.errorNoRepeat = function(){
                	acNoRepeatRemote.queryForce({code:config.norepeat});
                	
                	if (!angular.isDefined($rootScope.norepeat) 
                			|| $rootScope.norepeat === null){
                		$rootScope.norepeat = {};
                	}
                	
                	$rootScope.norepeat[config.norepeat] = true;
                	$scope.ok();	
                }; 
                
                $scope.label = function(id){
                    return aLang.label(id);
                };
                 
                $scope.title = function (){
                    return $scope.label(config.title);
                };
        
                $scope.message = function (){
                    if (!angular.isDefined(config.message) || config.message === null){
                        return '';
                    }
                    return $scope.label(config.message);
                };
                
                $scope.detail = function (){
                    if (!angular.isDefined(config.detail) || config.detail === null){
                        return '';
                    }
                    return $scope.label(config.detail);
                };
                
                $scope.ok = function(){
                    config.close('ok');
                    if (angular.isFunction(config.callback)){
                        config.callback();
                    }
                };
                
            }
     ])  
     
     
     
     /**
      * Open Session Timeout dialog
      */
     .controller('acTimeoutDialogCtrl', [
              '$rootScope',
              '$scope',
              '$window',
              'acGlobal',
              'acBaseDialog',
              'aLang',
              'config',
              
             function($rootScope, $scope, $window, acGlobal, acBaseDialog, aLang, config){
         
             	 acBaseDialog.setGenericDialog($scope);
             	 
                 $scope.label = function(id){
                     return aLang.label(id);
                 };
                  
                 $scope.title = function (){
                     return $scope.label('TimeoutT');
                 };
         
                 $scope.message = function (){
                     return $scope.label('TimeoutM');
                 };
                 
                 
                 $scope.ok = function(){
                     $rootScope.relogin = false;
                     $window.location.href = acGlobal.globals().indexPage;
                     config.close('ok');
                 };
                 
             }
      ])  
     
;
