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
package br.eb.ime.pfc.controllers;

import br.eb.ime.pfc.domain.Feature;
import br.eb.ime.pfc.domain.Layer;
import br.eb.ime.pfc.domain.LayerManager;
import br.eb.ime.pfc.domain.ObjectDuplicateException;
import br.eb.ime.pfc.domain.ObjectNotFoundException;
import br.eb.ime.pfc.hibernate.HibernateUtil;
import flexjson.JSONSerializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.RollbackException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author arthurfernandes
 */
@WebServlet(name = "LayerHandlerServlet", urlPatterns = {"/layer-handler"})
public class LayerHandlerServlet extends HttpServlet {
    private static final String OK_MESSAGE = "OK";
    private static final String ERROR_MESSAGE = "ERROR";
    private static final String DUPLICATE_MESSAGE = "DUPLICATE";
    private static final String NOTFOUND_MESSAGE = "NOTFOUND";
    public enum LAYER_ACTION{
        DELETE,
        UPDATE,
        CREATE,
        READALL
    }
    final static Map<String,LAYER_ACTION> LAYER_ACTION_MAPPING = new HashMap<>();
    static{
        LAYER_ACTION_MAPPING.put("delete", LAYER_ACTION.DELETE);
        LAYER_ACTION_MAPPING.put("readAll", LAYER_ACTION.READALL);
        LAYER_ACTION_MAPPING.put("update", LAYER_ACTION.UPDATE);
        LAYER_ACTION_MAPPING.put("add", LAYER_ACTION.CREATE);
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String actionString = request.getParameter("action");
        if(actionString!=null){
            //final LayerManager layerManager = new LayerManager(HibernateUtil.getCurrentSession());
            final LAYER_ACTION action = LAYER_ACTION_MAPPING.get(actionString);
            if(action!=null){
                switch(action){
                    case READALL:
                        request.getServletContext().log("READALL");
                        this.readAll(request, response);
                        break;
                    case UPDATE:
                        request.getServletContext().log("UPDATE");
                        this.update(request,response);
                        break;
                    case CREATE:
                        request.getServletContext().log("CREATE");
                        this.create(request,response);
                        break;
                    case DELETE:
                        request.getServletContext().log("DELETE");
                        this.delete(request,response);
                        break;
                    default:
                        response.sendError(404);
                        break;
                }
            }
        }
        else{
            response.sendError(404);
        }
    }
    
    public void readAll(HttpServletRequest request,HttpServletResponse response) throws IOException{
        final LayerManager layerManager = new LayerManager(HibernateUtil.getCurrentSession());
        final List<Layer> allLayers = layerManager.readAll();
        JSONSerializer serializer = new JSONSerializer();
        final StringBuilder jsonLayersBuilder = new StringBuilder();

        serializer.rootName("objects").
            include("features").
            exclude("*.class").serialize(allLayers,jsonLayersBuilder);
                
        response.setContentType("application/json");
        response.getWriter().println(jsonLayersBuilder.toString());
        response.getWriter().flush();
    }
    
    public void update(HttpServletRequest request,HttpServletResponse response) throws IOException{
        final LayerManager layerManager = new LayerManager(HibernateUtil.getCurrentSession());
        try{
            Layer layer = this.retrieveObjectFromRequest(request);
            layerManager.update(layer);
            response.getWriter().print(OK_MESSAGE);
        }
        catch(ObjectNotFoundException e){
            response.getWriter().print(NOTFOUND_MESSAGE);
        }
        catch(RuntimeException e){
            response.getWriter().print(ERROR_MESSAGE);
        }
        finally{
            response.getWriter().flush();
        }
    }
    
    public void create(HttpServletRequest request,HttpServletResponse response) throws IOException{
        final String wmsId = request.getParameter("wmsId");
        if(wmsId != null){
            final LayerManager layerManager = new LayerManager(HibernateUtil.getCurrentSession());
            try{
                Layer layer = this.retrieveObjectFromRequest(request);
                layerManager.create(layer);
                response.getWriter().print(OK_MESSAGE);
            }
            catch(ObjectDuplicateException e){
                response.getWriter().print(DUPLICATE_MESSAGE);
            }
            catch(RollbackException e){
                response.getWriter().print(ERROR_MESSAGE);
            }
            finally{
                response.getWriter().flush();
            }
        }
        else{
            response.getWriter().print(ERROR_MESSAGE);
        }
    }
    
    public void delete(HttpServletRequest request,HttpServletResponse response) throws IOException{
        final String wmsId = request.getParameter("wmsId");
        if(wmsId != null){
            final LayerManager layerManager = new LayerManager(HibernateUtil.getCurrentSession());
            try{
                layerManager.delete(wmsId);
                response.getWriter().print(OK_MESSAGE);
            }
            catch(ObjectNotFoundException e){
                response.getWriter().print(NOTFOUND_MESSAGE);
            }
            catch(RuntimeException e){
                response.getWriter().print(ERROR_MESSAGE);
            }
            finally{
                response.getWriter().flush();
            }
        }
        else{
            response.getWriter().print(ERROR_MESSAGE);
        }
    }
    
    public Layer retrieveObjectFromRequest(HttpServletRequest request) throws ObjectNotFoundException{
        String wmsId = request.getParameter("wmsId");
        String name = request.getParameter("name");
        if(name == null || wmsId == null || wmsId.equals("")){
            throw new ObjectNotFoundException("No id or name attribute specified");
        }
        final Layer layer = new Layer(name,wmsId);
        
        String style = request.getParameter("style");
        if(style != null){
            layer.setStyle(style);
        }
        String opacityString = request.getParameter("opacity");
        if(opacityString != null)
        try{
            Double opacity = Double.parseDouble(opacityString);
            layer.setOpacity(opacity);
        }
        catch(RuntimeException e){
            throw new RuntimeException("Opacity Out of Range");
        }
        String featureWmsId;
        int featureIndex = 0;
        while((featureWmsId = request.getParameter("features["+featureIndex+"][wmsId]")) != null){
            if(featureWmsId.equals("")){
                throw new RuntimeException("");
            }
            String featureName = request.getParameter("features["+featureIndex+"][name]");
            if(featureName == null){
                featureName = "";
            }
            layer.addFeature(new Feature(featureName,featureWmsId));
            featureIndex += 1;
            request.getServletContext().log("INDEX:"+featureIndex+",ID:"+featureWmsId);
        }
        return layer;
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
