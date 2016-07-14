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
                 * Get Models
                 *********************************************/
                $scope.cdModel = $scope.model.getChildModelByDto('b2');
                $scope.sdModel = $scope.model.getChildModelByDto('c1');
                $scope.fdModel = $scope.model.getChildModelByDto('c2');
                $scope.smModel = $scope.model.getChildModelByDto('c3');
                $scope.fmModel = $scope.model.getChildModelByDto('c4');
                $scope.soModel = $scope.model.getChildModelByDto('c5');
                $scope.foModel = $scope.model.getChildModelByDto('c6');
                $scope.fcModel = $scope.model.getChildModelByDto('c7');
                
                /*-********************************************
                 * View Configurations
                 *********************************************/
                $scope.htmlSD = function(id){
                    var record = $scope.findById(id);
                    return record.getHtmlSD();
                };
                $scope.htmlCD = function(id){
                    var record = $scope.findById(id);
                    return record.getHtmlCD();
                };
                $scope.exportSD = function(id){
                    var record = $scope.findById(id);
                    return record.getExportSD();
                };
                $scope.exportFD = function(id){
                    var record = $scope.findById(id);
                    return record.getExportFD();
                };
                $scope.exportSM = function(id){
                    var record = $scope.findById(id);
                    return record.getExportSM();
                };
                $scope.exportFM = function(id){
                    var record = $scope.findById(id);
                    return record.getExportFM();
                };
                $scope.exportSO = function(id){
                    var record = $scope.findById(id);
                    return record.getExportSO();
                };
                $scope.exportFO = function(id){
                    var record = $scope.findById(id);
                    return record.getExportFO();
                };
                $scope.exportFC = function(id){
                    var record = $scope.findById(id);
                    return record.getExportFC();
                };
                $scope.showDetail = function(id){
                    var record = $scope.findById(id);
                    record.toggleSelect();
                };

                $scope.selected = function(id){
                    var record = $scope.findById(id);
                    return record.isSelect();
                };
                
                
                /*-********************************************
                 * Select options
                 *********************************************/
                $scope.engines       = $scope.model.valuesEngine();
                $scope.enginePeriods = $scope.model.valuesEnginePeriod();
                $scope.mouldAssign   = $scope.model.valuesMouldAssignType();
                $scope.imports       = $scope.model.valuesImportType();
                $scope.formatsSimu   = $scope.model.valuesExportSimuFileFormat();
                $scope.fixes         = $scope.model.valuesFixType();
                $scope.sorts         = $scope.model.valuesSortType();
                $scope.idxs          = $scope.model.valuesIdx0Type();
                $scope.timezones     = $scope.model.valuesTimezoneGMT();
            	
                $scope.simuMFDs      = $scope.sdModel.valuesSimuMDFType1();
                
                
                var setSelections = function(){
	            	for (var i=0; i<$scope.list.length; i++){
	            		var c = $scope.list[i];
	            		
	            		for (var j=0; j<$scope.engines.length; j++){
	                		var s = $scope.engines[j];
	                		if (c.getEngine() === s.key){
	                			c.setEngineValue(s);
	                		}
	                	}
	            		for (var j=0; j<$scope.enginePeriods.length; j++){
	                		var s = $scope.enginePeriods[j];
	                		if (c.getEnginePeriod() === s.key){
	                			c.setEnginePeriodValue(s);
	                		}
	                	}
	            		for (var j=0; j<$scope.mouldAssign.length; j++){
	            			var s = $scope.mouldAssign[j];
	            			if (c.getMouldAssignType() === s.key){
	            				c.setMouldAssignValue(s);
	            			}
	            		}
	            		for (var j=0; j<$scope.imports.length; j++){
	                		var s = $scope.imports[j];
	                		if (c.getImportType() === s.key){
	                			c.setImportValue(s);
	                		}
	                	}
	            		for (var j=0; j<$scope.formatsSimu.length; j++){
	                		var s = $scope.formatsSimu[j];
	                		if (c.getExportSimuFileFormat() === s.key){
	                			c.setExportSimuFileValue(s);
	                		}
	                	}	
	            		for (var j=0; j<$scope.fixes.length; j++){
	            			var s = $scope.fixes[j];
	            			if (c.getFixType() === s.key){
	            				c.setFixValue(s);
	            			}
	            		}	
	            		for (var j=0; j<$scope.sorts.length; j++){
	            			var s = $scope.sorts[j];
	            			if (c.getSortType() === s.key){
	            				c.setSortValue(s);
	            			}
	            		}	
	            		for (var j=0; j<$scope.idxs.length; j++){
	            			var s = $scope.idxs[j];
	            			if (c.getIdx0Type() === s.key){
	            				c.setIdx0Value(s);
	            			}
	            			if (c.getIdx1Type() === s.key){
	            				c.setIdx1Value(s);
	            			}
	            		}	
	            		for (var j=0; j<$scope.timezones.length; j++){
	            			var s = $scope.timezones[j];
	            			if (c.getTimezoneGMT() === s.key){
	            				c.setTimezoneGMTValue(s);
	            			}
	            		}	
	            		
	            		var c1 = $scope.htmlSD(c.getId());
	            		setSimuMFDs(c1);
                        
	            		c1 = $scope.exportSD(c.getId());
	            		setSimuMFDs(c1);
	            		
	            		c1 = $scope.exportFD(c.getId());
	            		setSimuMFDs(c1);
	            	}
                };
                
                var fieldType1  = $scope.sdModel.getFieldParameter('SimuMDFType1', 'dto'); 
                var fieldValue1 = $scope.sdModel.getFieldParameter('SimuMDFValue1', 'dto'); 
                var fieldType2  = $scope.sdModel.getFieldParameter('SimuMDFType2', 'dto'); 
                var fieldValue2 = $scope.sdModel.getFieldParameter('SimuMDFValue2', 'dto');
                var fieldType3  = $scope.sdModel.getFieldParameter('SimuMDFType3', 'dto'); 
                var fieldValue3 = $scope.sdModel.getFieldParameter('SimuMDFValue3', 'dto');
                var fieldType4  = $scope.sdModel.getFieldParameter('SimuMDFType4', 'dto'); 
                var fieldValue4 = $scope.sdModel.getFieldParameter('SimuMDFValue4', 'dto');
                
                var setSimuMFDs = function(c1){
                    for (var j=0; j<$scope.simuMFDs.length; j++){
                        var s = $scope.simuMFDs[j];
                        if (c1[fieldType1] === s.key){
                            c1[fieldValue1] = s;
                        }
                        if (c1[fieldType2] === s.key){
                            c1[fieldValue2] = s;
                        }
                        if (c1[fieldType3] === s.key){
                            c1[fieldValue3] = s;
                        }
                        if (c1[fieldType4] === s.key){
                            c1[fieldValue4] = s;
                        }
                    }
                };

                setSelections();
                
                
                
                //Override
                var save = $scope.recordSave;
                $scope.recordSave = function(){
                    
                	for (var i=0; i<$scope.list.length; i++){
                		var c = $scope.list[i];
                		c.setEngineValue(c.getEngineValue().value);
                		c.setEnginePeriodValue(c.getEnginePeriodValue().value);
                		c.setMouldAssignValue(c.getMouldAssignValue().value);
                		c.setImportValue(c.getImportValue().value);
                		c.setExportSimuFileValue(c.getExportSimuFileValue().value);
                		c.setFixValue(c.getFixValue().value);
                		c.setSortValue(c.getSortValue().value);
                		c.setIdx0Value(c.getIdx0Value().value);
                		c.setIdx1Value(c.getIdx1Value().value);
                		c.setTimezoneGMTValue(c.getTimezoneGMTValue().value);
                		
                		var c1 = $scope.htmlSD(c.getId());
                        c1[fieldValue1] = c1[fieldValue1].value;
                        c1[fieldValue2] = c1[fieldValue2].value;
                        c1[fieldValue3] = c1[fieldValue3].value;
                        c1[fieldValue4] = c1[fieldValue4].value;
                        
                        c1 = $scope.exportSD(c.getId());
                        c1[fieldValue1] = c1[fieldValue1].value;
                        c1[fieldValue2] = c1[fieldValue2].value;
                        c1[fieldValue3] = c1[fieldValue3].value;
                        c1[fieldValue4] = c1[fieldValue4].value;
                        
                        c1 = $scope.exportFD(c.getId());
                        c1[fieldValue1] = c1[fieldValue1].value;
                        c1[fieldValue2] = c1[fieldValue2].value;
                        c1[fieldValue3] = c1[fieldValue3].value;
                        c1[fieldValue4] = c1[fieldValue4].value;
                		
                		c.setSaveId($scope.getNextId()); //workaround to force save TODO: fix me !!!
                	}	
                	
                    save();
                };
                
                //Override
                var postSuccess = $scope.postSuccess;
                $scope.postSuccess = function(result){
                	postSuccess(result);
                	setSelections();
                };
                
               //Override
                var postError = $scope.postError;
                $scope.postError = function(result){
                	postError(result);
                	setSelections();
                };
                
            }
            
            
    ]);
