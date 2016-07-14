'use strict';

angular.module('mod.export.controllers', [])


    /**
     * Export Fixed data
     */
    .controller('exportCtrl', [
            '$rootScope',
            '$scope',
            '$timeout',
            '$window',
            'acController',
            'acCache',
            'exportModel',
            'mdataModel',
            'exportRemote',
            'exportFixed',
             
            function($rootScope, $scope, $timeout, $window, acController, acCache, exportModel, mdataModel, 
            		exportRemote, exportFixed){
             
                var config   = acController.createConfig('exportCtrl', exportModel.exportselection);
                config.title = 'ExpFix';
                
                acController.configure($scope, config);
                
                
                
                 /******************************
                  * Specific controller methods
                  ******************************/
                $scope.fixes      = $scope.cacheObj.getFixes();
                $scope.cacheObj.setFixes(null);
                
                $scope.sql        = $scope.cacheObj;
                $scope.from       = $scope.sql.getFromDate();
                $scope.to         = $scope.sql.getToDate();
                $scope.enableCsv  = false;
                $scope.enableFrom = true;
                
                
                //Override for start/end date
                var ux = $scope.inputUpdate;
                $scope.inputUpdate = function(field){
                    if (field === 'start'){
                    	$timeout(function() {
	                    	try{
	                    		var ds = $scope.formatDateForElementId('start-date');
	                    		var df = $scope.formatDateForElementId('finish-date');
	                    		
	                    		if (df < ds){
	                    			$scope.copyFrom();
                                	$scope.$digest();
	                    		}
	                    		
	                    	} catch (err){}
                    	}, 50);
                    }
                    ux();
                };
                
                
                $scope.copyFrom = function(){
                	$scope.to = $scope.from;
                };
                
                /*-*************************************
                 * Radio selections
                 ***************************************/
                $scope.view = {
                    selection: 'si',
                    
                    initialise: function (sql){
                    	$scope.enableCsv = false;
                    	$scope.enableFrom = true;
                    	
                        if (sql.getView() == exportModel.VIEW_MOULD_CHANGES){
                            this.selection = 'mc';
                        }
                        else if (sql.getView() == exportModel.VIEW_OVERVIEW){
                            this.selection = 'ov';
                        }
                        else if (sql.getView() == exportModel.VIEW_CYCLES){
                        	$scope.enableFrom = false;
                        	this.selection = 'cy';
                        }
                        else{
                        	$scope.enableCsv = true;
                            this.selection = 'si';
                        }
                    },
                    
                    set: function (sql){
                    	if (this.selection === 'mc'){
                        	sql.setView(exportModel.VIEW_MOULD_CHANGES);
                        }
                        else if (this.selection === 'ov'){
                            sql.setView(exportModel.VIEW_OVERVIEW);
                        }
                        else if (this.selection === 'cy'){
                            sql.setView(exportModel.VIEW_CYCLES);
                        }
                        else{
                            sql.setView(exportModel.VIEW_DETAIL);
                        }
                    },
                    
                };
                $scope.view.initialise($scope.sql);
                
                $scope.viewSelect = function(){
                	$timeout(function() {
	                	$scope.enableCsv = false;
	                	$scope.enableFrom = true;
	                	$scope.view.set($scope.sql);
	                	if ($scope.sql.getView() === exportModel.VIEW_DETAIL){
	                		$scope.enableCsv = true;
	                	}
	                	if ($scope.sql.getView() === exportModel.VIEW_CYCLES){
	                		$scope.enableFrom = false;
	                	}
	                	
                	}, 50);
                };
                 
                
                
                /*-*************************************
                 * Plants selections
                 ***************************************/
                var plantList = $scope.getCache(mdataModel.plant.model);
                $scope.plants = plantList.selectList();
                $scope.plant  = $scope.plants[0];
                for (var j=0; j < $scope.plants.length; j++){
                    var rec = $scope.plants[j];
                    if (rec.key === $scope.sql.getPlantId()){
                        $scope.plant = rec;
                        break;
                    }
                }
                
                
                /*-*************************************
                 * Formated <code>Select</code> list
                 **************************************/
                var selectList = function(firstRecord, listIds, listDescr){
                    var listx = [];
                    listx.push(firstRecord); 
                    for (var j=0; j < listIds.length; j++){
                    	listx.push({key: listIds[j], value : listDescr[j]});
                    }
                    return listx;
                };
                
                /*-*************************************
                 * Machine selections
                 ***************************************/
                $scope.machine  = {key: 0, value : $scope.label('All')};
                $scope.machines = selectList($scope.machine, $scope.sql.getMachineIds(), $scope.sql.getMachineDescrs());
                for (var j=0; j < $scope.machines.length; j++){
                    var rec = $scope.machines[j];
                    if (rec.key === $scope.sql.getMachineId()){
                        $scope.machine = rec;
                        break;
                    }
                }
                
                /*-*************************************
                 * Shift selections
                 ***************************************/
                $scope.shift  = {key: 0, value : $scope.label('All')};
                $scope.shifts = selectList($scope.shift, $scope.sql.getShiftIds(), $scope.sql.getShiftDescrs());
                for (var j=0; j < $scope.shifts.length; j++){
                    var rec = $scope.shifts[j];
                    if (rec.key === $scope.sql.getShiftId()){
                        $scope.shift = rec;
                        break;
                    }
                }
                
                
                
                /*-*************************************
                 * Run Export
                 ***************************************/
                $scope.exportPdf = function(){
                	$scope.sql.setExportFormat(exportModel.EXP_PDF);
                	exportSimu();
                };
                
                $scope.exportSS = function(){
                	$scope.sql.setExportFormat(exportModel.EXP_SS);
                	exportSimu();
                };
                
                $scope.exportCsv = function(){
                	$scope.sql.setExportFormat(exportModel.EXP_CSV);
                	exportSimu();
                };
                
                
                
                //Export simulation
                var exportSimu = function(){
                	$scope.sql.setFromDate($scope.from);
                	$scope.sql.setToDate($scope.to);

                	$scope.view.set($scope.sql);
                	
                	$scope.sql.setPlantId($scope.plant.key);
                    $scope.sql.setMachineId($scope.machine.key);
                    $scope.sql.setShiftId($scope.shift.key);
                    
                	var x = $scope.model.json($scope.sql);
                	exportFixed.queryForce({sql: x}, 
	 	  			        function(result){
	 	  			    	    var url = 'rest/spreadsheet/return?filename=' + result.getObject();
	 	  			    	    $window.open(url, '_tab');
	 	 					}, 
	 	 					function (result){
	 	 					   	$scope.displayErrorDialog('ExpE1', result.getMessage());
	 	 			    	}
                	);
                };
                
                
                
            }
    ]);
    