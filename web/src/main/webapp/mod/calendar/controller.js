'use strict';

angular.module('mod.calendar.controllers', [])


    /**
     * Calendar Map
     * Thanks to http://stackoverflow.com/questions/6807180/how-to-escape-a-json-string-to-have-it-in-a-url
     */
    .controller('calendarMapCtrl', [
            '$rootScope',
            '$scope', 
            'acController', 
            'acModel',
            
            'calendarModel',
            'calendarMapRemote',
            'calendarRemote',

            function($rootScope, $scope, acController, acModel, calendarModel, calendarMapRemote, calendarRemote){

                var config = acController.createConfig('calendarMapCtrl', calendarModel.calendarMap);
                config.title                = 'Calendar';
                config.remotePost           = calendarMapRemote;
                                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header1');
                $scope.moveScroller('header2');
                $scope.lockLeftScroller('#left-col-table');
                
                $scope.model  = $scope.getCacheModel(calendarModel.calendarMap.modelSql);
                $scope.record = $scope.model.createDto();
                $scope.start  = $scope.record.getStart(); 

                $scope.type = {
                    selection: 'month',
                    
                    initialise: function (sql){
                        if (sql.getType() == calendarModel.TYPE_DAY){
                            this.selection = 'day';
                        }
                        else if (sql.getType() == calendarModel.TYPE_WEEK){
                            this.selection = 'week';
                        }
                        else if (sql.getType() == calendarModel.TYPE_YEAR){
                            this.selection = 'year';
                        }
                        else{
                            this.selection = 'month';
                        }
                    },
                    
                    //Thanks to http://stackoverflow.com/questions/6963311/add-days-to-a-date-object
                    add: function (sql, periods){
                        sql.setFinish(null);
                        sql.setPeriods(periods);
                    },
                    
                    set: function (sql){
                        if (this.selection === 'day'){
                            sql.setType(calendarModel.TYPE_DAY);
                            this.add(sql, 18); //days * shifts
                        }
                        else if (this.selection === 'week'){
                            sql.setType(calendarModel.TYPE_WEEK);
                            this.add(sql, 16); //days
                        }
                        else if (this.selection === 'year'){
                            sql.setType(calendarModel.TYPE_YEAR);
                            this.add(sql, 12); //months
                        }
                        else{
                            sql.setType(calendarModel.TYPE_MONTH);
                            this.add(sql, 16); //weeks
                        }
                    },
                    
                };
                $scope.type.initialise($scope.record);
                
                var dialogId = $scope.newDialogId();
                var linkX = '';

                $scope.getViewLink = function(force){
                    $scope.record.setStart($scope.formatDateForElementId('calendar-start-sel'));

                    if ($scope.record.getStart() === null){
                        $scope.displayErrorDialog('Save', 'InvalidEntry', dialogId);
                        return;
                    }
                    $scope[dialogId] = false;
                    
                    $scope.setVisable('calendar-table', false);
                    $scope.type.set($scope.record);
                    $scope.setVisable('calendar-table', true);
                    

                    if (angular.isDefined(force) && force){
                        linkX = '&linkX=' + $scope.newDialogId();    
                    }
                    
                    return 'rest/calendar/view?sql=' + encodeURIComponent($scope.model.json($scope.record)) + linkX;
                };
                
                
                $scope.lookupAction = function(){
                    $scope.getViewLink();
                };
                

                $scope.next = function(dateString){
                	var el = document.getElementById('calendar-start-sel');
                    el.value = dateString;
                };
                
                $scope.previous = function(dateString){
                    $scope.start = $scope.formatDate(dateString);
                };

                /**
                 * Open new dialog
                 * @Override
                 */
                $scope.recordNew = function(){
                    $scope.openDialog('mod/calendar/view/calendar_dialog.html', 'calendarDialogCtrl', {dto: null, model: calendarModel.calendar});
                };
                
                /**
                 * Open edit dialog
                 * @Override
                 */
                $scope.recordEdit = function(id){
                    var sql = $scope.model.createDto();
                    sql.setId(id);
                    sql.setStart(null);
                    sql.setFinish(null);
                    sql.setEvent(null);
                    
                    calendarRemote.queryForce({sql: $scope.model.json(sql)},
                            function(result) { //success callback
                                $scope.openDialog('mod/calendar/view/calendar_dialog.html', 'calendarDialogCtrl', {dto: result.getObject()[0], model: calendarModel.calendar});            
                            },
                            function(result) { //error callback
                                $scope.postError(result);
                            });
                };
                
            }
    ])

     
    /**
     * Calendar dialog
     */
    .controller('calendarDialogCtrl', [
            '$rootScope',
            '$scope', 
            '$timeout',
            'acController',
            'acDialogs',
            'mdataModel',
            'calendarRemote',
            'config',

            function($rootScope, $scope, $timeout, acController, acDialogs, mdataModel, calendarRemote, config){
                
                var configX = acController.createConfig('calendarDialogCtrl', config.model);
                acController.configure($scope, configX);
                
                $scope.dto    = null;
                $scope.showDeleteBtn = false;
                
                //Initialize new dto
                if (config.dto === null){
                    $scope.dto = $scope.model.createDto();
                }
                //Initialize edit dto
                else{
                    $scope.dto = $scope.model.configureObject(config.dto);
                    $scope.showDeleteBtn = true;
                }
                
                $scope.startDate  = $scope.dto.getStart();
                $scope.finishDate = $scope.dto.getFinish();
                                
                $scope.events   = $scope.model.valuesEvent();
                $scope.eventC   = null;
                for (var j=0; j < $scope.events.length; j++){
                    var rec = $scope.events[j];
                    rec.value = $scope.label(rec.value);

                    if (rec.key === $scope.dto.getEvent()){
                        $scope.eventC = rec;
                    }
                }
                
                var plantList = $scope.getCache(mdataModel.plant.model);
                $scope.plants = plantList.selectList();
                $scope.plant  = $scope.plants[0];
                for (var j=0; j < $scope.plants.length; j++){
                    var rec = $scope.plants[j];
                    if (rec.key === $scope.dto.getPlantId()){
                        $scope.plant = rec;
                        break;
                    }
                }
                
                var machineList = $scope.getCache(mdataModel.machine.model);
                $scope.machine  = {key: 0, value : $scope.label('All')};
                $scope.machines = machineList.selectList($scope.machine);
                for (var j=0; j < $scope.machines.length; j++){
                    var rec = $scope.machines[j];
                    if (rec.key === $scope.dto.getMachineId()){
                        $scope.machine = rec;
                        break;
                    }
                }
                
                $scope.station  = $scope.dto.getStationNr() !== 0? $scope.dto.getStationNr() : null;
                
                
                var shiftList = $scope.getCache(mdataModel.shift.model);
                $scope.shift  = {key: 0, value : $scope.label('All')};
                $scope.shifts = shiftList.selectList($scope.shift);
                for (var j=0; j < $scope.shifts.length; j++){
                    var rec = $scope.shifts[j];
                    rec.value = $scope.label(rec.value);
                    if (rec.key === $scope.dto.getShiftId()){
                        $scope.shift = rec;
                    }
                }
                
                
                
                $scope.title = function(){
                    if (config.dto === null){
                        return $scope.label('EventN');
                    }
                    return $scope.label('EventE');
                };
                                
                
                $scope.cancel = function(){
                    config.close('ok');
                };
                
                $scope.remove = function(){
                    $scope.dto.setDelete();
                    save($scope.dto);
                };
                

                //Override for start/end date
                var ux = $scope.inputUpdate;
                $scope.inputUpdate = function(field){
                    if (field === 'start'){
                    	$timeout(function() {
	                    	try{
	                    		var ds = $scope.formatDateForElementId('start-date');
	                    		var df = $scope.formatDateForElementId('finish-date');
	                    		
	                    		if (df < ds){
	                    			$scope.finishDate = ds;
                                	var el = document.getElementById('finish-date');
                                	el.value = $scope.model.formatDate(ds);
                                	$scope.$digest();
	                    		}
	                    		
	                    	} catch (err){}
                    	}, 50);
                    }
                    ux();
                };
                
                
                var dialogId = $scope.newDialogId();
                $scope.save = function(){
                    
                    $scope.dto.setStart($scope.formatDateForElementId('start-date'));
                    $scope.dto.setFinish($scope.formatDateForElementId('finish-date'));
                    
                    $scope.dto.setStartString($scope.model.formatDate($scope.dto.getStart(), $scope.dateFormatDto));
                    $scope.dto.setFinishString($scope.model.formatDate($scope.dto.getFinish(), $scope.dateFormatDto));
                    
                    $scope.dto.setEvent($scope.eventC.key);
                    $scope.dto.setPlantId($scope.plant.key);
                    $scope.dto.setMachineId($scope.machine.key);
                    $scope.dto.setStationNr($scope.station !== null && $scope.station.length !== 0? $scope.station : 0);
                    $scope.dto.setShiftId($scope.shift.key);

                    if (!$scope.dto.isValid()){
                        $scope.displayErrorDialog('Save', 'InvalidEntry', dialogId);
                        return;
                    }
                    $scope[dialogId] = false;
                    
                    save($scope.dto);                    
                };
                
                
                var save = function(dto){
                    var saveList = [];
                    saveList.push(dto);

                    calendarRemote.post(
                            saveList,  
                            null,
                            function(result) { //success callback
                                config.close('ok');
                                config.scope.getViewLink(true);
                            },
                            function(result) { //error callback
                                $scope.postError(result);
                            }
                    );

                };
                
                
            }
     ])

    

;