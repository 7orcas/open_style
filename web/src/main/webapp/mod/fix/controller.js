'use strict';

angular.module('mod.fix.controllers', ['ngDragDrop', 'pasvaz.bindonce'])


    /**
     * Fix selection
     */
    .controller('fixListCtrl', [
            '$rootScope',
            '$scope', 
            '$timeout',
            '$http',
            'acCache',
            'acController',
            'acGlobal',
            'fixModel',
            'fixListRemote',
            'fixListRunRemote',
            'fixListExportRemote',
            'mdataModel',
            
             
            function($rootScope, $scope, $timeout, $http, acCache, acController, acGlobal, 
                    fixModel,  
                    fixListRemote, fixListRunRemote, fixListExportRemote,
                    mdataModel){
             
                var config = acController.createConfig('fixListCtrl', fixModel.list);
                config.title                = 'Fixes';
                config.sort_predicate_field = '';
                config.isCachedObjectList   = false;
                config.remotePost           = fixListRunRemote;
                config.remoteExport         = fixListExportRemote;
                config.editmode             = true;

                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                $scope.addLoadingNoReg();
                
                
                /******************************
                 * Specific controller methods
                 ******************************/
                $scope.sql       = $scope.cacheObj;
                $scope.fStatus   = $scope.sql.getStatusUnprocessed();
                $scope.fStyle    = $scope.sql.getStyle();
                $scope.fColor    = $scope.sql.getColor();
                $scope.fMould    = $scope.sql.getMould();
                $scope.fMouldGrp = $scope.sql.getMouldGroup();
                $scope.fSourceId = $scope.sql.getSourceId();
                
                
                var viewLinkEncoded   = $scope.model.json($scope.sql);
                $scope.viewLinkId = $scope.getNextId();
                $scope.getViewLink = function(){
                    $scope.setVisable('result_table');
                    return 'rest/fix/list/view?sql=' + encodeURIComponent(viewLinkEncoded) + "&lk=" + $scope.viewLinkId + '&rl=' + true;
                };
                
                
                //override - Export to spreadsheet
                var ex = $scope.exportSS;
                $scope.exportSS = function(){
                    ex({sql:$scope.model.json($scope.sql)});
                };
       
                
                /**************************************************************************************
                 * Run report
                 **************************************************************************************/

                $scope.runReport = function(){
                    
                    $scope.sql.setStatusUnprocessed($scope.fStatus);
                    $scope.sql.setStyle($scope.fStyle);
                    $scope.sql.setColor($scope.fColor);
                    $scope.sql.setMould($scope.fMould);
                    $scope.sql.setMouldGroup($scope.fMouldGrp);
                    $scope.sql.setSourceId($scope.fSourceId);
                    
                    fixListRunRemote.queryForce({sql: $scope.model.json($scope.sql)},
                        function(result){
                            $scope.page = createPageNavigationForGeneratedHtml();
                            $scope.sql = result.getObject();
                            
                            //Remember for user clicking away and then coming back
                            config.sql          = $scope.sql;
                            
                            viewLinkEncoded   = $scope.model.json($scope.sql);
                            $scope.viewLinkId = $scope.getNextId();
                            $scope.addLoadingNoReg();
                        },
                        function(result) { 
                            $scope.displayError(result);
                        }
                    );
                };
               
                

                /**************************************************************************************
                 * Filters
                 **************************************************************************************/
               
                /**
                 * Lookup Styles
                 */
                $scope.lookupStyle = function(val) {
                    var p = {limit: 15, code: val};
                    return $scope.http.get($scope.remoteUrlPrefix + 'mdata/lookup', {params: p}).then(function(res){
                        var list = [];
                        for (var i=0; i<res.data.o.length; i++){
                            list.push(res.data.o[i]);
                        }
                        return list;
                    });
                };


                /**
                 * Lookup Moulds
                 */
                $scope.lookupMould = function(val) {
                    var p = {limit: 15, code: val};
                    return $scope.http.get($scope.remoteUrlPrefix + 'mdata/mould/lookup', {params: p}).then(function(res){
                        var list = [];
                        for (var i=0; i<res.data.o.length; i++){
                            list.push(res.data.o[i]);
                        }
                        return list;
                    });
                };


                /**
                 * Lookup Mould Groups
                 */
                $scope.lookupMouldGrp = function(val) {
                    var p = {limit: 15, code: val};
                    return $scope.http.get($scope.remoteUrlPrefix + 'mdata/mouldgroup/lookup', {params: p}).then(function(res){
                        var list = [];
                        for (var i=0; i<res.data.o.length; i++){
                            list.push(res.data.o[i]);
                        }
                        return list;
                    });
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
                

                
                
            }
    ]);
