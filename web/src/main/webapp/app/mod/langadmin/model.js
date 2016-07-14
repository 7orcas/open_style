'use strict';

angular.module('app.mod.langadmin.model', [])

    /**
      * Language key-value pair administration model definitions
      */
    .service('langadminModel', function(acModel) {
        
        var self = {};
            
        /**
         * Copy <b>this</b> object into the passed in dto (except language code)
         */
        var copy = function(dto){
            dto.setKey(this.getKey());
            dto.setText(this.getText());
            dto.setSets(this.getSets());
            dto.setClient(this.getClient());
            dto.setLangcode(null);
        };

        self.list = acModel.createModelDef('langAdmin', 'lang/all', 'lang.LangListDto', 'lang.LangSql');
        self.list.cacheObjects = false;
        self.list.fields = [{config:'deleteable'},
                            {fn:'copy',      fx: copy},];
        
        
        self.exportSS = acModel.createModelDef(null, 'lang/export', null);
        
        return self;
    })

;