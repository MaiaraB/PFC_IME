package br.eb.ime.pfc.domain;

/**
 * 
 */
public class Service {
    
    
    public Service(){
        checkRep();
    }
    
    /**
     * Checks the representation invariant.
     */
    private void checkRep(){
        
    }
    
    public void addLayer(Layer layer){
        throw new RuntimeException();
    }
    
    public void addAccessLevel(AccessLevel accessLevel){
        throw new RuntimeException();
    }
    
    public void addUser(User user){
        throw new RuntimeException();
    }
    
    public Layer getLayerById(String layerWmsId){
        throw new RuntimeException();
    }
    
    public AccessLevel getAccessLevelByName(String accessLevelName){
        throw new RuntimeException();
    }
    
    public User getUserByUsername(String username){
        throw new RuntimeException();
    }
    
}