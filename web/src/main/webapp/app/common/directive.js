'use strict';

/**
 * @name directives
 * @description 
 * 
 * Application directives.<p> 
 * 
 * [License]
 * @author various
 */


//application level module
var app = angular.module('app');

/**
 * Automatically clear cache.<p>
 *  
 * Thanks to http://stackoverflow.com/questions/14718826/angularjs-disable-partial-caching-on-dev-machine
 */
app.run(function($rootScope, $templateCache) {
       $rootScope.$on('$viewContentLoaded', function() {
          $templateCache.removeAll();
       });
    });


/**
 * Work around, angular has no support for on blur
 * Thanks to http://coding-issues.blogspot.in/2013/10/angularjs-blur-directive.html
 */
app.directive('ngBlur', ['$parse', function($parse) {
    return function(scope, element, attr) {
        var fn = $parse(attr['ngBlur']);
        element.bind('blur', function(event) {
            scope.$apply(function() {
                fn(scope, {$event:event});
            });
        });
      };
}]);



/**
 * Focus on an element directive.<p>
 * Thanks to http://stackoverflow.com/questions/14833326/how-to-set-focus-in-angularjs
 */
app.directive('focusOn', function() {
    return function(scope, elem, attr) {
        scope.$on('focusOn', function(e, name) {
            if(name === attr.focusOn) {
                elem[0].focus();
            }
        });
    };
});

/**
 * Focus on an element function.<p>
 * Thanks to http://stackoverflow.com/questions/14833326/how-to-set-focus-in-angularjs
 */
app.factory('focus', function ($rootScope, $timeout) {
    return function(name) {
        $timeout(function (){
            $rootScope.$broadcast('focusOn', name);
        }, 50);
    };
});


/**
 * Iterator through a list in reverse order (used in <code>Repeat</code> directive).<p>
 */
app.filter('reverseFilter', function() {
    return function(items) {
        return items.slice().reverse();
    };
});

/**
 * 'Start' index for a list (used in <code>Repeat</code> directive).<p>
 */
app.filter('startFrom', function() {
    return function(input, start) {
        start = +start; //parse to int
        return input.slice(start);
    };
});


/**
 * Directive to fire a function upon a <code>enter</code> key press.
 * thanks to http://stackoverflow.com/questions/17470790/how-to-use-a-keypress-event-in-angularjs
 */
app.directive('soEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if(event.which === 13) {
                scope.$apply(function (){
                    scope.$eval(attrs.soEnter);
                });

                event.preventDefault();
            }
        });
    };
});


/**
 * File uploader functionality
 * thanks to http://cgeers.com/2013/05/03/angularjs-file-upload/
 */
app.directive('upload', ['uploadManager', function factory(uploadManager) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            $(element).fileupload({
                dataType: 'text',
                add: function (e, data) {
                    uploadManager.add(data, attrs.uploadfile);
                },
                progressall: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    uploadManager.setProgress(progress);
                },
                done: function (e, data) {
                    uploadManager.setProgress(0);
                }
            });
        }
    };
}]);
