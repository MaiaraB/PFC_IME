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
import br.eb.ime.pfc.domain.AccessLevelManager;
import br.eb.ime.pfc.domain.ObjectDuplicateException;
import br.eb.ime.pfc.domain.ObjectNotFoundException;
import br.eb.ime.pfc.domain.User;
import br.eb.ime.pfc.domain.UserManager;
import br.eb.ime.pfc.hibernate.HibernateUtil;
import flexjson.JSONSerializer;
import flexjson.transformer.AbstractTransformer;
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
@WebServlet(name = "UserHandlerServlet", urlPatterns = {"/user-handler"})
public class UserHandlerServlet extends HttpServlet {
    private static final String OK_MESSAGE = "OK";
    private static final String ERROR_MESSAGE = "ERROR";
    private static final String DUPLICATE_MESSAGE = "DUPLICATE";
    private static final String NOTFOUND_MESSAGE = "NOTFOUND";
    public enum USER_ACTION{
        DELETE,
        UPDATE,
        CREATE,
        READALL
    }
    final static Map<String,USER_ACTION> USER_ACTION_MAPPING = new HashMap<>();
    static{
        USER_ACTION_MAPPING.put("delete", USER_ACTION.DELETE);
        USER_ACTION_MAPPING.put("readAll", USER_ACTION.READALL);
        USER_ACTION_MAPPING.put("update", USER_ACTION.UPDATE);
        USER_ACTION_MAPPING.put("add", USER_ACTION.CREATE);
    }
    
    private void readAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final UserManager userManager = new UserManager(HibernateUtil.getCurrentSession());
        final List<User> users = userManager.readAll();
        JSONSerializer serializer = new JSONSerializer();
        final StringBuilder jsonLayersBuilder = new StringBuilder();

        serializer.rootName("objects").transform(new AbstractTransformer(){
            @Override
            public void transform(Object object) {
                if(object instanceof AccessLevel){
                    AccessLevel accessLevel = (AccessLevel) object;
                    this.getContext().writeQuoted(accessLevel.getName());
                }
            }
        }, "accessLevel").
            exclude("*.class").
            serialize(users,jsonLayersBuilder);

        response.setContentType("application/json");
        response.getWriter().println(jsonLayersBuilder.toString());
        response.getWriter().flush();
    }
    
    private void update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final UserManager userManager = new UserManager(HibernateUtil.getCurrentSession());
        try{
            User user = this.retrieveObjectFromRequest(request);
            userManager.update(user);
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
        final String username = request.getParameter("username");
        if(username != null){
            final UserManager userManager = new UserManager(HibernateUtil.getCurrentSession());
            try{
                User user = this.retrieveObjectFromRequest(request);
                userManager.create(user);
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
        final String username = request.getParameter("username");
        if(username != null){
            final UserManager userManager = new UserManager(HibernateUtil.getCurrentSession());
            try{
                userManager.delete(username);
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
            final USER_ACTION action = USER_ACTION_MAPPING.get(actionString);
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
     
    public User retrieveObjectFromRequest(HttpServletRequest request){
        final UserManager userManager = new UserManager(HibernateUtil.getCurrentSession());
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String accessLevelName = request.getParameter("accessLevel");
        if(username == null || password == null || accessLevelName == null ||
                username.equals("") || password.equals("") || accessLevelName.equals("")){
            throw new RuntimeException("Cannot initialize user");
        }
        final AccessLevelManager accessLevelManager = new AccessLevelManager(HibernateUtil.getCurrentSession());
        final AccessLevel accessLevel = accessLevelManager.getById(accessLevelName);
        final User user = new User(username,password,accessLevel);
        //OPTIONAL PARAMETERS
        String email = request.getParameter("email");
        String name = request.getParameter("name");
        String telephone = request.getParameter("telephone");
        if(email!=null){
            user.setEmail(email);
        }
        if(name!=null){
            user.setName(name);
        }
        if(telephone!=null){
            user.setTelephone(telephone);
        }
        return user;
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
