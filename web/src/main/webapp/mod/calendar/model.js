'use strict';

angular.module('mod.calendar.model', [])

    /**
      * Calendar Data model definitions
      */
    .service('calendarModel', function($rootScope, acGlobal, acCache, acModel) {
        
        var self = {};
             
        //Type definitions (form entities.calendar.CalendarI)
        self.TYPE_DAY     = 1;
        self.TYPE_WEEK    = 2;
        self.TYPE_MONTH   = 3;
        self.TYPE_YEAR    = 4;
        
        self.calendarMap = acModel.createModelDef('calendarMap', 'calendar/map', null);
        self.calendarMap.modelSql = 'calendar.CalendarSql';
        self.calendarMap.dependencies = ['calendarRemote'];
        
        
        self.calendar = acModel.createModelDef('calendarList', 'calendar/list', 'calendar.EventDto');
        self.calendar.cacheObjects = false;
        self.calendar.dependencies = ['mdataPlantRemote', 'mdataMachineRemote', 'mdataShiftRemote'];
        self.calendar.fields = [{config:'deleteable'},];
                
        
        return self;
         
    })

;
     
