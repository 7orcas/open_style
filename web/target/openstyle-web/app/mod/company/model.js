'use strict';

angular.module('app.mod.company.model', [])

    /**
      * Company Administration model definitions
      */
    .service('companyModel', function(acModel) {
        
        var self = {};
        
        self.list = acModel.createModelDef('companyAdmin', 'company85/list', 'app.CompanyDto');
        self.list.fields = [{config:'deleteable'},
                            {config:'selectable'},];
               
        
        return self;
    });

     
     
