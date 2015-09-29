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
    this.renderProgressBar();
    this.showBaseLayer("Aerial");
    this.addLayers(this.layers);
    this.configureLayerBar();
    this.configurePopup();
    this.addFullScreen();
    this.addTrackLocationControl();
};

mapControl.createMap = function(){
    this.view = new ol.View({
          center: [-4838783.385621721, -2622880.3527477663],
          zoom: 11
        });
    return new ol.Map({
        layers: [],
        /* Improve user experience by loading tiles while dragging/zooming. Will make
         zooming choppy on mobile or slow devices.*/
        loadTilesWhileInteracting: true,
        target: this.targetMapDivId,
        view: this.view
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
        var source = new ol.source.BingMaps({
            key: 'Ak-dzM4wZjSqTlzveKz5u0d4IQ4bRzVI309GxmkgSVr1ewS6iPSrOvOKhA-CJlm3',
            imagerySet: this.baseLayerStyles[i],
            maxZoom: 19
        });
        var self = this;
        source.on('tileloadstart', function(event) {self.progress.addLoading();});
        source.on('tileloadend', function(event) {self.progress.addLoaded();});
        source.on('tileloaderror', function(event) {self.progress.addLoaded();});
        var baseLayer = (new ol.layer.Tile({
            visible: false,
            preload: Infinity,
            type : 'base',
            source: source 
        }));
        this.baseLayers.push(baseLayer);
        this.map.addLayer(baseLayer);
    } 
};

mapControl.addLayers = function(layers){
    var i,ii;
    for(i = 0, ii = layers.length;i<ii;i++){
        var layer = layers[i];
        var source = new ol.source.TileWMS(({
            url: this.layerWMSURL,
            params: { 
                STYLES : layer.style.name,
                LAYERS: layer.wmsId
            }
        }));
        var self = this;
        source.on('tileloadstart', function(event) {self.progress.addLoading();});
        source.on('tileloadend', function(event) {self.progress.addLoaded();});
        source.on('tileloaderror', function(event) {self.progress.addLoaded();});
        
        var olLayer = new ol.layer.Tile({
            visible: false,
            preload: Infinity,
            serverType: 'geoserver',
            source: source,
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
    $(container).show();
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
    overlay.setPosition(undefined);
    this.map.addOverlay(overlay);
    /**
    * Add a click handler to the map to render the popup.
    */
    var self = this;
    this.map.on('singleclick', function(evt) {
        content.empty();
        var hasFeatures = self.getFeatureInfo(evt,{"content" : content});
        //console.log(hasFeatures);
        if(hasFeatures === true){
            overlay.setPosition(evt.coordinate);
        } else {
            overlay.setPosition(undefined);
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
                var name = this.layers[i].name;
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

                        var featuresData = jsonData.features[0];
                        var features = olLayer.layerObj.features;
                        
                        //counting size of featuresData
                        var key, count = 0;
                        for(key in featuresData) {
                          if(featuresData.hasOwnProperty(key)) {
                            count++;
                          } 
                        }
                        if (count === 0 || features.length === 0) {
                            return false;
                        }
                        
                        var j,jj;
                        var layerTable = $("<table></table>").addClass("table").addClass("");
                        var layerTableRowHeader = $("<tr></tr>");
                        layerTableRowHeader.append($("<th></th>").html(name).attr("rowspan",features.length));
                        var featureValue = featuresData.properties[features[0].wmsId];
                        if(featureValue){
                                layerTableRowHeader.append($("<td></td>").html(features[0].name));
                                layerTableRowHeader.append($("<td></td>").html(featureValue));
                                layerTable.append(layerTableRowHeader);
                        }
                        layerTable.append(layerTableRowHeader);
                        
                        for(j = 1,jj = features.length;j<jj;j++){
                            featureValue = featuresData.properties[features[j].wmsId];
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

//PROGRESS BAR
mapControl.renderProgressBar = function() {

    /**
     * Renders a progress bar.
     * @param {Element} el The target element.
     * @constructor
     */
    function Progress(el) {
        this.el = el;
        this.loading = 0;
        this.loaded = 0;
    };

    /**
     * Increment the count of loading tiles.
     */
    Progress.prototype.addLoading = function() {
        if (this.loading === 0) {
            this.show();
        }
        ++this.loading;
        this.update();
    };

    /**
     * Increment the count of loaded tiles.
     */
    Progress.prototype.addLoaded = function() {
        setTimeout(function() {
            ++this.loaded;
            this.update();
        }.bind(this), 100);
    };

    /**
     * Update the progress bar.
     */
    Progress.prototype.update = function() {
        var width = (this.loaded / this.loading * 100).toFixed(1) + '%';
        this.el.style.width = width;
        if (this.loading === this.loaded) {
            this.loading = 0;
            this.loaded = 0;
            setTimeout(this.hide.bind(this), 500);
      }
    };

    /**
     * Show the progress bar.
     */
    Progress.prototype.show = function() {
        this.el.style.visibility = 'visible';
    };

    /**
     * Hide the progress bar.
     */
    Progress.prototype.hide = function() {
        if (this.loading === this.loaded) {
            this.el.style.visibility = 'hidden';
            this.el.style.width = 0;
        }
    };

    this.progress = new Progress(document.getElementById('progress'));
};

mapControl.addFullScreen = function() {
    this.map.addControl(new ol.control.FullScreen());
    this.map.addInteraction(new ol.interaction.DragRotateAndZoom());
    
    var mapdiv = $("#map");
    $(".ol-full-screen").on("click", ".ol-full-screen-false", function() {
        mapdiv.attr("style","top:0px;");
    });
    $(".ol-full-screen").on("click", ".ol-full-screen-true", function() {
        mapdiv.attr("style","top:50px;");
    });
};

mapControl.addTrackLocationControl = function() {
    /**
    * Define a namespace for the application.
    */
    window.app = {};
    var app = window.app;  
    var self = this;
    /**
    * @constructor
    * @extends {ol.control.Control}
    * @param {Object=} opt_options Control options.
    */
   app.TrackLocationControl = function(opt_options) {

        var options = opt_options || {};

        var geolocateBtn = document.createElement('button');
        geolocateBtn.innerHTML = 'N';
        
        // Geolocation marker
        var markerEl = document.getElementById('geolocation_marker');
        var marker = new ol.Overlay({
          positioning: 'center-center',
          element: markerEl,
          stopEvent: false
        });
        self.map.addOverlay(marker);

        // LineString to store the different geolocation positions. This LineString
        // is time aware.
        // The Z dimension is actually used to store the rotation (heading).
        var positions = new ol.geom.LineString([],
            /** @type {ol.geom.GeometryLayout} */ ('XYZM'));

        // Geolocation Control
        var geolocation = new ol.Geolocation(/** @type {olx.GeolocationOptions} */ ({
          projection: self.view.getProjection(),
          trackingOptions: {
            maximumAge: 10000,
            enableHighAccuracy: true,
            timeout: 600000
          }
        }));

        var deltaMean = 500; // the geolocation sampling period mean in ms

        // Listen to position changes
        geolocation.on('change', function(evt) {
          var position = geolocation.getPosition();
          var accuracy = geolocation.getAccuracy();
          var heading = geolocation.getHeading() || 0;
          var speed = geolocation.getSpeed() || 0;
          var m = Date.now();

          addPosition(position, heading, m, speed);

          var coords = positions.getCoordinates();
          var len = coords.length;
          if (len >= 2) {
            deltaMean = (coords[len - 1][3] - coords[0][3]) / (len - 1);
          }

          var html = [
            'Position: ' + position[0].toFixed(2) + ', ' + position[1].toFixed(2),
            'Accuracy: ' + accuracy,
            'Heading: ' + Math.round(radToDeg(heading)) + '&deg;',
            'Speed: ' + (speed * 3.6).toFixed(1) + ' km/h',
            'Delta: ' + Math.round(deltaMean) + 'ms'
          ].join('<br />');
          //document.getElementById('info').innerHTML = html;
        });

        geolocation.on('error', function() {
          alert('geolocation error');
          // FIXME we should remove the coordinates in positions
        });

        // convert radians to degrees
        function radToDeg(rad) {
          return rad * 360 / (Math.PI * 2);
        }
        // convert degrees to radians
        function degToRad(deg) {
          return deg * Math.PI * 2 / 360;
        }
        // modulo for negative values
        function mod(n) {
          return ((n % (2 * Math.PI)) + (2 * Math.PI)) % (2 * Math.PI);
        }

        function addPosition(position, heading, m, speed) {
          var x = position[0];
          var y = position[1];
          var fCoords = positions.getCoordinates();
          var previous = fCoords[fCoords.length - 1];
          var prevHeading = previous && previous[2];
          if (prevHeading) {
            var headingDiff = heading - mod(prevHeading);

            // force the rotation change to be less than 180°
            if (Math.abs(headingDiff) > Math.PI) {
              var sign = (headingDiff >= 0) ? 1 : -1;
              headingDiff = - sign * (2 * Math.PI - Math.abs(headingDiff));
            }
            heading = prevHeading + headingDiff;
          }
          positions.appendCoordinate([x, y, heading, m]);

          // only keep the 20 last coordinates
          positions.setCoordinates(positions.getCoordinates().slice(-20));

          // FIXME use speed instead
          if (heading && speed) {
            markerEl.src = './resources/img/geolocation_marker_heading.png';
          } else {
            markerEl.src = './resources/img/geolocation_marker.png';
          }
        }

        var previousM = 0;
        // change center and rotation before render
        self.map.beforeRender(function(map, frameState) {
          if (frameState !== null) {
            // use sampling period to get a smooth transition
            var m = frameState.time - deltaMean * 1.5;
            m = Math.max(m, previousM);
            previousM = m;
            // interpolate position along positions LineString
            var c = positions.getCoordinateAtM(m, true);
            var view = frameState.viewState;
            if (c) {
              //view.center = getCenterWithHeading(c, -c[2], view.resolution);
              //view.rotation = -c[2];
              marker.setPosition(c);
            }
          }
          return true; // Force animation to continue
        });

        // recenters the view by putting the given coordinates at 3/4 from the top or
        // the screen
        function getCenterWithHeading(position, rotation, resolution) {
          var size = self.map.getSize();
          var height = size[1];

          return [
            position[0] - Math.sin(rotation) * height * resolution * 1 / 4,
            position[1] + Math.cos(rotation) * height * resolution * 1 / 4
          ];
        }

        // postcompose callback
        function render() {
          self.map.render();
        }

        // geolocate device
        geolocateBtn.addEventListener('click', function() {
          geolocation.setTracking(true); // Start position tracking

          self.map.on('postcompose', render);
          self.map.render();

          //disableButtons();
        }, false);

        //button.addEventListener('click', trackLocation, false);
        //button.addEventListener('touchstart', trackLocation, false);

        var element = document.createElement('div');
        element.className = 'track-location ol-unselectable ol-control';
        element.appendChild(geolocateBtn);
        
        ol.control.Control.call(this, {
            element: element,
            target: options.target
         });
        
   };
   ol.inherits(app.TrackLocationControl, ol.control.Control);
   
   this.map.addControl(new app.TrackLocationControl());
};

