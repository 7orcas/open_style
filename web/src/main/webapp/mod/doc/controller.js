'use strict';

angular.module('mod.doc.controllers', ['ngDragDrop', 'pasvaz.bindonce'])


    /**
     * Show / Edit Document
     */
    .controller('docCtrl', [
            '$rootScope',
            '$scope', 
            '$timeout',
            '$window',
            '$http',
            'acCache',
            'acController',
            'acGlobal',
            'acDialogs',
            'acDefinition',
            'docModel',
            'docRemote',
             
            function($rootScope, $scope, $timeout, $window, $http, acCache, acController, acGlobal, acDialogs, acDefinition, 
                    docModel, docRemote
                    ){
             
            	var config = acController.createConfig('docCtrl', docModel.doc);
                config.title                = 'PUCons';
                config.remotePost           = docRemote;
                
                
                acController.configure($scope, config);
                $scope.moveScroller('sidebaraction');
                $scope.moveScroller('header');
                
                var cco =  $scope.cacheObj;
                
                
                //Provide view link
                $scope.getViewLink = function(){
                    return 'rest/doc/view?cco=' + encodeURIComponent($scope.model.json(cco));
                };
                
            }
    ])

  
     
          
;
