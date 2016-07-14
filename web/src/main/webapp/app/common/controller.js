'use strict';
angular.module('app.common.controller', ['app.common.global'])

    /**
     * @doc module
     * @name app.common.controller
     * @description 
     * 
     * Base (common) controller functions.<p> 
     * 
     * The design goals of <b>this</b> module are:<ol>
     *     <li>provide a consistent controller behavior within the client application.</li><br>
     *     <li>controllers have a lot of boiler plate code which should be written only once, ie here. Hence this goal is to minimize 
     *         repeated code.</li><br> 
     * </ol><p>
     * 
     * Design notes:<ul>
     *     <li></li><br>
     * </ul>
     * 
     * Module dependencies:<ul>
     *     <li>app.lang.service</li>
     *     <li>app.common.global</li>
     *     <li>app.common.userobject</li>
     * </ul>
     * 
     * TODO: IMPLEMENT THIS MODULE
     * - refactor this module to have only one controller
     * - pass in a control object with various presets
     * - use this module to create default preset
     * - unit test
     * 
     * [License]
     * @author John Stewart
     */
    .service('acController', function($rootScope, $state, $stateParams, $http, $injector, $interval, $timeout, $window, ipCookie,
            aLang, acDefinition, acCache, acModel, acUserObj, acGlobal, acDialogs, acHeartbeatRemote) {
        

        var self = {
            
            /**
             * Create a default controller configuration object. This can amended by the client prior to 
             * the controller configuration. The configuration object is cached and can be used for
             * setting / resetting config attributes.
             *
             * @param controller name
             * @param main DTO model
             */
            createConfig: function (controllerName, model) {
                var conf;

                //Setup controller config cache 
                if (!angular.isDefined($rootScope.confCntrl)){
                    $rootScope.confCntrl = {};
                    conf = {};
                }
                else{
                    conf = $rootScope.confCntrl[controllerName];
                    if (angular.isDefined(conf) && conf !== null){
                        return conf;
                    }
                    conf = {};
                }

                //Type values are page (normal page) or dialog (modal dialog)
                conf.type  = 'page';

                //General attibutes (set defaults)
                conf.title                = null;
                conf.remotePost           = null;
                conf.remoteExport         = null;
                conf.list                 = null;
                conf.listOrg              = null;            //Used by mulitple pages to remember list when toggling edit mode
                conf.initCachedList       = true;
                conf.editmode             = false;
                conf.sort_predicate_field = '';
                conf.sort_predicate       = '';
                conf.sort_reverse         = false;
                conf.sort_initialised     = false;
                conf.showPageSelection    = false;
                conf.pageObject           = null;
                conf.removeCacheObjects   = false;            //Once loaded, remove objects from cache and controller form cache on destroy (unless reloading)
                conf.controllerName       = controllerName;   //Unique id for this controller in cache
                conf.reloading            = false;            //If true, then do not remove this controller from cache
                conf.scrollLeft           = 0;
                conf.scrollTop            = 0;
                
                
                //Model
                if (!angular.isDefined(model) || model.model === null){
                    conf.modelDef         = {};
                    conf.modelDef.valid   = false;
                    conf.menuId           = '';
                }
                else{
                    conf.modelDef         = model;
                    conf.modelDef.valid   = true;
                    conf.menuId           = model.model;
                    
                    //'.' are not allowed in id's
                    var i = conf.menuId.indexOf('.');
                    while (i !== -1){
                        conf.menuId = conf.menuId.substring(0,i) + conf.menuId.substring(i + 1);
                        i = conf.menuId.indexOf('.');
                    }
                
                    if (angular.isDefined(model.cacheObjects)){
                        conf.removeCacheObjects = !model.cacheObjects;
                    }

                }


                //Multiple pages
                conf.maxRecordsPerPage    = 50;
                conf.maxPageSel           = 10;


                $rootScope.confCntrl[controllerName] = conf;  //Cache this controller. Allows user to navigate away and then return. This behavior can be overwritten via model.cacheObjects 
                return conf;
            
            }, //createConfig


            /**
             * Common functions for all controllers.
             *
             * @param controller scope object
             * @param controller configuration object
             * @param objects to display (if defined)
             */
            configure: function ($scope, conf, objects) {
                var scope = $scope;

                //Default functions
                aLang.addFormFunctions(scope);

                //Convenience cache functions 
                scope.getCache = function (key){
                    return acCache.get(key);
                };
                scope.invalidateCache = function (key){
                    return acCache.invalidate(key);
                };
                scope.getCacheModel = function (key){
                    return acCache.getModel(key);
                };
                scope.putCacheModel = function (key, object){
                    return acCache.putModel(key, object);
                };
                

                //Model objects
                scope.model    = null;
                scope.sqlObj   = null;
                scope.cacheObj = null;

                if (conf.modelDef.valid){
                    scope.model = acCache.getModel(conf.modelDef.model);
                    
                    if (angular.isDefined(objects)){
                        scope.cacheObj = objects;
                    }
                    else{
                        scope.cacheObj = acCache.get(conf.modelDef.model);

                        //Remove list and controller cache objects on destroy, forces new list for each call to this controller
                        if (conf.removeCacheObjects){
                            scope.$on('$destroy', function() {
                                scope.destroy();                
                            });
                        }
                    }

                    if (scope.cacheObj !== null && angular.isDefined(scope.cacheObj._sql)){
                        scope.sqlObj = scope.cacheObj._sql;
                    }
                    
                }


                //Remove cached objects 
                scope.destroy = function(){
                    if (!conf.reloading){
                        acCache.invalidate(conf.modelDef.model);
                        $rootScope.confCntrl[conf.controllerName] = null;    
                    }
                };

                
                //User type status
                scope.isAdmin = false;
                scope.isService = false;
                scope.setUserType = function(){
                    scope.isAdmin = acUserObj.isAdmin();
                    scope.isService = acUserObj.isService();
                };
                scope.setUserType();
                

                scope.listOrg = null;
                scope.list    = null;
                scope.page    = null;
                
                if (scope.cacheObj === null){
                    //do nothing
                }
                else if (angular.isArray(scope.cacheObj)){
                    scope.listOrg = scope.cacheObj;
                    conf.isCachedObjectList = true;
                }
                //not a list object
                else{
                    conf.isCachedObjectList = false;
                }
                

                //scope.list is a copy of the actual list. This allows editing without affecting the application cache
                scope.initList = function(list){

                    //Get selected id's to re-apply
                    var selectedIds = [];
                    if (scope.model.isConfig('selectable')
                           && angular.isDefined(scope.list) 
                           && scope.list !== null){
                        for (var i=0; i<scope.list.length; i++){  
                            var rec = scope.list[i];
                            if (rec.isSelect()){
                                selectedIds.push(rec.getId());
                            }
                        }
                    }

                    scope.list = scope.listCopy(list);

                    //Configure new list
                    if (scope.model !== null){
                        scope.model.configureObject(list);
                    }
                    
                    conf.list = scope.list; 

                    //Re-Select
                    for (var i=0; i<selectedIds.length; i++){  
                        var id = selectedIds[i];
                        var rec = scope.list.getObjectById(id);
                        if (rec !== null){
                            rec.setSelect();    
                        }
                    }


                    scope.testShowWarning();
                    scope.testShowChanges();
                };

                //Copy passed in list objects and arrays (but not functions)
                scope.listCopy = function(list){
                    var listX = [];
                
                    //Copy list functions
                    for (var f in list){
                        if (!angular.isFunction(list[f])){
                            continue;
                        }
                        listX[f] = list[f];
                    }

                    for (var i=0; i<list.length; i++){
                        var rec = list[i];
                        
                        if (rec === null 
                                || !angular.isObject(rec)
                                || angular.isDate(rec)){
                            listX.push(rec);    
                            continue;
                        }
                        
                        var recX = {};
                        
                        for (var f in rec){
                            if (angular.isFunction(list[f])){
                                continue;
                            }
                            else if (angular.isArray(rec[f])){
                                recX[f] = scope.listCopy(rec[f]); 
                            }
                            else{
                                recX[f] = rec[f];
                            }
                        }
                        listX.push(recX);
                    }
                    
                    return listX;
                };
                
                //Helper function to set top title in gui
                scope.setMainTitle = function(key){
                    try{
                        if (acGlobal.globals().testCompany){
                            key = '*** TEST ***  ' + (key !== null? scope.label(key) + '  *** TEST ***' : '');
                        }
                        
                        if (key !== null){
                            document.getElementById("appTitle").innerHTML = scope.label(key);
                        }
                    } catch (err){}
                };
                scope.setMainTitle(conf.title);

                
                //Helper functions
                scope.getColumnClass = columnClassFn;

                //Number of records in main list
                scope.getListCount = function(){
                    if (scope.list === null){
                        return 0;
                    }

                    return scope.list.length;
                };

                //Retrieve object index via its id
                scope.findIndexById = function(id, list){
                    if (!angular.isDefined(id) 
                            || id === null
                            || !angular.isDefined(list)
                            || list === null){
                        return null;
                    }

                    for (var i=0; i<list.length; i++){
                        var r = list[i];
                        if (r.getId() === id){
                            return i;
                        }
                    }
                    return -1;
                };

                //Retrieve object via its id
                scope.findById = function(id, list){
                    if (!angular.isDefined(list)){
                        list = scope.list;
                    }
                    var index = scope.findIndexById(id, list);
                    if (index === -1){
                        return null;
                    }
                    return list[index];
                };

                /**
                 * Edit controls
                 */
                scope.recordEdit = function(){
                    conf.editmode = !conf.editmode; 
                    conf.reloading = true;

                    $state.transitionTo($state.current, $stateParams, {
                        reload: true,
                        inherit: false,
                        notify: true
                    });
                };
                scope.isEdit = function(){return conf.editmode;};
                

                /**
                 * Update controls
                 */
                scope.inputUpdate = function(record, field){
                    if (!angular.isDefined(record) || record === null){
                        return;
                    }

                    scope.testShowWarning(record);
                    scope.testShowChanges();

                    //Update invalid class
                    var el = angular.element("#id_" + record.getId() + "_" + field);
                    if (record.isValid(field)){
                        el.removeClass('div-table-col-invalid');
                    }
                    else{
                        el.addClass('div-table-col-invalid');
                    }
                    
                    
                };


                /**
                 * new record controls
                 */
                scope.recordNew = function(){
                    var dto = scope.model.createDto();
                    scope.list.addNew(dto); 
                    scope.testShowWarning (dto);
                    scope.testShowChanges();
                    return dto;
                };

                
                /**
                 * Export to spreadsheet
                 */
                scope.exportSS = function(params){
                    if (!angular.isDefined(params)){
                        params = null;
                    }
                    conf.remoteExport.queryForce(params, 
                            function(result){
                                    var url = 'rest/spreadsheet/return?filename=' + result.getObject();
                                    $window.open(url, '_tab');
                                }, function (result){
                                   alert($scope.label(result.getMessage()));
                            });
                };
                    
                    
                /**
                 * Is the scope list changed?
                 */
                scope.isRecordChanged = function(){
                    return angular.equals(scope.list, scope.listOrg);
                };
                
                /**
                 * Roll back all changes
                 */
                scope.recordUndo = function(){
                    acDialogs.yesNo('Undo', 'ResetChanges', function(){scope.initList(scope.listOrg);});
                };

                /**
                 * Save list to server (provided a POST method is configured). If successful then the scope list is updated with the 
                 * newly saved list.
                 */
                scope.recordSave = function(params){
                    if (conf.remotePost === null){
                        return;
                    }

                    try{
                        scope.list.clearErrors();
                    }
                    catch (err){}

                    if (!scope.isListValid()){
                        scope.displayDialog('Save', 'CantSave');
                        return;
                    }


                    //Only post new and changed records
                    var listX = [];
                    for (var i=0; i<scope.list.length; i++){
                        var o1 = scope.list[i];
                        
                        if (o1.isNew() 
                                && angular.isFunction(o1.isDelete)
                                && o1.isDelete()){
                            continue;
                        }

                        var o2 = scope.findById(o1.getId(), scope.listOrg);

                        if (testChangesObject(o1, o2)){
                            listX.push(o1);
                        }
                    }

                    if (!angular.isDefined(params)){
                        params = {response: 'full'}; //Request server to send back a complete new list (TODO: make this a parameter in the modelconfig)
                    }
                    
                    conf.remotePost.post(
                            listX,             //list of objects to post to server
                            params,
                            function(result) { //success callback
                                scope.postSuccess(result);
                            },
                            function(result) { //error callback
                                scope.postError(result);
                            }
                    );
                };


                /**
                 * Successful handling from call to <code>POST</code>
                 * UPDATE NOTE: Overridden by page controller
                 */                
                scope.postSuccess = function(result){
                    scope.listOrg = result.getObject();
                    scope.initList(result.getObject());
                    
                    if (angular.isDefined(scope.initialise)){
                        scope.initialise();
                    }
                };


                /**
                 * Error handling from call to <code>POST</code>
                 */                
                scope.postError = function(result){
                    if (result.isReturnDto()){

                        //Set errors in actual objects
                        scope.setErrors(result, scope.list);
                        
                        if (angular.isDefined(scope.errorCallback)){
                            scope.errorCallback(result);
                        }
                    
                        var m;
                        if (angular.isDefined(result.m)){
                            m = result.m;
                        }
                        else if (result.isMessage()){
                            m = result.getMessage();
                        }
                        else{ //Standard message
                            m = scope.label('CantSave');
                        }
                        scope.displayErrorDialog('Save', m);
                    }
                    //Damn, uncaught error!
                    else{
                        acDialogs.error();
                    }
                };


                /**
                 * Convenience method to return a new id 
                 */
                scope.getNextId = function(){
                    return acGlobal.getNextId();
                };
                

                /*************************************************
                 * Message dialogs
                 * Note: Error dialogs return an id that controlls
                 * if they are to be reopened
                 *************************************************/
                /**
                 * Display message in dialog
                 * @param dialog title
                 * @param dialog message
                 */
                scope.displayDialog = function(title, message){
                    acDialogs.message(title, message);
                };

                /**
                 * Return a new dialog id (used to control if reopened)
                 */
                scope.newDialogId = function(){
                    return '_dialogId_' + acGlobal.getNextId();
                };
                
                /**
                 * Test if dialog is open
                 */
                var isDialogOpen = function(id){
                    if (angular.isDefined(id) 
                    		&& id !== null
                    		&& angular.isDefined(scope[id])){
                        return scope[id];
                    }
                    return false;
                };
                
                
                /**
                 * Display error message in dialog
                 * @param dialog title
                 * @param dialog message (either string or ReturnMessageDto)
                 * @param dialog id (if used)
                 * @param show no repeat message
                 */
                scope.displayErrorDialog = function(title, message, id, norepeat){
                    if (isDialogOpen(id)){
                        return;
                    }
                    if (angular.isDefined(id) && id !== null){
                        scope[id] = true;
                    }
                    
                    var m = message;
                    var d = null;
                    
                    if (angular.isObject(message)){
                        m = message.m;
                        d = message.d;
                    }
                    
                    var params = {};
                    params.title    = title;
                    params.message  = m;
                    params.detail   = d;
                    params.norepeat = norepeat;
                    
                    acDialogs.errorMessage(params);
                };
                
                
                /**
                 * Display error message in dialog
                 * @param dialog title
                 * @param dialog message
                 * @param dialog id (if used)
                 */
                scope.displayErrorUnknown = function(id){
                    acDialogs.error();
                };
                
                
                /**
                 * Display formated error
                 * @param return result object
                 */
                scope.displayError = function(result){
                    if (result.isReturnDto()){
                        var m;
                        if (result.isMessage()){
                            m = result.getMessage();
                        }
                        else{ //Standard message
                            m = scope.label('ErrUnknown');
                        }
                        scope.displayErrorDialog('CantRunPrep', m);
                    }
                    //Damn, uncaught error!
                    else{
                        scope.displayErrorUnknown();
                    }                        
                };
                
                /*************************************************
                 * Open dialog
                 *************************************************/
                
                /**
                 * Convenience method to open a dialog.<p>
                 * If model is passed in with the config object, then any dependencies on the model will first be resolved before the dialog is opened.
                 * 
                 * @param dialog url
                 * @param dialog controller
                 * @param dialog config object (if undefined then one is created)
                 */
                scope.openDialog = function(url, controller, dialogConfig){
                    var model = null;
                    
                    if (!angular.isDefined(dialogConfig)){
                        dialogConfig = {scope: $scope};
                    }
                    else if (!angular.isDefined(dialogConfig.scope)){
                        dialogConfig.scope = scope;
                    }

                    if (angular.isDefined(dialogConfig.model)){
                        model = dialogConfig.model;
                    }
                    
                    if (model !== null){
                        resolveDependencies(
                                model,
                                function(){
                                    acDialogs.openDialog(url, controller, dialogConfig);    
                                });
                    }
                    else{
                        acDialogs.openDialog(url, controller, dialogConfig);
                    }
                    
                    
                };
                
                
                /**
                 * Resolve dependencies on passed in model. Once resolved the the callback is fired.
                 * @param model with dependencies 
                 * @param callback to fire once resolved
                 */
                var resolveDependencies = function(model, callback){
                    
                    var load = [];

                    //Load dependencies first (remotes for loading lists)
                    if (angular.isDefined(model.dependencies)){
                        for(var i=0; i<model.dependencies.length; i++){
                            var rem = $injector.get(model.dependencies[i]);
                            load[i] = rem.query();
                        }
                    }
                    
                    
                    //Load sql model
                    if (angular.isDefined(model.modelSql) && !acCache.isModel(model.modelSql)){
                        load.push(acDefinition.loadSql(model.modelSql));
                    }


                    var stopTime = $interval(function() {
                        //Test if dependencies are loaded
                        var fire = true;
                        for(var i=0; i<load.length; i++){
                            if (!load[i]()){
                                fire = false;
                            }
                        }

                        if (fire){
                            $interval.cancel(stopTime);
                            callback();
                            return true;
                        }
                    }, 100);
                };
                
                
                
                
                
                /*************************************************
                 * Errors
                 *************************************************/
                
                
                /**
                 * Set returned errors on passed in list
                 * @param server return object
                 * @param list to look in for record
                 */
                scope.setErrors = function(result, list){
                    if (result.isMessageById()){
                        for (var i=0; i<list.length; i++){
                            var rec = list[i];
                            var errors = result.getMessagesById(rec.getId());
                            for (var j=0; j<errors.length; j++){
                                rec.addError(errors[j]);
                            }
                            if (errors.length > 0){
                                scope.testShowWarning(rec);
                            }
                            
                        }
                    }
                };

                /**
                 * Is the current list valid? I.e. are there any validation errors
                 * Note: If record is flagged delete, then validations are ignored
                 */
                scope.isListValid = function(){
                    for (var i=0; i<scope.list.length; i++){
                        var rec = scope.list[i];
                        if (!rec.isDelete() && !rec.isValid()){
                            return false;
                        }
                    }
                    return true;
                };

                /**
                 * Test if record warning icon is to be shown
                 * @param record to test and display warning for
                 */
                scope.testShowWarning = function(record){
                    var w = angular.element("#sidebar_warn_" + conf.menuId);

                    if (!angular.isDefined(record) || record === null){
                        w.hide();
                        return;
                    }
                    
                    var k = angular.element("#" + record.getId() + "_warn");
                    if (!record.isValid()){
                        k.show();
                        w.show();
                    }
                    else{
                        k.hide();
                        w.hide();
                    }
                };

                /**
                 * Test for changes to list objects
                 * If changed, show menu save icon
                 */
                scope.testShowChanges = function(){
                    var test = testChangesList(scope.listOrg, scope.list);
                    var s= angular.element("#sidebar_save_" + conf.menuId);
                    if (test){
                        s.show();
                    }
                    else{
                        s.hide();
                    }
                    return test;
                };


                /**
                 * Test for changes between passed in lists
                 */
                var testChangesList = function(list1, list2){
                    
                    if (!angular.isDefined(list1) 
                        || list1 === null
                        || !angular.isDefined(list2)
                        || list2 === null){
                        return false;
                    }

                    if (list1.length !== list2.length){
                        return true;
                    }
                    
                    for(var i=0; i<list1.length; i++){
                        if (testChangesObject(list1[i], list2[i])){
                            return true;
                        }
                    }
                    return false;
                };

                /**
                 * Test for changes between passed in objects
                 */
                var testChangesObject = function(o1, o2){
                    if (!angular.isDefined(o1) && !angular.isDefined(o2)){
                        return false;
                    }

                    if (!angular.isDefined(o1) || !angular.isDefined(o2)){
                        return true;
                    }

                    if (o1 === null && o2 === null){
                        return false;
                    }

                    if (o1 === null || o2 === null){
                        return true;
                    }

                    if (!angular.isObject(o1) && !angular.isObject(o2)){
                        return o1 !== o2;
                    }
                    if (!angular.isObject(o1) || !angular.isObject(o2)){
                        return true;
                    }

                    if (angular.isDate(o1) && angular.isDate(o2)){
                        return o1 !== o2;
                    }
                    if (angular.isDate(o1) || angular.isDate(o2)){
                        return true;
                    }

                    var o1 = acModel.stripDto(o1);
                    var o2 = acModel.stripDto(o2);

                    for (var f in o1){
                        if (f === "$$hashKey"){
                            continue;
                        }

                        if (!angular.isDefined(o2[f])){
                            return true;
                        }
                        else if (angular.isArray(o1[f])){
                            if (testChangesList (o1[f], o2[f]) === true){
                                return true;
                            }
                        }
                        else if (angular.isObject(o1[f])){
                            if (testChangesObject(o1[f], o2[f]) === true){
                                return true;
                            }
                        }
                        else if (o1[f] !== o2[f]){
                            return true;
                        }
                    }
                    return false;
                };


                /**
                 * Toggle visibility of the passed in ID 
                 * @param id
                 */
                scope.toggleVisable = function(id){
                    var w = angular.element("#" + id);
                    
                    if (w.is(':visible')){
                        w.hide();
                    }
                    else{
                        w.show();
                    }
                };

                /**
                 * Set visibility of the passed in ID 
                 * @param id
                 * @param true/false visable
                 */
                scope.setVisable = function(id, visible){
                    var w = angular.element("#" + id);
                    
                    if (!angular.isDefined(visible) || visible === true){
                        w.show();
                    }
                    else{
                        w.hide();
                    }
                };


                /*************************************************
                 * Dates
                 *************************************************/
                
                /**
                 * Datepicker setup
                 * see http://angular-ui.github.io/bootstrap/#/datepicker
                 */
                scope.datepickerDateOptions = {
                    'year-format': "'yy'",
                    'starting-day': 1
                };
                scope.datepickerShowButtonBar = false;
                scope.datepickerFormat = acGlobal.globals().dateFormat;
                scope.dateFormatDto    = acGlobal.globals().dateFormatDto;
                
                
                /**
                 * Format a date string within an element (eg may be manually entered).<p>
                 * Assumes format as dd mm yy or dd mm yyyy (spaces can be delimited with anything)
                 * @param date field id
                 * @return date object
                 */
                scope.formatDateForElementId = function (id){
                    var el = document.getElementById(id);
                    if (el !== null && el.value !== null && el.value.length > 0){
                        return scope.formatDate(el.value);    
                    }
                    return null;
                };

                /**
                 * Format a date string.<p>
                 * @param date string
                 * @return date object
                 */
                scope.formatDate = function (string){
                    if (acGlobal.globals().dateFormatMonth === 3){
                        return formatDate3(string);    
                    }
                    else{
                        return formatDate2(string);    
                    }
                };
                
                /**
                 * Format a date string.<p>
                 * Assumes format as dd mm yy or dd mm yyyy (spaces can be delimited with anything)
                 * @param date string
                 * @return date object
                 * Thanks to http://stackoverflow.com/questions/1576753/parse-datetime-string-in-javascript
                 * Thanks to http://stackoverflow.com/questions/1353684/detecting-an-invalid-date-date-instance-in-javascript
                 */
                var formatDate2 = function (string){
                    try{
                        var day   = parseInt(string.substring(0,2));
                        var month = parseInt(string.substring(3,5)); //info: months are zero based
                        var year  = parseInt(string.substring(6));

                        if (string.length === 8){
                            year += 2000;
                        }

                        var d = new Date(year, month - 1, day);

                        if (testNaN(d.getTime())){
                            d = formatDate3(string);
                        }

                        return d;

                    } catch (err){
                        return formatDate3(string);
                    }
                };

                
                /**
                 * Format a date string.<p>
                 * Assumes format as dd mmm yy or dd mmm yyyy (spaces can be delimited with anything)
                 * @param date string
                 * @return date object
                 */
                var formatDate3 = function (string){
                    try{
                        var day   = parseInt(string.substring(0,2));
                        var month = string.substring(3,6); //text month
                        var year  = parseInt(string.substring(7));

                        if (string.length === 9){
                            year += 2000;
                        }

                        //find month
                        var monthx = null;
                        for (var i=0;i<acGlobal.globals().monthsShort.length;i++){
                            var m = acGlobal.globals().monthsShort[i];
                            if (m === month){
                                monthx = i;
                                break;
                            }
                        }

                        
                        //find month from 'en'
                        if (monthx === null){
                            for (var i=0;i<acGlobal.globals().monthsShortDefault.length;i++){
                                var m = acGlobal.globals().monthsShortDefault[i];
                                if (m === month){
                                    monthx = i;
                                    break;
                                }
                            }
                        }

                        return new Date(year, monthx, day);

                    } catch (err){
                        return null;
                    }
                };

                

                /**
                 * Generic date picker control fields. 
                 */            
                scope.genericDatePicker    = null;
                scope.genericDatePicker_id = null;

                /**
                 * Generic date picker. 
                 * Used for multiple rows / fields that require a date picker. This improves the effiency of page loading (ie only need one date picker).
                 * @param element id of record/field to use date picker for
                 * @param element id of date picker object
                 */
                scope._odp = function(id, datepicker_id, offset_top, offset_left){
                    
                    $scope.datepicker   = $scope.formatDateForElementId(id);
                    $scope.datepickerId = id;
                    
                    var x = document.getElementById(id);
                    //var coords = getOffset(event);

                    //Thanks to http://stackoverflow.com/questions/442404/retrieve-the-position-x-y-of-an-html-element
                    var elemRect = x.getBoundingClientRect();
                    var top  = elemRect.top; 
                    var left = elemRect.left;
                    
                    if (angular.isDefined(offset_top)){
                        top += offset_top;
                    }
                    if (angular.isDefined(offset_left)){
                        left += offset_left;
                    }
                    
                    $timeout(function() {
                        var el = document.getElementById(datepicker_id);
                        
                        //reposition datepicker
                        var d = $("#" + datepicker_id);
                        d.css({
                            position: "absolute",
                            top: top,
                            left: left,
                        });
                        
                        el.value = x.value;
                        el.focus();                
                    }, 50);
                };
                
                
                /**
                 * Update field after generic date picker selection
                 */
                $scope.changeGenDatePicker = function(datepicker_id){
                    $timeout(function() {
                        var x = document.getElementById(datepicker_id);
                        var el = document.getElementById($scope.datepickerId);
                        el.value = x.value;
                    }, 50);
                };
                
                
                /*************************************************
                 * Scroll control
                 *************************************************/
                
                /**
                 * Set scroll to keep at top
                 * Thanks to http://stackoverflow.com/questions/1216114/how-can-i-make-a-div-stick-to-the-top-of-the-screen-once-its-been-scrolled-to
                 * 
                 * NOTE: 50px is add to allow for bootstrap-3.1.1.css: .navbar{ min-height: 50px;}
                 */
                scope.moveScroller = function(field, marginTop) {
                    var x = 0;
                    
                    if (angular.isDefined(marginTop)){
                        x = marginTop;
                    }
                    else if (field.indexOf('header') !== -1){
                        x = 50;
                    }
                    
                    var move = function() {
                        try{
                            var st = $(window).scrollTop();
                            var ot = $("#scroller-anchor-" + field).offset().top;
                            var s = $("#scroller-" + field);
                            var top = '' + (st - ot + x) + 'px';
                            if(st > ot) {
                                s.css({
                                    position: "relative",
                                    top: top
                                });
                            } else {
                                if(st <= ot) {
                                    s.css({
                                        position: "relative",
                                        top: ""
                                    });
                                }
                            }
                        } catch(err){}
                    };
                    $(window).scroll(move);
                    move();
                    return move;
                };

                /**
                 * Locking columns
                 * Thanks to http://stackoverflow.com/questions/8327093/position-a-div-fixed-vertically-and-absolute-horizontally-within-a-position
                 *           http://stackoverflow.com/questions/4306387/jquery-add-and-remove-window-scrollfunction
                 */
                scope.lockLeftScroller = function(field) {
                    $(window).scroll(function(event) {
                       $(field).css("margin-left", $(document).scrollLeft());
                    });
                };
                
                
                /*************************************************
                 * Main Table Sort
                 *************************************************/
                if (!conf.sort_initialised){
                    
                    if (conf.sort_predicate_field !== ''){
                        scope.sort_predicate = scope.model.getFieldParameter (conf.sort_predicate_field, 'dto'); 
                    }
                    else{
                        scope.sort_predicate = '';
                    }

                    scope.sort_reverse    = conf.sort_reverse;
                    conf.sort_predicate   = scope.sort_predicate;
                    conf.sort_initialised = true;
                }
                else{
                    scope.sort_reverse   = conf.sort_reverse;
                    scope.sort_predicate = conf.sort_predicate;
                }
                
                scope.sortColumn = function(predicate) {
                    if (!angular.isDefined(predicate) || predicate === null) {
                        //do nothing
                    }
                    else{
                        if (scope.sort_predicate === predicate) {
                            scope.sort_reverse = !scope.sort_reverse;
                        }
                        else{
                            scope.sort_predicate = predicate;
                            scope.sort_reverse   = false;
                        }
                    }
                    conf.sort_predicate = scope.sort_predicate;
                    conf.sort_reverse   = scope.sort_reverse; 
                };

                scope.sortRow = function(rec) {
                        
                    if (rec.getId() < 0){
                        if (scope.sort_reverse){
                            return rec.getId() * -1;    
                        }
                        return rec.getId();
                    }
                    return rec[$scope.sort_predicate];
                };


                /*************************************************
                 * Lookup
                 *************************************************/
                scope.ADVANCE_LOOKUP = '***'; // also defined in com.progenso.desma.app.entities.sql.SqlI
                
                /**
                 * Can be over-writen by calling controller to add in extra parameters
                 */
                scope.lookupParamaters = function(p){};

                scope.setLookup = function(url, sql, limit) {
                    createLookup($rootScope, scope, conf, $state, $timeout, acDefinition, url, sql, limit);
                };

                //reinitialise state with new lookup / search results
                scope.reinitialise = function(sql, delay){
                    scope.destroy();
                    $state.go('clearPage');
                    
                    if (!angular.isDefined(delay)){
                        delay = 100;
                    }
                    
                    if (angular.isDefined(sql)){
                        sql.setLookup(scope.lookupSelect);
                        sql.setOffset(0);
                        sql.setCount(null);
                    }
                    
                    $timeout(function() {
                        if (angular.isDefined(sql)){
                            $rootScope.pgConfig[conf.modelDef.state].p = {sql: sql.model.json(sql)};
                        }
                        $state.go(conf.modelDef.state);
                        
                        if (angular.isDefined(sql)){
                            $rootScope.pgConfig[conf.modelDef.state].p = null;
                        }
                    }, delay);
                };

                

                /*************************************************
                 * Form Initialisation
                 *************************************************/

                //Is this form currently being edited?
                if (conf.list !== null){
                    scope.list = conf.list; 
                    if (conf.listOrg !== null){
                        scope.listOrg = conf.listOrg;
                    }
                }
                //Otherwise use cached list. If this does not exist then use create and configure an empty list. 
                else if (conf.isCachedObjectList === true){
                    var list = scope.listOrg;
                    
                    //create list if not exist
                    if (list === null){
                        list = [];
                        scope.model.configureObject(list);
                    }
                    
                    scope.initList(list);
                }


                
                /*************************************************
                 * Load the next offset / limit
                 *************************************************/
                scope.loadNext = function(recordsToLoad){
                    
                    //Test invalid paramters
                    if (scope.sqlObj === null 
                        || scope.sqlObj.getLimit() === null 
                        || scope.sqlObj.getCount() === null ){
                        return;
                    }

                    var offset = scope.sqlObj.getOffset() !== null? scope.sqlObj.getOffset() : 0;

                    //Test all records loaded
                    if ((scope.sqlObj.getLimit() + offset) >= scope.sqlObj.getCount()){
                        return;
                    }          

                    scope.sqlObj.setOffset(scope.sqlObj.getLimit() + offset);
                    var limit = scope.sqlObj.getLimit();

                    if (angular.isDefined(recordsToLoad) && recordsToLoad > limit * 2 + offset){
                        scope.sqlObj.setLimit(recordsToLoad);
                    }

                    conf.modelDef.remote.queryForce({sql: scope.sqlObj.model.json(scope.sqlObj)},
                            function(result){
                                var z = result.getObject();
                                for (var k=0; k<z.length; k++){
                                    scope.list.push(z[k]);  
                                }
                                scope.sqlObj = z._sql;
                                scope.sqlObj.setLimit(limit);
                                if (conf.showPageSelection){
                                    scope.page.setPageNumbers();
                                }
                            },
                            function(result) { //error callback
                                displayError(result);
                            });
                };

                
                /*************************************************
                 * Defaults
                 *************************************************/
                scope.temp_prefix     = acGlobal.globals().temp_prefix;
                scope.remoteUrlPrefix = acGlobal.globals().remoteUrlPrefix;
                scope.http            = $http;
                
                
                /*************************************************
                 * Page Selection
                 * This MUST come after scope.list is defined
                 *************************************************/
                if (conf.showPageSelection){
                    if (!conf.reloading){
                        scope.page = createPageObject(scope, conf, $timeout);
                        setPageScopeFunctions (scope, scope.page, conf, $state, acDialogs);
                        conf.pageObject = scope.page; 
                    }
                    else{
                        scope.page = conf.pageObject;
                        setPageScopeFunctions (scope, scope.page, conf, $state, acDialogs);
                    }
                }
                

                /*************************************************
                 * Convenience functions
                 *************************************************/
                scope.stateGo = function(url){
                    $state.go(url);
                };
                
                scope.addLoading = function(){
                    acGlobal.addLoading();
                };
                
                scope.addLoadingNoReg = function(){
                    acGlobal.addLoadingNoReg();
                };
                
                scope.removeLoading = function(){
                    acGlobal.removeLoading();
                };

                
                //This MUST BE the last action
                conf.reloading = false;
                
            }, //configure



            /*************************************************
             * Send heart beats to keep session alive
             *************************************************/
            heartbeat: function ($scope) {
                if (acGlobal.globals().heartbeatTimer !== null) {
                    $scope.heartbeat = function(){
                        $timeout(function() {
                            acHeartbeatRemote.queryForce();
                            $scope.heartbeat();
                        }, acGlobal.globals().heartbeatTimer);
                    };
                    $scope.heartbeat();
                }
            }, 
            
            
            /*************************************************
             * Time out session
             * Thanks to http://www.javaworld.com/article/2073234/tracking-session-expiration-in-browser.html
             *************************************************/
            sessionValid: function ($scope) {
                if (acGlobal.globals().sessionTimeoutTimer !== null) {
                    
                    var serverTime = ipCookie('serverTime');
                    serverTime = serverTime==null ? null : Math.abs(serverTime);
                    var clientTimeOffset = (new Date()).getTime() - serverTime;
                    ipCookie('clientTimeOffset', clientTimeOffset, { path: '/' });
                    
                    $scope.checkSession = function(){
                        $timeout(function() {
                            var sessionExpiry = Math.abs(ipCookie('sessionExpiry'));
                            var timeOffset = Math.abs(ipCookie('clientTimeOffset'));
                            var localTime = (new Date()).getTime();
                            localTime = localTime - timeOffset; 
                            
                            if (localTime > (sessionExpiry+15000)) { // 15 extra seconds to make sure
                                //acDialogs.timeout();
                                $rootScope.relogin = false;
                                $window.location.href = acGlobal.globals().indexPage;
                            } else {
                                $scope.checkSession();
                            }
                            
                        }, acGlobal.globals().sessionTimeoutTimer);
                    };
                    $scope.checkSession();
                }
            }
            
        };
        

        /**
         * Return a matching column class based on:<ol>
         *     <li>acModel.APP_TYPE (if exists)</li>
         *     <li>acModel.TYPE</li>
         * </ol>
         * The returned type is generic (defined in table.less).
         */ 
        var columnClassFn = function(field, model){
            var colClass = null;
            if (!angular.isDefined(model) || model === null){
                model = $scope.model;
            }
            var appType = model.getFieldParameter (field, 'appType');

            if (appType !== null){
                switch(appType){
                    case acModel.APP_TYPE_POS_NR:
                    case acModel.APP_TYPE_ENTITY_NR:
                    case acModel.APP_TYPE_SORT_NR:
                        colClass = 'div-table-pos';
                        break;
                    case acModel.APP_TYPE_ID: 
                    case acModel.APP_TYPE_REF_ID:
                        colClass = 'div-table-id';
                        break;
                    case acModel.APP_TYPE_REF_CODE:
                    case acModel.APP_TYPE_LOOKUP_REF:
                    case acModel.APP_TYPE_KEY:
                        colClass = 'div-table-code';
                        break;
                    case acModel.APP_TYPE_DESCR:    
                    case acModel.APP_TYPE_LOOKUP_VALUE:
                            colClass = 'div-table-desc';
                        break;
                    case acModel.APP_TYPE_PERCENTAGE:
                        colClass = 'div-table-perc';
                        break;
                    case acModel.APP_TYPE_CURRENCY:
                        //use double
                }
            }

            if (colClass === null){
                var type = model.getFieldParameter (field, 'type');
                switch(type){
                    case acModel.TYPE_INTEGER:
                        colClass = 'div-table-int';
                        break;
                    case acModel.TYPE_LONG:
                        colClass = 'div-table-long';
                        break;
                    case acModel.TYPE_DOUBLE:
                        colClass = 'div-table-doub';
                        break;
                    case acModel.TYPE_BOOLEAN:
                        colClass = 'div-table-bool';
                        break;
                    case acModel.TYPE_DATE:
                        colClass = 'div-table-date';
                        break;
                    case acModel.TYPE_STRING:
                        //use default
                }
            }
                
            //default, i.e. string
            if (colClass === null){
                colClass = 'div-table-str';
            }

            return colClass;
        };


        
        return self;
    })


    .controller('exceptionCtrl', [
             '$rootScope',
             '$scope', 
             'acController', 
             'mdataModel',

             function($rootScope, $scope, acController, mdataModel){  
                 
                var config = acController.createConfig($scope);
                acController.configure($scope, config);
                $scope.setMainTitle('Exception');       
             }
     ]);
    
 

    /**
     * Standard Lookup function
     * 
     * Notes:<ul>
     *    <li>Designed to be used with 'lookup' in 'sidebar_actions.html'.</li>
     *    <li>An <code>sql</code> object is required</li>
     * </ul>
     */
    var createLookup = function($rootScope, scope, conf, $state, $timeout, acDefinition, url, sql, limit){
        
        scope.lookupSelect = null;


        var p = {};
        if (angular.isDefined(limit)){
            p.limit = limit;
        }

        /**
         * Used by sidebar_actions.html typeahead
         */
        scope.lookup = function(val) {
            p.code = val;
            
            //Can be over-writen by calling controller to add in extra parameters
            scope.lookupParamaters(p);

            return scope.http.get(scope.remoteUrlPrefix + url, {
              params: p
            }).then(function(res){
              var list = [];
              for (var i=0; i<res.data.o.length; i++){
                  list.push(res.data.o[i]);
              }
              return list;
            });
        };
        
        /**
         * Used by sidebar_actions.html lookupAction button.
         */
        scope.lookupAction = function(){
            if (angular.isObject(sql)){
                scope.reinitialise(sql);
            }
            else{
                acDefinition.loadSql(sql, function(sql){
                    scope.reinitialise(sql);
                })();
            }
        };
    };


    /**
     * Page object will control previous, next and page selection functions.
     * This function works in combination with template/app/page_navigation.html
     *
     * Notes:<ul>
     *    <li>A number of base functions (eg recordNew,loadNext,postSuccess) are overriden to all better control of page displays.</li>
     *    <li>Sorting must be disabled for the controller</li>
     *    <li>Updated records are copied back to the list and listOrg</li>
     *    <li>New and Deleted tigger a reload of the current page</li>
     * </ul>  
     *
     * Thanks to http://stackoverflow.com/questions/4228356/integer-division-in-javascript
     */
    var createPageObject = function(scope, conf, $timeout){
        var page = {};


        //Page index object
        page.addPage = function(index, records){
            return {i: index, r: records};
        };


        page.getIndex = function (index){
            try{
                return this.pageIndexes[index].i;    
            } catch (err){
                return -1;
            }
        };
        
        page.getRecords = function (index){
            try{
                return this.pageIndexes[index].r;
            } catch (err){
                return '';
            }
        };

        
        //Dynamically set page numbers to show
        page.setPageNumbers = function(){
            var start = (this.current+1) - this.plusPageSel;
            if (start < 1){
                start = 1;
            }            
    
            var end = start + this.maxPageSel;
            if (end > this.totalPages + 1){
                end = this.totalPages + 1;
            }
    
            if (end - start < this.maxPageSel){
                start = end - this.maxPageSel;
                if (start < 1){
                    start = 1;
                }
            }
    
            this.pages = [];
            for (var i=start; i<end; i++){
                this.pages.push(i);
            }    
        };
        
    
        //Page has changed, so reset properties
        page.configPage = function(){
    
            //Test if records need to be loaded
            if (this.sqlObj !== null){
                if (this.pageIndexes[this.current].i == -1){

                    //calculate offset
                    var offset = this.current * this.recsPerPage;
                    this.sqlObj.setOffset(offset);
                    scope.loadNext(this);
                }
            }
    
            var p = this;
            $timeout(function() {
                var el = angular.element('.nav-pagecounter');
                el.removeClass('nav-pagenumber-sel');
                el.addClass('nav-pagenumber');
    
                var id = '#page_number_'+  (p.current + 1);
                el = angular.element(id);
                el.removeClass('nav-pagenumber');
                el.addClass('nav-pagenumber-sel');
            },50);
        };
        
    
        page.isPrevious = function(){return this.current > 0;};
        page.isPage1Visible = function(){
            for (var i=0;i<page.pages.length;i++){
                if (page.pages[i] === 1){
                    return true;
                }
            }
            return false;
        };
        page.previous = function(){
            if (this.isPrevious()){
                this.current=this.current-1;
                this.configPage();
                this.setPageNumbers();
            }
        };
     
        page.isNext = function(){return this.current < this.totalPages - 1;};
        page.isLastPageVisible = function(){
            for (var i=0;i<page.pages.length;i++){
                if (page.pages[i] === this.totalPages){
                    return true;
                }
            }
            return false;
        };
        page.next = function(){
            if (this.isNext()){
                this.current=this.current+1;
                this.configPage();
                this.setPageNumbers();
            }
        };
     
        page.select = function(p){
            this.current=p;
            this.configPage();
            this.setPageNumbers();
        };
    

             
        //Setup page controls
        page.initialise = function (sqlObj, pagenr){
            
            this.sqlObj = sqlObj;
            this.recsPerPage = conf.maxRecordsPerPage;

            if (this.sqlObj !== null && this.sqlObj.getLimit() === null){
                this.sqlObj.setLimit(conf.maxRecordsPerPage);
            }
            if (this.sqlObj !== null && this.sqlObj.getLimit() !== null && this.sqlObj.getLimit() < this.recsPerPage){
                this.recsPerPage = this.sqlObj.getLimit();
            }

            this.totalRecs   = this.sqlObj === null || this.sqlObj.getCount() === null? scope.list.length : this.sqlObj.getCount();
            
            this.totalPages  = Math.ceil(this.totalRecs/this.recsPerPage);
        
            this.current     = pagenr;
            this.show        = this.totalPages > 1; 
            this.pages       = [];
            this.pageIndexes = [];

            //Initialise the index for each page
            var cc = this.totalRecs;
            for (var i=0; i<this.totalPages; i++){
                var index = i * this.recsPerPage;

                if (index >= scope.list.length){
                    index = -1;
                }
                var tt = this.recsPerPage;
                if (i == this.totalPages - 1){
                    tt = cc;
                }

                this.pageIndexes[i] = page.addPage(index, tt);
                cc -= tt;
            }

            this.maxPageSel  = conf.maxPageSel;
            if (this.maxPageSel > this.totalPages){
                this.maxPageSel = this.totalPages;
            }
            
            this.plusPageSel = Math.ceil(this.maxPageSel / 2);
            if (this.plusPageSel < 0){
                this.plusPageSel = 0;
            }

            this.setPageNumbers();
            if (this.totalRecs > 0){
                this.configPage();
            }

        };
        page.initialise(scope.sqlObj, 0);

        return page;
    
    };



    /**
     * Set page scope functions, ie these override the default scope functions
     */
    var setPageScopeFunctions = function(scope, page, conf, $state, acDialogs){

        /**
         * Override
         * Add new record. New records are added into the current page. They stay here unless
         * a later page is added with the same id. In this case the original DTO is removed.
         */
        scope.recordNew = function(){
            scope.recordEdit();
            var setEdit = false;
            if (!conf.editmode){
                setEdit = true;
            }
            else{
                scope.recordEdit();
            }
             
           
            var dto = scope.model.createDto();
             
             if (page.pageIndexes.length === 0){
                 page.pageIndexes[0] = page.addPage(0,0);
             }

             var index = page.pageIndexes[page.current].i;
             page.pageIndexes[page.current].r++;
             for (var i=page.current+1; i<page.pageIndexes.length; i++){
                 page.pageIndexes[i].i++;
             }

             scope.list.splice(index, 0, dto); 
             page.totalRecs++;

             scope.testShowWarning (dto);
             scope.testShowChanges();

             if (setEdit){
                scope.recordEdit();
             }

             return dto;
        };


       /**
        * Override
        */
        scope.recordUndo = function(){
            acDialogs.yesNo('Undo', 'ResetChanges', function(){
                scope.initList(scope.listOrg);
                page.initialise(page.sqlObj, page.current);
            });
        };


       /**
        * Override
        */
        scope.getListCount = function(){
            if (scope.list === null){
                return 0;
            }

            return page.getRecords(page.current) + ' (' + page.totalRecs + ')';
        };

        

       /**
        * Implementing controller can override <b>this</b> method to get access to the result object
        * load some more records
        */
        scope.loadNextCallback = function (result){
        };
        
       /**
        * Override
        * load some more records
        */
        scope.loadNext = function(page){

            conf.modelDef.remote.queryForce({sql: page.sqlObj.model.json(page.sqlObj)},
                    function(result){
                        var index = scope.list.length;

                        var z = result.getObject();
                        var o = [];
                        for (var k=0; k<z.length; k++){
                            scope.list.push(z[k]);
                            o.push(z[k]);
                        }
                        
                        if (conf.listOrg === null){
                            conf.listOrg = scope.listOrg;
                        }

                        //Need to update original list
                        o = scope.listCopy(o);
                        for (var k=0; k<o.length; k++){
                            scope.listOrg.push(o[k]);
                        }

                        //Initialise the index for each newly loaded page
                        var pagesLoaded = Math.ceil(z.length/page.recsPerPage);
                        
                        var x = z.length;
                        var recs = x > page.recsPerPage? page.recsPerPage : x;
                        for (var i=0; i<pagesLoaded; i++){
                            page.pageIndexes[page.current + i] = page.addPage(index, recs);
                            index = index + recs;
                            x -= recs;
                            recs = x > page.recsPerPage? page.recsPerPage : x;
                        }

                        page.setPageNumbers();
                        
                        scope.loadNextCallback(result);
                    },
                    function(result) { //error callback
                        displayError(result);
                    });
        };


      /**
        * Override
        * Successful handling from call to <code>POST</code><p>
        * 
        * The expected return object will contain a list of <code>ReturnIdDto</code> for new records. 
        */                
        scope.postSuccess = function(result){
            
            var rtnList = result.getObject();
            var id_field = scope.model.getFieldParameter ('Id', 'dto'); 
            
            var reload = false;
            for (var i=0; i<scope.list.length; i++){
                var rec = scope.list[i];

                //List size changed, reload
                if (rec.isNew()){
                    
                    //find new id and replace
                    for (var j=0; j<rtnList.length; j++){
                        
                        //Future TODO: trnList may contain full dto, so then replace
                        
                        if (rec.getId() === rtnList[j].id_dto){
                            rec[id_field] = rtnList[j].id;
                            break;
                        }
                    }
                    
                    if (rec.isNew()){
                        reload = true;
                        break;
                    }
                }

                //List size changed, reload
                if (rec.isDelete()){
                    scope.list.splice(i,1);
                    i = scope.list.length > 0? i-1 : 0;
                    page.totalRecs--;
                }

            }

            //This is a work around to reload the page as well as the controller
            //see https://github.com/angular-ui/ui-router/issues/582
            if (reload){
                scope.destroy();
                $state.go('clearPage');
                $state.go('.', null, {reload: true});
            }
            else{
                scope.listOrg = scope.listCopy(scope.list);
            }

        };


    
    };

    
    
;