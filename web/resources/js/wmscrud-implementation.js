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


/* global WMSCRUD */

if(typeof WMSCRUD === 'undefined'){
    console.log("No WMSCRUD prototype defined");
}
else{
    function LayerHandler(objectListDiv,objectDiv){
        WMSCRUD.ObjectHandler.call(this);
        this.idField = "wmsId";
        this.url = "layer-handler";
        this.objectDiv = objectDiv;
        this.objectListDiv = objectListDiv;
        this.saveState = false;
    };
    LayerHandler.prototype = Object.create(WMSCRUD.ObjectHandler.prototype);
    LayerHandler.constructor = LayerHandler;
    LayerHandler.prototype.mapping = function(){
        return { name : "",
            wmsId : "",style : {name : "",blob : ""},opacity : "",features : [{name : "",wmsId :""}],accessLevels: [{name : ""}]};
    };
    
    LayerHandler.prototype.obeyRestrictions = function(matchObject){
        this.clearErrorHighlight();
        if(typeof matchObject === 'undefined'){
            return false;
        }
        if(matchObject['wmsId'] === ""){
            this.objectDiv.find(".handler-object-wmsId").addClass("handler-error-highlight");
            return false;
        }
        else{
            matchObject['wmsId'] = matchObject['wmsId'].trim();
        }
        try{
            if((typeof matchObject['opacity'] === 'undefined' )|| (matchObject['opacity'] === "")){
                this.objectDiv.find(".handler-object-opacity").addClass("handler-error-highlight");
                return false;
            }
            else{
                matchObject['opacity'] = matchObject['opacity'].trim();
            }
            var opacity = parseFloat(matchObject['opacity']);
            if(opacity < 0 || opacity > 1){
                this.objectDiv.find(".handler-object-opacity").addClass("handler-error-highlight");
                return false;
            }
        }
        catch(err){
            return false;
        }
        var i = 0,ii=matchObject['features'].length;
        var featuresViewIds = this.objectDiv.find(".handler-object-features-wmsId");
        for(;i<ii;i++){
            var feature = matchObject['features'][i];
            
            if(feature['wmsId'] === ''){
                try{
                    $(featuresViewIds[i]).addClass("handler-error-highlight");
                }
                catch(err){
                    console.log("Could not highlight field");
                }
                return false;
            }
            else if(typeof feature['wmsId'] !== 'undefined'){
                feature['wmsId'] = feature['wmsId'].trim();
            }
        }
        
        var i = 0,ii=matchObject['accessLevels'].length;
        var accessLevelsNames = this.objectDiv.find(".handler-object-accessLevels-name");
        for(;i<ii;i++){
            var accessLevel = matchObject['accessLevels'][i];
            
            if(accessLevel['name'] === ''){
                try{
                    $(accessLevelsNames[i]).addClass("handler-error-highlight");
                }
                catch(err){
                    console.log("Could not highlight field");
                }
                return false;
            }
            else{
                accessLevel['name'] = accessLevel['name'].trim(); 
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
        this.saveState = false;
    };
    AccessLevelHandler.prototype = Object.create(WMSCRUD.ObjectHandler.prototype);
    AccessLevelHandler.constructor = AccessLevelHandler;
    AccessLevelHandler.prototype.mapping = function(){
        return { name : "",layers: [{wmsId :""}]};
    };
    AccessLevelHandler.prototype.obeyRestrictions = function(matchObject){
        this.clearErrorHighlight();
        if(typeof matchObject === 'undefined'){
            return false;
        }
        if(matchObject['name'] === ''){
            this.objectDiv.find(".handler-object-name").addClass("handler-error-highlight");
            return false;
        }
        else{
            matchObject['name'] = matchObject['name'].trim();
        }
        var layers = matchObject['layers'];
        var layerViewIds = this.objectDiv.find(".handler-object-layers-wmsId");
        var i = 0,ii=layers.length;
        for(;i<ii;i++){
            var layer = layers[i];
            if(layer['wmsId']===''){
                try{
                    $(layerViewIds[i]).addClass("handler-error-highlight");
                }
                catch(err){
                    console.log("Could not highlight layer of access level");
                }
                return false;
            }
            else{
                layer['wmsId'] = layer['wmsId'].trim();
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
        this.saveState = false;
    };
    UserHandler.prototype = Object.create(WMSCRUD.ObjectHandler.prototype);
    UserHandler.constructor = UserHandler;
    UserHandler.prototype.mapping = function(){
        return { username : "",password: "", accessLevel : "",telephone : "",email: "",name:""};
    };
    UserHandler.prototype.obeyRestrictions = function(matchObject){
        this.clearErrorHighlight();
        if(typeof matchObject === 'undefined'){
            return false;
        }
        if((matchObject['username'] === '') || (matchObject['username'].trim().indexOf(" ") > -1)){
            this.objectDiv.find(".handler-object-username").addClass("handler-error-highlight");
            return false;
        }
        if(matchObject['password'] === ''){
            this.objectDiv.find(".handler-object-password").addClass("handler-error-highlight");
            return false;
        }
        
        if(matchObject['accessLevel'] === ''){
            this.objectDiv.find(".handler-object-accessLevel").addClass("handler-error-highlight");
            return false;
        }
        return true;
    };
}
