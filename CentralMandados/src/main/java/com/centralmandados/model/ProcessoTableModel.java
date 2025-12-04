/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.centralmandados.model;

import javax.swing.table.AbstractTableModel;
import java.util.List;
/**
 *
 * @author Laris
 */
public class ProcessoTableModel extends AbstractTableModel {
    private final List<Processo> lista;
    private final String[] colunas = { "Número do Processo" };

    public ProcessoTableModel(List<Processo> lista) {
        this.lista = lista;
    }

    @Override
    public int getRowCount() {
        return lista.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return lista.get(row).getNumeroProcesso();
    }

    @Override
    public String getColumnName(int col) {
        return colunas[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return Long.class;
    }
}
