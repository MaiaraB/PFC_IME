package br.eb.ime.pfc.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * A representation of a WMS Feature of a WMS Layer.
 * 
 * A feature contains a name that is visible to the user, and a wmsId that 
 * identifies this feature in the WMS service, where the information retrieved
 * by GetFeatureInfo requests is stored.
 * 
 */
@Embeddable
//@Table(name = "features")
public class Feature implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Column(name="FEATURE_NAME") private final String name;
    @Column(name="FEATURE_ID") private final String wmsId;
    
    //@Id
    //@ManyToOne(optional = false)
    //@JoinColumn(name="LAYER_ID",referencedColumnName="LAYER_ID")
    //private final Layer layer;
    /*
     * Representation invariant:
     * wmsId must not be empty String
     * name must not be null.
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
    
    protected Feature(){
        this.name = null;
        this.wmsId = null;
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
    
    @Override
    public Feature clone(){
        return new Feature(this.name,this.wmsId);
    }
}