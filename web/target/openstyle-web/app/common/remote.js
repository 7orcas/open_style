'use strict';


angular.module('app.common.remote', ['ngResource']) //, 'app.common.dialogs'

    /**
     * @module acRemote
     * @version app.common.remote
     * 
     * @description
     * Base remote server call. All <code>service.factory()</code> methods utilize <b>this</b> file. It
     * contains standard methods for calling the server, basic <code>DTO</code> configuration and basic error handling.
     * 
     * 
     * TODO: Implement a default server timeout (needs to be graceful)
     * 
     * [License]
     * @author John Stewart
     */
    .service('acRemote', function($rootScope, $resource, $state, acGlobal, acModel, acDialogs, acCache) { 
        
        
        //Return status (as defined in Java ApplicationI)
        var RETURN_STATUS_UNKNOWN  = acGlobal.globals().RETURN_STATUS_UNKNOWN; //return object is not a ReturnDto
        var RETURN_STATUS_OK       = acGlobal.globals().RETURN_STATUS_OK;
        var RETURN_STATUS_WARNING  = acGlobal.globals().RETURN_STATUS_WARNING;
        var RETURN_STATUS_ERROR    = acGlobal.globals().RETURN_STATUS_ERROR;
        var RETURN_STATUS_MAX_ROWS = acGlobal.globals().RETURN_STATUS_MAX_ROWS;
        var RETURN_STATUS_INVALID  = acGlobal.globals().RETURN_STATUS_INVALID;
        var RETURN_STATUS_EXISTS   = acGlobal.globals().RETURN_STATUS_EXISTS;
        var RETURN_STATUS_NO_PERM  = acGlobal.globals().RETURN_STATUS_NO_PERM;
        
        
        var self = {
            
            /**
             * Configure the server's standard response wrapper <code>ReturnDto</code> object (but not the actual
             * response <code>DTO's</code>.<br>
             * The <code>ReturnDto</code> object contains the following fields:
             * <ul>
             *     <li>response object. This is either the expected <code>DTO(s)</code> from a server call or 
             *        error objects (TODO: Perhaps add error definitions via dto, model, etc)</li>
             *     <li>control object (optional), at this stage this is the search/query <code>DTO</code> that 
             *        this client send to initiate the server call</li>
             *     <li>return status value</li>
             *     <li>return status ID value, a finer gain response from the server, eg RETURN_STATUS_MAX_ROWS 
             *        (max rows for the query have been exceeded)</li>
             *     <li>HTML formated error message in the clients language code</li>
             *     <li>message object containing exception / error or errors per record id (includes id, field name 
             *         and message)</li>
             * </ul>
             *  
             * The above fields are wrapped in <code>getter</code> methods (<code>is</code> methods for booleans).
             * 
             * @private
             * @method
             * @name module:acRemote.configReturn
             * @param {ReturnDto} ReturnDto server's standard response wrapper <code>ReturnDto</code> object
             * @returns {ReturnDto} ReturnDto configured 
             */ 
             configReturn: function (result){
                
                //If returned object is encoded wrapper, the set functions
                if (angular.isDefined(result._s)){
                    result.isReturnDto      = function(){return true;};
                    result.isEncoded        = function(){return this.w;};
                    result.isObject         = function(){return angular.isObject(this.o);};
                    result.getObject        = function(){return this.o;};
                    result.isModel          = function(){return angular.isDefined(this.d) && this.d !== null;};
                    result.getModel         = function(){return this.d;};
                    result.isControl        = function(){return angular.isDefined(this.c) && this.c !== null;};
                    result.getControl       = function(){return this.c;};
                    result.isSql            = function(){return angular.isDefined(this.q) && this.q !== null;};
                    result.getSql           = function(){return this.q;};
                    result.getErrorHtml     = function(){return this._h;};
                    result.getStatus        = function(){return this._s;};
                    result.getStatusId      = function(){return angular.isDefined(this.i)? this.i : 0;};
                }
                else{
                    result.isReturnDto   = function(){return false;};
                    result.getStatus     = function(){return RETURN_STATUS_UNKNOWN;};
                    result.getStatusId   = function(){return 0;};
                }
                
                /**
                 * Call resulted in error from server
                 */
                result.isError = function(){
                    return this.isReturnDto() === true
                               && this.getStatus() !== RETURN_STATUS_OK
                               && this.getStatus() !== RETURN_STATUS_WARNING;
                };
                
                /**
                 * Call resulted in warning from server, however normal processing will continue
                 */
                result.isWarning = function(){
                    return this.isReturnDto() === true
                               && this.getStatus() === RETURN_STATUS_WARNING;
                };
                
                result.isOk = function(){
                    return this.isReturnDto() === true
                               && this.getStatus() === RETURN_STATUS_OK;
                };
                
                /**
                 * Configure returned message object
                 */
                if (angular.isDefined(result.m)){
                    var message = result.m;
                    message.getMessage = function (){return this.m};

                    result.isMessage = function(){return true;};
                    result.getMessageObject = function(){return this.m;};
                    result.getMessage = function(){return this.m.m;};
                }
                else{
                    result.isMessage = function(){return false;};
                }
                
                
                /**
                 * In errors, then configure validation messages
                 */
                if (result.getStatusId() === RETURN_STATUS_INVALID 
                    && result.getObject() !== null){

                    for (var i=0; i<result.getObject().length; i++){
                        var m = result.getObject()[i];
                        m.getFieldname = function(){return this.f;};
                        m.getMessage   = function(){return this.m;};
                        m.getId        = function(){return this.id;};
                    }
                    
                    result.isMessageById = function(){return true};
                    
                    //Return list of error messages for an id (empty list === no messages)
                    result.getMessagesById = function(id){
                        var list = [];
                        for (var i=0; id !== null && i<result.getObject().length; i++){
                            var m = result.getObject()[i];
                            if (m.getId() === id){
                                list.push(m);
                            }
                        }
                        return list;
                    };
                    
                }
                else{
                    result.isMessageById = function(){return false;};
                }
                
                
            },
            
            
            /**
             * Set <b>this</b> remote to always call the server (if parameter is <code>true</code>).<br>
             * If false, then once called the method returns without calling the server. The system assumes that
             * the a previous call result was cached and is still valid.<p>
             * 
             * The default value is true.
             * 
             * @name module:acRemote.setAlwaysUseQueryForce
             * @method
             * @param {boolean} value true = force sever call 
             */
            setAlwaysUseQueryForce: function(value) {
                this.alwaysUseQueryForce = value;
            },
            
            /**
             * Default server call parameters. Can be overwritten by the implementing method.<br>
             * 
             * @private
             * @name module:acRemote.defaultParams
             * @method
             * @returns {Class} default parameters 
             */
            defaultParams: function() {
                var parms = {};
                parms.cn = acGlobal.globals().company;
                return parms;
            },
            
            /**
             * Combine passed in parameters with default parameters. Note the passed in parameters
             * have priority over default parameters.
             * 
             * @private
             * @method
             * @name module:acRemote.combineParams
             * @param {class} params parameters from calling method.
             * @returns {Class} combined parameters
             */
            combineParams: function (params){
                
                var paramsX = {};
                
                //1. Use passed in params 
                if (angular.isDefined(params) && params !== null){
                    for (var p in params){
                        if (!angular.isFunction(params[p])){
                            paramsX[p] = params[p]; 
                        }
                    }
                }
                
                //2. Use default params 
                var defaultParams = self.defaultParams();
                for (var p in defaultParams){
                    if (!angular.isFunction(defaultParams[p]) && !angular.isDefined(paramsX[p])){
                        paramsX[p] = defaultParams[p];  
                    }
                }
                
                
                //3. Use default params for specific initialisations
                if (angular.isDefined(defaultParams.init)){
                    defaultParams.init(paramsX);
                }
                
                return paramsX;
            },
            
            
            /**
             * Create a GET and POST resource. Implementing function can override the <code>processGet</code> function 
             * (this is a call back following a successful GET response).<p>
             * 
             * The GET method is accessed via:<ul>
             *     <li><code>query()</code>, or</li>
             *     <li><code>queryForce()</code></li>
             * </ul><p>
             * 
             * The default method is <code>queryForce()</code> (ie call to <code>query()</code> will use <code>queryForce()</code>. 
             * This will always result in a call to the server. The <code>query()</code> method is used for one time calls (eg
             * master data that doesn't change). Use the <code>setAlwaysUseQueryForce(false)</code> method to set this behavior.<p>
             *  
             * The POST method is accessed <code>post()</code>.<p> 
             * 
             * Design notes:<ul>
             *     <li>The GET and POST methods are tightly coupled with the server's standard response wrapper <code>ReturnDto</code> object.
             *         Please refer to this Java object for details.</li> 
             *     <li>Standard error handling is coded within this method. The calling method can provide a error callback method to override
             *         the standard behavior.</li>
             *     <li>Both GET and POST method calls use <code>acGlobal.addLoading()</code> and <code>removeLoading()</code> to
             *         give visual clues</li>
             *     <li>The GET method return object(s) is configured (fields only) via the its <code>Model</code></li>
             *            
             * </ul><p>
             *   
             * TODO: If model is undefined then request server for definition. So need to pass in DTO name and check cache if model exists.
             *  
             * @method  
             * @name module:acRemote.createRemote
             * @param {Class} model config
             * @returns {Class} remote object for calling server
             */
            createRemote: function(modelConfig){
                
                //set parameters in call to assignable
                var params = self.defaultParams();
                var paramsCall = {};
                for (var p in params){
                    if (!angular.isFunction(params[p])){
                        paramsCall[p] = '@' + p;
                    }
                }
                
                var remote = $resource(acGlobal.globals().remoteUrlPrefix  + modelConfig.url, {}, 
                        {_query: {method:'GET', params: paramsCall, isArray: false},
                         _post:  {method:'POST', params: paramsCall, data: '@data', isArray:false},
                        }); 
                
                modelConfig.remote = remote;
                remote.alwaysUseQueryForce = true;
                remote.cacheList = true;
                remote.standardErrorHandling = true;
                
                //control for actual call to remote
                remote.fired_get = false;
                remote.getUrl = function(){return modelConfig.url;};
                
                //Control to show loading message
                remote.showLoading = true;
                
                remote.removeLoading = function(){
                	if (remote.showLoading){
                		acGlobal.removeLoading();
                    }
                };
                
                
                //add function to force a 'get'
                remote.queryForce = function(params, callback, errorCallback) {
                    remote.fired_get = false;
                    this.query (params, callback, errorCallback);
                };
                
                
                /**
                 * GET method.<p>
                 * 
                 * Used for:<ul>
                 *     <li><code>query()</code>, or</li>
                 *     <li><code>queryForce()</code></li>
                 * </ul><p>
                 * 
                 * <b>This</b> method returns a function. The value of the function indicates the current status of <b>this</b>
                 * method. Return values are:<ul>
                 *     <li>initial value is false, i.e. method is waiting response from server.</li>
                 *     <li>value 'fired', i.e. this method is configured to be called only once, and it has already been called.</li>
                 *     <li>value 'neterror' = error has occured whilst calling the server.</li> 
                 *     <li>value 'error' = error has occured whilst processing the return object.</li> 
                 *     <li>value is returned (configured) object from server, i.e. normal flow.</li> 
                 * </ul>
                 * 
                 * In the event of an unknown error then the return is a simple object with status RETURN_STATUS_UNKNOWN.
                 *
                 * Notes:<ul>
                 *    <li>the return method can be used for a quasi promise.</li>
                 *    <li>the calling method's callback functions are not effected by the return function</li>
                 *    <li>the calling method must then decide on appropriate action in the event of an error / exception.</li>
                 * </ul>
                 * 
                 * @method
                 * @name module:acRemote.createRemote>query
                 * @param {class} params parameters from calling method.
                 * @param {function} callback callback method upon a successful server response <code>ReturnDto</code> object.
                 * @param {function} errorCallback callback method upon a status error within the server response <code>ReturnDto</code> object.
                 * @returns {function} remote function call
                 */
                remote.query = function(params, callback, errorCallback) {


                    //Initial value for return function
                    var returnValue = false;
                    
                    var standardErrorHandling = this.standardErrorHandling;
                    
                    //test if only a callback attribute has been given
                    if (angular.isFunction(params) && !angular.isDefined(callback)){
                        callback = params;
                        params = null;
                    }

                    //Method has been configured to be called once, and it has already been called
                    if (!this.alwaysUseQueryForce && this.fired_get){
                        returnValue = 'fired';
                        if (angular.isFunction(callback)){
                            callback();
                        }
                        return;
                    }
                    
                    this.fired_get = true;
                    if (remote.showLoading){
                    	acGlobal.addLoading();
                    }
                    
                    var paramsX = self.combineParams (params);

                    //Does the model need to be loaded?                 
                    if (angular.isDefined(modelConfig.model) && !acCache.isModel(modelConfig.model)){
                        paramsX.model = modelConfig.model;
                    }
                    
                    
                    var result = this._query(paramsX,
                            function (){

                                self.configReturn(result);
                                var model = null;
                                
                                //Configure returned model and put in cache
                                //TODO: refactor to remove 'o' acModel logic
                                if (result.isModel()){
                                    var fieldsX = acModel.configureFields({o:result.getModel()}, modelConfig);
                                    model = acModel.configureModel(fieldsX);
                                    acCache.putModel(modelConfig.model, model);
                                }
                                
                                //Get model for configuration of objects
                                if (model === null && angular.isDefined(modelConfig.model)){
                                    model = acCache.getModel(modelConfig.model);
                                }



                                //If returned object from server is not of type <code>ReturnDto</code>
                                if (result.isReturnDto() === false){
                                    remote.removeLoading();
                                    if (angular.isFunction(callback)){
                                        callback(result);
                                    }
                                    returnValue = result;
                                    return;
                                }
                                
                                
                                //Server returned an error / exception
                                if (result.isOk() !== true){
                                    remote.removeLoading();
                                    if (angular.isFunction(errorCallback)){
                                        errorCallback(result);
                                    }
                                    else if (standardErrorHandling){
                                        acDialogs.error(result.getMessage());
                                    }
                                    returnValue = result;
                                    return;
                                }
                                
                                
                                
                                //If returned object is encoded, then decode 
                                if (result.isObject() && result.isEncoded() === true && model !== null){
                                    decode(result, model);
                                }
                        
                                
                                //Format incoming types according to the model
                                if (model !== null && result.isObject()){
                                    formatTypes(result.getObject(), model);
                                    model.configureObject(result.getObject());
                                }

                                
                                //Add sql object
                                if (result.isObject() && result.isSql()){
                                    var modelSql = acCache.getModel(modelConfig.modelSql);
                                    formatTypes(result.getSql(), modelSql);
                                    result.getObject()._sql = modelSql.configureObject(result.getSql());
                                }
                                
                                //Add control object
                                if (result.isObject() && result.isControl()){
                                    result.getObject()._control = result.getControl();
                                }
                                
                                //Cache objects (if required)
                                if (remote.cacheList
                                        && angular.isDefined(modelConfig.model)){
                                    acCache.put(modelConfig.model, result.getObject());                                  
                                }
                                
                                try{
                                    if (angular.isFunction(remote.processGet)){
                                        remote.processGet (result);
                                    }
                                    remote.removeLoading();
                                }
                                catch (err){
                                    acGlobal.removeLoadingForce();
                                    acGlobal.globals().systemErr = err.message;
                                    returnValue = 'error';
                                    if (angular.isFunction(errorCallback)){
                                        errorCallback(result);
                                    }
                                    return;
                                }
                                
                                if (angular.isFunction(callback)){
                                    callback(result);
                                }

                                returnValue = result;
                            },
                            function (resultX){ //unsuccessful
                                remote.removeLoading();
                                returnValue = 'neterror';

                                //User may have timed out, relogin
                                if (angular.isDefined(resultX) 
                                    && angular.isDefined(resultX.status) 
                                    && resultX.status === 401){
                                    $state.go("relogin");
                                    return;
                                }
                                
                                if (angular.isFunction(errorCallback)){
                                    errorCallback(result);
                                }
                                
                            }
                            
                    );  
                    
                    //Used a basis for 'returnValue' logic 
                    return function(){return returnValue;};
                };
                
                
                /**
                 * POST method.<p>
                 * 
                 * @method
                 * @name module:acRemote.createRemote>post
                 * @param {class} data <code>DTO</code> object
                 * @param {class} params parameters from calling method.
                 * @param {function} callback callback method upon a successful server response <code>ReturnDto</code> object.
                 * @param {function} errorCallback callback method upon a status error within the server response <code>ReturnDto</code> object.
                 * @returns {function} remote function call
                 */
                remote.post = function(data, params, callback, errorCallback) {
            
                    //test if no params attribute has been given
                    if (angular.isFunction(params)){
                        errorCallback = callback; 
                        callback = params;
                        params = null;
                    }
                    
                    if (remote.showLoading){
                    	acGlobal.addLoading();
                    }
                    
                    params = self.combineParams (params);
                    
                    //Only send actual DTO fields
                    var dataX;
                    if (angular.isArray(data)){
                        dataX = [];
                        for (var i = 0; i < data.length; i++){
                            var rec = data[i];
                            dataX.push(acModel.stripDto(rec));   
                        }
                    }
                    else{
                        dataX = acModel.stripDto(data);
                    }
                    
                    
                    var result = this._post(params, dataX,  
                        function (){
                            remote.removeLoading(); //default action to hide loading icon
                    
                            self.configReturn(result);
                            
                            //ReturnDto error or warning
                            if (result.isOk() !== true && angular.isFunction(errorCallback)){
                                errorCallback(result);
                                return;
                            }
                            
                            else if (result.isOk() !== true){
                                acGlobal.globals().systemErr = result.getMessage();
                                return;
                            }
                                
                            //Add sql object
                            if (result.isObject() && result.isSql()){
                                var modelSql = acCache.getModel(modelConfig.modelSql);
                                result.getObject()._sql = modelSql.configureObject(result.getSql());
                            }
                            
                            //Add control object
                            if (result.isObject() && result.isControl()){
                                result.getObject()._control = result.getControl();
                            }
                            
                            if (angular.isFunction(remote.processPut)){
                                remote.processPut(result);
                            }
                            else{
                                var model = acCache.getModel(modelConfig.model);
                                if (model !== null && result.isObject()){
                                    //Format incoming types according to the model
                                    formatTypes(result, model);
                                    model.configureObject(result.getObject());
                                    acCache.put(modelConfig.model, result.getObject());
                                }
                            }
                                
                            if (angular.isDefined(callback)){   
                                callback(result);
                            }
                            
                        },
                        function (result){
                            remote.removeLoading(); //default action to hide loading icon
                            acGlobal.globals().systemErr = 'NoSaveRecord'; //<<< needs language ! TODO
                            
                            //User may have timed out, relogin
                            if (angular.isDefined(result) 
                                && angular.isDefined(result.status) 
                                && result.status === 401){
                                $state.go("relogin");
                                return;
                            }

                            self.configReturn(result);
                            if (angular.isFunction(errorCallback)){
                                errorCallback(result);
                            }
                        });
                };
                
                
                return remote;
            },
            
        };
        
        
        /**
         * Decode an encoded <code>ReturnDto</code> object using its model definitions.<br>
         * @param <code>ReturnDto</code> object
         * @param object model
         */
        var decode = function(result, model){
            if (result.isEncoded() === true){
                decodeX (result, result.getObject(), model);
            }
        };
        
        var decodeX = function(result, array, model){

            for (var i=0; i<model.fieldDefs.length; i++){
                var def = model.fieldDefs[i];


                if (!angular.isDefined(def.encodeField)){
                    continue;
                }

                for (var j=0; j<array.length; j++){
                    var obj = array[j];

                    //recursive call
                    if (def.type === acModel.TYPE_LIST){
                        var m = model.getChildModel(def.field);
                        
                        for (var k=0; k<obj.length; k++){
                            decodeX (result, obj[k], m !== null? m : model);
                        }
                        continue;
                    }

                    if (!angular.isDefined(obj[def.encodeField])){
                        continue;
                    }                    
                    
                    var index = obj[def.encodeField];
                    var value = array[index][def.dto];
                    obj[def.dto] = value;
                }
            }
        };
        
        
        /**
         * Format base types within the <code>ReturnDto</code> object.<br>
         * eg Dates
         * @param data object
         * @param model object
         */
        var formatTypes = function(obj, model){

            if (!angular.isArray(obj)){
                formatTypesX(obj, model);
                return;
            }

            for (var i=0; i<obj.length; i++){
                formatTypesX(obj[i], model);
            }
        };
        
        
        //Format incoming types according model
        var formatTypesX = function(obj, model){
            for (var x in obj){
                
                if (obj[x] === null){
                    continue;
                }
                
                var f = model.dtoFieldDef(x);

                //recusive call
                if (angular.isArray(obj[x])){
                	for (var i = 0; i < obj[x].length; i++) {
                		var z = obj[x][i];
                        
                        if (z === null){
                            //do nothing
                        }
                        else if (angular.isObject(z)){
                            formatTypesX(z, model);         
                        }
                        else{
                            obj[x][i] = formatTypesZ(z, f);         
                        }
                		
                    }
                	continue;
                }
                
                if (f === null){
                    continue;
                }

                //recusive call
                if (f.type === acModel.TYPE_LIST){
                    var m = model.getChildModel(f.field);
                    m =  m !== null? m : model; 
                    
                    for (var i=0; i<obj[x].length; i++){
                        formatTypesX (obj[x][i], m);
                    }
                    continue;
                }

                obj[x] = formatTypesZ(obj[x], f);
            }
            
        };
        
        
        //Format incoming types according to field definition
        var formatTypesZ = function(obj, field){
            if (field === null){
                return obj;
            }
            
            if (field.type === acModel.TYPE_DATE){
            	var o = acGlobal.globals().timezoneOffset;
                return new Date(obj  + o);
            }

            return obj;
        };
        
        
        
        
        return self;
    })
    
    
;   
