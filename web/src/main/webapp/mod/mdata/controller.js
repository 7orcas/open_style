'use strict';

angular.module('mod.mdata.controllers', [])


    /**************************************************************************************
     * Moulds
     **************************************************************************************/


    /**
     * Mould Map
     */
    .controller('mdataMouldMapCtrl', [
            '$rootScope',
            '$scope', 
            'acController', 
            'mdataModel',
            'mdataMouldMapRemote',

            function($rootScope, $scope, acController, mdataModel, mdataMouldMapRemote){

                var config = acController.createConfig('mdataMouldMapCtrl', mdataModel.mouldMap);
                config.title                = 'Moulds';
                config.remotePost           = mdataMouldMapRemote;
                                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                var sqlM = $scope.getCacheModel(mdataModel.mouldMap.modelSql);
                
                $scope.getViewLink = function(){
                    return 'rest/mdata/mould/view';
                };
                
                
                $scope.openAllMoulds = function(id){
                    var sql = sqlM.createDto();
                    open(sql);
                };
                
                //Open Group
                $scope.oG = function(id){
                    var sql = sqlM.createDto();
                    sql.setMouldGroupId(id);
                    open(sql);
                };
                
                //Open Mould
                $scope.oM = function(id){
                    var sql = sqlM.createDto();
                    sql.setMouldCodeId(id);
                    open(sql);
                };
                
                var open = function(sql){
                    $scope.addLoading();
                    $scope.invalidateCache(mdataModel.mouldCode.model);
                    $rootScope.pgConfig.mouldCodeList.p = {sql:sqlM.json(sql)};
                    $scope.stateGo("mouldCodeList");
                    //Special process
                    //$scope.removeLoading();
                };
                
                $scope.openGroups = function(){
                    $scope.stateGo("mouldGroup");
                };
                
            }
    ])


    /**
     * Moulds
     */
    .controller('mdataMouldCodeCtrl', [
            '$rootScope',
            '$scope', 
            'acController', 
            'acDialogs',
            'mdataModel',
            'mdataMouldCodeRemote',
            'mdataMouldCodesExportRemote',
            
            function($rootScope, $scope, acController, acDialogs, 
            		mdataModel, mdataMouldCodeRemote, mdataMouldCodesExportRemote){

                var config = acController.createConfig('mdataMouldCodeCtrl', mdataModel.mouldCode);
                config.remotePost           = mdataMouldCodeRemote;
                config.remoteExport         = mdataMouldCodesExportRemote;
                config.sort_predicate_field = 'Code';
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                //Special process: Called from mould map!
                $scope.removeLoading();
                
                //Required objects to display in list
                $scope.plants = $scope.getCache(mdataModel.plant.model);
                $scope.groups = $scope.getCache(mdataModel.mouldGroup.model);
                
                var sqlM = $scope.getCacheModel(mdataModel.mouldMap.modelSql);
                var sql  = $scope.cacheObj._sql;
                var obj  = $scope.cacheObj.length > 0? $scope.cacheObj[0] : null;
                
                var groupCode   = null;
                var groupObject = null;
                var titleX = '';
                if (sql.getMouldGroupId() !== null){
                    var g = $scope.groups.getObjectById(sql.getMouldGroupId());
                    if (g !== null){
                        groupCode   = g.getCode();
                        groupObject = g;
                        var d = groupCode;
                        if (g.getDescr() !== null){
                            d = d + ' ' + g.getDescr(); 
                        }
                        
                        titleX = ' (' + $scope.label('Mould.G') + ': ' + d + ')';
                    }
                }
                else if (sql.getMouldCode() !== null){
                    titleX = ' (' + $scope.label('Code') + ': ' + sql.getMouldCode() + ')';
                }

                //override - Export to spreadsheet
                var ex = $scope.exportSS;
                $scope.exportSS = function(){
                    ex({sql:sqlM.json(sql)});
                };
                
                //Remove other groups
                if (groupObject !== null){
                    $scope.groups = [];
                    $scope.groups.push(groupObject);
                }
                else{
                    //var el = angular.element('#sbRecordNew');
                    //el.addClass('inactive');
                    $scope.setVisable('sbRecordNew', false);
                }
                
                
                //Override
                $scope.setMainTitle($scope.label('Moulds') + titleX);

                
                //Override
                var inputUpdate = $scope.inputUpdate;
                $scope.inputUpdate = function(record, field){
                    if (!angular.isDefined(record) || record === null){
                        return;
                    }

                    //Show which object is to be deleted
                    if (field === 'deleteX'){
                        if (record.getMCode() !== null && record.getMCode().length>0){
                            setDelete(record, 'cox');
                            setDelete(record, 'cdx');    
                        }
                        else{
                            setDelete(record, 'mcx');
                            setDelete(record, 'mdx');    
                        }
                    }
                    inputUpdate(record, field);
                };

                var setDelete = function (record, id){
                    var el = angular.element('#' + id + record.getId());
                    if (record.isDelete()){
                        el.addClass('so41-delx');
                    }
                    else{
                        el.removeClass('so41-delx');
                    }
                };


                //Override
                var recordNew = $scope.recordNew;
                $scope.recordNew = function(){
                    
                    if (groupCode === null){
                        return;
                    }
                    
                    var dto = recordNew();

                    dto.setPlantCode($scope.plants[0].getCode());
                    
                    if (groupCode !== null){
                        dto.setGroupCode(groupCode);
                    }
                    //try first object in list
                    else if (obj !== null){
                        dto.setGroupCode(obj.getGroupCode());
                    }
                    
                    dto.setMouldCode(sql.getMouldCode());
                }; 
               
                //Override
                var save = $scope.recordSave;
                $scope.recordSave = function(){
                    acDialogs.yesNo('Mould.UPt', 'Mould.UP', 
                            function(){saveX(true);},
                            function(){saveX(false);}
                    );
                };
                var saveX = function(update){
                    sql.setUpdateStyles(update);
                    var params = {sql:sqlM.json(sql)};
                    save(params);
                };
                
            }
    ])


    
    /**
     * Mould Groups
     */
    .controller('mdataMouldGroupCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'mdataModel',
            'mdataMouldGroupRemote',
            'mdataMouldGroupExportRemote',
            
            function($rootScope, $scope, acController, 
            		mdataModel, mdataMouldGroupRemote, mdataMouldGroupExportRemote){
             
                var config = acController.createConfig('mdataMouldGroupCtrl', mdataModel.mouldGroup);
                config.title                = 'Mould.Gs';
                config.remotePost           = mdataMouldGroupRemote;
                config.remoteExport         = mdataMouldGroupExportRemote;
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                //Test to show attribute input object (note passed in values are empty)
                $scope._si = function(colorFlag, colorId){
                    return $scope.isEdit();
                };

                /* *************************************************
                 * Color Picker Functions 
                 * TODO duplicate code - reduce
                 ***************************************************/
                //Override
                var recordSave = $scope.recordSave;
                $scope.recordSave = function(params){
                    cPicker.loaded = [];
                    recordSave(params);
                };
                
                var createColorPickerObject = function(){
                    var self = {};
                    
                    self.loaded = [];

                    self.isLoaded = function (id){
                        for (var i=0; i<this.loaded.length; i++){
                            if (this.loaded[i] === id){
                                return true;
                            }
                        }
                        return false;
                    };

                    self.setColorPicker = function (rec, setFn){
                        if (!self.isLoaded(rec.getId())){
                            //Initialise control variable
                            rec.colorActive = true;
                            self.loaded.push(rec.getId());
                            setColorPicker(rec, setFn, $scope);        
                        }
                        else{
                            $('#cpicker' + rec.getId()).click();
                        }
                    };

                    return self;
                };
                var cPicker = createColorPickerObject();

                $scope.setColor = function(rec){
                    if (!$scope.isEdit()){
                        return;
                    }
                    cPicker.setColorPicker(rec, 'setRgb');
                };

                $scope.colorPickerLabel = function(rec){
                    if ($scope.isEdit()){
                        return $scope.label('ClickChng');
                    }
                    return null;
                };
                
                $scope.blankColor = function(rec){
                    rec.setRgb('255,255,255');
                    rec.colorActive = false;
                    $('#colorpicker' + rec.getId()).fadeToggle("slow", "linear");
                };

                
            }
            
    ])
    

    
    /**************************************************************************************
     * Lasts
     **************************************************************************************/

    
    
    /**
     * Last Map
     */
    .controller('mdataLastMapCtrl', [
            '$rootScope',
            '$scope', 
            'acController', 
            'mdataModel',
            'mdataLastMapRemote',

            function($rootScope, $scope, acController, mdataModel, mdataLastMapRemote){

                var config = acController.createConfig('mdataLastMapCtrl', mdataModel.lastMap);
                config.title                = 'Lasts';
                config.remotePost           = mdataLastMapRemote;
                                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                var sqlM = $scope.getCacheModel(mdataModel.lastMap.modelSql);
                
                $scope.getViewLink = function(){
                    return 'rest/mdata/last/view';
                };
                
                $scope.openAllLasts = function(id){
                    var sql = sqlM.createDto();
                    open(sql);
                };
                
                $scope.oG = function(id){
                    var sql = sqlM.createDto();
                    sql.setLastGroupId(id);
                    open(sql);
                };
                
                $scope.oL = function(id){
                    var sql = sqlM.createDto();
                    sql.setLastCodeId(id);
                    open(sql);
                };
                
                var open = function(sql){
                    $scope.invalidateCache(mdataModel.lastCode.model);
                    $rootScope.pgConfig.lastCodeList.p = {sql:sqlM.json(sql)};
                    $scope.stateGo("lastCodeList");
                };
                
                $scope.openGroups = function(){
                    $scope.stateGo("lastGroup");
                };
                
            }
    ])


    /**
     * Lasts
     */
    .controller('mdataLastCodeCtrl', [
            '$rootScope',
            '$scope', 
            'acController', 
            'mdataModel',
            'mdataLastCodeRemote',
            
            function($rootScope, $scope, acController, mdataModel, mdataLastCodeRemote){

                var config = acController.createConfig('mdataLastCodeCtrl', mdataModel.lastCode);
                //config.title                = 'Lasts';
                config.remotePost           = mdataLastCodeRemote;
                config.sort_predicate_field = 'Code';
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                //Required objects to display in list
                $scope.plants = $scope.getCache(mdataModel.plant.model);
                $scope.groups = $scope.getCache(mdataModel.lastGroup.model);
                
                var sqlM = $scope.getCacheModel(mdataModel.lastMap.modelSql);
                var sql  = $scope.cacheObj._sql;
                var obj  = $scope.cacheObj.length > 0? $scope.cacheObj[0] : null;
                
                var groupCode = null;
                var groupObject = null;
                var titleX = '';
                if (sql.getLastGroupId() !== null){
                    var g = $scope.groups.getObjectById(sql.getLastGroupId());
                    if (g !== null){
                        groupCode = g.getCode();
                        groupObject = g;
                        var d = groupCode; 
                        if (g.getDescr() !== null){
                            d = d + ' ' + g.getDescr(); 
                        }
                        titleX = ' ' + $scope.label('Last.G') + ': ' + d;
                    }
                }
                else if (sql.getLastCode() !== null){
                    titleX = ' ' + $scope.label('Code') + ': ' + sql.getLastCode();
                }

                //Remove new fn for all groups
                if (groupObject === null){
                    $scope.setVisable('sbRecordNew', false);
                }
                
                //Override

                $scope.setMainTitle($scope.label('Lasts') + titleX);


                //Override
                var recordNew = $scope.recordNew;
                $scope.recordNew = function(){
                    var dto = recordNew();

                    dto.setPlantCode($scope.plants[0].getCode());
                    
                    if (groupCode !== null){
                        dto.setGroupCode(groupCode);
                    }
                    //try first object in list
                    else if (obj !== null){
                        dto.setGroupCode(obj.getGroupCode());
                    }
                    
                    dto.setLastCode(sql.getLastCode());
                }; 
               
                //Override
                var save = $scope.recordSave;
                $scope.recordSave = function(){
                    var params = {sql:sqlM.json(sql)};
                    save(params);
                };
                
                
            }
    ])


    
    /**
     * Last Groups
     */
    .controller('mdataLastGroupCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'mdataModel',
            'mdataLastGroupRemote',
            
            function($rootScope, $scope, acController, mdataModel, mdataLastGroupRemote){
             
                var config = acController.createConfig('mdataLastGroupCtrl', mdataModel.lastGroup);
                config.title                = 'Last.Gs';
                config.remotePost           = mdataLastGroupRemote;
                config.sort_predicate_field = 'Sort';
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                
                /* *************************************************
                 * Color Picker Functions 
                 * TODO duplicate code - reduce
                 ***************************************************/
                //Override
                var recordSave = $scope.recordSave;
                $scope.recordSave = function(params){
                    cPicker.loaded = [];
                    recordSave(params);
                };
                
                var createColorPickerObject = function(){
                    var self = {};
                    
                    self.loaded = [];

                    self.isLoaded = function (id){
                        for (var i=0; i<this.loaded.length; i++){
                            if (this.loaded[i] === id){
                                return true;
                            }
                        }
                        return false;
                    };

                    self.setColorPicker = function (rec, setFn){
                        if (!self.isLoaded(rec.getId())){
                            //Initialise control variable
                            rec.colorActive = true;
                            self.loaded.push(rec.getId());
                            setColorPicker(rec, setFn, $scope);        
                        }
                        else{
                            $('#cpicker' + rec.getId()).click();
                        }
                    };

                    return self;
                };
                var cPicker = createColorPickerObject();

                $scope.setColor = function(rec){
                    if (!$scope.isEdit()){
                        return;
                    }
                    cPicker.setColorPicker(rec, 'setRgb');
                };

                $scope.colorPickerLabel = function(rec){
                    if ($scope.isEdit()){
                        return $scope.label('ClickChng');
                    }
                    return null;
                };
                
                $scope.blankColor = function(rec){
                    rec.setRgb('255,255,255');
                    rec.colorActive = false;
                    $('#colorpicker' + rec.getId()).fadeToggle("slow", "linear");
                };
                
            }
            
    ])
    
    
    /**************************************************************************************
     * Machines
     **************************************************************************************/

    
    /**
     * Machines
     */
    .controller('mdataMachineCtrl', [
            '$rootScope',
            '$scope', 
            'acController', 
            'mdataModel',
            'mdataMachineRemote',

            function($rootScope, $scope, acController, mdataModel, mdataMachineRemote){

                //Need objects to display in list
                $scope.plants = $scope.getCache(mdataModel.plant.model);

                var config = acController.createConfig('mdataMachineCtrl', mdataModel.machine);
                config.title                = 'Machines';
                config.remotePost           = mdataMachineRemote;
                config.sort_predicate_field = $scope.plants.length > 1? 'PlantCode' : 'Nr';
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                

                var colorModel = $scope.getCacheModel(mdataModel.machineColor.model);


                //Test to show attribute input object (note passed in values are empty)
                $scope._si = function(colorFlag, colorId){
                    return $scope.isEdit();
                };
                
                //Override
                var recordNew = $scope.recordNew;
                $scope.recordNew = function(){
                    var dto = recordNew();
                    dto.setPlantCode($scope.plants[0].getCode());
                };

                $scope.showDetail = function(id){
                    var record = $scope.findById(id);
                    record.toggleSelect();
                };

                //Add a color
                $scope._ac = function(record){
                    var c = colorModel.createDto();                    
                    c.parent = record;
                    record.getColors().push(c);
                };

                //Show inner tables?
                $scope.selected = function(id){
                    var record = $scope.findById(id);
                    if (record !== null){
                        return record.isSelect();
                    }
                    return false;
                };

                //Show next title?
                $scope.selectedX = function(id){
                    if (!$scope.selected){
                        return false;
                    }
                    var index = $scope.findIndexById(id, $scope.list);
                    return index === null || index < $scope.list.length - 1;
                };

                $scope.shiftLabel = function (rec){
                    return $scope.label('Machine') + ' ' + rec.getNr() + ': ' + $scope.label('CapS');
                };
                
                $scope.colorLabel = function (rec){
                    return $scope.label('Machine') + ' ' + rec.getNr() + ': ' + $scope.label('MaxMSC');
                };

            }
    ])


    /**************************************************************************************
     * General
     **************************************************************************************/    
    
    /**
     * Plants
     */
    .controller('mdataPlantCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'mdataModel',
            'mdataPlantRemote',
             
            function($rootScope, $scope, acController, mdataModel, mdataPlantRemote){
             
                var config = acController.createConfig('mdataPlantCtrl', mdataModel.plant);
                config.title                = 'Plants';
                config.remotePost           = mdataPlantRemote;
                config.sort_predicate_field = 'Sort';
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
            }
    ])


    /**
     * Categories
     */
    .controller('mdataCategoryCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'mdataModel',
            'mdataCategoryRemote',
             
            function($rootScope, $scope, acController, mdataModel, mdataCategoryRemote){
             
                var config = acController.createConfig('mdataCategoryCtrl', mdataModel.category);
                config.title                = 'Categories';
                config.remotePost           = mdataCategoryRemote;
                config.sort_predicate_field = 'Event';
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
            }
    ])


    /**
     * Shifts
     */
    .controller('mdataShiftCtrl', [
            '$rootScope',
            '$scope', 
            'acController', 
            'mdataModel',
            'mdataShiftRemote',

            function($rootScope, $scope, acController, mdataModel, mdataShiftRemote){

                var config = acController.createConfig('mdataShiftCtrl', mdataModel.shift);
                config.title                = 'Shifts';
                config.remotePost           = mdataShiftRemote;
                config.sort_predicate_field = 'Sort';
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                //Need plant objects to display in list
                $scope.plants = $scope.getCache(mdataModel.plant.model);
                
                /**
                 * new record override to add default plant
                 */
                var recordNew = $scope.recordNew;
                $scope.recordNew = function(){
                    var dto = recordNew();
                    dto.setPlantCode($scope.plants[0].getCode());
                    dto.setPlantId($scope.plants[0].getId());
                };
                
                
                //Override
                var recordSave = $scope.recordSave;
                $scope.recordSave = function(params){
                    $scope.invalidateCache(mdataModel.machine.model);
                    recordSave(params);
                };
                
            }
    ])

    
    /**
     * Production Order Requirement Types
     */
    .controller('mdataReqtypeCtrl', [
            '$rootScope',
            '$scope', 
            'acController', 
            'mdataModel',
            'mdataReqtypeRemote',

            function($rootScope, $scope, acController, mdataModel, mdataReqtypeRemote){

                var config = acController.createConfig('mdataReqtypeCtrl', mdataModel.reqtype);
                config.title                = 'ReqTypes';
                config.remotePost           = mdataReqtypeRemote;
                config.sort_predicate_field = 'Sort';
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');


                /* *************************************************
                 * Color Picker Functions 
                 * TODO duplicate code - reduce
                 ***************************************************/
                //Override
                var recordSave = $scope.recordSave;
                $scope.recordSave = function(params){
                    cPicker.loaded = [];
                    recordSave(params);
                };
                
                var createColorPickerObject = function(){
                    var self = {};
                    
                    self.loaded = [];

                    self.isLoaded = function (id){
                        for (var i=0; i<this.loaded.length; i++){
                            if (this.loaded[i] === id){
                                return true;
                            }
                        }
                        return false;
                    };

                    self.setColorPicker = function (rec, setFn){
                        if (!self.isLoaded(rec.getId())){
                            //Initialise control variable
                            rec.colorActive = true;
                            self.loaded.push(rec.getId());
                            setColorPicker(rec, setFn, $scope);        
                        }
                        else{
                            $('#cpicker' + rec.getId()).click();
                        }
                    };

                    return self;
                };
                var cPicker = createColorPickerObject();

                $scope.setColor = function(rec){
                    if (!$scope.isEdit()){
                        return;
                    }
                    cPicker.setColorPicker(rec, 'setRgb');
                };

                $scope.colorPickerLabel = function(rec){
                    if ($scope.isEdit()){
                        return $scope.label('ClickChng');
                    }
                    return null;
                };
                
                $scope.blankColor = function(rec){
                    rec.setRgb('255,255,255');
                    rec.colorActive = false;
                    $('#colorpicker' + rec.getId()).fadeToggle("slow", "linear");
                };
                
            }
    ])
  
    
    /**************************************************************************************
     * Size table
     **************************************************************************************/
    
    .controller('mdataSizeCtrl', [
            '$rootScope',
            '$scope',
            '$timeout',
            'acController',
            'mdataModel',
            'mdataSizeRemote',
            
            function($rootScope, $scope, $timeout, acController, mdataModel, mdataSizeRemote){
             
                var config = acController.createConfig('mdataSizeCtrl', mdataModel.size);
                config.title                = 'Sizes';
                config.remotePost           = mdataSizeRemote;
                config.sort_predicate_field = 'Sort';
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                var sqlM = $scope.getCacheModel(mdataModel.size.modelSql);
                var sql  = $scope.cacheObj._sql;
                
                $scope.unique = sql.isUniqueCodes();
                
                $scope.initialise = function(){
                    $scope.setUnique();
                };
                
                //Redisplay 
                $scope.setUnique = function(){
                    $timeout(function() {
                        sql.setUniqueCodes($scope.unique);
                        var params = {sql:sqlM.json(sql)};
                        mdataSizeRemote.query(params, 
                            function(result) { //success callback
                                $scope.initList(result.getObject());
                            }); 
                    }, 50);
                };
                
            }
    ])
    
    
    /**************************************************************************************
     * Styles
     **************************************************************************************/
    
    /**
     * Styles
     */
    .controller('mdataStyleCtrl', [
            '$rootScope',
            '$scope', 
            '$http',
            '$timeout',
            '$state', 
            'acController',
            'acDefinition',
            'acDialogs',
            'mdataModel',
            'mdataStyleRemote',
            'mdataMouldCodesByGroupRemote',
            'mdataLastCodesByGroupRemote',
            'mdataStyleExportRemote',

            function($rootScope, $scope, $http, $timeout, $state, acController, acDefinition, acDialogs, 
                    mdataModel, mdataStyleRemote, mdataMouldCodesByGroupRemote, mdataLastCodesByGroupRemote,
                    mdataStyleExportRemote
                    ){

                var config = acController.createConfig('mdataStyleCtrl', mdataModel.style);
                config.title                = 'Styles';
                config.remotePost           = mdataStyleRemote;
                config.remoteExport         = mdataStyleExportRemote;
                config.maxRecordsPerPage    = 30;
                config.showPageSelection    = true; //if number of records > maxRecordsPerPage then show page selection header

                acController.configure($scope, config);
                
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header1');
                $scope.moveScroller('header2');
                $scope.lockLeftScroller('#left-col-table');
                
                var sqlM = $scope.getCacheModel(mdataModel.style.modelSql);
                var sql  = $scope.cacheObj._sql;
                
                createStyleMenu ($rootScope, $scope, $state, $timeout, acDefinition, config, mdataModel, sql);
                
            
                //override
                $scope.openMoulds = function(){
                    mdataModel.style.open = '';
                    sql.setIncludeMouldCodes(true);
                    sql.setIncludeLastCodes(false);
                    $scope.lookupAction();
                    $scope.setMainTitle();
                };
                
                //override
                $scope.openLasts = function(){
                    mdataModel.style.open = '';
                    sql.setIncludeLastCodes(true);
                    sql.setIncludeMouldCodes(false);
                    $scope.lookupAction();
                    $scope.setMainTitle();
                };
                
                
                $scope.groupKey = function(){
                    if (sql.isIncludeMouldCodes()){
                        return 'Mould.Gc';
                    }
                    else if (sql.isIncludeLastCodes()){
                        return 'Last.Gc';
                    }
                };
                
                
                //override - Export to spreadsheet
                var ex = $scope.exportSS;
                $scope.exportSS = function(){
                    ex({sql:sqlM.json(sql)});
                };
                
                
                //Override
                $scope.setMainTitle = function(){
                    var x = $scope.label('Moulds');
                    if (sql.isIncludeLastCodes()){
                        x = $scope.label('Lasts'); 
                    }   
                    document.getElementById("appTitle").innerHTML = $scope.label('Styles') + ' - ' + x;
                };
                $scope.setMainTitle();
                
                $scope.setLookup('mdata/lookup', sql, 20);
                
                //Group - Size Id's 
                var result = $scope.getCache(mdataModel.style.model);
                var emptyGr = 'egrXX';
                
                $scope.sizegrs = [];
                var sizeIds = {};
                var rgbgrs  = {};
                if (angular.isDefined(result._control) && result._control !== null){
                    for (var i=0; i<result._control.length; i++){
                        var c = result._control[i];
                        var ca = c.a;
                        $scope.sizegrs.push(ca);
                        if (ca === ''){
                            ca = emptyGr; 
                        }
                        sizeIds[ca] = c.b;
                        rgbgrs[ca]  = c.c;
                    }
                    config.sizeIds = sizeIds;
                    config.rgbgrs  = rgbgrs;
                    config.sizegrs = $scope.sizegrs;
                }
                else if (angular.isDefined(config.sizeIds)){
                    sizeIds        = config.sizeIds;
                    rgbgrs         = config.rgbgrs;   
                    $scope.sizegrs = config.sizegrs;
                }

                var groupsX = {};

                //Override
                var lookupAction = $scope.lookupAction; 
                $scope.lookupAction = function(){
                    sql.setStyle(null);
                    sql.setVariant(null);
                    sql.setDescr(null);
                    sql.setCode(null);
                    sql.setGroup(null);
                    sql.setMissingCodes(null);
                    
                    lookupAction();
                };
                
                //Call back for mould group descr field
                $scope.getMouldGroupId = function(record){
                    return 'id="mg_' + record.getId() + '"';
                };
                
                //return Style setting for group rgb color
                $scope.getStyleGroupRgb = function(record){
                    var rgb = findStyleGroupRgb (record);
                    if (rgb === null){
                        return '';
                    }
                    return 'style="background-color:rgb(' + rgb + ');"';
                };
                var findStyleGroupRgb = function(record){
                    if (record.getGroupStyle() === null 
                            || record.getGroupStyle().length === 0
                            || !angular.isDefined(rgbgrs[record.getGroupStyle()])){
                        return null;
                    }
                    return rgbgrs[record.getGroupStyle()];
                };
                
                
                $scope._ug = function(index, field){
                    var record = $scope.list[index];
                    $scope.inputUpdate(record, field);
                };
                
                //Override - Mould/Last updates
                var inputUpdate = $scope.inputUpdate;
                $scope.inputUpdate = function(record, field){
                    
                    if (field === 'Group' || field === 'GroupStyle'){
                        
                        var styleGr = record.getGroupStyle();
                        if (styleGr === null || styleGr === ''){
                            styleGr = emptyGr;
                        }
                        var sizeIdsX = sizeIds[styleGr];
                        
                        var groups  = null;
                        if (sql.isIncludeMouldCodes()){
                            groups = $scope.getCache(mdataModel.mouldGroup.model);
                        }
                        else{
                            groups = $scope.getCache(mdataModel.lastGroup.model);
                        }
                        
                        var g = groups.getObjectByCode(record.getGroup());
                        
                        //Thanks to http://stackoverflow.com/questions/2554149/html-javascript-change-div-content
                        var el = document.getElementById('mg_' + record.getId());
                        if (el !== null){
                            el.innerHTML = g !== null? g.getDescr() : '';     
                        }
                        
                        
                        if (g !== null){
                            record.setGroup(g.getCode());
                           
                            if (angular.isDefined(groupsX[g.getCode()])){
                                record.assignCode(groupsX[g.getCode()], sizeIdsX);
                            }
                            else if (sql.isIncludeMouldCodes()){
                                mdataMouldCodesByGroupRemote.query({id: g.getId()}, 
                                    function(result) { //success callback
                                        var codesList = result.getObject();
                                        groupsX[g.getCode()] = codesList;
                                        record.assignCode(codesList, sizeIdsX);
                                    });
                            }
                            else if (sql.isIncludeLastCodes()){
                                mdataLastCodesByGroupRemote.query({id: g.getId()}, 
                                    function(result) { //success callback
                                        var codesList = result.getObject();
                                        groupsX[g.getCode()] = codesList;
                                        record.assignCode(codesList, sizeIdsX);
                                    });
                            }
                        }
                        else{
                            record.assignCode(null, sizeIdsX);
                        }
                    }
                    inputUpdate(record, field);
                };
                
                
                //Override
                var save = $scope.recordSave;
                $scope.recordSave = function(){
                    var params = {sql:sqlM.json(sql), sz:angular.toJson(sizeIds)};
                    save(params);
                };
                
                
                //Override
                var recordEdit = $scope.recordEdit;
                $scope.recordEdit = function(){
             //     $scope.addLoading();
                    recordEdit();
                    editFlag = $scope.isEdit();
                };
                
                /**
                 * Open advance search dialog
                 */
                $scope.lookupAdvance = function(){
                    acDialogs.openDialog('mod/mdata/view/style_search.html', 'mdataStyleSearchCtrl', $scope.lookupParams);
                };
                
                /**
                 * Jump to last opened
                 */
                if (mdataModel.style.open === 'mould'){
                    $scope.openMoulds();    
                }
                
                /**
                 * html generator link
                 */
                var viewLinkEncoded = sqlM.json(sql);
                var editFlag        = $scope.isEdit();
                $scope.viewLinkId   = $scope.getNextId();
                $scope.getViewLink  = function(){
                    $scope.setVisable('result_table');
                    return 'rest/mdata/view?sql=' + encodeURIComponent(viewLinkEncoded) + "&lk=" + $scope.viewLinkId + '&e=' + editFlag + '&rl=' + true;
                };
                
                
                $scope._sgc = function (field, id){
                    var x = $scope.findById(id, $scope.list);
                    x[field] = 'B';
                };
                
                /**
                 * Jump to last opened
                 */
                if (mdataModel.style.open === 'last'){
                    $timeout(function() {
                        $scope.openLasts(); 
                    }, 50);
                }
            }
    ])

   
    
    /**
     * Style-Machines
     */
    .controller('mdataStyleMachineCtrl', [
            '$rootScope',
            '$scope', 
            '$http',
            '$state', 
            '$timeout',
            'acController',
            'acDefinition',
            'acDialogs',
            'mdataModel',
            'mdataStyleMachineRemote',

            function($rootScope, $scope, $http, $state, $timeout, 
                     acController, acDefinition, acDialogs, mdataModel, mdataStyleMachineRemote){

                var config = acController.createConfig('mdataStyleMachineCtrl', mdataModel.styleMachine);
                config.title                = 'StyleM';
                config.remotePost           = mdataStyleMachineRemote;
                config.maxRecordsPerPage    = 30;
                config.showPageSelection    = true; //if number of records > maxRecordsPerPage then show page selection header

                acController.configure($scope, config);
                
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header1');
                $scope.moveScroller('header2');
                $scope.lockLeftScroller('#left-col-table');
                
                var sql  = $scope.cacheObj._sql;
                
                createStyleMenu ($rootScope, $scope, $state, $timeout, acDefinition, config, mdataModel, sql);
                
                $scope.setLookup('mdata/lookup', sql, 20);
                  
                //Override
                var lookupAction = $scope.lookupAction; 
                $scope.lookupAction = function(){
                    sql.setStyle(null);
                    sql.setVariant(null);
                    sql.setDescr(null);
                    sql.setCode(null);
                    sql.setGroup(null);
                    sql.setMissingCodes(null);
                    
                    lookupAction();
                };
                
                //Override - Only one machine allowed
                var inputUpdate = $scope.inputUpdate;
                $scope.inputUpdate = function(record, field, index){
                    
                    if (field === 'MachineActive'){
                        for (var i=0; i<record.getMachineActive().length; i++){
                            if (i !== index){
                                record.getMachineActive()[i]=false;
                            }
                        }
                    }
                    inputUpdate(record, field);
                };

                
                /**
                 * Open advance search dialog
                 */
                $scope.lookupAdvance = function(){
                    sql.setIncludeMachines(true);
                    sql.setIncludeMaterials(false);
                    sql.setIncludeAttributes(false);
                    acDialogs.openDialog('mod/mdata/view/style_search.html', 'mdataStyleSearchCtrl', $scope.lookupParams);
                };
                
            }
    ])


    /**
     * Style-Attributes (with and without colors)
     */
    .controller('mdataStyleAttrCtrl', [
            '$rootScope',
            '$scope', 
            '$http',
            '$state', 
            '$timeout',
            'acController',
            'acDefinition',
            'acDialogs',
            'uploadManager',
            'mdataModel',
            'mdataStyleAttrRemote',
            'mdataStyleAttrExportRemote',
            'mdataStyleAttrImportRemote',

            function($rootScope, $scope, $http, $state, $timeout, 
                    acController, acDefinition, acDialogs, uploadManager, 
                    mdataModel, mdataStyleAttrRemote, mdataStyleAttrExportRemote, mdataStyleAttrImportRemote){

                var config = acController.createConfig('mdataStyleAttrCtrl', mdataModel.styleAttr);
                config.title                = 'AttrSA';
                config.remotePost           = mdataStyleAttrRemote;
                config.remoteExport         = mdataStyleAttrExportRemote;
                config.maxRecordsPerPage    = 30;
                config.showPageSelection    = true; //if number of records > maxRecordsPerPage then show page selection header
                
                acController.configure($scope, config);
                
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                var sql  = $scope.cacheObj._sql;
                
                createStyleMenu ($rootScope, $scope, $state, $timeout, acDefinition, config, mdataModel, sql);
                
                $scope.setLookup('mdata/lookup', sql, 20);
                  
                //Test to show attribute input object
                $scope._si = function(colorFlag, firstRowFlag, colorId){
                    if (!$scope.isEdit()){
                        return false;
                    }
                    if (firstRowFlag && !colorFlag){
                        return true;
                    }
                    if (colorId === 0 && !colorFlag){
                        return true;
                    }
                    if (colorId !== 0 && colorFlag){
                        return true;
                    }
                    return false;
                };
                
                //Override
                var lookupAction = $scope.lookupAction; 
                $scope.lookupAction = function(){
                    if (angular.isDefined(sql.setStyle)){
                        sql.setStyle(null);
                        sql.setVariant(null);
                        sql.setDescr(null);
                        sql.setCode(null);
                        sql.setGroup(null);
                        sql.setMissingCodes(null);
                        sql.setIncludeMouldCodes(false);
                        sql.setIncludeLastCodes(false);
                        sql.setSearchAttributes(null);
                    }
                    lookupAction();
                };
                
                /**
                 * Open advance search dialog
                 */
                $scope.lookupAdvance = function(){
                    if (angular.isDefined(sql.setIncludeMachines)){
                        sql.setIncludeMachines(false);
                        sql.setIncludeMaterials(false);
                        sql.setIncludeAttributes(true);
                    }
                    acDialogs.openDialog('mod/mdata/view/style_search.html', 'mdataStyleSearchCtrl', $scope.lookupParams);
                };
                
                
                
                /******************************
                 * SS File up load
                 ******************************/
                configImportSS($scope, $timeout, uploadManager, 'attr', 
                	function (filename){
                	    mdataStyleAttrImportRemote.queryForce(
	    						{fn:filename},
	    						function(result){
	    							$scope.displayDialog('AttrS', $scope.label('ImportOK'));
	    							$scope.openMoulds();
	    							return;
	    						},
	    						function(result) { //error callback
	    							$scope.displayErrorDialog('Invalid', result.getMessage());
	    						}
	    				);
                    }	
                );
                
                
                
            }
    ])

    
    /**
     * Style-Materials (with and without colors/sizes)
     */
    .controller('mdataStyleMaterialCtrl', [
            '$rootScope',
            '$scope', 
            '$http',
            '$state', 
            '$timeout',
            'acController',
            'acDefinition',
            'acDialogs',
            'uploadManager',
            'mdataModel',
            'mdataStyleMaterialRemote',
            'mdataStyleMaterialExportRemote',
            'mdataStyleBomImportRemote',

            function($rootScope, $scope, $http, $state, $timeout, 
                    acController, acDefinition, acDialogs, uploadManager,
                    mdataModel, mdataStyleMaterialRemote, mdataStyleMaterialExportRemote,
                    mdataStyleBomImportRemote
                    ){

                var config = acController.createConfig('mdataStyleMaterialCtrl', mdataModel.styleMaterial);
                config.title                = 'StyleMT';
                config.remotePost           = mdataStyleMaterialRemote;
                config.remoteExport         = mdataStyleMaterialExportRemote;
                config.maxRecordsPerPage    = 30;
                config.showPageSelection    = true; //if number of records > maxRecordsPerPage then show page selection header
                
                acController.configure($scope, config);
                
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                var sql  = $scope.cacheObj._sql;
                
                createStyleMenu ($rootScope, $scope, $state, $timeout, acDefinition, config, mdataModel, sql);
                
                $scope.setLookup('mdata/lookup', sql, 20);
                  
                //Test to show attribute input object
                $scope._si = function(colorFlag, colorId){
                    if (!$scope.isEdit()){
                        return false;
                    }
                    if (colorId === 0 && !colorFlag){
                        return true;
                    }
                    if (colorId !== 0 && colorFlag){
                        return true;
                    }
                    return false;
                };
                
                //Override
                var lookupAction = $scope.lookupAction; 
                $scope.lookupAction = function(){
                    if (angular.isDefined(sql.setStyle)){
                        sql.setStyle(null);
                        sql.setVariant(null);
                        sql.setDescr(null);
                        sql.setCode(null);
                        sql.setGroup(null);
                        sql.setMissingCodes(null);
                        sql.setIncludeMouldCodes(false);
                        sql.setIncludeLastCodes(false);
                        sql.setSearchAttributes(null);
                    }
                    lookupAction();
                };
                
                
                /**
                 * Open advance search dialog
                 */
                $scope.lookupAdvance = function(){
                    if (angular.isDefined(sql.setIncludeMachines)){
                        sql.setIncludeMachines(false);
                        sql.setIncludeMaterials(true);
                        sql.setIncludeAttributes(false);
                    }
                    acDialogs.openDialog('mod/mdata/view/style_search.html', 'mdataStyleSearchCtrl', $scope.lookupParams);
                };
                
                
                /******************************
                 * SS File up load
                 ******************************/
                configImportSS($scope, $timeout, uploadManager, 'bom', 
                	function (filename){
                	    mdataStyleBomImportRemote.queryForce(
	    						{fn:filename},
	    						function(result){
	    							$scope.displayDialog('AttrS', $scope.label('ImportOK'));
	    							$scope.openMoulds();
	    							return;
	    						},
	    						function(result) { //error callback
	    							$scope.displayErrorDialog('Invalid', result.getMessage());
	    						}
	    				);
                    }	
                );

                
                
            }
    ])

    
    
    /**
     * Attribute Type
     */
    .controller('mdataAttributeTypeCtrl', [
            '$rootScope',
            '$scope', 
            'acController', 
            'mdataModel',
            'mdataAttributeTypeRemote',
            
            function($rootScope, $scope, acController, mdataModel, mdataAttributeTypeRemote){

                var config = acController.createConfig('mdataAttributeTypeCtrl', mdataModel.attributeType);
                config.title                = 'AttrTS';
                config.remotePost           = mdataAttributeTypeRemote;
                config.sort_predicate_field = 'Sort';
                                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
               
                //Set the temp field type
                var setType = function (rec){
                    for (var i=0;i<attrs.length;i++){
                        var a = attrs[i];
                        if(rec.getProcessType() === a.getType()){
                            rec.setType(a.getDescr());
                            return;
                        }
                    }                        
                };
                

                //Set the process type for the description
                var setProcessType = function (rec){
                    for (var i=0;i<attrs.length;i++){
                        var a = attrs[i];
                        if(rec.getType() === a.getDescr()){
                            rec.setProcessType(a.getType());
                            return;
                        }
                    }                        
                };

                
                //Values defined in entities.mdata.AttributeI
                var attrs = $scope.getCache(mdataModel.attributeProcess.model);
                $scope.process = [];
                if (attrs !== null){
                    for (var i=0;i<attrs.length;i++){
                        $scope.process.push(attrs[i]);
                    }

                    for (var i=0;i<$scope.list.length;i++){
                        setType($scope.list[i]);
                    }
                }
                
                //Override
                var recordNew = $scope.recordNew;
                $scope.recordNew = function(){
                    var dto = recordNew();
                    dto.setProcessType($scope.process[0].getType());
                    setType(dto);
                };
                
                //Override
                var recordSave = $scope.recordSave;
                $scope.recordSave = function(params){
                    
                    //Set process type
                    for (var i=0;i<$scope.list.length;i++){
                        setProcessType($scope.list[i]);
                    }

                    recordSave(params);
                };
                
                //Override
                var postSuccess = $scope.postSuccess;
                $scope.postSuccess = function(result){
                    postSuccess(result);

                    //Set temp type
                    for (var i=0;i<$scope.list.length;i++){
                        setType($scope.list[i]);
                    }

                    
                };

                
            }
    ])
    
    
    /**
     * Styles Search
     */
    .controller('mdataStyleSearchCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'acDialogs',
            'mdataModel',
            'mdataStyleRemote',
            'config',

            function($rootScope, $scope, acController, acDialogs, mdataModel, mdataStyleRemote, config){
                
                var configX = acController.createConfig('mdataStyleSearchCtrl', mdataModel.style);
                acController.configure($scope, configX);
                
                var ATP_TOTAL = 6; /*Defined in AttributeI */

                createStyleSearch ($scope, mdataModel, config);
                
                $scope.setLookup('mdata/lookup', $scope.dto, 20);
                //Override 
                $scope.lookupParamaters = function(p){
                    if (angular.isDefined(config.toggleIO)){
                        p.active = config.toggleIO;
                    }
                    p.desc = false;
                };

                $scope.cancel = function(){
                    config.close('ok');
                };
                
                $scope.search = function(){
                    
                    //Atrributes (searchAttrX are defined in StyleSearchCallback)
                    var x = "";
                    for (var i=1;i<=ATP_TOTAL;i++){
                        if (angular.isDefined($scope['searchAttr' + i])
                                && (!angular.isString($scope['searchAttr' + i]) || $scope['searchAttr' + i].length > 0)){
                            
                            if (x.length > 0){
                                x += ',';
                            }
                            
                            x += i + "=" + $scope['searchAttr' + i]; 
                        }
                        
                    }
                    $scope.dto[$scope.model.getSearchAttributes_F()] = x;
                    
                    config.close('ok');
                    config.scope.reinitialise($scope.dto, 150);
                };
                
            }
     ])
     
     
    /**
     * Material Type
     */
    .controller('mdataMaterialTypeCtrl', [
            '$rootScope',
            '$scope', 
            'acController', 
            'mdataModel',
            'mdataMaterialTypeRemote',

            function($rootScope, $scope, acController, mdataModel, mdataMaterialTypeRemote){

                var config = acController.createConfig('mdataMaterialTypeCtrl', mdataModel.materialType);
                config.title                = 'MatTS';
                config.remotePost           = mdataMaterialTypeRemote;
                config.sort_predicate_field = 'Sort';
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                
            }
    ])
    
    
   /**
     * PU Type
     */
    .controller('mdataPUTypeCtrl', [
            '$rootScope',
            '$scope', 
            'acController', 
            'mdataModel',
            'mdataPUTypeRemote',

            function($rootScope, $scope, acController, mdataModel, mdataPUTypeRemote){

                var config = acController.createConfig('mdataPUTypeCtrl', mdataModel.putype);
                config.title                = 'PUTyps';
                config.remotePost           = mdataPUTypeRemote;
                config.sort_predicate_field = 'Code';
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                
            }
    ])

    
    /**
     * PU Configuration
     */
    .controller('mdataPUConfigCtrl', [
            '$rootScope',
            '$scope', 
            'acController', 
            'mdataModel',
            'mdataPUConfigRemote',

            function($rootScope, $scope, acController, mdataModel, mdataPUConfigRemote){

                var config = acController.createConfig('mdataPUConfigCtrl', mdataModel.puconfig);
                config.title                = 'PUCons';
                config.remotePost           = mdataPUConfigRemote;
                config.sort_predicate_field = 'Code';
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                $scope._se = function(record){
                	if ($scope.isEdit()){
                		return true;
                	}
                	if (record.isNew()){
                		return true;
                	}
                	
                	return false;
                };
                
            }
    ]);







     
    var createStyleMenu = function($rootScope, $scope, $state, $timeout, acDefinition, config, mdataModel, sql){
    
        $scope.toggleIO = sql.getActiveOnly();
        $scope.toggleImportOnly = function(){
            $timeout(function() {
                    sql.setActiveOnly($scope.toggleIO);
                    sql.setTempTableMaterials(null);
                    createStyleSearch ($scope, mdataModel, {scope: $scope, sql : sql});
                    $scope.reinitialise($scope.dto, 150);
                }, 50);
        }; 
         
        //Override 
        $scope.lookupParamaters = function(p){
            p.active = $scope.toggleIO;
        };

        $scope.lookupParams = {scope: $scope, sql : sql, toggleIO: $scope.toggleIO};

        $scope.openAttr = function(){
            if (config.controllerName === 'mdataStyleAttrCtrl'){
                return;
            }
            $state.go("styleAttrList");
        };
        
        $scope.openMaterials = function(){
            if (config.controllerName === 'mdataStyleMaterialCtrl'){
                return;
            }
            $state.go("styleMaterialList");
        };
        
        $scope.openMachines = function(){
            if (config.controllerName === 'mdataStyleMachineCtrl'){
                return;
            }
            $state.go("styleMachineList");
        };
        
        $scope.openMoulds = function(){
            mdataModel.style.open = 'mould';
            $state.go("styleList");
        };
        
        $scope.openLasts = function(){
            mdataModel.style.open = 'last';
            $state.go("styleLast");
        };
    
    };


    var createStyleSearch = function($scope, mdataModel, config){

        $scope.model = $scope.getCacheModel(mdataModel.style.modelSql);
        $scope.dto   = $scope.model.createDto();
        
        $scope.dto.setStyle (config.sql.getStyle());
        $scope.dto.setVariant (config.sql.getVariant());
        $scope.dto.setColor (config.sql.getColor());
        $scope.dto.setDescr (config.sql.getDescr());
        $scope.dto.setCode (config.sql.getCode());
        $scope.dto.setGroup (config.sql.getGroup());
        $scope.dto.setStyleGroup (config.sql.getStyleGroup());
        $scope.dto.setMaterial (config.sql.getMaterial());
        $scope.dto.setMaterialColor (config.sql.getMaterialColor());
        $scope.dto.setMissingCodes (config.sql.getMissingCodes());
        $scope.dto.setInactivePrep (config.sql.getInactivePrep());
        $scope.dto.setActiveOnly (config.sql.getActiveOnly());
        
        
        var lcv  = true;
        var lgv  = true;
        var lsg  = false;
        var lmv  = true;
        var liv  = true;
        var lmt  = false;
        var lat  = false;
        
        $scope.labelCodeV       = function(){return lcv;};
        $scope.labelGroupV      = function(){return lgv;};
        $scope.labelStyleGroupV = function(){return lsg;};
        $scope.labelMissV       = function(){return lmv;};
        $scope.labelInactV      = function(){return liv;};
        $scope.labelMatV        = function(){return lmt;};
        $scope.labelMatCV       = function(){return lmt;};
        $scope.labelAttr        = function(){return lat;};
        
        
        //Set search for mould codes
        if (config.sql.isIncludeMouldCodes()){
            $scope.dto.setIncludeMouldCodes(true);
            $scope.labelCode  = $scope.label('Mould.C');
            $scope.labelGroup = $scope.label('Mould.Gc');
            $scope.labelMiss  = $scope.label('Mould.M');
            $scope.labelInact = $scope.label('StyleIP');
            lsg = true;
        }
        
        //Set search for last codes
        else if (config.sql.isIncludeLastCodes()){
            $scope.dto.setIncludeLastCodes(true);    
            $scope.labelCode  = $scope.label('Last.C');
            $scope.labelGroup = $scope.label('Last.Gc');
            $scope.labelMiss  = $scope.label('Last.M');
            liv = false;
        }
        
        //Set search for machines
        else if (config.sql.isIncludeMachines()){
            $scope.dto.setIncludeMachines(true);    
            $scope.labelMiss  = $scope.label('StyleLM');
            lcv = false;
            lgv = false;
            liv = false;
        }
        
        //Set search for materials
        else if (config.sql.isIncludeMaterials()){
            $scope.dto.setIncludeMaterials(true);    
            lmv = false;
            lcv = false;
            lgv = false;
            liv = false;
            lmt = true;
        }
        
        //Set search for attributes
        else if (config.sql.isIncludeAttributes()){
            $scope.dto.setIncludeAttributes(true);    
            lmv = false;
            lcv = false;
            lgv = false;
            liv = false;
            lat = true;
        }
        
        else{
            lcv = false;
            lgv = false;
            lmv = false;
            liv = false;
        }
        

    };

     
    /**
     * File up load
     * 
     * Notes:
     * - The upload is handled by FileUploadServlet (defined in web.xml)
     */
    var configImportSS = function($scope, $timeout, uploadManager, hrefParam, importFn){

        $scope.upload = function() {
            $timeout(function() {
                angular.element("#upload_file").trigger('click');
            }, 50);
        };
        
        $scope.getFileUploadHref = function(){
            return 'import/fileUpload?t=' + hrefParam;
        };
        
        var percentage = 0;
        var filename_up = null;
        var uploadfile_up = null;
        var timeoutPromise = null;
    
        $scope.$on('fileAdded', function (e, filename, uploadfile) {
            
            if (!filename.endsWith('.xls')
                    && !filename.endsWith('.xlsx')){
                uploadManager.clear();
                alert ($scope.label('ImportIF'));
                return;
            }
            
            $scope.addLoading();
            filename_up = filename;
            uploadfile_up = uploadfile;
            
            timeoutPromise = $timeout(function() {
                uploadComplete('timeout');
            }, 30000);
            
            uploadManager.upload();
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
            if (timeoutPromise !== null){
        		$timeout.cancel(timeoutPromise);
        	}
            
            if (source === 'timeout' && uploadfile_up !== null){
            	$scope.displayErrorDialog('Invalid', $scope.label('ImportEr1'));
            }
            else if (source === 'timeout'){
            	$scope.displayErrorDialog('Invalid', $scope.label('ImportEr4'));
            }
            else if (uploadfile_up === 'file'){
            	importFn(filename_up);
            }
        };
        
        $scope.$on('$destroy', function(){
            if (timeoutPromise !== null){
                $timeout.cancel(timeoutPromise);
            }
        });

    	
    };
    
