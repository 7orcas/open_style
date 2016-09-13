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

                //Allow reloads
                acCache.removeModel(docModel.doc.model);
                
                var cco =  $scope.cacheObj;
                
                $scope.viewLinkId  = $scope.getNextId();
                
                //Provide view link
                $scope.getViewLink = function(){
                    return 'rest/doc/view?cco=' + encodeURIComponent($scope.model.json(cco) + '&lk=' + $scope.viewLinkId);
                };
                
                
                $scope._edit = function(){
                	cco.setEditMode(!cco.isEditMode());
                	$scope.viewLinkId  = $scope.getNextId();
                };
                
            }
    ])

  
     
          
;
