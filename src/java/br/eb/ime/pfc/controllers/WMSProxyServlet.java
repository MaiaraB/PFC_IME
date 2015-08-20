/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.eb.ime.pfc.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author arthurfernandes
 */
@WebServlet(name = "WMSProxyServlet", urlPatterns = {"/wms"})
public class WMSProxyServlet extends HttpServlet {
    private static final String GEOSERVER_URL = "http://ec2-54-94-206-253.sa-east-1.compute.amazonaws.com/geoserver/rio2016/wms?";
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
            return;
        }
    }

    private boolean authenticateLayers(HttpServletRequest request, HttpServletResponse response){
        return true;
    }
    
    private void redirectStream(HttpServletRequest request, HttpServletResponse response){
        final String urlName = GEOSERVER_URL +request.getQueryString();
        URL url = null;
        try{
            url = new URL(urlName);
        }
        catch(MalformedURLException e){
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
        
        //InputStream is = null;
        try(InputStream is = conn.getInputStream();
                OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        }
        catch(IOException e){
            return;
        }
        finally{
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
