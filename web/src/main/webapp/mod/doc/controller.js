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
                
                $scope.dto =  $scope.cacheObj;
                $scope.cnt =  $scope.sqlObj;
                
                $scope.viewLinkId  = $scope.getNextId();
                
                //Provide view link
                $scope.getViewLink = function(){
                    return 'rest/doc/view?cnt=' + encodeURIComponent($scope.model.json($scope.cnt) + '&lk=' + $scope.viewLinkId);
                };
                
                
                $scope._edit = function(){
                	$scope.cnt.setEditMode(!$scope.cnt.isEditMode());
                	$scope.viewLinkId  = $scope.getNextId();
                };
                
            }
    ])

  
     
          
;
