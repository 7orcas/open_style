 'use strict';

angular.module('app.mod.langadmin.controllers', [])


    /**
     * Language key-values pairs administration 
     */
    .controller('langAdminCtrl', [
            '$rootScope',
            '$scope',
            '$window',
            'acGlobal',
            'acController',
            'aLang',
            'langadminModel',
            'langadminRemote',
            'langadminExportRemote',
             
            function($rootScope, $scope, $window, acGlobal, acController, aLang, 
            		langadminModel, langadminRemote, langadminExportRemote){
             
                var config = acController.createConfig('langAdminCtrl', langadminModel.list);
                config.title                = 'LangAdmin';
                config.remotePost           = langadminRemote;
                config.sort_predicate_field = 'Key';
                
                config.maxRecordsPerPage    = 30;
                config.showPageSelection    = true; //if number of records > maxRecordsPerPage then show page selection header
                
                acController.configure($scope, config);
                $scope.sql  = $scope.cacheObj._sql;
                
                
                //User is returning to this page
                if (angular.isDefined(config.sql)){
                    $scope.sql = config.sql;
                }
                //First time
                else{
                    config.sql = $scope.sql;
                }
                
                
                

                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                $scope.setLookup('lang/lookup', langadminModel.list.modelSql, 20);
                
                $scope.copy = function(id){
                    var dto1 = $scope.findById(id);
                    var dto2 = $scope.recordNew();
                    dto1.copy(dto2);
                };

                $scope.reload = function(){
                	aLang.clearcache(new function(){
                		aLang.loadLangForce(acGlobal.globals().language);
                	});
                };
                
                
                /**
                 * Open advance search dialog
                 */
                $scope.lookupAdvance = function(){
                    $scope.openDialog('app/mod/langadmin/view/search.html', 'langAdminSearchCtrl', {sql : $scope.sql});
                };
                
                /**
                 * Export to spreadsheet
                 */
                $scope.exportSS = function(){
                	var encoded = $scope.sql.model.json($scope.sql);
                	langadminExportRemote.queryForce({sql: encoded}, 
 	  			        function(result){
	 	  			    	    var url = 'rest/spreadsheet/return?filename=' + result.getObject();
	 	  			    	    $window.open(url, '_tab');
 	 					    }, function (result){
 	 			    		   alert(aLang.label(result.getMessage()));
 	 			    	});
                };
                
            }
    ])
    
    
    
    /**
     * Search
     */
    .controller('langAdminSearchCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'acDialogs',
            'langadminModel',
            'langadminRemote',
            'config',

            function($rootScope, $scope, acController, acDialogs, langadminModel, langadminRemote, config){
                
                var configX = acController.createConfig('langAdminSearchCtrl', langadminModel.list);
                acController.configure($scope, configX);
                
                $scope.model = $scope.getCacheModel(langadminModel.list.modelSql);
                $scope.dto   = $scope.model.createDto();
                
                $scope.cancel = function(){
                    config.close('ok');
                };
                
                $scope.search = function(){
                    config.close('ok');
                    config.scope.sql = $scope.dto;
                    config.scope.reinitialise($scope.dto, 150);
                };
                
            }
     ])
    
    
;