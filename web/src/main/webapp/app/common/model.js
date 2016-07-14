'use strict';

angular.module('app.common.model', [])


    /**
     * @module acModel
     * @version app.common.model
     * 
     * @description
     * Base Model object. Used to generate a data model for a DTO or client side entity (ie it is the M part of the MVC design pattern).
     * 
     * Note acModel is used <u>extensively</u> within <i>this</i> application. The design goal is to have as much configuration
     * on the server side as possible. This will reduce duplicate code and simplify the design.<p>
     * 
     * The initialization of the model is a multi-step process:
     * <ol>
     *     <li>For server side DTO's, the DTO definitions are down-loaded from the server. These definitions are based on 
     *         Java annotations within the Entity / DTO.</li><br>
     *         
     *     <li>The <code>configureFields</code> method within <b>this</b> object is called to setup attributes and methods for 
     *         each field based on the Java annotations. The resulting <code>fieldDefs</code> array is used as the basis for the 
     *         configuration of the DTO's Model. Note the <code>fieldDefs</code> array is an important attribute of the DTO Model 
     *         and is used extensively within the Model.</li><br>
     *         
     *     <li>Client side fields and methods are added to the DTO Model's <code>fieldDefs</code> array (if required).</li><br>
     *     
     *     <li>The <code>configureModel</code> method within <b>this</b> object is then called to setup attributes and methods 
     *         for the DTO model.</li><br>
     *         
     *     <li>At this point attributes, fields and methods can be overwritten.</li><br>
     *     
     *     <li>The DTO model is added to the <code>acCache</code> and ready for general use.</li><br>
     * </ol><p>
     * 
     * Note that a DTO may contain child objects (eg an <code>ArrayList</code> of positions). It is possible that the position objects
     * also contain child objects. There is no limit to the depth of this relationship (although physical constraints will ultimately
     * limit this). Each child object will be configured with its own Model and <code>fieldDefs</code> array.<p> 
     * 
     * [License]
     * @author John Stewart
     */
    .service('acModel', function($state, $rootScope, acGlobal, acCache) {
        
        var self = {};
        
        //Convenience variables (used a lot in this module)
        var globals       = acGlobal.globals();   //TODO: Remove globals, add fields here
        var temp_prefix   = acGlobal.globals().temp_prefix;
        var INVALID_DATE  = '??';  
        
        //Defined in ApplicationI
        var ENTITY_PERMANENT_ID = 10000;
        
        /**
         * Field types (as defined in <code>ApplicationI</code>):<ul> 
         *     <li>TYPE_UNKNOWN       = 0;</li>
         *     <li>TYPE_INTEGER       = 1;</li>
         *     <li>TYPE_LONG          = 2;</li>
         *     <li>TYPE_STRING        = 3;</li>
         *     <li>TYPE_DOUBLE        = 4;</li>
         *     <li>TYPE_BOOLEAN       = 5;</li>
         *     <li>TYPE_DATE          = 6;</li>
         *     <li>TYPE_LIST          = 20;</li>
         * </ul>
         * Refer to <code>ApplicationI</code> in JavaDocs
         * @static
         * @name module:acModel.TYPE
         */
        self.TYPE_UNKNOWN       = 0;
        self.TYPE_INTEGER       = 1;
        self.TYPE_LONG          = 2;
        self.TYPE_STRING        = 3;
        self.TYPE_DOUBLE        = 4;
        self.TYPE_BOOLEAN       = 5;
        self.TYPE_DATE          = 6;
        self.TYPE_LIST          = 20;
        
        /**
         * Application types (as defined in <code>ApplicationI</code>):<ul> 
         *
         *     <li>APP_TYPE_ID           = 51;</li>
         *     <li>APP_TYPE_CURRENCY     = 52;</li>
         *     <li>APP_TYPE_REF_CODE     = 53;</li>
         *     <li>APP_TYPE_REF_ID       = 54;</li>
         *     <li>APP_TYPE_LOOKUP_REF   = 55;</li>
         *     <li>APP_TYPE_LOOKUP_VALUE = 56;</li>
         *     <li>APP_TYPE_ENTITY_NR    = 57;</li>
         *     <li>APP_TYPE_PERCENTAGE   = 58;</li>
         *     <li>APP_TYPE_DESCR        = 59;</li>
         *     <li>APP_TYPE_POS_NR       = 60;</li>
         *     <li>APP_TYPE_KEY          = 61;</li>     
         * </ul>
         * Refer to <code>ApplicationI</code> in JavaDocs
         * @static
         * @name module:acModel.APP_TYPE
         */
        self.APP_TYPE_UNDEFINED    = 0;
        self.APP_TYPE_ID           = 51;
        self.APP_TYPE_CURRENCY     = 52;
        self.APP_TYPE_REF_CODE     = 53;
        self.APP_TYPE_REF_ID       = 54;
        self.APP_TYPE_LOOKUP_REF   = 55;
        self.APP_TYPE_LOOKUP_VALUE = 56;
        self.APP_TYPE_ENTITY_NR    = 57;  //TODO: create getObjectByNr
        self.APP_TYPE_PERCENTAGE   = 58;
        self.APP_TYPE_DESCR        = 59;
        self.APP_TYPE_POS_NR       = 60;  //TODO: create getObjectByPosNr
        self.APP_TYPE_KEY          = 61;  //TODO: create getObjectByKey     
        self.APP_TYPE_SORT_NR      = 62;        
        
        
        ///////////////////////////////////////////////////////// Field Configuration  //////////////////////////////////////////////////////////////////
        
        
        
        
        /**
         * Process the DTO model fields as defined by Java annotations. 
         * The field list comes from:<ul>
         *    <li>a server call to <code>acDefinition.loadDef</code></li>
         *    <li>defined within the client model definition (fields element). Note that these field definitions may be targeted
         *        for child models.</li>
         * </ul><p>
         * 
         * Each fields attribute are inspected and configured for use within <i>this</i> client. Note if an attribute is missing a default value is assigned.<p>
         * 
         * The resulting configured field attributes are:<ul>
         *     <li><code>field</code>       : field access name (setters and getters are prefixed to it)</li>
         *     <li><code>modelonly</code>   : field is for model configuration only (i.e. not to be added the data objects)</li>
         *     <li><code>trans</code>       : transient field, will not be put into Json string or posted</li>
         *     <li><code>edit</code>        : true == field can be edited, 'new' == only new records (ie not persisted to database) can be edited</li> 
         *     <li><code>config</code>      : configuration attribute to control initialization of model and new objects</li>
         *     <li><code>listfn</code>      : configuration attribute to control initialization of model and new lists</li>
         *     <li><code>max</code>         : maximum value (depends on field type, eg for strings this is the maximum length, for int's it is the maximum value)</li>
         *     <li><code>min</code>         : minimum value - same logic as max</li>
         *     <li><code>notNull</code>     : true == field cannot be null</li>
         *     <li><code>label</code>       : field label (if undefined then the 'field' attribute is used)</li>
         *     <li><code>type</code>        : field type (eg integer, string, long, etc)</li>
         *     <li><code>array</code>       : field is an array</li>
         *     <li><code>format</code>      : field format (related to field type, eg double type format is number of decimal places, date type is DDMMYY, etc)</li>
         *     <li><code>appType</code>     : specific application type (eg position number field, lookup reference field, etc). Used in processing control.</li>
         *     <li><code>init</code>        : initial value given to new objects</li>
         *     <li><code>encodeField</code> : indicates the field name containing the index for actual field value. Ie if exists then the record has been encoded within JSON server return object</li>
         *     <li><code>encodeSub</code>   : indicates the field may contain partial encoding</li>
         *     <li><code>lang</code>        : true == use the language file to translate the field value</li>
         *     <li><code>fn</code>          : function name given to objects</li>
         *     <li><code>fx</code>          : function given to objects (used with 'fn')</li>
         *     <li><code>child</code>       : configuration field list for child object. Can be annotated as child.child to indicate the path to the child 
         *                                    (in this case is a child of the child).</li>
         * </ul><p>
         * 
         * Refer to <code>FieldDefDto</code> in JavaDocs
         * 
         * @method
         * @name module:acModel.configureFields
         * @param {FieldDefDto} FieldDefDto list returned from the server after a <code>acDefinition.loadDef</code> call
         * @param {ModelConfig} model config object (optional)
         * @returns {Array} FieldDefs array containing configured fields and methods 
         */ 
        self.configureFields = function (defDto, modelConfig){
            var fieldList = defDto.o;

            //Add in client side definitions
            if (angular.isDefined(modelConfig) 
                && modelConfig !== null 
                && angular.isDefined(modelConfig.fields)){
                
                for (var i=0; i<modelConfig.fields.length; i++){
                    var f = modelConfig.fields[i];

                    //Find child fields and push into them
                    if (angular.isDefined(f.child)){
                        var fieldListChild = findChildFields(fieldList, f.child);
                        if (fieldListChild !== null){
                            fieldListChild.push(f);
                        }
                    }
                    else{
                        fieldList.push(f);
                    }
                }
            }

            //Configure the fields (ie add in default attibutes and functions)
            return configureFields(fieldList);
        };

        //Find a child field array
        var findChildFields = function (fieldDefs, field){
            
            var index = field.indexOf('.'); 
            if (index != -1){
                var fieldx = field.substring(index + 1, field.length);
                
                for (var j=0; j<fieldDefs.length; j++){
                    var f = fieldDefs[j];
                    if (angular.isDefined(f.c)){
                        var x = findChildFields(f.c, fieldx);
                        if (x !== null){
                            return x;
                        }
                    }
                }
            }
            
            for (var i=0; i < fieldDefs.length; i++){
                var f = fieldDefs[i];
                if (angular.isDefined(f.c)
                        && angular.isDefined(f.n)
                        && f.n === field){
                    return f.c;
                }
            }

            return null;
        };

        
        /**
         * Configure the passed in <code>FieldDefDto</code> list.<br>
         * Note: <b>this</b> method is recursively called for child objects.
         * @private 
         * @method
         * @name module:acModel.configureFields>recursive
         * @param {FieldDefDto} FieldDefDto list returned from the server after a <code>acDefinition.loadDef</code> call
         * @returns {Array} FieldDefs array containing configured fields and methods
         */
        var configureFields = function (fieldList){
            var fields = [];
            
            //Iterate each field and configure its attributes
            for (var i=0; i<fieldList.length; i++){
                var f = fieldList[i];
                var obj = {};
                
                //Configuration field
                if (angular.isDefined(f.config)){
                    obj = f;
                }
                
                //Configuration list function
                if (angular.isDefined(f.listfn)){
                    obj = f;
                }

                fields.push(obj);
                
                //Field name (used for access within this client)
                if (angular.isDefined(f.n)){
                    obj.field = f.n;
                }
                else{
                    obj.field = f.field;
                }
                
                //Field DTO name (as defined in the Java DTO)
                obj.dto = f.d;
                if (!angular.isDefined(f.d) && testAttribute(f.dto)){
                    obj.dto = f.dto; 
                }
                
                //Has its own fn (function)
                if (angular.isDefined(f.fn)){
                    obj.fn = f.fn;
                    obj.fx = f.fx;
                }
                
                //Make special fields as model only (ie they will only be available in the model and not be added to data objects)
                if (testAttribute(f.y)){
                    obj.modelonly = true;   
                }
                
                //Indicate that language file is needed to translate the field value
                if (testAttribute(f.p)){
                    obj.lang = true;
                }
                
                //Field type (eg String, Integer, etc)
                if (testAttribute(f.t)){
                    obj.type = f.t; 
                }
                else{
                    obj.type = self.TYPE_STRING;
                }
                
                //Array type
                if (angular.isDefined(f.b)){
                    obj.array = f.b;
                }
                
                /* Field application type. This is used to help this client add extra processing to the 
                 * field (eg appType APP_TYPE_LOOKUP_REF will add a 'values' lookup function) 
                 */
                if (testAttribute(f.a)){
                    obj.appType = f.a;  
                }
                else{
                    obj.appType = self.APP_TYPE_UNDEFINED;
                }

                //Transient field
                if (angular.isDefined(f.trans)){
                    obj.trans = f.trans;
                }

                //Encoded field
                if (angular.isDefined(f.k)){
                    obj.encodeField = f.k;
                    if (angular.isDefined(f.j)){
                        obj.encodeSub = true;
                    }
                }
                
                obj.isDate = false;
                obj.isBoolean = false;

                //Set specific 'is' attributes
                switch(obj.type){
                    case self.TYPE_INTEGER:
                    case self.TYPE_LONG: 
                    case self.TYPE_DOUBLE: 
                        obj.isNumber = true; 
                        break;
                    case self.TYPE_BOOLEAN:
                        obj.isBoolean = true;
                        obj.isNumber = false;
                        break;
                    case self.TYPE_DATE:
                        obj.isDate = true;
                        obj.isNumber = false;
                        break;
                    default:
                        obj.isNumber = false;
                }
                


                //Field label (ie language key). 
                if (testAttribute(f.l)){
                    obj.label = f.l;    
                }
                else{
                    obj.label = f.n;
                }
                
                //not null. 
                if (testAttribute(f.u)){
                    obj.notNull = true;    
                }
                else{
                    obj.notNull = false;
                }

                //Field edit / readonly attribute. 
                //Edit can be for 'new' records only (ie not yet persisted in database) or editing for any time.
                if (testAttribute(f.e)){
                    if (f.e === 'true'){
                        obj.edit = true;
                    }
                    else{
                        obj.edit = f.e; 
                    }
                }
                if (!angular.isDefined(obj.edit) || obj.edit === 'false'){
                    obj.edit = false;
                }
                
                /* Field format (depends on field type). eg
                 * - the format represents the number of decimals for double field
                 * - "dd.mm.yyyy" represents a date format for a date field
                 */
                if (testAttribute(f.f)){
                    if (obj.isNumber){
                        obj.format = parseInt(f.f);
                    }
                    else{
                        obj.format = f.f;   
                    }
                }
                else{
                    obj.format = null;
                }
                
                //Field minimum value (depends on field type).
                if (testAttribute(f.m)){
                    obj.min = f.m;  
                }
                else{
                    obj.min = null;
                }
                
                //Field maximum value (depends on field type).
                if (testAttribute(f.x)){
                    obj.max = f.x;  
                }
                else{
                    obj.max = null;
                }
                
                /* Field values (used in appType APP_TYPE_LOOKUP_REF and APP_TYPE_LOOKUP_VALUE).
                 * These are the language values that match the field value. 
                 */
                if (testAttribute(f.v)){
                    obj.values = [];
                    var keyValues = f.v.split(',');
                    for (var j=0; j < keyValues.length; j++){
                        var keyValue = keyValues[j].split('=');
                        var key = keyValue[0];
                        
                        if (obj.isNumber === true){
                            key = parseInt(key);
                        }
                        obj.values.push({key: key, value : keyValue[1]});
                    }
                }
                
                //Field initialization value (ie initial value when object is created by this client) 
                if (testAttribute(f.z)){
                    obj.init = f.z; 
                }
                
                /* Does this field contain child objects? If so then a new Model is created for the child DTO and
                 * this method is recursively called to configure the child.
                 */
                if (testAttribute(f.c)){
                    obj.model = {};
                    obj.model.fieldDefs = configureFields(f.c); 
                }

                /**
                 * Test if passed in value is valid (according to <b>this</b> fields definition)
                 * TODO: implement all field def's 
                 * TODO: implement all app type field validations (eg posnr needs to be unique)
                 * @param value to test
                 */
                obj.isValid = function (value){
                    var field = null;
                    if (angular.isDefined(value)){
                        field = value;
                    }

                    if (this.notNull && (field === null || field.length === 0)){
                        return false;
                    }

                    if (field === null){
                        return true;
                    }

                    //Tests for non-null values

                    if (this.isDate){
                        var x = self.formatDate(field);
                        if (x === null || x === INVALID_DATE){
                            return false;                            
                        }
                    }
                    
                    if (this.isNumber && testNaN(field)){
                        return false;
                    }

                    return true;
                };

            }
            
            
            //Set field defaults
            for (var i=0; i<fields.length; i++){
                var f = fields[i];
            
                //Set default controls        
                switch(f.appType){
                    case self.APP_TYPE_LOOKUP_REF:
                    case self.APP_TYPE_LOOKUP_VALUE:
                        //These types use language translations
                        if (!angular.isDefined(f.lang)){
                            f.lang = true;
                        }
                        break;
                }

                //Set default lengths
                if (!testAttribute(f.max)){
                    switch(f.type){
                        case self.TYPE_INTEGER:
                        case self.TYPE_LONG: 
                            f.max = 10; 
                            break;
                        
                        case self.TYPE_DOUBLE: 
                            f.max = 10; 
                            break;

                        case self.TYPE_BOOLEAN: 
                            f.max = 10; 
                            break;
                            
                        case self.TYPE_DATE: 
                            f.max = 10; 
                            break;
                        
                        case self.TYPE_STRING:
                        default:
                            f.max = 20;
                    }

                    switch(f.appType){
                        case self.APP_TYPE_ID:
                            f.max = 10; 
                            break;
                        
                        case self.APP_TYPE_CURRENCY: 
                            f.max = 10; 
                            break;
                    }
                }
            }
            
            return fields;
        };
        
        
        /**
         * Test the existence of an attribute
         * @private 
         * @method
         * @name module:acModel.testAttribute
         * @param {Object} Attribute to test
         * @returns {Boolean} True if attribute exists 
         */
        var testAttribute = function(x){
            if (!angular.isDefined(x) || x === null){
                return false;
            }
            if (!angular.isArray(x) && x.length === 0){
                return false;
            }
            return true;
        };
        
        

        
        ///////////////////////////////////////////////////////// Model Configuration  //////////////////////////////////////////////////////////////////       
        
        
        
        /**
         * Create and configure a DTO or client side entity Model (ie a Model is the M part of the MVC design pattern).<p>
         * 
         * Note a DTO / entity Model is used <u>extensively</u> within <i>this</i> application. It contains business and presentation functionality.<p>
         * 
         * Notes:<ul>
         *     <li>- the passed in field definitions are used to create getter/setter methods</li>
         *     <li>- special <code>appType</code> fields are given relevant extra methods</li>
         *     <li>- special format methods depending of the fields <code>Type</code>, eg Date fields are given a <code>formatDate()</code> method</li>
         *  </ul>
         * 
         * @method
         * @name module:acModel.configureModel
         * @param {Class} FieldDefs configured field definitions
         * @returns {Class} Model configured DTO model
         */
        self.configureModel = function (fieldDefs){
            
            //Check if passed in fieldDefs contain fields for child models
            for (var i=0; i<fieldDefs.length; i++){
                var f = fieldDefs[i];
                
                if (angular.isDefined(f.child)){
                    var model = findChildModel(fieldDefs, f.child);
                    if (model !== null){
                        model.fieldDefs.push(f);
                        fieldDefs.splice(i, 1);
                        i = i === 0? i : i - 1;
                        continue;
                    }
                }
            }
            
            
            var model = {};
            model.fieldDefs = fieldDefs;
            return configureModel(model);
        };
        
        
        //Find a child model
        var findChildModel = function (fieldDefs, field){
            
            var index = field.indexOf('.'); 
            if (index != -1){
                var fieldx = field.substring(index + 1, field.length);
                
                for (var j=0; j<fieldDefs.length; j++){
                    var f = fieldDefs[j];
                    if (angular.isDefined(f.model)){
                        var x = findChildModel(f.model.fieldDefs, fieldx);
                        if (x !== null){
                            return x;
                        }
                    }
                }
            }
            
            for (var i=0; i < fieldDefs.length; i++){
                var f = fieldDefs[i];
                if (f.field === field && angular.isDefined(f.model)){
                    return f.model;
                }
            }

            return null;
        };
                
        
        //Recursive call to this function from child objects                    
        var configureModel = function (model){          
            
            //Model permissions
            var perm = 'R'; //Default permission is read only
            for (var i=0; i < model.fieldDefs.length; i++){
                var f = model.fieldDefs[i];
                if (f.field === 'Permission'){
                    perm = f.init;
                    break;
                }
            }

            
            model.isCreate = function(){return perm.indexOf('C') !== -1;};
            model.isRead   = function(){return perm.indexOf('R') !== -1;};
            model.isUpdate = function(){return perm.indexOf('U') !== -1;};
            model.isDelete = function(){return perm.indexOf('D') !== -1;};
            
            
            //Add in default config's
            var defaults = ['init', 'getters', 'setters', 'mGetters', 'id'];
            if (model.isCreate() || model.isUpdate() || model.isDelete()){
                defaults.push('errors');
            }
            
            for (var i=0; i < defaults.length; i++){
                var con = defaults[i];
                if (findConfig(con, model.fieldDefs) === null){
                    model.fieldDefs.push({config:con, value:true});
                }
            }
            
            //Add standard formatters
            model.formatCurrency = formatCurrency;
            model.formatDate     = self.formatDate;
            model.formatPercent  = formatPercent;
            model.formatDecimal  = self.formatDecimal;
              
            //Return child model via dto name
            model.getChildModelByDto = function (dto){
                for (var i=0; i < model.fieldDefs.length; i++){
                    var f = model.fieldDefs[i];
                    if (f.dto === dto && angular.isDefined(f.model)){
                        return f.model;
                    }
                }
                return null;
            };
            
            //Return child model via child field name
            model.getChildModel = function (child){
                return findChildModel(model.fieldDefs, child);
            };
            
            
            
            //Add lookups
            for (var i=0; i<model.fieldDefs.length; i++){
                var f = model.fieldDefs[i];
                
                //Recursive call to create child object's model 
                if (angular.isDefined(f.model)){
                    f.model.parent = model;
                    configureModel (f.model);
                }
                
                
                f.isJson = (!angular.isDefined(f.trans) || !f.trans)
                        && !angular.isDefined(f.config)  
                        && !angular.isDefined(f.fn);

                
                if (f.appType === null){
                    //Do nothing
                }
                
                //Use the Key=Value pairs for passed in values ('v') definition
                else if (f.appType === self.APP_TYPE_LOOKUP_REF
                        || f.appType === self.APP_TYPE_LOOKUP_VALUE){
                    
                    var values = angular.toJson(f.values);
                    
                    model['lookup' + f.field] = new Function("key", "scope", 
                            "var values = " + values + ";" +
                            "var sel = null;" +
                            "for (var j=0; j < values.length; j++){" +
                                "var v = values[j];" +
                                "if (angular.isDefined(scope)){" +
                                    "v.value = scope.label(v.value);" +
                                "}" +
                                "if (v.key === key){" +
                                    "sel = v;" +
                                "}" +
                            "}" +
                            "return sel;");
                    
                    model['values' + f.field] = new Function("scope",
                            "var values = " + values + ";" +
                            "for (var j=0; j < values.length; j++){" +
                                "var v = values[j];" +
                                "if (angular.isDefined(scope)){" +
                                    "v.value = scope.label(v.value);" +
                                "}" +
                            "}" +
                            "values.lookup = function(key){" +
                                "for (var j=0; j < values.length; j++){" +
                                    "var v = values[j];" +
                                    "if (v.key === key){" +
                                        "return v;" +
                                    "}" +
                                "}" +
                                "return null;" +
                            "};" +
                            "return values;");
                    
                }
                
            }
            
            
            configureMetaModel (model);
            
            //Add method to serialize to Json for using in GET's
            model.json = function (dto) {
                var obj;

                if (angular.isArray(dto)){
                    obj = [];
                    
                    for (var m = 0; m < dto.length; m++) {
                        obj.push(jsonObj(dto[m], model.fieldDefs));
                    }
                }
                else{
                    obj = jsonObj(dto, model.fieldDefs);                     
                }

                return angular.toJson(obj);
            };
            
            
            //Create a new dto object
            model.createDto = function(){
                var newObj = {};
                
                configureDto (newObj, model);
            
                //Use defaults from def's
                for (var i=0; i < model.fieldDefs.length; i++){
                    var f = model.fieldDefs[i];

                    //Initialize this field?
                    if (!initThisField(f)){
                        continue;
                    }
                        
                    
                    if (f.appType === self.APP_TYPE_ID){
                        newObj[f.dto] = acGlobal.getNextId() * -1;
                        continue;
                    }
                    
                    if (f.type === self.TYPE_LIST){
                        newObj[f.dto] = [];
                        continue;
                    }
                    
                    //Thanks to http://stackoverflow.com/questions/4852017/proper-way-to-initialize-an-arrays-length-in-javascript
                    if (angular.isDefined(f.array) && f.array > 0){
                        newObj[f.dto] = new Array(f.array);
                        continue;
                    }
                    
                    if (angular.isDefined(f.init) && f.init !== null){
                        
                        switch(f.type){
                            case self.TYPE_INTEGER:
                            case self.TYPE_LONG: 
                                try{
                                    newObj[f.dto] = parseInt(f.init); 
                                } catch (err){}
                                break;
                                
                            case self.TYPE_DOUBLE: 
                            case self.APP_TYPE_CURRENCY: 
                                try{
                                    newObj[f.dto] = f.init; 
                                } catch (err){}
                                break;
                                
                                
                            case self.TYPE_DATE:
                                try{
                                	newObj[f.dto] = new Date(f.init);
                                } catch (err){}
                                break;
                            
                            default:
                                newObj[f.dto] = f.init;
                        }
                    }
                }
            
                return newObj;
            };
            
            
            
            //Configure an object / the list objects with all this model's definitions 
            model.configureObject = function (obj){
                obj.model = model;
                configureObject(obj);
                return obj;
            };

            
            return model;
        };
        

        /**
         * Remove client date timezone offset
         * Thanks to http://stackoverflow.com/questions/2771609/how-to-ignore-users-time-zone-and-force-date-use-specific-time-zone
         * @param date to adjust
         */
        var removeTimezoneOffset = function(d){
        	return new Date(d.getTime() - globals.timezoneOffset);
        };
        
        
        /**
         * Strip passed in dto object of all functions and transient fields
         * Thanks to http://stackoverflow.com/questions/10286204/the-right-json-date-format
         * @param object to strip
         */
        self.stripDto = function(rec){
            if (rec === null || !angular.isObject(rec)){
                return rec;
            }
            
            if (angular.isDate(rec)){
            	rec = removeTimezoneOffset(rec);
        		return rec.toJSON();
            }
            
            var model = rec.model;
            if (!angular.isDefined(model)){
                model = null;
            }

            var recX = {};
            for (var f in rec){
                if (rec[f] === null
                        || f === 'model' 
                        || f === 'parent'
                        || f === '_errors'){
                    continue;
                }
                
                //Function, don't send
                if (angular.isFunction(rec[f])){
                    continue;
                }
                
                //Temporary field, don't send
                if (f.indexOf(acGlobal.globals().temp_prefix) != -1){
                    continue;
                }
                
                //Transient field, don't send
                if (model !== null){
                    var trans = model.getFieldParameter(f, 'trans');
                    if (trans === true){
                        continue;
                    }
                }
                
                //Recursive call for child list
                if (angular.isArray(rec[f])){
                    var arrayX = [];
                    for (var i = 0; i < rec[f].length; i++){
                        arrayX.push(self.stripDto(rec[f][i]));    
                    }
                    recX[f] = arrayX;
                }
                else{
                	
                	if (angular.isDate(rec[f])){
                		recX[f] = removeTimezoneOffset(rec[f]);
                		recX[f] = recX[f].toJSON();
                    }
                	else{
                		recX[f] = rec[f];
                	}
                	
                }
            }
            return recX;
        };

        
        /**
         * Accept a date string of dd.mm.yy or dd.mm.yyyy
         * The separator can be '.' or '/' or ' ' or not exist 
         * TODO make part of infrastructure (ie not need to be called by programmer)
         */
        self.parseDate = function(s){
            if (!angular.isDefined(s)
                    || s == null
                    || s.length < 6
                    || s.length > 10){
                return null;
            }
            
            s = s.replace("/","."); 
            s = s.replace(" ",".");
            
            var day, month, year;
            
            if (s.indexOf(".") != -1){
                var i1 = s.indexOf(".");
                day   = s.substring(0, i1);
                
                var i2 = s.indexOf(".", i1+1);
                if (i2 < 0){
                    return null;
                }
                month = s.substring(i1+1, i2);
                year  = s.substring(i2+1, s.length); 
            }
            else{
                day   = s.substring(0, 1);
                month = s.substring(2, 3);
                year  = s.substring(4, s.length);
            }
            
            if (year.length == 2){
                year = '20' + year;
            }
            
            return new Date(Number(year), Number(month) - 1, Number(day));
        };
        
        
        //Get fields to jsonise
        var jsonObj = function (dto, fieldDefs) {
            var obj = {};
            
            for (var m = 0; m < fieldDefs.length; m++) {
                 var f = fieldDefs[m];
                 if (f.isJson && dto[f.dto] !== null){
                    obj[f.dto] = jsonObjX(dto[f.dto], f);
                 }
            }
            return obj;
        };


        //jsonise 
        var jsonObjX = function (dto, field) {
            
            if (angular.isArray(dto)){
                for (var i = 0; i < dto.length; i++) {
                    dto[i] = jsonObjX(dto[i], field);
                }
            }
            
            else if (field.type === self.TYPE_DATE && angular.isDate(dto)){
                dto = self.formatDate(dto, 'dd.mm.yyyy');
            }
            
            
            return dto;
        };
        
        /**
         * Find the specified config field definition object
         */
        var findConfig = function (con, fieldDefs){
            var conX = con.toLowerCase();
            for (var i=0; i < fieldDefs.length; i++){
                var field = fieldDefs[i];
                if (angular.isDefined(field.config) 
                        && field.config.toLowerCase() === conX){
                    return field;
                }
            }
            return null;
        };
        
        
        /**
         * Add model methods to object.
         * First the 'config' methods are added. 
         * Second the specific 'fn' methods are added. Note that 'fn' methods may override standard 'config' methods
         */
        var configureDto = function(obj, model){
            
            obj.model = model;
            
            //Standard Methods
            obj.isNew = function(){
                return obj.getId() < 0; 
            };
            
            //Add config methods
            for (var i=0; i < model.fieldDefs.length; i++){
                var f = model.fieldDefs[i];
                
                if (angular.isDefined(f.config)){
                    var c = f.config.toLowerCase();
                    
                    if (c === 'getters'){
                        initGetters(obj, model.fieldDefs);
                    }
                    else if (c === 'setters'){
                        initSetters(obj, model.fieldDefs);
                    }
                    else if (c === 'errors'){
                        initErrors(obj, model.fieldDefs);
                    }
                    
                    
                    //Control functions
                    else if (c === 'selectable'){
                        f.value = true;
                        initControlFunctions (obj, 'Select', f.field);
                    }
                    else if (c === 'deleteable'){
                        f.value = true;
                        initControlFunctions (obj, 'Delete', f.field, false);
                    }
                    else if (c === 'editable'){
                        f.value = true;
                        initControlFunctions (obj, 'Edit', f.field);
                    }
                    
                }
            }
            
            
            //provide a dummy isValid method
            if (!angular.isDefined(obj.isValid)){
                obj.isValid = function(){return true;};
            }
            
            
            //Initialise dto's fields (done after setters and getters)
            for (var i=0; i < model.fieldDefs.length; i++){
                var f = model.fieldDefs[i];
                
                if (angular.isDefined(f.config)){
                    var c = f.config.toLowerCase();
                    
                    if (c === 'init'){
                        initFields(obj, model.fieldDefs);
                    }
                   
                }
            }
            
            
            //Check model for specific requirements
            for (var i=0; i < model.fieldDefs.length; i++){
                var f = model.fieldDefs[i];

                //Add defined methods
                if (angular.isDefined(f.fn)){
                    obj[f.fn] = f.fx; 
                }
                
                //All objects require an id. Therefore if null is passed from server, a negative id is assigned
                if (f.appType === self.APP_TYPE_ID && obj[f.dto] === null){
                    obj[f.dto] = acGlobal.getNextId() * -1;
                }
            }

            
            //Initialise dto object with specific function, eg create string field (done last)
            for (var i=0; i < model.fieldDefs.length; i++){
                var f = model.fieldDefs[i];
                
                if (angular.isDefined(f.config) && angular.isDefined(f.fx)){
                    var c = f.config.toLowerCase();
                    
                    if (c === 'initobject'){
                        f.fx(obj);
                    }
                   
                }
            }
            
            
            //Is this a service record. Note ids under 10000 are considered special.
            obj.isService = function(){
                if (obj.getId() > 0 
                        && obj.getId() <= ENTITY_PERMANENT_ID){
                    return true;
                }
                return false;
            };

        };
        

        /**
         * Add meta methods to model
         */
        var configureMetaModel = function(model){
            
            //Return field definition for the passed in field (via its dto name) 
            model.dtoFieldDef = function (dto){
                for (var i=0; i < model.fieldDefs.length; i++){
                    var f = model.fieldDefs[i];
                    
                    if (angular.isDefined(f.dto) && f.dto === dto){
                        return f;
                    }
                }
                return null;
            };
            
            //Return field definition for the passed in field (via its name) 
            model.getFieldDef = function (field){
                for (var i=0; i < model.fieldDefs.length; i++){
                    var f = model.fieldDefs[i];
                    
                    if (angular.isDefined(f.field) && f.field === field){
                        return f;
                    }
                }
                return null;
            };
            
            
            //Return field parameter definition for the passed in field (via its name) 
            model.getFieldParameter = function (field, param){
                for (var i=0; i < model.fieldDefs.length; i++){
                    var f = model.fieldDefs[i];
                    
                    if (angular.isDefined(f.field)
                            && f.field === field
                            && angular.isDefined(f[param])){
                        return f[param];
                    }
                }
                return null;
            };
            
            //Is the passed in field a config field set to 'true'
            model.isConfig = function (field){
                for (var i=0; i < model.fieldDefs.length; i++){
                    var f = model.fieldDefs[i];
                    
                    if (angular.isDefined(f.config) && f.config === field){
                        return f.value;
                    }
                }
                return false;
            };
            
            
            //Set meta getters / setters for model
            for (var i=0; i < model.fieldDefs.length; i++){
                var f = model.fieldDefs[i];
                
                //set if model contains id field
                if (f.appType === self.APP_TYPE_ID
                        && !model.isConfig('idable')){
                     model.fieldDefs.push({config:'idable', value:true});
                }
                
                
                if (angular.isDefined(f.config)){
                    var c = f.config.toLowerCase();
                    
                    if (c === 'mgetters'){
                        initMetaGetters(model);
                    }
                }
            }
            
        };
        
        /**
         * Add methods to the object / list objects.
         */
        var configureObject = function(obj){
            if (angular.isArray(obj)){
                configureList(obj, null);   
                return;
            }
            
            configureDto (obj, obj.model);
            
            for (var x in obj){

                //Configure a list (child) field 
                if (angular.isArray(obj[x])){
                    var m = obj.model.getChildModelByDto(x);
                    obj[x].model = m !== null? m : obj.model;
                    configureList(obj[x], obj);
                }
            }
            
        };
        
        
        /**
         * Add methods to list objects.
         * Add methods to lists.
         */
        var configureList = function(list, parent){
            
            for (var i=0; i < list.length; i++){
                var obj = list[i];
                
                if (obj === null || !angular.isObject(obj)){
                    continue;
                }
                
                obj.parent = parent;
                configureDto (obj, list.model);
                
                for (var x in obj){

                    //Recursive call to create child object's model 
                    if (angular.isArray(obj[x])){
                        
                        var m = list.model.getChildModelByDto(x);
                        if (m !== null){
                            obj[x].model = m;
                            configureList(obj[x], obj);
                        }
                    }
                }
            }
            
            for (var i=0; i < list.model.fieldDefs.length; i++){
                var f = list.model.fieldDefs[i];
                var value = angular.isDefined(f.value)? f.value : true; 
                
                if (angular.isDefined(f.config) && value === true){
                    var c = f.config;
                    
                    if (c === 'id'){
                        initIdList(list, f.field, list.model.fieldDefs);
                    }
                    else if (c === 'utilities'){
                        initUtilitiesList(list);
                    }
                    else if (c === 'errors'){
                        initErrorsList(list);
                    }
                    
                    
                    //Control functions
                    else if (c === 'selectable'){
                        initControlFunctionsList (list, 'Select', f.field);
                    }
                    else if (c === 'deleteable'){
                        initControlFunctionsList (list, 'Delete', f.field);
                    }
                    //TODO: Is this needed?
                    else if (c === 'editable'){
                        initControlFunctionsList (list, 'Edit', f.field);
                    }
                    
                    //Utility functions
                    else if (c === 'getObjectBy'){
                        var dto = list.model.getFieldParameter(f.field, 'dto');
                        list['getObjectBy' + f.field] = function(value){
                            return self.getObjectBy(list, dto, value);
                        };
                    }
                    else if (c === 'getObjectsBy'){
                        var dto = list.model.getFieldParameter(f.field, 'dto');
                        list['getObjectsBy' + f.field] = function(value){
                            return self.getObjectsBy(list, dto, value);
                        };
                    }
                    
                }
                
                //List functions defined in model
                if (angular.isDefined(f.listfn) && angular.isDefined(f.fx) && value === true){
                    list[f.listfn] = f.fx;
                }

            }

            /**
             * Logic to add new records. Needs to insert sort numbers, position numbers
             */
            list.addNew = function(obj){
                this.push(obj);

                for (var i=0; i < list.model.fieldDefs.length; i++){
                    var f = list.model.fieldDefs[i];

                    switch(f.appType){
                        //Get latest sort number and add 1
                        case self.APP_TYPE_SORT_NR:
                            obj[f.dto] = findNextNumber (list, f.dto, 1);
                            break;
                        case self.APP_TYPE_POS_NR:
                            obj[f.dto] = findNextNumber (list, f.dto, 10);
                            break;

                        }

                }

            };


        };
        
        /**
         * Find the next number for the passed in field
         * @param list
         * @param field dto name
         * @param field increment value
         */
        var findNextNumber = function (list, dto, increment){
            var x = increment;

            while (true){
                if (findNextNumberTest (list, dto, x)){
                    return x;
                }
                x = x + increment;
            }
        };
        
        /**
         * Test if passed in test number is the maximum value within the list
         * @param list
         * @param field dto name
         * @param number to test
         */
        var findNextNumberTest = function (list, dto, testNumber){
            for (var i=0; i < list.length; i++){
                var d = list[i][dto];
                if (d >= testNumber){
                    return false;
                }
            }
            return true;
        };
        


        /**
         * Initialize an object with the field definitions, ie each
         * field is created and assigned 'null'.
         * Note, if a field already exists then no action is taken.
         * TODO decide how to initiliaze arrays? 
         */
        var initFields = function (obj, fieldDefs){
            
            var isNew = angular.isDefined(obj.getId) && (obj.getId() === null || obj.getId() < 0);
            
            for (var i=0; i < fieldDefs.length; i++){
                var field = fieldDefs[i];
                
                //Initialize this field?
                if (!initThisField(field)){
                    continue;
                }
                
                if (angular.isDefined(field.fn) || field.type === self.TYPE_LIST){
                    continue;
                }
                
                if (isNew 
                        && !angular.isDefined(obj[field.dto])
                        && angular.isDefined(field.init)){
                    
                    if (field.type === self.TYPE_DATE){
                        obj[field.dto] = new Date(field.init);
                    }
                    else{
                        obj[field.dto] = field.init;
                    }
                    
                }
                if (!angular.isDefined(obj[field.dto])){
                    obj[field.dto] = null;
                }
            }
        };
        
        /**
         * Initialize an object with getter functions 
         */
        var initGetters = function (obj, fieldDefs){
            for (var i=0; i < fieldDefs.length; i++){
                var field = fieldDefs[i];
                
                //Initialize this field?
                if (!initThisField(field)){
                    continue;
                }
                
                obj['get' + field.field] = new Function("return this['" + field.dto + "'];");
                obj['get' + field.field + '_f'] = obj['get' + field.field];
                
                if (!angular.isDefined(field.type)){
                    continue;
                }
                
                if (field.type === self.TYPE_BOOLEAN){
                    obj['is' + field.field] = new Function("return this['" + field.dto + "'] !== null && this['" + field.dto + "'] === true;");
                }
                
                if (field.type === self.TYPE_DOUBLE){
                    obj['get' + field.field + '_f'] = new Function("return this.model.formatDecimal(this['" + field.dto + "']," + (field.format !== null? field.format : 2) + ");");
                }
                
                if (field.type === self.TYPE_DATE){
                    obj['get' + field.field + '_f'] = new Function("return this.model.formatDate(this['" + field.dto + "'],'" + (field.format !== null? field.format : 'dd.mm.yyyy') + "');");
                }
                
                
                if (field.appType == self.APP_TYPE_UNDEFINED){
                    continue;
                }
                
                
                if (field.appType === self.APP_TYPE_LOOKUP_REF || field.appType === self.APP_TYPE_LOOKUP_VALUE){
                    obj['get' + field.field + '_f'] = new Function("scope",
                            "var x = this.model.lookup" + field.field + "(this['" + field.dto + "']);" +
                            "if (x !== null && angular.isDefined(scope)){" +
                                "x.value = scope.label(x.value);" +
                            "}" +
                            "return x !== null? x.value : null;"
                    );
                }
                
                
            }
        };
        
        
        
        /**
         * Initialize model with meta data getter functions 
         */
        var initMetaGetters = function (model){
            for (var i=0; i < model.fieldDefs.length; i++){
                var field = model.fieldDefs[i];
                
                //Initialize this field?
                if (!initThisField(field)){
                    continue;
                }
                 
                model['get' + field.field + '_F'] = new Function("return '" + field.dto + "';");
                model['get' + field.field + '_L'] = new Function("return '" + field.label + "';");
                
            }
        };
        
        /**
         * Initialize an object with setter functions 
         */
        var initSetters = function (obj, fieldDefs){
            for (var i=0; i < fieldDefs.length; i++){
                var field = fieldDefs[i];
                
                //Initialize this field?
                if (!initThisField(field) || field.edit === false){
                    continue;
                }
                
                obj['set' + field.field] = new Function("x", "this['" + field.dto + "'] = x;");
            }
        };
        
        
       /**
         * Initialize an object with error / validation functions 
         */
        var initErrors = function (obj, fieldDefs){

            //Test <b>this</b> object for being valid according to model
            //If passed in field is defined, then only test that field
            obj.isValid = function(fieldX){

                var f = null;
                //Test individual field
                if (angular.isDefined(fieldX) && fieldX !== null){
                    f = fieldX;
                }
                //Test for errors from server
                else if (this.containsErrors()){
                    return false;
                }

                for (var i=0; i < fieldDefs.length; i++){
                    var field = fieldDefs[i];

                    if (f !== null && f !== field.field){
                        continue;
                    }

                    //Initialize this field?
                    if (!initThisField(field)){
                        continue;
                    }

                    var value = this[field.dto];
                    
                    if (angular.isArray(value)){
                        for (var j=0; j<value.length; j++){
                            if (angular.isObject(value[j]) && angular.isFunction(value[j].isValid) && !value[j].isValid()){
                                return false;
                            }
                        }
                    }
                    else if (!field.isValid(value)){
                        return false; 
                    }
                }

                return true;
            };

            //Error messages from server
            obj._errors  = null;
            
            obj.containsErrors = function(){            
                return this._errors !== null && this._errors.length > 0;
            };
            
            //Add and format an error message
            obj.addError = function(error){            
                if (this._errors === null){
                    this._errors = [];
                }

                //Test if error exists
                for (var i=0; i < this._errors.length; i++){
                    var errorX = this._errors[i];
                    if (errorX.getFieldname() === error.getFieldname() 
                            && errorX.getMessage() === error.getMessage()){
                        return;
                    }
                }                

                this._errors.push(error);
            };

        };

        /**
         * Initialize a list with error functions.
         * param list - list ot add functions to
         */
        var initErrorsList = function (list){

            //Clear error messages
            list.clearErrors = function(){            
                for (var i=0; i < this.length; i++){
                    if (this[i] === null){
                        continue;
                    }
                    this[i]._errors = null;
                }
            };
        };
        

        
        /**
         * Test if the passed in field is to be initialized
         * @param field to test
         */
        var initThisField = function (fieldDef){

            //Model only field
            if (angular.isDefined(fieldDef.modelonly) && fieldDef.modelonly){
                return false;
            }
            
            //config object
            if (angular.isDefined(fieldDef.config)){
                return false;
            }
             
            //Has its own functions
            if (angular.isDefined(fieldDef.fn)){
                return false;
            }

            return true;
        };
        
        
        /**
         * Initialize an object with control functions
         * obj: Object to append functions to
         * fn: Main name of function (should be capitalized, eg Select, Edit, Delete, etc)
         * field: if defined is appended to the field name (ie allows multiple control functions per object) 
         */
        var initControlFunctions = function (obj, fn, field, useTempPrefix){
            
            if (!angular.isDefined(field)){
                field = '';
            }
            
            if (!angular.isDefined(useTempPrefix)){
                useTempPrefix = true;
            }
            
            var fieldX = (useTempPrefix? temp_prefix : '') + fn.toLowerCase()  + field;
            
            isFunction (obj, 'is' + fn + field, fieldX);
            toggleFunction (obj, 'toggle' + fn  + field, fieldX);
            clearFunction (obj, 'clear' + fn  + field, fieldX);
            setFunctionTrue (obj, 'set'  + fn + field, fieldX);
            
        };
        
        /**
         * Initialize a list with control functions
         * list: list to append functions to
         * fn: Main name of function (should be capitalized, eg Select, Edit, Delete, etc)
         * field: if defined is appended to the field name (ie allows multiple control functions per object) 
         */
        var initControlFunctionsList = function (list, fn, field){
            
            if (!angular.isDefined(field)){
                field = '';
            }
            var fieldX = temp_prefix + fn + field;
            
            //return objectsby their control value
            list['getObjectsBy' + fn + field] = function(validateFn){
                return self.getObjectsBy(this, fieldX, true, validateFn);
            };
        };
        
        
        /**
         * Initialize a list with id functions.
         * param list - list to add functions to 
         * param field - id field name (if undefined then accessed via getId() is used)  
         * param list - field definitions from model
         */
        var initIdList = function (list, field, fieldDefs){
            
            //Find the id field
            for (var i=0; i<fieldDefs.length; i++){
                var f = fieldDefs[i];
                if (f.appType === self.APP_TYPE_ID){
                    field = f.dto;
                    break;
                }
            }

            if (!angular.isDefined(field)){
                field = acGlobal.globals().idFieldname;
            }
            
            //return selected objects
            list['getObjectById'] = function(id){
                return self.getObjectBy(this, field, id);
            };
        };
        
        /**
         * Initialize a list with utility functions.
         * param list - list ot add functions to
         */
        var initUtilitiesList = function (list){
            
            /*
             * Replace object in list. Use passed in field to find original object.
             * return true if success
             */
            list.replace = function (obj, field){
                for (var i=0; i<list.length; i++){
                    var rec = list[i];
                    if (rec !== null && rec[field] === obj[field]){
                        list[i] = obj;
                        return true;
                    }
                }
                return false;
            };
            
/*
 * Clear controls field (except 'new' control, this must be saved to database and retrieved - unless override is used).
 * @param force new control to be res
 * ToDo check if used 
 */
list.clearControls = function(forceClearNew) {
    if (angular.isDefined(this.isSelectable) && this.isSelectable()){
        this.clearSelect();         
    }
    if (angular.isDefined(this.isEditable) && this.isEditable()){
        this.clearEdit();           
    }
    if (angular.isDefined(this.isDeleteable) && this.isDeleteable()){
        this.clearDelete();         
    }
};
            
            /*
             * Copy functions and data
             * @param end index, default = array length
             */
            list.copy = function (end){
                var listCopy = [];
                
                //copy everything
                for (var thing in this) {
                    listCopy[thing] = angular.copy(this[thing]);
                }
                
                //tuncate data
                if (angular.isDefined(end) && end < this.length){
                    listCopy.splice(0,end);
                }
                
                
                return listCopy;
            };
            
            /*
             * Return a list of changed records 
             */
            list.getChanges = function(){
                var listx = new Array();
                
                var deleteable = this.model.isConfig('deleteable');
                var editable   = this.model.isConfig('editable');
                var sortable   = this.model.isConfig('sortable');               
                
                for(var i=0; i < this.length; i++) {
                    var rec = this[i];
                    
                    var isDel = deleteable && rec.isDelete();
                    
                    if (rec.isNew() && isDel){
                        continue;
                    }
                    
                    var isEdi = editable && rec.isEdit();
                    var isReo = sortable && rec.isReorder();
                    
                    //Record needs to be updated to database
                    if (rec.isNew() || isEdi || isDel || isReo){ 
                        listx.push(rec);
                    }
                } 
                
                return listx;
            };
            
            
            
        };
        
        

        // Generic helper functions ----------------------------------------------------------------------------------------------
        
        var isFunction = function (obj, method, field){
            var fn = function(){
                return this[field] !== null && this[field] === true; 
            };
            obj[method] = fn;
        };
        
        var setFunctionTrue = function (obj, method, field){
            var fn = function(){this[field] = true;};
            obj[method] = fn;
        };

        var clearFunction = function (obj, method, field){
            var fn = function(){this[field] = null;};
            obj[method] = fn;
        };
        
        
        var toggleFunction = function (obj, method, field){
            var fn = function(){
                if (!angular.isDefined(this[field]) 
                        || this[field] === null
                        || this[field] === false){
                    this[field] = true;
                    return;
                }
                this[field] = null; 
            };
            obj[method] = fn;
        };
        
        //get object
        //Thanks to http://stackoverflow.com/questions/4059147/check-if-a-variable-is-a-string
        self.getObjectBy = function(list, field, value){
            if (!angular.isDefined(value) || value === null){
                return null;
            }
            var lc = typeof value === 'string' || value instanceof String;
            if (lc){
                value = value.toLowerCase();  
            }
            
            for(var i=0; i < list.length; i++) {
                var x = list[i][field];
                if (lc && x !== null){
                    x = x.toLowerCase();  
                }   
                if (x === value) {
                    return list[i];
                }
            } 
            return null; 
        };
        
        //list of objects
        self.getObjectsBy = function(list, field, value, validateFn){
            var listx = new Array();
            for(var i=0; i < list.length; i++) {
                if (list[i][field] === value) {
                    if (angular.isDefined(validateFn) && !validateFn(list[i])){
                        //do nothing
                    }
                    else{
                        listx.push(list[i]);
                    }
                }
            } 
            return listx; 
        };
        
        

        
        // Generic formatting functions ----------------------------------------------------------------------------------------------
        
        var formatCurrency = function (field){
            if (!angular.isDefined(field) || field === null){
                return null;
            }
            //return parseFloat(field).toFixed(2);
            
            var n = field, 
                c = isNaN(c = Math.abs(c)) ? 2 : c, 
                d = d == undefined ? "," : d, 
                t = t == undefined ? "." : t, 
                s = n < 0 ? "-" : "", 
                i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", 
                j = (j = i.length) > 3 ? j % 3 : 0;
            return s + (j ? i.substr(0, j) + t : "") + 
                   i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + 
                   (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
        };

        var formatPercent = function (field, decimials){
            if (!angular.isDefined(field) || field === null){
                return null;
            }
            return formatDecimalX(parseFloat(field).toFixed(decimials)) + "%";
        };
        
        self.formatDecimal = function (field, decimials){
            if (!angular.isDefined(field) || field === null){
                return null;
            }
            return formatDecimalX(parseFloat(field).toFixed(decimials));
        };
        
        var formatDecimalX = function (s){
            if (globals.decimalSymbol !== '.'
                    && angular.isDefined(s)
                    && s !== null){
                s = s.toString().replace ('.', globals.decimalSymbol);
            }
            return s;
        };
        
        /**
         * Date format
         * TODO: Use a library to format
         * @param date field
         * @param format string
         * @return date formatted as a string
         */
        self.formatDate = function (field, format){
            
            if (!angular.isDefined(field) || field === null){
                return null;
            }
            
            if (!angular.isDefined(format) || format === null){
                format = acGlobal.globals().dateFormat;
            }
            
            var year = 2;
            if (format.indexOf('yyyy') != -1){
                year = 4;
            }

            var ts = false;
            if (format.indexOf('hh:mm') != -1){
                ts = true;
            }            
            
            try{ 
                var curr_date = field.getDate();
                var curr_month = field.getMonth() + 1; //Months are zero based
                var curr_year = '' + field.getFullYear();
                if (year === 2){
                    curr_year = curr_year.substring(2);
                }
                var time = '';
                if (ts){
                    var h = field.getHours();
                    var m = field.getMinutes();
                    time = ' ' + (h <= 9 ? '0' : '') + h + ':' + (m <= 9 ? '0' : '') + m;
                }

                return (curr_date <= 9 ? '0' : '') + curr_date  + "." 
                        + (curr_month <= 9 ? '0' : '') + curr_month + "." 
                        + curr_year 
                        + time;
            } catch (err){
                return  INVALID_DATE;
            };
        }; 
        

        
        // Utility functions for modules ----------------------------------------------------------------------------------------------
        

        /**
         * Create a standard model definition using state configuration 
         */
        self.createModelDef = function (state, url, modelDto, modelSqlDto){
            //set defaults
            var def = {
                url:          url,
                state:        state,
                model:        modelDto,
                remote:       null,
                cacheObjects: true
            };


            //Include dto sql in parameters
            if (angular.isDefined(modelSqlDto)){
                def.modelSql = modelSqlDto;
                def.params   = function(){
                                    var model = acCache.getModel(modelSqlDto);
                                    var dto = model.createDto();
                                    return {sql: model.json(dto)};
                                };
            }

            return def;
        };
        
        
        /**
         * Return formated <code>Select</code> list
         * @param first record (if required)
         * @param field to set as value
         */
        self.selectList = function(firstRecord, field){
            var list = [];
            
            if (angular.isDefined(firstRecord) && firstRecord !== null){
                list.push(firstRecord); 
            }
            
            var value = 'getDescr';
            if (angular.isDefined(field)){
                value = field;
                if (field !== 'toString'){
                    value = 'get' + value;
                }
            }
            
            for (var j=0; j < this.length; j++){
                var rec = this[j];
                var sel = {key: rec.getId(), value : rec[value]()};
                list.push(sel);
            }
            return list;
        };
        
        
        return self;
    });
