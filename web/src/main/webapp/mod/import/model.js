'use strict';

angular.module('mod.import.model', [])

    /**
      * Task model definitions
      */
    .service('importModel', function($rootScope, acCache, acModel) {
        
        var self = {};
        
        /* Defined in ApplicationI */     
        self.TASK_STATUS_NOT_CREATED    = 1;
        self.TASK_STATUS_RUNNING        = 2;
        self.TASK_STATUS_FINISHED       = 3;
        self.TASK_STATUS_FINISHED_ERROR = 4;
        self.TASK_STATUS_FINISHED_WARN  = 5;
        
        
        /* Defined in entities.importdb.ImportFileDto */     
        self.IMAGE_NONE  = 0;
        self.IMAGE_SS    = 1;
        self.IMAGE_CSV   = 2;
        


        var getStatusText = function (){
            switch(this.getStatus()){
                case self.TASK_STATUS_NOT_CREATED:
                    return 'TaskSN';
                case self.TASK_STATUS_RUNNING:
                    return 'TaskSR';
                case self.TASK_STATUS_FINISHED:
                    return 'TaskSF';
                case self.TASK_STATUS_FINISHED_ERROR:
                    return 'TaskSFE';
                case self.TASK_STATUS_FINISHED_WARN:
                    return 'TaskSFW';
            }
            return '?';
        };


        self.importlogi = {
                url:            'import/logiimportstatus',
                state:          'importList',
                model:          'importdb.ImportResponseDto',
                cacheObjects:   true,
                remote:         null,
                fields:         [{fn:'getStatusText', fx: getStatusText}],
            };
        self.importlogi.cacheObjects = false;
        
        
        var getImageFile = function (){
            if (this.getFilename() === null || this.getFilename().length === 0){
                this.setImage(self.IMAGE_NONE);
                return '';
            }
            
            if (this.getFilename().match(/.xls$/)){
                this.setImage(self.IMAGE_SS);
            }
            else if (this.getFilename().match(/.csv$/)){
                this.setImage(self.IMAGE_CSV);
            }

            switch(this.getImage()){
                case self.IMAGE_SS:
                    return 'img/ss.png';
                case self.IMAGE_CSV:
                    return 'img/csv.png';
            }
            return '';
        };
        
        var isImageFile = function(){
            this.getImageFile();
            switch(this.getImage()){
                case self.IMAGE_SS:
                    return true;
                case self.IMAGE_CSV:
                    return true;
            }
            return false;
        };
        
        
        self.importfile = acModel.createModelDef('importFile', 'import/fileimportstatus', 'importdb.ImportResponseDto');
        self.importfile.cacheObjects = false;
        self.importfile.fields = [{fn:'getStatusText', fx: getStatusText},
                                  {child: 'Files', fn:'getImageFile',  fx: getImageFile},
                                  {child: 'Files', fn:'isImageFile',   fx: isImageFile}];

        
        
        self.importfilevalid = acModel.createModelDef(null, 'import/fileimportvalidate', 'importdb.ImportFileDto');
        self.importfilevalid.cacheObjects = false;
        
        self.importconflict       = acModel.createModelDef(null, 'import/conflict', null);
        self.importconflictExport = acModel.createModelDef(null, 'import/conflict/export');
                
        self.importstart = {
                url:          'import/import',
                cacheObjects: false,
                remote:       null
            };
        
        self.createcsv = {
                url:          'import/createcsv',
                cacheObjects: false,
                remote:       null
            };
        
        self.importcsv = {
                url:          'import/importcsv',
                cacheObjects: false,
                remote:       null
            };
        
        self.importstart.model    = self.importlogi.model;
        self.importstart.fields   = self.importlogi.fields;
        
        self.importcsv.model    = self.importlogi.model;
        self.importcsv.fields   = self.importlogi.fields;
        
        self.importreset = {
                url:          'import/resetfixes',
                cacheObjects: false,
                remote:       null
            };
        
        self.importExport = acModel.createModelDef(null, 'import/export');
        
        return self;
         
    });

     
     
