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

import br.eb.ime.pfc.domain.AccessLevel;
import br.eb.ime.pfc.domain.AccessLevel.LayerRepetitionException;
import br.eb.ime.pfc.domain.AccessLevelManager;
import br.eb.ime.pfc.domain.Layer;
import br.eb.ime.pfc.domain.LayerManager;
import br.eb.ime.pfc.hibernate.HibernateUtil;
import flexjson.JSONSerializer;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.HibernateException;

/**
 *
 * @author arthurfernandes
 */
@WebServlet(name = "AccessLevelHandlerServlet", urlPatterns = {"/access-level-handler"})
public class AccessLevelHandlerServlet extends HttpServlet {

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
        final String action = request.getParameter("action");
        if(action!=null){
            final AccessLevelManager accessLevelManager = new AccessLevelManager(HibernateUtil.getCurrentSession());
            
            if(action.equalsIgnoreCase("update")){
                try{
                    final AccessLevel accessLevel = this.retrieveObject(request);
                    accessLevelManager.update(accessLevel);
                    response.getWriter().print("OK");
                }
                catch(RuntimeException e){
                    response.getWriter().print("ERROR");
                }
                finally{
                    response.getWriter().flush();
                }
            }
            else if(action.equalsIgnoreCase("add")){
                try{
                    final AccessLevel accessLevel = this.retrieveObject(request);
                    if(accessLevelManager.getAccessLevel(accessLevel.getName())!=null){
                        response.getWriter().print("DUPLICATE");
                    }
                    else{
                        accessLevelManager.create(accessLevel);
                        response.getWriter().print("OK");
                    }
                }
                catch(RuntimeException e){
                    response.getWriter().print("ERROR");
                }
                finally{
                    response.getWriter().flush();
                }
            }
            else if(action.equalsIgnoreCase("delete")){
                final String name = request.getParameter("name");
                try{
                    final AccessLevel accessLevel = accessLevelManager.getAccessLevel(name);
                    accessLevelManager.delete(accessLevel);
                    response.getWriter().print("OK");
                }
                catch(RuntimeException e){
                    response.getWriter().print("ERROR");
                }
                finally{
                    response.getWriter().flush();
                }
            }
            else if(action.equalsIgnoreCase("readAll")){
                final List<AccessLevel> accessLevels = accessLevelManager.readAll();
                JSONSerializer serializer = new JSONSerializer();
                final StringBuilder jsonLayersBuilder = new StringBuilder();
                
                serializer.rootName("objects").
                    include("layers").
                    exclude("*.class").serialize(accessLevels,jsonLayersBuilder);
                
                response.setContentType("application/json");
                response.getWriter().println(jsonLayersBuilder.toString());
                response.getWriter().flush();
            }
            else{
                response.sendError(404);
            }
        }
    }

    public AccessLevel retrieveObject(HttpServletRequest request){
        String name = request.getParameter("name");
        if(name == null || name.equals("")){
            throw new RuntimeException("No name attribute specified");
        }
        final AccessLevel accessLevel = new AccessLevel(name);
        String layerWmsId;
        int layerIndex = 0;
        final LayerManager layerManager = new LayerManager(HibernateUtil.getCurrentSession());
        while((layerWmsId = request.getParameter("layers["+layerIndex+"][wmsId]")) != null){
            if(layerWmsId.equals("")){
                throw new RuntimeException("");
            }
            try{
                final Layer layer = layerManager.getLayerById(layerWmsId);
                accessLevel.addLayer(new Layer(layerWmsId,layerWmsId));
            }
            catch(HibernateException e){
                throw new RuntimeException("No layer with the specified id");
            }
            catch(LayerRepetitionException e){
                throw new RuntimeException("Repeated layer to add");
            }
            layerIndex += 1;
        }
        
        return accessLevel;
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
