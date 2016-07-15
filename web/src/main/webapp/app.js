'use strict';

/**
 * @doc model
 * @name appx
 * @description Application js
 * 
 */

//application level module
var App = angular.module('app', [
    
    'ngResource',            //angular-resource-1.2.16.min.js
    //'ngSanitize',          //angular-sanitize-1.2.16.min.js
    'ui.router',             //angular-ui-router-0.2.10.min.js
    'ui.bootstrap',          //ui-bootstrap-tpls-0.11.0.min.js
    'ng-context-menu',       //ng-context-menu.js    
    'ipCookie',              //Session timeout test
    
     //Application modules
    'home.controller',
        
    'app.login.service_init',   
    'app.common.global',
    'app.common.cache',
    'app.common.model',
    'app.common.dialogs',
    'app.common.controller',
    'app.common.remote',
    'app.common.service',
    'app.common.userobject',
    
    'app.lang.model',
    'app.lang.service',
    
    'app.mod.user.service',
    'app.mod.user.controllers',
    'app.mod.user.model',
    
    
    //service:start
    'app.common.service.menu',
    
    'app.mod.langadmin.service',
    'app.mod.langadmin.controllers',
    'app.mod.langadmin.model',
    
    'app.mod.useradmin.service',
    'app.mod.useradmin.controllers',
    'app.mod.useradmin.model',
    
    'app.mod.company.service',
    'app.mod.company.controllers',
    'app.mod.company.model',
    
    'app.mod.patches.service',
    'app.mod.patches.controllers',
    'app.mod.patches.model',
    
    //service:end
    
    
    'mod.mdata.service',
    'mod.mdata.controllers',
    'mod.mdata.model',
    
    'mod.calendar.service',
    'mod.calendar.controllers',
    'mod.calendar.model',
    
    'mod.import.service',
    'mod.import.controllers',
    'mod.import.model',
    
    'mod.rpt.service',
    'mod.rpt.controllers',
    'mod.rpt.model',
    
    'mod.prep.service',
    'mod.prep.controllers',
    'mod.prep.model',
    
    'mod.simu.service',
    'mod.simu.controllers',
    'mod.simu.model',
    
    'mod.period.service',
    'mod.period.controllers',
    'mod.period.model',
    
    'mod.export.service',
    'mod.export.controllers',
    'mod.export.model',
    
    'mod.fix.service',
    'mod.fix.controllers',
    'mod.fix.model'
    
	]
	

);

