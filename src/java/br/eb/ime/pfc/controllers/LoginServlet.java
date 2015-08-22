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

import br.eb.ime.pfc.domain.User;
import br.eb.ime.pfc.domain.UserManager;
import br.eb.ime.pfc.hibernate.HibernateUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 *
 * @author arthurfernandes
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

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
        //Get Parameters from Post Request
        String username = (String) request.getParameter("username");
        String password = (String) request.getParameter("password");
        if(username == null || password == null){
            throw new RuntimeException("Username and Password parameters are not set for request");
        }
        else if(username.equals("") || password.equals("")){
            request.setAttribute("authentication_failure", true);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        else{
            Session session = HibernateUtil.getCurrentSession();
            //Create User Manager to Retrieve user
            final UserManager userManager = new UserManager(session);
            
            //If user isn't found it will be null
            User user;
            try{
                user = userManager.getUserByUsername(username);
            }
            catch(HibernateException e){
                user = null;
            }
            
            //User Validation
            if(user!= null && password.equals(user.getPassword())){
                request.getSession().setAttribute("user", user);
                response.sendRedirect(request.getContextPath()+"/map");
            }
            else{
                //Not valid Username or Password
                request.setAttribute("authentication_failure", true);
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        }
    }
    
    private void forwardToErrorPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        final String errorPagePath = request.getContextPath() +"/error.jsp";
        request.getRequestDispatcher(errorPagePath).forward(request, response);
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
