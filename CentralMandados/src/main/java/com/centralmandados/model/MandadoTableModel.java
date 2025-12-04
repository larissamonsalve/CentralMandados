/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.centralmandados.model;

/**
 *
 * @author Laris
 */
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class MandadoTableModel extends AbstractTableModel {
    private final List<Mandado> mandados;
    private final String[] colunas = {
        "Número Mandado", "Número Processo", "Data Emissão", "Status", "Oficial", "Anotações"
    };
    
    public MandadoTableModel(List<Mandado> mandados) {
        this.mandados = mandados;
    }

    @Override
    public int getRowCount() {
        return mandados.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return colunas[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Mandado m = mandados.get(rowIndex);
        switch (columnIndex) {
            case 0: return m.getNumeroMandado();
            case 1: return m.getProcesso().getNumeroProcesso();
            case 2: return m.getDataEmissao();
            case 3: return m.getStatus();
            case 4: return m.getOficial() != null ? m.getOficial().getNome() : "";
            case 5: return m.getAnotacoes();
            default: return null;
        }
    }
    
    public void addRow(Mandado novo) {
        mandados.add(novo);
        int pos = mandados.size() - 1;
        fireTableRowsInserted(pos, pos);
    }
    
    public void clear() {
        mandados.clear();
        fireTableDataChanged();
    }
}
