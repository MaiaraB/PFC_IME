package br.eb.ime.pfc.domain;

/**
 * A representation of a WMS feature of a WMS layer.
 * 
 * A feature contains a name that is visible to the user, and a wmsId that 
 * identifies this feature in the WMS service, where the information retrieved
 * by GetFeatureInfo requests is stored.
 * 
 */
public class Feature {
    private final String name;
    private final String wmsId;
    
    /*
     * Representation invariant:
     * wmsId must not be empty String
     * name must not be null.
     *
    */
    
    /**
     * Creates a WMS feature with a name and wmsId.
     * @param name
     * The name that is visible to users of the feature.
     * @param wmsId 
     * A String that identifies this feature in the WMS service.
     */
    public Feature(String name,String wmsId){
        this.name = name;
        this.wmsId = wmsId;
        checkRep();
    }
    
    /**
     * Checks the representation invariant.
     */
    private void checkRep(){
        assert !this.wmsId.equals("");
        assert name != null;
    }
    
    /**
     * Returns the name of this Feature.
     * @return 
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * Returns the wmsId of this Feature.
     * @return 
     */
    public String getWmsId(){
        return this.wmsId;
    }
}