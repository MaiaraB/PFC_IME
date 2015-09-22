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
        <!--Bootbox-->
        <script src="${pageContext.request.contextPath}/resources/js/bootbox.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function(){
                //Create Namespace
                var WMSCRUD = WMSCRUD || {};
                
                WMSCRUD.ObjectHandler = function(){
                    this.ACTIONS = {DELETE : "delete",READALL : "readAll",SAVE: "update",ADD: "add"};
                    this.ACTIONSUBJECTSTATE = {UNDEFINED : "UNDEFINED",OK : "OK", 
                        ERROR : "ERROR",TIMEOUT : "TIMEOUT",DUPLICATE : "DUPLICATE",RESTRICTIONVIOLATION : "RESTRICTION"};
                    this.DATARESPONSESTATE = {OK : "OK",ERROR : "ERROR"};
                    this.REQUESTTIMEOUT = 3000;
                    this.viewIdPreffix = "handler";
                    this.mappingObject = this.mapping();
                    
                    this.url = "";
                    this.idField = "";
                    this.objectDiv = $();
                    this.objectListDiv = $();
                    this.observers = {};
                };
                
                WMSCRUD.ObjectHandler.prototype.attach = function(actionSubject,actionObserver){
                    this.observers[actionSubject] = this.observers[actionSubject] || [];
                    //actionSubject.observers = actionSubject.observers || [];
                    //actionSubject.observers.push(actionObserver);
                    this.observers[actionSubject].push(actionObserver);
                };
                
                WMSCRUD.ObjectHandler.prototype.detach = function(actionSubject,actionObserver){
                    if(typeof actionSubject.observers !== 'undefined'){
                        var i = 0, ii = this.observers[actionSubject].length;
                        for(;i<ii;i++){
                            if(this.observers[actionSubject][i] === actionObserver){
                                this.observers[actionSubject].splice(i,1);
                            }
                        }
                    }
                };
                
                WMSCRUD.ObjectHandler.prototype.notify = function(actionSubject){
                    var observers = this.observers[actionSubject];
                    if(typeof observers !== 'undefined'){
                        var i =0,ii=observers.length;
                        for(;i<ii;i++){
                            var observer = observers[i];
                            if(typeof actionSubject.state === 'undefined'){
                                actionSubject.state = this.ACTIONSUBJECTSTATE.UNDEFINED;
                            }
                            observer(actionSubject.state);
                        }
                    }
                };
                
                WMSCRUD.ObjectHandler.prototype.requestAction = function(requestURL,actionSubject,dataToSend,successCallBack){
                    actionSubject.state = this.ACTIONSUBJECTSTATE.UNDEFINED;
                    var self = this;
                    $.ajax({
                       type : 'POST',
                       url : requestURL,
                       async : true,
                       data : dataToSend || "",
                       timeout : self.REQUESTTIMEOUT
                    }).done(function(data,textStatus,jqXHR){
                        console.log(data);
                       actionSubject.state = self.ACTIONSUBJECTSTATE.OK;
                       successCallBack(self,data,textStatus,jqXHR);
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
                                    var templateClass = self.viewIdPreffix+"-nested-template-"+key;
                                    var templateView = self.objectDiv.find("."+ templateClass).first();
                                    var nestedObjectView = self.objectDiv.find(objectKeySelector).first();
                                    nestedObjectView.empty();
                                    if(nestedObjectsArray.length > 0){
                                       if(templateView.length > 0 && nestedObjectView.length > 0){
                                           var i = 0,ii=nestedObjectsArray.length;
                                           for(;i<ii;i++){
                                                var nestedObject = nestedObjectsArray[i];
                                                var newElement = templateView.clone();
                                                newElement.removeClass(templateClass);
                                                newElement.addClass(self.viewIdPreffix+"-nested-copy-"+key);
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
                    this.add.state = this.ACTIONSUBJECTSTATE.UNDEFINED;
                    var objectSave = this.initializeObject(self);
                    self.objectSave = objectSave;
                    if(!this.obeyRestrictions(objectSave)){
                        console.log("Object does not obey restrictions");
                        this.add.state = this.ACTIONSUBJECTSTATE.RESTRICTIONVIOLATION;
                        this.notify(self.add);
                        return;
                    }
                    
                    //Check if mapping corresponds to jsonObject
                    if(!this.matchMapping(self,objectSave)){
                        console.log("Object does not match mapping type");
                        this.add.state = this.ACTIONSUBJECTSTATE.ERROR;
                        self.notify(self.save);
                    }
                    var saveURL = this.url + "?action=" + this.ACTIONS.ADD;
                    var successCallBack = function(self,data){
                        if(typeof data !== 'undefined' && data === self.DATARESPONSESTATE.OK){
                            self.add.state = self.ACTIONSUBJECTSTATE.OK;
                        }
                        else{
                            self.add.state = self.ACTIONSUBJECTSTATE.ERROR;
                        }
                    };
                    
                    this.requestAction(saveURL,self.add,objectSave,successCallBack);
                };
                
                WMSCRUD.ObjectHandler.prototype.save = function(){
                    var self = this;
                    this.save.state = this.ACTIONSUBJECTSTATE.UNDEFINED;
                    var objectSave = this.initializeObject(self);
                    self.objectSave = objectSave;
                    if(!this.obeyRestrictions(objectSave)){
                        console.log("Object does not obey restrictions");
                        this.save.state = this.ACTIONSUBJECTSTATE.RESTRICTIONVIOLATION;
                        this.notify(self.save);
                        console.log(this.observers[self.save]);
                        return;
                    }
                    
                    //Check if mapping corresponds to jsonObject
                    if(!this.matchMapping(self,objectSave)){
                        console.log("Object does not match mapping type");
                        this.save.state = this.ACTIONSUBJECTSTATE.ERROR;
                        self.notify(self.save);
                    }
                    var saveURL = this.url + "?action=" + this.ACTIONS.SAVE;
                    var successCallBack = function(self,data){
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
                    var successCallBack = function(self,data){
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
                                        var templateSelector = "."+self.viewIdPreffix+"-nested-copy-"+key;
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
                
                WMSCRUD.ObjectHandler.prototype.clearObjectDiv = function(self){
                    if(typeof self === 'undefined'){
                        var self = this;
                    }
                    if(typeof self.objectDiv !== 'undefined' && self.objectDiv.length > 0){ 
                        for(var key in self.mappingObject){
                            var selector = "."+self.viewIdPreffix+"-object-"+key;
                            if(key!==null){
                                if($.isArray(self.mappingObject[key])){
                                        var nestedObjectsView = self.objectDiv.find(selector);
                                        nestedObjectsView.empty();
                                    }
                                else{
                                    self.objectDiv.find(selector).val("");
                                }
                            }
                        }      
                    }
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
                    this.saveState = true;
                };
                LayersHandler.prototype = Object.create(WMSCRUD.ObjectHandler.prototype);
                LayersHandler.constructor = LayersHandler;
                LayersHandler.prototype.mapping = function(){
                    return { name : "",
                        wmsId : "",style : "",opacity : "",features : [{name : "",wmsId :""}]};
                };
                LayersHandler.prototype.obeyRestrictions = function(matchObject){
                    if(typeof matchObject === 'undefined'){
                        return false;
                    }
                    if(matchObject['wmsId'] === ""){
                        return false;
                    }
                    try{
                        if(matchObject['opacity'] === ""){
                            return false;
                        }
                        var opacity = parseFloat(matchObject['opacity']);
                        if(opacity < 0 || opacity > 1){
                            return false;
                        }
                    }
                    catch(err){
                        return false;
                    }
                    var i = 0,ii=matchObject['features'].length;
                    for(;i<ii;i++){
                        var feature = matchObject['features'][i];
                        if(feature['wmsId'] === ''){
                            return false;
                        }
                    }
                    return true;
                };
                
                function AccessLevelHandler(objectListDiv,objectDiv){
                    WMSCRUD.ObjectHandler.call(this);
                    this.idField = "name";
                    this.url = "access-level-handler";
                    this.objectDiv = objectDiv;
                    this.objectListDiv = objectListDiv;
                    this.saveState = true;
                };
                AccessLevelHandler.prototype = Object.create(WMSCRUD.ObjectHandler.prototype);
                AccessLevelHandler.constructor = AccessLevelHandler;
                AccessLevelHandler.prototype.mapping = function(){
                    return { name : "",layers: [{wmsId :""}]};
                };
                AccessLevelHandler.prototype.obeyRestrictions = function(matchObject){
                    if(typeof matchObject === 'undefined'){
                        return false;
                    }
                    if(matchObject['name'] === ''){
                        return false;
                    }
                    var layers = matchObject['layers'];
                    var i = 0,ii=layers.length;
                    for(;i<ii;i++){
                        var layer = layers[i];
                        if(layer['wmsId']===''){
                            return false;
                        }
                    }
                    return true;
                };
                
                function UserHandler(objectListDiv,objectDiv){
                    WMSCRUD.ObjectHandler.call(this);
                    this.idField = "username";
                    this.url = "user-handler";
                    this.objectDiv = objectDiv;
                    this.objectListDiv = objectListDiv;
                    this.saveState = true;
                };
                UserHandler.prototype = Object.create(WMSCRUD.ObjectHandler.prototype);
                UserHandler.constructor = UserHandler;
                UserHandler.prototype.mapping = function(){
                    return { username : "",password: "", accessLevel : "",telephone : "",email: "",name:""};
                };
                UserHandler.prototype.obeyRestrictions = function(matchObject){
                    if(typeof matchObject === 'undefined'){
                        return false;
                    }
                    if(matchObject['username'] === ''){
                        return false;
                    }
                    if(matchObject['accessLevel'] === ''){
                        return false;
                    }
                    return true;
                };
                
                var initializeUIPanel = function(panel,HandlerClass,nestedElement){
                    var panelHeading = panel.find(".panel-heading").first();
                    var panelBody = panel.find(".panel-body").first();
                    var objectListDiv = panel.find(".object-list-div").first();
                    var objectDiv = panel.find(".object-div").first();
                    var addSelect = panel.find(".handler-add-select").first();
                    var nestedAddSelect = panel.find(".handler-nested-add-select-"+nestedElement);
                    var addSaveButton = panel.find(".handler-add-save").first();
                    var deleteButton = panel.find(".handler-delete-current").first();
                    var objectHandler = new HandlerClass(objectListDiv,objectDiv);
                
                    panelBody.hide();
                    panelHeading.click(function(event){
                       if(!panelBody.is(":visible")){
                         objectHandler.readObjs();
                       }

                       panelBody.slideToggle(300); 

                    });
                    var changeSaveAddView = function(){
                    if(objectHandler.saveState){
                        addSaveButton.removeClass("handler-add-current"); 
                        addSaveButton.addClass("handler-save-current");
                        addSaveButton.text("Salvar");
                    }
                    else{
                        addSaveButton.removeClass("handler-save-current");
                        addSaveButton.addClass("handler-add-current"); 
                        addSaveButton.text("Adicionar");
                    }
                };
                
                addSelect.click(function(event){
                    objectHandler.clearObjectDiv();
                    objectHandler.saveState = false;
                    changeSaveAddView();
                });
                
                var nestedElementDeleteFunction = function(){
                    var layersNestedDeleteCurrent = objectHandler.objectDiv.find(".handler-nested-delete-current");
                    $.each(layersNestedDeleteCurrent,function(index){
                        layersNestedDeleteCurrent.click(function(event){
                            $(this).parents(".handler-nested-copy-"+nestedElement).first().remove();
                        });
                    });
                };
                
                nestedAddSelect.click(function(event){
                    var handlerNested = objectHandler.objectDiv.find(".handler-object-"+nestedElement).first();
                    var template = objectHandler.objectDiv.find(".handler-nested-template-"+nestedElement).first();
                    var copy = template.clone();
                    copy.removeClass("handler-nested-template-"+nestedElement);
                    copy.addClass("handler-nested-copy-"+nestedElement);
                    copy.show();
                    handlerNested.append(copy);
                    nestedElementDeleteFunction();
                });
                
                addSaveButton.click(function(event){
                    if(objectHandler.saveState){
                        console.log("SAVE");
                        objectHandler.save();
                    }
                    else{
                        console.log("ADD");
                        objectHandler.add();
                    }
                });
                deleteButton.click(function(event){
                   objectHandler.deleteCurrent(); 
                });
                objectHandler.attach(objectHandler.loadObj,function(state){
                    nestedElementDeleteFunction();
                });
                
                objectHandler.attach(objectHandler.loadObj,function(state){
                    objectHandler.saveState = true;
                    changeSaveAddView();
                });
                
                objectHandler.attach(objectHandler.readObjs,function(state){
                    if(state === objectHandler.ACTIONSUBJECTSTATE.OK){
                        var loadObjFunction = function(event){
                            var loadedObject = $(this).parents(".handler-template-obj").first().get(0).loadedObject;
                            if(typeof loadedObject === 'undefined'){
                                console.log("Internal error, no object to load");
                            }
                            else{
                                objectHandler.loadObj(objectHandler,loadedObject);
                            }  
                        };
                        var deleteCurrentFunction = function(event){
                            var template= $(this).parents(".handler-template-obj").first();
                            var objId = template.find(".handler-id-"+objectHandler.idField).first().text();
                            objectHandler.delete.viewObj = template;
                            objectHandler.delete(objId);
                        };
                        var objectIds = objectHandler.objectListDiv.find(".handler-id-"+objectHandler.idField);
                        objectIds.click(loadObjFunction);
                        $.each(objectIds,function(){
                            var templateParent = $(this).parents(".handler-template-obj").first();
                            templateParent.find(".handler-load-current").click(loadObjFunction);
                            templateParent.find(".handler-delete-selected").click(deleteCurrentFunction);
                        });   
                    }
                });
                
                objectHandler.attach(objectHandler.delete,function(state){
                   if(state === objectHandler.ACTIONSUBJECTSTATE.OK){
                       if(typeof objectHandler.delete.viewObj !== 'undefined'){
                           $(objectHandler.delete.viewObj).remove();
                       }
                   } 
                });
                
                var addSaveActionSuccess = function(state){
                    if(state === objectHandler.ACTIONSUBJECTSTATE.OK){
                        if(typeof objectHandler.objectSave !== 'undefined' && typeof objectHandler.objectSave !== null){
                            objectHandler.objectSave = undefined;
                            objectHandler.readObjs();      
                        }
                    }
                };
                
                objectHandler.attach(objectHandler.add,addSaveActionSuccess);
                objectHandler.attach(objectHandler.save,addSaveActionSuccess);
                
                var messageContainer = $("#message-container");
                
                /*
                errorMessageContainer.draggable({
                   handler: errorMessageContainer.find(".modal-header") 
                });*/
                var alertMessage = function(title,message,importance){    
                    var messageView = messageContainer.find(".message-body").first().clone();
                    messageView.removeClass("alert-danger","alert-success","alert-info","alert-warning")
                            .addClass("alert-"+importance);
                    messageView.find(".message-title").text(title);
                    messageView.find(".message-body").text(message);
                    panel.find(".panel-heading").find(".message-body").remove();
                    messageView.appendTo(panelHeading);
                    messageView.fadeIn(300);
                };
                alertMessage.importance = {WARNING : 'warning',SUCCESS : 'success',INFO : 'info',DANGER : 'danger'};
                
                objectHandler.attach(objectHandler.add,function(state){
                    switch(state){
                        case objectHandler.ACTIONSUBJECTSTATE.OK:
                            alertMessage("Sucesso: ","Item adicionado com sucesso",alertMessage.importance.SUCCESS);
                            break;
                        case objectHandler.ACTIONSUBJECTSTATE.ERROR:
                            alertMessage("Erro: ","Um problema ocorreu ao processar sua requisição",alertMessage.importance.DANGER);
                            break;
                        case objectHandler.ACTIONSUBJECTSTATE.DUPLICATE:
                            alertMessage("Erro: ","Um item com mesmo identificador já foi adicionado",alertMessage.importance.DANGER);
                            break;
                        case objectHandler.ACTIONSUBJECTSTATE.RESTRICTIONVIOLATION:
                            alertMessage("Aviso: ","Não foi possível adicionar o item pois há campos preenchidos incorretamente",alertMessage.importance.WARNING);
                            break;
                        case objectHandler.ACTIONSUBJECTSTATE.TIMEOUT:
                            alertMessage("Erro: ","O servidor não respondeu à requisição",alertMessage.importance.DANGER);
                            break;
                        default:
                            break;
                    }
                });
                objectHandler.attach(objectHandler.save,function(state){
                    switch(state){
                        case objectHandler.ACTIONSUBJECTSTATE.OK:
                            alertMessage("Sucesso: ","O item foi salvo com sucesso",alertMessage.importance.SUCCESS);
                            break;
                        case objectHandler.ACTIONSUBJECTSTATE.ERROR:
                            alertMessage("Erro: ","Um problema ocorreu ao processar sua requisição",alertMessage.importance.DANGER);
                            break;
                        case objectHandler.ACTIONSUBJECTSTATE.RESTRICTIONVIOLATION:
                            alertMessage("Aviso: ","Não foi possível salvar o item pois há campos preenchidos incorretamente",alertMessage.importance.WARNING);
                            break;
                        case objectHandler.ACTIONSUBJECTSTATE.TIMEOUT:
                            alertMessage("Erro: ","O servidor não respondeu à requisição",alertMessage.importance.DANGER);
                            break;
                        default:
                            break;
                    }
                });
                objectHandler.attach(objectHandler.delete,function(state){
                    switch(state){
                        case objectHandler.ACTIONSUBJECTSTATE.OK:
                            alertMessage("Sucesso: ","O item foi removido com sucesso",alertMessage.importance.SUCCESS);
                            break;
                        case objectHandler.ACTIONSUBJECTSTATE.ERROR:
                            alertMessage("Erro: ","Um problema ocorreu ao processar sua requisição",alertMessage.importance.DANGER);
                            break;
                        case objectHandler.ACTIONSUBJECTSTATE.TIMEOUT:
                            alertMessage("Erro: ","O servidor não respondeu à requisição",alertMessage.importance.DANGER);
                            break;
                        default:
                            break;
                    }
                });
            };
            
                var layersPanel = $("#layers-panel");
                var accessLevelPanel = $("#access-level-panel");
                var usersPanel = $("#users-panel");
                initializeUIPanel(layersPanel,LayersHandler,"features");
                initializeUIPanel(accessLevelPanel,AccessLevelHandler,"layers");
                initializeUIPanel(usersPanel,UserHandler,"accessLevel");
            });
        </script>
        <style>
            .clickable{
                cursor : pointer;
            }
            .trash { color:rgb(209, 91, 71); }
            //.okglyph {font-size : 25px;color: #006699}
            .glyphicon-plus-sign {font-size : 35px;color: #009966}
            
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
        </style>
    </head>
    <body>
        
        <div class="container container-fluid">
            <div>
                
            </div>
            <!--LAYERS-->
            <div id="layers-panel" class="panel panel-default">
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
                                            <a href="#" class="handler-load-current"><span class="glyphicon glyphicon-pencil"></span></a>
                                            <a href="#" class="trash handler-delete-selected"><span class="glyphicon glyphicon-trash"></span></a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            
                            
                            <div class="panel panel-default object-div">
                                <div class="panel-heading input-group">
                                        <input type="text" class="pull-left form-control handler-object-wmsId" placeholder="wmsId"/>
                                        <span class="input-group-addon clickable handler-save-current handler-add-save">Salvar</span>
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
                                    <div class="panel panel-danger">
                                        <div class="panel-heading">
                                            Features
                                        </div>
                                        <div class="panel-body">
                                            <div class="handler-object-features list-group"></div>
                                            <div class="text-center handler-nested-add-select-features clickable"><span class="glyphicon glyphicon-plus-sign"></span></div>
                                            <div class=""></div>
                                            <div class="handler-nested-template-features list-group-item" style="display:none">
                                                <a class="trash handler-nested-delete-current clickable"><span class="glyphicon glyphicon-trash"></span></a>
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
                            <div class="panel panel-default object-div">
                                <div class="panel-heading input-group">
                                        <input type="text" class="pull-left form-control handler-object-name" placeholder="name"/>
                                        <span class="input-group-addon clickable handler-save-current handler-add-save">Salvar</span>
                                </div>
                                <div class="panel-body">    
                                    <div class="panel panel-danger">
                                        <div class="panel-heading">
                                            Camadas
                                        </div>
                                        <div class="panel-body">
                                            <div class="handler-object-layers list-group"></div>
                                            <div class="text-center handler-nested-add-select-layers clickable"><span class="glyphicon glyphicon-plus-sign"></span></div>
                                            <div class=""></div>
                                            <div class="handler-nested-template-layers list-group-item" style="display:none">
                                                <a class="trash handler-nested-delete-current clickable"><span class="glyphicon glyphicon-trash"></span></a>
                                                <div class="input-group">
                                                    <span for="handler-object-layers-wmsId" class="input-group-addon">wmsId</span>
                                                    <input type="text" class="form-control handler-object-layers-wmsId" placeholder="wmsId"/>
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
                            <div class="panel panel-default object-div">
                                <div class="panel-heading input-group">
                                        <input type="text" class="pull-left form-control handler-object-username" placeholder="username"/>
                                        <span class="input-group-addon clickable handler-save-current handler-add-save">Salvar</span>
                                </div>
                                <div class="panel-body">    
                                    
                                    <div class="input-group">
                                        <label for="handler-object-name" class="input-group-addon">Nome:</label>
                                        <input type="text" class="form-control handler-object-name" placeholder="Nome"/>
                                    </div>
                                    <div class="input-group">
                                        <label for="handler-object-name" class="input-group-addon">Senha</label>
                                        <input type="text" class="form-control handler-object-password" placeholder="******">
                                    </div>
                                    <div class="input-group">
                                        <label for="handler-object-name" class="input-group-addon">Email</label>
                                        <input type="text" class="form-control handler-object-email" placeholder="email">
                                    </div>     
                                    <div class="input-group">
                                        <label for="handler-object-name" class="input-group-addon">Telefone</label>
                                        <input type="text" class="form-control handler-object-telnumber" placeholder="telefone">
                                    </div>
                                    <div class="panel panel-danger">
                                        <div class="panel-heading">
                                            Nível de Acesso
                                        </div>
                                        <div class="panel-body">
                                            <div class="input-group">
                                                <label for="handler-object-accessLevel" class="input-group-addon">Nome:</label>
                                                <input type="text" class="form-control handler-object-accessLevel" placeholder="nome">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
            </div>  
            <div id="message-container" style="display:none">
                <div class="message-body" style="display:none">
                    <strong><span class="message-title"></span></strong>
                    <span class="message-body"></span>
                </div>
            </div>
        </div>
    </body>
</html>
