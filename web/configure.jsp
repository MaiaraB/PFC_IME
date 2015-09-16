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
        
        <script type="text/javascript">
            $(document).ready(function(){
                //Create Namespace
                var WMSCRUD = WMSCRUD || {};
                
                WMSCRUD.ObjectHandler = function(){
                    this.ACTIONS = {DELETE : "delete",READALL : "readAll",SAVE: "update",ADD: "add"};
                    this.ACTIONSUBJECTSTATE = {UNDEFINED : "UNDEFINED",OK : "OK", ERROR : "ERROR",TIMEOUT : "TIMEOUT"};
                    this.DATARESPONSESTATE = {OK : "OK",ERROR : "ERROR"};
                    this.REQUESTTIMEOUT = 3000;
                    this.viewIdPreffix = "handler";
                    this.mappingObject = this.mapping();
                    
                    this.url = "";
                    this.idField = "";
                    this.objectDiv = $();
                    this.objectListDiv = $();
                    
                };
                
                WMSCRUD.ObjectHandler.prototype.attach = function(actionSubject,actionObserver){
                    actionSubject.observers = actionSubject.observers || [];
                    actionSubject.observers.push(actionObserver);
                };
                
                WMSCRUD.ObjectHandler.prototype.detach = function(actionSubject,actionObserver){
                    if(typeof actionSubject.observers !== 'undefined'){
                        var i = 0, ii = actionSubject.observers.length;
                        for(;i<ii;i++){
                            if(actionSubject.observers[i] === actionObserver){
                                actionSubject.observers.splice(i,1);
                            }
                        }
                    }
                };
                
                WMSCRUD.ObjectHandler.prototype.notify = function(actionSubject){
                    if(typeof actionSubject.observers !== 'undefined'){
                        var i =0,ii=actionSubject.observers.length;
                        for(;i<ii;i++){
                            var observer = actionSubject.observers[i];
                            if(typeof actionSubject.state === 'undefined'){
                                actionSubject.state = this.ACTIONSUBJECTSTATE.UNDEFINED;
                            }
                            observer(actionSubject.state);
                        }
                    }
                };
                
                WMSCRUD.ObjectHandler.prototype.requestAction = function(requestURL,actionSubject,data,successCallBack){
                    actionSubject.state = this.ACTIONSUBJECTSTATE.UNDEFINED;
                    var self = this;
                    $.ajax({
                       type : 'POST',
                       url : requestURL,
                       async : true,
                       data : data || "",
                       timeout : self.REQUESTTIMEOUT
                    }).done(function(data){
                       actionSubject.state = self.ACTIONSUBJECTSTATE.OK;
                       successCallBack(self,data);
                    }).fail(function(jqXHR,textStatus){
                       if(textStatus === 'timeout'){
                           actionSubject.state = self.ACTIONSUBJECTSTATE.TIMEOUT;
                       }
                       else{
                           actionSubject.state = self.ACTIONSUBJECTSTATE.ERROR;
                       }
                    }).always(function(){
                       self.notify(actionSubject); 
                    });
                };
                
                WMSCRUD.ObjectHandler.prototype.readObjs = function(){
                    this.loadObjsList.state = this.ACTIONSUBJECTSTATE.UNDEFINED;
                    var loadURL = this.url + "?action=" + this.ACTIONS.READALL;
                    this.requestAction(loadURL,this.readObjs,"",this.loadObjsList,this.failCallBack);
                };
                
                WMSCRUD.ObjectHandler.prototype.loadObjsList = function(self,data){
                    if(typeof data.objects !== 'undefined' && data.objects.constructor === Array){
                        self.loadedObjects = data.objects;
                    }
                    else{
                        return;
                    }
                    
                    if(typeof self.objectListDiv === 'undefined'){
                        console.log("Panel of Objects is undefined");
                        return;
                    }
                    if((typeof self.objectListDiv !== 'undefined') && (!(self.objectListDiv instanceof jQuery))){
                        self.objectListDiv = $(self.objectListDiv);
                    }
                    self.objectListDiv.empty();
                    var templateElement = self.objectListDiv.siblings("." + self.viewIdPreffix + "-template-obj").first();
                    if(typeof templateElement !== 'undefined' && templateElement.length !==0){
                        var i=0,ii=self.loadedObjects.length;
                        for(;i<ii;i++){
                            var loadedObject = self.loadedObjects[i];
                            if(self.matchMapping(self,loadedObject)){
                               var newElement = templateElement.clone(); 
                               if(self.idField in loadedObject){
                                   
                                  newElement.get(0).loadedObject = loadedObject;
                                  newElement.find("."+self.viewIdPreffix+"-id-"+self.idField) 
                                          .text(loadedObject[self.idField]);    
                                  newElement.show();
                                  self.objectListDiv.append(newElement);
                                  
                               }
                            }
                        }
                    }
                    else{
                        console.log("No template element found");
                        return;
                    }
                };
                
                WMSCRUD.ObjectHandler.prototype.loadObj = function(self,loadedObject){
                    self.ACTIONSUBJECTSTATE.UNDEFINED;
                    if(!typeof self.objectDiv === 'undefined'){
                        self.loadObj.state = self.ACTIONSUBJECTSTATE.ERROR;
                    }
                    else{
                        self.objectDiv.loadedObject = loadedObject;
                        self.objectDiv.show();
                        for(var key in loadedObject){
                            if(key !== null){
                                var objectKeySelector = "." + self.viewIdPreffix + "-object-"+key;
                                if($.isArray(loadedObject[key])){
                                    var nestedObjectsArray = loadedObject[key];
                                    var templateView = self.objectDiv.find("."+self.viewIdPreffix+"-nested-template-"+key).first();
                                    var nestedObjectView = self.objectDiv.find(objectKeySelector).first();
                                    nestedObjectView.empty();
                                    if(nestedObjectsArray.length > 0){
                                       if(templateView.length > 0 && nestedObjectView.length > 0){
                                           var i = 0,ii=nestedObjectsArray.length;
                                           for(;i<ii;i++){
                                                var nestedObject = nestedObjectsArray[i];
                                                var newElement = templateView.clone();
                                                for(var param in nestedObject){
                                                   newElement.find(objectKeySelector+"-"+param).val(nestedObject[param]);
                                                }
                                                newElement.show();
                                                nestedObjectView.append(newElement);
                                           }
                                       }

                                    }
                                }
                                else{
                                    var viewElement = self.objectDiv.find(objectKeySelector).first();
                                    viewElement.val('');
                                    viewElement.val(loadedObject[key]);
                                }
                            }
                        }
                        self.loadObj.state = self.ACTIONSUBJECTSTATE.OK;
                    }
                    self.notify(self.loadObj);
                };
                
                WMSCRUD.ObjectHandler.prototype.add = function(){
                    var self = this;
                    this.save.state = this.ACTIONSUBJECTSTATE.UNDEFINED;
                    var objectSave = this.initializeObject(self);
                    if(!this.obeyRestrictions(objectSave)){
                        console.log("Object does not obey restrictions");
                        this.save.state = this.ACTIONSUBJECTSTATE.ERROR;
                        this.notify(this.save);
                        return;
                    }
                    
                    //Check if mapping corresponds to jsonObject
                    if(!this.matchMapping(self,objectSave)){
                        console.log("Object does not match mapping type");
                        this.save.state = this.ACTIONSUBJECTSTATE.ERROR;
                        self.notify(self.save);
                    }
                    var saveURL = this.url + "?action=" + this.ACTIONS.ADD;
                    var successCallBack = function(data){
                        if(typeof data !== 'undefined' && data === self.DATARESPONSESTATE.OK){
                            self.save.state = self.ACTIONSUBJECTSTATE.OK;
                        }
                        else{
                            self.save.state = self.ACTIONSUBJECTSTATE.ERROR;
                        }
                    };
                    
                    this.requestAction(saveURL,self.save,objectSave,successCallBack);
                };
                
                WMSCRUD.ObjectHandler.prototype.save = function(){
                    var self = this;
                    this.save.state = this.ACTIONSUBJECTSTATE.UNDEFINED;
                    var objectSave = this.initializeObject(self);
                    if(!this.obeyRestrictions(objectSave)){
                        console.log("Object does not obey restrictions");
                        this.save.state = this.ACTIONSUBJECTSTATE.ERROR;
                        this.notify(this.save);
                        return;
                    }
                    
                    //Check if mapping corresponds to jsonObject
                    if(!this.matchMapping(self,objectSave)){
                        console.log("Object does not match mapping type");
                        this.save.state = this.ACTIONSUBJECTSTATE.ERROR;
                        self.notify(self.save);
                    }
                    var saveURL = this.url + "?action=" + this.ACTIONS.SAVE;
                    var successCallBack = function(data){
                        if(typeof data !== 'undefined' && data === self.DATARESPONSESTATE.OK){
                            self.save.state = self.ACTIONSUBJECTSTATE.OK;
                        }
                        else{
                            self.save.state = self.ACTIONSUBJECTSTATE.ERROR;
                        }
                    };
                    
                    this.requestAction(saveURL,self.save,objectSave,successCallBack);
                };
                
                WMSCRUD.ObjectHandler.prototype.delete = function(objId){
                    var self = this;
                    self.delete.state = this.ACTIONSUBJECTSTATE.UNDEFINED;        
                    var deleteURL = this.url + "?action=" + this.ACTIONS.DELETE + "&" + this.idField + "=" + objId;
                    var successCallBack = function(data){
                        if(typeof data !== 'undefined' && data === self.DATARESPONSESTATE.OK){
                            self.delete.state = self.ACTIONSUBJECTSTATE.OK;
                        }
                        else{
                            self.delete.state = self.ACTIONSUBJECTSTATE.ERROR;
                        }
                    };
                    this.requestAction(deleteURL,self.delete,"",successCallBack);
                };
                
                WMSCRUD.ObjectHandler.prototype.deleteCurrent = function(){
                    var deleteId = this.objectDiv.find("."+this.viewIdPreffix+"-object-"+this.idField).first();
                    this.delete(deleteId);
                };
                
                WMSCRUD.ObjectHandler.prototype.matchMapping = function(self,matchObject){
                    for(var key in self.mappingObject){
                        if(!(key in matchObject)){
                            //Internal Error
                            console.log("Object does not contain"+key);
                            return false;
                        }
                    }
                    return true;
                };
                
                WMSCRUD.ObjectHandler.prototype.obeyRestrictions = function(self,matchObject){
                    return true;
                };
                
                WMSCRUD.ObjectHandler.prototype.initializeObject = function(self){
                    var objectInit = {};
                    if(typeof self.objectDiv !== 'undefined' && self.objectDiv.length > 0){ 
                        for(var key in self.mappingObject){
                            if(key!==null){
                                if($.isArray(self.mappingObject[key])){
                                    objectInit[key] = [];
                                    if(self.mappingObject[key].length > 0){
                                        var templateNested = self.mappingObject[key][0];
                                        var nestedSelector = "."+self.viewIdPreffix+"-object-"+key;
                                        var templateSelector = "."+self.viewIdPreffix+"-nested-template-"+key;
                                        var nestedObjectsView = self.objectDiv.find(nestedSelector);
                                        var nestedObjectsViewElements = nestedObjectsView.find(templateSelector);
                                        nestedObjectsViewElements.each(function(index){
                                            var nestedObject = {};
                                            for(var param in templateNested){
                                                nestedObject[param] = $(this).find(nestedSelector+"-"+param).first().val();
                                            }
                                            objectInit[key].push(nestedObject);
                                        });
                                    }
                                }
                                else{
                                    objectInit[key] = self.objectDiv.find("."+self.viewIdPreffix+"-object-"+key).first().val();
                                }
                            }
                                
                        }
                    }
                    return objectInit;
                };
                
                WMSCRUD.ObjectHandler.prototype.mapping = function(objId){
                    this.mappingObject = {};
                };
                
                function LayersHandler(objectListDiv,objectDiv){
                    WMSCRUD.ObjectHandler.call(this);
                    this.idField = "wmsId";
                    this.url = "layer-handler";
                    this.objectDiv = objectDiv;
                    this.objectListDiv = objectListDiv;
                };
                LayersHandler.prototype = Object.create(WMSCRUD.ObjectHandler.prototype);
                LayersHandler.constructor = LayersHandler;
                LayersHandler.prototype.mapping = function(){
                    return { name : "",
                        wmsId : "",style : "",opacity : "",features : [{name : "",wmsId :""}]};
                };
                LayersHandler.prototype.obeyRestrictions = function(self,matchObject){
                    return true;
                };
                
                var layersPanel = $("#layers-panel");
                var layersPanelHeading = layersPanel.find(".panel-heading").first();
                var layersPanelBody = layersPanel.find(".panel-body").first();
                var layersObjectListDiv = layersPanel.find(".object-list-div").first();
                var layersObjectDiv = layersPanel.find(".object-div").first();
                var layersAddButton = layersPanel.find(".handler-add-button").first();
                var layersSaveButton = layersPanel.find(".handler-save-button").first();
                var layersDeleteButton = layersPanel.find(".handler-delete-button").first();
                var layerHandler = new LayersHandler(layersObjectListDiv,layersObjectDiv);
                
                layersPanelBody.hide();
                layersPanelHeading.click(function(event){
                   if(!layersPanelBody.is(":visible")){
                     layerHandler.readObjs();
                   }
                   else{
                     layersObjectDiv.hide();
                   }
                   layersPanelBody.slideToggle(300); 
                });
                
                layersAddButton.click(function(event){
                    layerHandler.add();
                });
                layersSaveButton.click(function(event){
                    layerHandler.save();
                });
                layersDeleteButton.click(function(event){
                   layerHandler.deleteCurrent(); 
                });
                layerHandler.attach(layerHandler.loadObj,function(state){
                    layersSaveButton.show();
                    layersAddButton.hide();
                });
                
                layerHandler.attach(layerHandler.readObjs,function(state){
                    if(state === layerHandler.ACTIONSUBJECTSTATE.OK){
                        var clickFunction = function(event){
                            var loadedObject = $(this).parents(".handler-template-obj").first().get(0).loadedObject;
                            if(typeof loadedObject === 'undefined'){
                                console.log("Internal error, no object to load");
                            }
                            else{
                                layerHandler.loadObj(layerHandler,loadedObject);
                            }  
                        };
                        var layerIds = layerHandler.objectListDiv.find(".handler-id-wmsId");
                        layerIds.click(clickFunction);
                        $.each(layerIds,function(){
                            $(this).parent().find(".glyphicon-pencil").click(clickFunction);
                        });
                        
                    }
                });
            });
        </script>
        <style>
            .clickable{
                cursor : pointer;
            }
            .trash { color:rgb(209, 91, 71); }
            .okglyph {font-size : 25px;color: #006699}
            .panel-footer .pagination { margin: 0; }
            .panel .glyphicon,.list-group-item .glyphicon { margin-right:5px; }
            .panel-body .radio, .checkbox { display:inline-block;margin:0px; }
            .panel-body input[type=checkbox]:checked + label { text-decoration: line-through;color: rgb(128, 144, 160); }
            .list-group-item:hover, a.list-group-item:focus {text-decoration: none;background-color: rgb(245, 245, 245);}
            .list-group { margin-bottom:0px; }
            .input-group {margin-bottom:15px;}
            .glyphicon-plus-sign {font-size : 35px;color: #009966}
        </style>
    </head>
    <body>
        <div class="container container-fluid">
            <div id="layers-panel" class="panel panel-success">
                <div class="panel-heading clickable">
                    <span class="glyphicon glyphicon-list"></span>Camadas
                </div>
                <div class="row panel-body">
                    <div class="col-md-5">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <span class="glyphicon glyphicon-list"></span>Configuração
                                <div class="pull-right action-buttons">
                                    <div class="btn-group pull-right">
                                        <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">
                                        <span class="glyphicon glyphicon-cog" style="margin-right: 0px;"></span>
                                        </button>
                                        <ul class="dropdown-menu slidedown sortable">
                                            <li><a href="" class="trash"><span class="glyphicon glyphicon-trash"></span>Deletar</a></li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                            <div class="panel-body">
                                <div class="list-group object-list-div">
                                    
                                </div>
                                <div class="text-center handler-add-select clickable"><span class="glyphicon glyphicon-plus-sign"></span></div>
                                <div class="handler-template-obj list-group-item" style="display:none">
                                        
                                        <input type="checkbox" class="checkbox"/>
                                        <label for="checkbox" class="clickable handler-id-wmsId"></label>
                                        
                                        <div class="pull-right action-buttons">
                                            <a href="#"><span class="glyphicon glyphicon-pencil"></span></a>
                                            <a href="#" class="trash"><span class="glyphicon glyphicon-trash"></span></a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="panel panel-default object-div">
                                <div class="panel-heading">
                                        <input type="text" class="pull-left form-control handler-object-wmsId" placeholder="wmsId" style="max-width:90%;"/>
                                        <a href="#" class="okglyph"><span class="glyphicon glyphicon-ok-sign"></span></a>
                                </div>
                                <div class="panel-body">
                                    <div class="input-group">
                                        <label for="handler-object-name" class="input-group-addon">Nome:</label>
                                        <input type="text" class="form-control handler-object-name" placeholder="Nome"/>
                                    </div>
                                    <div class="input-group">
                                        <label for="handler-object-name" class="input-group-addon">Estilo:</label>
                                        <input type="text" class="form-control handler-object-style" placeholder="Estilo">
                                    </div>
                                    <div class="input-group">
                                        <label for="handler-object-name" class="input-group-addon">Opacidade:</label>
                                        <input type="" class="form-control handler-object-opacity" placeholder="Opacidade">
                                    </div>     
                                    <div class="panel panel-primary">
                                        <div class="panel-heading">
                                            Features
                                        </div>
                                        <div class="panel-body">
                                            <div class="handler-object-features list-group"></div>
                                            <div class="text-center handler-objects-features-add-select clickable"><span class="glyphicon glyphicon-plus-sign"></span></div>
                                            <div class=""></div>
                                            <div class="handler-nested-template-features list-group-item" style="display:none">
                                                <a href="" class="trash"><span class="glyphicon glyphicon-trash"></span></a>
                                                <div class="input-group">
                                                    <span for="handler-object-features-wmsId" class="input-group-addon">wmsId:</span>
                                                    <input type="text" class="form-control handler-object-features-wmsId" placeholder="wmsId"/>
                                                </div>
                                                <div class="input-group">
                                                    <span for="handler-object-features-wmsId" class="input-group-addon">Nome:</span>
                                                    <input type="text" class="form-control handler-object-features-name" placeholder="Nome"/>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="panel-footer">
                                <button class="text-capitalize handler-add-button">Adicionar</button>
                                <button class="text-capitalize handler-save-button" style="display:none">Salvar</button>
                                <button class="text-capitalize handler-delete-button">Deletar</button>
                            </div>
                        </div>
                    </div>
                    
                    
                    
                </div>

            </div>
        </div>
        
                    
    </body>
</html>
