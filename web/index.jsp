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
        <meta name="description" content="">
        <meta name="author" content="">
        
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/maxcdn.bootstrapcdn.com_bootstrap_3.3.5_css_bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/font-awesome-4.4.0/css/font-awesome.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/indexpage.css">
        
    </head>
    <body>
        <div class="container">
            <div class="row">
                <div class="col-sm-6 col-md-6 col-lg-5 hidden-xs iphone">
                    <img src="resources/img/iphone.png" alt="Celular exibindo mapa.">
                </div>
                <div class="col-sm-5 col-md-5 col-lg-5 col-md-offset-1 col-sm-offset-1">
                    <div class="panel panel-default">
                        <div class="panel-heading"> <strong class=""><h2>PFC IME</h2></strong>
                        </div>
                        <div class="panel-body">
                            <form class="form-horizontal" role="form" action="login" method="POST">
                                <div class="form-group">
                                    <label for="inputEmail3" class="col-sm-3 control-label">Usuário</label>
                                    <div class="col-sm-9">
                                        <input class="form-control" name="username" placeholder="usuario">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="inputPassword3" class="col-sm-3 control-label">Senha</label>
                                    <div class="col-sm-9">
                                        <input type="password" class="form-control" name="password" placeholder="senha">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-sm-offset-3 col-sm-9">
                                        <div class="checkbox">
                                            <label class="">
                                                <input type="checkbox" class="">Mantenha-me conectado</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group last ">
                                    <div class="col-sm-offset-3 col-sm-9">
                                        <button type="submit" class="btn btn-success col-xs-12">Entrar</button>
                                    </div>
                                </div>
                                <div class="col-sm-offset-3 col-sm-9">
                                    <c:if test="${authentication_failure}">
                                        <span role="alert" class="text-danger">Usuário ou senha incorretos.</span>
                                    </c:if>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="${pageContext.request.contextPath}/resources/js/code.jquery.com_jquery-2.1.4.min.js"></script>
        <script src="${pageContext.request.contextPath}/resources/js/maxcdn.bootstrapcdn.com_bootstrap_3.3.5_js_bootstrap.min.js"></script>
    </body>
</html>

