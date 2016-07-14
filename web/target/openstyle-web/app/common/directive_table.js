'use strict';

/**
 * @name Specific table directives
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
 * 'div' table column heading.<p>
 * 
 * <b>This</b> function extensively uses the <code>dto model</code> to format the attributes. These
 * can be overwritten via passing in values via attributes.<p> 
 * 
 * Directive attributes:<ul>
 *     <li>field: field name</li>
 *     <li>label: language key (optional, default is from dto model)</li>
 *     <li>model: from definition (optional, default is 'model' in parent scope)</li>
 *     <li>class: specific column class (optional, default is derived from dto model)</li>
 * </ul>
 */
app.directive('tableColHead', function ($compile) {
    
    var getTemplate = function(col, sort, colclasshead, colclass){
        return '<div '
             + 'class="' + colclasshead + ' ' + colclass + '" '
             + (sort !== null? 'ng-click="sortColumn(\'' + sort + '\')"' : '')
             + '>'
             + '{{label("' + col + '")}}'
             + '</div>';
    };

    return{
        restrict: 'AE',
        scope: true, //ie inherit 
        link:function(scope, elem, attrs) {
            var model = scope.$parent.model;

            var childmodel = elem.parent().attr('childmodel');
            if (angular.isDefined(childmodel)){
                model = model.getChildModel(childmodel);
            }
            
            var langKey = model.getFieldParameter (attrs.field, 'label');
            if (langKey === null){
                langKey=attrs.field;
            }
            if (angular.isDefined(attrs.label)){
                langKey = attrs.label;
            }

            var colclasshead = 'div-table-col-head';

            //Set sort on column (unless enclosing div has 'so-no-sort' attribute)
            var sort   = null;
            var noSort = angular.isDefined(attrs.soNoSort);
            if (!noSort){
                sort = model.getFieldParameter (attrs.field, 'dto'); 
                if (sort === null){
                    sort = attrs.field;
                }
            }
            else{
                colclasshead = 'div-table-col-head-nosort';
            }

            //get specific column class
            var colclass = '';
            if (angular.isDefined(attrs.class)){
                colclass = attrs.class;
            }
            else{
                colclass = scope.$parent.getColumnClass(attrs.field, model);
            }
             
            
            var el = $compile(getTemplate(langKey, sort, colclasshead, colclass))(scope);
            elem.replaceWith(el);
        },
    };
    
    
});



/**
 * 'div' table column spacer.<p>
 */
app.directive('tableColSpacer', function ($compile) {
    
    var getTemplate = function(){
        return '<div class="so0-spacer" </div>';
    };

    return{
        restrict: 'AE',
        scope: true, //ie inherit 
        link:function(scope, elem, attrs) {
            var el = $compile(getTemplate())(scope);
            elem.replaceWith(el);
        },
    };
});


/**
 * 'div' table column row.<p>
 * 
 * <b>This</b> function extensively uses the <code>dto model</code> to format the attributes. These
 * can be overwritten via passing in values via attributes.<p> 
 * 
 * Directive attributes:<ul>
 *     <li>field: field name</li>
 *     <li>model: from definition (optional, default is 'model' in parent scope)</li>
 *     <li>class: specific column class (optional, default is derived from dto model)</li>
 * </ul>
 */
app.directive('tableColRow', function ($compile) {
    
    var getTemplate = function(value, divId, evenClass, colclassrow, colclass, xtra, trans){
        return '<div '
             +  divId
             + 'class="' + colclassrow + ' ' + colclass + ' ' + '" '
             +  ' ' + evenClass 
             +  ' ' + xtra
             + '>' + value + trans
             + '</div>';
    };

    return{
        restrict: 'AE',
        transclude: true,
        scope: true, //ie inherit 
        link:function(scope, elem, attrs, nullController, transcludeFn) {

            //Manually get html to transclude
            var transc = "";
            transcludeFn(function( clone ) {
                for(var i=0; i<clone.length; i++){
                    transc = transc + clone[i].outerHTML;
                }
            });

            var record = scope.$parent.record;
            var model  = scope.$parent.model;

            //Child model has been declared 
            var childmodel = elem.parent().attr('childmodel');
            if (angular.isDefined(childmodel)){
                model = model.getChildModel(childmodel);
            }
            
            //Another model has been explicitly declared 
            var modelX = elem.parent().attr('model');
            if (angular.isDefined(modelX)){
                model = scope.$parent[modelX];
            }

            var id      = record.getId();
            
            var divId = "";
            if (angular.isDefined(attrs.id)){
                divId = 'id=' + attrs.id + ' ';
            }

            var fieldId = 'id_' + id + '_' + attrs.field;
            if (angular.isDefined(attrs.fieldId)){
                fieldId = attrs.fieldId;
            }
            var idClass = 'row_' + id;
            var def     = model.getFieldDef(attrs.field);
            
           
            //Array field
            var arrayIndex = null;
            if (angular.isDefined(attrs.index)){
                arrayIndex = parseInt(attrs.index);
            }
            
            
            var value   = null;
            var edit    = record !== null && def !== null && (def.edit === true || (def.edit === 'new' && id < 0));

            //Service check re editability
            if (edit && record.isService() && !scope.$parent.isService){
                edit = false;
            }
            
            //Service check in html re editability
            if (edit && angular.isDefined(attrs.isService) && !scope.$parent.isService){
                edit = false;
            }
            
            //Record override re editability
            if (edit && angular.isDefined(attrs.soFieldEdit)){
                edit = record[attrs.soFieldEdit](attrs.field, arrayIndex);
            }
            

            if (edit && (scope.$parent.isEdit() || id < 0)){
                
                //Get list to update
                var list = elem.parent().attr('list');
                var index = null;

                if (!angular.isDefined(list)){

                    //use default from controller
                    list = 'list';    
                    
                    //use child model. Need to find parent id 
                    if (angular.isDefined(childmodel)){
                        var parentIndex = scope.$parent.findIndexById(record.parent.getId(), scope.$parent[list]);
                        var parentDef   = record.parent.model.getFieldDef(childmodel);

                        index = scope.$parent.findIndexById(id, scope.$parent[list][parentIndex][parentDef.dto]);
                        list = list + '[' + parentIndex + '].' + parentDef.dto;
                    }
                }

                if (index === null){
                    index = scope.$parent.findIndexById(id, scope.$parent[list]);
                }

                var input = list + '[' + index + '].' + def.dto;
                if (arrayIndex !== null){
                    input = list + '[' + index + '].' + def.dto + '[' + arrayIndex + ']';
                }
                
                var classX = '';
                if (record !== null && !record.isValid(attrs.field)){
                    classX = 'div-table-col-invalid';
                }

                var type = 'text';
                if (def.isBoolean){
                    type = 'checkbox';
                }
                
                //Select is special case
                if (angular.isDefined(attrs.selectOptions)){
                    value = '<select '
                            + 'class="' + classX + '" '
                            + 'ng-options="' + attrs.selectOptions + '" '
                            + 'id="' + fieldId + '" '
                            + 'ng-model="' + input + '" '
                            + (def.notNull? 'required ' : '')
                            + 'ng-change="inputUpdate(record,\'' + attrs.field + '\')" '
                            + '></select>';
                }
                //Date is special case
                //Thanks to http://plnkr.co/edit/Ata3YOsl7p9hVNMRQFGT?p=preview
                else if (def.isDate){
                    classX = classX + ' form-control div-table-datepicker';
                    value = '<input type="text" '
                            + 'class="' + classX + '" '
                            + 'datepicker-popup="{{datepickerFormat}}" '
                            + 'show-button-bar="{{datepickerShowButtonBar}}" '
                            + 'datepicker-options="{{datepickerDateOptions}}" '
                            + 'is-open="opened" '
                            + 'init-date=new Date '
                            + 'id="' + fieldId + '" '
                            + 'ng-model="' + input + '" '
                            + (def.notNull? 'required ' : '')
                            + 'ng-change="inputUpdate(record,\'' + attrs.field + '\')" '
                            + '>'; 
                }
                else{
                    value = '<input '
                            + 'type="'+ type + '" '
                            + 'id="' + fieldId + '" '
                            + 'ng-model="' + input + '" '
                            + 'ng-change="inputUpdate(record,\'' + attrs.field + '\',' + arrayIndex + ')" '
                            + 'class="' + classX + '" '
                            + (def.min !== null?('minlength="' + def.min + '" ') : '')
                            + (def.max !== null?('maxlength="' + def.max + '" ') : '')
                          //  + 'placeholder="' + def.label + '" '  TODO: Make this a requested feature
                            + (def.notNull? 'required ' : '')
                            + '>';          
                }
            } //edit

            else if (record === null){
                //do nothing
            }

            //special case, show tick
            else if (def.isBoolean){
                var x = false;
                if (arrayIndex !== null){
                    var xx = record['get' + attrs.field]();
                    if (angular.isArray(xx) && xx.length > arrayIndex){
                        x = record['get' + attrs.field]()[arrayIndex];    
                    }
                }
                else{
                    x = record['get' + attrs.field]();
                }

                if (x !== null && x){
                    value = '<i class="fa fa-check"></i>';
                }
                else{
                    value = '';
                }
            }
            else{
                if (angular.isDefined(record['get' + attrs.field + '_f'])){
                    value = record['get' + attrs.field + '_f']();
                    if (arrayIndex !== null && value !== null){
                        value = value[arrayIndex];
                    }
                }
                else if (angular.isDefined(record['get' + attrs.field])){
                    value = record['get' + attrs.field]();
                    if (arrayIndex !== null && value !== null){
                        value = value[arrayIndex];
                    }
                }
                
                if (angular.isDefined(def.lang) && def.lang){
                    value = "{{label('" + value + "')}}";
                }
            }

            
            if (angular.isDefined(value) && value === 0 && angular.isDefined(attrs.zeroAsNull)){
                value = '';
            }
            
            if (!angular.isDefined(value) || value === null){
                value = '';
            }
 
            
            
            var evenClass='';
            if (angular.isDefined(attrs.even)){
                evenClass = 'ng-class-even="\'' + attrs.even + '\'"';
            }
            /* TODO: Make work
            var evenClass = (scope.$index % 2) === 0? 'div-table-evenrow' : '';
            if (angular.isDefined(attrs.even) && attrs.even === false){
                evenClass = "";
            }
            */
            
            //get specific column class
            var colclass = '';
            var colclassrow = idClass;

            if (angular.isDefined(attrs.class)){
                colclass = attrs.class;
            }
            else{
                colclass = scope.$parent.getColumnClass(attrs.field, model);
                colclassrow = 'div-table-col ' + colclassrow;
            }
            
            if (angular.isDefined(attrs.soFieldClass)){
                colclassrow = record[attrs.soFieldClass](attrs.field) + ' ' + colclassrow;
            }
            
            //Call back function that will pass the current record
            var xtra = '';
            if (angular.isDefined(attrs.callbackFn)){
                var fn = scope.$parent[attrs.callbackFn];
                xtra = fn(record);
            }
            
            var el = $compile(getTemplate(value, divId, evenClass, colclassrow, colclass, xtra, transc))(scope);
            elem.replaceWith(el);
        },
    };
    
    
});


/**
 * 'div' table delete column heading.<p>
 * 
 * <b>This</b> function appends a delete control column to a table.<p> 
 */
app.directive('tableColHeadDel', function ($compile) {
    
    var getTemplate = function(colclasshead, display, delAll){
        return '<div '
             + 'class="' + colclasshead + ' div-table-delete"'
             + display
             + '>'
             + '{{label("Delete");}}'
             + delAll
             + '</div>';
    };

    return{
        restrict: 'AE',
        scope: true, //ie inherit 
        link:function(scope, elem, attrs) {

            var colclasshead = 'div-table-col-head-nosort';
            
            //Is the controller in edit mode?
            var display = '';
            if (!scope.$parent.isEdit()){
                display = ' style="display:none"';
            }

            var delAll = '';
            if (angular.isDefined(attrs.delAll)){
                delAll = '<input ng-model="selectDeleteAllCb" type="checkbox" ng-click="selectDeleteAll()">';
            }

            var el = $compile(getTemplate(colclasshead, display, delAll))(scope);
            elem.replaceWith(el);
        },
    };
    
    
});


/**
 * 'div' table delete column row.<p>
 * 
 * <b>This</b> function appends a delete control column to a table.<p>
 */
app.directive('tableColRowDel', function ($compile) {
    
    var getTemplate = function(input, colclassrow, evenClass, display){
        return '<div '
             + 'class="' + colclassrow + ' div-table-delete" '
             + evenClass 
             + display 
             + '>' 
             + input 
             + '</div>';
    };

    return{
        restrict: 'AE',
        scope: true, //ie inherit 
        link:function(scope, elem, attrs, nullController, transcludeFn) {

            var evenClass = "ng-class-even=\"'labeltab-evenrow'\" ";
            if (angular.isDefined(attrs.even) && attrs.even === false){
                evenClass = "";
            }
            
            var colclassrow = 'div-table-col';

            var model = scope.$parent.model;
            var record  = scope.$parent.record;
            
            //Is the controller in edit mode and is the record deletable?
            var display = '';
            if (!scope.$parent.isEdit()){
                display = ' style="display:none"';
            }

            var input = '<input '
                         + 'ng-model="record.delete" '
                         + 'ng-change="inputUpdate(record,\'deleteX\')" '
                         + 'type="checkbox">';
            if (record === null
                    || !model.isDelete()
                    || (record.isService() && !scope.$parent.isService)){
                input = '';
            }            

            var el = $compile(getTemplate(input, colclassrow, evenClass, display))(scope);
            elem.replaceWith(el);
        },
    };
    
    
});


/**
 * 'div' table langkey column heading.<p>
 */
app.directive('tableColHeadLangkey', function ($compile) {
    
    var getTemplate = function(display){
        return '<div '
             + 'class="div-table-col-head div-table-lkey"'
             + display
             + '>'
             + '{{label("LangKey");}}'
             + '</div>';
    };

    return{
        restrict: 'AE',
        scope: true, //ie inherit 
        link:function(scope, elem, attrs) {

            //Is the controller in edit mode?
            var display = '';
            if (!scope.$parent.isService || !scope.$parent.isEdit()){
                display = ' style="display:none"';
            }

            var el = $compile(getTemplate(display))(scope);
            elem.replaceWith(el);
        },
    };
    
    
});


/**
 * 'div' table language key column row.<p>
 * 
 * Note the field accessor must be <code>Langkey</code> 
 */
app.directive('tableColRowLangkey', function ($compile) {
    
    var getTemplate = function(input, evenClass, display){
        return '<div '
             + 'class="div-table-col div-table-lkey" '
             + evenClass 
             + display 
             + '>' 
             + input 
             + '</div>';
    };

    return{
        restrict: 'AE',
        scope: true, //ie inherit 
        link:function(scope, elem, attrs, nullController, transcludeFn) {
            
            var evenClass = "ng-class-even=\"'labeltab-evenrow'\" ";
            if (angular.isDefined(attrs.even) && attrs.even === false){
                evenClass = "";
            }
            
            var model  = scope.$parent.model;

            //Child model has been declared 
            var childmodel = elem.parent().attr('childmodel');
            if (angular.isDefined(childmodel)){
                model = model.getChildModel(childmodel);
            }

            var def    = model.getFieldDef('Langkey');
            var record = scope.$parent.record;
            var id      = record.getId();
            var display = '';
            
            var edit = scope.$parent.isService 
                          && scope.$parent.isEdit()
                          && record !== null 
                          && def !== null 
                          && (def.edit === true || (def.edit === 'new' && id < 0));

            
            if (edit){
            
                var fieldId = 'id_' + id + '_Langkey';
                if (angular.isDefined(attrs.fieldId)){
                    fieldId = attrs.fieldId;
                }
                
               
                var list = elem.parent().attr('list');
                var index = null;
    
                if (!angular.isDefined(list)){
    
                    //use default from controller
                    list = 'list';    
                    
                    //use child model. Need to find parent id 
                    if (angular.isDefined(childmodel)){
                        var parentIndex = scope.$parent.findIndexById(record.parent.getId(), scope.$parent[list]);
                        var parentDef   = record.parent.model.getFieldDef(childmodel);
    
                        index = scope.$parent.findIndexById(id, scope.$parent[list][parentIndex][parentDef.dto]);
                        list = list + '[' + parentIndex + '].' + parentDef.dto;
                    }
                }
    
                if (index === null){
                    index = scope.$parent.findIndexById(id, scope.$parent[list]);
                }
               
                
                var input = '<input '
                            + 'type="text" '
                            + 'id="' + fieldId + '" '
                            + 'ng-model="' + list + '[' + index + '].' + def.dto + '"'
                            + 'ng-change="inputUpdate(record,\'Langkey\')" ';
                            + (def.min !== null?('minlength="' + def.min + '" ') : '')
                            + (def.max !== null?('maxlength="' + def.max + '" ') : '')
                            + 'placeholder="' + def.label + '" '
                            + (def.notNull? 'required ' : '')
                            + '>';      
            }
            else {
                input = '';
                display = ' style="display:none"';
            }            

                        
            var el = $compile(getTemplate(input, evenClass, display))(scope);
            elem.replaceWith(el);
        },
    };
    
    
});



/**
 * 'div' table waring column row.<p>
 * 
 * <b>This</b> function appends a delete control column to a table.<p>
 */
app.directive('tableColRowWarning', function ($compile) {
    
    var getTemplate = function(id, display, evenClass){
        return '<div'
             + ' id="' + id + '"'
             + ' class="div-table-warning"'
             + evenClass 
             + display
             + '>' 
             + '<span style="color:red"><i class="fa fa-warning"></i></span>' 
             + '</div>';
    };

    return{
        restrict: 'AE',
        scope: true, //ie inherit 
        link:function(scope, elem, attrs, nullController, transcludeFn) {

            var evenClass = " ng-class-even=\"'labeltab-evenrow'\"";
            if (angular.isDefined(attrs.even) && attrs.even === false){
                evenClass = "";
            }

            var record  = scope.$parent.record;
            var id = record !== null? record.getId() : 'xx';
            id = id + '_warn';


            //Is this record valid? If not display warning 
            var display = '';
            if (record === null || record.isValid()){
                display = ' style="display:none"';
            }

            var el = $compile(getTemplate(id, display, evenClass))(scope);
            elem.replaceWith(el);
        },
    };
    
    
});



