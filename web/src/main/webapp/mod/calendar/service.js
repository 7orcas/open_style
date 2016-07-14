'use strict';

angular.module('mod.calendar.service', [])

    
     /**
      * Calendar Map
      */
    .factory('calendarMapRemote', function(acRemote, calendarModel) {
        return acRemote.createRemote (calendarModel.calendarMap);
    })
    
    
    /**
      * Calendar List
      */
    .factory('calendarRemote', function(acRemote, calendarModel) {
    	return acRemote.createRemote (calendarModel.calendar);
    })
        
;


