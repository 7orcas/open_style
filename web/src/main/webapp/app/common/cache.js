'use strict';

/**
 * @doc module
 * @name app.common.cache
 * @description 
 * 
 * Module to manage cached items.<p> 
 * 
 * The design goals of <b>this</b> module are:<ol>
 *     <li>a central repository for storing objects within <b>this</b> application.</li><br>
 *     <li>management of entities that implement the <code>CacheI</code> interface. Ie should the entity/entities become obsolete then 
 *         it is flagged for update.</li><br> 
 * </ol><p>
 * 
 * Design notes:<ul>
 *     <li>objects are <u>time-stamped</u> upon storage. This maybe used to compare server based time-stamps for changes to the object. The
 *         general use case is master data lists. For example, customer groups may have changed on the server after the groups where
 *         stored in <b>this</b> cache, and hence the server time-stamp will be after <b>this</b> modules time-stamp.</li><br>
 *     <li>objects flagged invalid (ie obsolete) are not returned via getter methods.</li><br>
 *     <li>changes originating from <b>this</b> client are updated immediately via the normal service call.</li><br>
 *     <li>specific put/get methods allow use of the same key, eg 'putModel' distinguish different objects for the same entity.</li><br>
 * </ul>
 * 
 * 
 * TODO: IMPLEMENT THIS MODULE
 * - timer
 * - unit test
 * 
 * [License]
 * @author John Stewart
 */
angular.module('app.common.cache', [])


    .service('acCache', function($rootScope, acGlobal) {

    	 /**
         * Remove '.' from keys
         */
        var cacheKey = function (key){
            if (key === null){
            	return null;
            } 
            var i = key.indexOf('.');
            while (i !== -1){
                key = key.substring(0,i) + key.substring(i + 1);
                i = key.indexOf('.');
            }
            return key;
        };
    	
        /**
         * Store model definitions separately
         */
        var modelCacheKey = function (key){
            if (key === null){
            	return null;
            } 
            key = cacheKey(key);
            return key + '_M';
        };
        
        
        var self = {};

       
        /**
         * Cache an object.<br>
         * Sets time-stamp and valid flag
         */
        self.put = function (key, object){
        	key = cacheKey(key);
        	var obj = true;
        	
        	//Must be an object
        	if (!angular.isObject(object)){
                var x = {o: object};
                object = x;
                obj = false;
            }
            self[key]        = object;
            self[key].intime = new Date();
            self[key].valid  = true; //false == object is obsolete
            self[key].obj    = obj;
        };

        /**
         * Invalidate a cached object.<br>
         */
        self.invalidate = function (key){
        	key = cacheKey(key);
            if (angular.isDefined(self[key])){
                self[key].valid = false;
            }
        };
        
        /**
         * Return cached object (if exists in cache and is <b>valid</b>)
         */
        self.get = function (key){
        	key = cacheKey(key);
            if (angular.isDefined(self[key]) && self[key].valid){
            	if (!self[key].obj){
            		return self[key].o;
            	}
            	
                return self[key];
            }
            return null;
        };

        /**
         * Is there a cached non-obsolete object
         * @param cache key
         */
        self.isCached = function (key){
        	if (!angular.isDefined(key) || key === null){
                return false;
            }
        	key = cacheKey(key);
            if (angular.isDefined(self[key])){
                return self[key].valid;
            }
            return false;
        };

        /**
         * Cache a <code>Model</code> object
         */
        self.putModel = function (key, object){
        	self.put(modelCacheKey(key), object);
        };

        /**
         * Return cached <code>Model</code> object (if exists in cache)
         */
        self.getModel = function (key){
        	return self.get(modelCacheKey(key));
        };

        /**
         * Is there a cached  non-obsolete model object
         * @param cache key
         */
        self.isModel = function (key){
        	if (!angular.isDefined(key) || key === null){
                return false;
            }
        	return self.isCached(modelCacheKey(key));
        };

        return self;
    })

;