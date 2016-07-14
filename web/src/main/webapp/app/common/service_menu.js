'use strict';

/**
 * 
 * @doc module
 * @name app.common.service
 * @description 
 * 
 * <b>This</b> module contains services that are specific to <u>this</u> application's service menu.<p> 
 * 
 * [License]
 * @author John Stewart
 */

angular.module('app.common.service.menu', ['app.common.remote'])

   
    //////////////////////    <!-- service:start -->
    
    
   /**
      * Start timers.<p>
      */
    .factory('acStartTimersRemote', function(acRemote) {
        return acRemote.createRemote ({url:'timer/start'});
    })
    
    /**
      * Cancel timers.<p>
      */
    .factory('acCancelTimersRemote', function(acRemote) {
        return acRemote.createRemote ({url:'timer/cancel'});
    })
    
    

     /**
      * Clear fixes with invalid status.<p>
      */
    .factory('acClearFixesRemote', function(acRemote) {
        return acRemote.createRemote ({url:'simu/clearfixes'});
    })
    
    
    /**
      * Reapply logisoft fix.<p>
      */
    .factory('acReapplyLSFixRemote', function(acRemote) {
        return acRemote.createRemote ({url:'simu/reapplylogisoftfix'});
    })
    
    /**
      * Reload application properities.<p>
      */
    .factory('acReloadAppPropRemote', function(acRemote) {
        return acRemote.createRemote ({url:'appsys/reloadappprop'});
    })
    
    /**
      * Close Postgres SQL connection.<p>
      */
    .factory('acCloseSqlConnection', function(acRemote) {
        return acRemote.createRemote ({url:'appsys/closesqlconnection'});
    })
    
    /**
      * Reset db.<p>
      */
    .factory('acResetDBRemote', function(acRemote) {
        return acRemote.createRemote ({url:'appsys/resetdatabase'});
    })
    
    
    /**
      * Toggle debug flag.<p>
      */
    .factory('acToggleDebugRemote', function(acRemote) {
        return acRemote.createRemote ({url:'appsys/toggledebug'});
    })
    
    /////////////////    <!-- service:end -->
    


;