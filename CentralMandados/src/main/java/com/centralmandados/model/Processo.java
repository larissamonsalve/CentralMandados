/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.centralmandados.model;

/**
 *
 * @author Laris
 */
public class Processo {
    private int id;
    private String numeroProcesso;

    public Processo() {
    }
    
    public Processo(int id) {
        this.id = id;
    }


    public Processo(int id, String numeroProcesso) {
        this.id = id;
        this.numeroProcesso = numeroProcesso;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    @Override
    public String toString() {
        return numeroProcesso;
    }
}
