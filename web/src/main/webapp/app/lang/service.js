'use strict';

/**
 * @version app.lang.service
 * 
 * @description
 * Services for application 'language' methods.<p>
 * 
 * See main documentation: Language<p>
 * 
 * [License]
 * @author John Stewart
 */
angular.module('app.lang.service', ['app.common.model', 'app.common.remote', 'app.lang.model'])

     
     /**
      * Get language key-value objects for the given in language code (this is a passed in parameter
      * when the server call is made). 
      */
    .factory('aLangRemote', function($rootScope, acRemote, langModel) {
    
        var remote = acRemote.createRemote ({url:'lang'});
        
        remote.processGet = function (result){
            langModel.configureObject(result.getObject());
            $rootScope.langList = result.getObject();
            
//Test if assign lang key as attribute is faster
            /*
			for (var i=0; i<result.length; i++){
				if (!angular.isDefined(result[result[i].getId()])){
					result[result[i].getId()] = result[i].getText(); 
				}
			}
			*/

        };
            
        return remote;
        
    })
    
     /**
      * Clear the server language cache. 
      */
    .factory('aLangClearCacheRemote', function($rootScope, acRemote, langModel) {
    	return acRemote.createRemote ({url:'lang/clearcache'});
    })
    
    
     /**
      * Application language functions
      */
    .service('aLang', function($rootScope, acGlobal, aLangRemote, aLangListener, aLangClearCacheRemote) {
        
        var self = {
                
            /**
             * Load language key-value objects for the passed in language code
             * @param language code
             * @param call back function (called after successful server call)
             */ 
            loadLang: function (langX, callback) {
                aLangRemote.query ({lang: langX}, callback);
            },
            
            /**
             * Clear the server side language cache
             */
            clearcache: function (callback) {
            	aLangClearCacheRemote.query (null, callback);
            },
            
            /**
             * Load 'login' (ie subset) language key-value objects for the passed in language code
             * @param language code
             * @param call back function (called after successful server call)
             */
            loadLangLogin: function (langX, callback) {
                acGlobal.globals().language = langX;
                aLangRemote.queryForce ({lang: langX, set:'Login'}, callback);
            },
            
            /**
             * Load language key-value objects for the passed in language code then update any
             * registered language listeners.<p>
             * 
             * @param language code
             */
            loadLangForce: function (lang) {
                acGlobal.globals().language = lang;
                var count = 0;
              
                var listeners = aLangListener.getListeners();
                for (var key in listeners){
                    if (key.indexOf('_remote_') === 0){
                        count = count + 1;
                        listeners[key].queryForce({lang: lang}, function(){
                                count = count - 1;
                                self.loadLangData(count);
                            });
                    }
                }
            
                aLangRemote.queryForce ({lang: lang}, function (){
                    for (var key in listeners){
                        if (key.indexOf('_key_') === 0){
                            listeners[key]();
                        }
                    }  
                });
            },
        
            /**
             * TODO: Decide if this is useful
             */
            loadLangData: function (count) {
                if (count > 0){
                    return;
                }           
                var listeners = aLangListener.getListeners();
                for (var key in listeners){
                    if (key.indexOf('_data_') === 0){
                        listeners[key]();
                    }
                }
            },
            
            /**
             * Return a value for a language key-value pair for the currently loaded language.<br>
             * If the language value has '%%' place holders, then the calling method can:<ul>
             *    <li>pass in the values in the lang id (delimited with '%'')</li>
             *    <li>pass in the values for the place holders (as extra arguments)</li>
             * </ul><p>
             * 
             * Thanks to http://stackoverflow.com/questions/2141520/javascript-variable-number-of-arguments-to-function (Johan Hoeksma).<br> 
             * 
             * @param language key 
             * @return language value
             */
            label: function(id){
            	
            	//Don't allow numbers id's
            	try{
            		var x = parseInt(id);
            		if (!testNaN(x)){
            			return id;
            		}
            		
            	} catch (err){}
            	
            	if (typeof $rootScope.langList === 'undefined'){
                    return id;
                }

                var values = [];
                if (id.indexOf('%') !== -1){
                    var idx = id.substring(id.indexOf('%') + 1);
                    id = id.substring(0,id.indexOf('%'));

                    var s = idx.split('%');
                    for (var i=0;i<s.length;i++){
                        values.push(s[i]);
                    }
                }

                var text = id;
                
//Test if assign language key as attribute is faster
                /*
    			if (angular.isDefined($rootScope.langList[id])){
    				text = $rootScope.langList[id]; 
    			}
    			else{
    			*/
    				for (var j=0; j<$rootScope.langList.length; j++){
                        var lang = $rootScope.langList[j];
                        
                        if (lang.getId() === id){
                            text = lang.getText();
                             break;
                        }   
                    }
    		    /*
    			}
                */
    				
                var pointer = 1;
                var index = text.indexOf('%%');
                while (index != -1){
                    
                    //Test for passed place holders within the id
                    if (values.length > 0 && values.length >= pointer){
                        text = text.substring(0,index) + values[pointer - 1] + text.substring(index+2, text.length);  
                    }
                    //Test for passed in arguments to match '%%' place holders
                    else if (arguments.length > pointer){
                        text = text.substring(0,index) + arguments[pointer] + text.substring(index+2, text.length);  
                    }
                    else{
                        text = text.substring(0,index) + ' ' + text.substring(index+2, text.length);
                    }
                    
                    pointer += 1;
                    index = text.indexOf('%%');
                }
                
                return text;        
            },
            
            /**
             * TODO: Decide if this function is useful. If so then refactor to use function above so that parameters
             * can be passed into the returned language value. 
             */
            labelConcat: function(ids, concat){
                if (typeof ids === 'undefined'){
                    return '?';
                }
                if (typeof $rootScope.langList === 'undefined'){
                    return ids;
                }
                var l = '';
                for (var i=0; i<ids.length; i++){
                    var id = ids[i];
                    for (var j=0; j<$rootScope.langList.length; j++){
                        var lang = $rootScope.langList[j];
                        
                        if (lang.getId() === id){
                            l = l + ((l.length==0)?'':concat) + lang.getText();
                            break;
                        }   
                    }
                }
                return l;
            },
            
            /**
             * Language functions for controllers.
             * scope = controller scope
             * set = predefined language subset (if passed in). ie used to return a smaller list.
             * TODO: refactor this function to the common controller method.
             */
            addFormFunctions: function ($scope, set) {
                
                /**
                 * Get the defined language definition for the key
                 * id = language key
                 */
                $scope.label = function(id){
                    return self.label(id);
                };
                
                
                /**
                 * Return a concatenated list of language definitions
                 * ids = [] of language keys
                 * concat = symbol to join language definitions (if undefined then a space is used) 
                 */
                $scope.labelConcat = function(ids,concat){
                    return self.labelConcat(ids,concat);
                };
                
                /**
                 * Change language definitions
                 * lang = passed in lang code (eg 'de', 'en'). Note, must be defined code otherwise the default language will be returned.
                 * set = passed in from controller above. Used to return a predefined language definition sub set (ie a cut down list of definitions) 
                 */
                $scope.changeLanguage = function(lang){
                    
                    if (angular.isDefined(set) 
                            && set !== null
                            && set === 'login'){
                        
                        self.loadLangLogin(lang);
                        $scope.selectedLang = lang;
                        return;
                    }
                    
                    self.loadLangForce(lang);
                };
            }, 
            
        };
        
        
        return self;
    })

    
    /**
      * Language listeners
      */
    .service('aLangListener', function() {
        var listeners = {};
        return {
            addRemoteListener: function (remote) {
                    listeners['_remote_' + remote.getUrl()] = remote;
                },
            addListener: function (key, fn) {
                    listeners['_key_' + key] = fn;
                },
            //Listeners that depend on masterdata update first
            addDataListener: function (key, fn) {
                    listeners['_data_' + key] = fn;
                },
            getListeners: function () {
                  return listeners;
                }
            
        };
    })
    
     
    
    

;