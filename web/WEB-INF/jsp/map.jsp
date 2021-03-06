<%-- 
    Document   : index.jsp
    Created on : 28/07/2015, 19:30:40
    Author     : arthurfernandes
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>PFC IME</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <%@include file="/WEB-INF/jspf/default_includes.jspf" %>
        
    </head>
    <body>
        <div class="container">
            <nav class="navbar navbar-fixed-top navbar-default" role="navigation">
                <div class="container-fluid">
                    <!-- Brand and toggle get grouped for better mobile display -->
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>
                        <a class="navbar-brand hidden-xs" href="#">PFC_IME</a>
                        <span class="navbar-brand">${username} : ${accessLevelName}</span>
                    </div>
                    
                    <!-- Collect the nav links, forms, and other content for toggling -->
                    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                        
                        <ul class="nav navbar-nav navbar-right">
                            <li><a href="#">Ajuda</a></li>
                            <li class="divider"></li>
                            <li><a href="${pageContext.request.contextPath}/logout">Sair</a></li>
                        </ul>
                    </div><!-- /.navbar-collapse -->
                </div><!-- /.container-fluid -->
            </nav>
        </div>
        <div class="navbar-offset"></div>
        <div id="map"> 
            <div id="sidebar" class="row main-row ">
                <div class="col-sm-4 col-md-3 sidebar sidebar-left pull-left">
                    <div class="panel-group sidebar-body" id="accordion-left">
                        <div class="panel panel-default scrollable_list">
                            <div class="panel-heading">
                                <h4 class="panel-title">
                                    <a data-toggle="collapse" href="#layers">
                                        <i class="fa fa-list-alt"></i>
                                        Camadas
                                    </a>
                                    <span class="pull-right slide-submenu">
                                        <i class="fa fa-chevron-left"></i>
                                    </span>
                                </h4>
                            </div>
                            <div id="layers" class="panel-collapse collapse in">
                                <div class="panel-body list-group">
                                    <a href="#" class="list-group-item" data-toggle="collapse" data-target="#bl" data-parent="#menu">
                                        <i class="fa fa-list-alt"> Camadas Base</i>
                                    </a>
                                    <div id="bl" class="sublinks collapse">
                                        <a onclick="mapControl.showBaseLayer('Aerial')" class="list-group-item small clickable">
                                            <i class="fa fa-globe" ></i> <span>Satélite</span>
                                        </a>
                                        <a onclick="mapControl.showBaseLayer('Road')" class="list-group-item small clickable">
                                            <i class="fa fa-globe"></i> <span>Ruas</span>
                                        </a>
                                        <a onclick="mapControl.showBaseLayer('AerialWithLabels')" class="list-group-item small clickable">
                                            <i class="fa fa-globe"></i> <span>Satélite + Ruas</span>
                                        </a>
                                    </div>
                                    <a href="#" class="list-group-item" data-toggle="collapse" data-target="#ol" data-parent="#menu">
                                        <i class="fa fa-list-alt"> Outras Camadas</i>
                                    </a>
                                    <div id="ol" class="sublinks collapse map-control-layers">
                                    <!--c:forEach var="layer" items="{layers}" varStatus="status">
                                        <a onclick="mapControl.showLayer(this,{status.index})" class="list-group-item small clickable">
                                            <i class="glyphicon-plus"></i> <span class="text-danger">{layer.name}</span>
                                        </a>
                                    <!--/c:forEach-->
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div id="mini-menu" class="mini-submenu mini-submenu-left pull-left">
                <i class="fa fa-list-alt"></i>
            </div>
            <div id="progress" class="progress-bar"></div>    
        </div>
       
        <div id="popup" class="ol-popup" style="display:none">
            <a href="#" id="popup-closer" class="ol-popup-closer"></a>
            <div id="popup-content" class="table-responsive ol-popup-content"></div>
        </div>
        <div class="marker-container">
            <img id="geolocation_marker" src="./resources/img/geolocation_marker.png" data-toggle='tooltip' class='marker-tooltip' style="height: 15px; width:15px;">
        </div>
        
        <!--%@include file="/WEB-INF/jspf/wms_openlayers.jspf" %-->
        <script src="${pageContext.request.contextPath}/resources/js/mapcontrol.js"></script>
    </body>
</html>

