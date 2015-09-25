package br.eb.ime.pfc.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
    
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "ACCESSLEVEL_LAYER",
                joinColumns = {@JoinColumn(name="ACCESSLEVEL_ID",referencedColumnName="ACCESSLEVEL_ID")},
                inverseJoinColumns = {@JoinColumn(name="LAYER_ID",referencedColumnName="LAYER_ID",nullable = false)})
    //@OrderColumn(name = "layer_index",updatable = true,insertable = true,nullable = false)
    private final List<Layer> layers;
    
    @OneToMany(fetch=FetchType.LAZY,cascade = CascadeType.ALL,mappedBy="accessLevel")
    private final List<User> users;
    /*
    * Representation Invariant:
    * name must not be empty
    * layers must contain all values of mapLayers
    * layers must have the same size of mapLayers
    */
    
    //Constructors
    
    public static AccessLevel makeAccessLevel(String name) throws ObjectInvalidIdException{
        if(isValidId(name)){
            return new AccessLevel(name);
        }
        else{
            throw new ObjectInvalidIdException("Could not create AccessLevel because it's not a valid name");
        }
    }
    
    public static boolean isValidId(String name){
        if(name.equals("")){
            return false;
        }
        else if(!name.equals(name.trim())){
            return false;
        }
        else{
            return true;
        }
    }
    
    /**
     * Creates an Access Level with the specified name.
     * @param name 
     * Requires: name must not be empty
     */
    protected AccessLevel(String name){
        this.name = name;
        this.layers = new LinkedList<>();
        this.users = new ArrayList<>();
        checkRep();
    }
    
    /**
     * Default Constructor for serialization/deserialization process only.
     */
    protected AccessLevel(){
        name = null;
        this.users = new ArrayList<>();
        this.layers = new LinkedList<>();
    }
    
    /**
     * Checks the representation invariant.
     */
    private void checkRep(){
        assert !this.name.equals("");
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
    
    public void addUser(User user){
        if(this.containsUser(user.getUsername())){
            throw new UserRepetitionException("Access Level already contains user with the specified username");
        }
        else{
            this.users.add(user);
        }
    }
    
    public Collection<User> getUsers(){
        return Collections.unmodifiableList(users);
    }
    
    public boolean containsUser(String username){
        for(User user : this.users){
            if(user.getUsername().equalsIgnoreCase(username)){
                return true;
            }
        }
        return false;
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
    
    /**
     * This exception is triggered when one tries to add a layer to an Access Level, 
     * and the layer has the same wmsId of a layer already present in the 
     * AccessLevel.
     */
    public static class UserRepetitionException extends RuntimeException{
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Creates a LayerRepetitionException with a detail message.
         * @param message 
         * The message that specify the error.
         */
        public UserRepetitionException(String message){
            super(message);
        }
    }
}