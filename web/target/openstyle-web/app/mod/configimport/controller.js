 'use strict';

angular.module('app.mod.company.controllers', [])


    /**
     * Company Administration list data
     */
    .controller('companyAdminCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'companyModel',
            'companyadminRemote',
             
            function($rootScope, $scope, acController, companyModel, companyadminRemote){
             
                var config = acController.createConfig('companyAdminCtrl', companyModel.list);
                config.title                = 'CompanyAdmin';
                config.remotePost           = companyadminRemote;
                config.sort_predicate_field = 'CompanyNr';
                acController.configure($scope, config);
                
                /*-********************************************
                 * Engines
                 *********************************************/
                $scope.engines = $scope.model.valuesEngine();
            	var setSelectionE = function(){
	            	for (var i=0; i<$scope.list.length; i++){
	            		var c = $scope.list[i];
	            		
	            		for (var j=0; j<$scope.engines.length; j++){
	                		var s = $scope.engines[j];
	                		if (c.getEngine() === s.key){
	                			c.setEngineValue(s);
	                		}
	                	}	
	            		
	            	}
                };
                setSelectionE();
                
                /*-********************************************
                 * Imports
                 *********************************************/
                $scope.imports = $scope.model.valuesImportType();
            	var setSelectionI = function(){
	            	for (var i=0; i<$scope.list.length; i++){
	            		var c = $scope.list[i];
	            		
	            		for (var j=0; j<$scope.imports.length; j++){
	                		var s = $scope.imports[j];
	                		if (c.getImportType() === s.key){
	                			c.setImportValue(s);
	                		}
	                	}	
	            		
	            	}
                };
                setSelectionI();
                
                /*-********************************************
                 * Fixing
                 *********************************************/
                $scope.fixes = $scope.model.valuesFixType();
            	var setSelectionF = function(){
	            	for (var i=0; i<$scope.list.length; i++){
	            		var c = $scope.list[i];
	            		
	            		for (var j=0; j<$scope.fixes.length; j++){
	                		var s = $scope.fixes[j];
	                		if (c.getFixType() === s.key){
	                			c.setFixValue(s);
	                		}
	                	}	
	            		
	            	}
                };
                setSelectionF();
                
                
            	
                $scope.showDetail = function(id){
                    var record = $scope.findById(id);
                    record.toggleSelect();
                };

                $scope.selected = function(id){
                    var record = $scope.findById(id);
                    return record.isSelect();
                };
                
                //Override
                var save = $scope.recordSave;
                $scope.recordSave = function(){
                    
                	for (var i=0; i<$scope.list.length; i++){
                		var c = $scope.list[i];
                		c.setEngineValue(c.getEngineValue().value);
                		c.setImportValue(c.getImportValue().value);
                		c.setFixValue(c.getFixValue().value);
                	}	
                	
                    save();
                };
                
                //Override
                var postSuccess = $scope.postSuccess;
                $scope.postSuccess = function(result){
                	postSuccess(result);
                	setSelectionE();
                	setSelectionI();
                	setSelectionF();
                };
                
                
            }
    ]);
