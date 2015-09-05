/* global ol, Infinity */

var mapControl = {
    targetMapDivId : 'map',
    baseLayerStyles : ['Road','Aerial','AerialWithLabels'],
    layerJSONURL : 'layers',
    layerWMSURL : 'wms',
    layers : [],
    baseLayers : [],
    olLayers : []
};

/*Initialize onLoad*/
$(document).ready(function(){
   mapControl.getLayersFromServer();
});
    
/*METHODS*/

mapControl.loadWMSService = function (layers){
    this.layers = layers;
    this.map = this.map || this.createMap();
    this.addBaseLayers();
    this.showBaseLayer("Aerial");
    this.addLayers(this.layers);
    this.configureLayerBar();
    this.configurePopup();
};

mapControl.createMap = function(){
    return new ol.Map({
        layers: [],
        /* Improve user experience by loading tiles while dragging/zooming. Will make
         zooming choppy on mobile or slow devices.*/
        loadTilesWhileInteracting: true,
        target: this.targetMapDivId,
        view: new ol.View({
          center: [-4838783.385621721, -2622880.3527477663],
          zoom: 11
        })
    });
};

mapControl.getLayersFromServer = function(){
    var url = this.layerJSONURL;
    this.layers = [];
    var self = this;
    $.getJSON(url,function(data){
        self.loadWMSService(data.layers);
    }).error(function(){alert("ERROR");});
};

mapControl.addBaseLayers = function(){
    var i, ii;
    for (i = 0, ii = this.baseLayerStyles.length; i < ii; ++i) {
        var baseLayer = (new ol.layer.Tile({
            visible: false,
            preload: Infinity,
            type : 'base',
            source: new ol.source.BingMaps({
              key: 'Ak-dzM4wZjSqTlzveKz5u0d4IQ4bRzVI309GxmkgSVr1ewS6iPSrOvOKhA-CJlm3',
              imagerySet: this.baseLayerStyles[i],
              maxZoom: 19
            })  
        }));
        this.baseLayers.push(baseLayer);
        this.map.addLayer(baseLayer);
    } 
};

mapControl.addLayers = function(layers){
    var i,ii;
    for(i = 0, ii = layers.length;i<ii;i++){
        var layer = layers[i];
        var olLayer = new ol.layer.Tile({
            visible: false,
            preload: Infinity,
            serverType: 'geoserver',
            source: new ol.source.TileWMS(({
                url: this.layerWMSURL,
                params: { 
                    STYLES : layer.style,
                    LAYERS: layer.wmsId
                }
            })),
            opacity : layer.opacity
        });
        olLayer.layerObj = layer;
        this.olLayers.push(olLayer);
        this.map.addLayer(olLayer);
    }
};

mapControl.configureLayerBar = function(){
    var obj = $("#layers #ol");
    var i = 0,ii=0;
    var self = this;
    if(obj !== undefined){
        obj.empty();
        for(i = 0,ii = this.layers.length;i<ii;i++){
            var item = $("<a></a>").addClass("list-group-item small clickable").
                append($("<i></i>").addClass("glyphicon-plus")).
                append($("<span></span>").addClass("text-danger").html(this.layers[i].name));
            item.attr("onclick","mapControl.showLayer(this,"+i+")");
            obj.append(item);
        }
    }
};

mapControl.showLayer = function (element,i) {
    if(this.olLayers[i].getVisible()) {
        this.olLayers[i].setVisible(false);
        //otherLayers[i].setMap(null);
        $(element).children('i').removeClass('glyphicon-minus');
        $(element).children('i').addClass('glyphicon-plus');
        $(element).children('span').removeClass('text-success');
        $(element).children('span').addClass('text-danger');
    } else{
        this.olLayers[i].setVisible(true);
        $(element).children('i').removeClass('glyphicon-plus');
        $(element).children('i').addClass('glyphicon-minus');
        $(element).children('span').removeClass('text-danger');
        $(element).children('span').addClass('text-success');
    } 
};

mapControl.showBaseLayer = function (baselayer){
    for (var i = 0, ii = this.baseLayers.length; i < ii; ++i) {
      this.baseLayers[i].setVisible(this.baseLayerStyles[i] === baselayer);
    }
};

mapControl.configurePopup = function(){
    var container = document.getElementById("popup");
    var content = $('#popup-content');
    var closer = $('#popup-closer');

    /**
     * Add a click handler to hide the popup.
     * @return {boolean} Don't follow the href.
     */
    closer.click(function(){
      overlay.setPosition(undefined);
      closer.blur();
    });

    /**
     * Create an overlay to anchor the popup to the map.
     */
    var overlay = new ol.Overlay(/** @type {olx.OverlayOptions} */ ({
      element: container,
      autoPan: true,
      autoPanAnimation: {
        duration: 250
      }
    }));

    this.map.addOverlay(overlay);
    /**
    * Add a click handler to the map to render the popup.
    */
    var self = this;
    this.map.on('singleclick', function(evt) {
        content.empty();
        var hasFeatures = self.getFeatureInfo(evt,{"content" : content});
        console.log(hasFeatures);
        if(hasFeatures === true){
            overlay.setPosition(evt.coordinate);
        }
    });
};

mapControl.getFeatureInfo = function(evt,obj){
    var content = obj["content"];
    var hasFeatures = false;
    var view = this.map.getView();
    var viewResolution = view.getResolution();
    var viewProjection = view.getProjection();
    var i,ii;
    for(i = 0,ii = this.olLayers.length;i<ii;i++){
        if(this.olLayers[i].getVisible()){
            var source = this.olLayers[i].getSource();
            var url = source.getGetFeatureInfoUrl(
              evt.coordinate, viewResolution, viewProjection,
              {'INFO_FORMAT': 'application/json', 'FEATURE_COUNT': 1});
            if (url) {
                var olLayer = this.olLayers[i];
                $.ajax({
                    url : url,
                    async : false,
                    success: function(data){
                        var jsonData;
                        try{
                            jsonData = $.parseJSON(data);
                        }
                        catch(err){
                            return;
                        }

                        var features = olLayer.layerObj.features;
                        var featuresData = jsonData.features[0];
                        var j,jj;
                        var layerTable = $("<table></table>").addClass("table").addClass("table-responsive");
                        for(j = 0,jj = features.length;j<jj;j++){
                            var featureValue = featuresData.properties[features[j].wmsId];
                            if(featureValue){
                                var layerTableRow = $("<tr></tr>");
                                layerTableRow.append($("<td></td>").html(features[j].name));
                                layerTableRow.append($("<td></td>").html(featureValue));
                                layerTable.append(layerTableRow);
                            }
                        }
                        content.append(layerTable);
                        hasFeatures = true;
                    }
                });
            }
        }
    }
    return hasFeatures;
};