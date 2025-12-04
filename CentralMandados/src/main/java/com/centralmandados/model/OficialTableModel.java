/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.centralmandados.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class OficialTableModel extends AbstractTableModel {
    private final List<Oficial> oficiais;
    private final String[] colunas = { "ID", "Nome" };

    public OficialTableModel(List<Oficial> lista) {
        this.oficiais = new ArrayList<>(lista);
    }

    @Override
    public int getRowCount() {
        return oficiais.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public String getColumnName(int col) {
        return colunas[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Oficial o = oficiais.get(row);
        switch (col) {
            case 0: return o.getId();
            case 1: return o.getNome();
            default: return null;
        }
    }

    public void addRow(Oficial o) {
        oficiais.add(o);
        int pos = oficiais.size() - 1;
        fireTableRowsInserted(pos, pos);
    }

    public void clear() {
        oficiais.clear();
        fireTableDataChanged();
    }
}
