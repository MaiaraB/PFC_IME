/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pfc_ime.controllers;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author arthurfernandes
 */
@WebServlet(name = "MapaServlet", urlPatterns = {"/"})
public class MapaServlet extends HttpServlet {

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
        ArrayList<Camada> camadas = new ArrayList<>();
        String defaultStyle = "pinpoint";
        camadas.add(new Camada("Bairros","bairro_part",""));
        camadas.add(new Camada("Locais de Interesse","locais_de_interesse",defaultStyle));
        camadas.add(new Camada("Atrações","atracoes",defaultStyle));
        camadas.add(new Camada("Atrações do Comitê","atracoes_comite",defaultStyle));
        camadas.add(new Camada("Competições","competicoes",defaultStyle));
        camadas.add(new Camada("Hotéis","hoteis",defaultStyle));
        camadas.add(new Camada("Lanches e Refeições","lanches_refeicoes",defaultStyle));
        camadas.add(new Camada("Corpo de Bombeiros","corpo_de_bombeiros",defaultStyle));
        camadas.add(new Camada("Delegacias Policiais","delegacias_policiais",defaultStyle));
        camadas.add(new Camada("Paradas de Metro","paradas_metro",defaultStyle));
        camadas.add(new Camada("Paradas de Ônibus","paradas_onibus",""));
        camadas.add(new Camada("Paradas de Trem","paradas_trens",defaultStyle));
        request.setAttribute("camadas", camadas);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
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
