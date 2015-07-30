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
    <body onload='showRoad()'>
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
                        <a class="navbar-brand hidden-xs" href="#">RIO 2016</a>
                        <span class="navbar-brand">Falcon : Operacional</span>
                    </div>
                    
                    <!-- Collect the nav links, forms, and other content for toggling -->
                    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                        
                        <ul class="nav navbar-nav navbar-right">
                            <li><a href="#">Ajuda</a></li>
                            <li class="divider"></li>
                            <li><a href="#">Sair</a></li>
                        </ul>
                    </div><!-- /.navbar-collapse -->
                </div><!-- /.container-fluid -->
            </nav>
        </div>
        <div class="navbar-offset"></div>
        <div id="map">
        </div>
        <div class="row main-row">
            <div class="col-sm-4 col-md-3 sidebar sidebar-left pull-left">
                <div class="panel-group sidebar-body" id="accordion-left">
                    <div class="panel panel-default">
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
                                    <a onclick="showAerial()" class="list-group-item small clickable">
                                        <i class="fa fa-globe" ></i> Satélite
                                    </a>
                                    <a onclick="showRoad()" class="list-group-item small clickable">
                                        <i class="fa fa-globe"></i> Ruas
                                    </a>
                                    <a onclick="showAerialWithLabels()()" class="list-group-item small clickable">
                                        <i class="fa fa-globe"></i> Satélite + Ruas
                                    </a>
                                </div>
                                <a href="#" class="list-group-item" data-toggle="collapse" data-target="#ol" data-parent="#menu">
                                    <i class="fa fa-list-alt"> Outras Camadas</i>
                                </a>
                                <div id="ol" class="sublinks collapse">
                                <c:forEach var="camada" items="${camadas}" varStatus="status">
                                    <a onclick="showLayer(${status.index})" class="list-group-item small clickable">
                                        <i class="fa fa-globe"></i> ${camada.nome}
                                    </a>
                                </c:forEach>
                                
                                    
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="mini-submenu mini-submenu-left pull-left">
            <i class="fa fa-list-alt"></i>
        </div>
        
        <%@include file="/WEB-INF/jspf/wms_openlayers.jspf" %>
    </body>
</html>

