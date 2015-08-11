package br.eb.ime.pfc.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents the Access Level of a User.
 * 
 * This class represents an access level which contains a group of Layers that
 * can be accessed by this Access Level.
 * Users of the system have an unique Access Level that allows them to view the 
 * layers specified by the Access Level.
 * The Access Level is identified by its name.
 */
public class AccessLevel {
    
    private final String name;
    private final Map<String,Layer> mapLayers;
    private final List<Layer> orderedLayers;
    
    /*
    * Representation Invariant:
    * name must not be empty
    * orderedLayers must contain all values of mapLayers
    * orderedLayers must have the same size of mapLayers
    */
    
    //Constructors
    
    /**
     * Creates an Access Level with the specified name.
     * @param name 
     * Requires: name must not be empty
     */
    public AccessLevel(String name){
        this.name = name;
        this.mapLayers = new HashMap<>();
        this.orderedLayers = new LinkedList<>();
        checkRep();
    }
    
    /**
     * Checks the representation invariant.
     */
    private void checkRep(){
        assert !this.name.equals("");
        assert mapLayers.size() == orderedLayers.size();
        assert orderedLayers.containsAll(mapLayers.values());
    }
    
    /**
     * Returns the name of this Access Level
     * @return name
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * Returns the layers accessed by this Access Level.
     * @return layers
     * Effects: layers is an unmodifiable list that specifies each layer in 
     * the order they were added to this Access Level.
     */
    public List<Layer> getLayers(){
        return Collections.unmodifiableList(orderedLayers);
    }
    
    /**
     * Specifies whether a layer is accessed by this Access Level or not.
     * @param layerWmsId
     * The wmsId of the layer.
     * @return true if this Access Level has access to the layer with the specified
     * wmsId.
     */
    public boolean hasAccessToLayer(String layerWmsId){
        return mapLayers.containsKey(layerWmsId);
    }
    
    //Mutators
    
    /**
     * Add another Layer to this Access Level. 
     * @param layer to be accessed by this Access Level
     * @throws br.eb.ime.pfc.domain.AccessLevel.LayerRepetitionException
     * When the layer added has the same wmsId of another layer already added.
     */
    public void addLayer(Layer layer) throws LayerRepetitionException{
        if(hasAccessToLayer(layer.getWmsId())){
            throw new LayerRepetitionException("Cannot add Layer with the same wmsId of a Layer in use.");
        }
        this.orderedLayers.add(layer);
        this.mapLayers.put(layer.getWmsId(), layer);
        
        checkRep();
    }
    
    /**
     * This exception is triggered when one tries to add a layer to an Access Level, 
     * and the layer has the same wmsId of a layer already present in the 
     * AccessLevel.
     */
    public class LayerRepetitionException extends RuntimeException{
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Creates a LayerRepetitionException with a detail message.
         * @param message 
         * The message that specify the error.
         */
        public LayerRepetitionException(String message){
            super(message);
        }
    }
}