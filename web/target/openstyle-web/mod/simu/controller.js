'use strict';

angular.module('mod.simu.controllers', ['ngDragDrop', 'pasvaz.bindonce'])


    /**
     * Simulation selection and working page
     */
    .controller('simuCtrl', [
            '$rootScope',
            '$scope', 
            '$timeout',
            '$http',
            '$window',
            'acController',
            'acGlobal',
            'acDialogs',
            'acDefinition',
            'simuModel',
            'simuRemote',
            'simuListRemote',
            'simuLoadPageRemote',
            'simuExportSSRemote',
            'simuUpdateRemote',
            'simuMouldCodeRemote',
            'simuMoveMouldRemote',
            'simuDeleteMouldRemote',
            'simuFixValidateRemote',
            'simuFixCurrentRemote',
            'simuExportCsvDateRemote',
            'periodFunctions',
            'periodSetActiveRemote',
            'simuToggleRemote',
             
            function($rootScope, $scope, $timeout, $http, $window, 
                    acController, acGlobal, acDialogs, acDefinition, 
                    simuModel, simuRemote, simuListRemote, simuLoadPageRemote, simuExportSSRemote, 
                    simuUpdateRemote, simuMouldCodeRemote, simuMoveMouldRemote, simuDeleteMouldRemote,
                    simuFixValidateRemote, simuFixCurrentRemote, simuExportCsvDateRemote, 
                    periodFunctions, periodSetActiveRemote, simuToggleRemote
                    ){
             	
            	
            	var config = acController.createConfig('simuCtrl', simuModel.simuselection);
                config.isCachedObjectList   = false;
                config.editmode             = true;
                config.title                = 'Simu.T';
                acController.configure($scope, config);

                
                var sbs = $scope.moveScroller('sidebaraction');
                var hs1 = $scope.moveScroller('header');
                var hsx = $scope.moveScroller('headerx', 20);
                var hsp = $scope.moveScroller('headerp');
                
                /*-*****************************
                 * Specific controller methods
                 ******************************/
                $scope.sql            = $scope.cacheObj;
                $scope.selectedPlanNr = null;
                $scope.lookupSelect   = {code: null};
               
                //User is returning to this page
                if (angular.isDefined(config.sql)){
                    $scope.sql            = config.sql;
                    $scope.selectedPlanNr = $scope.sql.getPlanNr();
                    $scope.lookupSelect   = config.lookupSelect;
                    $scope.setVisable('result_table_simu');
                }
                //First time
                else{
                    config.sql          = $scope.sql;
                    config.lookupSelect = $scope.lookupSelect;
                }
               
               

                /*-*****************************
                 * View control
                 ******************************/

                //Encode the call
                var setViewLinkEncoded = function(sql){
                    
                    if (sql.getPlanNr() === null 
                            || sql.getPlanNr().length === 0
                            || testNaN(sql.getPlanNr())){
                        return null;
                    }

                    sql.setPlanNr(parseInt(sql.getPlanNr()));
                    return $scope.model.json($scope.sql);
                };

                var viewLinkEncoded = null;
                $scope.viewLinkId  = $scope.getNextId();

                //Force a view change
                var setView = function(sql){
                    viewLinkEncoded = setViewLinkEncoded(sql);
                    if (viewLinkEncoded == null){
                        return;
                    }
                    config.sql = sql;
                    $scope.viewLinkId  = $scope.getNextId();
                    $scope.setVisable('result_table_simu');
                    $scope.addLoadingNoReg();
                };
                
                //Thanks to http://stackoverflow.com/questions/21686986/run-javascript-after-angular-has-finished-loading-all-views
                $scope.$on('$includeContentLoaded', function() {
                    sbs();
                    hs1();
                    hsx();
                    hsp();
                });
                
                //Provide view link
                $scope.getViewLink = function(){
                    if (viewLinkEncoded === null){
                        return;
                    }
                    return 'rest/simu/view?simu=' + encodeURIComponent(viewLinkEncoded) + '&lk=' + $scope.viewLinkId + '&rl=' + true;
                };

                
                var viewPartialEncoded = null;
               
                //Force a partial view change
                var setPartialView = function(sql, id){
                    viewPartialEncoded = setViewLinkEncoded(sql);
                    if (viewPartialEncoded == null){
                        return;
                    }
                    $scope.setVisable(id);
                };
                
                //Provide partial view link
                $scope.getViewLinkPartial = function(){
                    if (viewPartialEncoded === null){
                        return;
                    }
                    return 'rest/simu/view' + viewPartialEncoded + '&lk=' + $scope.viewLinkId;
                };
                
                //Force open a simulation 
                var openSimu = function (nr){
                    $scope.sql.setPlanNr(nr);
                    $scope.selectedPlanNr = nr;
                    setView($scope.sql);
                };
                
                //Controller was opened from generation action in another page
                if (angular.isDefined(simuModel.simurun.plan_nr) && simuModel.simurun.plan_nr !== null){
                    $scope.sql.setFixPlan(false);
                    openSimu(simuModel.simurun.plan_nr);
                    simuModel.simurun.plan_nr = null; 
                }
                else{
                    simuFixCurrentRemote.queryForce(null, 
                            function(result){
                                 var nr = result.getObject();
                                 if (nr > 0){
                                     openSimu(nr);
                                 }
                                }, function (result){
                                   alert($scope.label(result.getMessage()));
                            });
                }
                
                
                /*-***********************************************
                 * View functions (called from generated HTML)
                 ************************************************/
                var scrollLeft = null;
                var scrollTop  = null;
                
                var setWindowScroll = function(){
                    scrollLeft = (window.pageXOffset !== undefined) ? window.pageXOffset : (document.documentElement || document.body.parentNode || document.body).scrollLeft,
                    scrollTop = (window.pageYOffset !== undefined) ? window.pageYOffset : (document.documentElement || document.body.parentNode || document.body).scrollTop;
//                    config.scrollLeft = scrollLeft;
//                    config.scrollTop  = scrollTop;
                };

                //Thanks to http://stackoverflow.com/questions/1144805/how-do-i-scroll-to-the-top-of-the-page-with-jquery
                //Thanks to http://stackoverflow.com/questions/25711867/angular-window-scroll
                var resetWindow = function(t, l){
                    if (!angular.isDefined(t) || t === null){
                        t = 0;
                    }
                    if (!angular.isDefined(l) || l === null){
                        l = 0;
                    }
                    
                    $timeout(function() {   
                        $window.scrollTo(l, t);
                    });
                };
              
                //Called from SimuHtml
                $scope._rwX = function(t, l){
                    if (!angular.isDefined(t) || t === null){
                        t = scrollTop;
                    }
                    if (!angular.isDefined(l) || l === null){
                        l = scrollLeft;
                    }
                    resetWindow (t, l);
                };

                //Select machine
                $scope._ms = function(nr){
                    $scope.sql.setMachineNr(nr);
                    $scope.sql.setStartPeriod(null);
                    $scope.sql.setEndPeriod(null);
                    setView($scope.sql);
                    resetWindow();
                };
                
                //Select machine and view
                $scope._msv = function(nr, v){
                    $scope.sql.setMachineNr(nr);
                    $scope.sql.setStartPeriod(null);
                    $scope.sql.setEndPeriod(null);
                    $scope.sql.setViewOption(v);
                    setView($scope.sql);
                    resetWindow();
                };
                
                //Load next page
                $scope._mp = function(nr, start, end){
                    $scope.sql.setMachineNr(nr);
                    $scope.sql.setStartPeriod(start);
                    $scope.sql.setEndPeriod(end);
                    $scope.setVisable ('simu_partial_f_' + end, false);
                    setPartialView($scope.sql, 'simu_partial_v_' + end);
                };
                
                //Display fix links
                $scope._fx = function(value){
                    $scope.sql.setFixPlan(value);
                    setView($scope.sql);    
                    resetWindow();
                };
                
                
                //Show instance detail
                $scope._si = function(ids, period, invalid){
                    setWindowScroll();
                    $scope.sql.setInstanceIds(ids);
                    $scope.sql.setInstanceInvalid(invalid);
                    $scope.sql.setPeriodDetailView(period);
                    setView($scope.sql);
                    $scope.sql.setInstanceIds(null);
                    $scope.sql.setInstanceInvalid(null);
                    $scope.sql.setPeriodDetailView(-1);
                };
                
                //Show cycle details
                $scope._sc = function(machineId, startP, endP){
                    setWindowScroll();
                    $scope.sql.setCycleMachineId(machineId);
                    $scope.sql.setCycleStartPeriod(startP);
                    $scope.sql.setCycleEndPeriod(endP);
                    setView($scope.sql);
                    $scope.sql.setCycleMachineId(null);
                    $scope.sql.setCycleStartPeriod(null);
                    $scope.sql.setCycleEndPeriod(null);
                };
                
                //return to simulation
                $scope._rs = function(){
                	setView($scope.sql);
                };
                
                $scope.refreshSimu = function(){
                    setView($scope.sql);
                };
                
                
                $scope.getSimu = function(){
                    if (testNaN($scope.selectedPlanNr)){
                        return;
                    }
                    $scope.sql.setPlanNr($scope.selectedPlanNr);
                    setView($scope.sql);  
                };
                
                
                //Load the requested page
                $scope.loadPage = function(index){
                    $scope.sql.setPageIndex(index);
                    setView($scope.sql);    
                };
                
                
                //Change view
                $scope._cv = function(view){
                    $scope.sql.setViewOption(view);
                    setView($scope.sql);    
                    resetWindow();
                };
                
                //Open log file
                $scope._logf = function(view){
                    var url = 'rest/simu/log/?planNr=' + $scope.sql.getPlanNr();
                    $window.open(url, '_blank');
                };
                

                /*-***********************************************
                 * Simu / Period list 
                 ************************************************/
                $scope._lx = function(){
                	simuListRemote.queryForce(null, 
            			function(result){
            			    showList(result);
                		}, function (result){
                			alert($scope.label(result.getMessage()));
                		});
            	};
                
                var showList = function (result){
                	acDialogs.openDialog('mod/simu/view/simu_list_dialog.html', 'simuListCtrl',
        					{scope:     $scope,
        				model:     $scope.model,
        				list:      result.getObject(),
        				windowClass:'simu-list-d'});
                };
                
                
                
                /*-***********************************************
                 * Export functions 
                 ************************************************/

                //export direct to spreadsheet
                $scope._x = function(view){
                    exSS();
                };
                
                var exSS = function(view){
                    simuExportSSRemote.queryForce({planNr: $scope.sql.getPlanNr()}, 
                            function(result){
                        var url = 'rest/spreadsheet/return?filename=' + result.getObject();
                        $window.open(url, '_tab');
                    }, function (result){
                        alert($scope.label(result.getMessage()));
                    });
                };
                
                
                //Export dialog
                $scope._xf = function(view){
                    simuExportCsvDateRemote.queryForce({planNr: $scope.sql.getPlanNr()}, 
                            function(result){
                        
                        var date = null;
                        if (result.getObject() !== null){
                            var o = acGlobal.globals().timezoneOffset;
                            date = new Date(result.getObject()  + o);
                        }
                        
                        acDialogs.openDialog('mod/simu/view/export_dialog.html', 'simuExportCtrl',
                             {scope:     $scope,
                              model:     $scope.model,
                              planNr:    $scope.sql.getPlanNr(),
                              sql:       $scope.sql,
                              date:      date,
                              view:      view,
                              exSS:      exSS,
                              windowClass:'simu-act-menu'});
                        
                    }, function (result){
                        alert($scope.label(result.getMessage()));
                    });
                };
                
                
                
                
                /*-***********************************************
                 * Lookup functions 
                 ************************************************/
                
                /**
                 * Used by sidebar_actions.html typeahead
                 * Override
                 */
                $scope.lookup = function(val) {
                    
                    var p = {limit: 15, planNr: $scope.sql.getPlanNr(), code: val};
                    
                    return $scope.http.get($scope.remoteUrlPrefix + 'simu/lookup', {params: p}).then(function(res){
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
                    $scope.sql.setOffset(0);
                    $scope.sql.setLookup($scope.lookupSelect.code);
                    setView($scope.sql);
                };
                
               
                /**
                 * Open advance search dialog
                 */
                $scope.lookupAdvance = function(){
                    $scope.openDialog('mod/simu/view/search_dialog.html', 'simuDialogCtrl', {dto: $scope.sql, model: simuModel.simuselection, lk: $scope.getNextId()});
                };
                
                
                
                /*-***********************************************
                 * Detail functions 
                 ************************************************/

                /**
                 * Set updates to actual fields and recalculate the station
                 */
                $scope._ig = function(ids){
                    simuUpdateRemote.queryForce({instanceIds: ids, planNr: $scope.sql.getPlanNr()},
                            function(result){
                                $scope._si(ids);
                            },
                            function(result) { 
                                $scope.displayError(result);
                            }
                    );
                };
                
                /**
                 * Update Selection
                 */
                $scope._ic = function(id){
                    var el = angular.element('#icb' + id);
                    var active = el.is(':checked');
                    updateRemote({recid: id, select: active});
                };

                /**
                 * Update mould style quantity
                 */
                var lastQty = {id:0,v:0};
                $scope._iq = function(id){
                    var v  = document.getElementById('qty' + id).value;
                    if (!testNumber(id,v,lastQty)){
                        return;
                    }
                    if (!testInput(id,v,lastQty)){
                        return;
                    }
                    updateRemote({recid: id, qty: v});
                };
                
                /**
                 * Update style sequence
                 */
                var lastSeq = {id:0,v:0};
                $scope._is = function(id){
                    var v  = document.getElementById('seq' + id).value;
                    if (!testNumber(id,v,lastSeq)){
                        return;
                    }
                    if (!testInput(id,v,lastSeq)){
                        return;
                    }
                    updateRemote({recid: id, seq: v});
                };
                
                
                /**
                 * Update Selection
                 */
                var updateRemote = function(param, callback){
                    simuUpdateRemote.queryForce(param,
                            function(result){
                    			if (angular.isDefined(callback)){
                    				callback();
                    			}
                            },
                            function(result) { 
                                $scope.displayError(result);
                            }
                    );
                };
                
                /**
                 * Test for repeat input
                 */
                var testInput = function (id, v, last){
                    if (last.id === id && last.v === v){
                        return false;
                    }
                    last.id = id;
                    last.v  = v;
                    return true;
                };
                
                /**
                 * Test for valid number
                 */
                var testNumber = function (id, v, last){
                    if (testNaN(v)){
                        if (last.id === id){
                            return;
                        }
                        last.id = id;
                        $scope.displayErrorDialog('Invalid', 'InvalidEntry');
                        return false;
                    }
                    return true;
                };
                
                /**
                 * Add style to instance
                 */
                $scope._ai = function(instanceId, mouldId, size, seq, instanceIds){
                    acDefinition.loadDef(simuModel.simuAddStyle.model, function(dto){
                        
                        acDialogs.openDialog('mod/simu/view/style_add_dialog.html', 'simuStyleAddCtrl', 
                                {scope: $scope,
                                 planNr: $scope.sql.getPlanNr(),
                                 instanceId:instanceId,
                                 instanceIds:instanceIds,
                                 mouldId: mouldId,
                                 size: size, 
                                 seq: seq,
                                 model: simuModel.simuAddStyle, 
                                 windowClass: 'simu-act-menu'});                    
                        
                    });
                };
                
                
                /**
                 * Move styles to another instance
                 */
                $scope._mi = function(instanceId, mouldCodeId, instanceIds){
                    acDefinition.loadDef(simuModel.simuMoveStyle.model, function(dto){
                        
                        acDialogs.openDialog('mod/simu/view/style_move_dialog.html', 'simuStyleMoveCtrl', 
                                {scope:       $scope,
                                 planNr:      $scope.sql.getPlanNr(),
                                 instanceId:  instanceId,
                                 instanceIds: instanceIds,
                                 mouldCodeId: mouldCodeId,
                                 model:       simuModel.simuMoveStyle,
                                 lk:          $scope.getNextId(),
                                 windowClass: 'simu-act-menu'});                    
                        
                    });
                };
                

                
                
                /*-***********************************************
                 * Mould functions 
                 ************************************************/
                //Drop instance into mould
                $scope._d = function($event,$data,instanceId){
                    setWindowScroll();
                    simuMouldCodeRemote.queryForce(
                            {planNr: $scope.sql.getPlanNr(),
                             ids: '' + $data.id + ',' + instanceId}, 
                            function(result){
                                 var s = result.getObject().split(',');
                                 var fromC = s[0];
                                 var toC   = s[1];
                                 
                                 acDialogs.openDialog('mod/simu/view/mould_move_dialog.html', 'simuMouldMoveCtrl',
                                         {scope: $scope,
                                     planNr: $scope.sql.getPlanNr(),
                                     from:   $data.id,
                                     fromC:  fromC,
                                     to:     instanceId,
                                     toC:    toC,
                                     model:  simuModel.simuselection,
                                     sql:    $scope.sql,
                                     setView:setView,
                                     windowClass:'simu-act-menu'});
                                 
                                }, function (result){
                                   alert($scope.label(result.getMessage()));
                            });
                    
                };

                //Drop instance into empty start mould position
                $scope._ds = function($event,$data,machineId, stationNr){
                    setWindowScroll();
                    simuMoveMouldRemote.queryForce(
                            {planNr: $scope.sql.getPlanNr(),
                             from: $data.id,
                             to: -102,
                             skey: machineId + ','  + stationNr,
                             action: 'after'}, 
                            function(result){
                                   setView($scope.sql);
                                }, function (result){
                                   alert($scope.label(result.getMessage()));
                            });
                    
                };
                
                
                //Drop instance into clip board
                $scope._dx = function($event,$data,instanceId){
                    setWindowScroll();
                    simuMoveMouldRemote.queryForce(
                            {planNr: $scope.sql.getPlanNr(),
                             from: $data.id,
                             to: -101,
                             action: 'after'}, 
                            function(result){
                                   setView($scope.sql);
                                }, function (result){
                                   alert($scope.label(result.getMessage()));
                            });
                };

                
                //Add mould to clip board
                $scope._am = function(){
                    setWindowScroll();
                    acDefinition.loadDef(simuModel.simuAddMould.model, function(dto){
                        
                        acDialogs.openDialog('mod/simu/view/mould_add_dialog.html', 'simuMouldAddCtrl', 
                                {scope: $scope,
                                 planNr: $scope.sql.getPlanNr(),
                                 model: simuModel.simuAddMould, 
                                 windowClass: 'simu-act-menu'});                    
                        
                    });
                };


                
                /*-***********************************************
                 * Context Menu functions 
                 ************************************************/
                //Used by context menu to store instance_id
                $scope.menu_ids = null;
                
                //Move mould to clipboard 
                $scope._mv = function(id){
                    simuMoveMouldRemote.queryForce(
                            {planNr: $scope.sql.getPlanNr(),
                             from: id,
                             to: -101,
                             action: 'after'}, 
                            function(result){
                                   setView($scope.sql);
                                }, function (result){
                                   alert($scope.label(result.getMessage()));
                            });
                };

                
                //Delete mould 
                $scope._dl = function(id){
                    simuDeleteMouldRemote.queryForce(
                            {planNr: $scope.sql.getPlanNr(),
                             id: id}, 
                            function(result){
                                   setView($scope.sql);
                                }, function (result){
                                   alert($scope.label(result.getMessage()));
                            });
                };
                
                /*-***********************************************
                 * Fix functions 
                 ************************************************/
                
                //Fix station(s)
                $scope._fs = function(machine_id, station, s, e){
                    
                    simuFixValidateRemote.queryForce(
                            {planNr:    $scope.sql.getPlanNr(),
                             machine_id:machine_id, 
                             station:   station, 
                             periodS:   s,
                             periodE:   e}, 
                            function(result){
                                 var s = result.getObject().split(':');
                                 var sText    = s[0];
                                 var bText    = s[1];
                                 var stations = s[2];
                                 
                                 acDialogs.openDialog('mod/simu/view/fix_dialog.html', 'simuFixCtrl',
                                         {scope:     $scope,
                                          planNr:    $scope.sql.getPlanNr(),
                                          machine_id:machine_id, 
                                          station:   station, 
                                          stations:  stations,
                                          period:    e,
                                          sText:     sText,
                                          bText:     bText,
                                          model:     simuModel.simuselection,
                                          sql:       $scope.sql,
                                          setView:   setView,
                                          windowClass:'simu-act-menu'});
                                 
                                }, function (result){
                                	$scope.displayErrorDialog($scope.label('Fix'),$scope.label(result.getMessage()));
                            });
                    
                };
                
                
                //Fix station(s) confirmation / fix number display
                $scope._fsc = function(number){
                    acDialogs.openDialog('mod/simu/view/fix_confirmation_dialog.html', 'simuFixConfirmCtrl',
                            {scope: $scope,
                        planNr: $scope.sql.getPlanNr(),
                        number: number, 
                        model:  simuModel.simuselection,
                        sql:    $scope.sql,
                        setView:setView,
                        windowClass:'simu-act-menu'});
                };
                
                


                /*-***********************************************
                 * Period Simulation functions
                 ************************************************/

                //Make plan active
                $scope._pa = function(id){
                    periodSetActiveRemote.queryForce(
                            {planNr: $scope.sql.getPlanNr()}, 
                            function(result){
                                acDialogs.openDialog('mod/simu/view/period_active_dialog.html', 'simuPeriodActiveCtrl', 
                                        {scope: $scope,
                                         planNr: $scope.sql.getPlanNr(),
                                         dto: result.getObject(),
                                         sql:$scope.sql,
                                         setView:setView,
                                         windowClass: 'simu-act-menu'});
                                }, function (result){
                                   alert($scope.label(result.getMessage()));
                            });
                };

                periodFunctions.create($scope, setView, resetWindow, setWindowScroll, simuUpdateRemote, simuMoveMouldRemote, simuToggleRemote);
                
            }
    ])


    /**
     * Simulation Search dialog<p>
     * 
     * This dialog allows specific fields to be searched for. A '***' value will be set into the general lookup field 
     * to give the user an indication that advanced search is active. If this is deleted then the advanced search is removed.<br>
     * Note: '***' is defined in entities.plan.PrepI.LOOKUP_ADVANCED
     */
    .controller('simuDialogCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'config',

            function($rootScope, $scope, acController, config){
                
                var configX = acController.createConfig('simuDialogCtrl', config.model);
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
                    return 'rest/simu/viewsearch?simu=' + encodeURIComponent(encoded) + '&lk=' + config.lk;
                };

                
            }
     ])

    
    /**
     * Move Styles to another Mould Instance dialog<p>
     */
    .controller('simuStyleMoveCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'config',
            'simuMoveStyleRemote',

            function($rootScope, $scope, acController, config, simuMoveStyleRemote){
                
                var configX = acController.createConfig('simuStyleMoveCtrl', config.model);
                acController.configure($scope, configX);
                
                $scope.dto = $scope.model.createDto();
                $scope.dto.setPlanNr(config.planNr);
                $scope.dto.setInstanceIdFrom(config.instanceId);
                $scope.dto.setMouldCodeId(config.mouldCodeId);
                
                
                $scope.cancel = function(){
                    config.close('ok');
                };
                
                $scope._ms = function(instance_id, action){
                    $scope.dto.setInstanceIdTo(instance_id);
                    $scope.dto.setAction(action);
                    
                    var saveList = [];
                    saveList.push($scope.dto);
                    
                    simuMoveStyleRemote.post(
                            saveList,  
                            null, 
                            function(result){
                                   config.close('ok');   
                                   config.scope._si(config.instanceIds);
                                }, 
                            function (result){
                                   alert($scope.label(result.getMessage()));
                            });
                };
                
                var encoded = $scope.model.json($scope.dto);
                $scope.getViewLink = function(){
                    return 'rest/simu/viewmovestyles?simu=' + encodeURIComponent(encoded) + '&lk=' + config.lk;
                };

                
            }
     ])

     
    
    /**
     * Move Moulds dialog
     */
    .controller('simuMouldMoveCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'acDialogs',
            'config',
            'simuMoveMouldRemote',

            function($rootScope, $scope, acController, acDialogs, config, simuMoveMouldRemote){
                
                var configX = acController.createConfig('simuMouldMoveCtrl', config.model);
                acController.configure($scope, configX);

                $scope.title = function(){
                    return $scope.label('SimuMMove') + ': ' + config.fromC;
                };
                
                $scope.swapLabel = function(){
                    return $scope.label("SimuMx") + ': ' + config.fromC + ' <> ' + config.toC;
                };
                
                $scope.beforeLabel = function(){
                    return '' + $scope.label('SimuMb') + ': ' + config.toC;
                };
                
                $scope.afterLabel = function(){
                    return $scope.label('SimuMa') + ': ' + config.toC;
                };
                
                $scope.swap = function(){
                    move('swap');
                };
                
                $scope.before = function(){
                    move('before');
                };
                
                $scope.after = function(){
                    move('after');
                };
                
                $scope.cancel = function(){
                    config.close('ok');
                };
                
                var move = function(action){
                    simuMoveMouldRemote.queryForce(
                            {planNr: config.planNr,
                             from: config.from,
                             to: config.to,
                             action: action}, 
                            function(result){
                                   config.close('ok');    
                                   config.setView(config.sql);
                                }, function (result){
                                   alert($scope.label(result.getMessage()));
                            });
                };
                
                
            }
     ])
            
     
     /**
     * Add Styles Controller
     */
    .controller('simuStyleAddCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'acDialogs',
            'config',
            'simuAddStyleRemote',

            function($rootScope, $scope, acController, acDialogs, config, simuAddStyleRemote){
                
                var configX = acController.createConfig('simuStyleAddCtrl', config.model);
                acController.configure($scope, configX);
                               
                $scope.dto = $scope.model.createDto();
                $scope.dto.setPlanNr(config.planNr);
                $scope.dto.setInstanceId(config.instanceId);
                $scope.dto.setSequenceNr(config.seq);
                                
                
                /* *****************************************************************************************
                 * Lookups
                 *******************************************************************************************/
                $scope.lookupStyle = function(val) {
                    
                    var p = {limit: 15, code: val, desc:false, mouldId: config.mouldId};
                    
                    return $scope.http.get($scope.remoteUrlPrefix + 'mdata/lookup', {params: p}).then(function(res){
                        var list = [];
                        for (var i=0; i<res.data.o.length; i++){
                            list.push(res.data.o[i]);
                        }
                        return list;
                    });
                };
                
                
                /* *****************************************************************************************
                 * Sizes
                 *******************************************************************************************/
                $scope.sizes = [];
                if (config.size.indexOf(",") === -1){
                    $scope.sizeSel = config.size;
                    $scope.dto.setSize($scope.sizeSel);
                }
                else{
                    var x = config.size.split(",");
                    for (var i=0;i<x.length;i++){
                        $scope.sizes[i] = {key:i, value:x[i]};
                    }
                    $scope.sizeSel = $scope.sizes[0];
                }
                
                $scope.isSizeSelect = function(){
                    return $scope.sizes.length > 1;
                };
                
                
                
                $scope.cancel = function(){
                    config.close('ok');
                };
                
                $scope.add = function(){
                    var saveList = [];
                    
                    if ($scope.isSizeSelect()){
                        $scope.dto.setSize($scope.sizeSel.value);
                    }
                    
                    saveList.push($scope.dto);
                    
                    simuAddStyleRemote.post(
                            saveList,  
                            null,
                            function(result){
                                config.close('ok');    
                                config.scope._si(config.instanceIds);
                            }, 
                            function (result){
                                alert($scope.label(result.getMessage()));
                            }
                    );
                };
            }
     ])
    

    /**
     * Add Mould Controller
     */
    .controller('simuMouldAddCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'acDialogs',
            'config',
            'simuAddMouldRemote',

            function($rootScope, $scope, acController, acDialogs, config, simuAddMouldRemote){
                
                var configX = acController.createConfig('simuMouldAddCtrl', config.model);
                acController.configure($scope, configX);
                               
                $scope.dto = $scope.model.createDto();
                $scope.dto.setPlanNr(config.planNr);
                
                /* *****************************************************************************************
                 * Lookups
                 *******************************************************************************************/
                $scope.lookupMould = function(val) {
                    
                    var p = {limit: 15, code: val};
                    
                    return $scope.http.get($scope.remoteUrlPrefix + 'mdata/mould/lookup/m', {params: p}).then(function(res){
                        var list = [];
                        for (var i=0; i<res.data.o.length; i++){
                            list.push(res.data.o[i]);
                        }
                        return list;
                    });
                };
                
                
                $scope.cancel = function(){
                    config.close('ok');
                };
                
                $scope.add = function(){
                    var saveList = [];
                    saveList.push($scope.dto);
                    
                    simuAddMouldRemote.post(
                            saveList,  
                            null,
                            function(result){
                                config.close('ok');    
                                config.scope.refreshSimu();
                            }, 
                            function (result){
                                alert($scope.label(result.getMessage()));
                            }
                    );
                };
            }
     ])
    

    /**
     * Fix station(s) controller
     */
    .controller('simuFixCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'acDialogs',
            'config',
            'simuFixRemote',

            function($rootScope, $scope, acController, acDialogs, config, simuFixRemote){
                
                var configX = acController.createConfig('simuFixCtrl', config.model);
                acController.configure($scope, configX);

                $scope.title = function(){
                    return $scope.label('FixP');
                };
                
                $scope.columnLabel = function(){
                    return config.sText;
                };
                
                $scope.blockLabel = function(){
                    return config.bText;
                };
                
                $scope.isFixV = function(){
                    return config.sText.length > 0;
                };
                
                $scope.fixColumn = function(){
                    fix('column');
                };
                
                $scope.isFixH = function(){
                    return config.bText.length > 0;
                };
                
                $scope.fixBlock = function(){
                    fix('block');
                };
                
                $scope.cancel = function(){
                    config.close('ok');
                };
                
                var fix = function(action){
                    simuFixRemote.queryForce(
                            {planNr:    config.planNr,
                             machine_id:config.machine_id, 
                             station:   config.station,
                             stations:  config.stations,
                             period:    config.period,
                             action:    action}, 
                            function(result){
                                   config.close('ok');    
                                   config.setView(config.sql);
                                   config.scope._fsc(result.getObject());
                                }, function (result){
                                   config.close('ok');
                                   config.scope.displayErrorDialog($scope.label('Fix'),$scope.label(result.getMessage()));
                            });
                };
                
                
            }
     ])
                 

  
    /**
     * Fix station(s) confirmation controller
     */
    .controller('simuFixConfirmCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'acDialogs',
            'config',
            'simuFixRemote',

            function($rootScope, $scope, acController, acDialogs, config, simuFixRemote){
                
                var configX = acController.createConfig('simuFixConfirmCtrl', config.model);
                acController.configure($scope, configX);

                $scope.title = function(){
                    return $scope.label('FixSC');
                };
                
                $scope.number = function(){
                    return config.number;
                };
                
                $scope.cancel = function(){
                    config.close('ok');
                };
                
            }
     ])

     
    /**
     * Export controller
     */
    .controller('simuExportCtrl', [
            '$rootScope',
            '$scope', 
            '$window',
            'acController',
            'acDialogs',
            'config',
            'simuExportCsvRemote',

            function($rootScope, $scope, $window, acController, acDialogs, config, simuExportCsvRemote){
                
                var configX = acController.createConfig('simuExportCtrl');
                acController.configure($scope, configX);

                $scope.upto = {};
                $scope.upto.d = config.date;
                
                $scope.title = function(){
                    return $scope.label('ExportSF');
                };
                
                $scope.cancel = function(){
                    config.close('ok');
                };
                
                //export direct to spreadsheet
                $scope._x = function(){
                    $scope.cancel();
                    config.exSS(config.view);
                };
                
                //export direct to CSV
                $scope._xf = function(){
                    $scope.cancel();
                    
                    var date = config.model.formatDate($scope.upto.d, 'dd.MM.yyyy');
                    
                    simuExportCsvRemote.queryForce({planNr: config.planNr, upto: date}, 
                            function(result){
                        var url = 'rest/spreadsheet/return?filename=' + result.getObject();
                        $window.open(url, '_tab');
                    }, function (result){
                        alert($scope.label(result.getMessage()));
                    });
                };
                
                
            }
     ])

     
          
    /**
     * Simulation List controller
     */
    .controller('simuListCtrl', [
            '$rootScope',
            '$scope', 
            '$window',
            'acController',
            'acDialogs',
            'config',
            
            function($rootScope, $scope, $window, acController, acDialogs, config){
                
                var configX = acController.createConfig('simuListCtrl');
                acController.configure($scope, configX);

                $scope.list = config.list;
                
                $scope.cancel = function(){
                    config.close('ok');
                };
                
                $scope.count = function(rec){
                    return $scope.label('RecordCount') + ": " + $scope.list.length; 
                };
                
                $scope.select = function(rec){
                    $scope.cancel();
                    config.scope.selectedPlanNr = rec.getNr(); 
                    config.scope.getSimu();
                };
            }
     ])


     
     
     /**
     * Simulation Configuration
     */
    .controller('simuConfigCtrl', [
            '$rootScope',
            '$scope', 
            'acController', 
            'simuModel',
            'simuConfigRemote',
    
            function($rootScope, $scope, acController, simuModel, simuConfigRemote){
    
                var config = acController.createConfig('simuConfigCtrl', simuModel.config);
                config.title                = 'SimuConfig';
                config.remotePost           = simuConfigRemote;
                config.sort_predicate_field = 'Code';
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                $scope.moveScroller('reqtyp-t', 100);
                
                var id = 0;
                $scope.dto = null;
                
                $scope.select = function(record){
                    id = record.getId();
                    $scope.dto = record;
                    config.dto = record;
                    
                    for (var i=0;i<$scope.list.length; i++){
                        $scope.list[i].clearSelect();
                    }
                    
                    record.setSelect();
                    viewLinkEncoded = linkEncode();
                };
                
                //Override
                var eFn = $scope.recordEdit; 
                $scope.recordEdit = function(){
                    eFn();
                    viewLinkEncoded = linkEncode();
                };
                
                //Provide view link
                var linkEncode = function(){
                    return '?id=' + id + '&em=' + $scope.isEdit();
                };
                var viewLinkEncoded = linkEncode();
                
                $scope.getViewLink = function(){
                    return 'rest/simu/config/view' + viewLinkEncoded;
                };
                
                //User is returning to this page
                if (angular.isDefined(config.dto)){
                    $scope.select(config.dto);
                }
                else{
                    if ($scope.list.length > 0){
                        $scope.select($scope.list[0]);
                    }
                }

                //Override, need to reselect in order to 'hook-up' the new dto object
                var sFn = $scope.postSuccess;
                $scope.postSuccess = function(result){
                    sFn(result);
                    var dtox = $scope.findById(id);
                    $scope.select(dtox);
                };
            }
    ])

    
    /**
     * Simulation Start Moulds
     */
    .controller('simuStartMouldCtrl', [
            '$rootScope',
            '$scope', 
            '$timeout',
            'acController', 
            'simuModel',
            'simuStartMouldRemote',
            'mdataModel',
    
            function($rootScope, $scope, $timeout, acController, simuModel, simuStartMouldRemote, mdataModel){
    
                var config = acController.createConfig('simuStartMouldCtrl', simuModel.startMould);
                config.title                = 'SimuSMT';
                config.remotePost           = simuStartMouldRemote;
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.datepickerShowButtonBar = true;
                
                var sqlM = $scope.getCacheModel(simuModel.startMould.modelSql);
                var sql = sqlM.createDto();
                
                
                //Override
                var eFn = $scope.recordEdit; 
                $scope.recordEdit = function(){
                    eFn();
                    setViewLinkEncoded();
                };
                
                var viewLinkId = $scope.getNextId();
                
                //Provide view link
                var viewLinkEncoded = null;
                var setViewLinkEncoded = function(){
                    viewLinkEncoded = encodeURIComponent(sqlM.json(sql)) + '&em=' + $scope.isEdit() + '&lk=' + viewLinkId;
                };
                setViewLinkEncoded();
                
                $scope.getViewLink = function(){
                    return 'rest/simu/startmould/view?sql=' + viewLinkEncoded;
                };
                

                /* *****************************************************************************************
                 * Lookups
                 *******************************************************************************************/
                $scope.lookupStyle = function(val) {
                    
                    var p = {limit: 15, code: val, desc:false};
                    
                    return $scope.http.get($scope.remoteUrlPrefix + 'mdata/lookup', {params: p}).then(function(res){
                        var list = [];
                        for (var i=0; i<res.data.o.length; i++){
                            list.push(res.data.o[i]);
                        }
                        return list;
                    });
                };
                
                $scope.lookupMould = function(val) {
                    
                    var p = {limit: 15, code: val};
                    
                    return $scope.http.get($scope.remoteUrlPrefix + 'mdata/mould/lookup/m', {params: p}).then(function(res){
                        var list = [];
                        for (var i=0; i<res.data.o.length; i++){
                            list.push(res.data.o[i]);
                        }
                        return list;
                    });
                };
                
                
                /* *****************************************************************************************
                 * Dates:   
                 *******************************************************************************************/
                
                //Reformat dates into real dates
                var setDates = function(){
                    var dates = [];
                    for (var i=0; i<$scope.list.length; i++){
                        var sm = $scope.list[i];
                        for (var x=0; x<sm.getStarts().length; x++){
                            sm.getStarts()[x] = $scope.formatDateForElementId('smd_' + i + "_" + x);
                        }
                    }
                    return dates;
                };

                
                /* *****************************************************************************************
                 * Back Order:   
                 *******************************************************************************************/                
                $scope._rsbo = function(){
                	if (!$scope.isEdit()){
                		return;
                	}
                	
                	for (var i=0; i<$scope.list.length; i++){
                        var sm = $scope.list[i];
                        for (var x=0; x<sm.getBackOrders().length; x++){
                        	if (sm.getBackOrders()[x] !== null && sm.getBackOrders()[x].length > 0){
                        		sm.getBackOrders()[x] = '';
                        	}
                        }
                    }
                };
                
                
                /* *****************************************************************************************
                 * Shifts:  Problem with ng-model reading from array in sql object, so use fields in scope as a work around 
                 *******************************************************************************************/
                var setShifts = function(){
                    for (var i=0; i<$scope.list.length; i++){
                        var sm = $scope.list[i];
                        
                        for (var j=0; j<sm.getShiftIds().length; j++){
                            
                            var rec = $scope.smshifts['i' + i + 'n' + j];
                            sm.getShiftIds()[j] = rec !== null? rec.key : null;
                        }
                    }
                };
                
                
                //Set shift selections
                var shiftList = $scope.getCache(mdataModel.shift.model);
                $scope.shifts = shiftList.selectList(null, 'Code');
                
                for (var k=0; k < $scope.shifts.length; k++){
                    var rec = $scope.shifts[k];
                    rec.value = $scope.label('' + rec.value);
                }
                
               //Problem with ng-model reading from array in sql object, so use fields in scope as a work around
                $scope.smshifts = {};
                var encodeShifts = function(){
                    for (var i=0; i<$scope.list.length; i++){
                        var sm = $scope.list[i];
                        
                        for (var j=0; j<sm.getShiftIds().length; j++){
                            var rec = null;
                            
                            if (sm.getShiftIds()[j] !== null){
                                var id = sm.getShiftIds()[j];
                                
                                for (var k=0; k < $scope.shifts.length; k++){
                                    var recx = $scope.shifts[k];
                                    if (recx.key === id){
                                        rec = recx;
                                        break;
                                    }
                                }
                            }
                            $scope.smshifts['i' + i + 'n' + j] = rec;
                        }
                    }
                };                
                encodeShifts();
                
                //Override
                var savex = $scope.recordSave;
                $scope.recordSave = function(){
                    if (!$scope.isEdit()){
                        return;
                    }
                    setDates();
                    setShifts();
                    savex();
                    
                };
                
                //Override
                var postSuccessx = $scope.postSuccess;
                $scope.postSuccess = function(result){
                    postSuccessx(result);
                    encodeShifts();
                };
                
                
            }
    ])
    
    
    
    
     
     
    /**
     * Preparation Engine 2 
     */
    .controller('simuEngine2Ctrl', [
            '$rootScope',
            '$scope', 
            '$timeout',
            '$http',
            'acCache',
            'acController',
            'acGlobal',
            'simuModel',
            'simuRunRemote',
            'simuUpdateRemote',
             
            function($rootScope, $scope, $timeout, $http, acCache, acController, acGlobal, 
                    simuModel, simuRunRemote, simuUpdateRemote){
                
                var config = acController.createConfig('simuEngine2Ctrl', simuModel.simuengine2prep);
                config.title                = 'Simu.TM';
                config.isCachedObjectList   = false;
                config.editmode             = true;

                acController.configure($scope, config);
                
                var sbs = $scope.moveScroller('sidebaraction');
                var hs1 = $scope.moveScroller('header');
                
                
                /******************************
                 * Specific controller methods
                 ******************************/
                $scope.sql          = $scope.cacheObj;
                $scope.prepid       = null;
                $scope.planconfigid = null;
                $scope.createnew    = false;
                $scope.runNr        = 0;
                
               
                //User is returning to this page
                if (angular.isDefined(config.sql)){
                    $scope.sql            = config.sql;
                    $scope.prepid         = config.prepid;
                    $scope.planconfigid   = config.planconfigid;
                    $scope.createnew      = config.createnew;
                }
                //First time
                else{
                    config.prepid       = $scope.prepid;
                    config.planconfigid = $scope.planconfigid;
                    config.createnew    = $scope.createnew; 
                }
                
                
                //Controller was opened from preparation page
                var loading = false;
                if (angular.isDefined(simuModel.simuengine2prep.prepid) && simuModel.simuengine2prep.prepid !== null){
                    loading = true;
                    $scope.addLoadingNoReg();
                    $scope.prepid         = simuModel.simuengine2prep.prepid;
                    $scope.planconfigid   = simuModel.simuengine2prep.planconfigid;
                    $scope.createnew      = simuModel.simuengine2prep.createnew;
                    
                    config.prepid       = $scope.prepid;
                    config.planconfigid = $scope.planconfigid;
                    config.createnew    = $scope.createnew; 
                    
                    simuModel.simuengine2prep.prepid       = null;
                    simuModel.simuengine2prep.planconfigid = null;
                    simuModel.simuengine2prep.createnew    = false;
                }
                
                //Thanks to http://stackoverflow.com/questions/21686986/run-javascript-after-angular-has-finished-loading-all-views
                $scope.$on('$includeContentLoaded', function() {
                    sbs();
                    hs1();
                });


                $scope._ss = function(runNr){
                    simuRunRemote.queryForce({prepid: $scope.prepid, planconfigid:$scope.planconfigid, runnr: runNr, period: false},
                            function(result){
                                simuModel.simurun.plan_nr = result.getObject().getPlanNr();
                                $scope.stateGo(simuModel.simurun.state);
                            },
                            function(result) {
                                $scope.displayErrorDialog('CantRunPrep',result.getMessageObject());
                            });
                };

                
                /******************************
                 * View control
                 ******************************/

                //Encode the call
                var setViewLinkEncoded = function(){
                    if ($scope.prepid === null || $scope.planconfigid === null){
                        return null;
                    }
                    return "OK";
                };

                var viewLinkEncoded = setViewLinkEncoded();
                $scope.viewLinkId  = $scope.getNextId();
                
                //Provide view link
                $scope.getViewLink = function(){
                    if (viewLinkEncoded === null){
                        return;
                    }
                    return 'rest/simu/viewengine2prep?prepid=' + $scope.prepid 
                              + '&planconfigid=' + $scope.planconfigid 
                              + '&create=' + $scope.createnew
                              + '&runnr='  + $scope.runNr
                              + '&lk=' + $scope.viewLinkId
                              + '&rl=' + loading;
                };

                
                
                
                /************************************************
                 * Detail functions 
                 ************************************************/
                
                /**
                 * Update machine
                 */
                $scope._ms = function(id){
                    var v  = document.getElementById('ms' + id).value;
                    updateRemoteX({prepid: $scope.prepid, mgpid: id, mid: v});              
                };
                
                /**
                 * Update max stations
                 */
                var lastMaxSta = {id:0,v:0};
                $scope._ims = function(id){
                    var v  = document.getElementById('max_sta' + id).value;
                    if (!testNumber(id,v,lastMaxSta)){
                        return;
                    }
                    if (!testInput(id,v,lastMaxSta)){
                        return;
                    }
                    updateRemote({prepid: $scope.prepid, mgpid: id, max_sta: v});
                };
                
                /**
                 * Update stations
                 */
                var lastSta = {id:0,v:0};
                $scope._is = function(id){
                    var v  = document.getElementById('sta' + id).value;
                    if (!testInput(id,v,lastSta)){
                        return;
                    }
                    updateRemote({prepid: $scope.prepid, mgpid: id, sta: v});
                };
                
                /**
                 * Test for repeat input
                 */
                var testInput = function (id, v, last){
                    if (last.id === id && last.v === v){
                        return false;
                    }
                    last.id = id;
                    last.v  = v;
                    return true;
                };
                
                /**
                 * Test for valid number
                 */
                var testNumber = function (id, v, last){
                    if (testNaN(v)){
                        if (last.id === id){
                            return;
                        }
                        last.id = id;
                        $scope.displayErrorDialog('Invalid', 'InvalidEntry');
                        return false;
                    }
                    return true;
                };
                
                /**
                 * Select record 
                 */
                $scope._sa = function(mgpid){
                    var el = angular.element('#sa' + mgpid);
                    var active = el.is(':checked');
                    updateRemoteX({prepid: $scope.prepid, mgpid: mgpid, sa: active});
                };
                
                /**
                 * Select station 1 
                 */
                $scope._s1 = function(mgpid){
                    var el = angular.element('#cb1' + mgpid);
                    var active = el.is(':checked');
                    updateRemote({prepid: $scope.prepid, mgpid: mgpid, sta1: active});
                };
                
                
                /**
                 * Select station 2 
                 */
                $scope._s2 = function(mgpid){
                    var el = angular.element('#cb2' + mgpid);
                    var active = el.is(':checked');
                    updateRemote({prepid: $scope.prepid, mgpid: mgpid, sta2: active});
                };
                
                /**
                 * Update Selection
                 */
                var updateRemote = function(param){
                    simuUpdateRemote.queryForce(param,
                            function(result){
                            },
                            function(result) { 
                                $scope.displayError(result);
                            }
                    );
                };
                
                /**
                 * Drop instance into mould group to change priority 
                 */
                $scope._mg = function($event,$data,mgpid,runNr){
                    $scope.createnew = false;
                    $scope.runNr     = runNr;
                    updateRemoteX({prepid: $scope.prepid, mgpid: mgpid, mgpidx: $data.id});
                };

                /**
                 * Reset priorities to default
                 */
                $scope._res = function(runNr){
                    $scope.createnew = false;
                    $scope.runNr     = runNr; 
                    updateRemoteX({prepid: $scope.prepid, reset: true});
                };
                
                /**
                 * Update Selection and redisplay
                 */
                var updateRemoteX = function(param){
                    $scope.addLoading();
                    simuUpdateRemote.queryForce(param,
                            function(result){
                                $scope.removeLoading();
                                loading = true;
                                $scope.addLoadingNoReg();

                                $timeout(function() {
                                    $scope.viewLinkId  = $scope.getNextId();
                                }, 50);
                                
                            },
                            function(result) { 
                                $scope.removeLoading();
                                $scope.displayError(result);
                            }
                    );
                };
                
            }
    ])


    
    /**
     * Make period active 
     */
    .controller('simuPeriodActiveCtrl', [
            '$rootScope',
            '$scope', 
            '$window',
            'acController',
            'acDialogs',
            'config',
            'periodSetActiveRemote',

            function($rootScope, $scope, $window, acController, acDialogs, config, periodSetActiveRemote){
                
                var configX = acController.createConfig('simuPeriodActiveCtrl');
                acController.configure($scope, configX);

                var dto = config.dto;
                
                
                $scope.title = function(){
                    return $scope.label('PeriodA');
                };
                
                $scope.cancel = function(){
                    config.close('ok');
                };
                
                $scope.message = function(){
                    return dto.getMessage();
                };
                
                //set active
                $scope.ok = function(){
                    
                    var dtoX = dto.model.createDto();
                    dtoX.setPeriodId(dto.getId());
                    dtoX.setNr(dto.getNr());
                    
                    var sql = config.sql;
                    sql.setCycleStartPeriod(1); //force a view change
                    
                    var saveList = [];
                    saveList.push(dtoX);
                    
                    periodSetActiveRemote.post(
                            saveList,  
                            null, 
                            function(result){
                                   config.close('ok');  
                                   config.setView(sql);
                                }, 
                            function (result){
                                   alert($scope.label(result.getMessage()));
                            });
                };
                
                
            }
     ])


    
    
;   

