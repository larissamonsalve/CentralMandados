/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.centralmandados.model;

import java.sql.Date;
/**
 *
 * @author Laris
 */
public class Mandado {
    private int id;
    private String numeroMandado;
    private Date dataEmissao;
    private Date prazoCumprimento;
    private String status; 
    private String anotacoes;
    private Processo processo;
    private Oficial oficial;   

    public Mandado() {
    }

    public Mandado(int id, String numeroMandado, Date dataEmissao, Date prazoCumprimento,
                   String status, String anotacoes, Processo processo, Oficial oficial) {
        this.id = id;
        this.numeroMandado = numeroMandado;
        this.dataEmissao = dataEmissao;
        this.prazoCumprimento = prazoCumprimento;
        this.status = status;
        this.anotacoes = anotacoes;
        this.processo = processo;
        this.oficial = oficial;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroMandado() {
        return numeroMandado;
    }

    public void setNumeroMandado(String numeroMandado) {
        this.numeroMandado = numeroMandado;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Date getPrazoCumprimento() {
        return prazoCumprimento;
    }

    public void setPrazoCumprimento(Date prazoCumprimento) {
        this.prazoCumprimento = prazoCumprimento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public Oficial getOficial() {
        return oficial;
    }

    public void setOficial(Oficial oficial) {
        this.oficial = oficial;
    }

    public int getIdProcesso() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public Object getIdOficial() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
