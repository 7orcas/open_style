'use strict';

/**
 * @name directives
 * @description 
 * 
 * Company maintenance directives.<p>
 * Allow multiple objects to use the same included template directive.<p> 
 * 
 * Thanks to http://stackoverflow.com/questions/13422966/how-to-specify-model-to-a-nginclude-directive-in-angularjs
 * 
 * [License]
 * @author various
 */


//application level module
var app = angular.module('app');

/**
 * Simulation detail.<p>
 */
app.directive('htmlDetail', function() {
    return {
    	restrict: 'E',
        templateUrl: 'app/mod/company/view/html_detail.html', // markup for template
        scope: {
            type: '=' // allows data to be passed into directive from controller scope
        }
    };
});

/**
 * Simulation cycle detail.<p>
 */
app.directive('htmlCycle', function() {
    return {
    	restrict: 'E',
        templateUrl: 'app/mod/company/view/html_cycle.html', // markup for template
        scope: {
            type: '=' // allows data to be passed into directive from controller scope
        }
    };
});

/**
 * Simulation detail.<p>
 */
app.directive('exportDetail', function() {
    return {
    	restrict: 'E',
        templateUrl: 'app/mod/company/view/export_detail.html', // markup for template
        scope: {
            type: '=' // allows data to be passed into directive from controller scope
        }
    };
});


/**
 * Simulation Mould changes.<p>
 */
app.directive('exportMouldChange', function() {
    return {
    	restrict: 'E',
        templateUrl: 'app/mod/company/view/export_mouldchange.html', 
        scope: {
            type: '=' 
        }
    };
});


/**
 * Simulation Overview.<p>
 */
app.directive('exportOverview', function() {
    return {
    	restrict: 'E',
        templateUrl: 'app/mod/company/view/export_overview.html', 
        scope: {
            type: '=' 
        }
    };
});

/**
 * Simulation fixed detail.<p>
 */
app.directive('exportDetailFix', function() {
    return {
    	restrict: 'E',
        templateUrl: 'app/mod/company/view/export_detail_fix.html',
        scope: {
            type: '=' 
        }
    };
});

/**
 * Simulation Fixed Mould changes.<p>
 */
app.directive('exportMouldChangeFix', function() {
    return {
    	restrict: 'E',
        templateUrl: 'app/mod/company/view/export_mouldchange_fix.html', 
        scope: {
            type: '=' 
        }
    };
});


/**
 * Simulation Fix Overview.<p>
 */
app.directive('exportOverviewFix', function() {
    return {
    	restrict: 'E',
        templateUrl: 'app/mod/company/view/export_overview_fix.html', 
        scope: {
            type: '=' 
        }
    };
});

/**
 * Simulation Fixed Cycles.<p>
 */
app.directive('exportCycleFix', function() {
    return {
    	restrict: 'E',
        templateUrl: 'app/mod/company/view/export_cycle_fix.html', 
        scope: {
            type: '=' 
        }
    };
});

