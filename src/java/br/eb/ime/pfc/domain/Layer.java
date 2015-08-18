package br.eb.ime.pfc.domain;


import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * A mutable representation of a WMS layer.
 * 
 * A WMS layer contains the following information:
 * -WMSId: Identifies each layer within a WMS service.
 * -Name: The name of the layer that is visible to users.
 * -Style: The SLD style defined in the WMS server used to render this layer.
 * -Features: Additional information about this layer, used in GetFeatureInfo requests
 * to the WMS server.
 * -Opacity: Defines 
 * 
 */
@Entity
@Table(name = "layers")
public class Layer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final double DEFAULT_OPACITY = 1.0;
    public static final String DEFAULT_STYLE = "";
    
    
    @Column(name="NAME") private final String name;
    @Id @Column(name="LAYER_ID") private final String wmsId;
    @Column(name="STYLE") private String style;
    @Column(name="OPACITY") private double opacity;
    
    @OneToMany(mappedBy = "layer",targetEntity=Feature.class,fetch=FetchType.EAGER,cascade =CascadeType.ALL)
    private final List<Feature> features;
    
    
    /*
    * Representation Invariant:
    * - The wmsId must not be an empty string.
    * - 0.0 <= opacity <= 1.0
    * - features,name and style must not be null.
    */
    
    //CONSTRUCTORS
    
    /**
     * Creates a WMS Layer with the specified name and wmsId.
     * The style to be used is the default. To set the style see setStyle() method.
     * The opacity defined is the default. To set the opacity see setOpacity() method.
     * @param name
     * The name of the Layer, visible to the user.
     * @param wmsId 
     * The identifier of this Layer in the WMS service.
     */
    public Layer(String name,String wmsId){
        this.name = name;
        this.wmsId = wmsId;
        this.style = DEFAULT_STYLE;
        this.opacity = DEFAULT_OPACITY;
        this.features = new LinkedList<>();
    }
    
    protected Layer(){
        this.name = null;
        this.wmsId = null;
        this.style = DEFAULT_STYLE;
        this.opacity = DEFAULT_OPACITY;
        this.features = new LinkedList<>();
    }
    
    /**
     * Checks the representation invariant.
     */
    private void checkRep(){
        assert !wmsId.equals("");
        assert (0.0 <= opacity) && (opacity <= 1.0);
        assert style != null;
        assert features != null;
        
    }
    
    //OBSERVERS
    
    /**
     * Returns the name of this Layer.
     * @return name
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * Returns the wmsId of this Layer. The wmsId is a String that identifies 
     * this Layer on the WMS service, and is not an empty String.
     * @return
     */
    public String getWmsId(){
        return this.wmsId;
    }
    
    /**
     * Returns the style of this Layer.
     * @return style
     */
    public String getStyle(){
        return this.style;
    }
    
    /**
     * Returns the opacity of this Layer.
     * @return opacity
     * The opacity is a value between 0.0 and 1.0.
     */
    public double getOpacity(){
        return this.opacity;
    }
    
    /**
     * Returns an unmodifiable collection of features of this Layer.
     * @return features
     * 
     */
    public List<Feature> getFeatures(){
        return Collections.unmodifiableList(features);
    }
    
    //MUTATORS
    
    /**
     * Add a feature to this Layer.
     * @param feature 
     * The feature to be added
     * @throws br.eb.ime.pfc.domain.Layer.FeatureRepetitionException when the user tries to add a feature
     * with the same wmsId of a feature that already exists in this Layer.
     */
    public void addFeature(Feature feature) throws FeatureRepetitionException{
        for(Feature feat : features){
            if(feat.getWmsId().equals(feature.getWmsId())){
                throw new FeatureRepetitionException("Cannot add feature with repeated wmsId");
            }
        }
        features.add(feature);
    }
    
    /**
     * Set the opacity of this Layer.
     * @param opacity 
     * Requires opacity to be a value between 0.0 and 1.0.
     * @throws br.eb.ime.pfc.domain.Layer.OpacityOutOfRangeException
     * When opacity is less than 0.0 or opacity is greater than 1.0.
     */
    public void setOpacity(double opacity) throws OpacityOutOfRangeException{
        if(opacity < 0.0 || opacity > 1.0){
            throw new OpacityOutOfRangeException("Opacity must be a value between 0.0 and 1.0");
        }
        this.opacity = opacity;
    }
    
    public void setStyle(String style){
        this.style = style;
    }
    
    /**
     * This exception is triggered when one tries to add a feature to a Layer, 
     * and the feature has the same wmsId of a feature already present in the 
     * Layer.
     */
    public class FeatureRepetitionException extends RuntimeException{
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Creates a FeatureRepetitionException with a detail message.
         * @param message 
         * The message that specify the error.
         */
        public FeatureRepetitionException(String message){
            super(message);
        }
    }
    
    /**
     * This exception is triggered when the opacity of a Layer obeys the condition:
     * opacity < 0.0 or opacity > 1.0.
     */
    public class OpacityOutOfRangeException extends RuntimeException{
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Creates an OpacityOutOfRangeException with a detail message.
         * @param message 
         * The message that specify the error.
         */
        public OpacityOutOfRangeException(String message){
            super(message);
        }
    }
}
