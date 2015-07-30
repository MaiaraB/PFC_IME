package br.com.pfc_ime.controllers;


import java.io.Serializable;
import java.util.Optional;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author arthurfernandes
 */
public class Camada implements Serializable {
    private final String nome;
    private final String wmsId;
    private final String style;
    
    public Camada(String nome,String wmsId,String style){
        this.nome = nome;
        this.wmsId = wmsId;
        this.style = style;
    }
    
    public String getNome(){
        return this.nome;
    }
    
    public String getWmsId(){
        return this.wmsId;
    }
    
    public String getStyle(){
        return this.style;
    }
}
