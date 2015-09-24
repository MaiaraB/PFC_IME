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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/bootstrap-3.3.5-dist/css/bootstrap.min.css">
        <script src="${pageContext.request.contextPath}/resources/bootstrap-3.3.5-dist/js/bootstrap.min.js"></script>
        <!--Font Awesome-->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/font-awesome-4.4.0/css/font-awesome.min.css"/>
        <script src="${pageContext.request.contextPath}/resources/LABjs-2.0.3/LAB.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function(){
                $LAB
                .script("resources/js/wmscrud-template.js").wait()
                .script("resources/js/wmscrud-implementation.js").wait()
                .script("resources/js/wmscrud-uihandler.js")
                .script("resources/js/jquery-ui-1.11.4.custom/jquery-ui.min.css")
                .wait(function(){
                    if(typeof LayerHandler === 'undefined' || UserHandler === 'undefined' || AccessLevelHandler === 'undefined'){

                    console.log("No WMSCRUD class LayerHandler||UserHandler||AccessLevelHandler");
                    return;
                    }

                    var layersPanel = $("#layers-panel");
                    var accessLevelPanel = $("#access-level-panel");
                    var usersPanel = $("#users-panel");
                    initializeUIPanel(layersPanel,LayerHandler,"features");
                    initializeUIPanel(accessLevelPanel,AccessLevelHandler,"layers");
                    initializeUIPanel(usersPanel,UserHandler,"accessLevel");
                    
                    var modalPrompt = $("#modal-prompt");
                    var modalConfirm = modalPrompt.find(".modal-confirm").first();
                    //Additional UI
                    /*$(document).keyup(function(e){
                        if(e.keyCode === 13){//ENTER
                            if(modalPrompt.is(":visible")){
                                
                            }
                        }
                        else if(e.keyCode === 27){//ESCAPE
                            
                        }
                    });*/
                    
                    $(".slidable").each(function(){
                        var self = this;
                        $(this).click(function(event){
                           try{
                                $(self).siblings(".panel-body").first().slideToggle(200); 
                            }
                            catch(err){
                                console.log("No body to hide");
                            }
                        });
                    });
                    
                    var objectFeatures = $("#layers-panel .handler-object-features");
                    objectFeatures.sortable({
                        handler : ".panel-heading",
                        placeholder : "handler-object-features-placeholder"
                    });
                });
            });
        </script>
        <style>
            .clickable{
                cursor : pointer;
            }
            .trash { color:rgb(209, 91, 71); }
            //.okglyph {font-size : 25px;color: #006699}
            .glyphicon-plus-sign {color: #009966}
            .add-glyph{font-size : 35px;}
            .handler-selected-item{
                background-color: rgba(145,238,176,0.6);
            }
            
            .handler-error-highlight{
                border-color: rgb(225,20,20);
            }
            
            .panel .glyphicon,.list-group-item .glyphicon { margin-right:5px; }
            .panel-body .radio, .checkbox { display:inline-block;margin:0px; }
            .panel-body input[type=checkbox]:checked + label { text-decoration: line-through;color: rgb(128, 144, 160); }
            .list-group-item:hover, a.list-group-item:focus {text-decoration: none;background-color: rgb(245, 245, 245);}
            
            .list-group { margin-bottom:0px; }
            .input-group {margin-bottom:15px;}
            
            body{ 
                background-image: url('${pageContext.request.contextPath}/resources/img/background3.jpg');
                background-repeat: repeat;
            }
            body .panel {
                background-color: rgba(255,255,255,0.5);
            }
            .handler-add-current{
                background-color: rgba(0,170,102,0.9);
                color: #ffffff;
            }
            .handler-save-current{
                background-color: rgba(15,188,214,0.9);
                color: #ffffff;
            }
            #message-container{
                right: 30px;
                top: 30px;
                width : 50%;
                position: fixed;
                z-index: 999;
            }
            #layers-panel{
                margin-top: 8vh;
            }
            .extensible-panel{
                max-height: 80vh;
            }
            .handler-object-features-placeholder{
                line-height : 10px;
                height: 100px;
            }
            .handler-object-features .input-group{
                margin-bottom:7px;
            }
        </style>
    </head>
    <body>
        
        <div class="container container-fluid">
            
            <!--LAYERS-->
            <div id="layers-panel" class="panel panel-default table-responsive">
                <div class="panel-heading clickable">
                    <span class="glyphicon glyphicon-list"></span>Camadas
                </div>
                <div class="row panel-body">
                    <div class="col-md-5">
                        <div class="panel panel-default extensible-panel table-responsive">
                            <div class="panel-heading">
                                <span class="glyphicon glyphicon-list"></span>Configuração
                                <div class="pull-right action-buttons">
                                    <div class="btn-group pull-right">
                                        <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">
                                        <span class="glyphicon glyphicon-cog" style="margin-right: 0px;"></span>
                                        </button>
                                        <ul class="dropdown-menu slidedown sortable">
                                            <li><a class="clickable handler-add-select"><span class="glyphicon glyphicon-plus-sign"></span>Adicionar</a></li>
                                            <li><a class="trash clickable"><span class="glyphicon glyphicon-trash"></span>Deletar</a></li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                            <div class="panel-body">
                                
                                <div class="list-group object-list-div">
                                </div>
                                <div class="text-center handler-add-select clickable"><span class="glyphicon glyphicon-plus-sign add-glyph"></span></div>
                                <div class="handler-template-obj list-group-item" style="display:none">
                                        
                                        <input type="checkbox" class="checkbox"/>
                                        <label for="checkbox" class="clickable handler-id-wmsId"></label>
                                        
                                        <div class="pull-right action-buttons">
                                            <a href="#" class="handler-load-current"><span class="glyphicon glyphicon-pencil"></span></a>
                                            <a href="#" class="trash handler-delete-selected"><span class="glyphicon glyphicon-trash"></span></a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            
                            
                            <div class="panel panel-default extensible-panel object-div table-responsive">
                                <div class="panel-heading input-group">
                                        <!--a class="input-group-addon trash clickable handler-delete-current"><span class="glyphicon glyphicon-trash"></span></a-->
                                        <input type="text" class="pull-left form-control handler-object-wmsId handler-field" placeholder="wmsId"/>
                                        <span class="input-group-addon clickable handler-add-current handler-add-save">Adicionar</span>
                                </div>
                                <div class="panel-body">
                                    <div class="input-group">
                                        <label for="handler-object-name" class="input-group-addon">Nome:</label>
                                        <input type="text" class="form-control handler-object-name handler-field" placeholder="Nome"/>
                                    </div>
                                    <div class="input-group">
                                        <label for="handler-object-name" class="input-group-addon">Estilo:</label>
                                        <input type="text" class="form-control handler-object-style handler-field" placeholder="Estilo">
                                    </div>
                                    <div class="input-group">
                                        <label for="handler-object-name" class="input-group-addon">Opacidade:</label>
                                        <input type="" class="form-control handler-object-opacity handler-field" placeholder="Opacidade">
                                    </div>     
                                    <div class="panel panel-danger">
                                        <div class="panel-heading slidable clickable">
                                            Features
                                        </div>
                                        <div class="panel-body">
                                            <div class="handler-object-features list-group"></div>
                                            <div class="text-center handler-nested-add-select-features clickable"><span class="glyphicon glyphicon-plus-sign add-glyph"></span></div>
                                            <div class=""></div>
                                            <div class="handler-nested-template-features list-group-item" style="display:none">
                                                <label class="number-order-placeholder"></label><span></span>
                                                <a class="trash handler-nested-delete-current clickable pull-right"><span class="glyphicon glyphicon-trash"></span></a>
                                                <div class="input-group">
                                                    <span for="handler-object-features-wmsId" class="input-group-addon">wmsId:</span>
                                                    <input type="text" class="form-control handler-object-features-wmsId handler-field" placeholder="wmsId"/>
                                                </div>
                                                <div class="input-group">
                                                    <span for="handler-object-features-wmsId" class="input-group-addon">Nome:</span>
                                                    <input type="text" class="form-control handler-object-features-name handler-field" placeholder="Nome"/>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
            </div>   
            <!--ACCESS-LEVEL-->
            <div id="access-level-panel" class="panel panel-default">
                <div class="panel-heading clickable">
                    <span class="glyphicon glyphicon-list"></span>Níveis de Acesso
                </div>
                <div class="row panel-body">
                    <div class="col-md-5">
                        <div class="panel panel-default extensible-panel table-responsive">
                            <div class="panel-heading">
                                <span class="glyphicon glyphicon-list"></span>Configuração
                                <div class="pull-right action-buttons">
                                    <div class="btn-group pull-right">
                                        <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">
                                        <span class="glyphicon glyphicon-cog" style="margin-right: 0px;"></span>
                                        </button>
                                        <ul class="dropdown-menu slidedown sortable">
                                            <li><a class="clickable handler-add-select"><span class="glyphicon glyphicon-plus-sign"></span>Adicionar</a></li>
                                            <li><a class="trash clickable"><span class="glyphicon glyphicon-trash"></span>Deletar</a></li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                            <div class="panel-body">
                                <div class="list-group object-list-div">
                                    
                                </div>
                                <div class="text-center handler-add-select clickable"><span class="glyphicon glyphicon-plus-sign add-glyph"></span></div>
                                <div class="handler-template-obj list-group-item" style="display:none">
                                        
                                        <input type="checkbox" class="checkbox"/>
                                        <label for="checkbox" class="clickable handler-id-name"></label>
                                        
                                        <div class="pull-right action-buttons">
                                            <a href="#" class="handler-load-current"><span class="glyphicon glyphicon-pencil"></span></a>
                                            <a href="#" class="trash handler-delete-selected"><span class="glyphicon glyphicon-trash"></span></a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="panel panel-default extensible-panel object-div table-responsive">
                                <div class="panel-heading input-group">
                                        <input type="text" class="pull-left form-control handler-object-name handler-field" placeholder="name"/>
                                        <span class="input-group-addon clickable handler-add-current handler-add-save">Adicionar</span>
                                </div>
                                <div class="panel-body">    
                                    <div class="panel panel-danger">
                                        <div class="panel-heading slidable clickable">
                                            Camadas
                                        </div>
                                        <div class="panel-body">
                                            <div class="handler-object-layers list-group"></div>
                                            <div class="text-center handler-nested-add-select-layers clickable"><span class="glyphicon glyphicon-plus-sign add-glyph"></span></div>
                                            <div class=""></div>
                                            <div class="handler-nested-template-layers list-group-item" style="display:none">
                                                <a class="trash handler-nested-delete-current clickable"><span class="glyphicon glyphicon-trash"></span></a>
                                                <div class="input-group">
                                                    <span for="handler-object-layers-wmsId" class="input-group-addon">wmsId</span>
                                                    <input type="text" class="form-control handler-object-layers-wmsId handler-field" placeholder="wmsId"/>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
            </div>   
            
            <!--USERS-->
            
            <div id="users-panel" class="panel panel-default">
                <div class="panel-heading clickable">
                    <span class="glyphicon glyphicon-list"></span>Usuários
                </div>
                <div class="row panel-body">
                    <div class="col-md-5">
                        <div class="panel panel-default extensible-panel table-responsive">
                            <div class="panel-heading">
                                <span class="glyphicon glyphicon-list"></span>Configuração
                                <div class="pull-right action-buttons">
                                    <div class="btn-group pull-right">
                                        <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">
                                        <span class="glyphicon glyphicon-cog" style="margin-right: 0px;"></span>
                                        </button>
                                        <ul class="dropdown-menu slidedown sortable">
                                            <li><a class="clickable handler-add-select"><span class="glyphicon glyphicon-plus-sign"></span>Adicionar</a></li>
                                            <li><a class="trash clickable"><span class="glyphicon glyphicon-trash"></span>Deletar</a></li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                            <div class="panel-body">
                                <div class="list-group object-list-div">
                                    
                                </div>
                                <div class="text-center handler-add-select clickable"><span class="glyphicon glyphicon-plus-sign add-glyph"></span></div>
                                <div class="handler-template-obj list-group-item" style="display:none">
                                        
                                        <input type="checkbox" class="checkbox"/>
                                        <label for="checkbox" class="clickable handler-id-username"></label>
                                        
                                        <div class="pull-right action-buttons">
                                            <a href="#" class="handler-load-current"><span class="glyphicon glyphicon-pencil"></span></a>
                                            <a href="#" class="trash handler-delete-selected"><span class="glyphicon glyphicon-trash"></span></a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="panel extensible-panel panel-default object-div table-responsive">
                                <div class="panel-heading input-group">
                                        <input type="text" class="pull-left form-control handler-object-username handler-field" placeholder="username"/>
                                        <span class="input-group-addon clickable handler-add-current handler-add-save">Adicionar</span>
                                </div>
                                <div class="panel-body">    
                                    
                                    <div class="input-group">
                                        <label for="handler-object-name" class="input-group-addon">Nome:</label>
                                        <input type="text" class="form-control handler-object-name handler-field" placeholder="Nome"/>
                                    </div>
                                    <div class="input-group">
                                        <label for="handler-object-name" class="input-group-addon">Senha</label>
                                        <input type="text" class="form-control handler-object-password handler-field" placeholder="******">
                                    </div>
                                    <div class="input-group">
                                        <label for="handler-object-name" class="input-group-addon">Email</label>
                                        <input type="text" class="form-control handler-object-email handler-field" placeholder="email">
                                    </div>     
                                    <div class="input-group">
                                        <label for="handler-object-name" class="input-group-addon">Telefone</label>
                                        <input type="text" class="form-control handler-object-telnumber handler-field" placeholder="telefone">
                                    </div>
                                    <div class="panel panel-danger">
                                        <div class="panel-heading">
                                            Nível de Acesso
                                        </div>
                                        <div class="panel-body">
                                            <div class="input-group">
                                                <label for="handler-object-accessLevel" class="input-group-addon">Nome:</label>
                                                <input type="text" class="form-control handler-object-accessLevel handler-field" placeholder="nome">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
            </div>  
            
            <!-- IMPORT-EXPORT -->
            
            <div class="col-xs-8 col-xs-offset-2 col-md-4 col-md-offset-4 btn-group">
                <button id="import-configuration" class="btn btn-lg btn-success col-xs-6">Importar</button>
                <button id="export-configuration" class="btn btn-lg btn-info col-xs-6">Exportar</button>
            </div>
            
            <!-- MESSAGE-CONTAINER-->
            
            <div id="message-container">
                <div class="message-body alert" style="display:none">
                    <strong><span class="message-title"></span></strong>
                    <span class="message-text"></span>
                    <button type="button" class="close" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                </div>
            </div>

            <!-- ModalBox -->
            <div id="modal-prompt" class="modal fade" role="dialog">
              <div class="modal-dialog modal-sm">

                <!-- Modal content-->
                <div class="modal-content">
                  <div class="modal-header" style="display:none">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title"></h4>
                  </div>
                  <div class="modal-body">
                    
                  </div>
                  <div class="modal-footer">
                    <button type="button" class="btn btn-default modal-cancel" data-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary modal-confirm" data-dismiss="modal">Confirmar</button>
                  </div>
                </div>

              </div>
            </div>
        </div>
    </body>
</html>
