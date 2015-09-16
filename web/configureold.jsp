<%-- 
    Document   : configure
    Created on : 11/09/2015, 17:06:03
    Author     : arthurfernandes
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Configuração</title>
        <!--JQuery-->
        <script src='${pageContext.request.contextPath}/resources/js/code.jquery.com_jquery-2.1.4.min.js'></script>
        <!--Bootstrap-->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/maxcdn.bootstrapcdn.com_bootstrap_3.3.5_css_bootstrap.min.css">
        <script src="${pageContext.request.contextPath}/resources/js/maxcdn.bootstrapcdn.com_bootstrap_3.3.5_js_bootstrap.min.js"></script>
        <!--Font Awesome-->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/font-awesome-4.4.0/css/font-awesome.min.css"/>
        
        <script type="text/javascript">
            $(document).ready(function(){
                var StateLoadingEnum = {UNLOADED : 0,LOADING: 1,LOADED : 2};
                
                var layerHandler = {
                    layerActionEnum : {READALL : "readAll"},
                    url : "layer-handler",
                    state : StateLoadingEnum.UNLOADED,
                    layers : {}
                };
                
                layerHandler.loadLayers = function(layersTableBody){
                    if(layerHandler.state === StateLoadingEnum.UNLOADED){
                        layerHandler.state = StateLoadingEnum.LOADING;
                        var urlAction = layerHandler.url + "?action=" + layerHandler.layerActionEnum.READALL;
                        $.getJSON(urlAction,function(data){
                            layersTableBody.empty(); //Clears layersBody
                            var layers = {};
                            var i = 0,ii = data.layers.length;
                            for(;i<ii;i++){
                                var layerId = "layerId"+i;
                                var layer = data.layers[i];
                                layers[layerId] = layer;
                                //TableRow
                                var tableRow = $("<tr></tr>").attr("id",layerId);
                                //Layer Attributes
                                var wmsIdColumn = $("<td></td>").addClass("layerWmsId");
                                wmsIdColumn.text(layer.wmsId);
                                var nameColumn = $("<td></td>").addClass("layer-name");
                                nameColumn.text(layer.name);
                                var styleColumn = $("<td></td>").addClass("layer-style");
                                styleColumn.text(layer.style);
                                var opacityColumn = $("<td></td>").addClass("layer-opacity");
                                opacityColumn.text(layer.opacity);
                                tableRow.append(wmsIdColumn,nameColumn,styleColumn,opacityColumn);
                                
                                //Features
                                var featuresTable = $("<table></table>").addClass("table table-responsive table-bordered");
                                var featuresTableHead = $("<thead></thead>").appendTo(featuresTable);
                                featuresTableHead.append($("<th></th>").text("wmsId"));
                                featuresTableHead.append($("<th></th>").text("Nome"));
                                var featuresTableBody = $("<tbody></tbody>",{id: 'features'+layerId}).appendTo(featuresTable);
                                var features = layer.features;
                                var j=0,jj= features.length;
                                for(;j<jj;j++){
                                    var featuresTableRow = $("<tr></tr>").appendTo(featuresTableBody);
                                    featuresTableRow.append($("<td></td>",{text : features[j].wmsId}));
                                    featuresTableRow.append($("<td></td>",{text : features[j].name}));
                                }
                                
                                //Append to Table
                                layersTableBody.append(tableRow);
                                var layerTableFeaturesTableRow = $("<tr></tr>").append($("<td></td>").attr("colspan","4").append(featuresTable));
                                layerTableFeaturesTableRow.hide();
                                tableRow.click(function(){$(this).next().toggle(300);});
                                layersTableBody.append(layerTableFeaturesTableRow);
                            }
                            layerHandler.layers = layers;
                            layerHandler.state = StateLoadingEnum.LOADED;
                        }).error(function(){
                            alert("ERROR");
                        });
                    }
                };
                
                var layersPanel = $("#layers-panel");
                var layersPanelHeading = $("#layers-heading");
                var layersBody = $("#layers-body");
                var layersTableBody = $("#layers-table-body");
                
                layersBody.hide();
                layersPanelHeading.click(function(){
                   layersBody.slideToggle(300); 
                   layerHandler.loadLayers(layersTableBody);
                });
                
                
                
            });
        </script>
        <style>
            .clickable{
                cursor : pointer;
            }

            table {
                    background: #f5f5f5;
                    border-collapse: separate;
                    box-shadow: inset 0 1px 0 #fff;
                    font-size: 12px;
                    line-height: 24px;
                    //margin: 30px auto;
                    text-align: left;
                    width: 800px;
            }	

            th {
                    background: url(http://jackrugile.com/images/misc/noise-diagonal.png), linear-gradient(#777, #444);
                    border-left: 1px solid #555;
                    border-right: 1px solid #777;
                    border-top: 1px solid #555;
                    border-bottom: 1px solid #333;
                    box-shadow: inset 0 1px 0 #999;
                    color: #fff;
                    font-weight: bold;
                    padding: 10px 15px;
                    position: relative;
                    text-shadow: 0 1px 0 #000;	
            }

            th:after {
                    background: linear-gradient(rgba(255,255,255,0), rgba(255,255,255,.08));
                    content: '';
                    display: block;
                    height: 25%;
                    left: 0;
                    margin: 1px 0 0 0;
                    position: absolute;
                    top: 25%;
                    width: 100%;
            }

            th:first-child {
                    border-left: 1px solid #777;	
                    box-shadow: inset 1px 1px 0 #999;
            }

            th:last-child {
                    box-shadow: inset -1px 1px 0 #999;
            }

            td {
                    border-right: 1px solid #fff;
                    border-left: 1px solid #e8e8e8;
                    border-top: 1px solid #fff;
                    border-bottom: 1px solid #e8e8e8;
                    padding: 10px 15px;
                    position: relative;
                    transition: all 300ms;
            }

            td:first-child {
                    box-shadow: inset 1px 0 0 #fff;
            }	

            td:last-child {
                    border-right: 1px solid #e8e8e8;
                    box-shadow: inset -1px 0 0 #fff;
            }	

            tr {
                    background: url(http://jackrugile.com/images/misc/noise-diagonal.png);	
            }

            tr:nth-child(odd) td {
                    background: #f1f1f1 url(http://jackrugile.com/images/misc/noise-diagonal.png);	
            }

            tr:last-of-type td {
                    box-shadow: inset 0 -1px 0 #fff; 
            }

            tr:last-of-type td:first-child {
                    box-shadow: inset 1px -1px 0 #fff;
            }	

            tr:last-of-type td:last-child {
                    box-shadow: inset -1px -1px 0 #fff;
            }	

            /*tbody:hover td {
                    color: transparent;
                    text-shadow: 0 0 3px #aaa;
            }*/

            tbody:hover tr:hover td {
                    color: #444;
                    text-shadow: 0 1px 0 #fff;
            }
            
        </style>
    </head>
    <body>
        <div class="container container-fluid">
            <div id="layers-panel" class="panel panel-default col-md-12">
                <div id="layers-heading" class="panel-heading clickable">Camadas</div>
                <div id="layers-body" class="panel-body table-responsive">
                    <table id="layers-table" class="table">
                        <thead>
                            <th class="col-md-4">WmsId</th>
                            <th class="col-md-4">Nome</th>
                            <th class="col-md-3">Estilo</th>
                            <th class="col-md-1">Opacidade</th>
                        </thead>
                        <tbody id="layers-table-body" class="table-responsive">
                            
                            
                        </tbody>
                    </table>
                </div>

            </div>
        </div>
    </body>
</html>
