'use strict';

angular.module('mod.prep.model', [])

    /**
      * Plan Preparation model definitions
      */
    .service('prepModel', function($rootScope, acCache, acModel, acGlobal) {

        
        
        var self = {};
        
        
        
        /**************************************************************************************
         * Categories Logic
         **************************************************************************************/
        
        self.categories = acModel.createModelDef(null, 'prep/categories', 'plan.CategoriesDto');
        
        /**
         * Return a string list of possible categories (using their descriptions)
         */ 
        self.getCategories = function(){
            var list = [];
            for (var i=0; i < this.getList().length; i++) {
                list.push(this.getList()[i].getDescription());
            }
            return list;
        };

        /**
         * Return the category codes for passed in category descriptions 
         * @param category descriptions 
         */
        self.getCodes = function(list){
            var codes = '';
            
            for (var j=0; j<list.length; j++) {
                var desc = list[j];
                for (var i=0; i<this.getList().length; i++) {
                    var c = this.getList()[i];
                    if (c.getDescription() === desc){
                        codes += c.getCode();
                        break;
                    }
                }
            }
            return codes;
        };

        
        /**
         * Return category descriptions for the passed in category codes
         * @param category codes
         * @return category descriptions
         */
        self.getDescriptions = function(codes){
            
            var list = [];
            for (var i=0; codes !== null && i < codes.length; i++) {
                var c = codes.charAt(i);
                for (var j=0; j < this.getList().length; j++) {
                    var rec = this.getList()[j];
                    if (rec.getCode() === c){
                        list.push(rec.getDescription());
                        break;
                    }
                }
            }
            return list;
        };

        
        self.categories.fields = [{fn:'getCategories',   fx: self.getCategories},
                                  {fn:'getCodes',        fx: self.getCodes},
                                  {fn:'getDescriptions', fx: self.getDescriptions},
                                  ];
        
        
        
        /**************************************************************************************
         * Preparation Selection
         **************************************************************************************/
        
        self.prepselection = acModel.createModelDef('prep', 'prep/selection', 'plan.PrepSqlDto');
        self.prepselection.dependencies = ['prepCategoriesRemote','simuConfigRemote'];
        
      
        /**
         * Select a page via its index number 
         */
        self.select = function (index){
        	this.setOffset(index * this.getLimit());
		};
		
		/**
         * Select previous page via its index number
         * Return index number 
         */
		self.previous = function (){
			var o = this.getOffset() - this.getLimit();
			o = o >= 0? o : 0;
			this.setOffset(o);
			return this.getOffset() / this.getLimit();
		};
		
		/**
         * Select next page via its index number
         * Return index number 
         */
		self.next = function (){
			var o = this.getOffset() + this.getLimit();
			this.setOffset(o);
			return this.getOffset() / this.getLimit();
		};
        
        
        /**
         * ID fields are readonly and don't have a setter method, therefore need to override this rule
         */
        var setId = function(id){
            this[acGlobal.globals().idFieldname] = id;
        };
        
        
        /**
         * Is the passed in value empty?
         */
		var isEmpty = function (value){
			return value === null || value.length === 0;  
		};
        
        /**
         * Is the current search advanced?
         */
		var isAdvancedSearch = function (){
			return !isEmpty(this.getStyle())
					|| !isEmpty(this.getVariant())
					|| !isEmpty(this.getColor())
					|| !isEmpty(this.getSize())
					|| !isEmpty(this.getReqType())
					|| !isEmpty(this.getMouldGroup())
					|| !isEmpty(this.getMouldCode())
					|| !isEmpty(this.getSourceIdx())
					|| !isEmpty(this.getSourceIdx1())
					|| !isEmpty(this.getDelDateFrom())
					|| !isEmpty(this.getDelDateTo())
					|| !isEmpty(this.getWeekNumberFrom())
					|| !isEmpty(this.getWeekNumberTo())
					|| !isEmpty(this.getShipSourceId())
					|| !isEmpty(this.getSoleColor1())
					|| !isEmpty(this.getSoleColor2())
					|| !isEmpty(this.getPoType())
					|| !isEmpty(this.getSelected())
					|| this.getMissingStock() === true
					|| this.getInvalidData() === true;
		};
        
        
        self.prepselection.fields = [{fn:'setId',           fx: setId},
                                     {fn:'select',          fx: self.select},
                                     {fn:'previous',        fx: self.previous},
                                     {fn:'next',            fx: self.next},
                                     {fn:'isAdvancedSearch',fx: isAdvancedSearch},
                                     ];
        
        
        
        /**************************************************************************************
         * Preparation Run
         **************************************************************************************/
        
        self.preprun            = acModel.createModelDef('prep', 'prep/run', 'plan.PrepSqlDto');
        self.prepSum            = acModel.createModelDef(null, 'prep/sumactive', null);
        self.prepPeriod         = acModel.createModelDef(null, null, 'plan.PrepPeriodDto');
        self.prepPeriodValidate = acModel.createModelDef(null, 'prep/run/validate/period', 'plan.PrepPeriodDto');
        
        
        /**************************************************************************************
         * Preparation Export
         **************************************************************************************/
        self.prepexportSS = acModel.createModelDef('prep', 'prep/spreadsheet');
        
        /**************************************************************************************
         * Preparation Updates
         **************************************************************************************/
        self.prepUpdate = acModel.createModelDef(null, 'prep/update', null);
        
        
        
        
        return self;
         
    });

     
     
