/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.eb.ime.pfc.controllers;

import br.eb.ime.pfc.domain.AccessLevel;
import br.eb.ime.pfc.domain.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * This class is a Controller tha works as a proxy to WMS requests.
 * 
 * The user must be signed in to perform WMS requests to this controller.
 * This controller is responsible to verify the layers that are accessed by the 
 * WMS requests and to check if the user has access to the corresponding layers.
 * 
 * If the user has access denied, a Http 401 code is sent.
 */
@WebServlet(name = "WMSProxyServlet", urlPatterns = {"/wms"})
public class WMSProxyServlet extends HttpServlet {
    private static final String GEOSERVER_URL = "http://ec2-54-94-206-253.sa-east-1.compute.amazonaws.com/geoserver/rio2016/wms?";
    private static final int REDIRECT_BUFFER_SIZE = 1024;
    //private static final String GEOSERVER_UFL = http://localhost/geoserver/rio2016/wms?
    
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
        
        if(authenticateLayers(request,response)){
            redirectStream(request,response);
        }
        else{
            response.sendError(401);
        }
    }

    /**
     * Specify if the user in this session has access to the layers of this WMS request.
     * @param request servlet request
     * @param response servlet response
     * @return true if the user has access to the layers in its request parameters or false 
     * otherwise.
     */
    protected boolean authenticateLayers(HttpServletRequest request, HttpServletResponse response){
        final User user = (User) request.getSession().getAttribute("user");
        
        if(user == null){ //user isn't logged in
            return false;
        }
        else{ //user is logged in
            
            final List<String> layerWmsIds = getLayerIdsFromRequest(request);
            
            //User isn't trying to access any layer deny it
            if(layerWmsIds.isEmpty()){
                return false;
            }
            
            final AccessLevel accessLevel = user.getAccessLevel();
            for(String layerWmsId : layerWmsIds){
                if(!accessLevel.hasAccessToLayer(layerWmsId)){
                    return false;
                }
            }
            
            return true;
        }
    }
    
    /**
     * This method is responsible for finding a param named layers in the request object,
     * where the name of this parameter is not case sensitive, and return a list containing 
     * the WMS layer id's specified by this parameter and separated by comma.
     * @param request the servlet request
     * @return list of WMS Layer id's
     */
    protected List<String> getLayerIdsFromRequest(HttpServletRequest request){
        //Check if this request has layers as param, whether it's lowercase or uppercase
        String layerParam = null;
        final Enumeration<String> requestParams = request.getParameterNames();
        for(;requestParams.hasMoreElements();){
            String param = requestParams.nextElement();
            if(param.equalsIgnoreCase("LAYERS")){
                layerParam = request.getParameter(param);
            }
        }

        final List<String> layerIds = new ArrayList<>();

        //Returns an empty list if there is no layer param
        if(layerParam == null){
            return layerIds;
        }        
        //Remove Spaces
        layerParam = layerParam.replaceAll(" ", "");
        //Each layer is separated by a comma
        final String layerURISeparator = ",";
        String layerURIArray[] = layerParam.split(layerURISeparator);
        for(String layerURI : layerURIArray){
            if(!layerURI.equals("")){
                layerIds.add(layerURI);
            }
        }        

        return layerIds;
    }
    
    /**
     * This method is responsible for redirecting the request to the WMS Server 
     * and returning the result to the user.
     * @param request servlet request
     * @param response servlet response
     */
    protected void redirectStream(HttpServletRequest request, HttpServletResponse response){
        final String urlName = GEOSERVER_URL + request.getQueryString();
        URL url = null;
        try{
            url = new URL(urlName);
        }
        catch(MalformedURLException e){
            //Internal error, the user will receive no data.
            return;
        }
        
        HttpURLConnection conn = null;
        try{
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            response.setContentType(request.getContentType());
            conn.connect();
        }
        catch(IOException e){
            return;
        }
        
        try(InputStream is = conn.getInputStream();
                OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[REDIRECT_BUFFER_SIZE];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        }
        catch(IOException e){
            return;
        }
        finally{ //Close connection to save resources
            conn.disconnect();
        }
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
