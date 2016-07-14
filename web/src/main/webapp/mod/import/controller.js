'use strict';

angular.module('mod.import.controllers', [])


    /**
     * Import LogiSoft data
     */
    .controller('importLogiCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'acCache',
            'acDialogs',
            'importModel',
            'importLogiRemote',
            'importStartRemote',
            'createCsvRemote',
            'importCsvRemote',
            'importResetFixRemote',
            'mdataImportExportRemote',
             
            function($rootScope, $scope, acController, acCache, acDialogs, 
            		importModel, importLogiRemote, importStartRemote, createCsvRemote, importCsvRemote, importResetFixRemote, mdataImportExportRemote){
             
                var config = acController.createConfig('importLogiCtrl', importModel.importlogi);
                config.title                = 'ImportLogi';
                config.remotePost           = importLogiRemote;
                config.remoteExport         = mdataImportExportRemote;
                config.sort_predicate_field = 'Started';
                config.isCachedObjectList   = false;

                acController.configure($scope, config);
                
                
                
                
                 /******************************
                  * Specific controller methods
                  ******************************/
                $scope.task = $scope.cacheObj;
                $scope.initList ($scope.task.getRuns());
                $scope.pfile = null;
                
                $scope.canImport = function(){
                    return !$scope.task.isActiveFixes() && !$scope.task.isFixToday();
                };
                $scope.isMessage = function(){
                    return $scope.task.isActiveFixes() || $scope.task.isFixToday();
                };
                $scope.message = function(){
                    return $scope.task.getMessage();
                };
                
                
                //Remove cache object for next page visit 
                acCache.invalidate(importModel.importlogi.model);
                
                $scope.getTaskStatus = function(){
                    if ($scope.task === null){
                        return '?';
                    }
                    return $scope.label($scope.task.getStatusText());
                };
                
                $scope.toggleDetails = function(){
                    $scope.toggleVisable('task_table');
                };

                
                $scope.startCreate = function(){
                	createCsvRemote.queryForce({pfile: $scope.pfile},
                            function(result){
                                $scope.displayDialog('StartCreate', 'OK');
                            },
                            function(result) { //error callback
                                if (result.isReturnDto()){
                                    $scope.displayErrorDialog('CantImport', result.getMessage());
                                }
                                //Damn, uncaught error!
                                else{
                                    $scope.displayErrorUnknown();
                                }
                            });
                        
                    };
                
                $scope.startUpload = function(){
                    importCsvRemote.queryForce(null,
                        function(result){
                    	    $rootScope.norepeat = null;
                            $scope.task = result.getObject();
                            $scope.initList ($scope.task.getRuns());
                        },
                        function(result) { //error callback
                            if (result.isReturnDto()){
                                $scope.displayErrorDialog('CantImport', result.getMessage());
                            }
                            //Damn, uncaught error!
                            else{
                                $scope.displayErrorUnknown();
                            }
                        });
                    
                };
                
                $scope.resetFix = function(){
                    importResetFixRemote.queryForce(null,
                            function(result){
                                $scope.reinitialise();
                            },
                            function(result) { //error callback
                                if (result.isReturnDto()){
                                    $scope.displayErrorDialog('CantReset', result.getMessage());
                                }
                                //Damn, uncaught error!
                                else{
                                    $scope.displayErrorUnknown();
                                }
                            });
                        
                    };
                
                
                    
                /**************************************************************************************
                 * Run Import
                 **************************************************************************************/
                
                /**
                 * Open progress dialog 
                 */
                var opd = function(){
                	$scope.addLoadingNoReg();
                    acDialogs.openDialog('mod/import/view/import_progress_dialog.html', 'importProgressCtrl', 
                            {scope: $scope,
                             windowClass: 'simu-act-menu'});
                };
                
               
                $scope.startImport = function(){
                    if (!$scope.canImport()){
                        return;
                    }
                    importStartRemote.queryForce(null,
                        function(result){
                    	    opd();
                        },
                        function(result) { //error callback
                            if (result.isReturnDto()){
                                $scope.displayErrorDialog('CantImport', result.getMessage());
                            }
                            //Damn, uncaught error!
                            else{
                            	$scope.displayError(result);
                            }
                        });
                    
                };
                
                /**
                 * Refresh page 
                 */
                $scope.osp = function(){
                	$rootScope.norepeat = null;
                	importLogiRemote.queryForce(null,
                            function(result){
                		$scope.task = result.getObject();
                        $scope.initList ($scope.task.getRuns());
                        });
                };
 
                    
            }
    ])



    /**
     * Import Progress Controller
     */
    .controller('importProgressCtrl', [
            '$rootScope',
            '$scope', 
            '$timeout',
            '$window',
            'acController',
            'acDialogs',
            'config',
            
            function($rootScope, $scope, $timeout, $window, acController, acDialogs, config){
                
                var configX = acController.createConfig('importProgressCtrl');
                acController.configure($scope, configX);
                
                var close   = false;
                var footer  = false;
                
                $scope.cancel = function(){
                    close = true;
                    config.close('ok');
                    $scope.removeLoading();
                };

                $scope.footer = function(){
                    return footer;
                };
                
                $scope.osp = function(){
                    config.close('ok');
                    $scope.removeLoading();
                    config.scope.osp();
                };

                
                /* *****************************************************************************************
                 * Labels
                 *******************************************************************************************/
                $scope.title = function() {
                    return $scope.label('ImportLogi');
                };
                
                var message = '';
                $scope.progress = function() {
                    return message;
                };

                var steps = '';
                $scope.progressSteps = function() {
                    return steps;
                };
                
                var loop = function(){
                    $timeout(function() {
                        
                        if (close){
                            return;
                        }

                        $scope.http.get($scope.remoteUrlPrefix + 'import/progress', null).then(function(res){
                            var rtn = res.data.split(',');
                            message = rtn[0];
                            steps   = rtn[2];
                            
                            if (rtn[1] === '-1'){
                                close = true;
                                footer = true;
                                $scope.removeLoading();
                            }
                            
                            if (rtn[1] === '0'){
                                close = true;
                                $scope.osp();
                            }
                        });

                        loop();
                    }, 500);
                };
                
                loop();
                
            }
     ])
    
    


    /**
     * Import file data
     */
    .controller('importFileCtrl', [
            '$rootScope',
            '$scope', 
            '$timeout',
            '$window',
            'acController',
            'acCache',
            'acModel',
            'uploadManager',
            'importModel',
            'importFileRemote',
            'importFileValidateRemote',
            'importStartRemote',
            'createCsvRemote',
            'importCsvRemote',
            'importResetFixRemote',
            'importConflictRemote',
            'importConflictExportRemote',
             
            function($rootScope, $scope, $timeout, $window, acController, acCache, acModel, uploadManager, 
                importModel, importFileRemote, importFileValidateRemote, importStartRemote, 
                createCsvRemote, importCsvRemote, importResetFixRemote, 
                importConflictRemote, importConflictExportRemote){
             
                var config = acController.createConfig('importFileCtrl', importModel.importfile);
                config.title              = 'ImportFile';
                config.remotePost         = importFileRemote;
                config.remoteExport       = importConflictExportRemote;
                config.isCachedObjectList = false;
    
                acController.configure($scope, config);
                
                
                
                
                 /******************************
                  * Specific controller methods
                  ******************************/
                $scope.task    = $scope.cacheObj;
                var initialise = function(){
                	$scope.initList ($scope.task.getRuns());
                	$scope.files     = $scope.task.getFiles();
                	
                	//NB 'conflicts' is hard coded in html view
                	$scope.conflicts = $scope.task.getConflicts();
                };
                initialise();
                
                var isValidImport = function(){
                	var v = true;
                	for (var i=0; i<$scope.files.length;i++){
                		var f = $scope.files[i];
                		
                		if (f.isInvalid()){
                			v = false;
                		}
                		else if (f.isMandatory() && !f.isImageFile()){
                			f.setInvalid(true);
                			f.setMessage($scope.label('ImportEr2'));
                			v = false;
                		}
                	}
                	return v;
                };
                $scope.canImport = function(){
                    return !$scope.task.isActiveFixes() && !$scope.task.isFixToday();
                };
                $scope.isMessage = function(){
                    return $scope.task.isActiveFixes() || $scope.task.isFixToday();
                };
                $scope.message = function(){
                    return $scope.task.getMessage();
                };
                
                
                //Remove cache object for next page visit 
                acCache.invalidate(importModel.importfile.model);
                
                $scope.getTaskStatus = function(){
                    if ($scope.task === null){
                        return '?';
                    }
                    return $scope.label($scope.task.getStatusText());
                };
                
                $scope.isTaskError = function(){
                    if ($scope.task === null){
                        return false;
                    }
                    return $scope.task.isError();
                };
                
                $scope.toggleDetails = function(){
                    $scope.toggleVisable('task_table');
                };
    
                $scope.startImport = function(){
                    if (!$scope.canImport() || !isValidImport()){
                        return;
                    }
                    
                    config.remotePost.post(
                    		$scope.files, 
                    		null, //params, 
                            function(result) { //success callback
                    			$rootScope.norepeat = null;
                    			$scope.task = result.getObject();
                    			if ($scope.task.isError()){
                    				$scope.displayErrorDialog('TaskSFE', $scope.label($scope.task.getStatusText()));
                    			}
                    			else if ($scope.task.isWarn()){
                    				$scope.displayErrorDialog('TaskSFW', $scope.label($scope.task.getStatusText()));
                    				processConflicts();
                    			}
                    			else{
                    				processConflicts();
                    			}
                    			initialise();
                            },
                            function(result) { //error callback
                                scope.postError(result);
                            }
                    );
                };
                
                
                $scope.startCreate = function(){
                    createCsvRemote.queryForce(null,
                            function(result){
                                $scope.displayDialog('StartCreate', 'OK');
                            },
                            function(result) { //error callback
                                if (result.isReturnDto()){
                                    $scope.displayErrorDialog('CantImport', result.getMessage());
                                }
                                //Damn, uncaught error!
                                else{
                                    $scope.displayErrorUnknown();
                                }
                            });
                        
                    };
                
                $scope.startUpload = function(){
                    importCsvRemote.queryForce(null,
                        function(result){
                            $scope.task = result.getObject();
                            $scope.initList ($scope.task.getRuns());
                        },
                        function(result) { //error callback
                            if (result.isReturnDto()){
                                $scope.displayErrorDialog('CantImport', result.getMessage());
                            }
                            //Damn, uncaught error!
                            else{
                                $scope.displayErrorUnknown();
                            }
                        });
                    
                };
                
                $scope.resetFix = function(){
                    importResetFixRemote.queryForce(null,
                            function(result){
                                $scope.reinitialise();
                            },
                            function(result) { //error callback
                                if (result.isReturnDto()){
                                    $scope.displayErrorDialog('CantReset', result.getMessage());
                                }
                                //Damn, uncaught error!
                                else{
                                    $scope.displayErrorUnknown();
                                }
                            });
                        
                    };
                

                    
                //Open log file
                $scope._logf = function(file){
                	var url = 'rest/import/log/?log=' + file.getLog();
			    	$window.open(url, '_blank');
                };


                /******************************
                 * Conflicts
                 ******************************/
                $scope.setallconflictsQ = false;
                $scope.setallconflictsM = false;
                
                //After import process 
                var processConflicts = function(){
                	if ($scope.task.isConflict()){
        				$scope.displayDialog ($scope.label('Import'), $scope.label('ImportCH'));
        				$scope.setVisable('save_conflict', true);
        				$scope.setVisable('save_conflict_ex', true);
        			}
                	else{
                		$scope.setVisable('save_conflict', false);
        				$scope.setVisable('save_conflict_ex', false);
                	}
        			$scope.viewLinkId = $scope.getNextId();
    				
                };
                
                //Change all prod_order adjustment statuses
                $scope._cau = function(){
                	var el = angular.element('#update-allQ');
                    var active = el.is(':checked');
                    for (var i=0;i<$scope.conflicts.length;i++){
                    	var c = $scope.conflicts[i];
                    	c.setAdjustedUpdate(active);
                    }
                    
                    $timeout(function() {
                    	$scope.showHideAdjustments();
                    }, 500);
                };
                
                //Change all material adjustment statuses
                $scope._cam = function(){
                	var el = angular.element('#update-allM');
                    var active = el.is(':checked');
                    for (var i=0;i<$scope.conflicts.length;i++){
                    	var c = $scope.conflicts[i];
                    	c.setMaterialUpdate(active);
                    }
                };
                
                //Show / hide adjustment all import
                $scope.showHideAdjustments = function(){
                	for (var i=0;i<$scope.conflicts.length;i++){
                		var c = $scope.conflicts[i];
                		$scope.showHideAdjustment(c);
                    }
                };
                //Show / hide adjustment import
                $scope._sha = function(id){
                	var rec = $scope.findById(id, $scope.conflicts);
                	if (rec !== null){
                		$scope.showHideAdjustment(rec);
                	}
                };
                	
                $scope.showHideAdjustment = function(rec){
                	var el = angular.element('#u' + rec.getId());
                	var active = el.is(':checked');
                	
                	var elq = angular.element('#z' + rec.getId());
                	var elm = angular.element('#v' + rec.getId());
                	if (active){
                		elq.show();
                		elm.show();
                	}
                	else{
                		elq.hide();
                		elm.hide();
                	}
                };
                $timeout(function() {
                	$scope.showHideAdjustments();
                }, 500);
                
                
                $scope.viewLinkId  = $scope.getNextId();
                $scope.getViewLink = function(){
                    return 'rest/import/conflict/view?lk=' + $scope.viewLinkId + '&rl=' + false;
                };
                
                $scope.saveConflictAdjustments = function(){
                	importConflictRemote.post(
                			$scope.conflicts, 
                            null,
                            function(result) { //success callback
                				$scope.displayDialog('ImportAO', result.getObject());
                			}, 
                            function(result) { //error callback
                                $scope.postError(result);
                            }
                    );
                };
                
                
                /******************************
                 * File up loads
                 ******************************/
                $scope.upload = function(f) {
                    file = f;
                    angular.element("#" + f.getTypeId()).trigger('click');
                };

                $scope.remove = function(file) {
                    file.setFilename(null);
                    file.getImageFile();
                    file.setInvalid(false);
                    file.setMessage(null);
                    file.setLog(null);
                };

                $scope.getFileUploadHref = function(f){
                    return 'import/fileUpload?t=' + f.getTypeId();
                };

                var file = null;
                var files = [];
                var percentage = 0;
                var filename_up = null;
                var uploadfile_up = null;
                var timeoutPromise = null;
            
                $scope.$on('fileAdded', function (e, filename, uploadfile) {
                    
                    if (!filename.endsWith('.csv')
                            && !filename.endsWith('.xls')
                            && !filename.endsWith('.xlsx')){
                        uploadManager.clear();
                        alert ($scope.label('ImportIF'));
                        return;
                    }
                    
                    $scope.addLoading();
                    files.push(filename);
                    filename_up = filename;
                    uploadfile_up = uploadfile;
                    
                    timeoutPromise = $timeout(function() {
                        uploadComplete('timeout');
                    }, 15000);
                    
                    uploadManager.upload();
                    files = [];
                    $scope.$apply();
                });

                $scope.$on('uploadProgress', function (e, call) {
                    percentage = call;
                });
                
                $scope.$on('uploadComplete', function (e) {
                    uploadComplete('uploadComplete');
                });
                
                var uploadComplete = function(source){
                    
                    $scope.removeLoading();
                
                    if (source === 'timeout' && uploadfile_up !== null){
                        if (up_idx === up_id){
                            alert ($scope.label('ImportEr1'));
                        }
                        file.setInvalid(true);
                    	file.setMessage($scope.label('ImportEr4'));
                    	updateFile();
                    }
                    else if (source === 'timeout'){
                    	file.setInvalid(true);
                    	file.setMessage($scope.label('ImportEr4'));
                    	updateFile();
                    }
                    else if (uploadfile_up === 'file'){
                        file.setFilename(filename_up);
                        file.getImageFile();
                        file.setInvalid(false);
                        file.setMessage(null);
                        file.setLog(null);
                        
            			if (!file.isImageFile()){
            				file.setInvalid(true);
            				file.setMessage($scope.label('ImportEr2'));
            			}
            			//Validate file on server
            			else{
            				importFileValidateRemote.queryForce(
            						{f:file.getFilename(), i:file.getTypeId()},
            						function(result){
            							var v = result.getObject();
            							file.setInvalid(v.isInvalid());
            							file.setMessage(v.getMessage());
            							file.setLog(v.getLog());
            							updateFile();
            						},
            						function(result) { //error callback
            							file.setInvalid(true);
            							file.setMessage($scope.label('ImportEr3'));
            							updateFile();
            						}
            				);
            			}
                        
                    }

                    
                };
            
                var updateFile = function(){
                	file = null;
                	uploadfile_up = null;
                	filename_up = null;
                	if (timeoutPromise !== null){
                		$timeout.cancel(timeoutPromise);
                	}
                	$scope.$apply();
                };

                
                
                
                $scope.$on('$destroy', function(){
                    if (timeoutPromise !== null){
                        $timeout.cancel(timeoutPromise);
                    }
                });

            }
    ]);
