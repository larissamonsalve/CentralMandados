/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.centralmandados.view;

import com.centralmandados.connection.ConnectionFactory;
import com.centralmandados.model.Mandado;
import com.centralmandados.model.MandadoTableModel;
import com.centralmandados.model.Oficial;
import com.centralmandados.model.Processo;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Laris
 */
public class TelaListarMandados extends javax.swing.JInternalFrame {
   
    public TelaListarMandados() throws SQLException {
        initComponents();
        tabelaMandados.setModel(new MandadoTableModel(new ArrayList<>()));
        carregarCombos();
        try {
            carregarMandadosFiltrados(null, null, null, null, null);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar mandados iniciais: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void carregarCombos() {
        cmbStatus.removeAllItems();
        cmbStatus.addItem("Selecione");
        cmbStatus.addItem("Pendente");
        cmbStatus.addItem("Cumprido");
        cmbStatus.addItem("Devolvido");

        cmbOficial.removeAllItems();
        cmbOficial.addItem("Selecione");
        try (Connection conn = ConnectionFactory.getConnection()) {
            String sql = "SELECT id, nome FROM oficial";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cmbOficial.addItem(rs.getInt("id") + " - " + rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        cmbProcesso.removeAllItems();
        cmbProcesso.addItem("Selecione");
        try (Connection conn = ConnectionFactory.getConnection()) {
            String sql = "SELECT id, numero_processo FROM processo";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cmbProcesso.addItem(rs.getInt("id") + " - " + rs.getString("numero_processo"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void carregarMandadosFiltrados(String status, Integer idOficial, Integer idProcesso, String dataInicial, String dataFinal) throws SQLException {
        MandadoTableModel model = new MandadoTableModel(new ArrayList<>());
        tabelaMandados.setModel(model);

        StringBuilder sb = new StringBuilder(
            "SELECT m.numero_mandado, p.numero_processo, m.data_emissao, m.status, o.nome AS nome_oficial, m.anotacoes "
          + "FROM mandado m "
          + "LEFT JOIN processo p ON m.id_processo = p.id "
          + "LEFT JOIN oficial o ON m.id_oficial = o.id "
          + "WHERE 1=1 "
        );

        if (status != null) sb.append("AND m.status = ? ");
        if (idOficial != null) sb.append("AND m.id_oficial = ? ");
        if (idProcesso != null) sb.append("AND m.id_processo = ? ");
        if (dataInicial != null) sb.append("AND m.data_emissao >= ? ");
        if (dataFinal != null) sb.append("AND m.data_emissao <= ? ");

        System.out.println("SQL: " + sb);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sb.toString())) {

            int idx = 1;
            if (status != null)   stmt.setString(idx++, status);
            if (idOficial != null) stmt.setInt(idx++, idOficial);
            if (idProcesso != null) stmt.setInt(idx++, idProcesso);
            if (dataInicial != null) stmt.setDate(idx++, parseData(dataInicial));
            if (dataFinal != null) stmt.setDate(idx++, parseData(dataFinal));

            System.out.println("Params: status=" + status + ", idOficial=" + idOficial +
                    ", idProcesso=" + idProcesso + ", dataInicial=" + dataInicial +
                    ", dataFinal=" + dataFinal);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Mandado m = new Mandado();
                m.setNumeroMandado(rs.getString("numero_mandado"));

                Processo p = new Processo();
                p.setNumeroProcesso(rs.getString("numero_processo"));
                m.setProcesso(p);

                m.setDataEmissao(rs.getDate("data_emissao"));
                m.setStatus(rs.getString("status"));

                String nomeOficial = rs.getString("nome_oficial");
                if (nomeOficial != null) {
                    Oficial o = new Oficial();
                    o.setNome(nomeOficial);
                    m.setOficial(o);
                }

                m.setAnotacoes(rs.getString("anotacoes"));

                model.addRow(m);
            }
            System.out.println("Resultado obtido: " + model.getRowCount());
        }
    }
    
    private int getIdSelecionado(JComboBox<String> combo) {
        String item = (String) combo.getSelectedItem();
        if (item != null && item.contains(" - ")) {
            try {
                return Integer.parseInt(item.split(" - ")[0]);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }   
    
     private java.sql.Date parseData(String data) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date utilDate = sdf.parse(data);
            return new java.sql.Date(utilDate.getTime());
        } catch (Exception e) {
            return null;
        }
    }
     
    private void aplicarFiltros() throws SQLException {
        String status = cmbStatus.getSelectedIndex() > 0 ? (String) cmbStatus.getSelectedItem() : null;
        Integer idOficial = cmbOficial.getSelectedIndex() > 0 ? getIdSelecionado(cmbOficial) : null;
        Integer idProcesso = cmbProcesso.getSelectedIndex() > 0 ? getIdSelecionado(cmbProcesso) : null;
        String dataInicial = txtDataInicial.getText().trim();
        if (!dataInicial.matches("\\d{2}/\\d{2}/\\d{4}")) dataInicial = null;
        String dataFinal = txtDataFinal.getText().trim();
        if (!dataFinal.matches("\\d{2}/\\d{2}/\\d{4}")) dataFinal = null;
       
        try {
            carregarMandadosFiltrados(status, idOficial, idProcesso, dataInicial, dataFinal);
        } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao aplicar filtros: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
        }

    }

    private void limparFiltros() throws SQLException {
        cmbStatus.setSelectedIndex(0);     
        cmbOficial.setSelectedIndex(0);   
        cmbProcesso.setSelectedIndex(0);
        txtDataInicial.setText("");
        txtDataFinal.setText("");
        try {
            carregarMandadosFiltrados(null, null, null, null, null);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao limpar filtros: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void exportarParaExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar como");
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivo Excel (.xlsx)", "xlsx"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".xlsx")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }

        try (Workbook wb = new XSSFWorkbook();
            FileOutputStream out = new FileOutputStream(file)) {

            Sheet sheet = wb.createSheet("Mandados");
            TableModel model = tabelaMandados.getModel();

            // Cabeçalhos
            Row header = sheet.createRow(0);
            for (int j = 0; j < model.getColumnCount(); j++) {
                Cell cell = header.createCell(j);
                cell.setCellValue(model.getColumnName(j));
            }

            // Linhas de dados
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    Cell cell = row.createCell(j);
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }

            // Auto-ajustar colunas
            for (int j = 0; j < model.getColumnCount(); j++) {
                sheet.autoSizeColumn(j);
            }

            wb.write(out);
            JOptionPane.showMessageDialog(this, "Exportado com sucesso para:\n" + file.getAbsolutePath());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao exportar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        cmbOficial = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        cmbProcesso = new javax.swing.JComboBox<>();
        btnFiltrar = new javax.swing.JButton();
        btnCancelarMandado = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDataFinal = new javax.swing.JFormattedTextField();
        btnLimparFiltros = new javax.swing.JButton();
        painelTabelaMandados = new javax.swing.JScrollPane();
        tabelaMandados = new javax.swing.JTable();
        btnExportar = new javax.swing.JButton();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setTitle("Listar Mandados");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Status:");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecione" }));
        cmbStatus.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 51)));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Oficial:");

        cmbOficial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecione" }));
        cmbOficial.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 51)));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Processo:");

        cmbProcesso.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecione" }));
        cmbProcesso.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 51)));

        btnFiltrar.setBackground(new java.awt.Color(0, 102, 51));
        btnFiltrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnFiltrar.setForeground(new java.awt.Color(255, 255, 255));
        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnCancelarMandado.setBackground(new java.awt.Color(0, 102, 51));
        btnCancelarMandado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancelarMandado.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelarMandado.setText("Cancelar");
        btnCancelarMandado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarMandadoActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Data Inicial:");

        txtDataInicial.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 51)));
        try {
            txtDataInicial.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Data Final:");

        txtDataFinal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 51)));
        try {
            txtDataFinal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                    .addComponent(txtDataInicial))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5))
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        btnLimparFiltros.setBackground(new java.awt.Color(0, 102, 51));
        btnLimparFiltros.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLimparFiltros.setForeground(new java.awt.Color(255, 255, 255));
        btnLimparFiltros.setText("Limpar Filtros");
        btnLimparFiltros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparFiltrosActionPerformed(evt);
            }
        });

        painelTabelaMandados.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        tabelaMandados.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tabelaMandados.setForeground(new java.awt.Color(0, 102, 51));
        tabelaMandados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Número Mandado", "Número do Processo", "Data de Emissão", "Status", "Oficial", "Anotações"
            }
        ));
        tabelaMandados.setName("tabelaMandados"); // NOI18N
        painelTabelaMandados.setViewportView(tabelaMandados);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 96, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbProcesso, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbOficial, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancelarMandado, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(116, 116, 116)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnLimparFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(131, 131, 131)
                        .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(228, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(painelTabelaMandados, javax.swing.GroupLayout.DEFAULT_SIZE, 1081, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(cmbOficial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(cmbProcesso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLimparFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCancelarMandado, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(painelTabelaMandados, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnExportar.setBackground(new java.awt.Color(0, 102, 51));
        btnExportar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnExportar.setForeground(new java.awt.Color(255, 255, 255));
        btnExportar.setText("Exportar Dados");
        btnExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnExportar, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnExportar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarMandadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarMandadoActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelarMandadoActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        try {
            aplicarFiltros();
        } catch (SQLException ex) {
            Logger.getLogger(TelaListarMandados.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnLimparFiltrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparFiltrosActionPerformed
        try {
            limparFiltros();
        } catch (SQLException ex) {
            Logger.getLogger(TelaListarMandados.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLimparFiltrosActionPerformed

    private void btnExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarActionPerformed
        exportarParaExcel();
    }//GEN-LAST:event_btnExportarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelarMandado;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnLimparFiltros;
    private javax.swing.JComboBox<String> cmbOficial;
    private javax.swing.JComboBox<String> cmbProcesso;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane painelTabelaMandados;
    private javax.swing.JTable tabelaMandados;
    private javax.swing.JFormattedTextField txtDataFinal;
    private javax.swing.JFormattedTextField txtDataInicial;
    // End of variables declaration//GEN-END:variables
}
