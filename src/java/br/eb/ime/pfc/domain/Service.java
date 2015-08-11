package br.eb.ime.pfc.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a service that contains information about all the users, 
 * all layers and all access levels used by the system.
 * 
 */
public class Service {
    private final Map<String,User> users;
    private final Map<String,Layer> layers;
    private final Map<String,AccessLevel> accessLevel;
    
    /**
     * Creates an instance of a service.
     */
    public Service(){
        users = new HashMap<>();
        layers = new HashMap<>();
        accessLevel = new HashMap<>();
        checkRep();
    }
    
    /**
     * Checks the representation invariant.
     */
    private void checkRep(){
        
    }
    
    //OBSERVERS
    
    /**
     * Returns the layer specified by its wmsId.
     * @param layerWmsId
     * The wmsId of the layer.
     * Requires: layerWmsId not empty.
     * @return the Layer specified in this Service that contains the wmsId with the
     * value specified by the param.
     * @throws br.eb.ime.pfc.domain.Service.LayerNotFoundException if no Layers of this service has the specified
     * wmsId.
     */
    public Layer getLayerById(String layerWmsId) throws LayerNotFoundException{
        throw new RuntimeException();
    }
    
    /**
     * Returns the AccessLevel specified by its name.
     * @param accessLevelName
     * The name of the access level
     * @return the AccessLevel specified in this service that has the specified
     * name.
     * @throws br.eb.ime.pfc.domain.Service.AccessLevelNotFoundException if no AccessLevel of this service has
     * the specified name.
     */
    public AccessLevel getAccessLevelByName(String accessLevelName) throws AccessLevelNotFoundException{
        throw new RuntimeException();
    }
    
    /**
     * Returns the User specified by its username.
     * @param username
     * The username of the User
     * @return The User of this service that has the same username specified
     * @throws br.eb.ime.pfc.domain.Service.UserNotFoundException if no User of this service has the specified
     * username.
     */
    public User getUserByUsername(String username) throws UserNotFoundException{
        throw new RuntimeException();
    }
    
    //MUTATORS
    
    /**
     * Add a Layer to this Service.
     * @param layer
     * The Layer that will be added to this Service.
     * Requires: The layer must not contain the same wmsId of a Layer already
     * present in this service.
     * @throws br.eb.ime.pfc.domain.Service.LayerRepetitionException 
     */
    public void addLayer(Layer layer) throws LayerRepetitionException{
        checkRep();
    }
    
    /**
     * Add an AccessLevel to this Service.
     * @param accessLevel
     * The AccessLevel that will be added to this Service.
     * Requires: The accessLevel must not contain the same name of an AccessLevel
     * already present in this service.
     * @throws br.eb.ime.pfc.domain.Service.AccessLevelRepetitionException 
     */
    public void addAccessLevel(AccessLevel accessLevel) throws AccessLevelRepetitionException{
        checkRep();
    }
    
    /**
     * Add an User to this Service.
     * @param user
     * The User that will be added to this service.
     * Requires: The user must not contain the same username of an User already present 
     * in this service.
     * @throws br.eb.ime.pfc.domain.Service.UserRepetitionException  if user has
     * the same username of a User already present in this service.
     */
    public void addUser(User user) throws UserRepetitionException{
        checkRep();
    }
    
    /**
     * This exception is triggered when no Layer is found in this service 
     * with the specified parameters.
     */
    public class LayerNotFoundException extends RuntimeException{
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Creates a LayerNotFoundException with a detail message.
         * @param message 
         * The message that specifies the error.
         */
        public LayerNotFoundException(String message){
            super(message);
        }
        
    }
    
    /**
     * This exception is triggered when no AccessLevel is found in this service 
     * with the specified parameters.
     */
    public class AccessLevelNotFoundException extends RuntimeException{
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Creates an AccessLevelNotFoundException with a detail message.
         * @param message 
         * The message that specifies the error.
         */
        public AccessLevelNotFoundException(String message){
            super(message);
        }
        
    }
    
    /**
     * This exception is triggered when no User is found in this service 
     * with the specified parameters.
     */
    public class UserNotFoundException extends RuntimeException{
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Creates a UserNotFoundException with a detail message.
         * @param message 
         * The message that specifies the error.
         */
        public UserNotFoundException(String message){
            super(message);
        }
        
    }
    
    /**
     * This exception is triggered when one tries to add a Layer wich has a wmsId that is
     * already present in a Layer of this service.
     */
    public class LayerRepetitionException extends RuntimeException{
        private static final long serialVersionUID = 1L;
        
        /**
         * Creates a LayerRepetitionException with a detail message.
         * @param message 
         * The message that specifies the error.
         */
        public LayerRepetitionException(String message){
            super(message);
        }
    }
    
    /**
     * This exception is triggered when one tries to add an AccessLevel with the same name of
     * an AccessLevel already present in this service.
     */
    public class AccessLevelRepetitionException extends RuntimeException{
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Creates an AccessLevelRepetitionException with a detail message.
         * @param message 
         * The message that specifies the error.
         */
        public AccessLevelRepetitionException(String message){
            super(message);
        }
    }
    
    /**
     * This exception is triggered when one tries to add an User with the same username of 
     * a User already present in this service.
     */
    public class UserRepetitionException extends RuntimeException{
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Creates an UserRepetitionException with a detail message.
         * @param message 
         * The message that specifies the error.
         */
        public UserRepetitionException(String message){
            super(message);
        }
    }
}