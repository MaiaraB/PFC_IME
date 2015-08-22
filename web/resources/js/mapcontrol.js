
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

/*METHODS*/

mapControl.loadWMSService = function (layers){
    this.layers = layers;
    this.map = this.map || this.createMap();
    this.addBaseLayers();
    this.addLayers(this.layers);
    this.showBaseLayer("Aerial");
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
    });
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
    var container = document.getElementById('popup');
    var content = document.getElementById('popup-content');
    var closer = document.getElementById('popup-closer');


    /**
     * Add a click handler to hide the popup.
     * @return {boolean} Don't follow the href.
     */
    closer.onclick = function() {
      overlay.setPosition(undefined);
      closer.blur();
      return false;
    };

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
        var coordinate = evt.coordinate;
        overlay.setPosition(coordinate);
        var hdms = ol.coordinate.toStringHDMS(ol.proj.transform(
        coordinate, 'EPSG:3857', 'EPSG:4326'));
        content.innerHTML = "<div><img src='resources/img/loading_small.gif' width='42' height='42'></div>";
        self.getFeatureInfo(evt,content);
    });
};

mapControl.getFeatureInfo = function(evt,content){
    var view = this.map.getView();
    var viewResolution = view.getResolution();
    var viewProjection = view.getProjection();
    var i,ii;
    for(i = 0,ii = this.olLayers.length;i<ii;i++){
        if(this.olLayers[i].getVisible()){
            var source = this.olLayers[i].getSource();
            var url = source.getGetFeatureInfoUrl(
              evt.coordinate, viewResolution, viewProjection,
              {'INFO_FORMAT': 'application/json', 'FEATURE_COUNT': 50});
              
            if (url) {
                var olLayer = this.olLayers[i];
                $.getJSON(url,function(data){
                    var features = olLayer.layerObj.features;
                    var j,jj;
                    for(j = 0,jj = features.length;j<jj;j++){
                        content.innerHTML = "<p>"+features[j].name+" : " + data.features[0].properties[features[j].wmsId] + "</p>";
                    }
                });
            }
        }
    }
}