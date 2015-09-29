/* global app, angular */

app.controller('LayerController',function ($scope,$log,layerHandler) {
    $scope.layers = [];
    $log.debug("DEGUB");
    $scope.loadLayers = function(){
        $log.debug("LOADING");
        layerHandler.loadLayers().then(function(response){
            $scope.layers = angular.isArray(response.data.objects) ? response.data.objects : [];
            $log.debug($scope.layers);
        });
    };
});