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
package br.eb.ime.pfc.domain;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.decoder.RESTStyleList;
import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author arthurfernandes
 */
public class GeoServerCommunication {
    private final static String GEOSERVER_URL = "http://ec2-54-94-206-253.sa-east-1.compute.amazonaws.com/geoserver";
    //private final static String GEOSERVER_URL = "http://localhost:8080/geoserver";
    private final static String GEOSERVER_RESTUSER = "admin";
    private final static String GEOSERVER_RESTPW = "geoserver";
    
    private final GeoServerRESTReader reader;
    private final GeoServerRESTPublisher publisher;
    
    public static GeoServerCommunication makeGeoserverCommunication() throws GeoserverCommunicationException{
        GeoServerRESTReader reader = null;
        GeoServerRESTPublisher publisher = null;
        try{
            reader = new GeoServerRESTReader(GEOSERVER_URL, GEOSERVER_RESTUSER, GEOSERVER_RESTPW);
            publisher = new GeoServerRESTPublisher(GEOSERVER_URL, GEOSERVER_RESTUSER, GEOSERVER_RESTPW);
        }
        catch(MalformedURLException | IllegalArgumentException e){
            throw new GeoserverCommunicationException("Could not stablish GeoserverCommunication due to malformed URL: "+GEOSERVER_URL);
        }
        if((reader != null) && (publisher != null)){
            return new GeoServerCommunication(reader,publisher);
        }
        else{
            throw new GeoserverCommunicationException("Could not stablish GeoserverCommunication due to unknown Problem.");
        }
    }
    
    private static void sendError(HTTP_STATUS status,HttpServletResponse response){
        try(Writer writer = response.getWriter()){
            response.sendError(status.getCode());
        }
        catch(IOException e){
        }
    }
    
    public static void redirectWMSStreamFromRequest(HttpServletRequest request,HttpServletResponse response){
        final String urlName = GEOSERVER_URL + "/wms?" + request.getRequestURI() + request.getQueryString();
        redirectWMSStream(urlName,request,response);
    }
    
    public static void getLegendGraphic(String layerId,int width,int height,HttpServletRequest request,HttpServletResponse response){
        final String urlName = GEOSERVER_URL + "/wms?" + "REQUEST=GetLegendGraphic&VERSION=1.0.0&FORMAT=image/png&WIDTH="
                + width+"&HEIGHT="+height+"&LAYER="+layerId + "&EXCEPTIONS=application/vnd.ogc.se_blank";
        redirectWMSStream(urlName,request,response);
    }
    
    private static void redirectWMSStream(String urlName,HttpServletRequest request, HttpServletResponse response){
        URL url = null;
        try{
            url = new URL(urlName);
        }
        catch(MalformedURLException e){
            //Internal error, the user will receive no data.
            sendError(HTTP_STATUS.BAD_REQUEST,response);
            return;
        }
        HttpURLConnection conn = null;
        try{
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            response.setContentType(request.getContentType());
            conn.connect();
        }
        catch(IOException e){
            sendError(HTTP_STATUS.INTERNAL_ERROR,response);
            return;
        }
        
        try(InputStream is = conn.getInputStream();OutputStream os = response.getOutputStream()){
            IOUtils.copy(is, os);
        }
        catch(IOException e){
            sendError(HTTP_STATUS.INTERNAL_ERROR,response);
            return;
        }
        finally{ //Close connection to save resources
            conn.disconnect();
        }
    }
    
    private GeoServerCommunication(GeoServerRESTReader reader, GeoServerRESTPublisher publisher){    
        this.reader = reader;
        this.publisher = publisher;
    }
    
    public boolean existsGeoserver(){
        return this.reader.existGeoserver();
    }
    
    public List<String> getLayerNames() throws GeoserverCommunicationException{
        if(this.reader.existGeoserver()){
            final List<String> layerNames = new ArrayList<>();
            final RESTLayerList restLayerList = this.reader.getLayers();
            if(restLayerList == null){
                throw new GeoserverCommunicationException("Communication issue with Geoserver REST API at:"+GEOSERVER_URL);
            }
            for(NameLinkElem elem : restLayerList){
                layerNames.add(elem.getName());
            }
            return layerNames;
        }
        else{
            throw new GeoserverCommunicationException("Could not establish REST Communication with Server at +"+GEOSERVER_URL);
        }
    }
    
    public List<String> getStyleNames(){
        if(this.reader.existGeoserver()){
            final List<String> styleNames = new ArrayList<>();
            final RESTStyleList restStyleList = this.reader.getStyles();
            if(restStyleList == null){
                throw new GeoserverCommunicationException("Communication issue with Geoserver REST API at:"+GEOSERVER_URL);
            }
            for(NameLinkElem elem : restStyleList){
                styleNames.add(elem.getName());
            }
            return styleNames;
        }
        else{
            throw new GeoserverCommunicationException("Could not establish REST Communication with Server at +"+GEOSERVER_URL);
        }
    }
    
    public boolean existsStyle(String styleName){
        return reader.existsStyle(styleName);
    }
    
    public boolean addStyle(String name,String resourceURL,String format,Integer size){
        return publisher.publishStyle(this.getSLDFileBody(resourceURL, format, size), name);
    }

    public boolean removeStyle(String styleName){
        return publisher.removeStyle(styleName);
    }
    
    public boolean updateStyle(String name,String resourceURL,String format,Integer size){
        return publisher.updateStyle(this.getSLDFileBody(resourceURL, format, size), name);
    }
    
    private String getSLDFileBody(String resourceURL,String format,Integer size){
        final String[] strSLDArray = {
            "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n",
            "<StyledLayerDescriptor version=\"1.0.0\"\n",
            "xsi:schemaLocation=\"http://www.opengis.net/sld StyledLayerDescriptor.xsd\"\n",
            "xmlns=\"http://www.opengis.net/sld\"\n",
            "xmlns:ogc=\"http://www.opengis.net/ogc\"\n",
            "xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n",
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n",
                "<NamedLayer>\n",
                    "<Name>Point as graphic</Name>\n",
                    "<UserStyle>\n",
                        "<Title>Point as Graphic</Title>\n",
                        "<FeatureTypeStyle>\n",
                            "<Rule>\n",
                                "<PointSymbolizer>\n",
                                    "<Graphic>\n",
                                        "<ExternalGraphic>\n",
                                        "<OnlineResource xlink:type=\"simple\"\n",
                                        "xlink:href=\""+resourceURL+ "\"/>\n",
                                        "<Format>"+format+"</Format>\n",
                                        "</ExternalGraphic>\n",
                                        "<Size>"+size+"</Size>\n",
                                    "</Graphic>\n",
                                "</PointSymbolizer>\n",
                            "</Rule>\n",
                        "</FeatureTypeStyle>\n",
                    "</UserStyle>\n",
                "</NamedLayer>\n",
            "</StyledLayerDescriptor>\n"
        };
        final StringBuilder builder = new StringBuilder();
        for(String str : strSLDArray){
            builder.append(str);
        }
        return builder.toString();
    }
    
    public static class GeoserverCommunicationException extends RuntimeException{
        private static final long serialVersionUID = 1L;
        
        /**
         * Creates a GeoserverCommunicationException with a detail message.
         * @param message 
         * The message that specify the error.
         */
        public GeoserverCommunicationException(String message){
            super(message);
        }
    }
    
    public static void main(String args[]) throws MalformedURLException{
        String RESTURL  = "http://localhost:8080/geoserver";
        //private static final String GEOSERVER_URL = "http://localhost/geoserver/rio2016/wms?";
        String RESTUSER = "admin";
        String RESTPW   = "geoserver";
        
        GeoServerRESTReader reader = new GeoServerRESTReader(RESTURL, RESTUSER, RESTPW);
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);
        
        GeoServerCommunication com = GeoServerCommunication.makeGeoserverCommunication();
        System.out.println(com.removeStyle("novo_estilo"));
        System.out.println(com.addStyle("novo_estilo","http://com.cartodb.users-assets.production.s3.amazonaws.com/simpleicon/map43.svg" , "image/svg", 32));
        System.out.println(com.updateStyle("novo_estilo","http://com.cartodb.users-assets.production.s3.amazonaws.com/simpleicon/map43.svg" , "image/svg", 32));
        /*
        
        RESTLayerList list = reader.getLayers();
        System.out.println();
        for(NameLinkElem layer : list){
            System.out.println(layer.getName());
        }*/
        //RESTStyleList styleList = reader.getStyles();
        //Iterator styleListIterator = styleList.iterator();
        /*
        while(styleListIterator.hasNext()){
            System.out.println((styleListIterator.next()));
        }*/
        //String sldFile = reader.getSLD("point");
        //System.out.println(publisher.publishStyleInWorkspace(null,sldFile,"novsa_camada"));
    }
}
