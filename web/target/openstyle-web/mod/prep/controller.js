'use strict';

angular.module('mod.prep.controllers', ['ngDragDrop', 'pasvaz.bindonce'])


    /**
     * Preparation selection
     */
    .controller('prepCtrl', [
            '$rootScope',
            '$scope', 
            '$timeout',
            '$window',
            '$http',
            'acCache',
            'acController',
            'acGlobal',
            'acDialogs',
            'acDefinition',
            'prepModel',
            'prepRemote',
            'prepRunRemote',
            'prepExportSSRemote',
            'prepUpdateRemote',
            'prepSumRemote',
            'simuModel',
            'simuRunRemote',
            'rptModel',
             
            function($rootScope, $scope, $timeout, $window, $http, acCache, acController, acGlobal, acDialogs, acDefinition, 
                    prepModel, prepRemote, prepRunRemote, prepExportSSRemote, prepUpdateRemote, prepSumRemote, 
                    simuModel, simuRunRemote, rptModel
                    ){
             
                
                /**
                 * Simulation Engine numbers (as defined in <code>Company.java</code>):<ul> 
                 */
                var ENGINE_2 = 2;
                
                var config = acController.createConfig('prepCtrl', prepModel.prepselection);
                config.title                = 'PrepT';
                config.remotePost           = prepRunRemote;
                config.sort_predicate_field = '';
                config.isCachedObjectList   = false;
                config.editmode             = true;

                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                
                /******************************
                 * Specific controller methods
                 ******************************/
                
                $scope.sql                     = $scope.cacheObj;
                $scope.dueDate                 = {};
                $scope.datepicker              = null;
                $scope.datepickerId            = null;
                $scope.datepickerShowButtonBar = true;
                $scope.lookupSelect            = {code: null};
                
                
                //Problem with ng-model reading from array in sql object, so use fields in scope as a work around
                var initialiseDates = function(){
                    for (var i=0; i<$scope.sql.getNumberOfDates(); i++){
                        var d = $scope.sql.getFrom()[i];
                        if (d !== null && !angular.isDate(d)){
                            d = $scope.formatDate(d);
                        }
                        $scope.dueDate['d' + i] = d; 
                    }
                    
                    //Initialize rules and period
                    $timeout(function() {
	                    var el = document.getElementById('cbr1');
	                	if (el !== null){
	                		el.checked = $scope.sql.isRule_periodPlan();
	                	}
	                	el = document.getElementById('cbr3');
	                	if (el !== null){
	                		el.checked = $scope.sql.isRule_completeIdx1();
	                	}
	                	
	                	//Thanks to http://stackoverflow.com/questions/78932/how-do-i-programatically-set-the-value-of-a-select-box-element-using-javascript
	                	el = document.getElementById('selectr1');
	                	if (el !== null){
	                		el.value = $scope.sql.getPeriod();
	                	}
	                	
	                	setPlanClass();
                    }, 100);
                    
                };
                
                var setPlanClass = function(){
                	var el = angular.element("#labelr1");
                	if (el !== null){
                		if (!$scope.sql.isRule_periodPlan()){
                			el.addClass('so1r1');
                		}
                		else{
                			el.removeClass('so1r1');
                		}
                	}
                	el = angular.element("#labelr3");
                	if (el !== null){
                		if (!$scope.sql.isRule_completeIdx1()){
                			el.addClass('so1r1');
                		}
                		else{
                			el.removeClass('so1r1');
                		}
                	}
                };                

                //User is returning to this page
                if (angular.isDefined(config.sql)){
                    $scope.dueDate      = config.dueDate;
                    $scope.sql          = config.sql;
                    $scope.lookupSelect = config.lookupSelect;
                    initialiseDates();
                }
                //First time
                else{
                    initialiseDates();
                    config.sql          = $scope.sql;
                    config.dueDate      = $scope.dueDate;
                    config.lookupSelect = $scope.lookupSelect;
                }
                
                initialiseDates();
                
                
                //Toggle rules
                $scope._r = function(id){
                	if (!angular.isDefined(id)){
                		return;
                	}
                	var el = angular.element('#' + id);
                    var active = el.is(':checked');
                	                	
                	if (id === 'cbr1'){
                		$scope.sql.setRule_periodPlan(active);
                	}
                	else if (id === 'cbr3'){
                		$scope.sql.setRule_completeIdx1(active);
                	}
                	setPlanClass();
                };
                
                
                /**
                 * Open generic date picker for row date fields
                 * Thanks to http://stackoverflow.com/questions/21683073/opening-the-angular-strap-datepicker-programmatically 
                 */
                $scope.openDatePicker = function(id){
                    
                    $scope.datepicker   = $scope.formatDateForElementId(id);
                    $scope.datepickerId = id;
                    
                    var x = document.getElementById(id);
                    
                    $timeout(function() {
                        var el = document.getElementById('prep-datepick');
                        el.value = x.value;
                        el.focus();                
                    }, 50);
                };
                
                        
                /**
                 * After selection, do updates
                 * Thanks to http://stackoverflow.com/questions/25390998/how-to-trigger-a-function-or-assign-selected-value-to-another-scope-variable-for
                 */
                $scope.changeDatePicker = function(){
                    $timeout(function() {
                        var x = document.getElementById('prep-datepick');
                        var el = document.getElementById($scope.datepickerId);
                        el.value = x.value;
                        x.value = $scope.formatDateForElementId('prep-datepick');
                        x.value = $scope.model.formatDate(new Date(x.value), $scope.dateFormatDto);
                        
                        var params = {prepid: $scope.sql.getId(),
                                      recid:  $scope.datepickerId.substring(2)};
                        
                        if ($scope.datepickerId.indexOf('nb') != -1){
                            params.notbefore = x.value; 
                        }
                        else if ($scope.datepickerId.indexOf('na') != -1){
                            params.notafter = x.value; 
                        }
                        
                        prepUpdateRemote.queryForce(params,
                                function(result){},
                                function(result) { 
                                    $scope.displayError(result);
                                }
                        );
                        
                    }, 50);
                };
                
                
        
                /**
                 * After selection, do updates
                 * Thanks to http://stackoverflow.com/questions/4813219/jquery-checkbox-value
                 * Thanks to http://stackoverflow.com/questions/10611170/how-to-set-value-of-input-text-using-jquery
                 */
                $scope._ca = function(id){
                    
                    var el = angular.element('#cb' + id);
                    var active = el.is(':checked');
                    
                    prepUpdateRemote.queryForce(
                                {prepid: $scope.sql.getId(),
                                 recid:  id,
                                 active: active},
                            function(result){
                                el = angular.element('#prep-total');
                                el.val(result.getObject());  
                            },
                            function(result) { 
                                $scope.displayError(result);
                            }
                    );
                };

                
			    /**
			     * Toggle all selections
			     * Thanks to http://stackoverflow.com/questions/15843581/how-to-corectly-iterate-through-getelementsbyclassname
			     */
			    $scope._cax = function(){
			    	$scope.addLoading();
			    	
			    	$timeout(function() {
				    	var sleep = function (miliseconds) {
			    		   var currentTime = new Date().getTime();
			    		   while (currentTime + miliseconds >= new Date().getTime()) {
			    		   }
			    		};
				    	
			    		var el   = angular.element('#cbx_all');
				        var cbxA = el.is(':checked');
				    	
				    	var cbs = document.getElementsByClassName('ptcb');
				    	for(var i = 0; i < cbs.length; i++){
				    		el = angular.element('#' + cbs[i].id);
				            var active = el.is(':checked');
				            if (cbxA !== active){
				            	el.click();
				            	sleep(20);
				            }
				    	}
				    	
				    	//Recalculate sum
				    	$timeout(function() {
				    		prepSumRemote.queryForce({prepid: $scope.sql.getId()},
	                               function(result){
	                                   el = angular.element('#prep-total');
	                                   el.val(result.getObject());
	                                   $scope.removeLoading();
	                               },
	                               function(result) { 
	                            	   $scope.removeLoading();
	                                   $scope.displayError(result);
	                               }
				        	);
				        }, 500);
				    	
			    	}, 200);
			    	
			    };
    
			    
                //Open log file
                $scope._logf = function(view){
                    var url = 'rest/prep/log/';
                    $window.open(url, '_blank');
                };

                
                /**
                 * Used by sidebar_actions.html typeahead
                 * Override
                 */
                $scope.lookup = function(val) {
                    
                    var p = {limit: 15, prepid: $scope.sql.getId(), code: val};
                    
                    return $scope.http.get($scope.remoteUrlPrefix + 'prep/lookup', {params: p}).then(function(res){
                        var list = [];
                        for (var i=0; i<res.data.o.length; i++){
                            list.push(res.data.o[i]);
                        }
                        return list;
                    });
                };
                
                /**
                 * Used by sidebar_actions.html lookupAction button. Also called from search dialog
                 * Override
                 */
                $scope.lookupAction = function(){
                    this.sql.setOffset(0);
                    this.sql.setLookup($scope.lookupSelect.code);
                    config.lookupSelect = $scope.lookupSelect;
                    viewLinkEncoded     = $scope.model.json($scope.sql);
                    $scope.viewLinkId   = $scope.getNextId();
                    $scope.addLoadingNoReg();
                    window.scrollTo(0, 0);
                };
                
               
                /**
                 * Open advance search dialog
                 */
                $scope.lookupAdvance = function(){
                    $scope.openDialog('mod/prep/view/search_dialog.html', 'prepDialogCtrl', {dto: $scope.sql, model: prepModel.prepselection, lk: $scope.getNextId()});
                };
                
                
                $scope.openConfiguration = function(){
                    $scope.stateGo("simuconfig");
                };
                
                $scope._osm = function(){
                    $scope.stateGo("startMoulds");
                };
                
                
                
                /**************************************************************************************
                 * Run Preparation
                 **************************************************************************************/
                var setDates = function(){
                    var dates = [];
                    for (var i=0; i<$scope.sql.getNumberOfDates(); i++){
                        dates[i] = $scope.formatDateForElementId('select_from' + i);
                    }
                    return dates;
                };
                
                $scope.runPrep = function(){
                    
                    $scope.sql.setFrom(setDates());
                    $scope.sql.setCategories(categories.getCodes($scope.aggregate2));
                    $scope.sql.setPlanConfigId($scope.config.key);
                    
                    if (!$scope.sql.isValid()){
                        $scope.displayDialog('RunPrep', 'PrepNotValid');
                        return;
                    }
                    
                    prepRunRemote.queryForce({prep: $scope.model.json($scope.sql)},
                        function(result){
                            $scope.page = createPageNavigationForGeneratedHtml();
                            $scope.sql = result.getObject();
                            
                            if ($scope.sql.isAdvancedSearch()){
                                $scope.lookupSelect.code = $scope.ADVANCE_LOOKUP;
                            }
                            else{
                                $scope.lookupSelect.code = $scope.sql.getLookup();
                            }
                            
                            initialiseDates();                          
                            $scope.sql.setRun(true);
                            
                            //Remember for user clicking away and then coming back
                            config.sql          = $scope.sql;
                            config.lookupSelect = $scope.lookupSelect;
                            
                            viewLinkEncoded   = $scope.model.json($scope.sql);
                            $scope.viewLinkId = $scope.getNextId();
                            $scope.addLoadingNoReg();
                            
                            if ($scope.sql.isContainsInvalidMoulds()){
                            	var err = 'prepErr1'; //hard coded in UserRest
                                if (!angular.isDefined($rootScope.norepeat)
                                		|| $rootScope.norepeat === null
                                		|| !angular.isDefined($rootScope.norepeat[err]) 
                                		|| $rootScope.norepeat[err] === null
                                		|| !$rootScope.norepeat[err]){
                            		$scope.displayErrorDialog('MouldM3', 'MouldM4', null, err);
                                }
                            }
                            
                        },
                        function(result) { 
                            $scope.displayError(result);
                        }
                    );
                };
                
                
                var viewLinkEncoded   = $scope.model.json($scope.sql);
                $scope.viewLinkId = $scope.getNextId();
                $scope.getViewLink = function(){
                    $scope.setVisable('result_table');
                    return 'rest/prep/view?prep=' + encodeURIComponent(viewLinkEncoded) + "&lk=" + $scope.viewLinkId + '&rl=' + true;
                };

                
                /**
                 * Display totals summary dialog
                 */
                $scope._su = function(){
                    $scope.openDialog('mod/prep/view/summary_dialog.html', 'prepSummaryDialogCtrl', 
                    		{dto: $scope.sql, 
                    	     model: prepModel.prepselection,
                    	     windowClass: 'preps-dwc',
                    	     lk: $scope.getNextId()});
                };
                
                
                /**
                 * Show/Hide shipment details
                 */
                $scope._st = function(){
                	$scope.sql.setShowShipments(!$scope.sql.isShowShipments());
                	$scope.runPrep();
                };
                
                
                /************************************************
                 * Export functions 
                 ************************************************/

                //export direct to spreadsheet
                $scope._x = function(){
                	exSS();
                };
                
                var exSS = function(view){
                	prepExportSSRemote.queryForce({prep: $scope.model.json($scope.sql)}, 
                			function(result){
                		var url = 'rest/spreadsheet/return?filename=' + result.getObject();
                		$window.open(url, '_tab');
                	}, function (result){
                		alert($scope.label(result.getMessage()));
                	});
                };
                
                

                
                /**************************************************************************************
                 * Material Report
                 **************************************************************************************/
                $scope._mr = function(){
                    rptModel.prepMat.prepSql = $scope.sql;
                    $scope.stateGo(rptModel.prepMat.state);
                };
                
                
                /**************************************************************************************
                 * Run Simulation
                 **************************************************************************************/
                
                /**
                 * Open progress dialog 
                 */
                var opd = function(planNr){
                	$scope.addLoadingNoReg();
                    acDialogs.openDialog('mod/simu/view/simu_progress_dialog.html', 'simuProgressCtrl', 
                            {scope: $scope,
                             planNr: planNr,
                             model: simuModel.simuprogress,
                             windowClass: 'simu-act-menu'});
                };
                
                /**
                 * Open simulation page 
                 */
                $scope.osp = function(planNr){
                    simuModel.simurun.plan_nr = planNr;
                    $scope.stateGo(simuModel.simurun.state);    
                };


                /**
                 * Start a simulation plan
                 */
                $scope.startSimu = function(){
                    
                    //Open preparation page for engine 2
                    if ($scope.sql.getEngineNumber() === ENGINE_2){
                        simuModel.simuengine2prep.prepid = $scope.sql.getId();
                        simuModel.simuengine2prep.planconfigid = $scope.config.key;
                        simuModel.simuengine2prep.createnew = true;
                        $scope.stateGo("simuEngine2");
                        return;
                    }
                    
                    simuRunRemote.queryForce({prepid: $scope.sql.getId(), 
                    	                      planconfigid:$scope.config.key, 
                    	                      period: false,
                    	                      periodNr: $scope.sql.getPeriod()},
                        function(result){
                            var def = result.getObject();

                            if (def.isBatchMode()){
                                opd(def.getPlanNr());
                            }
                            else{
                                $scope.osp(def.getPlanNr());
                            }
                        },
                        function(result) {
                            $scope.displayError(result);
                        });
                };
                
                /**
                 * Start a period plan
                 */
                $scope.startPeriod = function(){
                	acDefinition.loadModel(prepModel.prepPeriod.model, function(){
                        acDialogs.openDialog('mod/prep/view/period_simu_dialog.html', 'prepPeriodNewCtrl', 
                                {scope: $scope,
                                 model: prepModel.prepPeriod, 
                                 windowClass: 'run-period-d'});                    
                        
                    });
                };

                //Run a period plan
                $scope.runPeriod = function(start){
                    simuRunRemote.queryForce({prepid: $scope.sql.getId(), 
                    	                      planconfigid:$scope.config.key, 
                    	                      period: true,
                    	                      periodNr: 0,
                    	                      start: start},
                            function(result){
                                var def = result.getObject();

                                if (def.isBatchMode()){
                                    opd(def.getPlanNr());
                                }
                                else{
                                    $scope.osp(def.getPlanNr());
                                }
                            },
                            function(result) {
                                $scope.displayError(result);
                            });
                };
                
                
                //Configuration selection
                var configList = $scope.getCache(simuModel.config.model);
                $scope.config  = null;
                $scope.configs = configList.selectList(null, 'Code');
                for (var j=0; j < $scope.configs.length; j++){
                    var rec = $scope.configs[j];
                    if (rec.key === $scope.sql.getPlanConfigId()){
                        $scope.config = rec;
                    }
                }
                //Use first record (which is default)
                if ($scope.config === null && $scope.configs.length > 0){
                    $scope.config = $scope.configs[0];
                }
                

                /**************************************************************************************
                 * Sort
                 **************************************************************************************/
                
                //Set sort parameters
                var setSort = function(col){

                    if (col !== null){
                        //test if column has already been clicked
                        if (config.sqlCol !== null && config.sqlCol === col){
                            config.sqlDesc = !config.sqlDesc;
                            config.sortCount++;
                        }
                        else{
                            config.sortCount = 1;
                            config.sqlDesc = false;

                        }
                        
                        //reset column sort
                        if (config.sortCount > 2){
                            col = null;
                            config.sqlDesc = false;
                            config.sortCount = 1;
                        }

                        $scope.sql.setOffset(0);
                        config.sqlCol = col;
                    }

                    $scope.sql.setOrderby(config.sqlCol);
                    $scope.sql.setOrderbyDesc(config.sqlDesc);
                };


                //Sort the result
                $scope._sc = function(col){
                    setSort(col);
                    viewLinkEncoded = $scope.model.json($scope.sql);
                };
             
                
                                

                /**************************************************************************************
                 * Categories Logic
                 **************************************************************************************/

                //Required objects to display in list
                var categories = $scope.getCache(prepModel.categories.model);

                
                //Drag 'n Drop arrays and actions
                $scope.aggregate1 = categories.getCategories();
                $scope.aggregate2 = []; 
                var dropStop      = false;
                
                $scope._dshx = function($event,index,array){
                    var d = array[index];
                    var i = indexCat(d, array);
                    $scope._dsh($event,index,array);
                    
                    var i1 = indexCat(d, $scope.aggregate1);
                    var i2 = indexCat(d, $scope.aggregate2);
                    
                    if (i !== -1 && i1 === -1 && i2 === -1){
                        array.push(d);
                    }
                };
                
                $scope._dsh = function($event,index,array){
                    if (dropStop){
                        dropStop = false;
                        return;
                    }
                    array.splice(index,1);
                    $scope.aggregate1.sort();
                };
                $scope.onDrop = function($event,$data,array){
                    for (var i=0;i<array.length;i++){
                        if (array[i] === $data){
                            return;
                        }
                    }
                    array.push($data);
                };
                //Thanks to http://stackoverflow.com/questions/586182/insert-item-into-array-at-a-specific-index
                $scope.onDropX = function($event,$data,array,index){
                    var i = indexCat($data,$scope.aggregate1);
                    var f = false;
                    if (i !== -1){
                        $scope.aggregate1.splice(i,1);
                        f = true;
                    }
                    i = indexCat($data,$scope.aggregate2);
                    if (i !== -1){
                        $scope.aggregate2.splice(i,1);
                        f = true;
                    }

                    if (!f){
                        return;
                    }

                    $scope.aggregate2.splice(index,0,$data);
                    $event.stopPropagation();
                    dropStop = true;
                };


                //Double click action
                $scope.addCategory = function (sel){
                    $scope.onDrop(null, sel, $scope.aggregate2);
                    $scope._dsh(null, indexCat(sel, $scope.aggregate1), $scope.aggregate1);
                };
                //Double click action
                $scope.removeCategory = function (sel){
                    $scope.onDrop(null, sel, $scope.aggregate1);
                    $scope._dsh(null, indexCat(sel, $scope.aggregate2), $scope.aggregate2);
                };

                //Find the category index in array
                var indexCat = function (sel, array){
                    for (var i=0;i<array.length;i++){
                        if (array[i] === sel){
                            return i;
                        }
                    }
                    return -1;
                };

               


                /**************************************************************************************
                 * Set page navigation functions
                 * These functions work with the <code>pageNavigation</code> html generator
                 * Note: there must be a valid $scope.sql $scope.viewLinkId object to work with 
                 **************************************************************************************/
                var createPageNavigationForGeneratedHtml = function(){
                    var self = {};
                    
                    self.loaded = [];
                    
                    self.select = function (index){
                        $scope.sql.select(index);
                        self.incrementLinkId(index);
                    };
                    self.previous = function (){
                        var index = $scope.sql.previous();
                        self.incrementLinkId(index);
                    };
                    self.next = function (){
                        var index = $scope.sql.next();
                        self.incrementLinkId(index);
                    };
                    
                    //Force a reload (if page has been loaded)
                    self.incrementLinkId = function (index){
                        viewLinkEncoded   = $scope.model.json($scope.sql);
                        if (self.isLoaded(index)){
                            $scope.viewLinkId = $scope.getNextId();
                            $scope.addLoadingNoReg();
                        }
                        else{
                            self.loaded.push(index);    
                        }
                    };
                    
                    //has the page already been loaded
                    self.isLoaded = function (index){
                        for (var i=0; i<this.loaded.length; i++){
                            if (this.loaded[i] === index){
                                return true;
                            }
                        }
                        return false;
                    };
                    
                    return self;
                };
                $scope.page = createPageNavigationForGeneratedHtml();
                
                
                /**************************************************************************************
                 * Refresh this controller (called each time this controller is fired)
                 **************************************************************************************/
                $scope.refreshCntrl = function(){
                    var list = categories.getDescriptions($scope.sql.getCategories());
                    for (var i=0;i<list.length;i++){
                       $scope.addCategory(list[i]);
                    }
                };
                $scope.refreshCntrl();
                
                
                
                /**************************************************************************************
                 * Load first page if available
                 **************************************************************************************/
                if ($scope.sql.isRun() && $scope.sql.getOffset() === null){
                    $scope.sql.getOffset(0);
                }

            }
    ])

   
    /**
     * Preparation Search dialog<p>
     * 
     * This dialog allows specific fields to be searched for (including date and week fields). A '***' value will be set into the general lookup field 
     * to give the user an indication that advanced search is active. If this is deleted then the advanced search is removed.<br>
     * Note: '***' is defined in entities.plan.PrepI.LOOKUP_ADVANCED
     */
    .controller('prepDialogCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'config',

            function($rootScope, $scope, acController, config){
                
                var configX = acController.createConfig('prepDialogCtrl', config.model);
                acController.configure($scope, configX);
                
                $scope.dto    = config.dto;
                
                $scope.cancel = function(){
                    config.close('ok');
                };

                $scope.search = function(){
                    if ($scope.dto.isAdvancedSearch()){
                        config.scope.lookupSelect.code = $scope.ADVANCE_LOOKUP;
                    }
                    else{
                        config.scope.lookupSelect.code = null;
                    }
                    config.scope.lookupAction();
                    config.close('ok');
                };
                
                var encoded = $scope.model.json($scope.dto);
                $scope.getViewLink = function(){
                    return 'rest/prep/viewsearch?prep=' + encodeURIComponent(encoded) + '&lk=' + config.lk;
                };

                
            }
     ])
    
     
     

    /**
     * Simu Progress Controller
     */
    .controller('simuProgressCtrl', [
            '$rootScope',
            '$scope', 
            '$timeout',
            '$window',
            'acController',
            'acDialogs',
            'config',
            'simuProgressRemote',

            function($rootScope, $scope, $timeout, $window, acController, acDialogs, config, simuProgressRemote){
                
                var configX = acController.createConfig('simuProgressCtrl');
                acController.configure($scope, configX);
                
                var close   = false;
                var footer  = false;
                var logfile = false;
                var nr      = -1;
                
                $scope.cancel = function(){
                    close = true;
                    config.close('ok');
                    $scope.removeLoading();
                };

                $scope.footer = function(){
                    return footer;
                };
                
                $scope.logfile = function(){
                	if (!angular.isDefined(nr) || nr === -1){
                		return false;
                	}
                	return true;
                };
                
                $scope.osp = function(){
                    config.close('ok');
                    $scope.removeLoading();
                    config.scope.osp(config.planNr);
                };

                //Open log file
                $scope._logf = function(){
                	if (!$scope.logfile()){
                		return;
                	}
                	$scope.cancel();
                	
                    var url = 'rest/simu/log/?planNr=' + nr;
                    $window.open(url, '_blank');
                };
                
                /* *****************************************************************************************
                 * Labels
                 *******************************************************************************************/
                $scope.title = function() {
                    return $scope.label('Simu') + ': ' +  config.planNr;
                };
                
                var message = '';
                $scope.progress = function() {
                    return message;
                };
                
                var p = {planNr:config.planNr};

                var loop = function(){
                    $timeout(function() {
                        
                        if (close){
                            return;
                        }

                        $scope.http.get($scope.remoteUrlPrefix + 'simu/progress', {params: p}).then(function(res){
                            var rtn = res.data.split(',');
                            message = rtn[0];
                            nr      = rtn[2]; 
                            
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
     * Preparation Summary dialog<p>
     */
    .controller('prepSummaryDialogCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'config',

            function($rootScope, $scope, acController, config){
                
                var configX = acController.createConfig('prepSummaryDialogCtrl', config.model);
                acController.configure($scope, configX);
                
                $scope.dto    = config.dto;
                
                $scope.close = function(){
                    config.close('ok');
                };

                var encoded = $scope.model.json($scope.dto);
                $scope.getViewLink = function(){
                    return 'rest/prep/viewsummary?prep=' + encodeURIComponent(encoded) + '&lk=' + config.lk;
                };

                
            }
     ])
     

    /**
     * Start a period plan Controller
     */
    .controller('prepPeriodNewCtrl', [
            '$rootScope',
            '$scope', 
            '$timeout',
            'acController',
            'acDialogs',
            'config',
            'prepPeriodValidateRemote',

            function($rootScope, $scope, $timeout, acController, acDialogs, config, 
            		prepPeriodValidateRemote){
                
                var configX = acController.createConfig('prepPeriodNewCtrl', config.model);
                acController.configure($scope, configX);


                $timeout(function() {
                    var el = document.getElementsByClassName("modal-dialog " + config.windowClass);
                    for (var i = 0; i < el.length; ++i) {
                        var item = el[i];  
                        item.style.width = "400px";
                    }
                }, 100);

                           
                $scope.dto = $scope.model.createDto();
                
                $scope.cancel = function(){
                    config.close('ok');
                };
                
                $scope.runLabel = function(){
                	return $scope.label('Period.StW');
                }; 
                
                $scope.validationMessage = null;
                $scope.setValidationMessage = function(message){
                    $scope.validationMessage = message;
                };
                
                $scope.run = function(){
                	
                    prepPeriodValidateRemote.queryForce(

                            {dto: $scope.model.json($scope.dto)},
                            function(result){
                            		var dtoX = result.getObject();
                            		$scope.model.configureObject(dtoX);
                            		
                            		if (dtoX.isValid()){
                            			config.scope.runPeriod(dtoX.getStart());
                            			config.close('ok');
                            			return;
                            		}
                            		$scope.setValidationMessage(dtoX.getMessage());
                                }, function (result){
                                	$scope.setValidationMessage(result.getMessage());
                            });
                	
                };
            }
     ])
     
     
;
