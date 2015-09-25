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

if(typeof WMSCRUD === 'undefined'){
    console.log('No WMSCRUD class defined');
}
else{
    var initializeUIPanel = function(panel,HandlerClass,nestedElements){
            var panelHeading = panel.find(".panel-heading").first();
            var panelBody = panel.find(".panel-body").first();
            var objectListDiv = panel.find(".object-list-div").first();
            var objectDiv = panel.find(".object-div").first();
            var addSelect = panel.find(".handler-add-select");
            var addSaveButton = panel.find(".handler-add-save").first();
            var deleteMultipleButton = panel.find(".handler-delete-multiple").first();
            var objectHandler = new HandlerClass(objectListDiv,objectDiv);
            var objectHandlerId = panel.find(".handler-object-"+objectHandler.idField).first();
            addSaveButton.click();
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
                $(objectHandlerId).attr("readonly","true");
                addSaveButton.text("Salvar");
            }
            else{
                addSaveButton.removeClass("handler-save-current");
                addSaveButton.addClass("handler-add-current"); 
                $(objectHandlerId).removeAttr('readonly');
                addSaveButton.text("Adicionar");
            }
        };
        
        addSelect.each(function(){
            $(this).click(function(event){
                objectHandler.clearObjectDiv();
                objectHandler.saveState = false;
                changeSaveAddView();
                objectHandler.clearErrorHighlight();
             });
         });
        var i = 0,ii = nestedElements.length;
        var nestedAddSelect = [];
        for(;i<ii;i++){
            nestedAddSelect[i] = panel.find(".handler-nested-add-select-"+nestedElements[i]).first();
            nestedAddSelect[i].get(0).nested = nestedElements[i];
            
            var nestedElementDeleteFunction = function(nestedElement,container){
                if(typeof container === 'undefined'){
                    container = objectHandler.objectDiv;
                }
                var layersNestedDeleteCurrent = $(container).find(".handler-nested-delete-current");
                $.each(layersNestedDeleteCurrent,function(index){
                    layersNestedDeleteCurrent.unbind("click");
                    layersNestedDeleteCurrent.click(function(event){
                        $(this).parents(".handler-nested-copy-"+nestedElement).first().remove();
                    });
                });
            };

            nestedAddSelect[i].click(function(event){
                var nestedElement = this.nested;
                var handlerNested = objectHandler.objectDiv.find(".handler-object-"+nestedElement).first();
                var template = objectHandler.objectDiv.find(".handler-nested-template-"+nestedElement).first();
                var copy = template.clone();
                copy.removeClass("handler-nested-template-"+nestedElement);
                copy.addClass("handler-nested-copy-"+nestedElement);
                copy.show();
                handlerNested.append(copy);
                nestedElementDeleteFunction(nestedElement,copy);
            });
        }
        
        objectHandler.attach(objectHandler.loadObj,function(state){
            if(state === objectHandler.ACTIONSUBJECTSTATE.OK){
                console.log("NESTED"+nestedAddSelect.length);
                var i = 0,ii = nestedAddSelect.length;
                for(;i<ii;i++){
                    nestedElementDeleteFunction(nestedAddSelect[i].get(0).nested);
                }
            }
        });
        
        addSaveButton.click(function(event){
            if(objectHandler.saveState){
                //promptMessage("","Deseja salvar o item?",function(){
                    objectHandler.save();
                //});  
                console.log("SAVE");
            }
            else{
                console.log("ADD");
                objectHandler.add();
            }
        });
        
        deleteMultipleButton.click(function(event){
            var objectIdDeleteList = [];
            var objectIdTemplateList = [];
            objectHandler.objectListDiv.find(".handler-checkbox").each(function(index){
                try{
                    if(this.checked){
                        var template = $(this).parents(".handler-template-obj").first();
                        var objectId = template.find(".handler-id-"+objectHandler.idField).first().text();
                        objectIdDeleteList.push(objectId);
                        objectIdTemplateList.push(template);
                    }
                }
                catch(err){
                    
                }
            });
            var message = "";
            if(objectIdDeleteList.length < 1){
                alertMessage("Info: ","Não há itens selecionados no momento",alertMessage.importance.INFO);
                return;
            }
            else if(objectIdDeleteList.length < 2){
                message = "Deseja apagar o item?";
            }
            else{
                message = "Deseja apagar todos os "+objectIdDeleteList.length+" items?";
            }
            promptMessage("",message,function(){
                        try{
                            var successDeleteCallback = function(state){
                                console.log("CALLBACK");
                                if(state === objectHandler.ACTIONSUBJECTSTATE.OK){
                                    var i = 0,ii = objectIdTemplateList.length;
                                    for(;i<ii;i++){
                                        console.log(objectIdTemplateList[0]);
                                        $(objectIdTemplateList[i]).remove();
                                    }
                                }
                                objectHandler.detach(objectHandler.delete,successDeleteCallback);
                            }
                            objectHandler.attach(objectHandler.delete,successDeleteCallback);
                            objectHandler.delete(objectIdDeleteList);
                        }
                        catch(err){
                            console.log("Problem when deleting multiple items");
                        }
                });
            
        });
        
        
        objectHandler.attach(objectHandler.loadObj,function(state){
            objectHandler.saveState = true;
            changeSaveAddView();
        });

        objectHandler.attach(objectHandler.readObjs,function(state){
            if(state === objectHandler.ACTIONSUBJECTSTATE.OK){
                var objectIds = objectHandler.objectListDiv.find(".handler-id-"+objectHandler.idField);
                var loadObjFunction = function(event){
                    var loadedObject = $(this).parents(".handler-template-obj").first().get(0).loadedObject;
                    if(typeof loadedObject === 'undefined'){
                        console.log("Internal error, no object to load");
                    }
                    else{
                        objectHandler.loadObj(objectHandler,loadedObject);
                        objectIds.parents(".list-group-item").removeClass("handler-selected-item");
                        $(this).parents(".list-group-item").addClass("handler-selected-item");
                    }  
                };
                var deleteCurrentFunction = function(event){
                    var self = this;
                    promptMessage("","Deseja apagar?",function(){
                        var template= $(self).parents(".handler-template-obj").first();
                        var objId = template.find(".handler-id-"+objectHandler.idField).first().text();
                        objectHandler.delete.viewObj = template;
                        objectHandler.delete(objId);
                    });
                };
                
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

        objectHandler.attach(objectHandler.loadObj,function(state){objectHandler.clearErrorHighlight();});
        objectHandler.attach(objectHandler.readObjs,function(state){objectHandler.clearErrorHighlight();});

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
        
        var alertMessage = function(title,message,importance){    
            var messageView = messageContainer.find(".message-body").first().clone();
            messageView.removeClass("alert-danger");
            messageView.removeClass("alert-success");
            messageView.removeClass("alert-warning");
            messageView.removeClass("alert-info");
            messageView.addClass("alert-"+importance);
            
            messageView.find(".message-title").text(title);
            messageView.find(".message-text").text(message);
            messageView.find(".close").click(function(evt){
               messageView.fadeOut(400); 
            });
            messageContainer.find(".message-body").remove();
            messageView.appendTo(messageContainer);
            messageView.fadeIn({
                    duration : 400,
                    done: function(){
                        window.setTimeout(function(){messageView.fadeOut(400);},3500);
                    }
                });
        };
        alertMessage.importance = {WARNING : 'warning',SUCCESS : 'success',INFO : 'info',DANGER : 'danger'};
        //ADD
        objectHandler.attach(objectHandler.add,function(state){
            switch(state){
                case objectHandler.ACTIONSUBJECTSTATE.OK:
                    alertMessage("Sucesso: ","Item adicionado com sucesso",alertMessage.importance.SUCCESS);
                    break;
                case objectHandler.ACTIONSUBJECTSTATE.DUPLICATE:
                    alertMessage("Erro: ","Um item com mesmo identificador já foi adicionado",alertMessage.importance.WARNING);
                    break;
                case objectHandler.ACTIONSUBJECTSTATE.RESTRICTIONVIOLATION:
                    alertMessage("Aviso: ","Não foi possível adicionar o item pois há campos preenchidos incorretamente",alertMessage.importance.WARNING);
                    break;
                case objectHandler.ACTIONSUBJECTSTATE.TIMEOUT:
                    alertMessage("Erro: ","O servidor não respondeu à requisição",alertMessage.importance.DANGER);
                    break;
                case objectHandler.ACTIONSUBJECTSTATE.ERROR:
                    alertMessage("Erro: ","Um problema ocorreu ao processar sua requisição",alertMessage.importance.DANGER);
                    break;
                default:
                    alertMessage("Erro: ","Um problema ocorreu ao processar sua requisição",alertMessage.importance.DANGER);
                    break;
            }
        });
        objectHandler.attach(objectHandler.save,function(state){
            switch(state){
                case objectHandler.ACTIONSUBJECTSTATE.OK:
                    alertMessage("Sucesso: ","O item foi salvo com sucesso",alertMessage.importance.SUCCESS);
                    break;
                case objectHandler.ACTIONSUBJECTSTATE.NOTFOUND:
                    alertMessage("Erro: ","O item não foi encontrado",alertMessage.importance.DANGER);
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
                    alertMessage("Erro: ","Um problema ocorreu ao processar sua requisição",alertMessage.importance.DANGER);
                    break;
            }
        });
        objectHandler.attach(objectHandler.delete,function(state){
            switch(state){
                case objectHandler.ACTIONSUBJECTSTATE.OK:
                    alertMessage("Sucesso: ","O item foi removido com sucesso",alertMessage.importance.SUCCESS);
                    break;
                case objectHandler.ACTIONSUBJECTSTATE.NOTFOUND:
                    alertMessage("Erro: ","O item não foi encontrado",alertMessage.importance.DANGER);
                    break;
                case objectHandler.ACTIONSUBJECTSTATE.ERROR:
                    alertMessage("Erro: ","Um problema ocorreu ao processar sua requisição",alertMessage.importance.DANGER);
                    break;
                case objectHandler.ACTIONSUBJECTSTATE.TIMEOUT:
                    alertMessage("Erro: ","O servidor não respondeu à requisição",alertMessage.importance.DANGER);
                    break;
                default:
                    alertMessage("Erro: ","Um problema ocorreu ao processar sua requisição",alertMessage.importance.DANGER);
                    break;
            }
        });
        
        
        var promptContainer = $("#modal-prompt");
        var promptMessage = function(title,message,success,cancel){
            if(title !== ""){
                promptContainer.find(".modal-title").html(title);
                promptContainer.find(".modal-header").show();
            }
            else{
                promptContainer.find(".modal-header").hide();
            }
            promptContainer.find(".modal-body").html(message);
            var modalConfirm = promptContainer.find(".modal-confirm");
            modalConfirm.unbind("click");
            modalConfirm.click(function(){
                if(typeof success !== 'undefined'){
                    try{
                        success();
                    } 
                    catch(err){
                        console.log("Error in prompt callback for confirm");
                    }
                }
            });
            var modalCancel = promptContainer.find(".modal-cancel");
            modalCancel.unbind("click");
            modalCancel.click(function(){
                if(typeof cancel !== 'undefined'){
                    try{
                        cancel();
                    }
                    catch(err){
                        console.log("Error in prompt callback for cancel");
                    }
                }
            });
            promptContainer.modal('show');
        };
        return objectHandler;
    };
}
