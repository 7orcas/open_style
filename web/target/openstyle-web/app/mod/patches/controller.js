 'use strict';

angular.module('app.mod.patches.controllers', [])

    /**
     * Database patches
     */
    .controller('patchesCtrl', [
            '$rootScope',
            '$scope', 
            'acController',
            'patchesModel',
            function($rootScope, $scope, acController, patchesModel){
             
                var config = acController.createConfig('patchesCtrl', patchesModel.list);
                config.title                = 'Patches';
                config.sort_predicate_field = 'Nr';
                config.isCachedObjectList   = false;
                acController.configure($scope, config);
                
                /******************************
                 * Specific controller methods
                 ******************************/
                $scope.servers = null;
                if ($scope.cacheObj.length > 0){
                	$scope.servers = $scope.cacheObj[0].getServers();
                }
                
                $scope.getServersCount = function(){
                	if ($scope.showServers()){
                		return $scope.servers.length;
                	}
                	return 0;
                };
                $scope.showServers = function(){
                	return $scope.servers !== null && $scope.servers.length > 0;
                };
                
                
            }
	]);
