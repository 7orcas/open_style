'use strict';

angular.module('mod.mdata.model', [])

    /**
      * Master Data model definitions
      */
    .service('mdataModel', function($rootScope, acGlobal, acCache, acModel) {
        
        var self = {};
             
        /**
         * Return string representation of generic code - description object
         */
        var toStringDescr = function(){
            return this.getCode() + '-' + this.getDescr();
        };

        /**
         * Initialize passed in object with string representation
         * Used in <code>typeahead</code> function
         */
        var setString = function(obj){
            obj[acGlobal.globals().temp_prefix + 'string'] = obj.toString();
        };
        
        /**
         * Return string representation
         * Used in <code>typeahead</code> function
         */
        var getString = function(obj){
            if (!angular.isDefined(obj)){
                return obj;
            }
            return obj[acGlobal.globals().temp_prefix + 'string'];
        };

        
        
        /**************************************************************************************
         * General
         **************************************************************************************/
        
        self.plant = acModel.createModelDef('plantList', 'mdata/plantlist', 'mdata.PlantDto');
        self.plant.fields = [{config:'deleteable'},
                             {config:'initObject', fx: setString},
                             {fn:'toString',       fx: toStringDescr},
                             {fn:'getString',      fx: getString},
                             {listfn:'selectList', fx: acModel.selectList},];
        
        

        self.reqtype = acModel.createModelDef('reqtypeList', 'mdata/reqtypelist', 'mdata.ReqTypeDto');
        self.reqtype.fields = [{field:'colorActive', trans:true},
                               {config:'deleteable'},
                               {config:'initObject', fx: setString},
                               {fn:'toString',       fx: toStringDescr},
                               {fn:'getString',      fx: getString},
                               {listfn:'selectList', fx: acModel.selectList},];
        
        


        self.category = acModel.createModelDef('categoryList', 'mdata/categorylist', 'mdata.CategoryDto');
        self.category.fields = [{config:'deleteable'}];
        
        self.putype = acModel.createModelDef('putypeList', 'mdata/putype/list', 'mdata.PUTypeDto');
        self.putype.fields = [{config:'deleteable'}];
        
        self.puconfig = acModel.createModelDef('puconfigList', 'mdata/puconfig/list', 'mdata.PUConfigDto');
        self.puconfig.fields = [{config:'deleteable'}];
        
        
        /**************************************************************************************
         * Moulds
         **************************************************************************************/
        
        self.mouldMap = acModel.createModelDef('mouldMap', 'mdata/mould/mdatamap', null);
        self.mouldMap.modelSql = 'mdata.MouldSql';
        
        //directive_table.js callback
        var fieldClass = function(field){
            if (!this.getFieldHeader() && !this.isNew()){
                return 'so41-rep';
            }
            return '';
        };
        
        //directive_table.js callback
        var fieldEdit = function(field, index){
            if (field === 'Lasts' || field === 'Sizes'){
                return this.getFieldHeader() || this.isNew();
            }
            if (field === 'MachineActive'){
                return this.isNewRecordsCanSetMachines() || !this.isNew();
            }
            if (field === 'MouldCode'){
                return this.getFieldHeader() || this.isNew();
            }
            if (field === 'MouldCodeDesc'){
                return this.getFieldHeader() || this.isNew();
            }
            return true;
        };
        
        
        self.mouldCode = acModel.createModelDef('mouldCodeList', 'mdata/mould/listcodes', 'mdata.MouldMdataDto');
        self.mouldCode.cacheObjects = false;
        self.mouldCode.modelSql     = 'mdata.MouldSql';
        self.mouldCode.dependencies = ['mdataPlantRemote', 'mdataMouldGroupRemote'];
        self.mouldCode.fields = [{config:'deleteable'},
                                 {fn:'fieldClass',       fx: fieldClass},
                                 {fn:'fieldEdit',        fx: fieldEdit},];
        
        self.mouldCodeExport = acModel.createModelDef(null, 'mdata/mould/export');
        
        
        self.mouldGroup = acModel.createModelDef('mouldGroup', 'mdata/mouldgrouplist', 'mdata.MouldGroupDto');
        self.mouldGroup.cacheObjects = false;
        self.mouldGroup.fields = [{field:'colorActive', trans:true},
                                  {config:'deleteable'},
                                  {config:'getObjectBy', field:'Code'},
                                  {fn:'fieldClass',      fx: fieldClass},
                                  {fn:'fieldEdit',       fx: fieldEdit},];
        
        self.mouldGroupExport = acModel.createModelDef(null, 'mdata/mouldgroupexport');
        
        self.mouldCodesByGroup = acModel.createModelDef(null, 'mdata/mould/listcodesByGroup', 'mdata.MouldCodeSizeDto');
        self.mouldCodesByGroup.fields = [{config:'getObjectBy', field:'SizepId'},];
        
        
        /**************************************************************************************
         * Lasts
         **************************************************************************************/
        
        self.lastMap = acModel.createModelDef('lastMap', 'mdata/last/mdatamap', null);
        self.lastMap.modelSql = 'mdata.LastSql';
        
        //directive_table.js callback
        var fieldEditLast = function(field, index){
            if (field === 'Lasts'){
                return this.isNew();
            }
            if (field === 'MachineActive'){
                return !this.isNew();
            }
            if (field === 'LastCode'){
                return this.isNew();
            }
            return true;
        };
        
        self.lastCode = acModel.createModelDef('lastCodeList', 'mdata/last/listcodes', 'mdata.LastMdataDto');
        self.lastCode.cacheObjects = false;
        self.lastCode.modelSql     = 'mdata.LastSql';
        self.lastCode.dependencies = ['mdataPlantRemote', 'mdataLastGroupRemote'];
        self.lastCode.fields = [{config:'deleteable'},
                                 {fn:'fieldClass',       fx: fieldClass},
                                 {fn:'fieldEdit',        fx: fieldEditLast},];
        
        
        
        self.lastGroup = acModel.createModelDef('lastGroup', 'mdata/lastgrouplist', 'mdata.LastGroupDto');
        self.lastGroup.fields = [{field:'colorActive', trans:true},
                                 {config:'deleteable'},
                                 {config:'getObjectBy', field:'Code'},];
        
        
        self.lastCodesByGroup = acModel.createModelDef(null, 'mdata/last/listcodesByGroup', 'mdata.LastCodeSizeDto');
        self.lastCodesByGroup.fields = [{config:'getObjectBy', field:'SizepId'},];
        

        
        
        /**************************************************************************************
         * Machines
         **************************************************************************************/
        
        self.machine = acModel.createModelDef('machineList', 'mdata/machinelist', 'mdata.MachineDto');
        self.machine.dependencies = ['mdataPlantRemote'];
        self.machine.model_dependencies = ['mdata.MachineColorDto'];
        self.machine.cacheObjects = false;
        self.machine.fields = [{config:'deleteable'},
                               {config:'selectable'},
                               {listfn:'selectList',     fx: acModel.selectList},];
        

        self.shift = acModel.createModelDef('shiftList', 'mdata/shiftlist', 'mdata.ShiftDto');
        self.shift.dependencies = ['mdataPlantRemote'];
        self.shift.fields = [{config:'deleteable'},
                             {config:'initObject', fx: setString},
                             {fn:'toString',       fx: toStringDescr},
                             {fn:'getString',      fx: getString},
                             {listfn:'selectList', fx: acModel.selectList},];
                
        self.machineColor = acModel.createModelDef(null, null, 'mdata.MachineColorDto');
        self.machineColor.cacheObjects = false;
        self.machineColor.fields = [{config:'deleteable'},
                                   {config:'selectable'},];
        
        /**************************************************************************************
         * Styles
         **************************************************************************************/
        //Override of standard
        var valid = function(){
            return true;
        };
        
        //Loop sizes and assign mould / last codes
        var assignCode = function(codesList, sizeIds){
            for (var i=0;i<this.getCodes().length; i++){
                this.getCodes()[i] = null;
            }
            if (codesList === null){
                return;
            }
            for (var j=0;j<codesList.length; j++){
                var c = codesList[j];
                
                
                for (var k=0;k<sizeIds.length; k++){
                    var id  = sizeIds[k];
                    
                    if (c.getSizepId() === id){
                        this.getCodes()[k] = c.getCode();
                        break;
                    }
                }
            }
        }; 
        
        
        self.style = acModel.createModelDef('styleList', 'mdata/stylemdatalist', 'mdata.StyleMdataDto');
        self.style.cacheObjects = false;
        self.style.modelSql     = 'mdata.StyleSql';
        self.style.open         = '';
        self.style.dependencies = ['mdataMouldGroupRemote','mdataLastGroupRemote'];
        self.style.fields       = [{fn:'isValid',         fx: valid},
                                   {fn:'assignCode',      fx: assignCode},
                                   ];
        
        
        self.color = acModel.createModelDef('colorlist', 'mdata/colorlist', 'mdata.ColorDto');

        self.styleMachine = acModel.createModelDef('styleMachineList', 'mdata/stylemachinelist', 'mdata.StyleMachineDto');
        self.styleMachine.cacheObjects = false;
        self.styleMachine.modelSql     = 'mdata.StyleSql';
        
        
        self.attributeProcess = acModel.createModelDef(null, 'mdata/attrtype/processlist', 'mdata.AttributeProcessDto');
        
        self.attributeType = acModel.createModelDef('attributeTypeList', 'mdata/attrtype/list', 'mdata.AttributeTypeDto');
        self.attributeType.fields = [{config:'deleteable'},
                                    {fn:'toString',       fx: toStringDescr},
                                    {listfn:'selectList', fx: acModel.selectList},];
        self.attributeType.dependencies = ['mdataAttrProcessRemote'];

        
        self.styleMaterial = acModel.createModelDef('styleMaterialList', 'mdata/stylemateriallist', 'mdata.StyleColorSizeMaterialDto');
        self.styleMaterial.cacheObjects = false;
        self.styleMaterial.modelSql     = 'mdata.StyleSql';
        
        self.styleAttr = acModel.createModelDef('styleAttrList', 'mdata/styleattrlist', 'mdata.StyleColorAttrDto');
        self.styleAttr.cacheObjects = false;
        self.styleAttr.modelSql     = 'mdata.StyleSql';
        
        
        
        self.styleExport         = acModel.createModelDef(null, 'mdata/stylemouldexport');
        self.styleMaterialExport = acModel.createModelDef(null, 'mdata/stylematerialexport');
        self.styleAttrExport     = acModel.createModelDef(null, 'mdata/styleattrexport'); 
        self.styleBomImport      = acModel.createModelDef(null, 'mdata/fileUpload/bom');
        self.styleAttrImport     = acModel.createModelDef(null, 'mdata/fileUpload/attr');

        /**************************************************************************************
         * Materials
         **************************************************************************************/
        self.materialType = acModel.createModelDef('materialTypeList', 'mdata/mattype/list', 'mdata.MaterialTypeDto');
        self.materialType.fields = [{config:'deleteable'},
                                    {fn:'toString',       fx: toStringDescr},
                                    {listfn:'selectList', fx: acModel.selectList},];
                

        
        
        /**************************************************************************************
         * Sizes
         **************************************************************************************/
        self.size = acModel.createModelDef('sizelist', 'mdata/sizelist', 'mdata.SizeDto');
        self.size.cacheObjects = false;
        self.size.modelSql     = 'mdata.SizeSql';
        
        
        
        
        return self;
         
    })

;
     
