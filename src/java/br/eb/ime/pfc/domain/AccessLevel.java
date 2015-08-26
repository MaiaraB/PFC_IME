package br.eb.ime.pfc.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Represents the Access Level of a User.
 * 
 * This class represents an access level which contains a group of Layers that
 * can be accessed by this Access Level.
 * Users of the system have an unique Access Level that allows them to view the 
 * layers specified by the Access Level.
 * The Access Level is identified by its name.
 */
@Entity
@Table(name = "access_levels")
public class AccessLevel implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "ACCESSLEVEL_ID")
    private final String name;
    
    @ManyToMany(fetch=FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name = "LAYER_ACCESSLEVEL",
                joinColumns = {@JoinColumn(name="ACCESSLEVEL_ID",referencedColumnName="ACCESSLEVEL_ID")},
                inverseJoinColumns = {@JoinColumn(name="LAYER_ID",referencedColumnName="LAYER_ID")}
    )                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
    private final List<Layer> layers;
    
    @Transient
    private final Map<String,Layer> mapLayers;
    
    /*
    * Representation Invariant:
    * name must not be empty
    * layers must contain all values of mapLayers
    * layers must have the same size of mapLayers
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
        this.layers = new LinkedList<>();
        checkRep();
    }
    
    /**
     * Default Constructor for serialization/deserialization process only.
     */
    protected AccessLevel(){
        name = null;
        this.mapLayers = new HashMap<>();
        this.layers = new LinkedList<>();
    }
    
    /**
     * Checks the representation invariant.
     */
    private void checkRep(){
        assert !this.name.equals("");
        assert mapLayers.size() == layers.size();
        assert layers.containsAll(mapLayers.values());
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
        return Collections.unmodifiableList(layers);
    }
    
    /**
     * Specifies whether a layer is accessed by this Access Level or not.
     * @param layerWmsId
     * The wmsId of the layer.
     * @return true if this Access Level has access to the layer with the specified
     * wmsId.
     */
    public boolean hasAccessToLayer(String layerWmsId){
        for(Layer layer : this.layers){
            if(layer.getWmsId().equals(layerWmsId)){
                return true;
            }
        }
        return false;
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
        this.layers.add(layer);
        this.mapLayers.put(layer.getWmsId(), layer);
        
        checkRep();
    }
    
    /**
     * This exception is triggered when one tries to add a layer to an Access Level, 
     * and the layer has the same wmsId of a layer already present in the 
     * AccessLevel.
     */
    public static class LayerRepetitionException extends RuntimeException{
        
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