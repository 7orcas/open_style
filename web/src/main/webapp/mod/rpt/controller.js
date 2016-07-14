'use strict';

angular.module('mod.rpt.controllers', ['ngDragDrop', 'pasvaz.bindonce'])


    /**
     * Preparation material report selection
     */
    .controller('prepMatRptCtrl', [
            '$rootScope',
            '$scope', 
            '$timeout',
            '$http',
            'acCache',
            'acController',
            'acGlobal',
            'rptModel',
            'prepModel',
            'rptPrepMatRemote',
            'rptPrepMatRunRemote',
            'rptPrepMatExportRemote',
            'mdataModel',
            
             
            function($rootScope, $scope, $timeout, $http, acCache, acController, acGlobal, 
                    rptModel, prepModel, 
                    rptPrepMatRemote, rptPrepMatRunRemote, rptPrepMatExportRemote,
                    mdataModel){
             
                var config = acController.createConfig('prepMatRptCtrl', rptModel.prepMat);
                config.title                = 'MatRpt';
                config.sort_predicate_field = '';
                config.isCachedObjectList   = false;
                config.remotePost           = rptPrepMatRunRemote;
                config.remoteExport         = rptPrepMatExportRemote;
                config.editmode             = true;

                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                $scope.addLoadingNoReg();
                
                
                /******************************
                 * Specific controller methods
                 ******************************/
                $scope.sql       = $scope.cacheObj;
                $scope.fMissing  = $scope.sql.getMissingStock();
                $scope.fSupplier = $scope.sql.getSupplier();
                $scope.fMatType  = $scope.sql.getMaterialTypeId();
                $scope.fReqType  = $scope.sql.getReqTypeId();
                $scope.fMaterial = $scope.sql.getMaterial();
                $scope.fStyle    = $scope.sql.getStyle();
                $scope.fColor    = $scope.sql.getColor();
                $scope.fMould    = $scope.sql.getMould();
                $scope.fMouldGrp = $scope.sql.getMouldGroup();
                $scope.fSourceId = $scope.sql.getSourceId();
                
                
                var prepSql = rptModel.prepMat.prepSql;
                if (prepSql !== null){
                    $scope.sql.setPrepId(prepSql.getId());
                }
                
                
                var viewLinkEncoded   = $scope.model.json($scope.sql);
                $scope.viewLinkId = $scope.getNextId();
                $scope.getViewLink = function(){
                    $scope.setVisable('result_table');
                    return 'rest/rpt/matrpt/view?prep=' + encodeURIComponent(viewLinkEncoded) + "&lk=" + $scope.viewLinkId + '&rl=' + true;
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
                
                
                //override - Export to spreadsheet
                var ex = $scope.exportSS;
                $scope.exportSS = function(){
                    ex({sql:$scope.model.json($scope.sql)});
                };
       
                
                /**************************************************************************************
                 * Run report
                 **************************************************************************************/

                $scope.runReport = function(){
                    
                    $scope.sql.setCategories(categories.getCodes($scope.columns));
                    
                    $scope.sql.setMissingStock($scope.fMissing);
                    $scope.sql.setMaterial($scope.fMaterial);
                    $scope.sql.setMaterialTypeId($scope.fMatType.key);
                    $scope.sql.setReqTypeId($scope.fReqType.key);
                    $scope.sql.setSupplier($scope.fSupplier);
                    $scope.sql.setStyle($scope.fStyle);
                    $scope.sql.setColor($scope.fColor);
                    $scope.sql.setMould($scope.fMould);
                    $scope.sql.setMouldGroup($scope.fMouldGrp);
                    $scope.sql.setSourceId($scope.fSourceId);
                    
                    if (!$scope.sql.isValid()){
                        $scope.displayDialog('RunPrep', 'PrepNotValid');
                        return;
                    }
                    
                    rptPrepMatRunRemote.queryForce({prep: $scope.model.json($scope.sql)},
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
                 * Categories Logic
                 **************************************************************************************/

                //Required objects to display in list
                var categories = $scope.getCache(rptModel.categoriesMat.model);
                
                
                //Drag 'n Drop arrays and actions
                $scope.fields  = categories.getCategories();
                $scope.columns = []; 
                var dropStop   = false;
                
                $scope._dshx = function($event,index,array){
                	var d = array[index];
                	var i = indexCat(d, array);
                    $scope._dsh($event,index,array);
                    
                    var i1 = indexCat(d, $scope.fields);
                    var i2 = indexCat(d, $scope.columns);
                    
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
                    $scope.fields.sort();
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
                    var i = indexCat($data,$scope.fields);
                    var f = false;
                    if (i !== -1){
                        $scope.fields.splice(i,1);
                        f = true;
                    }
                    i = indexCat($data,$scope.columns);
                    if (i !== -1){
                        $scope.columns.splice(i,1);
                        f = true;
                    }

                    if (!f){
                        return;
                    }

                    $scope.columns.splice(index,0,$data);
                    $event.stopPropagation();
                    dropStop = true;
                };


                //Double click action
                $scope.addCategory = function (sel){
                    $scope.onDrop(null, sel, $scope.columns);
                    $scope._dsh(null, indexCat(sel, $scope.fields), $scope.fields);
                };
                //Double click action
                $scope.removeCategory = function (sel){
                    $scope.onDrop(null, sel, $scope.fields);
                    $scope._dsh(null, indexCat(sel, $scope.columns), $scope.columns);
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

                $scope.setColumns = function(){
                    var list = categories.getDescriptions($scope.sql.getCategories());
                    for (var i=0;i<list.length;i++){
                       $scope.addCategory(list[i]);
                    }
                };
                $scope.setColumns();
                

                /**************************************************************************************
                 * Filters
                 **************************************************************************************/
                
                /**
                 * Lookup suppliers
                 */
                $scope.lookupSup = function(val) {
                    var p = {limit: 15, code: val};
                    return $scope.http.get($scope.remoteUrlPrefix + 'mdata/supplier/lookup', {params: p}).then(function(res){
                        var list = [];
                        for (var i=0; i<res.data.o.length; i++){
                            list.push(res.data.o[i]);
                        }
                        return list;
                    });
                };
                
                
                /**
                 * Lookup Materials
                 */
                $scope.lookupMaterial = function(val) {
                    var p = {limit: 15, code: val};
                    return $scope.http.get($scope.remoteUrlPrefix + 'mdata/material/lookup', {params: p}).then(function(res){
                        var list = [];
                        for (var i=0; i<res.data.o.length; i++){
                            list.push(res.data.o[i]);
                        }
                        return list;
                    });
                };

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


                /**
                 * Get lookup index
                 */
                var setId = function (f, array){
                	if (f === null || f === 0){
                		return 0;
                	}
                	for (var i=0;i<array.length;i++){
                    	if (f === array[i].key){
                    		return i;
                    	}
                    }	
                	return 0;
                };
                
                var mattypesL   = $scope.getCache(mdataModel.materialType.model);
                $scope.mattypes = mattypesL.selectList(null, 'toString');
                $scope.mattypes.splice(0,0,{key: 0, value : null});
                $scope.fMatType = $scope.mattypes[setId($scope.fMatType, $scope.mattypes)];

                var reqtypesL   = $scope.getCache(mdataModel.reqtype.model);
                $scope.reqtypes = reqtypesL.selectList(null, 'toString');
                $scope.reqtypes.splice(0,0,{key: 0, value : null});
                $scope.fReqType = $scope.reqtypes[setId($scope.fReqType, $scope.reqtypes)];
                                

                $scope.showMat = function(){
                    return $scope.sql.containsCategory($scope.columns,$scope.label('MatC'));
                };
                $scope.showShip = function(){
                    return $scope.sql.containsCategory($scope.columns,$scope.label('MatSh'));
                };
                
                $scope.showStyle = function(){
                    return $scope.sql.containsCategory($scope.columns,$scope.label('Style'));
                };
                $scope.showColor = function(){
                    return $scope.sql.containsCategory($scope.columns,$scope.label('Color'));
                };
                $scope.showMould = function(){
                    return $scope.sql.containsCategory($scope.columns,$scope.label('Mould'));
                };
                $scope.showMouldGrp = function(){
                    return $scope.sql.containsCategory($scope.columns,$scope.label('Mould.Gc'));
                };
                $scope.showReqType = function(){
                    return $scope.sql.containsCategory($scope.columns,$scope.label('ReqType'));
                };
                $scope.showMatType = function(){
                    return $scope.sql.containsCategory($scope.columns,$scope.label('MatT'));
                };
                $scope.showSourceId = function(){
                    return $scope.sql.containsCategory($scope.columns,$scope.label('SourceC'));
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
