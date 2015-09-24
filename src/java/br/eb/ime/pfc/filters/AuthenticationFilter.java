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
package br.eb.ime.pfc.filters;

import br.eb.ime.pfc.domain.Layer;
import br.eb.ime.pfc.domain.ObjectNotFoundException;
import br.eb.ime.pfc.domain.User;
import br.eb.ime.pfc.domain.UserManager;
import br.eb.ime.pfc.hibernate.HibernateUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;

/**
 *
 * This class is a Filter responsible to intercept requests and verify whether 
 * the user is logged in this session or not.
 * 
 * If the user is logged in this Filter does nothing, otherwise it will send the user
 * a 403 Http Error Code.
 */
@WebFilter(filterName = "AuthenticationFilter", servletNames = {"MapServlet","WMSProxyServlet","ListLayersServlet"})
public class AuthenticationFilter implements Filter{

    private FilterConfig filterConfig = null;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final HttpSession session = httpRequest.getSession(true);
        final String username = (String) session.getAttribute("user");
        request.getServletContext().log("USERNAME: "+request.getParameter("username"));
        request.getServletContext().log("PASSWORD: "+request.getParameter("password"));
        if(username == null){
            if(this.basicAuthentication(httpRequest,httpResponse)){
                chain.doFilter(request, response);
            }
            else{
                HttpServletResponse resp = (HttpServletResponse) response;
                resp.sendError(403);
            }
        }
        else{
            chain.doFilter(request, response);
        }
    }
    
    protected boolean authenticateUser(HttpServletRequest request,HttpServletResponse response,String username,String password){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        final Session session = sessionFactory.openSession();
        session.beginTransaction();
        ManagedSessionContext.bind(session);
        final UserManager userManager = new UserManager(session);
        if(username.equals("")){//cannot be empty
            return false;
        }
        String passwordDB = null;
        Set<String> layersIds = new HashSet<>();
        try{
            final User user = userManager.getById(username);
            for(Layer layer : user.getAccessLevel().getLayers()){
                layersIds.add(layer.getWmsId());
            }
            passwordDB = user.getPassword();
            session.getTransaction().commit();
        }
        catch(HibernateException | ObjectNotFoundException e){
            session.getTransaction().rollback();
        }
        finally{
            session.close();
        }
        
        if(passwordDB != null && passwordDB.equals(password)){
            //CACHE LAYERS
            request.getSession().setAttribute("user", username);
            request.getSession().setAttribute("layers", layersIds);
            return true;
        }
        else{
            return false;
        }
    }
    
    protected boolean basicAuthentication(HttpServletRequest request,HttpServletResponse response){
        final Enumeration<String> headers = request.getHeaderNames();
        if(headers != null){
            String authorizationHeader = null;
            while(headers.hasMoreElements()){
                final String header = headers.nextElement();
                if(header.equalsIgnoreCase("authorization")){
                    authorizationHeader = request.getHeader(header);
                    break;
                }
            }
            if(authorizationHeader == null || authorizationHeader.toUpperCase().contains("Basic")){
                return false;
            }
            else{
                request.getServletContext().log("HEADER"+authorizationHeader);
                final String base64EncodedData = authorizationHeader.replace("Basic", "").replace(" ", "");
                byte[] decodedData = DatatypeConverter.parseBase64Binary(base64EncodedData);
                final String decodedString;
                try {
                    decodedString = new String(decodedData,"UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    return false;
                }
                final String[] usernamePasswordArray = decodedString.split(":");
                if(usernamePasswordArray.length != 2){
                    return false;
                }
                else{
                    final String username = usernamePasswordArray[0];
                    final String password = usernamePasswordArray[1];
                    return this.authenticateUser(request,response,username, password);
                }
            }
        }
        else{
            return false;
        }
    }
    
    @Override
    public void destroy() {        
    }
    
}
