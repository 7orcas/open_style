'use strict';

/**
 * @module mainCtrl
 * @version home.controller
 * 
 * @description
 * Application (i.e. main page) controller.<p>   
 * 
 * TODO: complete this documentation
 * 
 * See http://startbootstrap.com/sb-admin-v2
 * See code: https://github.com/IronSummitMedia/startbootstrap/tree/master/templates/sb-admin-v2
 * See demo: http://startbootstrap.com/templates/sb-admin-v2/
 *  
 * [License]
 * @author John Stewart
 */
angular.module('home.controller', ['ngResource'])
   
    .controller('mainCtrl', [
        '$scope',
        '$rootScope',
        '$state',
        '$location',
        '$timeout',
        '$window',
        'acUserObj',
        'aLang',
        'acController',
        'acGlobal',
        'acCache',
        //<!-- service:start -->  
        'acStartTimersRemote',
        'acCancelTimersRemote',
        'acClearFixesRemote',
        'acReapplyLSFixRemote',
        'acReloadAppPropRemote',
        'acCloseSqlConnection',
        'acResetDBRemote',
        'acToggleDebugRemote',
        //<!-- service:end -->        
        
         //Forces a loads of DTO definitions
        
        function($scope, $rootScope, $state, $location, $timeout, $window, acUserObj, aLang, acController, acGlobal, acCache
        		//<!-- service:start -->  
        		,acStartTimersRemote, acCancelTimersRemote, acClearFixesRemote, acReapplyLSFixRemote, acReloadAppPropRemote, acCloseSqlConnection, acResetDBRemote, acToggleDebugRemote
        		//<!-- service:end -->  
                ) {
        
            
            var config = acController.createConfig('mainCtrl');
            acController.configure($scope, config);
            
            $rootScope.relogin  = false;
            $rootScope.$on('$stateChangeStart', function(){
                    $scope.setMainTitle('');
                }
            );
            
            $rootScope.menuButtonsActive = true;
            
            //<!-- service:start -->  
            var debugMode = false;
            //<!-- service:end -->

            
            /*
             * Load initial definitions 
             */
            acGlobal.addLoading();
            
            //Only initialize user once all models have been loaded 
            var initUser = function(){
                $timeout(function() {
                    
                    acUserObj.initialize(function(result){
                        
                        var lang = $location.search().lang;
                        if (angular.isDefined(lang) && lang !== null){
                            acGlobal.globals().language = lang;
                        }
                        
                        aLang.loadLang(acGlobal.globals().language, function(){
                        	$scope.setMainTitle($scope.label('Home'));
                            angular.element(".page_header").show();
                        });
                        
                        //Used in date picker (see app/common/javascript_utils.jas)
                        appLanguageCode   = acGlobal.globals().language;
                        appLanguageDayS   = acGlobal.globals().daysShort;
                        appLanguageMonth  = acGlobal.globals().months;
                        appLanguageMonthS = acGlobal.globals().monthsShort;
                        
                        //<!-- service:start -->  
                        debugMode = result.dm;
                        toggleDebugStatus();
                        //<!-- service:end -->
                        
                        $scope.setUserType();
                        acController.heartbeat($scope);
                        
                        acController.sessionValid($scope);
                        
                        acGlobal.removeLoading();
                    });
                 
                        
                },50);
            };
            initUser();
            
            
            $scope.home = function(){
            };
            
            
            
            
            
            
            $scope.showDoc = function(id){
                $state.go("showDoc");
            };
            
            
            
            
            
            
            
            
            
            
            
            
            ///////////////////////////////////////// User Menu /////////////////////////////////////////////////////////            
            /*
             * Logout action
             */
            $scope.$on('event:logout', function() {
                $rootScope.relogin = false;
                $window.location.href = acGlobal.globals().indexPage;
            });
            
            $scope.logout = function(){
                if (!$rootScope.menuButtonsActive){
                    return;
                }
                acUserObj.logout();
            };

            
            $scope.changePassword = function(){
                $state.go("userChangePassword");
            };
            
            
            $scope.onlineHelp = function(){
                var lang = acGlobal.globals().language;
                var url = acGlobal.globals().helpUrlRoot;
                if (lang === 'de'){
                    window.open(url + "de/");
                }
                else{
                    window.open(url + "en/");
                }
                
            };
            
            //////////////////////    <!-- service:start -->
            
            $scope.userAdmin = function(){
                $state.go("userAdmin");
            };
            
            $scope.langAdmin = function(){
                $state.go("langAdmin");
            };
            
            $scope.langReload = function(){
                aLang.clearcache(new function(){
                    aLang.loadLangForce(acGlobal.globals().language);
                });
            };
            
            $scope.companyAdmin = function(){
                $state.go("companyAdmin");
            };
            
            $scope.clearFixes = function(){
            	if (!isDebugOn()){
            		return;
            	}
            	acClearFixesRemote.queryForce(null,
                        function(result){
                            $scope.displayDialog('FixR', result.getObject());
                        });
            };
            
            $scope.reapplyLSFix = function(){
            	if (!isDebugOn()){
            		return;
            	}
            	$scope.displayDialog($scope.label('FixRLS'), 'call desma/rest/simu/reapplylogisoftfix?fixNr=XXX');
            	
//TODO            	
//            	
//            	acDialogs.openDialog('mod/simu/view/fix_dialog.html', 'simuFixCtrl',
//		    			 {scope:     $scope,
//			    		  planNr:    $scope.sql.getPlanNr(),
//			    		  machine_id:machine_id, 
//						  station:   station, 
//						  stations:  stations,
//						  period:    e,
//						  sText:     sText,
//						  bText:     bText,
//			    		  model:     simuModel.simuselection,
//			    		  sql:       $scope.sql,
//			    		  setView:   setView,
//			    		  windowClass:'simu-act-menu'});
//            	
//            	acReapplyLSFixRemote.queryForce(null,
//                        function(result){
//                            $scope.displayDialog('FixRLS', result.getObject());
//                        });
            };
            
            $scope.patches = function(){
                $state.go("patches");
            };
            
            $scope.dataImport = function(){
                $state.go("dataImport");
            };
            
            $scope.whoAmI = function(){
                return acGlobal.globals().companyCode;
            };
            
            $scope.startTimers = function(){
                acStartTimersRemote.queryForce(null,
                        function(result){
                            $scope.displayDialog('TimersS', result.getObject());
                        });
            };
            
            
            $scope.cancelTimers = function(){
                acCancelTimersRemote.queryForce(null,
                        function(result){
                            $scope.displayDialog('TimersC', result.getObject());
                        });
            };
            
            $scope.reloadAppProp = function(){
                acReloadAppPropRemote.queryForce(null,
                        function(result){
                            $scope.displayDialog('AppPropR', result.getObject());
                        });
            }; 
            
            $scope.closeSqlConn = function(){
            	acCloseSqlConnection.queryForce(null,
                        function(result){
                            $scope.displayDialog('CloseC', result.getObject());
                        });
            }; 
            
            $scope.resetDB = function(){
            	if (!isDebugOn()){
            		return;
            	}
            	acResetDBRemote.queryForce(null,
                        function(result){
                            $scope.displayDialog('ResetDB', result.getObject());
                            $scope.logout();
                        },
                        function(result){
                            $scope.displayErrorDialog('ResetDB', result.m);
                        });
            }; 
            
            /*-**************************************
             * Debug functions
             ***************************************/
            $scope.toggleDebug = function(){
            	acToggleDebugRemote.queryForce(null,
                        function(result){
            				var m = result.getObject();
            				$scope.displayDialog('Debug', m);
                            if (m === 'Debug ON'){
                            	debugMode = true;
                            }
                            else{
                            	debugMode = false;
                            }
                            toggleDebugStatus();
                        });
            }; 
            
            //Test debug is on
            var isDebugOn = function(id){
	            if (debugMode){
	            	return true;
	            }
	            $scope.displayDialog('Debug', 'Debug must be ON');
	            return false;
            };
            
            //Show Debug Status
            var toggleStatus = function(id){
	            $timeout(function() {
	            	var el = angular.element('#'+ id);
	                if (debugMode){
	                	el.removeClass('inactive');
	                }
	                else{
	                	el.addClass('inactive');
	                }
	            },100);
            };
            
            var toggleDebugStatus = function(){
	            $timeout(function() {
	            	$scope.setVisable('debug_status', debugMode);
	            	toggleStatus('resetDB_status');
	            	toggleStatus('reapplyLSFix_status');
	            	toggleStatus('clearFixes_status');
	            },100);
            };
            toggleDebugStatus();
            
            
            /////////////////    <!-- service:end -->
            
            $scope.hideSideMenu = function(animation){
                $('#page-wrapper').css("margin-left", "0px");
                if (angular.isDefined(animation) && animation){
                    angular.element("#sideMenu").slideToggle(300, 'swing');
                }
                else{
                    angular.element("#sideMenu").hide();       
                }
                angular.element("#menu-sel-show").show();
                angular.element("#menu-sel-hide").hide();
            }; 
             
            $scope.showSideMenu = function(){
                $('#page-wrapper').css("margin-left", "150px");
                angular.element("#sideMenu").slideToggle(300, 'linear');
                angular.element("#menu-sel-show").hide();
                angular.element("#menu-sel-hide").show();
            };
            
            angular.element("#menu-sel-show").hide();
            angular.element("#menu-sel-hide").show();
            
            
            ///////////////////////////////////////// Master Data Menu /////////////////////////////////////////////////////////
            
            
            //<!-- service:start -->  

            $scope.sizes = function(){
            	$state.go("sizeList");
            };
            $scope.attributes = function(){
            	$state.go("attributetypeList");
            };
            $scope.materials = function(){
            	$state.go("materialtypeList");
            };
            $scope.categories = function(){
            	$state.go("categoryList");
            };
            $scope.reqtypes = function(){
            	$state.go("reqtypeList");
            };
            $scope.fixes = function(){
            	$state.go("fixList");
            };
            $scope.puTypes = function(){
            	$state.go("putypeList");
            };
            
            //<!-- service:end -->
            
            $scope.plants = function(){
                $state.go("plantList");
            };
            
            $scope.shifts = function(){
                $state.go("shiftList");
            };
            
            $scope.moulds = function(){
                $state.go("mouldMap");
            };
            
            $scope.lasts = function(){
                $state.go("lastMap");
            };
            
            $scope.calendar = function(){
                $state.go("calendarList");
            };
            
            $scope.styles = function(){
                $state.go("styleList");
            };
            
            $scope.machines = function(){
                $state.go("machineList");
            };
            
            $scope.puConfig = function(){
                $state.go("puConfig");
            };
            
            

            $scope.import = function(){
                $state.go("importXXX"); //    <!-- import:def -->
            };
            
            $scope.exportFix = function(){
                $state.go("exportFix");
            };
            
            $scope.prep = function(){
                $state.go("prep");
            };
            
            $scope.simu = function(){
                $state.go("simu");
            };

            $scope.period = function(){
                $state.go("period");
            };
            
            $scope.report = function(){
                $state.go("report");
            };
            
            
            /*
             * Main Menu actions
             */
            $scope.isMenuAction = function(){
                return $rootScope.menuButtonsActive;
            };
            
           
            
        }
    ])
    
    
    /**
     * Relogin Controller
     */
    .controller('reloginCtrl', [
            '$rootScope',
            '$scope', 
            '$state',
            'acGlobal',
            'aLang',
            'reloginRemote',
            'focus',

            function($rootScope, $scope, $state, acGlobal, aLang, reloginRemote, focus){

                $rootScope.relogin = true;
                aLang.addFormFunctions($scope);
                
                $scope.luser = acGlobal.globals().userid;
                $scope.lpass = null;
                
                focus('focusMe');
                
                $scope.relogin = function(){
                    reloginRemote.get({forx: acGlobal.globals().company, 
                        fgh: $scope.luser, 
                        ts1: md5($scope.lpass+"1"), 
                        ts2: md5($scope.lpass+"2"), 
                        ts3: md5($scope.lpass), 
                        ts4: md5($scope.lpass+"4"),
                        lan: acGlobal.globals().language,
                        cha:null, 
                        res:null}, 
                        
                        function (result){
                    
                            //Successful login
                            if (result.rs === 0) {
                                $rootScope.relogin = false;
                                $state.go('clearPage');
                            } 
                            
                            //Unsuccessful login
                            else {
                                $scope.loginFailedMessage = result.m;
                            }
                        }
                    );
                };
                
                
                
          }
    ])
    
    
    /**
     * Clear Page Controller
     */
    .controller('clearPageCtrl', [
            '$rootScope',
            '$scope', 
            
            function($rootScope, $scope){
                
          }
    ])
    
    
;      