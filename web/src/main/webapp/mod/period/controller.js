'use strict';

angular.module('mod.period.controllers', ['ngDragDrop', 'pasvaz.bindonce'])


    /**
     * Period selection and working page
     */
    .controller('periodCtrl', [
            '$rootScope',
            '$scope', 
            '$timeout',
            '$http',
            '$window',
            'acController',
            'acGlobal',
            'acDialogs',
            'acDefinition',
            
            'periodModel',
            'periodCurrentRemote',
            'periodPlansRemote',
            'periodListRemote',
            'periodFunctions',
            'periodUpdateRemote', 
            'periodMoveRemote', 
            'periodToggleRemote',
            'periodExportSSRemote',
            
            function($rootScope, $scope, $timeout, $http, $window, 
                    acController, acGlobal, acDialogs, acDefinition,
                    periodModel, periodCurrentRemote, periodPlansRemote, periodListRemote, periodFunctions,
                    periodUpdateRemote, periodMoveRemote, periodToggleRemote, periodExportSSRemote
                    ){
             
            	
            	var config = acController.createConfig('periodCtrl', periodModel.periodSelection);
                config.isCachedObjectList   = false;
                config.editmode             = true;
                config.title                = 'Period.W';
                acController.configure($scope, config);

                
                
                var sbs = $scope.moveScroller('sidebaraction', 40);
                var hs1 = $scope.moveScroller('header');
                
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
                $scope.planP = false;
                $scope.planN = false;
                
                var intialisePlans = function(sql){
                	$scope.planP = sql.getPlanPrevious() != null;
                	$scope.planN = sql.getPlanNext() != null;
                	$scope.sql.setPlanPrevious(sql.getPlanPrevious());
                	$scope.sql.setPlanNext(sql.getPlanNext());
                	
                	if ($scope.planP){
                		document.getElementById("so25-p").innerHTML = sql.getPlanPrevious();
                	}
                	else{
                		document.getElementById("so25-p").innerHTML = $scope.label('Period.P');
                	}
                	
                	if ($scope.planN){
                		document.getElementById("so25-n").innerHTML = sql.getPlanNext();
                	}
                	else{
                		document.getElementById("so25-n").innerHTML = $scope.label('Period.N');
                	}
                	
                };
                intialisePlans($scope.sql);
                
                
                var setWindowScroll = function(){
                	config.scrollLeft = (window.pageXOffset !== undefined) ? window.pageXOffset : (document.documentElement || document.body.parentNode || document.body).scrollLeft,
                    config.scrollTop = (window.pageYOffset !== undefined) ? window.pageYOffset : (document.documentElement || document.body.parentNode || document.body).scrollTop;
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
                    }, 50);
                };
              
                
                //View functions (called from generated HTML)
                $scope._rwX = function(t, l){
                    if (!angular.isDefined(t) || t === null){
                        t = config.scrollTop;
                    }
                    if (!angular.isDefined(l) || l === null){
                        l = config.scrollLeft;
                    }
                    resetWindow (t, l);
                };
                
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
                $scope.resetScroll = null;
                
                //Force a view change
                var setView = function(sql, resetScroll){
                    viewLinkEncoded = setViewLinkEncoded(sql);
                    if (viewLinkEncoded == null){
                        return;
                    }
                    
                    var rs = angular.isDefined(resetScroll);
                    if (rs){
                    	$scope.resetScroll = resetScroll;
                    }
                    else{
                    	$scope.resetScroll = null;
                    }
                    
                    setWindowScroll();
                    config.sql = sql;
                    $scope.viewLinkId  = $scope.getNextId();
                    $scope.setVisable('result_table_simu');
                    $scope.addLoadingNoReg();
                    
                    if (rs){
                    	$scope._rwX();
                    }
                    
                };
                
                //Thanks to http://stackoverflow.com/questions/21686986/run-javascript-after-angular-has-finished-loading-all-views
                $scope.$on('$includeContentLoaded', function() {
                    sbs();
                    hs1();
                });
                
                //Provide view link
                $scope.getViewLink = function(){
                    if (viewLinkEncoded === null){
                        return;
                    }
                    return 'rest/period/view?sql=' + encodeURIComponent(viewLinkEncoded) + '&lk=' + $scope.viewLinkId + '&rl=' + true + '&rs=' + $scope.resetScroll;
                };

                
                //Force open a period 
                var openPeriod = function (nr){
                	if (testNaN(nr)){
                		return;
                	}
                    $scope.sql.setPlanNr(nr);
                    $scope.selectedPlanNr = nr;
                    setView($scope.sql);
                    setPlans();
                };
                
                periodCurrentRemote.queryForce(null, 
            		function(result){
	                	var nr = result.getObject();
	                	if (nr > 0){
	                		openPeriod(nr);
	                	}
	                }, function (result){
	                	alert($scope.label(result.getMessage()));
	            });
                
                $scope.getPeriod = function(){
                	openPeriod($scope.selectedPlanNr);
                };
                
                $scope.getPeriodPrevious = function(){
                	if ($scope.planP){
                		openPeriod($scope.sql.getPlanPrevious());
                	}
                };
                
                $scope.getPeriodNext = function(){
                	if ($scope.planN){
                		openPeriod($scope.sql.getPlanNext());
                	}
                };
                
                //Get previous and next numbers
                var setPlans = function(){
                	periodPlansRemote.queryForce({planNr: $scope.sql.getPlanNr()}, 
                        function(result){
                		    intialisePlans(result.getObject());        
	                    }, function (result){
	                        alert($scope.label(result.getMessage()));
	                    });
                };
                
                
                /*-***********************************************
                 * Active Period list 
                 ************************************************/
                $scope._lx = function(){
                	periodListRemote.queryForce(null, 
               			function(result){
                		    showList(result);
	                	}, function (result){
	                		alert($scope.label(result.getMessage()));
	                	});
                };
                
                var showList = function (result){
                	acDialogs.openDialog('mod/period/view/period_list_dialog.html', 'periodListCtrl',
        					{scope:     $scope,
        				model:     $scope.model,
        				list:      result.getObject(),
        				windowClass:'so26-list-d'});
                };
                
                
                
                /*-***********************************************
                 * Export functions 
                 ************************************************/

                //export direct to spreadsheet
                $scope._x = function(view){
                    exSS();
                };
                
                var exSS = function(view){
                	periodExportSSRemote.queryForce({planNr: $scope.sql.getPlanNr()}, 
                            function(result){
                        var url = 'rest/spreadsheet/return?filename=' + result.getObject();
                        $window.open(url, '_tab');
                    }, function (result){
                        alert($scope.label(result.getMessage()));
                    });
                };
                
                
                /*-***********************************************
                 * Period Simulation functions
                 ************************************************/
                periodFunctions.create($scope, setView, resetWindow, setWindowScroll, periodUpdateRemote, periodMoveRemote, periodToggleRemote);
                
            }
    ])


         
          
    /**
     * Period List controller
     */
    .controller('periodListCtrl', [
            '$rootScope',
            '$scope', 
            '$window',
            'acController',
            'acDialogs',
            'config',
            
            function($rootScope, $scope, $window, acController, acDialogs, config){
                
                var configX = acController.createConfig('periodListCtrl');
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
                    config.scope.getPeriod();
                };
            }
     ])


  



    .service('periodFunctions', function($rootScope, acDialogs) {
    

        var self = {

            create: function ($scope, setView, resetWindow, setWindowScroll, updateRemote, moveRemote, toggleRemote) {

                //Show machine details
                $scope._sh = function(id, view){
                    toggelMachine(id,view,'show');
                };
                //Hide machine details
                $scope._hd = function(id, view){
                    toggelMachine(id,view,'hide');
                };
                
                var toggelMachine = function (id,view,action){
                    toggleRemote.queryForce(
                            {planNr: $scope.sql.getPlanNr(),
                             id: id,
                             view: view,
                             action: action}, 
                            function(result){
                                   setView($scope.sql);
                                }, function (result){
                                   alert($scope.label(result.getMessage()));
                            });
                };  


                //Select pu configuration
                $scope._puc = function(id){
                    $scope.sql.setPuConfigId(id);
                    setView($scope.sql);
                    resetWindow();
                };
                
                
                //Drop period object into clip board, drop boxes or another period object
                $scope._dxp = function($event,$data,recordId,type){
                    movePeriod(
                            {from: $data.id,
                             fromType: $data.type,
                             to: recordId,
                             toType: type});
                };

                //Menu move group or instance to clipboard 
                $scope._mvp = function($data){
                    var ids = $data.split(',');
                    movePeriod(
                            {from:ids[0],
                             fromType:ids[1],
                             to: 0,
                             toType: 0});
                };

                //Move period 
                var movePeriod = function(param){
                    param.planNr = $scope.sql.getPlanNr();
                    param.action = 'after';

                    moveRemote.queryForce(param, 
                            function(result){
                                   setView($scope.sql);
                                }, function (result){
                                   alert($scope.label(result.getMessage()));
                            });
                };
               
                var scrollLeft = 0;
                var scrollTop  = 0;
                
                //Show instance detail
                $scope._sip = function(type, ids){
                    $scope.sql.setPeriodDetailView(type);
                    $scope.sql.setInstanceIds(ids);
                    
                    //window scroll x,y to return to
                    scrollTop = (window.pageYOffset !== undefined) ? window.pageYOffset : (document.documentElement || document.body.parentNode || document.body).scrollTop;
                    scrollLeft = (window.pageXOffset !== undefined) ? window.pageXOffset : (document.documentElement || document.body.parentNode || document.body).scrollLeft;
                    
                    setView($scope.sql, '0,0'); 
                };

                /**
                 * Update Period Record(s) Selection
                 */
                $scope._icp = function(type1, type2, type1_id, type2_id){
                    var el = angular.element('#icbp' + type2 + '_' + type2_id);
                    var active = el.is(':checked');
                    updatePeriod({type1: type1, type2: type2, type1_id: type1_id, type2_id: type2_id, active: active},
                            function(){
                                $scope.sql.setCycleStartPeriod(1); //force a view refresh
                                setView($scope.sql);
                                $scope.sql.setCycleStartPeriod(null);
                            }
                    );
                };
                

                /**
                 * Update Selection
                 */
                var updatePeriod = function(param, callback){
                    updateRemote.queryForce(param,
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

                //return to simulation
                $scope._rsp = function(){
                    $scope.sql.setPeriodDetailView(null);
                    $scope.sql.setInstanceIds(null);
                    setView($scope.sql, '' + scrollTop + ',' + scrollLeft); //reset window scroll x,y
                };




            }
        }


        return self;

    })
    
    




;   

