/* 
 * The MIT License
 *
 * Copyright 2015 arthurfernandes.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


//Create Namespace
var WMSCRUD = WMSCRUD || {};

WMSCRUD.ObjectHandler = function(){
    this.ACTIONS = {DELETE : "delete",READALL : "readAll",SAVE: "update",ADD: "add"};
    this.ACTIONSUBJECTSTATE = {UNDEFINED : "UNDEFINED",OK : "OK", NOTFOUND : "NOTFOUND",
        ERROR : "ERROR",TIMEOUT : "TIMEOUT",DUPLICATE : "DUPLICATE",RESTRICTIONVIOLATION : "RESTRICTION"};
    this.DATARESPONSESTATE = {OK : "OK",ERROR : "ERROR",DUPLICATE: "DUPLICATE",NOTFOUND : "NOTFOUND"};
    this.REQUESTTIMEOUT = 3000;
    this.viewIdPreffix = "handler";
    this.mappingObject = this.mapping();

    this.url = "";
    this.idField = "";
    this.objectDiv = $();
    this.objectListDiv = $();
    this.loadedObjects = {};
    this.observers = {};
};

WMSCRUD.ObjectHandler.prototype.attach = function(actionSubject,actionObserver){
    this.observers[actionSubject] = this.observers[actionSubject] || [];
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
    this.requestAction(loadURL,this.readObjs,"",this.loadObjsList);
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
        else if(data === self.DATARESPONSESTATE.DUPLICATE){
            self.add.state = self.ACTIONSUBJECTSTATE.DUPLICATE;
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
        else if(data === self.DATARESPONSESTATE.NOTFOUND){
            self.save.state = self.ACTIONSUBJECTSTATE.NOTFOUND;
        }
        else{
            self.save.state = self.ACTIONSUBJECTSTATE.ERROR;
        }
    };

    this.requestAction(saveURL,self.save,objectSave,successCallBack);
};

WMSCRUD.ObjectHandler.prototype.delete = function(objIds){
    if(!$.isArray(objIds)){
        var temp = objIds;
        objIds = [];
        objIds.push(temp);
    }
    var self = this;
    self.delete.state = this.ACTIONSUBJECTSTATE.UNDEFINED;        
    var deleteURL = this.url + "?action=" + this.ACTIONS.DELETE;
    var successCallBack = function(self,data,textStatus,jqXHR){
        console.log("DATA:",data);
        if(typeof data !== 'undefined' && data === self.DATARESPONSESTATE.OK){
            self.delete.state = self.ACTIONSUBJECTSTATE.OK;
        }
        else if(data === self.DATARESPONSESTATE.NOTFOUND){
            self.delete.state = self.ACTIONSUBJECTSTATE.NOTFOUND;
        }
        else{
            self.delete.state = self.ACTIONSUBJECTSTATE.ERROR;
        }
    };
    var i = 0,ii = objIds.length;
    var dataSend = {ids : []};
    for(;i<ii;i++){
        dataSend.ids.push({id : objIds[i]});
    }
    this.requestAction(deleteURL,self.delete,dataSend,successCallBack);
};

WMSCRUD.ObjectHandler.prototype.deleteCurrent = function(){
    var deleteId = this.objectDiv.find("."+this.viewIdPreffix+"-object-"+this.idField).first().val();
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

WMSCRUD.ObjectHandler.prototype.clearErrorHighlight = function(){
    this.objectDiv.find('.handler-field').removeClass("handler-error-highlight");
};

WMSCRUD.ObjectHandler.prototype.mapping = function(objId){
    this.mappingObject = {};
};

