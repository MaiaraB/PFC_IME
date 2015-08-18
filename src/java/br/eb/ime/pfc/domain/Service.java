package br.eb.ime.pfc.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Service that contains information about all the Users, 
 * all WMS Layers and all Access Levels used by the system.
 * 
 * One can use this class to retrieve Layers by its wmsId's, Users by its usernames,
 * and AccessLevels by its names.
 * 
 * There are no Layers with the same wmsId's, no Users with the same username and
 * no AccessLevels with the same names.
 * 
 * This class cannot be directly instantiated. To create a Service see ServiceBuilder 
 * documentation for details.
 * 
 */
public class Service implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private final Map<String,User> users;
    private final Map<String,Layer> layers;
    private final Map<String,AccessLevel> accessLevels;
    
    /*
    * Abstraction Function:
    * users: maps usernames to User objects.
    * layers: maps wmsId to Layer objects.
    * accessLevel: maps names to AccessLevel objects.
    *
    * Representation Invariant:
    * 
    */
    
    /**
     * 
     * @param users
     * @param layers
     * @param accessLevels 
     */
    private Service(Map<String,User> users,Map<String,Layer> layers,Map<String,AccessLevel> accessLevels){
        this.users = users;
        this.layers = layers;
        this.accessLevels = accessLevels;
        checkRep();
    }
    
    /**
     * Checks the representation invariant.
     * @return true if the representation invariant is maintained
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
     * @throws br.eb.ime.pfc.domain.ServiceElementNotFoundException if no Layers of this service have the specified
     * wmsId.
     */
    public Layer getLayerById(String layerWmsId) throws ServiceElementNotFoundException{
        final Layer layer = layers.get(layerWmsId);
        if(layer == null){
            throw new ServiceElementNotFoundException("The Layer with specified wmsId was not found in the Service");
        }
        else{
            return layer;
        }
    }
    
    /**
     * Returns the AccessLevel specified by its name.
     * @param accessLevelName
     * The name of the access level
     * @return the AccessLevel specified in this service that has the specified
     * name.
     * @throws br.eb.ime.pfc.domain.ServiceElementNotFoundException if no AccessLevel of this service has
     * the specified name.
     */
    public AccessLevel getAccessLevelByName(String accessLevelName) throws ServiceElementNotFoundException{
        final AccessLevel accessLevel = accessLevels.get(accessLevelName);
        if(accessLevel == null){
            throw new ServiceElementNotFoundException("The AccessLevel with the specified name was not found in the Service.");
        }
        else{
            return accessLevel;
        }
    }
    
    /**
     * Returns the User specified by its username.
     * @param username
     * The username of the User
     * @return The User of this service that has the same username specified
     * @throws br.eb.ime.pfc.domain.ServiceElementNotFoundException if no User of this service has the specified
     * username.
     */
    public User getUserByUsername(String username) throws ServiceElementNotFoundException{
        final User user = users.get(username);
        if(user == null){
            throw new ServiceElementNotFoundException("The User with the specified username was not found in the Service.");
        }
        else{
            return user;
        }
    }
    
    /**
     * 
     */
    public static class ServiceBuilder{
        
        private final Map<String,User> users;
        private final Map<String,Layer> layers;
        private final Map<String,AccessLevel> accessLevels;
        
        /**
         * 
         */
        public ServiceBuilder(){
            users = new HashMap<>();
            layers = new HashMap<>();
            accessLevels = new HashMap<>();
        }
        
         /**
         * Add a Layer to the Service.
         * @param layer
         * The Layer that will be added to the Service.
         * Requires: The layer must not contain the same wmsId of a Layer already
         * present in the service.
         * @throws br.eb.ime.pfc.domain.ServiceElementRepetitionException if
         * the wmsId of layer is used by a Layer already  present in the Service.
         */
        public void addLayer(Layer layer) throws ServiceElementRepetitionException{
            if(layers.containsKey(layer.getWmsId())){
                throw new ServiceElementRepetitionException("There is a layer in the Service with the same wmsId");
            }
            else{
                layers.put(layer.getWmsId(), layer);
            }
        }

        /**
         * Add an AccessLevel to the Service.
         * @param accessLevel
         * The AccessLevel that will be added to the Service.
         * Requires: The accessLevel must not contain the same name of an AccessLevel
         * already present in this service.
         * @throws br.eb.ime.pfc.domain.ServiceElementRepetitionException if 
         * the name of accessLevel is used by an AccessLevel already present in the Service.
         */
        public void addAccessLevel(AccessLevel accessLevel) throws ServiceElementRepetitionException{
            if(accessLevels.containsKey(accessLevel.getName())){
                throw new ServiceElementRepetitionException("There is an AccessLevel in the Service with the same name.");
            }
            else{
                accessLevels.put(accessLevel.getName(), accessLevel);
            }
        }

        /**
         * Add an User to the Service.
         * @param user
         * The User that will be added to the service.
         * Requires: The user must not contain the same username of an User already present 
         * in this service.
         * @throws br.eb.ime.pfc.domain.ServiceElementRepetitionException   if
         * the username of user is already used by another User present in the Service.
         */
        public void addUser(User user) throws ServiceElementRepetitionException{
            if(users.containsKey(user.getUsername())){
                throw new ServiceElementRepetitionException("There is an User in the Service with the same username.");
            }
            else{
                users.put(user.getUsername(), user);
            }
        }
        
        /**
         * 
         * @return 
         */
        public Service buildService(){
            throw new RuntimeException();
        }
    }
}