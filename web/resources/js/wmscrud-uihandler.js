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
    var initializeUIPanel = function(panel,HandlerClass,nestedElement){
            var panelHeading = panel.find(".panel-heading").first();
            var panelBody = panel.find(".panel-body").first();
            var objectListDiv = panel.find(".object-list-div").first();
            var objectDiv = panel.find(".object-div").first();
            var addSelect = panel.find(".handler-add-select");
            var nestedAddSelect = panel.find(".handler-nested-add-select-"+nestedElement);
            var addSaveButton = panel.find(".handler-add-save").first();
            var deleteButton = panel.find(".handler-delete-current").first();
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
                clearErrorHighlight();
             });
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
        /*
        deleteButton.click(function(event){
           promptMessage("Tem certeza que deseja deletar?","A operação não poderá ser desfeita",function(){
               console.log(this.objectDiv.find("."+this.viewIdPreffix+"-object-"+this.idField).first().val());
               objectHandler.deleteCurrent(); 
           });
        });*/
        objectHandler.attach(objectHandler.loadObj,function(state){
            nestedElementDeleteFunction();
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
            messageView.fadeIn(400);
        };
        alertMessage.importance = {WARNING : 'warning',SUCCESS : 'success',INFO : 'info',DANGER : 'danger'};
        //ADD
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
                console.log(success);
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
            promptContainer.modal({keyboard : true});
        };
        return objectHandler;
    };
}
