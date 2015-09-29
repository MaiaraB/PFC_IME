/* global angular */

var app = angular.module("configurationApp", []);

app.factory("layerHandler",['$http','appConfiguration',function($http,appConfiguration){
    return {
        loadLayers : function(){
            //return $http.get(appConfiguration.layerUrl"?action=readAll");
        },
        
        saveLayer : function(layer){
            return $http.post("",layer);
        }
    };
}]);

app.factory("appConfiguration",function(){
   return{
       ACTION : {CREATE : 'add',READ : 'readAll',SAVE : 'save',DELETE : 'delete'},
       URL : {layerURL : 'layer-handler',accessLevelURL : 'access-level-handler',userURL : 'user-handler'},
       //buildURL : function(url,)
   };
});