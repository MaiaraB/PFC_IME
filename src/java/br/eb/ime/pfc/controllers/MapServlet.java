/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.eb.ime.pfc.controllers;

import br.eb.ime.pfc.domain.Layer;
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
@WebServlet(name = "MapServlet", urlPatterns = {"/map"})
public class MapServlet extends HttpServlet {

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
        ArrayList<Layer> camadas = new ArrayList<>();
        String defaultStyle = "pinpoint";
        
        camadas.add(new Layer("Bairros","bairro_part"));
        camadas.add(new Layer("Locais de Interesse","locais_de_interesse"));
        camadas.add(new Layer("Atrações","atracoes"));
        camadas.add(new Layer("Atrações do Comitê","atracoes_comite"));
        camadas.add(new Layer("Competições","competicoes"));
        camadas.add(new Layer("Hotéis","hoteis"));
        camadas.add(new Layer("Lanches e Refeições","lanches_refeicoes"));
        camadas.add(new Layer("Corpo de Bombeiros","corpo_de_bombeiros"));
        camadas.add(new Layer("Delegacias Policiais","delegacias_policiais"));
        camadas.add(new Layer("Paradas de Metro","paradas_metro"));
        camadas.add(new Layer("Paradas de Ônibus","paradas_onibus"));
        camadas.add(new Layer("Paradas de Trem","paradas_trens"));
        request.setAttribute("camadas", camadas);
        request.setAttribute("nome_bairro","nm_bairro");
        request.getRequestDispatcher("/WEB-INF/jsp/mapa.jsp").forward(request, response);
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
