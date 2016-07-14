'use strict';

/**
 * @name Specific menu directives
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
 * Menu icons.<p>
 */
app.directive('menuIcons', function ($compile) {
    
    var getTemplate = function(item){
        return '<div '
             + 'class="sidebar_icon" '
             + 'id="sidebar_warn_' + item + '" '
             + 'style="display:none" '
             + '><span style="color:red"><i class="fa fa-warning"></i></span>'
             + '</div>'
             + '<div '
             + 'class="sidebar_icon" '
             + 'id="sidebar_save_' + item + '" '
             + 'style="display:none" '
             + '><span style="color:blue"><i class="fa fa-floppy-o"></i></span>'
             + '</div>';
    };

    return{
        restrict: 'AE',
        scope: true, //ie inherit 
        link:function(scope, elem, attrs) {

            var item = "";
            if (angular.isDefined(attrs.item)){
                item = attrs.item;
            }

            var el = $compile(getTemplate(item))(scope);
            elem.replaceWith(el);
        },
    };
    
    
});

