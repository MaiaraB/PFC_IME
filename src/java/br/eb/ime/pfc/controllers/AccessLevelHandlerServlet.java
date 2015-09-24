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
import org.hibernate.HibernateException;

/**
 *
 * @author arthurfernandes
 */
@WebServlet(name = "AccessLevelHandlerServlet", urlPatterns = {"/access-level-handler"})
public class AccessLevelHandlerServlet extends HttpServlet {
    private static final String OK_MESSAGE = "OK";
    private static final String ERROR_MESSAGE = "ERROR";
    private static final String DUPLICATE_MESSAGE = "DUPLICATE";
    private static final String NOTFOUND_MESSAGE = "NOTFOUND";
    public enum ACCESSLEVEL_ACTION{
        DELETE,
        UPDATE,
        CREATE,
        READALL
    }
    final static Map<String,ACCESSLEVEL_ACTION> ACCESSLEVEL_ACTION_MAPPING = new HashMap<>();
    static{
        ACCESSLEVEL_ACTION_MAPPING.put("delete", ACCESSLEVEL_ACTION.DELETE);
        ACCESSLEVEL_ACTION_MAPPING.put("readAll", ACCESSLEVEL_ACTION.READALL);
        ACCESSLEVEL_ACTION_MAPPING.put("update", ACCESSLEVEL_ACTION.UPDATE);
        ACCESSLEVEL_ACTION_MAPPING.put("add", ACCESSLEVEL_ACTION.CREATE);
    }
    private void readAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final AccessLevelManager accessLevelManager = new AccessLevelManager(HibernateUtil.getCurrentSession());
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

    private void update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final AccessLevelManager accessLevelManager = new AccessLevelManager(HibernateUtil.getCurrentSession());
        try{
            AccessLevel accessLevel = this.retrieveObjectFromRequest(request);
            accessLevelManager.update(accessLevel);
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

    private void create(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String name = request.getParameter("name");
        if(name != null){
            final AccessLevelManager accessLevelManager = new AccessLevelManager(HibernateUtil.getCurrentSession());
            try{
                AccessLevel accessLevel = this.retrieveObjectFromRequest(request);
                accessLevelManager.create(accessLevel);
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
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String name = request.getParameter("name");
        if(name != null){
            final AccessLevelManager accessLevelManager = new AccessLevelManager(HibernateUtil.getCurrentSession());
            try{
                accessLevelManager.delete(name);
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
            final ACCESSLEVEL_ACTION action = ACCESSLEVEL_ACTION_MAPPING.get(actionString);
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
            else{
                response.sendError(404);
            }
        }
    }

    public AccessLevel retrieveObjectFromRequest(HttpServletRequest request){
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
                final Layer layer = layerManager.getById(layerWmsId);
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
    
    public AccessLevel loadObject(HttpServletRequest request,AccessLevelManager accessLevelManager){
        String name = request.getParameter("name");
        if(name == null || name.equals("")){
            throw new RuntimeException("No name attribute specified");
        }
        final AccessLevel accessLevel = accessLevelManager.getById(name);
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
