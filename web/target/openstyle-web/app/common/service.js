'use strict';

/**
 * @doc module
 * @name app.common.service
 * @description 
 * 
 * <b>This</b> module contains services that are general to <u>this</u> application.<p> 
 * 
 * [License]
 * @author John Stewart
 */

angular.module('app.common.service', ['app.common.remote'])

     
     /**
      * Send a heartbeat to the server to maintain a valid <code>HttpSession</code> session.<p>
      */
    .factory('acHeartbeatRemote', function(acRemote) {
        return acRemote.createRemote ({url:'login/heartbeat'});
    })

    /**
      * Save a 'no repeat' code
      */
    .factory('acNoRepeatRemote', function(acRemote) {
        return acRemote.createRemote ({url:'user/noRepeat'});
    })
    
     /**
      * Retrieve definition object.<p>
      */
    .factory('acDefinitionRemote', function(acRemote) {
        return acRemote.createRemote ({url:'defs'});
    })
    
    
    /**
      * Call server to request a re-login 
      */
    .factory('reloginRemote', function($resource) {
        
        var res = $resource('rest/login', {}, 
                {_get: {method:'GET', 
                        params: {forx:'@forx', 
                                 fgh: '@fgh', 
                                 ts1: '@ts1', 
                                 ts2: '@ts2', 
                                 ts3: '@ts3', 
                                 ts4: '@ts4', 
                                 lan: '@lang',
                                 cha: '@chall', 
                                 res: '@res'
                        },
                        isArray:false}
                });
        
        res.get = function(params, callback) {
            var result = res._get(params, function (){
                callback(result); 
            });
        };
            
        return res;
    })
    
  
    /**
     * Retrieve definition object.<p>
     */
     .service('acDefinition', function($rootScope, acGlobal, acDefinitionRemote, acModel, acCache) {
    
        
        var self = {
                
            loadDef: function (classname, callback) {

                var configDef = function(result){
                     var fields = acModel.configureFields({o:result.getObject()});
                     var model = acModel.configureModel(fields);

                     acCache.putModel(classname, model);

                     if (angular.isDefined(callback)){
                       callback();
                     }

                };

                if (acCache.isModel(classname)){
                    if (angular.isDefined(callback)){
                        callback();
                    }
                }
                else{
                    acDefinitionRemote.query ({classname: classname}, configDef);
                }
            },
       
            //Retrieve and configure sql definition from server     
            loadSql: function (dto, callback){
                
                 var configSql = function(result){
                     var fields = acModel.configureFields({o:result.getObject()});
                     var model = acModel.configureModel(fields);

                     //Override
                     var createDtoX = model.createDto;
                     model.createDto = function(){
                       var sql = createDtoX();
                       if (sql.getOffset() === null){
                           sql.setOffset(0);
                       }
                       return sql;
                     };
                     acCache.putModel(dto, model);

                     if (angular.isDefined(callback)){
                       callback(model.createDto());
                     }

                };

                return acDefinitionRemote.query ({classname: dto}, configSql);
            },

            loadModel: function (classname, callback) {

                var configDef = function(result){
                     var fields = acModel.configureFields({o:result.getObject()});
                     var model = acModel.configureModel(fields);

                     acCache.putModel(classname, model);

                     if (angular.isDefined(callback)){
                       callback();
                     }

                };

                return acDefinitionRemote.query ({classname: classname}, configDef);
            },

        };
    
    
        return self;
    })
    

    
    /**
     * File uploader functionality
     * thanks to http://cgeers.com/2013/05/03/angularjs-file-upload/
     */     
    .factory('uploadManager', function ($rootScope) {
        var _files = [];
        return {
            add: function (file, uploadfile) {
                _files.push(file);
                $rootScope.$broadcast('fileAdded', file.files[0].name, uploadfile);
            },
            clear: function () {
                _files = [];
            },
            files: function () {
                var fileNames = [];
                $.each(_files, function (index, file) {
                    fileNames.push(file.files[0].name);
                });
                return fileNames;
            },
            upload: function () {
                $.each(_files, function (index, file) {
                    file.submit();
                });
                this.clear(); 
            },
            setProgress: function (percentage) {
                $rootScope.$broadcast('uploadProgress', percentage);
                if (percentage === 0){
                    $rootScope.$broadcast('uploadComplete');
                }
            }
        };
    }) 


    

;