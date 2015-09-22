package br.eb.ime.pfc.domain;

import java.util.Comparator;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;

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

/**
 *
 * @author arthurfernandes
 */
public class LayerManager {
    private final Session session;
    
    public LayerManager(Session session){
        this.session = session;
    }
    
    public void create(Layer layer){
        if(this.session.get(Layer.class,layer.getWmsId())!=null){
            throw new ObjectDuplicateException("There's a layer with the specified wmsID");
        }
        this.session.merge(layer);
    }
    
    public Layer getById(String wmsId) throws ObjectNotFoundException{
        final Layer layer =  (Layer) this.session.get(Layer.class, wmsId);
        if(layer == null){
            throw new ObjectNotFoundException("No such layer with the specified wmsId");
        }
        return layer;
    }
    
    public List<Layer> readAll(){
        final Query query = session.createQuery("from Layer");
        final List<Layer> allLayers = query.list();
        
        allLayers.sort(new Comparator<Layer>(){
            @Override
            public int compare(Layer o1, Layer o2) {
                return o1.getWmsId().compareTo(o2.getWmsId());
            }
        });
        
        for(Layer layer : allLayers){
            Hibernate.initialize(layer);
        }
        return allLayers;
    }
    
    public void update(Layer layer) throws ObjectNotFoundException{
        this.getById(layer.getWmsId());
        this.session.merge(layer);
    }
    
    public void delete(String wmsId) throws ObjectNotFoundException{
        final Layer layer = (Layer) this.session.get(Layer.class, wmsId);
        if(layer == null){
            throw new ObjectNotFoundException("No such layer with the specified wmsId");
        }
        this.session.delete(layer);
    }
}
